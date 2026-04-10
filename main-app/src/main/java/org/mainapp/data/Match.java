package org.mainapp.data;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.mainapp.config.footballData.MatchDTO;

import java.time.LocalDate;


@Entity
@Setter
@Getter
public class Match {

    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "homeTeamId")
    private Team homeTeam;

    private int homeTeamGoals;

    @ManyToOne
    @JoinColumn(name = "awayTeamId")
    private Team awayTeam;

    private int awayTeamGoals;

    @ManyToOne()
    @JoinColumn(name = "weekId")
    private Matchday matchday;

    private int currentMinute;


    @Enumerated(EnumType.STRING)
    private MatchStatus status;




    public Match() {

    }


    public int getCurrentMinute() {
        return currentMinute;
    }

    public void setCurrentMinute(int currentMinute) {
        this.currentMinute = currentMinute;
    }

    public Team getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(Team homeTeam) {
        this.homeTeam = homeTeam;
    }

    public Team getAwayTeam() {
        return awayTeam;
    }

    public void setAwayTeam(Team awayTeam) {
        this.awayTeam = awayTeam;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }



    public int getHomeTeamGoals() {
        return homeTeamGoals;
    }

    public void setHomeTeamGoals(int homeTeamGoals) {
        this.homeTeamGoals = homeTeamGoals;
    }



    public int getAwayTeamGoals() {
        return awayTeamGoals;
    }

    public void setAwayTeamGoals(int awayTeamGoals) {
        this.awayTeamGoals = awayTeamGoals;
    }

    public Matchday getMatchday() {
        return matchday;
    }

    public void setMatchday(Matchday matchday) {
        this.matchday = matchday;
    }

    public MatchStatus getStatus() {
        return status;
    }

    public void setStatus(MatchStatus status) {
        this.status = status;
    }
}
