package org.mainapp.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mainapp.config.footballData.MatchDTO;
import org.mainapp.data.*;
import org.mainapp.config.footballData.MatchesClientService;
import org.mainapp.repository.MatchesRepository;
import org.mainapp.repository.MatchdaysRepository;
import org.mainapp.repository.TeamsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.hibernate.internal.util.collections.ArrayHelper.forEach;

@Service
public class MatchesService {

    private final MatchesClientService matchesClientService;
    private final MatchesRepository matchesRepository;
    private final MatchdaysRepository matchdaysRepository;
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;
    private final TeamsRepository teamsRepository;

    public MatchesService(MatchesClientService matchesClientService, MatchesRepository matchesRepository, MatchdaysRepository matchdaysRepository, StringRedisTemplate stringRedisTemplate, ObjectMapper objectMapper, TeamsRepository teamsRepository) {
        this.matchesClientService = matchesClientService;
        this.matchesRepository = matchesRepository;
        this.matchdaysRepository = matchdaysRepository;
        this.stringRedisTemplate = stringRedisTemplate;
        this.objectMapper = objectMapper;
        this.teamsRepository = teamsRepository;
    }

    public List<Match> getAllMatches() {
        return matchesRepository.findAll();
    }


    public List<MatchDTOSim> getMatchesByMatchday(int matchday)
    {
        return getMatchesListForMatchday(matchday);
    }

    public void initializeData()
    {
        List<Match> matchesList = matchesRepository.findAll();
        if(matchesList.isEmpty())
        {
            matchesList = matchesClientService.findAll().getMatches().stream().map(this::mapToEntity).toList();
            matchesRepository.saveAll(matchesList);

        }
    }

    public Match mapToEntity(MatchDTO matchDTO) {
        Matchday matchday = matchdaysRepository.findByWeekId(matchDTO.getMatchday());
        Team homeTeam = teamsRepository.findByShortName(matchDTO.getHomeTeamName().getShortName());
        Team awayTeam = teamsRepository.findByShortName(matchDTO.getAwayTeamName().getShortName());

        Match match = new Match();
        match.setId(matchDTO.getId());
        match.setHomeTeam(homeTeam);
        match.setAwayTeam(awayTeam);
        match.setMatchday(matchday);
        match.setStatus(MatchStatus.SCHEDULED);
        match.setCurrentMinute(0);

        return match;
    }

    public List<MatchDTOSim> getMatchesListForMatchday(int matchday)
    {
        String weekId = String.valueOf(matchday);
        String redisKey="week:"+weekId;
        Map<Object,Object> matchesList = stringRedisTemplate.opsForHash().entries(redisKey);
        if(!matchesList.isEmpty())
        {
            return deserializationList(matchesList.values());
        }

        List<MatchDTOSim> matches = matchdaysRepository.findByWeekId(matchday).getMatches().stream().map(MatchDTOSim::new).toList();
        if(!matches.isEmpty())
        {
            Map<String,String> mapToCache = matches
                    .stream()
                    .collect(Collectors.toMap(
                            p->p.getId().toString(),
                            this::json
                    ));
            stringRedisTemplate.opsForHash().putAll(redisKey,mapToCache);
            stringRedisTemplate.expire(redisKey, Duration.ofMinutes(10));
        }
        return matches;
    }

    public String json(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    public List<MatchDTOSim> deserializationList(Collection<Object> values)
    {
        return values.stream()
                .map(
                        p-> {
                            try {
                                return objectMapper.readValue((String) p, MatchDTOSim.class);
                            } catch (JsonProcessingException e) {
                                throw new RuntimeException(e);
                            }
                        }
                )
                .toList();
    }

}
