ALTER TABLE  public.eg_register_birth_statitical_information
ALTER COLUMN father_educationid type character varying(200),
			ALTER COLUMN father_education_subid type character varying(200),
			ALTER COLUMN father_proffessionid type character varying(200),
			ALTER COLUMN mother_educationid type character varying(200),
			ALTER COLUMN mother_education_subid type character varying(200),
			ALTER COLUMN mother_proffessionid type character varying(200);

ALTER TABLE  public.eg_register_birth_statitical_information_audit
ALTER COLUMN father_educationid type character varying(200),
			ALTER COLUMN father_education_subid type character varying(200),
			ALTER COLUMN father_proffessionid type character varying(200),
			ALTER COLUMN mother_educationid type character varying(200),
			ALTER COLUMN mother_education_subid type character varying(200),
			ALTER COLUMN mother_proffessionid type character varying(200);