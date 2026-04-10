ALTER TABLE teams_table
    ADD points INTEGER;

ALTER TABLE teams_table
    ALTER COLUMN points SET NOT NULL;