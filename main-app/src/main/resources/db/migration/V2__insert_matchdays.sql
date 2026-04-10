INSERT INTO match_weeks (week_id, week_status)
SELECT i,'SCHEDULED' FROM generate_series(1,38) AS i