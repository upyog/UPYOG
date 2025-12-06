-- Plant user mapping schema V1

-- Plant User Map
CREATE TABLE IF NOT EXISTS eq_plant_user_map
(
    id                character varying(64),
    tenantId          character varying(64),
    plantCode         character varying(255),
    individualId      character varying(255),
    additionalDetails json,
    isActive          boolean,
    createdBy         character varying(64),
    lastModifiedBy    character varying(64),
    createdTime       bigint,
    lastModifiedTime  bigint,
    CONSTRAINT pk_eq_plant_user_map PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS index_tenantId_eq_plant_user_map ON eq_plant_user_map (tenantId);

CREATE INDEX IF NOT EXISTS index_plantCode_eq_plant_user_map ON eq_plant_user_map (plantCode);

CREATE INDEX IF NOT EXISTS index_individualId_eq_plant_user_map ON eq_plant_user_map (individualId);


-- Plant User Map Audit Log
CREATE TABLE IF NOT EXISTS eq_plant_user_map_auditlog
(
    id                character varying(64),
    tenantId          character varying(64),
    plantCode         character varying(255),
    individualId      character varying(255),
    additionalDetails json,
    isActive          boolean,
    createdBy         character varying(64),
    lastModifiedBy    character varying(64),
    createdTime       bigint,
    lastModifiedTime  bigint
);

CREATE INDEX IF NOT EXISTS index_id_eg_pqm_tests_auditlog ON eq_plant_user_map_auditlog (id);