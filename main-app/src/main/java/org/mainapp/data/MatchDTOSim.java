package org.mainapp.data;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;


public class MatchDTOSim {

    private Long id;

    private String homeTeam;

    private int homeTeamGoals;

    private String awayTeam;

    private int awayTeamGoals;

    private int currentMinute;


    private Integer matchday;

    @Enumerated(EnumType.STRING)
    private MatchStatus status;


    public MatchDTOSim() {
    }

    public MatchDTOSim(Match match)
    {
        this.id=match.getId();
        this.homeTeam = match.getHomeTeam().getShortName();
        this.awayTeam = match.getAwayTeam().getShortName();
        this.homeTeamGoals = match.getHomeTeamGoals();
        this.awayTeamGoals= match.getAwayTeamGoals();
        this.currentMinute = match.getCurrentMinute();
        this.matchday = match.getMatchday().getWeekId();
        this.status = match.getStatus();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(String homeTeam) {
        this.homeTeam = homeTeam;
    }

    public int getHomeTeamGoals() {
        return homeTeamGoals;
    }

    public void setHomeTeamGoals(int homeTeamGoals) {
        this.homeTeamGoals = homeTeamGoals;
    }

    public String getAwayTeam() {
        return awayTeam;
    }

    public void setAwayTeam(String awayTeam) {
        this.awayTeam = awayTeam;
    }

    public int getAwayTeamGoals() {
        return awayTeamGoals;
    }

    public void setAwayTeamGoals(int awayTeamGoals) {
        this.awayTeamGoals = awayTeamGoals;
    }

    public Integer getMatchday() {
        return matchday;
    }

    public void setMatchday(Integer matchday) {
        this.matchday = matchday;
    }

    public MatchStatus getStatus() {
        return status;
    }

    public void setStatus(MatchStatus status) {
        this.status = status;
    }

    public int getCurrentMinute() {
        return currentMinute;
    }

    public void setCurrentMinute(int currentMinute) {
        this.currentMinute = currentMinute;
    }
}
