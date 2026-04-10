package org.mainapp.data;

public class DtoTeam {

    private String name;
    private int points;

    public DtoTeam() {
    }

    public DtoTeam(String name, int points) {
        this.name = name;
        this.points = points;
    }

    public DtoTeam(Team team)
    {
        this.name = team.getShortName();
        this.points = team.getPoints();
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
}
