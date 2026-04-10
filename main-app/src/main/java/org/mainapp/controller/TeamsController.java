package org.mainapp.controller;

import org.mainapp.config.footballData.TeamDTO;
import org.mainapp.data.DtoTeam;
import org.mainapp.data.Team;
import org.mainapp.service.TeamsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/football/teams")
public class TeamsController {

    private final TeamsService teamsService;


    public TeamsController(TeamsService teamsService) {
        this.teamsService = teamsService;
    }

    @GetMapping
    public List<Team> getAllTeams()
    {
        return teamsService.getAllTeams();
    }

    @GetMapping("/table")
    public List<DtoTeam> getLeagueTable()
    {
        return teamsService.getLeagueTable();
    }


}
