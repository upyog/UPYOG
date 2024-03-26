package org.egov.repository.querybuilder;

import java.util.List;

import org.egov.web.models.WMSContractAgreementApplicationSearchCriteria;
import org.egov.web.models.WMSContractorApplicationSearchCriteria;
import org.egov.web.models.WMSWorkApplicationSearchCriteria;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public class WMSContractAgreementApplicationQueryBuilder {

	
	private static final String BASE_CONAGRMT_QUERY = " SELECT sd.sdpg_id as aSdpgId,agr.agr_id as aAgrId,agr.agreement_no as aAgreementNo, agr.agreement_name as aAgreementName, agr.agreement_date as aAgreementDate, agr.department_name_ai as aDepartmentNameAi,part.party1_id as aParty1Id, part.department_name_party1 as aDepartmentNameParty1, agr.loa_no as aLoaNo, agr.resolution_no as aResolutionNo, agr.resolution_date as aResolutionDate, agr.tender_no as aTenderNo, agr.tender_date as aTenderDate, agr.agreement_type as aAgreementType, agr.defect_liability_period as aDefectLiabilityPeriod, agr.contract_period as aContractPeriod, agr.agreement_amount as aAgreementAmount, agr.payment_type as aPaymentType,sd.deposit_type as aDepositType,sd.deposit_amount as aDepositAmount,sd.account_no as aAccountNo,sd.particulars as aParticulars,sd.valid_from_date as aValidFromDate,sd.valid_till_date as aValidTillDate,sd.bank_branch_ifsc_code as aBankBranchIfscCode,sd.payment_mode as aPaymentMode,part.designation as aDesignation,part.employee_name as aEmployeeName,part.witness_name_p1 as aWitnessNameP1,part.address_p1 as aAddressP1,part.uid_p1 as aUidP1,pw.witness_name_p2 as aWitnessNameP2,pw.address_p2 as aAddressP2,pw.uid_p2 as aUidP2,pw.pw_id as aPwId,con.vendor_type as aVendorType,con.vendor_name as aVendorName,con.represented_by as aRepresentedBy,con.primary_party as aPrimaryParty,con.con_id as aConId,tnc.sr_no as aSrNo,tnc.tnc_id as aTncId,tnc.terms_and_conditions as aTermsAndConditions,ad.document_description as aDocumentDescription,ad.upload_document as aUploadDocument,ad.ad_id as aAdId,sd.sdpg_id as aSdpgId,sd.deposit_type as aDepositType,sd.deposit_amount as aDepositAmount,sd.account_no as aAccountNo,sd.particulars as aParticulars,sd.valid_from_date as aValidFromDate,sd.valid_till_date as aValidTillDate,sd.bank_branch_ifsc_code as aBankBranchIfsc_code,sd.payment_mode as PaymentMode ,conagrmt.createdby as aCreatedBy, conagrmt.lastmodifiedby as aLastmodifiedby, conagrmt.createdtime as aCreatedtime, conagrmt.lastmodifiedtime as aLastmodifiedtime ";

    //private static final String ADDRESS_SELECT_QUERY = " add.id as aid, add.tenantid as atenantid, add.doorno as adoorno, add.latitude as alatitude, add.longitude as alongitude, add.buildingname as abuildingname, add.addressid as aaddressid, add.addressnumber as aaddressnumber, add.type as atype, add.addressline1 as aaddressline1, add.addressline2 as aaddressline2, add.landmark as alandmark, add.street as astreet, add.city as acity, add.locality as alocality, add.pincode as apincode, add.detail as adetail, add.registrationid as aregistrationid ";

    //private static final String FROM_TABLES = " FROM eg_bt_registration btr LEFT JOIN eg_bt_address add ON btr.id = add.registrationid ";
    private static final String FROM_TABLES = " FROM contract_agreement conagrmt INNER JOIN  agreement agr ON conagrmt.agreement_no=agr.agreement_no INNER JOIN party1details part ON part.agreement_no=conagrmt.agreement_no INNER JOIN sdpgbgdetails sd ON sd.agreement_no=conagrmt.agreement_no INNER JOIN termsnconditions tnc ON tnc.agreement_no=conagrmt.agreement_no INNER JOIN agreementdocuments ad ON ad.agreement_no=conagrmt.agreement_no INNER JOIN party2witness pw ON pw.agreement_no=conagrmt.agreement_no INNER JOIN contractors con ON con.agreement_no=conagrmt.agreement_no";

    private final String ORDERBY_CREATEDTIME = " ORDER BY agr.agreement_date DESC ";

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
