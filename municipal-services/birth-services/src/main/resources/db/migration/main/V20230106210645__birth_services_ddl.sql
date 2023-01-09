ALTER TABLE public.eg_birth_details
ALTER COLUMN gender TYPE character varying(20) COLLATE pg_catalog."default";

ALTER TABLE public.eg_birth_details_audit
ALTER COLUMN gender TYPE character varying(20) COLLATE pg_catalog."default";

ALTER TABLE public.eg_register_birth_details
ALTER COLUMN gender TYPE character varying(20) COLLATE pg_catalog."default";

ALTER TABLE public.eg_register_birth_details_audit
ALTER COLUMN gender TYPE character varying(20) COLLATE pg_catalog."default";

DROP TABLE public.eg_birth_adoption_father_information,
    public.eg_birth_adoption_father_information_audit,
    public.eg_birth_adoption_mother_information,
    public.eg_birth_adoption_mother_information_audit,
    public.eg_birth_adoption_permanent_address,
    public.eg_birth_adoption_permanent_address_audit,
    public.eg_birth_adoption_present_address,
    public.eg_birth_adoption_present_address_audit,public.eg_register_birth_biological_father_information,
    public.eg_register_birth_biological_father_information_audit,
    public.eg_register_birth_biological_mother_information,
    public.eg_register_birth_biological_mother_information_audit,
    public.eg_register_birth_biological_permanent_address,
    public.eg_register_birth_biological_permanent_address_audit,
    public.eg_register_birth_biological_present_address,
    public.eg_register_birth_biological_present_address_audit,
    public.eg_birth_correction,public.eg_birth_correction_audit,
    public.eg_birth_correction_permanent_address,
    public.eg_birth_correction_permanent_address_audit,
    public.eg_birth_correction_present_address,
    public.eg_birth_correction_present_address_audit;


Alter Table public.eg_birth_permanent_address Add Column res_asso_no_ml character varying(1000) COLLATE pg_catalog."default",
Add Column streetname_en character varying(1000) COLLATE pg_catalog."default",
Add Column streetname_ml character varying(1000) COLLATE pg_catalog."default";

Alter Table public.eg_birth_permanent_address_audit Add Column res_asso_no_ml character varying(1000) COLLATE pg_catalog."default",
Add Column streetname_en character varying(1000) COLLATE pg_catalog."default",
Add Column streetname_ml character varying(1000) COLLATE pg_catalog."default";

Alter Table public.eg_birth_present_address Add Column res_asso_no_ml character varying(1000) COLLATE pg_catalog."default",
Add Column streetname_en character varying(1000) COLLATE pg_catalog."default",
Add Column streetname_ml character varying(1000) COLLATE pg_catalog."default";

Alter Table public.eg_birth_present_address_audit Add Column res_asso_no_ml character varying(1000) COLLATE pg_catalog."default",
Add Column streetname_en character varying(1000) COLLATE pg_catalog."default",
Add Column streetname_ml character varying(1000) COLLATE pg_catalog."default";

Alter Table public.eg_register_birth_permanent_address Add Column res_asso_no_ml character varying(1000) COLLATE pg_catalog."default",
Add Column streetname_en character varying(1000) COLLATE pg_catalog."default",
Add Column streetname_ml character varying(1000) COLLATE pg_catalog."default";

Alter Table public.eg_register_birth_permanent_address_audit Add Column res_asso_no_ml character varying(1000) COLLATE pg_catalog."default",
Add Column streetname_en character varying(1000) COLLATE pg_catalog."default",
Add Column streetname_ml character varying(1000) COLLATE pg_catalog."default";

Alter Table public.eg_register_birth_present_address Add Column res_asso_no_ml character varying(1000) COLLATE pg_catalog."default",
Add Column streetname_en character varying(1000) COLLATE pg_catalog."default",
Add Column streetname_ml character varying(1000) COLLATE pg_catalog."default";

Alter Table public.eg_register_birth_present_address_audit Add Column res_asso_no_ml character varying(1000) COLLATE pg_catalog."default",
Add Column streetname_en character varying(1000) COLLATE pg_catalog."default",
Add Column streetname_ml character varying(1000) COLLATE pg_catalog."default";