package org.egov.repository.querybuilder;

import java.util.List;

import org.egov.web.models.WMSContractAgreementApplicationSearchCriteria;
import org.egov.web.models.WMSContractorApplicationSearchCriteria;
import org.egov.web.models.WMSWorkApplicationSearchCriteria;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public class WMSContractAgreementApplicationQueryBuilder {

	
	private static final String BASE_CONAGRMT_QUERY = " SELECT conagrmt.agreement_no as aAgreementNo, conagrmt.agreement_name as aAgreementName, conagrmt.agreement_date as aAgreementDate, conagrmt.department_name as aDepartmentName, conagrmt.loa_no as aLoaNo, conagrmt.resolution_no as aResolutionNo, conagrmt.resolution_date as aResolutionDate, conagrmt.tender_no as aTenderNo, conagrmt.tender_date as aTenderDate, conagrmt.agreement_type as aAgreementType, conagrmt.defect_liability_period as aDefectLiabilityPeriod, conagrmt.contract_period as aContractPeriod, conagrmt.agreement_amount as aAgreementAmount, conagrmt.payment_type as aPaymentType,conagrmt.deposit_type as aDepositType,conagrmt.deposit_amount as aDepositAmount,conagrmt.work_description as aWorkDescription,conagrmt.account_no as aAccountNo,conagrmt.particulars as aParticulars,conagrmt.valid_from_date as aValidFromDate,conagrmt.valid_till_date as aValidTillDate,conagrmt.bank_branch_ifsc_code as aBankBranchIfscCode,conagrmt.payment_mode as aPaymentMode,conagrmt.designation as aDesignation,conagrmt.employee_name as aEmployeeName,conagrmt.witness_name as aWitnessName,conagrmt.address as aAddress,conagrmt.uid as aUid,conagrmt.vendor_type as aVendorType,conagrmt.vendor_name as aVendorName,conagrmt.represented_by as aRepresentedBy,conagrmt.primary_party as aPrimaryParty,conagrmt.sr_no as aSrNo,conagrmt.terms_and_conditions as aTermsAndConditions,conagrmt.document_description as aDocumentDescription,conagrmt.upload_document as aUploadDocument, conagrmt.createdby as aCreatedBy, conagrmt.lastmodifiedby as aLastmodifiedby, conagrmt.createdtime as aCreatedtime, conagrmt.lastmodifiedtime as aLastmodifiedtime ";

    //private static final String ADDRESS_SELECT_QUERY = " add.id as aid, add.tenantid as atenantid, add.doorno as adoorno, add.latitude as alatitude, add.longitude as alongitude, add.buildingname as abuildingname, add.addressid as aaddressid, add.addressnumber as aaddressnumber, add.type as atype, add.addressline1 as aaddressline1, add.addressline2 as aaddressline2, add.landmark as alandmark, add.street as astreet, add.city as acity, add.locality as alocality, add.pincode as apincode, add.detail as adetail, add.registrationid as aregistrationid ";

    //private static final String FROM_TABLES = " FROM eg_bt_registration btr LEFT JOIN eg_bt_address add ON btr.id = add.registrationid ";
    private static final String FROM_TABLES = " FROM contract_agreement conagrmt";

    private final String ORDERBY_CREATEDTIME = " ORDER BY conagrmt.agreement_date DESC ";

    public String getContractAgreementApplicationSearchQuery(WMSContractAgreementApplicationSearchCriteria criteria, List<Object> preparedStmtList){
        StringBuilder query = new StringBuilder(BASE_CONAGRMT_QUERY);
       // query.append(ADDRESS_SELECT_QUERY);
        query.append(FROM_TABLES);

        if(!ObjectUtils.isEmpty(criteria.getAgreementNo())){
        	 addClauseIfRequired(query, preparedStmtList);
             query.append(" conagrmt.agreement_no IN ( ").append(createQuery(criteria.getAgreementNo())).append(" ) ");
             addToPreparedStatement(preparedStmtList, criteria.getAgreementNo());
        }

        // order birth registration applications based on their createdtime in latest first manner
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

    private String createQuery(List<String> list) {
        StringBuilder builder = new StringBuilder();
        int length = list.size();
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
