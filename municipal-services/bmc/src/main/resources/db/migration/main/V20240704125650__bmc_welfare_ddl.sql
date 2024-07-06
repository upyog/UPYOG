CREATE TABLE IF NOT EXISTS public.eg_bmc_userqualification
(
    id bigint NOT NULL,
    "userID" bigint,
    "qualificatioID" bigint,
    createdon bigint,
    modifiedon bigint,
    createdby character varying(255),
    modifiedby character varying(255),
    CONSTRAINT eg_bmc_user_qualification_pkey PRIMARY KEY (id),
    CONSTRAINT fk_userdetail FOREIGN KEY (id)
        REFERENCES public.eg_bmc_userotherdetails (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE
        NOT VALID
);

-- Index: fki_fk_userdetail

DROP INDEX IF EXISTS public.fki_fk_userdetail;

CREATE INDEX IF NOT EXISTS fki_fk_userdetail
    ON public.eg_bmc_userqualification USING btree
    (id ASC NULLS LAST);

TRUNCATE TABLE eg_bmc_qualificationmaster RESTART IDENTITY;

Insert into eg_bmc_qualificationmaster(name, createdon,modifiedon,createdby,modifiedby) values
(
	'Postgraduation',    
	extract(epoch from now()),
	extract(epoch from now()),
	'system',
	'system'
);

Insert into eg_bmc_qualificationmaster(name, createdon,modifiedon,createdby,modifiedby) values
(
	'Graduation',    
	extract(epoch from now()),
	extract(epoch from now()),
	'system',
	'system'
);

Insert into eg_bmc_qualificationmaster(name, createdon,modifiedon,createdby,modifiedby) values
(
	'Postgraduate Diploma',    
	extract(epoch from now()),
	extract(epoch from now()),
	'system',
	'system'
);

Insert into eg_bmc_qualificationmaster(name, createdon,modifiedon,createdby,modifiedby) values
(
	'Diploma',    
	extract(epoch from now()),
	extract(epoch from now()),
	'system',
	'system'
);

Insert into eg_bmc_qualificationmaster(name, createdon,modifiedon,createdby,modifiedby) values
(
	'Senior Secondary',    
	extract(epoch from now()),
	extract(epoch from now()),
	'system',
	'system'
);

Insert into eg_bmc_qualificationmaster(name, createdon,modifiedon,createdby,modifiedby) values
(
	'High School',    
	extract(epoch from now()),
	extract(epoch from now()),
	'system',
	'system'
);