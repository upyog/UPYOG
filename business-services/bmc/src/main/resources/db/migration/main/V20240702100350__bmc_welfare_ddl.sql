-- Need to Refer eg_user where ever eg_user is referred.

-- Create table if not exists for eg_bmc_Caste
CREATE TABLE IF NOT EXISTS eg_bmc_scheme_Group (
    ID SERIAL PRIMARY KEY,
    Name VARCHAR(255) NOT NULL,
    Description VARCHAR(1000),
    CreatedOn BIGINT NOT NULL,
    ModifiedOn BIGINT,
    CreatedBy VARCHAR(255) NOT NULL,
    ModifiedBy VARCHAR(255)
);

TRUNCATE TABLE eg_bmc_scheme_Group RESTART IDENTITY CASCADE;

Insert into eg_bmc_scheme_Group(name,description, createdon,modifiedon,createdby,modifiedby) values
(
	'Skill Development',
	'Schemes for Skill Development',
	extract(epoch from now()),
	extract(epoch from now()),
	'system',
	'system'
);

Insert into eg_bmc_scheme_Group(name,description, createdon,modifiedon,createdby,modifiedby) values
(
	'Empowerment',
	'Schemes for Citizen Empowerment',
	extract(epoch from now()),
	extract(epoch from now()),
	'system',
	'system'
);

Insert into eg_bmc_scheme_Group(name,description, createdon,modifiedon,createdby,modifiedby) values
(
	'Pension Schemes',
	'Pension Schemes for Citizens',
	extract(epoch from now()),
	extract(epoch from now()),
	'system',
	'system'
);

ALTER TABLE IF EXISTS public.eg_bmc_schemes DROP COLUMN IF EXISTS "SchemeGroupID";

ALTER TABLE IF EXISTS public.eg_bmc_schemes
    ADD COLUMN "SchemeGroupID" bigint;

ALTER TABLE IF EXISTS public.eg_bmc_schemes DROP CONSTRAINT IF EXISTS "eg_schemegroup_FK";

ALTER TABLE IF EXISTS public.eg_bmc_schemes
    ADD CONSTRAINT "eg_schemegroup_FK" FOREIGN KEY ("SchemeGroupID")
    REFERENCES public.eg_bmc_scheme_group (id) MATCH SIMPLE
    ON UPDATE CASCADE
    ON DELETE CASCADE
    NOT VALID;

DROP INDEX IF EXISTS public."fki_eg_schemegroup_FK";

CREATE INDEX IF NOT EXISTS "fki_eg_schemegroup_FK"
    ON public.eg_bmc_schemes("SchemeGroupID");