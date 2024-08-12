DROP TABLE IF EXISTS eg_bmc_criteriatype CASCADE;
CREATE TABLE IF NOT EXISTS eg_bmc_criteriatype
(
    ID SERIAL PRIMARY KEY,
    criteriaType character varying(50)  NOT NULL,
    description character varying(100)  NOT NULL Default '',
    createdon bigint Not Null DEFAULT extract(epoch from now()),
    modifiedon  bigint DEFAULT extract(epoch from now()),
    lastmodifiedby VARCHAR(255) DEFAULT 'system',
    createdby VARCHAR(255) NOT NULL DEFAULT 'system'
);

DROP TABLE IF EXISTS eg_bmc_criteriacondition CASCADE;
CREATE TABLE IF NOT EXISTS eg_bmc_criteriacondition
(
    ID SERIAL PRIMARY KEY,
    criteriacondition character varying(50)  NOT NULL,
    description character varying(100)  NOT NULL Default '',
    createdon bigint Not Null DEFAULT extract(epoch from now()),
    modifiedon  bigint DEFAULT extract(epoch from now()),
    lastmodifiedby VARCHAR(255) DEFAULT 'system',
    createdby VARCHAR(255) NOT NULL DEFAULT 'system'
);




TRUNCATE TABLE eg_bmc_criteriatype RESTART IDENTITY;
Insert into eg_bmc_criteriatype(criteriaType,description) values('Gender','Gender');
Insert into eg_bmc_criteriatype(criteriaType,description) values('Age','Age');
Insert into eg_bmc_criteriatype(criteriaType,description) values('Income','Income');
Insert into eg_bmc_criteriatype(criteriaType,description) values('Disability','Disability');
Insert into eg_bmc_criteriatype(criteriaType,description) values('Education','Education');
Insert into eg_bmc_criteriatype(criteriaType,description) values('Document','Document');

TRUNCATE TABLE eg_bmc_criteriacondition RESTART IDENTITY;
Insert into eg_bmc_criteriacondition(criteriacondition,description) values('=','Equal');
Insert into eg_bmc_criteriacondition(criteriacondition,description) values('>=','Greater than Equal To');
Insert into eg_bmc_criteriacondition(criteriacondition,description) values('<=','Less Than Equal to');
Insert into eg_bmc_criteriacondition(criteriacondition,description) values('!=','Not Equal');
Insert into eg_bmc_criteriacondition(criteriacondition,description) values('>','Greater Than');
Insert into eg_bmc_criteriacondition(criteriacondition,description) values('<','Less Than');


TRUNCATE TABLE eg_bmc_qualificationmaster RESTART IDENTITY;

Insert into eg_bmc_qualificationmaster(name, createdon,modifiedon,createdby,modifiedby) values
(
	'Primary',    
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
	'Diploma',    
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
	'Postgraduation',    
	extract(epoch from now()),
	extract(epoch from now()),
	'system',
	'system'
);


DROP TABLE IF EXISTS eg_bmc_Criteria CASCADE;

CREATE TABLE IF NOT EXISTS eg_bmc_Criteria (
    ID SERIAL PRIMARY KEY,
    CriteriaType bigint,
    CriteriaValue VARCHAR(1000),
    CriteriaCondition bigint,
    CreatedOn BIGINT NOT NULL,
    ModifiedOn BIGINT,
    CreatedBy VARCHAR(255) NOT NULL,
    ModifiedBy VARCHAR(255)
);

ALTER TABLE IF EXISTS eg_bmc_criteria
    ADD CONSTRAINT "fK_type" FOREIGN KEY (criteriatype)
    REFERENCES eg_bmc_criteriatype (id) MATCH SIMPLE
    ON UPDATE CASCADE
    ON DELETE CASCADE
    NOT VALID;
CREATE INDEX IF NOT EXISTS "fki_fK_type"
    ON eg_bmc_criteria(criteriatype);

ALTER TABLE IF EXISTS eg_bmc_criteria
    ADD CONSTRAINT fk_condiotion FOREIGN KEY (criteriacondition)
    REFERENCES eg_bmc_criteriacondition (id) MATCH SIMPLE
    ON UPDATE CASCADE
    ON DELETE CASCADE
    NOT VALID;
