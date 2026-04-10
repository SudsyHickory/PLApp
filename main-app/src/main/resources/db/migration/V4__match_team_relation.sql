ALTER TABLE match
    ADD away_team_id BIGINT;

ALTER TABLE match
    ADD home_team_id BIGINT;

ALTER TABLE match
    ADD CONSTRAINT FK_MATCH_ON_AWAYTEAMID FOREIGN KEY (away_team_id) REFERENCES teams_table (id);

ALTER TABLE match
    ADD CONSTRAINT FK_MATCH_ON_HOMETEAMID FOREIGN KEY (home_team_id) REFERENCES teams_table (id);

ALTER TABLE match
DROP
COLUMN away_team;

ALTER TABLE match
DROP
COLUMN home_team;