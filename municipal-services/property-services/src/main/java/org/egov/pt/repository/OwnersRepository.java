
package org.egov.pt.repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.egov.pt.models.OwnerInfo;
import org.egov.pt.repository.builder.PropertyQueryBuilder;
import org.egov.pt.repository.rowmapper.OwnerRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class OwnersRepository {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private PropertyQueryBuilder queryBuilder;

	@Autowired
	private OwnerRowMapper ownerRowMapper;

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	private static final String UPDATE_OWNER_BY_PROPERTYID_AND_USERID = "UPDATE eg_pt_owner SET \"name\" = :name,"
			+ " mobile_number = :mobileNumber WHERE userid = :userId AND propertyid = :propertyId ";

	public List<OwnerInfo> getAllPropertyOwners() {
		List<Object> preparedStmtList = new ArrayList<>();
		String query = queryBuilder.getOwnerSearchQuery(preparedStmtList);

		return jdbcTemplate.query(query, preparedStmtList.toArray(), ownerRowMapper);
	}

	public void updateOwners(OwnerInfo owner) {
		Map<String, Object> ownerInputs = new HashMap<>();
		ownerInputs.put("userId", owner.getUuid());
		ownerInputs.put("propertyId", owner.getPropertyId());

		ownerInputs.put("name", owner.getName());
		ownerInputs.put("mobileNumber", owner.getMobileNumber());

		namedParameterJdbcTemplate.update(UPDATE_OWNER_BY_PROPERTYID_AND_USERID, ownerInputs);
	}

	public void updateOwnersBatch(List<OwnerInfo> owners) {
		if (owners == null || owners.isEmpty()) {
			log.info("No owners to update in batch.");
			return;
		}

		// Prepare the list of parameter maps for batch update
		List<Map<String, Object>> batchParams = new ArrayList<>();

		for (OwnerInfo owner : owners) {
			Map<String, Object> ownerParams = buildOwnerParams(owner);
			batchParams.add(ownerParams);
		}

		try {
			// Execute the batch update
			int[] updateCounts = namedParameterJdbcTemplate.batchUpdate(UPDATE_OWNER_BY_PROPERTYID_AND_USERID,
					batchParams.toArray(new Map[0]));

			// Log the results of the batch update
			int totalUpdated = Arrays.stream(updateCounts).sum();
			log.info("Batch update complete. Total rows updated: " + totalUpdated);

		} catch (Exception e) {
			log.error("Error during batch update of owners.", e);
			// Handle the exception appropriately
		}
	}

	private Map<String, Object> buildOwnerParams(OwnerInfo owner) {
		Map<String, Object> params = new HashMap<>();
		params.put("userId", owner.getUuid());
		params.put("propertyId", owner.getPropertyId());
		params.put("name", owner.getName());
		params.put("mobileNumber", owner.getMobileNumber());
		return params;
	}

}
