CREATE INDEX IF NOT EXISTS when_idx ON eg_pgr_action ("when");
CREATE INDEX IF NOT EXISTS action_idx ON eg_pgr_action ("action");
CREATE INDEX IF NOT EXISTS servicerequestid_idx ON eg_pgr_action ("businesskey");