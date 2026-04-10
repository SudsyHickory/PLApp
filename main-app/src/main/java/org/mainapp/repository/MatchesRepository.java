package org.mainapp.repository;

import org.mainapp.data.Match;
import org.mainapp.data.Matchday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchesRepository extends JpaRepository<Match, Long> {
    List<Match> findByMatchday(Matchday matchday);
}
