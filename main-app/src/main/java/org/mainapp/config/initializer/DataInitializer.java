package org.mainapp.config.initializer;

import org.mainapp.service.MatchesService;
import org.mainapp.service.TeamsService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final MatchesService matchesService;
    private final TeamsService teamsService;

    public DataInitializer(MatchesService matchesService, TeamsService teamsService) {
        this.matchesService = matchesService;
        this.teamsService = teamsService;
    }

    @Override
    public void run(String... args) throws Exception {
        teamsService.initializeData();
        matchesService.initializeData();
    }
}
