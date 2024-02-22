CREATE TABLE IF NOT EXISTS "state_sor" (

    "sor_id" SERIAL PRIMARY KEY,

    "sor_name" VARCHAR(255),

    "start_date" VARCHAR(100),

    "end_date" VARCHAR(100),

    "chapter" VARCHAR(255),

    "item_no" VARCHAR(255),

    "description_of_item" TEXT,

    "unit" VARCHAR(255),

    "rate" INTEGER

);



 

CREATE TABLE IF NOT EXISTS "work" (
    "work_id" SERIAL PRIMARY KEY,
    "project_id" INTEGER REFERENCES "project_master"("Project_ID"),
    "work_no" VARCHAR(255),
    "work_name" VARCHAR(255),
    "project_name" VARCHAR(255),
    "department_name" VARCHAR(255),
    "work_type" VARCHAR(255),
    "work_category" VARCHAR(255),
    "work_subtype" VARCHAR(255),
    "project_phase" VARCHAR(255),
    "deviation_percent" INTEGER,
    "start_location" VARCHAR(255),
    "end_location" VARCHAR(255),
    "financial_year" VARCHAR(255),
    "budget_head" VARCHAR(255)
);

