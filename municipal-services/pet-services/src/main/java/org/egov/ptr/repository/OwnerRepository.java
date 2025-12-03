package org.egov.ptr.repository;

import lombok.extern.slf4j.Slf4j;
import org.egov.ptr.models.Owner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;


@Slf4j
@Repository
public class OwnerRepository {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	private static final String INSERT_OWNER_QUERY = "INSERT INTO eg_ptr_owner " +
			"(uuid, tenantid, ptrregistrationid, status, isprimaryowner, ownertype, " +
			"ownershippercentage, institutionid, relationship, createdby, createdtime, " +
			"lastmodifiedby, lastmodifiedtime, additionaldetails) " +
			"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

	private static final String UPDATE_OWNER_QUERY = "UPDATE eg_ptr_owner SET " +
			"status = ?, isprimaryowner = ?, ownertype = ?, ownershippercentage = ?, " +
			"institutionid = ?, relationship = ?, lastmodifiedby = ?, lastmodifiedtime = ?, " +
			"additionaldetails = ? WHERE uuid = ? AND ptrregistrationid = ?";

	private static final String DELETE_OWNER_QUERY = "DELETE FROM eg_ptr_owner WHERE ptrregistrationid = ?";

	public void saveOwner(Owner owner, String ptrRegistrationId, String tenantId, String createdBy, Long createdTime) {
		try {
			jdbcTemplate.update(INSERT_OWNER_QUERY,
					owner.getUuid(), // uuid
					tenantId,
					ptrRegistrationId,
					owner.getStatus(),
					owner.getIsPrimaryOwner(),
					owner.getOwnerType(),
					owner.getOwnershipPercentage(),
					owner.getInstitutionId(),
					owner.getRelationship(),
					createdBy,
					createdTime,
					createdBy,
					createdTime,
					null // additionaldetails - can be extended later
			);
			log.info("Owner saved successfully for registration ID: {}", ptrRegistrationId);
		} catch (Exception e) {
			log.error("Error saving owner for registration ID: {}", ptrRegistrationId, e);
			throw new RuntimeException("Failed to save owner", e);
		}
	}

	public void updateOwner(Owner owner, String ptrRegistrationId, String lastModifiedBy, Long lastModifiedTime) {
		try {
			jdbcTemplate.update(UPDATE_OWNER_QUERY,
					owner.getStatus(),
					owner.getIsPrimaryOwner(),
					owner.getOwnerType(),
					owner.getOwnershipPercentage(),
					owner.getInstitutionId(),
					owner.getRelationship(),
					lastModifiedBy,
					lastModifiedTime,
					null, // additionaldetails - can be extended later
					owner.getUuid(), // uuid
					ptrRegistrationId
			);
			log.info("Owner updated successfully for registration ID: {}", ptrRegistrationId);
		} catch (Exception e) {
			log.error("Error updating owner for registration ID: {}", ptrRegistrationId, e);
			throw new RuntimeException("Failed to update owner", e);
		}
	}

	public void deleteOwnersByRegistrationId(String ptrRegistrationId) {
		try {
			jdbcTemplate.update(DELETE_OWNER_QUERY, ptrRegistrationId);
			log.info("Owners deleted successfully for registration ID: {}", ptrRegistrationId);
		} catch (Exception e) {
			log.error("Error deleting owners for registration ID: {}", ptrRegistrationId, e);
			throw new RuntimeException("Failed to delete owners", e);
		}
	}
}
