package org.mainapp.integration;


import org.junit.jupiter.api.Test;
import org.mainapp.data.Match;
import org.mainapp.data.MatchStatus;
import org.mainapp.data.Matchday;
import org.mainapp.repository.MatchesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
public class MatchesRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:latest");


    @Autowired
    private MatchesRepository matchesRepository;

    @Test
    void shouldSaveAndFindMatch() {

        Match match = new Match();
        match.setId(101L);
        match.setStatus(MatchStatus.SCHEDULED);

        matchesRepository.save(match);

        assertThat(matchesRepository.findById(101L)).isPresent();
    }

    @Test
    void shouldFindByMatchday()
    {
        Match match = new Match();
        match.setId(101L);
        match.setStatus(MatchStatus.SCHEDULED);

        Matchday matchday = new Matchday(2, MatchStatus.SCHEDULED);
        match.setMatchday(matchday);

        matchesRepository.save(match);

        List<Match> matches = matchesRepository.findByMatchday(matchday);

        assertThat(matches.getFirst()).isNotNull();
        assertThat(matches.getFirst().getId()).isEqualTo(match.getId());
    }


}
