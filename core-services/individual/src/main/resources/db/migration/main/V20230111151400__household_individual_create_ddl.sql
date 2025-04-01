CREATE TABLE IF NOT EXISTS HOUSEHOLD_INDIVIDUAL
(
    individualId                character varying(64),
    individualClientReferenceId character varying(64),
    householdId                 character varying(64),
    householdClientReferenceId  character varying(64),
    isHeadOfHousehold           boolean,
    createdBy                   character varying(64),
    lastModifiedBy              character varying(64),
    createdTime                 bigint,
    lastModifiedTime            bigint,
    rowVersion                  bigint,
    isDeleted                   boolean
);