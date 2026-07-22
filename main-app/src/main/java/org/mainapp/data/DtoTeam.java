package org.mainapp.data;

public class DtoTeam {

    private String name;
    private int points;
    private String crest;
    private int goalsDifference;
    private int matchesPlayed;

    public DtoTeam() {
    }

    public DtoTeam(String name, int points, String crest, int goalsDifference, int matchesPlayed) {
        this.name = name;
        this.points = points;
        this.crest = crest;
        this.goalsDifference = goalsDifference;
        this.matchesPlayed = matchesPlayed;
    }

    public DtoTeam(Team team)
    {
        this.name = team.getShortName();
        this.points = team.getPoints();
        this.crest = team.getCrest();
        this.goalsDifference = team.getGoalsDifference();
        this.matchesPlayed = team.getMatchesPlayed();
    }

    public String getCrest() {
        return crest;
    }

    public void setCrest(String crest) {
        this.crest = crest;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