CREATE INDEX IF NOT EXISTS fki_fk_condiotion
    ON eg_bmc_criteria(criteriacondition);

TRUNCATE TABLE eg_bmc_Criteria RESTART IDENTITY;

Insert into eg_bmc_Criteria(CriteriaType,CriteriaValue,CriteriaCondition, createdon,modifiedon,createdby,modifiedby) values
(
	1,
	'FEMALE',
    1,
	extract(epoch from now()),
	extract(epoch from now()),
	'system',
	'system'
);

Insert into eg_bmc_Criteria(CriteriaType,CriteriaValue,CriteriaCondition, createdon,modifiedon,createdby,modifiedby) values
(
	1,
	'MALE',
    1,
	extract(epoch from now()),
	extract(epoch from now()),
	'system',
	'system'
);

Insert into eg_bmc_Criteria(CriteriaType,CriteriaValue,CriteriaCondition, createdon,modifiedon,createdby,modifiedby) values
(
	1,
	'TRANSGENDER',
    1,
	extract(epoch from now()),
	extract(epoch from now()),
	'system',
	'system'
);

Insert into eg_bmc_Criteria(CriteriaType,CriteriaValue,CriteriaCondition, createdon,modifiedon,createdby,modifiedby) values
(
	2,
	'18',
    2,
	extract(epoch from now()),
	extract(epoch from now()),
	'system',
	'system'
);

Insert into eg_bmc_Criteria(CriteriaType,CriteriaValue,CriteriaCondition, createdon,modifiedon,createdby,modifiedby) values
(
	2,
	'60',
    3,
	extract(epoch from now()),
	extract(epoch from now()),
	'system',
	'system'
);

Insert into eg_bmc_Criteria(CriteriaType,CriteriaValue,CriteriaCondition, createdon,modifiedon,createdby,modifiedby) values
(
	3,
	'100000',
    3,
	extract(epoch from now()),
	extract(epoch from now()),
	'system',
	'system'
);

Insert into eg_bmc_Criteria(CriteriaType,CriteriaValue,CriteriaCondition, createdon,modifiedon,createdby,modifiedby) values
(
	4,
	'40',
    2,
	extract(epoch from now()),
	extract(epoch from now()),
	'system',
	'system'
);

Insert into eg_bmc_Criteria(CriteriaType,CriteriaValue,CriteriaCondition, createdon,modifiedon,createdby,modifiedby) values
(
	4,
	'80',
    2,
	extract(epoch from now()),
	extract(epoch from now()),
	'system',
	'system'
);

Insert into eg_bmc_Criteria(CriteriaType,CriteriaValue,CriteriaCondition, createdon,modifiedon,createdby,modifiedby) values
(
	4,
	'80',
    3,
	extract(epoch from now()),
	extract(epoch from now()),
	'system',
	'system'
);

Insert into eg_bmc_Criteria(CriteriaType,CriteriaValue,CriteriaCondition, createdon,modifiedon,createdby,modifiedby) values
(
	4,
	'70',
    5,
	extract(epoch from now()),
	extract(epoch from now()),
	'system',
	'system'
);

Insert into eg_bmc_Criteria(CriteriaType,CriteriaValue,CriteriaCondition, createdon,modifiedon,createdby,modifiedby) values
(
	6,
	'Ration Card - yellow',
    1,
	extract(epoch from now()),
	extract(epoch from now()),
	'system',
	'system'
);

Insert into eg_bmc_Criteria(CriteriaType,CriteriaValue,CriteriaCondition, createdon,modifiedon,createdby,modifiedby) values
(
	6,
	'Ration Card - orange',
    1,
	extract(epoch from now()),
	extract(epoch from now()),
	'system',
	'system'
);

Insert into eg_bmc_Criteria(CriteriaType,CriteriaValue,CriteriaCondition, createdon,modifiedon,createdby,modifiedby) values
(
	5,
	'1',
    5,
	extract(epoch from now()),
	extract(epoch from now()),
	'system',
	'system'
);





