package org.mainapp.unit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mainapp.config.footballData.TeamDTO;
import org.mainapp.config.footballData.TeamsClientService;
import org.mainapp.config.footballData.TeamsResponse;
import org.mainapp.data.DtoTeam;
import org.mainapp.data.Team;
import org.mainapp.repository.TeamsRepository;
import org.mainapp.service.TeamsService;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TeamsServiceTest {

    @Mock
    private TeamsRepository repository;

    @Mock
    private TeamsClientService teamsClientService;

    @InjectMocks
    private TeamsService teamsService;

    @Test
    void shouldSearchInDatabaseWhenGetAllTeams()
    {
        teamsService.getAllTeams();
        verify(repository).findAll();
    }

    @Test
    void verifyGetDataFromApiAndSaveInDatabase()
    {

        TeamsResponse response = new TeamsResponse();
        List<TeamDTO> teamDTOList=List.of(
                new TeamDTO(1L,"name","crew"),
                new TeamDTO(2L,"name","crest")
        );
        response.setTeams(teamDTOList);
        List<Team> teams = new ArrayList<>();

        when(repository.findAll()).thenReturn(teams);
        when(teamsClientService.findAll()).thenReturn(response);

        teamsService.initializeData();
        ArgumentCaptor<List<Team>> captor = ArgumentCaptor.forClass(List.class);
        verify(repository, times(1)).saveAll(captor.capture());

        List<Team> savedTeams = captor.getValue();
        assertEquals(2, savedTeams.size());
        assertEquals("name", savedTeams.getFirst().getShortName());

        verify(teamsClientService).findAll();
    }

    @Test
    void checkIfUpdateCachedMatchesPlayed()
    {
        List<TeamDTO> teamDTOList=List.of(
                new TeamDTO(1L,"first","crew"),
                new TeamDTO(2L,"second","crest")
        );
        List<Team> teamList = teamDTOList.stream().map(t-> teamsService.mapToEntity(t)).toList();
        when(repository.findAll()).thenReturn(teamList);
        teamsService.initializeTeamCache();
        teamsService.updateCachedMatchesPlayed("second", 2);

        Set<ZSetOperations.TypedTuple<String>> ranking = new HashSet<>();
        ranking.add(new DefaultTypedTuple("first", 0.0));
        ranking.add(new DefaultTypedTuple("second", 0.0));

        List<DtoTeam> result = teamsService.deserializationRanking(ranking, Map.of());

        DtoTeam second = result.stream()
                .filter(p -> p.getName().equals("second"))
                .findFirst()
                .orElseThrow();
        assertEquals(2, second.getMatchesPlayed());
        assertEquals("crest", second.getCrest());

        DtoTeam first = result.stream()
                .filter(p -> p.getName().equals("first"))
                .findFirst()
                .orElseThrow();
        assertEquals(0, first.getMatchesPlayed());
    }

    @Test
    void initializeDataVerifyNoInteractions()
    {
        when(repository.findAll()).thenReturn(List.of(new Team()));

        teamsService.initializeData();

        verify(teamsClientService,never()).findAll();
        verify(repository,never()).saveAll(any());
    }

    @Test
    void initializeDataVerifyTeamsClientServiceError()
    {
        when(repository.findAll()).thenReturn(new ArrayList<>());
        when(teamsClientService.findAll()).thenThrow(new RuntimeException("API Connection Error"));

        RuntimeException exception=assertThrows(RuntimeException.class, ()->
        {
            teamsService.initializeData();
        });

        assertEquals("API Connection Error",exception.getMessage());
        verify(repository, never()).save(any());
    }


    @Test
    void verifyGetLeagueTable()
    {
        Team teamHigherGdOnTiedPoints = new Team();
        teamHigherGdOnTiedPoints.setPoints(10);
        teamHigherGdOnTiedPoints.setGoalsDifference(5);

        Team teamLowerGdOnTiedPoints = new Team();
        teamLowerGdOnTiedPoints.setPoints(10);
        teamLowerGdOnTiedPoints.setGoalsDifference(-3);

        when(repository.findAll()).thenReturn(List.of(
                teamLowerGdOnTiedPoints,
                new Team(5),
                new Team(20),
                teamHigherGdOnTiedPoints
        ));

        List<DtoTeam> result = teamsService.getLeagueTable();
        assertEquals(4, result.size());
        assertEquals(20,result.get(0).getPoints());
        assertEquals(10,result.get(1).getPoints());
        assertEquals(5,result.get(1).getGoalsDifference());
        assertEquals(10,result.get(2).getPoints());
        assertEquals(-3,result.get(2).getGoalsDifference());
        assertEquals(5,result.get(3).getPoints());
    }

    @Test
    void verifyDeserializationRanking()
    {
        Set<ZSetOperations.TypedTuple<String>> ranking = new HashSet<>();

        DefaultTypedTuple teamA = new DefaultTypedTuple("teamA", 20.0);
        DefaultTypedTuple teamB = new DefaultTypedTuple("teamB", 12.0);
        DefaultTypedTuple teamC = new DefaultTypedTuple("teamC", 23.0);

        ranking.add(teamA);
        ranking.add(teamB);
        ranking.add(teamC);

        Map<String, Integer> goalsDifferenceByTeam = Map.of(
                "teamA", 4,
                "teamB", -2
        );

        Team teamEntity = new Team();
        teamEntity.setShortName("teamB");
        teamEntity.setMatchesPlayed(9);
        when(repository.findAll()).thenReturn(List.of(teamEntity));
        teamsService.initializeTeamCache();

        List<DtoTeam> result = teamsService.deserializationRanking(ranking, goalsDifferenceByTeam);

        assertEquals(3,result.size());
        DtoTeam team = result.stream()
                .filter(p-> p.getName().equals("teamB"))
                .findFirst()
                .orElseThrow();
        assertEquals(12,team.getPoints());
        assertEquals(-2,team.getGoalsDifference());
        assertEquals(9,team.getMatchesPlayed());

        DtoTeam teamWithoutGdEntry = result.stream()
                .filter(p-> p.getName().equals("teamC"))
                .findFirst()
                .orElseThrow();
        assertEquals(0,teamWithoutGdEntry.getGoalsDifference());
    }

}
