CREATE TABLE function_budget_head (
    id BIGSERIAL PRIMARY KEY,
    function_id BIGINT NOT NULL,
    budget_head_id BIGINT NOT NULL,

    CONSTRAINT fk_fbh_budget_head
        FOREIGN KEY (budget_head_id)
        REFERENCES egf_budgethead(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_fbh_function
        FOREIGN KEY (function_id)
        REFERENCES function(id)
        ON DELETE CASCADE,

    CONSTRAINT uq_function_budget_head UNIQUE (budget_head_id, function_id)
);
