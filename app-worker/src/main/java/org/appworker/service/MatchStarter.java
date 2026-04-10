package org.appworker.service;

import org.appworker.data.MatchDTOSim;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class MatchStarter {

    private final SimulationService simulationService;

    public MatchStarter(SimulationService simulationService) {
        this.simulationService = simulationService;
    }

    @KafkaListener(topics = "simulation", groupId = "simulation-group")
    public void simulateMatch(MatchDTOSim match) {
        simulationService.simulate(match);
    }
}
