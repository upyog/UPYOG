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

CREATE TABLE IF NOT EXISTS "Scheme_Master" (
    "Scheme_ID" SERIAL PRIMARY KEY,
    "Source_Of_Fund" VARCHAR(255),
    "Start_Date" VARCHAR(255),
    "End_Date" VARCHAR(255),
    "Scheme_Name_En" VARCHAR(255),
    "Scheme_Name_Reg" VARCHAR(255),
    "Fund" VARCHAR(255),
    "Description_Of_the_Scheme" TEXT,
    "Upload_Document" VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS "Project_Master" (
    "Project_ID" SERIAL PRIMARY KEY,
    "Project_Number" VARCHAR(255),
    "Project_Name_En" VARCHAR(255),
    "Project_Name_Reg" VARCHAR(255),
    "Project_Description" TEXT,
    "Project_Timeline" VARCHAR(255),
    "Project_Start_Date" DATE,
    "Project_End_Date" DATE,
    "Scheme_Name" VARCHAR(255),
    "Approval_Number" VARCHAR(255),
    "Approval_Date" DATE,
    "Status" VARCHAR(255)
);

 

CREATE TABLE IF NOT EXISTS "Work" (
    "Work_ID" SERIAL PRIMARY KEY,
    "Project_ID" INTEGER REFERENCES "Project_Master"("Project_ID"),
    "Work_No" VARCHAR(255),
    "Work_Name" VARCHAR(255),
    "Project_Name" VARCHAR(255),
    "Department_Name" VARCHAR(255),
    "Work_Type" VARCHAR(255),
    "Work_Category" VARCHAR(255),
    "Work_Subtype" VARCHAR(255),
    "Project_Phase" VARCHAR(255),
    "Deviation_Percent" INTEGER,
    "Start_Location" VARCHAR(255),
    "End_Location" VARCHAR(255),
    "Financial_Year" VARCHAR(255),
    "Budget_Head" VARCHAR(255)
);
