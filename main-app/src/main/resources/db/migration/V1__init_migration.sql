CREATE TABLE match
(
    id              BIGINT  NOT NULL,
    home_team       VARCHAR(255),
    home_team_goals INTEGER NOT NULL,
    away_team       VARCHAR(255),
    away_team_goals INTEGER NOT NULL,
    week_id         INTEGER,
    current_minute  INTEGER NOT NULL,
    status          VARCHAR(255),
    CONSTRAINT pk_match PRIMARY KEY (id)
);

CREATE TABLE match_weeks
(
    week_id     INTEGER NOT NULL,
    week_status VARCHAR(255),
    CONSTRAINT pk_match_weeks PRIMARY KEY (week_id)
);

ALTER TABLE match
    ADD CONSTRAINT FK_MATCH_ON_WEEKID FOREIGN KEY (week_id) REFERENCES match_weeks (week_id);

