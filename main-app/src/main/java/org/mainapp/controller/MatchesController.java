package org.mainapp.controller;

import org.mainapp.data.Match;
import org.mainapp.data.MatchDTOSim;
import org.mainapp.service.MatchesService;
import org.mainapp.service.SimulationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/football")
public class MatchesController {

    private final MatchesService matchesService;
    private final SimulationService simulationService;

    public MatchesController(MatchesService matchesService, SimulationService simulationService) {
        this.matchesService = matchesService;
        this.simulationService = simulationService;
    }

    @GetMapping("/matches")
    public List<Match> getMatches()
    {
        return matchesService.getAllMatches();
    }

    @GetMapping("/matches/{matchday}")
    public List<MatchDTOSim> getMatchesByMatchday(@PathVariable int matchday)
    {
        return matchesService.getMatchesByMatchday(matchday);
    }

    @PostMapping("/matches/simulation/{matchday}")
    public void startSimulationForMatchday(@PathVariable int matchday)
    {
        simulationService.startSimulationForMatchday(matchday);
        System.out.println("Start symulacji dla kolejki: " + matchday);
    }

    @PostMapping("/matches/simulation/{matchday}/{matchId}")
    public void startReplayForMatch(@PathVariable int matchday, @PathVariable long matchId)
    {
        simulationService.startReplayForMatch(matchId);
    }


}
