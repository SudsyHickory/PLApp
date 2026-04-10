package org.mainapp.repository;

import org.mainapp.data.Matchday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchdaysRepository extends JpaRepository<Matchday,Integer> {
    Matchday findByWeekId(Integer matchday);
}
