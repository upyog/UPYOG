package org.egov.ptr.repository.builder;

import org.egov.ptr.config.PetConfiguration;
import org.egov.ptr.models.PetApplicationSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

@Component
public class PetApplicationQueryBuilder {

	@Autowired
	private PetConfiguration petConfig;

	public static final String TENANTIDQUERY = "select distinct(tenantid) from eg_ptr_registration";

	// Updated BASE_PTR_QUERY with new columns from eg_ptr_registration
	private static final String BASE_PTR_QUERY = " SELECT ptr.id as pid, ptr.tenantid as ptenantid, ptr.applicationNumber as papplicationnumber, "
			+ "ptr.applicationType as papplicationtype, ptr.validityDate as pvaliditydate, "
			+ "ptr.status as pstatus, ptr.expireFlag as pexpireflag, ptr.petToken as ppettoken, ptr.previousApplicationNumber as ppreviousapplicationnumber, ptr.petregistrationnumber as ppetregistrationnumber, ptr.propertyId as ppropertyId, "
			+ "ptr.createdby as pcreatedby, ptr.lastmodifiedby as plastmodifiedby, ptr.createdtime as pcreatedtime, ptr.lastmodifiedtime as plastmodifiedtime ,";

	// Updated PET_SELECT_QUERY with new columns from eg_ptr_petdetails
	private static final String PET_SELECT_QUERY = " pet.id as ptid, pet.petName as ptpetname, pet.petType as ptpettype, "
			+ "pet.breedType as ptbreedtype, pet.petAge as ptpetage, pet.petGender as ptpetgender, pet.clinicName as ptclinicname, "
			+ "pet.doctorName as ptdoctorname, pet.lastVaccineDate as ptlastvaccinedate, pet.petDetailsId as ptpetdetails, "
			+ "pet.vaccinationNumber as ptvaccinationnumber, pet.petColor as ptpetcolor, pet.adoptionDate as ptadoptiondate, "
			+ "pet.birthDate as ptbirthdate, pet.identificationMark as ptidentificationmark ,";

	// No changes needed for the ADDRESS_SELECT_QUERY
	private static final String ADDRESS_SELECT_QUERY = " add.id as aid, add.tenantid as atenantid, add.doorno as adoorno, add.latitude as alatitude, "
			+ "add.longitude as alongitude, add.buildingname as abuildingname, add.addressid as aaddressid, add.addressnumber as aaddressnumber, "
			+ "add.type as atype, add.addressline1 as aaddressline1, add.addressline2 as aaddressline2, add.landmark as alandmark, "
			+ "add.street as astreet, add.city as acity, add.locality as alocality, add.pincode as apincode, add.detail as adetail, add.registrationid as aregistrationid ,";

	// Owner SELECT query for the new ptr_owner table
	private static final String OWNER_SELECT_QUERY = " owner.uuid as ouuid, owner.tenantid as otenantid, owner.ptrregistrationid as optrregistrationid, "
			+ "owner.status as ostatus, owner.isprimaryowner as oisprimaryowner, owner.ownertype as oownertype, "
			+ "owner.ownershippercentage as oownershippercentage, owner.institutionid as oinstitutionid, owner.relationship as orelationship, "
			+ "owner.createdby as ocreatedby, owner.createdtime as ocreatedtime, owner.lastmodifiedby as olastmodifiedby, "
			+ "owner.lastmodifiedtime as olastmodifiedtime, owner.additionaldetails as oadditionaldetails ,";

	// Updated DOCUMENTS_SELECT_QUERY to include audit fields
	private static final String DOCUMENTS_SELECT_QUERY = " doc.id as did, doc.tenantid as dtenantid, doc.documentType as documentType, "
			+ "doc.filestoreId as dfilestoreId, doc.documentUid as ddocumentUid, doc.active as dactive, doc.petApplicationId as dpetApplicationId, "
			+ "doc.createdby as dcreatedby, doc.lastmodifiedby as dlastmodifiedby, doc.createdtime as dcreatedtime, doc.lastmodifiedtime as dlastmodifiedtime ";

	// Updated FROM_TABLES query to include owner table
	private static final String FROM_TABLES = " FROM eg_ptr_registration ptr "
			+ "LEFT JOIN eg_ptr_address add ON ptr.id = add.registrationid "
			+ "LEFT JOIN eg_ptr_petdetails pet on ptr.id = pet.petDetailsId "
			+ "LEFT JOIN eg_ptr_applicationdocuments doc on ptr.id = doc.petApplicationId "
			+ "LEFT JOIN eg_ptr_owner owner on ptr.id = owner.ptrregistrationid ";

	private final String ORDERBY_CREATEDTIME = " ORDER BY ptr.createdtime DESC ";

