CREATE TABLE IF NOT EXISTS INDIVIDUAL_IDENTIFIER
(
    id               character varying(64),
    individualId     character varying(64),
    identifierType   character varying(64),
    identifierId     character varying(64),
    createdBy        character varying(64),
    lastModifiedBy   character varying(64),
    createdTime      bigint,
    lastModifiedTime bigint,
    rowVersion       bigint,
    isDeleted        boolean,
    CONSTRAINT uk_individual_identifier_id PRIMARY KEY (id)
);