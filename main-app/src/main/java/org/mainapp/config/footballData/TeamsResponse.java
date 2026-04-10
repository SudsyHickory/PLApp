package org.mainapp.config.footballData;

import java.util.List;

public class TeamsResponse {

    private List<TeamDTO> teams;

    public List<TeamDTO> getTeams() {
        return teams;
    }

    public void setTeams(List<TeamDTO> teams) {
        this.teams = teams;
    }
}
