package org.mainapp.service;

import org.mainapp.config.footballData.TeamDTO;
import org.mainapp.config.footballData.TeamsClientService;
import org.mainapp.data.DtoTeam;
import org.mainapp.data.Team;
import org.mainapp.repository.TeamsRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TeamsService {

    public record TeamCacheEntry(String crest, int matchesPlayed) {}

    private final TeamsClientService teamsClientService;
    private final TeamsRepository teamsRepository;
    private final Map<String, TeamCacheEntry> teamCache = new ConcurrentHashMap<>();

    public TeamsService(TeamsClientService teamsClientService, TeamsRepository teamsRepository) {
        this.teamsClientService = teamsClientService;
        this.teamsRepository = teamsRepository;
    }


    public List<Team> getAllTeams()
    {
        return teamsRepository.findAll();
    }

    public void initializeData() {
        List<Team> teamsList = teamsRepository.findAll();
        if(teamsList.isEmpty())
        {
            teamsList = teamsClientService.findAll().getTeams().stream().map(this::mapToEntity).toList();
            teamsRepository.saveAll(teamsList);
        }
    }

    public List<DtoTeam> getLeagueTable()
    {
        List<Team> teamsList = teamsRepository.findAll();
        return teamsList.stream().sorted(Comparator.comparing(Team::getPoints).thenComparing(Team::getGoalsDifference).reversed()).map(DtoTeam::new).toList();
    }

    public List<DtoTeam> deserializationRanking(Set<ZSetOperations.TypedTuple<String>> ranking, Map<String,Integer> goalsDifferenceByTeam)
    {
        List<DtoTeam> teams = new ArrayList<>();
        for (ZSetOperations.TypedTuple<String> team : ranking) {
            String name = team.getValue();
            TeamCacheEntry cached = teamCache.get(name);
            String crest = cached.crest();
            int matchesPlayed = cached.matchesPlayed();
            int goalDifference = goalsDifferenceByTeam.getOrDefault(name, 0);
            int points = team.getScore().intValue();
            teams.add(new DtoTeam(name,points,crest,goalDifference,matchesPlayed));
        }
        teams.sort(Comparator.comparing(DtoTeam::getPoints).thenComparing(DtoTeam::getGoalsDifference).reversed());
        return teams;
    }

    public void initializeTeamCache()
    {
        teamCache.clear();
        teamsRepository.findAll().forEach(team ->
                teamCache.put(team.getShortName(), new TeamCacheEntry(team.getCrest(), team.getMatchesPlayed())));
    }

    public void updateCachedMatchesPlayed(String shortName, int matchesPlayed)
    {
        teamCache.computeIfPresent(shortName, (name, entry) -> new TeamCacheEntry(entry.crest(), matchesPlayed));
    }

    public Team mapToEntity(TeamDTO teamDTO)
    {
        Team team = new Team();
        team.setId(teamDTO.getId());
        team.setShortName(teamDTO.getShortName());
        team.setCrest(teamDTO.getCrest());
        team.setPoints(0);
        team.setGoalsDifference(0);
        team.setMatchesPlayed(0);
        return team;
    }
}
