CREATE INDEX IF NOT EXISTS idx_individual_clientReferenceId ON INDIVIDUAL (clientReferenceId);
CREATE INDEX IF NOT EXISTS idx_individual_givenName ON INDIVIDUAL (givenName);
CREATE INDEX IF NOT EXISTS idx_individual_familyName ON INDIVIDUAL (familyName);
CREATE INDEX IF NOT EXISTS idx_individual_otherNames ON INDIVIDUAL (otherNames);
CREATE INDEX IF NOT EXISTS idx_individual_dateOfBirth ON INDIVIDUAL (dateOfBirth);
CREATE INDEX IF NOT EXISTS idx_individual_gender ON INDIVIDUAL (gender);

CREATE INDEX IF NOT EXISTS idx_localityCode ON ADDRESS (localityCode);

CREATE INDEX IF NOT EXISTS idx_individual_identifier_individualId ON INDIVIDUAL_IDENTIFIER (individualId);
CREATE INDEX IF NOT EXISTS idx_individual_identifier_identifierType ON INDIVIDUAL_IDENTIFIER (identifierType);
CREATE INDEX IF NOT EXISTS idx_individual_identifier_identifierId ON INDIVIDUAL_IDENTIFIER (identifierId);
CREATE INDEX IF NOT EXISTS idx_individual_identifier_isDeleted ON INDIVIDUAL_IDENTIFIER (isDeleted);
CREATE INDEX IF NOT EXISTS idx_individual_identifier_createdBy ON INDIVIDUAL_IDENTIFIER (createdBy);
CREATE INDEX IF NOT EXISTS idx_individual_identifier_lastModifiedBy ON INDIVIDUAL_IDENTIFIER (lastModifiedBy);
CREATE INDEX IF NOT EXISTS idx_individual_identifier_createdTime ON INDIVIDUAL_IDENTIFIER (createdTime);
CREATE INDEX IF NOT EXISTS idx_individual_identifier_lastModifiedTime ON INDIVIDUAL_IDENTIFIER (lastModifiedTime);