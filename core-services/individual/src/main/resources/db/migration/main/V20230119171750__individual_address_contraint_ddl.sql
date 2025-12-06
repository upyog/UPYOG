ALTER TABLE INDIVIDUAL_ADDRESS
ADD CONSTRAINT uk_individual_address_mapping UNIQUE(individualId, addressId);