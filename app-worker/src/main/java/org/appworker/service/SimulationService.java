package org.appworker.service;


import org.appworker.data.MatchAction;
import org.appworker.data.MatchDTOSim;
import org.appworker.data.MatchEvent;
import org.appworker.data.MatchStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class SimulationService {

    @Autowired
    private KafkaTemplate<Long, Object> kafkaTemplate;
    private final Random random = new Random();

    @Async
    public void simulate(MatchDTOSim match)
    {
        Long matchId = match.getId();
        int homeGoals = 0;
        int awayGoals = 0;
        MatchEvent event;
        match.setStatus(MatchStatus.LIVE);
        event = new MatchEvent(match,0, MatchAction.STARTED);
        kafkaTemplate.send("match-events", matchId, event);

        for (int minute = 1; minute < 90; minute++) {
            match.setCurrentMinute(minute);
            event = new MatchEvent(match,minute,MatchAction.UPDATE);
            kafkaTemplate.send("match-events",matchId,event);

            event = null;

            if (isGoal()) {
                if (whichTeam() == 0)
                    homeGoals++;
                else awayGoals++;
                match.setAwayTeamGoals(awayGoals);
                match.setHomeTeamGoals(homeGoals);
                event = new MatchEvent(match,minute, MatchAction.GOAL);
            }
            if (event != null) {
                kafkaTemplate.send("match-events", matchId, event);
            }
            try
            {
                Thread.sleep(1000);
            } catch (InterruptedException e)
            {
                return;
            }
        }
        match.setStatus(MatchStatus.FINISHED);
        event = new MatchEvent(match,90, MatchAction.FINISHED);
        kafkaTemplate.send("match-events", matchId, event);
    }


    private boolean isGoal() { return random.nextDouble() > 0.98; }
    private int whichTeam() { return random.nextInt(2); }


}
