-- CREATE TABLE budget_header (
--     id BIGSERIAL PRIMARY KEY,

--     currentfinancialyearid BIGINT NOT NULL,
--     financialyearid BIGINT NOT NULL,

--     name VARCHAR(100) NOT NULL,

--     state_id BIGINT,
--     version BIGINT DEFAULT 0,

--     -- audit fields from AbstractAuditable
--     createdby BIGINT,
--     createddate TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
--     lastmodifiedby BIGINT,
--     lastmodifieddate TIMESTAMP WITHOUT TIME ZONE,

--     CONSTRAINT fk_financialyear FOREIGN KEY (financialyearid)
--         REFERENCES financialyear(id) ON DELETE CASCADE,

--     CONSTRAINT fk_currentfinancialyear FOREIGN KEY (currentfinancialyearid)
--         REFERENCES financialyear(id) ON DELETE CASCADE,

--     CONSTRAINT fk_budget_state FOREIGN KEY (state_id)
--         REFERENCES eg_wf_states(id) ON DELETE CASCADE
-- );


-- CREATE INDEX idx_budget_header_fy ON budget_header (financialyearid);
-- CREATE INDEX idx_budget_header_curr_fy ON budget_header (currentfinancialyearid);
-- CREATE INDEX idx_budget_header_state ON budget_header (state_id);
-- CREATE INDEX idx_budget_header_name ON budget_header (name);
