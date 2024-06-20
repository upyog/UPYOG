ALTER TABLE agreement ADD CONSTRAINT constraint_agr UNIQUE (agreement_name);

ALTER TABLE party1details ADD CONSTRAINT constraint_party UNIQUE (uid_p1);

CREATE TABLE public."physical_milestone_activity" (

    desn_id varchar(100) NOT NULL,

    desn_name varchar(100) NOT NULL Unique,

    desn_status varchar(100) NULL,
   
    createdby varchar(255) NULL,

    lastmodifiedby varchar(255) NULL,

    createdtime int8 NULL,

    lastmodifiedtime int8 NULL, 

CONSTRAINT desn_pkey PRIMARY KEY (desn_id)

);


