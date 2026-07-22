package org.mainapp.unit;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mainapp.config.footballData.MatchDTO;
import org.mainapp.config.footballData.MatchesClientService;
import org.mainapp.config.footballData.MatchesResponse;
import org.mainapp.config.footballData.TeamDTO;
import org.mainapp.data.*;
import org.mainapp.repository.MatchdaysRepository;
import org.mainapp.repository.MatchesRepository;
import org.mainapp.repository.TeamsRepository;
import org.mainapp.service.MatchesService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MatchesServiceTest {

    @Mock
    private MatchesRepository matchesRepository;

    @Mock
    private MatchesClientService matchesClientService;

    @Mock
    private MatchdaysRepository matchdaysRepository;

    @Mock
    private TeamsRepository teamsRepository;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private StringRedisTemplate stringRedisTemplate;

    @InjectMocks
    private MatchesService matchesService;

    @Test
    void getAllMatchesHappyPath()
    {
        when(matchesRepository.findAll()).thenReturn(List.of(new Match(), new Match()));
        List<Match> list = matchesService.getAllMatches();
        verify(matchesRepository,times(1)).findAll();
        assertEquals(2,list.size());
    }

    @Test
    void initializeDataBehaviourTesting()
    {
        when(matchesRepository.findAll()).thenReturn(List.of());
        MatchesResponse response = new MatchesResponse();
        response.setMatches(List.of(
                new MatchDTO(1L, 2, "ARS", new TeamDTO(), new TeamDTO(), LocalDate.now()),
                new MatchDTO(2L, 2, "ARS", new TeamDTO(), new TeamDTO(), LocalDate.now())
        ));
        when(matchesClientService.findAll()).thenReturn(response);
        matchesService.initializeData();
        verify(matchesRepository, times(1)).saveAll(any());
    }

    @Test
    void verifyMapToEntity()
    {
        MatchDTO matchDTO = new MatchDTO(1L, 2, "status",
                new TeamDTO(1L,"ARS","CREST"),
                new TeamDTO(2L,"CHE","CREST"),
                LocalDate.now());
        when(matchdaysRepository.findByWeekId(2)).thenReturn(new Matchday(2, MatchStatus.SCHEDULED));
        when(teamsRepository.findByShortName("ARS")).thenReturn(new Team());
        when(teamsRepository.findByShortName("CHE")).thenReturn(new Team());

        Match  match = matchesService.mapToEntity(matchDTO);

        assertEquals(1L, match.getId());
        assertEquals(MatchStatus.SCHEDULED, match.getStatus());
    }

    @Test
    void verifyGetMatchesListForMatchday()
    {
        @SuppressWarnings("unchecked")
        HashOperations<String, Object, Object> hashOperations = mock(HashOperations.class);

        String redisKey = "week:2";
        HashMap<Object, Object> cachedData = new HashMap<>();
        cachedData.put("1", "{\"id\": 1, \"homeTeam\": \"Arsenal\"}");

        when(stringRedisTemplate.opsForHash()).thenReturn(hashOperations);
        when(hashOperations.entries(redisKey)).thenReturn(cachedData);

        MatchDTOSim simMatch = new MatchDTOSim();
        try {
            when(objectMapper.readValue(anyString(), eq(MatchDTOSim.class))).thenReturn(simMatch);
        } catch (JsonProcessingException e) {

        }

        List<MatchDTOSim> result = matchesService.getMatchesListForMatchday(2);
        assertEquals(1, result.size());
        verifyNoInteractions(matchdaysRepository);
    }

    @Test
    void verifyExceptionInJson() throws JsonProcessingException {
        when(objectMapper.writeValueAsString(any())).thenThrow(new JsonProcessingException("message") {});

        RuntimeException exception = assertThrows(RuntimeException.class, () ->{
            matchesService.json(new Object());
        });

        assertThat(exception.getMessage().contains("message"));

    }

}


