package org.egov.pt.calculator.repository.querybuilder;

import org.springframework.stereotype.Service;

@Service
public class AdoptionQueryBuilder {
	

//	public static final String WHATSAAP_ADOPTION_REPORT_QUERY ="select json_build_object ('businessservice','PT', 'id', pt.propertyid, 'tenantid', pt.tenantid,'consumercode',pt.propertyid,'usagecategory',pt.usagecategory, 'propertytype',pt.propertytype,'ownershipcategory',pt.ownershipcategory, "
//			+ "'userdetails',(SELECT json_agg(userdetails) FROM (SELECT usr.uuid \"user\", (SELECT CASE WHEN usr.gender = 1 THEN 'FEMALE' WHEN usr.gender = 2 THEN 'MALE' ELSE 'OTHERS' END ) \"gender\" FROM eg_pt_owner ptowner INNER JOIN eg_user usr ON ptowner.propertyid=pt.id and ptowner.status='ACTIVE' AND ptowner.userid=usr.uuid ) userdetails), "
//			+ "'assessmentdetails',(SELECT json_agg(assessmentdetails) FROM (SELECT job.assessmentnumber \"assessmentnumber\", (To_timestamp(job.createdtime/1000) at time Zone 'Asia/Kolkata')::date \"sms_sentdate\",job.tenantid \"tenantid\", job.financialyear \"sms_assessmentyear\", (SELECT CASE WHEN (select isvalid from decrypted_user u where u.uuid=(select userid from eg_pt_owner owner where owner.propertyid=pt.id and owner.status='ACTIVE' limit 1 ) limit 1) is null then false else true end) \"isValidsms\" FROM eg_pt_assessment_job job WHERE job.status!='FAILED' and job.propertyid=pt.propertyid) assessmentdetails), "
//			+ "'paymentdetails', (SELECT json_agg(paymentdtls) FROM (select p.tenantid \"tenantid\",pd.businessservice \"businessservice\",p.paymentmode \"paymentmode\", (To_timestamp(pd.receiptdate/1000) at time Zone 'Asia/Kolkata')::date \"paymentdate\",pd.amountpaid \"amountpaid\", (SELECT CASE WHEN (p.additionaldetails->>'isWhatsapp')::boolean is null then false else true END) \"isWhatsappPayment\" FROM egcl_bill bill INNER JOIN egcl_paymentdetail pd ON bill.consumercode=pt.propertyid AND pd.businessservice='PT' and pd.billid = bill.id INNER JOIN egcl_payment p ON p.paymentstatus!='CANCELLED' and p.id=pd.paymentid and (To_timestamp(pd.receiptdate/1000) at time Zone 'Asia/Kolkata') between (To_timestamp((select min(ast_job.createdtime) from eg_pt_assessment_job ast_job where ast_job.propertyid=pt.propertyid and ast_job.status!='FAILED')/1000) at time Zone 'Asia/Kolkata') and ((now())::date || ' 23:59:59')::timestamp order by pd.receiptdate) paymentdtls) ) from eg_pt_property pt WHERE pt.status='ACTIVE' and pt.propertyid IN (:propertyid) GROUP BY pt.tenantid, pt.propertyid, pt.usagecategory, pt.propertytype, pt.ownershipcategory,pt.id ;";

	public static final String PT_ASSESSMENT_DATA_QUERY ="select json_build_object ('businessservice', 'PT','type','assessment','id', job.id, "
			+ "'tenantid', pt.tenantid,'consumercode',pt.propertyid,'usagecategory',pt.usagecategory, 'propertytype',pt.propertytype,"
			+ "'ownershipcategory',pt.ownershipcategory, 'sms_assessmentyear',job.financialyear, "
			+ "'userdetails',(SELECT json_agg(userdetails) FROM (SELECT usr.uuid \"user\", (SELECT CASE WHEN usr.gender = 1 THEN 'FEMALE' WHEN usr.gender = 2 THEN 'MALE' ELSE 'OTHERS' END ) \"gender\" FROM eg_pt_owner ptowner "
			+ " INNER JOIN eg_user usr ON ptowner.propertyid=pt.id and ptowner.status='ACTIVE' AND ptowner.userid=usr.uuid ) userdetails),"
			+ "'assessmentnumber',job.assessmentnumber,'sms_sentdate',(To_timestamp(job.createdtime/1000) at time Zone 'Asia/Kolkata')::date,"  
			+ "'isValidsms',(SELECT CASE WHEN (select isvalid from decrypted_user u where u.uuid=(select userid from eg_pt_owner owner where owner.propertyid=pt.id and owner.status='ACTIVE' limit 1 ) limit 1) is null then false else true end)) " + 
			"from eg_pt_property pt,eg_pt_assessment_job job WHERE job.propertyid=pt.propertyid and pt.status='ACTIVE' and job.status!='FAILED' and pt.propertyid IN (:propertyid) ";
	
