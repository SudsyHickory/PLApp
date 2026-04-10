package org.mainapp.repository;

import org.mainapp.data.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamsRepository extends JpaRepository<Team,Long> {
    Team findByShortName(String shortName);
}
