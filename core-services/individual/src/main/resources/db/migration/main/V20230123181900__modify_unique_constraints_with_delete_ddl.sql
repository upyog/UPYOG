ALTER TABLE INDIVIDUAL_IDENTIFIER DROP CONSTRAINT IF EXISTS uk_individual_identifier_mapping;
ALTER TABLE INDIVIDUAL_ADDRESS DROP CONSTRAINT IF EXISTS uk_individual_address_mapping;
ALTER TABLE INDIVIDUAL_IDENTIFIER
    ADD CONSTRAINT uk_individual_identifier_mapping UNIQUE(individualId, identifierType, isdeleted);
ALTER TABLE INDIVIDUAL_ADDRESS ADD CONSTRAINT uk_individual_address_mapping UNIQUE(individualId, addressId, type, isdeleted);