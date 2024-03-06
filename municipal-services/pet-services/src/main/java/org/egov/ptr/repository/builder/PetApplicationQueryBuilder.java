package org.egov.ptr.repository.builder;

import org.egov.ptr.models.PetApplicationSearchCriteria;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Component
public class PetApplicationQueryBuilder {

    private static final String BASE_PTR_QUERY = " SELECT ptr.id as pid, ptr.tenantid as ptenantid, ptr.applicationnumber as papplicationnumber, ptr.applicantname as papplicantname, ptr.fathername as pfathername, ptr.mobileNumber as pmobileNumber, ptr.emailId as pemailId, ptr.createdby as pcreatedby, ptr.lastmodifiedby as plastmodifiedby, ptr.createdtime as pcreatedtime, ptr.lastmodifiedtime as plastmodifiedtime, ";

    private static final String PET_SELECT_QUERY = "  pet.id as ptid, pet.petName as ptpetname, pet.petType as ptpetType, pet.breedType as ptbreedtype, pet.petAge as ptpetage, pet.petGender as ptpetgender, pet.clinicName as ptclinicname, pet.doctorName as ptdoctorname, pet.lastVaccineDate as ptlastvaccinedate, pet.petDetailsId as ptpetdetails, pet.vaccinationNumber as ptvaccinationNumber,";
    
    private static final String ADDRESS_SELECT_QUERY = " add.id as aid, add.tenantid as atenantid, add.doorno as adoorno, add.latitude as alatitude, add.longitude as alongitude, add.buildingname as abuildingname, add.addressid as aaddressid, add.addressnumber as aaddressnumber, add.type as atype, add.addressline1 as aaddressline1, add.addressline2 as aaddressline2, add.landmark as alandmark, add.street as astreet, add.city as acity, add.locality as alocality, add.pincode as apincode, add.detail as adetail, add.registrationid as aregistrationid, ";

    private static final String DOCUMENTS_SELECT_QUERY = " doc.id as did, doc.tenantid as dtenantid, doc.documentType as documentType, doc.filestoreId as dfilestoreId, doc.documentUid as ddocumentUid, doc.active as dactive, doc.petApplicationId as dpetApplicationId ";
    
    private static final String FROM_TABLES = " FROM eg_ptr_registration ptr LEFT JOIN eg_ptr_address add ON ptr.id = add.registrationid LEFT JOIN eg_ptr_petdetails pet on ptr.id = pet.petDetailsId LEFT JOIN eg_ptr_applicationdocuments doc on ptr.id = doc.petApplicationId ";

    private final String ORDERBY_CREATEDTIME = " ORDER BY ptr.createdtime DESC ";
   
    
    public String getPetApplicationSearchQuery(PetApplicationSearchCriteria criteria, List<Object> preparedStmtList){
        StringBuilder query = new StringBuilder(BASE_PTR_QUERY);
        query.append(PET_SELECT_QUERY);
        query.append(ADDRESS_SELECT_QUERY);
        query.append(DOCUMENTS_SELECT_QUERY);
        query.append(FROM_TABLES);

        if(!ObjectUtils.isEmpty(criteria.getTenantId())){
            addClauseIfRequired(query, preparedStmtList);
            query.append(" ptr.tenantid = ? ");
            preparedStmtList.add(criteria.getTenantId());
        }
        if(!ObjectUtils.isEmpty(criteria.getStatus())){
            addClauseIfRequired(query, preparedStmtList);
            query.append(" ptr.status = ? ");
            preparedStmtList.add(criteria.getStatus());
        }
        if(!CollectionUtils.isEmpty(criteria.getIds())){
            addClauseIfRequired(query, preparedStmtList);
            query.append(" ptr.id IN ( ").append(createQuery(criteria.getIds())).append(" ) ");
            addToPreparedStatement(preparedStmtList, criteria.getIds());
        }
        if(!ObjectUtils.isEmpty(criteria.getApplicationNumber())){
            addClauseIfRequired(query, preparedStmtList);
            query.append(" ptr.applicationnumber = ? ");
            preparedStmtList.add(criteria.getApplicationNumber());
        }
        if(!ObjectUtils.isEmpty(criteria.getMobileNumber())){
            addClauseIfRequired(query, preparedStmtList);
            query.append(" ptr.mobilenumber = ? ");
            preparedStmtList.add(criteria.getMobileNumber());
        }
        if(!ObjectUtils.isEmpty(criteria.getPetType())){
            addClauseIfRequired(query, preparedStmtList);
            query.append(" LOWER(pet.pettype) = LOWER(?) ");
            preparedStmtList.add(criteria.getPetType());
        }
        if(!ObjectUtils.isEmpty(criteria.getBreedType())){
            addClauseIfRequired(query, preparedStmtList);
            query.append(" LOWER(pet.breedtype) = LOWER(?) ");
            preparedStmtList.add(criteria.getBreedType());
        }
        if(!ObjectUtils.isEmpty(criteria.getFromDate())){
            addClauseIfRequired(query, preparedStmtList);
            //query.append(" ptr.createdtime >= ? ");
            query.append(" ptr.createdtime >= CAST(? AS bigint) ");
            preparedStmtList.add(criteria.getFromDate());
        }
        if(!ObjectUtils.isEmpty(criteria.getToDate())){
            addClauseIfRequired(query, preparedStmtList);
           // query.append(" ptr.createdtime <= ? ");
            query.append(" ptr.createdtime <= CAST(? AS bigint) ");
            preparedStmtList.add(criteria.getToDate());
        }
        // order pet registration applications based on their createdtime in latest first manner
        query.append(ORDERBY_CREATEDTIME);

        return query.toString();
    }

    private void addClauseIfRequired(StringBuilder query, List<Object> preparedStmtList){
        if(preparedStmtList.isEmpty()){
            query.append(" WHERE ");
        }else{
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