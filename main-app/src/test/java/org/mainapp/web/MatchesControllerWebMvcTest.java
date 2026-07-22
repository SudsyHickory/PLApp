package org.mainapp.web;

import org.junit.jupiter.api.Test;
import org.mainapp.controller.MatchesController;
import org.mainapp.data.Match;
import org.mainapp.data.MatchDTOSim;
import org.mainapp.service.MatchesService;
import org.mainapp.service.SimulationService;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@WebMvcTest(MatchesController.class)
@AutoConfigureRestTestClient
public class MatchesControllerWebMvcTest {

    @Autowired
    RestTestClient restTestClient;

    @MockitoBean
    MatchesService matchesService;

    @MockitoBean
    SimulationService simulationService;

    @Test
    public void shouldReturnAllMatches()
    {

        List<Match> expectedResult = List.of(new Match());

        Mockito.when(matchesService.getAllMatches()).thenReturn(expectedResult);

        List<Match> matches = restTestClient.get().uri("/api/football/matches")
                .exchange()
                .expectBody(new ParameterizedTypeReference<List<Match>>() {
                })
                .returnResult()
                .getResponseBody();

        assertThat(matches).isNotNull();
        assertThat(matches.size()).isEqualTo(expectedResult.size());
    }

    @Test
    public void shouldStartSimulationForMatchday()
    {
        int matchdayId = 1;
        restTestClient.post().uri("/api/football/matches/simulation/{matchdayId}", matchdayId)
                .exchange()
                .expectStatus().isOk();
        Mockito.verify(simulationService).startSimulationForMatchday(matchdayId);
    }

    @Test
    public void shouldReturnBadRequest()
    {
        String matchdayId = "first";
        restTestClient.post().uri("/api/football/matches/simulation/{matchdayId}", matchdayId)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST);

        Mockito.verifyNoInteractions(simulationService);
    }

    @Test
    public void shouldReturnMatchesByMatchday()
    {
        List<MatchDTOSim> expectedResult = List.of(new MatchDTOSim());
        int matchdayId = 1;

        Mockito.when(matchesService.getMatchesByMatchday(1)).thenReturn(expectedResult);

        restTestClient.get().uri("/api/football/matches/{matchdayId}", matchdayId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<MatchDTOSim>>(){})
                .value(matches -> {
                    assertThat(matches).isNotNull();
                    assertThat(matches.size()).isEqualTo(expectedResult.size());
                });
    }


}
