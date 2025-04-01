CREATE TABLE IF NOT EXISTS INDIVIDUAL_SKILL
(
    id               character varying(64),
    individualId     character varying(64),
    type             character varying(64),
    level            character varying(64),
    experience       character varying(64),
    createdBy        character varying(64),
    lastModifiedBy   character varying(64),
    createdTime      bigint,
    lastModifiedTime bigint,
    isDeleted        boolean,
    CONSTRAINT uk_individual_skill_id PRIMARY KEY (id)
);