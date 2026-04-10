package org.mainapp.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "match_weeks")
public class Matchday {

    @Id
    Integer weekId;

    @Enumerated(EnumType.STRING)
    MatchStatus weekStatus;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "matchday")
    @JsonIgnore
    List<Match> matches;

    public Matchday() {
    }

    public Matchday(Integer weekId, MatchStatus weekStatus) {
        this.weekId = weekId;
        this.weekStatus = weekStatus;
    }

    public Integer getWeekId() {
        return weekId;
    }

    public void setWeekId(Integer weekId) {
        this.weekId = weekId;
    }

    public MatchStatus getWeekStatus() {
        return weekStatus;
    }

    public void setWeekStatus(MatchStatus weekStatus) {
        this.weekStatus = weekStatus;
    }

    public List<Match> getMatches() {
        return matches;
    }

    public void setMatches(List<Match> matches) {
        this.matches = matches;
    }
}
