package org.mainapp.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "teams_table")
public class Team {

    @Id
    private Long id;

    private String shortName;

    private String crest;

    private int points;

    private int goalsDifference;

    private int matchesPlayed;

    @OneToMany(fetch = FetchType.LAZY,mappedBy = "homeTeam")
    @JsonIgnore
    private List<Match> matchesAsHomeTeam;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "awayTeam")
    @JsonIgnore
    private List<Match> matchesAsAwayTeam;


    public Team() {
    }

    public Team(int points) {
        this.points = points;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getGoalsDifference() {
        return goalsDifference;
    }

    public void setGoalsDifference(int goalsDifference) {
        this.goalsDifference = goalsDifference;
    }

    public int getMatchesPlayed() {
        return matchesPlayed;
    }

    public void setMatchesPlayed(int matchesPlayed) {
        this.matchesPlayed = matchesPlayed;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getCrest() {
        return crest;
    }

    public void setCrest(String crest) {
        this.crest = crest;
    }
}