	public static final String PT_ASSESSMENT_DATE_FILTER =" and (To_timestamp(job.createdtime/1000) at time Zone 'Asia/Kolkata')::date between ((now() - INTERVAL :days)::date || ' 00:00:00')::timestamp and ((now() - INTERVAL '1 DAY')::date || ' 23:59:59')::timestamp";

	public static final String PT_PAYMENT_DATA_QUERY="select json_build_object ('isWhatsappPayment',(SELECT CASE WHEN (p.additionaldetails->>'isWhatsapp')::boolean is true then true else false END),"
			+ "'amountpaid',pd.amountpaid, 'paymentdate',(To_timestamp(pd.receiptdate/1000) at time Zone 'Asia/Kolkata')::date,'paymentmode',p.paymentmode,"
			+ "'businessservice', 'PT','type','payment','id', p.id, 'tenantid', pt.tenantid,'consumercode',pt.propertyid,'usagecategory',pt.usagecategory, "
			+ "'propertytype',pt.propertytype,'ownershipcategory',pt.ownershipcategory, 'userdetails',(SELECT json_agg(userdetails) FROM (SELECT usr.uuid \"user\", "
			+ "(SELECT CASE WHEN usr.gender = 1 THEN 'FEMALE' WHEN usr.gender = 2 THEN 'MALE' ELSE 'OTHERS' END ) \"gender\" FROM eg_pt_owner ptowner "
			+ "INNER JOIN eg_user usr ON ptowner.propertyid=pt.id and ptowner.status='ACTIVE' AND ptowner.userid=usr.uuid ) userdetails))\n" + 
			"from eg_pt_property pt,egcl_bill bill,egcl_paymentdetail pd,egcl_payment p WHERE pt.propertyid IN (:propertyid) and  bill.consumercode=pt.propertyid "
			+ " AND pd.billid = bill.id AND pd.businessservice='PT' AND p.paymentstatus!='CANCELLED' and p.id=pd.paymentid and (To_timestamp(pd.receiptdate/1000) at time Zone 'Asia/Kolkata') "
			+ " between ";
	
			
	public static final String PT_PAYMENT_TOTAL_DATE_FILTER=" (To_timestamp((select min(ast_job.createdtime) from eg_pt_assessment_job ast_job where ast_job.status!='FAILED')/1000) at time Zone 'Asia/Kolkata') and ((now())::date || ' 23:59:59')::timestamp order by pd.receiptdate;";
	
	public static final String PT_PAYMENT_DATE_FILTER=" ((now() - INTERVAL :days )::date || ' 00:00:00')::timestamp and ((now() - INTERVAL '1 DAY')::date || ' 23:59:59')::timestamp";
	
	public static final String TOTAL_PROPERTIES_IDS_QUERY ="SELECT propertyid FROM eg_pt_property where status=:status";

	public static final String ASSESS_JOB_PROPERTIES_IDS_QUERY ="SELECT distinct propertyid FROM ( SELECT distinct propertyid FROM eg_pt_assessment_job "
			+ "where status !='FAILED' and (To_timestamp(createdtime/1000) at time Zone 'Asia/Kolkata')::date "
			+ "between ((now() - INTERVAL :days)::date || ' 00:00:00')::timestamp and ((now() - INTERVAL '1 DAY')::date || ' 23:59:59')::timestamp "
			+ "UNION SELECT distinct bill.consumercode as propertyid FROM egcl_bill bill INNER JOIN egcl_paymentdetail pd ON pd.businessservice='PT' "
			+ "and pd.billid = bill.id INNER JOIN egcl_payment p ON p.paymentstatus!='CANCELLED' and p.id=pd.paymentid "
			+ "and (To_timestamp(pd.receiptdate/1000) at time Zone 'Asia/Kolkata')::date between ((now() - INTERVAL :days )::date || ' 00:00:00')::timestamp and ((now() - INTERVAL '1 DAY')::date || ' 23:59:59')::timestamp) as properties";

}
