package org.mainapp.config.footballData;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MatchDTO {
    private Long id;
    private Integer matchday;
    private String status;
    @JsonProperty("homeTeam")
    private TeamDTO homeTeamName;
    @JsonProperty("awayTeam")
    private TeamDTO awayTeamName;

    @JsonProperty("utcDate")
    LocalDate date;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getMatchday() {
        return matchday;
    }

    public void setMatchday(Integer matchday) {
        this.matchday = matchday;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public TeamDTO getHomeTeamName() {
        return homeTeamName;
    }

    public void setHomeTeamName(TeamDTO homeTeamName) {
        this.homeTeamName = homeTeamName;
    }

    public TeamDTO getAwayTeamName() {
        return awayTeamName;
    }

    public void setAwayTeamName(TeamDTO awayTeamName) {
        this.awayTeamName = awayTeamName;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
