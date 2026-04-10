package org.mainapp.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.mainapp.data.*;
import org.mainapp.repository.MatchesRepository;
import org.mainapp.repository.MatchdaysRepository;
import org.mainapp.repository.TeamsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.util.Pair;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.Set;

@Service
public class SimulationService {

    private final MatchesRepository matchesRepository;
    private final MatchdaysRepository matchdaysRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final StreamsBuilderFactoryBean streamsBuilderFactoryBean;
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;
    private final TeamsService teamsService;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
    @Autowired
    private TeamsRepository teamsRepository;


    public SimulationService(MatchesRepository matchesRepository, MatchdaysRepository matchdaysRepository, SimpMessagingTemplate messagingTemplate, StreamsBuilderFactoryBean streamsBuilderFactoryBean, StringRedisTemplate stringRedisTemplate, ObjectMapper objectMapper, TeamsService teamsService) {
        this.matchesRepository = matchesRepository;
        this.matchdaysRepository = matchdaysRepository;
        this.messagingTemplate = messagingTemplate;
        this.streamsBuilderFactoryBean = streamsBuilderFactoryBean;
        this.stringRedisTemplate = stringRedisTemplate;
        this.objectMapper = objectMapper;
        this.teamsService = teamsService;
    }

    @Transactional
    public void startSimulationForMatchday(int matchdayNumber) {
        int liveMatchdays = (int) matchdaysRepository.findAll().stream().filter(p->p.getWeekStatus()==MatchStatus.LIVE).count();
        if(liveMatchdays==0)
        {
            Matchday matchday= matchdaysRepository.findByWeekId(matchdayNumber);
            if(matchday.getWeekStatus()==MatchStatus.SCHEDULED)
            {
                List<Match> matches = matchday.getMatches();
                for (Match match : matches) {
                    String id = match.getId().toString();
                    MatchDTOSim matchDTOSim = new MatchDTOSim(match);
                    kafkaTemplate.send("simulation", id, matchDTOSim);
                }
                matchday.setWeekStatus(MatchStatus.LIVE);
                matchdaysRepository.save(matchday);
                initializeOfficialTable();
            }
        }
    }

    @KafkaListener(topics = "match-events", groupId = "events-group")
    public void consumeEvents(MatchEvent event)
    {
        if(event.getAction()==MatchAction.GOAL)
        {
            messagingTemplate.convertAndSend("/topic/match-logs",event);
        }
        messagingTemplate.convertAndSend("/simulation/match-update", event);

    }

    @KafkaListener(topics = "match-events", groupId = "database-group")
    @Transactional
    public void storeOrUpdateEvents(MatchEvent event)
    {
        MatchDTOSim currentMatch = event.getMatch();
        int minute = event.getMinute();
        currentMatch.setCurrentMinute(minute);
        saveMatchToCache(currentMatch);
        updateTeamsInCache(currentMatch);

        if(minute%10==0)
            refreshMatchesDatabase(currentMatch);

        if(currentMatch.getStatus()==MatchStatus.FINISHED)
        {
            matchdaysRepository.findByWeekId(currentMatch.getMatchday()).setWeekStatus(MatchStatus.FINISHED);
        }
    }

    public void saveMatchToCache(MatchDTOSim currentMatch)
    {
        String weekId = String.valueOf(currentMatch.getMatchday());
        try{
            String json = objectMapper.writeValueAsString(currentMatch);
            stringRedisTemplate.opsForHash().put("week:"+ weekId, currentMatch.getId().toString(),json);
            stringRedisTemplate.expire("week:"+ weekId, Duration.ofMinutes(10));
        }
        catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateTeamsInCache(MatchDTOSim currentMatch)
    {
        String homeTeamName = currentMatch.getHomeTeam();
        String awayTeamName = currentMatch.getAwayTeam();
        Pair<Integer,Integer> pointsToAdd = analyzeResult(currentMatch);
        saveTeamToBonusCache(homeTeamName, pointsToAdd.getFirst());
        saveTeamToBonusCache(awayTeamName, pointsToAdd.getSecond());

        stringRedisTemplate.opsForZSet().unionAndStore("table:official","table:live-bonus", "table:live");
        Set<ZSetOperations.TypedTuple<String>> ranking = stringRedisTemplate.opsForZSet().reverseRangeWithScores("table:live", 0, -1);
        List<DtoTeam> teams = teamsService.deserializationRanking(ranking);
        messagingTemplate.convertAndSend("/simulation/table-update", teams);
    }

    public void saveTeamToBonusCache(String teamName, int pointsToAdd)
    {
        stringRedisTemplate.opsForZSet().add("table:live-bonus", teamName,pointsToAdd);
    }

    public void initializeOfficialTable()
    {
        teamsRepository.findAll().forEach(team ->
        {
            stringRedisTemplate.opsForZSet().add("table:official", team.getShortName(),team.getPoints());
        });
    }

    @Transactional
    public void refreshMatchesDatabase(MatchDTOSim currentMatch) {
        matchesRepository.findById(currentMatch.getId()).ifPresent(match -> {
            if (currentMatch.getStatus() == MatchStatus.FINISHED && match.getStatus() != MatchStatus.FINISHED) {
                Pair<Integer,Integer> pointsToAdd = analyzeResult(currentMatch);
                updatePoints(match.getHomeTeam(),pointsToAdd.getFirst());
                updatePoints(match.getAwayTeam(),pointsToAdd.getSecond());
            }

            match.setStatus(currentMatch.getStatus());
            match.setAwayTeamGoals(currentMatch.getAwayTeamGoals());
            match.setHomeTeamGoals(currentMatch.getHomeTeamGoals());
            match.setCurrentMinute(currentMatch.getCurrentMinute());

            matchesRepository.save(match);
        });
    }

    private Pair<Integer,Integer> analyzeResult(MatchDTOSim currentMatch) {
        int homeGoals = currentMatch.getHomeTeamGoals();
        int awayGoals = currentMatch.getAwayTeamGoals();

        if (homeGoals > awayGoals) {
            return Pair.of(3,0);
        } else if (awayGoals > homeGoals) {
           return Pair.of(0,3);
        } else {
            return Pair.of(1,1);
        }
    }

    private void updatePoints(Team team, int points) {
        team.setPoints(team.getPoints() + points);
        teamsRepository.save(team);
    }

    public void startReplayForMatch(long matchId)
    {

        KafkaStreams kafkaStreams = streamsBuilderFactoryBean.getKafkaStreams();
        ReadOnlyKeyValueStore<Long,List<MatchEvent>> store = kafkaStreams.store(
                StoreQueryParameters.fromNameAndType(
                        "match-history-store", QueryableStoreTypes.keyValueStore()
                )
        );

        List<MatchEvent> matchEventList = store.get(matchId);
        for(MatchEvent event : matchEventList)
        {
            if(event.getAction()!=MatchAction.UPDATE)
                messagingTemplate.convertAndSend("/topic/match-ticks",event);
        }
    }
}
