-- Need to Refer eg_user where ever eg_user is referred.

-- Create table if not exists for eg_bmc_Caste
CREATE TABLE IF NOT EXISTS eg_bmc_Criteria (
    ID SERIAL PRIMARY KEY,
    CriteriaType VARCHAR(255) NOT NULL,
    CriteriaValue VARCHAR(1000),
    CriteriaCondition VARCHAR(5),
    CreatedOn BIGINT NOT NULL,
    ModifiedOn BIGINT,
    CreatedBy VARCHAR(255) NOT NULL,
    ModifiedBy VARCHAR(255)
);


CREATE TABLE IF NOT EXISTS public.eg_bmc_scheme_criteria
(
    id SERIAL NOT NULL,
    criteriaid bigint NOT NULL,
    schemeid bigint NOT NULL,
    createdon bigint NOT NULL,
    modifiedon bigint,
    createdby character varying(255) COLLATE pg_catalog."default" NOT NULL,
    modifiedby character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT eg_bmc_scheme_criteria_pkey PRIMARY KEY (id),
    CONSTRAINT "unique_Scheme_Criteria" UNIQUE (criteriaid, schemeid)
)
