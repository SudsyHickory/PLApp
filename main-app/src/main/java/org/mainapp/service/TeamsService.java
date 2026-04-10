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

@Service
public class TeamsService {

    private final TeamsClientService teamsClientService;
    private final TeamsRepository teamsRepository;

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
        return teamsList.stream().sorted(Comparator.comparing(Team::getPoints).reversed()).map(DtoTeam::new).toList();
    }

    public List<DtoTeam> deserializationRanking(Set<ZSetOperations.TypedTuple<String>> ranking)
    {
        List<DtoTeam> teams = new ArrayList<>();
        for (ZSetOperations.TypedTuple<String> team : ranking) {
            String name = team.getValue();
            int points = team.getScore().intValue();
            teams.add(new DtoTeam(name,points));
        }
        return teams;
    }

    public Team mapToEntity(TeamDTO teamDTO)
    {
        Team team = new Team();
        team.setId(teamDTO.getId());
        team.setShortName(teamDTO.getShortName());
        team.setCrest(teamDTO.getCrest());
        team.setPoints(0);
        return team;
    }
}
