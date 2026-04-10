CREATE TABLE teams_table
(
    id         BIGINT NOT NULL,
    short_name VARCHAR(255),
    crest      VARCHAR(255),
    CONSTRAINT pk_teams_table PRIMARY KEY (id)
);