	public String getPetApplicationSearchQuery(PetApplicationSearchCriteria criteria, List<Object> preparedStmtList) {

		// Build subquery to get application IDs with pagination
		StringBuilder subQuery = new StringBuilder(" SELECT ptr.id FROM eg_ptr_registration ptr ");
		List<Object> subQueryParams = new ArrayList<>();

		// Determine if we need to join owner table in subquery
		boolean needsOwnerJoin = !CollectionUtils.isEmpty(criteria.getOwnerUuids());

		// Determine if we need to join pet table in subquery
		boolean needsPetJoin = !ObjectUtils.isEmpty(criteria.getPetType()) || !ObjectUtils.isEmpty(criteria.getBreedType());

		// Add necessary joins to subquery
		if (needsOwnerJoin) {
			subQuery.append(" LEFT JOIN eg_ptr_owner owner ON ptr.id = owner.ptrregistrationid ");
		}
		if (needsPetJoin) {
			subQuery.append(" LEFT JOIN eg_ptr_petdetails pet ON ptr.id = pet.petDetailsId ");
		}

		// Add WHERE clauses to subquery
		if (!ObjectUtils.isEmpty(criteria.getTenantId())) {
			addClauseIfRequired(subQuery, subQueryParams);
			subQuery.append(" ptr.tenantid = ? ");
			subQueryParams.add(criteria.getTenantId());
		}
		if (!ObjectUtils.isEmpty(criteria.getStatus())) {
			addClauseIfRequired(subQuery, subQueryParams);
			subQuery.append(" ptr.status = ? ");
			subQueryParams.add(criteria.getStatus());
		}
		if (!CollectionUtils.isEmpty(criteria.getIds())) {
			addClauseIfRequired(subQuery, subQueryParams);
			subQuery.append(" ptr.id IN ( ").append(createQuery(criteria.getIds())).append(" ) ");
			addToPreparedStatement(subQueryParams, criteria.getIds());
		}
		if (!CollectionUtils.isEmpty(criteria.getApplicationNumber())) {
			addClauseIfRequired(subQuery, subQueryParams);
			subQuery.append(" ptr.applicationnumber IN ( ")
					.append(createQuery(criteria.getApplicationNumber()))
					.append(" ) ");
			addToPreparedStatement(subQueryParams, criteria.getApplicationNumber());
		}
		if (!CollectionUtils.isEmpty(criteria.getPetRegistrationNumber())) {
			addClauseIfRequired(subQuery, subQueryParams);
			subQuery.append(" ptr.petregistrationnumber IN ( ")
					.append(createQuery(criteria.getPetRegistrationNumber()))
					.append(" ) ");
			addToPreparedStatement(subQueryParams, criteria.getPetRegistrationNumber());
		}
		if (!CollectionUtils.isEmpty(criteria.getOwnerUuids())) {
			addClauseIfRequired(subQuery, subQueryParams);
			subQuery.append(" owner.uuid IN ( ").append(createQuery(criteria.getOwnerUuids())).append(" ) ");
			addToPreparedStatement(subQueryParams, criteria.getOwnerUuids());
		}
		if (!ObjectUtils.isEmpty(criteria.getPetType())) {
			addClauseIfRequired(subQuery, subQueryParams);
			subQuery.append(" LOWER(pet.pettype) = LOWER(?) ");
			subQueryParams.add(criteria.getPetType());
		}
		if (!ObjectUtils.isEmpty(criteria.getBreedType())) {
			addClauseIfRequired(subQuery, subQueryParams);
			subQuery.append(" LOWER(pet.breedtype) = LOWER(?) ");
			subQueryParams.add(criteria.getBreedType());
		}
		if (!ObjectUtils.isEmpty(criteria.getFromDate())) {
			addClauseIfRequired(subQuery, subQueryParams);
			subQuery.append(" ptr.createdtime >= CAST(? AS bigint) ");
			subQueryParams.add(criteria.getFromDate());
		}
		if (!ObjectUtils.isEmpty(criteria.getToDate())) {
			addClauseIfRequired(subQuery, subQueryParams);
			subQuery.append(" ptr.createdtime <= CAST(? AS bigint) ");
			subQueryParams.add(criteria.getToDate());
		}

		// Add GROUP BY if we have joins in subquery to avoid duplicates
		if (needsOwnerJoin || needsPetJoin) {
			subQuery.append(" GROUP BY ptr.id, ptr.createdtime ");
		}

		// Add ordering to subquery
		subQuery.append(" ORDER BY ptr.createdtime DESC ");

		// Add pagination to subquery
		int limit = criteria.getLimit() != null ? Math.min(criteria.getLimit(), petConfig.getMaxSearchLimit()) : petConfig.getDefaultLimit();
		int offset = criteria.getOffset() != null ? criteria.getOffset() : petConfig.getDefaultOffset();

		subQuery.append(" LIMIT ? OFFSET ? ");
		subQueryParams.add(limit);
		subQueryParams.add(offset);

		// Now build the main query
		StringBuilder mainQuery = new StringBuilder(BASE_PTR_QUERY);
		mainQuery.append(PET_SELECT_QUERY);
		mainQuery.append(ADDRESS_SELECT_QUERY);
		mainQuery.append(OWNER_SELECT_QUERY);
		mainQuery.append(DOCUMENTS_SELECT_QUERY);
		mainQuery.append(FROM_TABLES);

		// Add WHERE clause with subquery
		mainQuery.append(" WHERE ptr.id IN ( ");
		mainQuery.append(subQuery);
		mainQuery.append(" ) ");

		// Add all subquery parameters to the main prepared statement list
		preparedStmtList.addAll(subQueryParams);

		// Order the final result
		mainQuery.append(ORDERBY_CREATEDTIME);

		return mainQuery.toString();
	}

	private void addClauseIfRequired(StringBuilder query, List<Object> preparedStmtList) {
		if (preparedStmtList.isEmpty()) {
			query.append(" WHERE ");
		} else {
			query.append(" AND ");
		}
	}

	private String createQuery(List<String> ids) {
		StringBuilder builder = new StringBuilder();
		int length = ids.size();
		for (int i = 0; i < length; i++) {
			builder.append(" ?");
			if (i != length - 1)
				builder.append(",");
		}
		return builder.toString();
	}

	private void addToPreparedStatement(List<Object> preparedStmtList, List<String> ids) {
		ids.forEach(id -> {
			preparedStmtList.add(id);
		});
	}
}