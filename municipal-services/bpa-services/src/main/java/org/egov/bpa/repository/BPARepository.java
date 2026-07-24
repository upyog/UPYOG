package org.egov.bpa.repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.time.LocalDate;
import java.time.ZoneId;
import org.egov.bpa.config.BPAConfiguration;
import org.egov.bpa.producer.Producer;
import org.egov.bpa.repository.querybuilder.BPAQueryBuilder;
import org.egov.bpa.repository.rowmapper.BPARowMapper;
import org.egov.bpa.repository.rowmapper.BPASearchDataRowMapper;
import org.egov.bpa.service.SwsServiceV2;
import org.egov.bpa.web.model.BCategoryRequest;
import org.egov.bpa.web.model.BPA;
import org.egov.bpa.web.model.BPARequest;
import org.egov.bpa.web.model.BPASearchCriteria;
import org.egov.bpa.web.model.BSCategoryRequest;
import org.egov.bpa.web.model.NdbResponseInfoWrapper;
import org.egov.bpa.web.model.PayTpRateRequest;
import org.egov.bpa.web.model.PayTypeFeeDetailRequest;
import org.egov.bpa.web.model.PayTypeRequest;
import org.egov.bpa.web.model.ProposalTypeRequest;
import org.egov.bpa.web.model.SlabMasterRequest;
import org.egov.common.contract.request.RequestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.egov.bpa.web.model.ProposalDetails;
import org.egov.bpa.web.model.LabourDepartmentDetails;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import java.util.Collections;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class BPARepository {

	@Autowired
	private BPAConfiguration config;

	@Autowired
	private Producer producer;

	@Autowired
	private BPAQueryBuilder queryBuilder;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private BPARowMapper rowMapper;

	@Autowired
	private BPASearchDataRowMapper searchDataRowMapper;

	@Autowired
	SwsServiceV2 swsService;

	/**
	 * Pushes the request on save topic through kafka
	 *
	 * @param bpaRequest The bpa create request
	 */
	public void save(BPARequest bpaRequest) {
		producer.push(config.getSaveTopic(), bpaRequest);
		if (bpaRequest.getBPA().isSwsApplication()) {
			swsService.updateStatusToSwsIntiatedApplication(bpaRequest);
		}
	}

	public void saveDashboardPushRecord(NdbResponseInfoWrapper ndbResponseInfoWrapper) {
		producer.push(config.getPushRecordTopic(), ndbResponseInfoWrapper);
	}

	/**
	 * pushes the request on update or workflow update topic through kafaka based on
	 * th isStateUpdatable
	 * 
	 * @param bpaRequest
	 * @param isStateUpdatable
	 */
	public void update(BPARequest bpaRequest, boolean isStateUpdatable) {
		RequestInfo requestInfo = bpaRequest.getRequestInfo();

		BPA bpaForStatusUpdate = null;
		BPA bpaForUpdate = null;

		BPA bpa = bpaRequest.getBPA();

		if (isStateUpdatable) {
			bpaForUpdate = bpa;
		} else {
			bpaForStatusUpdate = bpa;
		}
		if (bpaForUpdate != null)
			producer.push(config.getUpdateTopic(), new BPARequest(requestInfo, bpaForUpdate));

		if (bpaForStatusUpdate != null)
			producer.push(config.getUpdateWorkflowTopic(), new BPARequest(requestInfo, bpaForStatusUpdate));

		if (bpa.isSwsApplication()) {
//			if ((bpa.getWorkflow().getAction() != null && bpa.getWorkflow().getAction().equalsIgnoreCase("PAY"))
//					|| bpa.getStatus().equalsIgnoreCase("REJECTED") || bpa.getStatus().equalsIgnoreCase("APPROVED"))
			if (bpa.getStatus().equalsIgnoreCase("REJECTED") || bpa.getStatus().equalsIgnoreCase("APPROVED"))
				swsService.updateStatusToSws(bpaRequest);
//			if (status.equalsIgnoreCase("REJECTED") || status.equalsIgnoreCase("APPROVED"))
//				swsService.updateStatusToSws(bpaRequest, status);
		}

	}

	/**
	 * BPA search in database
	 *
	 * @param criteria The BPA Search criteria
	 * @return List of BPA from search
	 */
	public List<BPA> getBPAData(BPASearchCriteria criteria, List<String> edcrNos) {
		List<Object> preparedStmtList = new ArrayList<>();
		String query = queryBuilder.getBPASearchQuery(criteria, preparedStmtList, edcrNos, false);
		List<BPA> BPAData = jdbcTemplate.query(query, preparedStmtList.toArray(), rowMapper);
		return BPAData;
	}

	/**
	 * BPA search count in database
	 *
	 * @param criteria The BPA Search criteria
	 * @return count of BPA from search
	 */
	public int getBPACount(BPASearchCriteria criteria, List<String> edcrNos) {
		List<Object> preparedStmtList = new ArrayList<>();
		String query = queryBuilder.getBPASearchQuery(criteria, preparedStmtList, edcrNos, true);
		int count = jdbcTemplate.queryForObject(query, preparedStmtList.toArray(), Integer.class);
		return count;
	}

	public List<BPA> getBPADataForPlainSearch(BPASearchCriteria criteria, List<String> edcrNos) {
		List<Object> preparedStmtList = new ArrayList<>();
		String query = queryBuilder.getBPASearchQueryForPlainSearch(criteria, preparedStmtList, edcrNos, false);
		List<BPA> BPAData = jdbcTemplate.query(query, preparedStmtList.toArray(), rowMapper);
		return BPAData;
	}

	public int createFeeDetail(PayTypeFeeDetailRequest payTypeFeeDetailRequest) {

		String query = "SELECT COUNT(*) FROM fee_details WHERE feetype=? and application_no=?";
		int count = jdbcTemplate.queryForObject(query,
				new Object[] { payTypeFeeDetailRequest.getFeeType(), payTypeFeeDetailRequest.getApplicationNo() },
				Integer.class);
		log.info("createFeeDetail count : " + count);
		count = count + 1;

		String isFDR = "N";

		if (payTypeFeeDetailRequest.getChargesTypeName().contains("Water")
				|| payTypeFeeDetailRequest.getChargesTypeName().contains("water")) {
			isFDR = "Y";
		}

		LocalDateTime date = LocalDateTime.now();
		String insertQuery = "insert into fee_details(paytype_id,feetype,srno,ulb_tenantid,bill_id,application_no,"
				+ "unit,charges_type_name,prop_plot_area,amount,rate,type,is_fdr,createdby,createddate)"
				+ "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,'" + date + "')";
//		String insertQuery = "insert into pre_post_fee_details(paytype_id,ulb_tenantid,bill_id,application_no,"
//				+ "unit_id,pay_id,charges_type_name,amount,status_type,propvalue,value,status,createdby,payment_type,tip_rate,createddate)"
//				+ "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,'" + date + "')";

		List<Object[]> parameters = new ArrayList<Object[]>();
//		for (PayTypeFeeDetailRequestWrapper payTypeFeeDetailRequestWrapper : payTypeFeeDetailRequestWrapperList) {
//			PayTypeFeeDetailRequest payTypeFeeDetailRequest = payTypeFeeDetailRequestWrapper
//					.getPayTypeFeeDetailRequest();
		parameters.add(new Object[] { payTypeFeeDetailRequest.getPayTypeId(), payTypeFeeDetailRequest.getFeeType(),
				count, payTypeFeeDetailRequest.getTenantId(), payTypeFeeDetailRequest.getBillId(),
				payTypeFeeDetailRequest.getApplicationNo(), payTypeFeeDetailRequest.getUnit(),
				payTypeFeeDetailRequest.getChargesTypeName(), payTypeFeeDetailRequest.getPropPlotArea(),
				payTypeFeeDetailRequest.getAmount(), payTypeFeeDetailRequest.getRate(),
				payTypeFeeDetailRequest.getType(), isFDR, payTypeFeeDetailRequest.getCreatedBy() });
//		}
//		int[] insertResult = jdbcTemplate.batchUpdate(insertQuery, parameters);
		int insertResult = jdbcTemplate.update(insertQuery, payTypeFeeDetailRequest.getPayTypeId(),
				payTypeFeeDetailRequest.getFeeType(), count, payTypeFeeDetailRequest.getTenantId(),
				payTypeFeeDetailRequest.getBillId(), payTypeFeeDetailRequest.getApplicationNo(),
				payTypeFeeDetailRequest.getUnit(), payTypeFeeDetailRequest.getChargesTypeName(),
				payTypeFeeDetailRequest.getPropPlotArea(), payTypeFeeDetailRequest.getAmount(),
				payTypeFeeDetailRequest.getRate(), payTypeFeeDetailRequest.getType(), isFDR,
				payTypeFeeDetailRequest.getCreatedBy());

		log.info("BPARepository.createFeeDetail: " + insertResult + " data inserted into fee_details table");
		String totalAmountQuery = "SELECT SUM(amount) as amount from fee_details WHERE application_no='"
				+ payTypeFeeDetailRequest.getApplicationNo() + "' and feetype='" + payTypeFeeDetailRequest.getFeeType()
				+ "' and is_fdr='N'";
		Map<String, Object> resultMap = jdbcTemplate.queryForMap(totalAmountQuery);
		updateBillDetailAmount(payTypeFeeDetailRequest.getApplicationNo(),
				Double.valueOf(resultMap.get("amount").toString()));
		return insertResult;
	}

	public int updateFeeDetails(PayTypeFeeDetailRequest payTypeFeeDetailRequest) {
		LocalDateTime date = LocalDateTime.now();

		String updateQuery = "update fee_details set amount='" + payTypeFeeDetailRequest.getAmount() + "',updatedby='"
				+ payTypeFeeDetailRequest.getUpdatedBy() + "',updateddate='" + date + "' where application_no ='"
				+ payTypeFeeDetailRequest.getApplicationNo() + "' and ulb_tenantid ='"
				+ payTypeFeeDetailRequest.getTenantId() + "'" + " and id=" + payTypeFeeDetailRequest.getId();
		int updateResult = jdbcTemplate.update(updateQuery);
		log.info("BPARepository.updateFeeDetails: " + updateResult + " data updated into fee_details table");

		String totalAmountQuery = "SELECT SUM(amount) as amount from fee_details WHERE application_no='"
				+ payTypeFeeDetailRequest.getApplicationNo() + "' and feetype='" + payTypeFeeDetailRequest.getFeeType()
				+ "' and is_fdr='N'";
		Map<String, Object> resultMap = jdbcTemplate.queryForMap(totalAmountQuery);
		updateBillDetailAmount(payTypeFeeDetailRequest.getApplicationNo(),
				Double.valueOf(resultMap.get("amount").toString()));

		return updateResult;
	}

	public List<Map<String, Object>> getFeeDetails(String applicationNo) {
		String query = "select id,ulb_tenantid,paytype_id,feetype,srno,bill_id,unit,charges_type_name,prop_plot_area,amount,rate,type,verify,is_fdr from fee_details where application_no=? and feetype='Post'";
		return jdbcTemplate.queryForList(query, new Object[] { applicationNo });

	}

	public int deleteFeeDetailsById(List<Integer> ids, String applicationNo, String feeType) {
//		String deleteQuery = "DELETE FROM fee_details WHERE id IN (:msgNos)";
		String id = ids.toString().replace("[", "").replace("]", "");
		String deleteQuery = "DELETE FROM fee_details WHERE id IN (" + id + ")";
		log.info("deleteQuery: " + deleteQuery);
		int deleteResult = jdbcTemplate.update(deleteQuery);
		log.info("BPARepository.deleteFeeDetailsById: " + deleteResult
				+ " data deleted from fee_details table of id(s) : " + ids.toString());
		String totalAmountQuery = "SELECT SUM(amount) as amount from fee_details WHERE application_no='" + applicationNo
				+ "' and feetype='" + feeType + "' and is_fdr='N'";
//		Map<String, Object> resultMap = jdbcTemplate.queryForMap(totalAmountQuery);
		Double updatedAmount = 0.0;
		List<Map<String, Object>> results = jdbcTemplate.queryForList(totalAmountQuery);
		if (!results.isEmpty()) {
			Map<String, Object> resultMap = results.get(0);
			updatedAmount = Double.valueOf(resultMap.get("amount").toString());
			log.info("Total Updated Amount: " + updatedAmount);
		}

//		updateBillDetailAmount(applicationNo, Double.valueOf(resultMap.get("amount").toString()));
		updateBillDetailAmount(applicationNo, updatedAmount);
		return deleteResult;
	}

	public int verifyFeeDetailsByApplicationNo(String applicationNo, String isVerified, String verifiedBy,
			String feeType) {
		LocalDateTime date = LocalDateTime.now();
		String updateQuery = "UPDATE fee_details SET verify='" + isVerified + "',verifiedby='" + verifiedBy
				+ "',verifieddate='" + date + "' WHERE application_no =" + "'" + applicationNo + "' and feetype='"
				+ feeType + "'";
		int updateResult = jdbcTemplate.update(updateQuery);
		log.info("BPARepository.verifyFeeDetailsByApplicationNo: " + updateResult
				+ " data updated into paytype_master table");
		return updateResult;
	}

	public void updateBillDetailAmount(String applicationNo, double totalAmount) {
//		String updateQuery = "UPDATE egbs_billdetail_v1 SET totalamount='" + totalAmount + "' WHERE consumercode ='"
//				+ applicationNo + "' AND businessservice='BPA.NC_SAN_FEE'";
		String updateQuery = "WITH updated_billdetail AS (UPDATE egbs_billdetail_v1 SET totalamount='" + totalAmount
				+ "' WHERE consumercode = '" + applicationNo
				+ "' AND businessservice ='BPA.NC_SAN_FEE' RETURNING id),updated_demanddetail AS (UPDATE egbs_demanddetail_v1 SET taxamount = '"
				+ totalAmount + "' WHERE demandid = ( SELECT id FROM egbs_demand_v1 WHERE consumercode = '"
				+ applicationNo
				+ "' AND businessservice = 'BPA.NC_SAN_FEE') RETURNING id), updated_billacountdetail AS (UPDATE egbs_billaccountdetail_v1 SET amount = '"
				+ totalAmount
				+ "' WHERE demanddetailid = ( SELECT id FROM updated_demanddetail) RETURNING id) UPDATE egbs_demanddetail_v1_audit SET taxamount = '"
				+ totalAmount + "' WHERE demanddetailid = (SELECT id FROM updated_demanddetail)";
		int updateResult = jdbcTemplate.update(updateQuery);
		log.info("BPARepository.updateFeeDetails: " + updateResult + " data updated into updated_billdetail table");
//		return updateResult;
	}

	public int createPayType(PayTypeRequest payTypeRequest) {

		LocalDateTime date = LocalDateTime.now();

//		String insertQuery = "insert into paytype_master(ulb_tenantid,charges_type_name,payment_type,"
//				+ "defunt,createdby,createddate) values (?,?,?,?,?,'" + date + "')";
		String insertQuery = "insert into paytype_master(ulb_tenantid,charges_type_name,payment_type,"
				+ "defunt,optflag,hrnh,depflag,fdrflg,zdaflg,createdby,createddate) values ('"
				+ payTypeRequest.getTenantId() + "','" + payTypeRequest.getChargesTypeName() + "','"
				+ payTypeRequest.getPaymentType() + "','" + payTypeRequest.getDefunt() + "','"
				+ payTypeRequest.getOptFlag() + "','" + payTypeRequest.getHrnh() + "','" + payTypeRequest.getDepFlag()
				+ "','" + payTypeRequest.getFdrFlg() + "','" + payTypeRequest.getZdaFlg() + "','"
				+ payTypeRequest.getCreatedBy() + "','" + date + "')";

//		Object parameters = new Object();
//		parameters=new Object[] { payTypeRequest.getTenantId(), payTypeRequest.getChargesTypeName(),
//				payTypeRequest.getPaymentType(), payTypeRequest.getDefunt(), payTypeRequest.getCreatedBy() };
//		List<Object[]> parameters = new ArrayList<Object[]>();
//		parameters.add(new Object[] { payTypeRequest.getTenantId(), payTypeRequest.getChargesTypeName(),
//				payTypeRequest.getPaymentType(), payTypeRequest.getDefunt(), payTypeRequest.getCreatedBy() });

//		int[] insertResult = jdbcTemplate.batchUpdate(insertQuery, parameters);
//		int insertResult = jdbcTemplate.update(insertQuery, parameters);
//		int insertResult = jdbcTemplate.update(insertQuery, payTypeRequest.getTenantId(),payTypeRequest.getChargesTypeName(),
//				payTypeRequest.getPaymentType(), payTypeRequest.getDefunt(), payTypeRequest.getCreatedBy());
		int insertResult = jdbcTemplate.update(insertQuery);
		log.info("BPARepository.createPayType: " + insertResult + " data inserted into paytype_master table");
//		return Array.getInt(insertResult, 0);
		return insertResult;
	}

	public List<Map<String, Object>> getPayTypeByTenantId(String tenantId) {

		String query = "select id,charges_type_name,payment_type,defunt,optflag,hrnh,depflag,fdrflg,zdaflg from paytype_master where ulb_tenantid=? order by id";
//        	String query="select charges_type_name from paytype_master where ulb_tenantid=?";
		return jdbcTemplate.queryForList(query, new Object[] { tenantId });
//    		return jdbcTemplate.queryForObject(query, new Object[] { tenantId }, Map.class);

	}

	public int updatePayType(PayTypeRequest payTypeRequest) {
		LocalDateTime date = LocalDateTime.now();

		String updateQuery = "update paytype_master set charges_type_name='" + payTypeRequest.getChargesTypeName()
				+ "',payment_type='" + payTypeRequest.getPaymentType() + "',defunt='" + payTypeRequest.getDefunt()
				+ "',optflag='" + payTypeRequest.getOptFlag() + "',hrnh='" + payTypeRequest.getHrnh() + "',depflag='"
				+ payTypeRequest.getDepFlag() + "',fdrflg='" + payTypeRequest.getFdrFlg() + "',zdaflg='"
				+ payTypeRequest.getZdaFlg() + "',updatedby='" + payTypeRequest.getUpdatedBy() + "',updateddate='"
				+ date + "' where id=" + payTypeRequest.getId();
		int updateResult = jdbcTemplate.update(updateQuery);
		log.info("BPARepository.updatePayType: " + updateResult + " data updated into paytype_master table");
		return updateResult;
	}

	public int createProposalType(ProposalTypeRequest proposalTypeRequest) {

		String query = "SELECT COUNT(*) FROM proposal_type_master WHERE ulb_tenantid=?";
		int count = jdbcTemplate.queryForObject(query, new Object[] { proposalTypeRequest.getTenantId() },
				Integer.class);
		log.info("createPayTpRate count : " + count);
		count = count + 1;

		LocalDateTime date = LocalDateTime.now();

		String insertQuery = "insert into proposal_type_master(srno,ulb_tenantid,description,defunt,createdby,createddate) "
				+ "values (" + count + ",'" + proposalTypeRequest.getTenantId() + "','" + proposalTypeRequest.getDesc()
				+ "','" + proposalTypeRequest.getDefunt() + "','" + proposalTypeRequest.getCreatedBy() + "','" + date
				+ "')";

//		Object parameters = new Object();
//		parameters = new Object[] { proposalTypeRequest.getTenantId(), proposalTypeRequest.getDesc(),
//				proposalTypeRequest.getDefunt(), proposalTypeRequest.getCreatedBy() };
//
//		int insertResult = jdbcTemplate.update(insertQuery, parameters);
		int insertResult = jdbcTemplate.update(insertQuery);

		log.info(
				"BPARepository.createProposalType: " + insertResult + " data inserted into proposal_type_master table");
		return insertResult;
	}

	// fetch data from proposal_type_master table
	public List<Map<String, Object>> getProposalTypeByTenantId(String tenantId) {

		String query = "select id,srno,description,defunt from proposal_type_master where ulb_tenantid=?";
		return jdbcTemplate.queryForList(query, new Object[] { tenantId });
	}

	public int updateProposalType(ProposalTypeRequest proposalTypeRequest) {
		LocalDateTime date = LocalDateTime.now();

		String updateQuery = "update proposal_type_master set description='" + proposalTypeRequest.getDesc()
				+ "',defunt='" + proposalTypeRequest.getDefunt() + "',updatedby='" + proposalTypeRequest.getUpdatedBy()
				+ "',updateddate='" + date + "' where id=" + proposalTypeRequest.getId();
		int updateResult = jdbcTemplate.update(updateQuery);
		log.info("BPARepository.updateBSCategory: " + updateResult + " data updated into paytype_master table");
		return updateResult;
	}

	public int createBCategory(BCategoryRequest bCategoryRequest) {

		LocalDateTime date = LocalDateTime.now();

		String insertQuery = "insert into bcategory_master(ulb_tenantid,description,defunt,createdby,createddate) "
				+ "values ('" + bCategoryRequest.getTenantId() + "','" + bCategoryRequest.getDesc() + "','"
				+ bCategoryRequest.getDefunt() + "','" + bCategoryRequest.getCreatedBy() + "','" + date + "')";

//		Object parameters = new Object();
//		parameters = new Object[] { bCategoryRequest.getTenantId(), bCategoryRequest.getDesc(),
//				bCategoryRequest.getDefunt(), bCategoryRequest.getCreatedBy() };
//
//		int insertResult = jdbcTemplate.update(insertQuery, parameters);
		int insertResult = jdbcTemplate.update(insertQuery);

		log.info("BPARepository.createBCategory: " + insertResult + " data inserted into bcategory_master table");
		return insertResult;
	}

	// fetch data from bcategory_master table
	public List<Map<String, Object>> getBCategoryByTenantId(String tenantId) {
		String query = "select id,description,defunt from bcategory_master where ulb_tenantid=?";
		return jdbcTemplate.queryForList(query, new Object[] { tenantId });
	}

	public int updateBCategory(BCategoryRequest bCategoryRequest) {
		LocalDateTime date = LocalDateTime.now();

		String updateQuery = "update bcategory_master set description='" + bCategoryRequest.getDesc() + "',defunt='"
				+ bCategoryRequest.getDefunt() + "',updatedby='" + bCategoryRequest.getUpdatedBy() + "',updateddate='"
				+ date + "' where id=" + bCategoryRequest.getId();
		int updateResult = jdbcTemplate.update(updateQuery);
		log.info("BPARepository.updateBCategory: " + updateResult + " data updated into paytype_master table");
		return updateResult;
	}

	public int createBSCategory(BSCategoryRequest bsCategoryRequest) {

		LocalDateTime date = LocalDateTime.now();

		String insertQuery = "insert into bscategory_master(ulb_tenantid,description,defunt,catid,createdby,createddate) "
				+ "values ('" + bsCategoryRequest.getTenantId() + "','" + bsCategoryRequest.getDesc() + "','"
				+ bsCategoryRequest.getDefunt() + "','" + bsCategoryRequest.getCatid() + "','"
				+ bsCategoryRequest.getCreatedBy() + "','" + date + "')";
//
//		Object parameters = new Object();
//		parameters = new Object[] { bsCategoryRequest.getTenantId(), bsCategoryRequest.getDesc(),
//				bsCategoryRequest.getDefunt(), bsCategoryRequest.getCatid(), bsCategoryRequest.getCreatedBy() };
//
//		int insertResult = jdbcTemplate.update(insertQuery, parameters);
		int insertResult = jdbcTemplate.update(insertQuery);

		log.info("BPARepository.createBSCategory: " + insertResult + " data inserted into bscategory_master table");
		return insertResult;
	}

	// fetch data from bscategory_master table
	public List<Map<String, Object>> getBSCategoryByTenantId(String tenantId, int catId) {
		String query = "select id,description,defunt,catid from bscategory_master where ulb_tenantid=? AND catid=?";
		return jdbcTemplate.queryForList(query, new Object[] { tenantId, catId });
	}

	public int updateBSCategory(BSCategoryRequest bsCategoryRequest) {
		LocalDateTime date = LocalDateTime.now();

		String updateQuery = "update bscategory_master set description='" + bsCategoryRequest.getDesc() + "',defunt='"
				+ bsCategoryRequest.getDefunt() + "',,catid='" + bsCategoryRequest.getCatid() + "',updatedby='"
				+ bsCategoryRequest.getUpdatedBy() + "',updateddate='" + date + "' where id="
				+ bsCategoryRequest.getId();
		int updateResult = jdbcTemplate.update(updateQuery);
		log.info("BPARepository.updateBSCategory: " + updateResult + " data updated into paytype_master table");
		return updateResult;
	}

	public int createPayTpRate(PayTpRateRequest payTpRateRequest) {

		String query = "SELECT COUNT(*) FROM pay_tp_rate_master WHERE ulb_tenantid=? AND typeid=?";
		int count = jdbcTemplate.queryForObject(query,
				new Object[] { payTpRateRequest.getTenantId(), payTpRateRequest.getTypeId() }, Integer.class);
		log.info("createPayTpRate count : " + count);
		LocalDateTime date = LocalDateTime.now();
		count = count + 1;

		String insertQuery = "insert into pay_tp_rate_master(ulb_tenantid,unitid,typeid,srno,calcon,"
				+ "calcact,p_category,b_category,s_category,rate_res,rate_comm,rate_ind,"
				+ "perval,createdby,createddate) " + "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,'" + date + "')";
//		String insertQuery = "insert into pay_tp_rate_master(ulb_tenantid,unitid,typeid,srno,calcon,"
//				+ "calcact,p_category,b_category,s_category,rate_res,rate_comm,rate_ind,"
//				+ "perval,createdby,createddate) " + "values ('" + payTpRateRequest.getTenantId() + "','"
//				+ payTpRateRequest.getUnitId() + "'," + payTpRateRequest.getTypeId() + "," + count + ",'"
//				+ payTpRateRequest.getCalCon() + "','" + payTpRateRequest.getCalCact() + "','"
//				+ payTpRateRequest.getPCategory() + "','" + payTpRateRequest.getBCategory() + "','"
//				+ payTpRateRequest.getSCategory() + "','" + payTpRateRequest.getRateRes() + "','"
//				+ payTpRateRequest.getRateComm() + "','" + payTpRateRequest.getRateInd() + "','"
//				+ payTpRateRequest.getPerVal() + "','" + payTpRateRequest.getCreatedBy() + "','" + date + "')";

//		Object parameters = new Object();
//		parameters = new Object[] { payTpRateRequest.getTenantId(), payTpRateRequest.getUnitId(),
//				payTpRateRequest.getTypeId(), count + 1, payTpRateRequest.getCalCon(), payTpRateRequest.getCalCact(),
//				payTpRateRequest.getPCategory(), payTpRateRequest.getBCategory(), payTpRateRequest.getSCategory(),
//				payTpRateRequest.getRateRes(), payTpRateRequest.getRateComm(), payTpRateRequest.getRateInd(),
//				payTpRateRequest.getPerVal(), payTpRateRequest.getCreatedBy() };
//
		int insertResult = jdbcTemplate.update(insertQuery, payTpRateRequest.getTenantId(),
				payTpRateRequest.getUnitId(), payTpRateRequest.getTypeId(), count, payTpRateRequest.getCalCon(),
				payTpRateRequest.getCalCact(), payTpRateRequest.getPCategory(), payTpRateRequest.getBCategory(),
				payTpRateRequest.getSCategory(), payTpRateRequest.getRateRes(), payTpRateRequest.getRateComm(),
				payTpRateRequest.getRateInd(), payTpRateRequest.getPerVal(), payTpRateRequest.getCreatedBy());
//		int insertResult = jdbcTemplate.update(insertQuery);

		log.info("BPARepository.createPayTpRate: " + insertResult + " data inserted into pay_tp_rate_master table");
		return insertResult;
	}

	public List<Map<String, Object>> getPayTpRateByTenantIdAndTypeId(String tenantId, int typeId) {
		String query = "select trm.id,trm.unitid,trm.srno,trm.calcon,trm.calcact,"
				+ "ptm.description as p_category, bm.description as b_category, bsm.description as s_category, "
				+ "trm.rate_res,trm.rate_comm,trm.rate_ind,trm.perval from pay_tp_rate_master trm "
				+ "LEFT JOIN proposal_type_master as ptm on ptm.id = trm.p_category "
				+ "LEFT JOIN bcategory_master as bm on bm.id = trm.b_category "
				+ "LEFT JOIN bscategory_master as bsm on bsm.id = trm.s_category "
				+ "where trm.ulb_tenantid=? AND trm.typeid=?";
//		String query = "select id,unitid,srno,calcon,calcact,p_category,b_category,s_category,"
//				+ "rate_res,rate_comm,rate_ind,perval from pay_tp_rate_master " + "where ulb_tenantid=? AND typeid=?";
		return jdbcTemplate.queryForList(query, new Object[] { tenantId, typeId });
	}

	// delete from pay_tp_rate_master table
	public int deletePayTpRateById(List<Integer> ids) {
		String id = ids.toString().replace("[", "").replace("]", "");
		String deleteQuery = "DELETE FROM pay_tp_rate_master WHERE id IN (" + id + ")";
		int deleteResult = jdbcTemplate.update(deleteQuery);
		log.info("BPARepository.deletePayTpRateById: " + deleteResult
				+ " data deleted from pay_tp_rate_master table of id(s) : " + id);
		return deleteResult;
	}

	// insert data into slab_master table
	public int createSlabMaster(SlabMasterRequest slabMasterRequest) {

		String query = "SELECT COUNT(*) FROM slab_master WHERE ulb_tenantid=? AND paytype_id=?";
		int count = jdbcTemplate.queryForObject(query,
				new Object[] { slabMasterRequest.getTenantId(), slabMasterRequest.getPayTypeId() }, Integer.class);
		log.info("createSlabMaster count : " + count);
		LocalDateTime date = LocalDateTime.now();
		count = count + 1;

		String insertQuery = "insert into slab_master(ulb_tenantid,paytype_id,srno,from_val,"
				+ "to_val,p_category,b_category,s_category,rate_res,rate_comm,rate_ind,"
				+ "operation,multp_val,max_limit,createdby,createddate) " + "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,'"
				+ date + "')";
//		String insertQuery = "insert into slab_master(ulb_tenantid,paytype_id,srno,from_val,"
//				+ "to_val,p_category,b_category,s_category,rate_res,rate_comm,rate_ind,"
//				+ "operation,multp_val,max_limit,createdby,createddate) " + "values ('"
//				+ slabMasterRequest.getTenantId() + "'," + slabMasterRequest.getPayTypeId() + "," + count + ",'"
//				+ slabMasterRequest.getFromVal() + "','" + slabMasterRequest.getToVal() + "','"
//				+ slabMasterRequest.getPCategory() + "','" + slabMasterRequest.getBCategory() + "','"
//				+ slabMasterRequest.getSCategory() + "','" + slabMasterRequest.getRateRes() + "','"
//				+ slabMasterRequest.getRateComm() + "','" + slabMasterRequest.getRateInd() + "','"
//				+ slabMasterRequest.getOperation() + "','" + slabMasterRequest.getMultpVal() + "','"
//				+ slabMasterRequest.getMaxLimit() + "','" + slabMasterRequest.getCreatedBy() + "','" + date + "')";
		int insertResult = jdbcTemplate.update(insertQuery, slabMasterRequest.getTenantId(),
				slabMasterRequest.getPayTypeId(), count, slabMasterRequest.getFromVal(), slabMasterRequest.getToVal(),
				slabMasterRequest.getPCategory(), slabMasterRequest.getBCategory(), slabMasterRequest.getSCategory(),
				slabMasterRequest.getRateRes(), slabMasterRequest.getRateComm(), slabMasterRequest.getRateInd(),
				slabMasterRequest.getOperation(), slabMasterRequest.getMultpVal(), slabMasterRequest.getMaxLimit(),
				slabMasterRequest.getCreatedBy());
//		int insertResult = jdbcTemplate.update(insertQuery);

		log.info("BPARepository.createPayTpRate: " + insertResult + " data inserted into slab_master table");
		return insertResult;
	}

	public List<Map<String, Object>> getSlabMasterByTenantIdAndTypeId(String tenantId, int payTypeId) {
		String query = "select sm.id,sm.srno,sm.from_val,sm.to_val,ptm.description as p_category, "
				+ "bm.description as b_category, bsm.description as s_category, sm.rate_res,"
				+ "sm.rate_comm,sm.rate_ind,sm.operation,sm.multp_val,sm.max_limit " + "from slab_master sm "
				+ "LEFT JOIN proposal_type_master as ptm on ptm.id = sm.p_category "
				+ "LEFT JOIN bcategory_master as bm on bm.id = sm.b_category "
				+ "LEFT JOIN bscategory_master as bsm on bsm.id = sm.s_category "
				+ "Where sm.ulb_tenantid=? AND sm.paytype_id=?";
//		String query = "select id,srno,from_val,to_val,p_category,b_category,s_category,"
//				+ "rate_res,rate_comm,rate_ind,operation,multp_val,max_limit from slab_master "
//				+ "where ulb_tenantid=? AND paytype_id=?";
		return jdbcTemplate.queryForList(query, new Object[] { tenantId, payTypeId });
	}

	public int deleteSlabMasterById(List<Integer> ids) {
		String id = ids.toString().replace("[", "").replace("]", "");
		String deleteQuery = "DELETE FROM slab_master WHERE id IN (" + id + ")";
		int deleteResult = jdbcTemplate.update(deleteQuery);
		log.info("BPARepository.deleteSlabMasterById: " + deleteResult
				+ " data deleted from slab_master table of id(s) : " + id);
		return deleteResult;
	}

	public List<Map<String, Object>> getDataCountsForDashboard(String tenantId) {
		/*
		 * String query1 = "SELECT\n" + "    Initiated,\n" +
		 * "    CITIZEN_APPROVAL_INPROCESS,\n" + "    Approved,\n" + "    Rejected,\n" +
		 * "    Reassign,\n" + "    Inprogress,\n" + "    appl_fee,\n" +
		 * "    sanc_fee_pending,\n" + "    department_inprocess,\n" +
		 * "    (Initiated + CITIZEN_APPROVAL_INPROCESS + Approved + Rejected + Reassign + Inprogress + appl_fee + sanc_fee_pending + department_inprocess) AS Total\n"
		 * + " FROM (\n" + "    SELECT\n" +
		 * "        COUNT(CASE WHEN bp.status = 'INITIATED' THEN 1 END) AS Initiated,\n"
		 * +
		 * "        COUNT(CASE WHEN bp.status IN ('CITIZEN_APPROVAL_INPROCESS','CITIZEN_ACTION_PENDING_AT_DOC_VERIF') THEN 1 END) AS CITIZEN_APPROVAL_INPROCESS,\n"
		 * + "        COUNT(CASE WHEN bp.status = 'APPROVED' THEN 1 END) AS Approved,\n"
		 * + "        COUNT(CASE WHEN bp.status = 'REJECTED' THEN 1 END) AS Rejected,\n"
		 * + "        COUNT(CASE WHEN bp.status = 'REASSIGN' THEN 1 END) AS Reassign,\n"
		 * +
		 * "        COUNT(CASE WHEN bp.status = 'INPROGRESS' THEN 1 END) AS Inprogress,\n"
		 * +
		 * "        COUNT(CASE WHEN bp.status = 'PENDING_APPL_FEE' THEN 1 END) AS appl_fee,\n"
		 * +
		 * "        COUNT(CASE WHEN bp.status = 'PENDING_SANC_FEE_PAYMENT' THEN 1 END) AS sanc_fee_pending,\n"
		 * +
		 * "        COUNT(CASE WHEN bp.status IN ('DOC_VERIFICATION_INPROGRESS_BY_ENGINEER', 'DOC_VERIFICATION_INPROGRESS_BY_BUILDER', 'POST_FEE_APPROVAL_INPROGRESS', 'APPROVAL_INPROGRESS') THEN 1 END) AS department_inprocess\n"
		 * + "    FROM eg_bpa_buildingplan bp WHERE bp.tenantid !='cg.citya'\n";
		 * 
		 * String query2 = "SELECT " +
		 * "COUNT(CASE WHEN (bp.status != 'INITIATED' AND bp.status != 'PENDING_APPL_FEE' AND bp.status != 'CITIZEN_APPROVAL_INPROCESS' AND txn_status = 'SUCCESS' AND txn_amount = 1.00) THEN 1 END) AS direct_bhawan_anugya "
		 * + "FROM eg_bpa_buildingplan bp, eg_pg_transactions bd " +
		 * "WHERE bp.applicationno = bd.consumer_code AND bp.tenantid !='cg.citya' ";
		 * 
		 * if (tenantId != null) { query1 += " AND bp.tenantid = '" + tenantId + "'";
		 * query2 += " AND bp.tenantid = '" + tenantId + "'"; } query1 += ") AS counts";
		 */

		String countQuery = "SELECT " + "    counts.Initiated, " + "    counts.CITIZEN_APPROVAL_INPROCESS, "
				+ "    counts.Approved, " + "    counts.Rejected, " + "    counts.Reassign, "
				+ "    counts.Inprogress, " + "    counts.appl_fee, " + "    counts.sanc_fee_pending, "
				+ "    counts.department_inprocess, "
				+ "    COALESCE(bhawan.direct_bhawan_anugya, 0) AS direct_bhawan_anugya, counts.Total "
				// + " (counts.Initiated + counts.CITIZEN_APPROVAL_INPROCESS + counts.Approved +
				// counts.Rejected + counts.Reassign + counts.Inprogress + counts.appl_fee +
				// counts.sanc_fee_pending + counts.department_inprocess) AS Total "
				+ " FROM (" + "    SELECT " + "		   COUNT(bp.applicationno) AS Total, "
				+ "        COUNT(CASE WHEN bp.status = 'INITIATED' THEN 1 END) AS Initiated, "
				+ "        COUNT(CASE WHEN bp.status IN ('CITIZEN_APPROVAL_INPROCESS','CITIZEN_ACTION_PENDING_AT_DOC_VERIF', 'CITIZEN_ACTION_PENDING_AT_APPROVAL', 'BACK_FROM_DOC_VERIFICATION_BY_BUILDER') THEN 1 END) AS CITIZEN_APPROVAL_INPROCESS, "
				+ "        COUNT(CASE WHEN bp.status = 'APPROVED' THEN 1 END) AS Approved, "
				+ "        COUNT(CASE WHEN bp.status = 'REJECTED' THEN 1 END) AS Rejected, "
				+ "        COUNT(CASE WHEN bp.status = 'REASSIGN' THEN 1 END) AS Reassign, "
				+ "        COUNT(CASE WHEN bp.status = 'INPROGRESS' THEN 1 END) AS Inprogress, "
				+ "        COUNT(CASE WHEN bp.status = 'PENDING_APPL_FEE' THEN 1 END) AS appl_fee, "
				+ "        COUNT(CASE WHEN bp.status = 'PENDING_SANC_FEE_PAYMENT' THEN 1 END) AS sanc_fee_pending, "
				+ "        COUNT(CASE WHEN bp.status IN ('DOC_VERIFICATION_INPROGRESS_BY_ENGINEER', 'DOC_VERIFICATION_INPROGRESS_BY_BUILDER', 'POST_FEE_APPROVAL_INPROGRESS', 'APPROVAL_INPROGRESS', 'POST_FEE_APPROVAL_INPROGRESS_BY_BUILDER', 'APPROVAL_INPROGRESS_BY_COMMISSIONER', 'SITE_VISIT_PENDING') THEN 1 END) AS department_inprocess "
				+ "    FROM eg_bpa_buildingplan bp " + "    WHERE bp.tenantid != 'cg.citya'";

		if (tenantId != null) {
			countQuery += " AND bp.tenantid = '" + tenantId + "'";
		}

		countQuery += ") AS counts " + " LEFT JOIN ("
				+ "    SELECT COUNT(DISTINCT bp.applicationno) AS direct_bhawan_anugya "
				+ "    FROM eg_bpa_buildingplan bp " + "    JOIN (" + "        SELECT DISTINCT consumer_code "
				+ "        FROM eg_pg_transactions " + "        WHERE txn_status = 'SUCCESS' "
				+ "        AND txn_amount = 1.00 " + "    ) bd ON bp.applicationno = bd.consumer_code "
				+ "    WHERE bp.tenantid != 'cg.citya' "
				+ "    AND bp.status NOT IN ('INITIATED', 'PENDING_APPL_FEE', 'CITIZEN_APPROVAL_INPROCESS')";

		if (tenantId != null) {
			countQuery += " AND bp.tenantid = '" + tenantId + "'";
		}

		countQuery += ") AS bhawan ON 1=1";

//		log.info("query1---" + query1);
//		log.info("query2---" + query2);

		List<Map<String, Object>> result = jdbcTemplate.queryForList(countQuery);

//		result.add(jdbcTemplate.queryForMap(countQuery));
//		result.add(jdbcTemplate.queryForMap(query2));

		return result;
	}

//	public List<Map<String, Object>> getApplicationDataInDasboardForUlb(String tenantId, String applicationType) {
//		String query = "SELECT " + "billdetail.consumercode AS applicationno, " + "bp.tenantid, "
//				+ "TO_CHAR(TO_TIMESTAMP(bp.createdtime / 1000), 'DD/MM/YYYY') AS applicationdate, "
//				+ "TO_CHAR(TO_TIMESTAMP(bp.approvaldate / 1000), 'DD/MM/YYYY') AS approval_date, "
//				+ "Architectuser.uuid AS uuid, " + "Citizenuser.uuid AS Cuuid, " + "Architectuser.name AS username, "
//				+ "Architectuser.mobilenumber AS altcontactnumber, " + "Architectuser.emailid AS emailid, "
//				+ "Citizenuser.name AS name, " + "Citizenuser.mobilenumber AS mobilenumber, "
//				+ "adr.khataNo AS khatano, " + "adr.mauza AS city, " + "adr.plotno AS plotno, "
//				+ "adr.plotArea AS plotarea, " + "adr.occupancy AS occupancy_type, "
//				+ "adr.patwarihn AS patwari_halka_no, adr.address AS address "
//				+ " SUM(CASE WHEN billdetail.businessservice = 'BPA.NC_APP_FEE' THEN billdetail.totalamount ELSE 0 END) AS prefees, "
//				+ " SUM(CASE WHEN billdetail.businessservice = 'BPA.NC_SAN_FEE' THEN billdetail.totalamount ELSE 0 END) AS postfees, "
//				+ " CASE " + "    WHEN bp.status = 'APPROVED' THEN 'Approved' "
//				+ "    WHEN bp.status IN ('PENDING_APPL_FEE','DOC_VERIFICATION_PENDING_BY_ENGINEER','DOC_VERIFICATION_INPROGRESS_BY_BUILDER','APPROVAL_INPROGRESS','DOC_VERIFICATION_INPROGRESS_BY_ENGINEER', 'POST_FEE_APPROVAL_INPROGRESS', 'PENDING_SANC_FEE_PAYMENT') THEN 'Pending' "
//				+ "    WHEN bp.status = 'REJECTED' THEN 'Rejected' " + "END AS status, "
//				+ "TO_CHAR(TO_TIMESTAMP(bp.approvaldate / 1000), 'DD/MM/YYYY') AS Building_permission_certificate "
//				+ "FROM egbs_billdetail_v1 billdetail "
//				+ "INNER JOIN eg_bpa_buildingplan bp ON billdetail.consumercode = bp.applicationno "
//				+ "INNER JOIN eg_pg_transactions txn ON billdetail.billid = txn.bill_id "
//				+ "INNER JOIN eg_land_address adr ON adr.landinfoid = bp.landid "
//				+ "INNER JOIN eg_user Architectuser ON bp.createdby = Architectuser.uuid "
//				+ "INNER JOIN eg_land_ownerinfo ownerinfo ON bp.landid = ownerinfo.landinfoid "
//				+ "INNER JOIN eg_user Citizenuser ON ownerinfo.uuid = Citizenuser.uuid " + "WHERE 1=1 "
//				+ "AND bp.status IN ('APPROVED','REJECTED','PENDING_APPL_FEE','DOC_VERIFICATION_PENDING_BY_ENGINEER', 'DOC_VERIFICATION_INPROGRESS_BY_BUILDER', 'APPROVAL_INPROGRESS', 'DOC_VERIFICATION_INPROGRESS_BY_ENGINEER', 'POST_FEE_APPROVAL_INPROGRESS', 'PENDING_SANC_FEE_PAYMENT') "
//				+ "AND txn.txn_status='SUCCESS' " + "GROUP BY " + "billdetail.consumercode, " + "bp.createdtime, "
//				+ "bp.approvaldate, " + "Architectuser.name, " + "Architectuser.mobilenumber, "
//				+ "Architectuser.emailid, " + "Citizenuser.name, " + "Citizenuser.mobilenumber, " + "adr.khataNo, "
//				+ "adr.plotno, " + "adr.plotArea, " + "occupancy_type, " + "patwari_halka_no, " + "adr.mauza, "
//				+ "bp.status, " + "bp.tenantid, " + "Architectuser.uuid, " + "Citizenuser.uuid "
//				+ "ORDER BY billdetail.consumercode";
//
//		return jdbcTemplate.queryForList(query, new Object[] {});
//	}
	public List<BPA> getApplicationData(BPASearchCriteria criteria) {
		List<Object> preparedStmtList = new ArrayList<>();
		String query = queryBuilder.getApplicationSearchQuery(criteria, preparedStmtList);
		List<BPA> ApplicationData = jdbcTemplate.query(query, preparedStmtList.toArray(), searchDataRowMapper);
		return ApplicationData;
	}

	public List<Map<String, Object>> getListOfApplications(String tenantId) {

		String query1 = "SELECT" + "  bp.applicationno" + " FROM" + "  eg_bpa_buildingplan bp"
				+ "  inner join eg_pg_transactions bd on bp.applicationno = bd.consumer_code" + "  where"
				+ "  bp.status != 'INITIATED'" + "  AND bp.status != 'PENDING_APPL_FEE'"
				+ "  AND bp.status != 'CITIZEN_APPROVAL_INPROCESS'" + "  AND txn_status = 'SUCCESS'"
				+ "  AND txn_amount = 1.00";
		if (tenantId != null) {
			query1 += " AND bp.tenantid = '" + tenantId + "'";
		}
		List<Map<String, Object>> resultList = new ArrayList<>();
		resultList.addAll(jdbcTemplate.queryForList(query1));

		return jdbcTemplate.queryForList(query1, new Object[] {});
	}

	public List<Map<String, Object>> getIngestData() {

		String query1 = "SELECT\n" + "    la.locality,\n" + "	la.tenantid,\n"
				+ "    COUNT(CASE WHEN bp.status = 'APPROVED' THEN 1 END) AS ApprovedCount,\n"
				+ "    COUNT(CASE WHEN bp.status = 'INITIATED' THEN 1 END) AS InitiatedCount,\n"
				+ "	COUNT(CASE WHEN bp.businessservice = 'BPA_LOW' THEN 1 END) AS LOW,\n"
				+ "	COUNT(CASE WHEN bp.businessservice = 'BPA' THEN 1 END) AS MEDHIGH,\n"
				+ "	COUNT(CASE WHEN la.occupancy = 'Residential' THEN 1 END) AS Residential,\n"
				+ "	COUNT(CASE WHEN la.occupancy = 'Mercantile / Commercial' THEN 1 END) AS Institutional,\n"
				+ "	COUNT(CASE WHEN egpg.gateway_payment_mode = 'Debit Card' THEN 1 END) AS Debit_Card,\n"
				+ "	COUNT(CASE WHEN egpg.gateway_payment_mode = 'Credit Card' THEN 1 END) AS Credit_Card,\n"
				+ "	COUNT(CASE WHEN egpg.gateway_payment_mode = 'Bharat QR' AND egpg.gateway_payment_mode = 'Unified Payments' THEN 1 END) AS UPI\n"
				+ "FROM\n" + "    eg_land_address AS la\n" + "LEFT JOIN\n"
				+ "    eg_bpa_buildingplan AS bp ON bp.landid = la.landinfoid\n" + "LEFT JOIN\n"
				+ "    eg_pg_transactions AS egpg ON bp.applicationno = egpg.consumer_code\n" + "\n" + "GROUP BY\n"
				+ "    la.locality, la.tenantid";

		List<Map<String, Object>> resultList = jdbcTemplate.queryForList(query1);

		log.info("IngestData ResultList Size: " + resultList.size());
		return resultList;
	}

	public int updateBillAmount(String applicationNo, String businessService, String feeType) {

		String updateQuery = "WITH get_amount AS (" + "    SELECT SUM(amount) AS amount" + "    FROM fee_details"
				+ "    WHERE application_no = '" + applicationNo + "'" + "      AND feetype = '" + feeType + "'" + "), "
				+ " updated_billdetail AS (" + "    UPDATE egbs_billdetail_v1"
				+ "    SET totalamount = (SELECT amount FROM get_amount) " + "    WHERE consumercode = '"
				+ applicationNo + "'" + "      AND businessservice = '" + businessService + "'" + "    RETURNING id"
				+ "), " + " updated_demanddetail AS (" + "    UPDATE egbs_demanddetail_v1"
				+ "    SET taxamount = (SELECT amount FROM get_amount) " + "    WHERE demandid IN ("
				+ "        SELECT id" + "        FROM egbs_demand_v1" + "        WHERE consumercode = '" + applicationNo
				+ "'" + "          AND businessservice = '" + businessService + "'" + "    )" + "    RETURNING id"
				+ "), " + " updated_billacountdetail AS (" + "    UPDATE egbs_billaccountdetail_v1"
				+ "    SET amount = (SELECT amount FROM get_amount) " + "    WHERE demanddetailid IN ("
				+ "        SELECT id FROM updated_demanddetail" + "    )" + "    RETURNING id" + ") "
				+ " UPDATE egbs_demanddetail_v1_audit" + " SET taxamount = (SELECT amount FROM get_amount) "
				+ " WHERE demanddetailid IN (" + "    SELECT id FROM updated_demanddetail" + ") ";

		int updateResult = jdbcTemplate.update(updateQuery);

		log.info("BPARepository.updateBillAmount: Bill Amount updated. Result " + updateResult);
		return updateResult;
	}

	public int deleteApplication(String applicationNo) {

		String deleteQuery = "WITH delete_buildingplan AS (\r\n" + "  DELETE FROM eg_bpa_buildingplan\r\n"
				+ "  WHERE applicationno='" + applicationNo + "'\r\n" + "  RETURNING id, landid\r\n"
				+ "), deleted_document AS(\r\n" + "  DELETE FROM eg_bpa_document\r\n"
				+ "  WHERE buildingplanid IN (SELECT id FROM delete_buildingplan)\r\n" + "  RETURNING id\r\n"
				+ "), deleted_landinfo AS (\r\n" + "  DELETE FROM eg_land_landinfo\r\n"
				+ "  WHERE id IN (SELECT landid from delete_buildingplan)\r\n" + "  RETURNING id\r\n"
				+ "), deleted_landaudit AS (\r\n" + "  DELETE FROM eg_land_auditdetails\r\n"
				+ "  WHERE id IN (SELECT landid from delete_buildingplan)\r\n" + "  RETURNING id\r\n"
				+ "), deleted_landaddress AS (\r\n" + "  DELETE FROM eg_land_address\r\n"
				+ "  WHERE landinfoid IN (SELECT landid from delete_buildingplan)\r\n" + "  RETURNING id\r\n"
				+ "), deleted_landowner AS (\r\n" + "  DELETE FROM eg_land_ownerinfo\r\n"
				+ "  WHERE landinfoid IN (SELECT landid from delete_buildingplan)\r\n" + "  RETURNING id\r\n"
				+ "), deleted_landunit AS (\r\n" + "  DELETE FROM eg_land_unit\r\n"
				+ "  WHERE landinfoid IN (SELECT landid from delete_buildingplan)\r\n" + "  RETURNING id\r\n"
				+ "), deleted_landowneraudit AS (\r\n" + "  DELETE FROM eg_land_owner_auditdetails\r\n"
				+ "  WHERE landinfoid IN (SELECT landid from delete_buildingplan)\r\n" + "  RETURNING id\r\n"
				+ "), deleted_landaddressaudit AS (\r\n" + "  DELETE FROM eg_land_address_auditdetails\r\n"
				+ "  WHERE landinfoid IN (SELECT landid from delete_buildingplan)\r\n" + "  RETURNING id\r\n"
				+ "), \r\n" + "  DELETE FROM eg_land_unit_auditdetails\r\n"
				+ "  WHERE landinfoid IN (SELECT landid from delete_buildingplan\r\n"
				+ "), deleted_landgeolocation AS (\r\n" + "  DELETE FROM eg_land_geolocation\r\n"
				+ "  WHERE addressid IN (SELECT id from deleted_landaddress)\r\n" + "  RETURNING id\r\n"
				+ "), deleted_landgeolocationaudit AS (\r\n" + "  DELETE FROM eg_land_geolocation_auditdetails\r\n"
				+ "  WHERE addressid IN (SELECT id from deleted_landaddress)\r\n" + "  RETURNING id\r\n" + ")\r\n";

		int deleteResult = jdbcTemplate.update(deleteQuery);

		log.info("BPARepository.deleteApplication: Application Deleted. Application No : " + applicationNo + ", Result "
				+ deleteResult);
		return deleteResult;
	}

	public int applicationStepBack(String applicationNo, String applicationStatus, int stepsBack) {

		/*
		 * String updateStatusQuery = "update  eg_bpa_buildingplan set status='" +
		 * applicationStatus + "' where applicationno ='" + applicationNo + "'";
		 * 
		 * int updateStatusResult = jdbcTemplate.update(updateStatusQuery);
		 * 
		 * log.info("BPARepository.applicationStepBack:  Application No : " +
		 * applicationNo + " Status : " + applicationStatus + " updated. Result " +
		 * updateStatusResult);
		 * 
		 * String fetchIdForDeleteQuery =
		 * "select id from eg_wf_processinstance_v2 where businessid ='" + applicationNo
		 * + "' ORDER BY createdtime DESC LIMIT " + stepsBack;
		 * 
		 * List<Map<String, Object>> processInstanceIdList =
		 * jdbcTemplate.queryForList(fetchIdForDeleteQuery);
		 * 
		 * String idListWithQuotes = processInstanceIdList.stream().map(row -> "'" +
		 * row.get("id").toString() + "'") .collect(Collectors.joining(","));
		 * 
		 * String deleteProcessInstanceQuery =
		 * "DELETE FROM eg_wf_processinstance_v2 WHERE id IN (" + idListWithQuotes +
		 * ")";
		 * 
		 * int deleteResult = jdbcTemplate.update(deleteProcessInstanceQuery);
		 * 
		 * log.info("BPARepository.applicationStepBack: Application No : " +
		 * applicationNo + ", +" + stepsBack + " step back, Result " + deleteResult);
		 */

		String queryString = "WITH deleted_ids AS (" + "    DELETE FROM eg_wf_processinstance_v2 "
				+ "    WHERE id IN (SELECT id FROM eg_wf_processinstance_v2 WHERE businessid = ? ORDER BY createdtime DESC LIMIT ? ) "
				+ "    RETURNING id " + ") " + " UPDATE eg_bpa_buildingplan " + "SET status = ? "
				+ " WHERE applicationno = ?";

		int queryResult = jdbcTemplate.update(queryString, applicationNo, stepsBack, applicationStatus, applicationNo);
		log.info("Update & Delete executed for Application No: {}, Result: {}, Step Back: {}", applicationNo,
				queryResult, stepsBack);

		return queryResult;
	}

	public List<BPA> getRiskTypeTest(String tenantId) {
		String query = "SELECT applicationno, edcrnumber FROM eg_bpa_buildingplan WHERE tenantid = '" + tenantId + "'";

		List<BPA> bpaList = new ArrayList<>();
		try {
			List<Map<String, Object>> resultList = jdbcTemplate.queryForList(query);

			log.info("query: " + query + ", resultList size : " + resultList.size());

			for (Map<String, Object> resultMap : resultList) {
				BPA bpa = new BPA();
				bpa.setApplicationNo(resultMap.get("applicationno").toString());
				bpa.setEdcrNumber(resultMap.get("edcrnumber").toString());
				bpa.setTenantId(tenantId);
				bpaList.add(bpa);
			}
		} catch (Exception ex) {
			log.error("bparepository.getRiskTypeTest Exception : " + ex.toString());
		}
		return bpaList;

	}

	public int updateRiskType(List<Map<String, Object>> batchValues) {
		String updateQuery = "UPDATE eg_bpa_buildingplan SET risktype = ? WHERE applicationno = ?";
		jdbcTemplate.batchUpdate(updateQuery, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				Map<String, Object> paramMap = batchValues.get(i);
				ps.setString(1, paramMap.get("riskType").toString());
				ps.setString(2, paramMap.get("applicatioNo").toString());
			}

			@Override
			public int getBatchSize() {
				return batchValues.size();
			}
		});

		return batchValues.size();
	}

	public List<Map<String, Object>> getBuildingDetails(String tenantId, String fromDate, String toDate) {
		String query = "SELECT bpa.applicationno, TO_CHAR(TO_TIMESTAMP(bpa.createdtime / 1000), 'DD/MM/YYYY') AS applicationdate, bpa.tenantid, bpa.edcrnumber, bpa.propertyid, cit.uuid, addr.plotno, addr.occupancy, addr.wardno, addr.address,\r\n"
				+ " TO_CHAR(TO_TIMESTAMP(bpa.approvaldate / 1000), 'DD/MM/YYYY') AS issueddate, addr.khatano, addr.plotarea, addr.patwarihn, addr.builtuparea "
				+ "	FROM eg_bpa_buildingplan bpa\r\n" + "	JOIN eg_land_landinfo land ON bpa.landid = land.id\r\n"
				+ "	JOIN eg_land_address addr ON bpa.landid = addr.landinfoid\r\n"
				+ "	JOIN eg_land_ownerinfo cit ON bpa.landid = cit.landinfoid\r\n" + "	WHERE bpa.tenantid='" + tenantId
				+ "' AND TO_TIMESTAMP(bpa.createdtime / 1000)  BETWEEN TO_DATE('" + fromDate + "', 'YYYY-MM-DD')  "
				+ " AND TO_DATE('" + toDate + "', 'YYYY-MM-DD') + interval '1 day' - interval '1 second'";

//		if (fromDate != null && !fromDate.trim().isEmpty() && toDate != null && !toDate.trim().isEmpty()) {
//			query += " AND TO_TIMESTAMP(bpa.createdtime / 1000)  BETWEEN TO_DATE('" + fromDate + "', 'YYYY-MM-DD')  "
//					+ " AND TO_DATE('" + toDate + "', 'YYYY-MM-DD') + interval '1 day' - interval '1 second'";
//		}
		List<Map<String, Object>> resultList = jdbcTemplate.queryForList(query);
		return resultList;
	}

	public int updatePropertyId(String applicationNo, String propertyId) {
		String updateQuery = "UPDATE eg_bpa_buildingplan SET propertyid = '" + propertyId + "' WHERE applicationno = '"
				+ applicationNo + "'";

		int updateResult = jdbcTemplate.update(updateQuery);
		log.info("BPARepository.updatePropertyId: " + updateResult + ", Property Id : " + propertyId
				+ " updated for applicatioNo : " + applicationNo);
		return updateResult;
	}

	public List<Map<String, Object>> getLabourCessFeeDetails(String tenantId, String fromDate, String toDate) {
		String query = "SELECT " +
				"pg.tenant_id AS ULB_Name, " +
				"pg.consumer_code AS consumer_code, " +
				"adr.occupancy AS occupancy_type, " +
				"adr.plotarea AS Plot_Area," +
				"  adr.builtuparea AS BuiltupArea , " +
				"fd.charges_type_name AS labour_cess_type, " +
				"fd.amount AS labour_cess_amount, " +
				"adr.wardno AS Ward_No, " +
				"TO_CHAR(TO_TIMESTAMP(pg.created_time / 1000), 'DD/MM/YYYY') AS payment_date, " +
				"TO_CHAR(TO_TIMESTAMP(bp.approvaldate / 1000), 'DD/MM/YYYY') AS Approval_date " +
				"FROM eg_pg_transactions pg " +
				"JOIN fee_details fd ON fd.bill_id = pg.bill_id " +
				"JOIN egbs_billdetail_v1 eb ON eb.billid = pg.bill_id " +
				"JOIN eg_bpa_buildingplan bp ON bp.applicationno = pg.consumer_code " +
				"JOIN eg_land_address adr ON bp.landid = adr.landinfoid " +
				"WHERE pg.txn_status = 'SUCCESS' " +
				"AND pg.tenant_id = ? " +
				"AND fd.charges_type_name IN (?, ?, ?) " +
				"AND TO_TIMESTAMP(pg.created_time / 1000) BETWEEN TO_DATE(?, 'YYYY-MM-DD') AND TO_DATE(?, 'YYYY-MM-DD') + interval '1 day' - interval '1 second' " +
				"ORDER BY pg.created_time";


		List<Map<String, Object>> resultList = jdbcTemplate.queryForList(query,
				new Object[] { tenantId, "Sub-Tax (Labor Tax)", "Sub-Tax(Karmakar)Fees", "Labour Cess", fromDate, toDate });

		log.info("BPARepository.getLabourCessFeeDetails: result size = " + resultList.size());
		return resultList;
	}
	
	public int updateConsentStatus(String applicationNo, String tenantId,Boolean consentStatus) {
		
		String query = "UPDATE eg_bpa_buildingplan SET consentstatus = ? WHERE applicationno = ? AND tenantid = ?";

		int updateResult = jdbcTemplate.update(query, consentStatus, applicationNo, tenantId);
		
		log.info("BPARepository.updateconsentStatus: " + updateResult 
				+ " updated for applicatioNo : " + applicationNo);
		
		return updateResult;
	}


	private final String fetchCountDetails = "SELECT ebb.TENANTID as tenant, COUNT(ebb.STATUS) AS received," +
			"    COUNT(*) FILTER(WHERE ebb.status LIKE 'APPROVED') AS resolved," +
			"    COUNT(*) FILTER(WHERE ebb.status LIKE '%PEND%') AS pending," +
			"    COUNT(*) FILTER(WHERE ebb.status LIKE 'REJECTED') AS rejected," +
			"    COUNT(*) FILTER(WHERE ebb.status LIKE 'APPROVED'" +
			"	 AND (ebb.lastmodifiedtime - ebb.createdtime) <= 5184000000) AS resolvedWithIn," +
			"    COUNT(*) FILTER(WHERE ebb.status LIKE 'APPROVED'" +
			"    AND (ebb.lastmodifiedtime - ebb.createdtime) > 5184000000) AS resolvedBeyond," +
			"    COUNT(*) FILTER(WHERE ebb.status LIKE '%PEND%'" +
			"    AND (ebb.lastmodifiedtime - ebb.createdtime) <= 5184000000) AS pendingWithIn," +
			"  	 COUNT(*) FILTER(WHERE ebb.status LIKE '%PEND%'" +
			"    AND (ebb.lastmodifiedtime - ebb.createdtime) > 5184000000) AS pendingBeyond" +
			"	 FROM EG_BPA_BUILDINGPLAN ebb" +
			"	 WHERE ebb.CREATEDTIME >= ?" +
			"    AND ebb.CREATEDTIME <= ?" +
			"	 AND ebb.TENANTID != 'cg.citya'" +
			"	 GROUP BY ebb.TENANTID";


	public Map<String, ProposalDetails> getCountsStatuses (LocalDate startDate, LocalDate endDate) {
		log.info("Quering Details from database from from Date =  " + startDate + " endDate = " + endDate);
		return jdbcTemplate.query(
				fetchCountDetails,
				new Object[]{startDate.atStartOfDay(ZoneId.of("UTC")).toInstant().toEpochMilli()
						, endDate.atStartOfDay(ZoneId.of("UTC")).toInstant().toEpochMilli()},
				rs -> {
					Map<String, ProposalDetails> resultMap = new HashMap<>();
					while (rs.next()) {
						String tenantId = rs.getString("tenant");
						ProposalDetails details = new ProposalDetails();
						details.setReceived(rs.getLong("received"));
						details.setResolved(rs.getLong("resolved"));
						details.setPending(rs.getLong("pending"));
						details.setRejected(rs.getLong("rejected"));
						details.setResolvedWithIn(rs.getLong("resolvedWithIn"));
						details.setResolvedBeyond(rs.getLong("resolvedBeyond"));
						details.setPendingWithIn(rs.getLong("pendingWithIn"));
						details.setPendingBeyond(rs.getLong("pendingBeyond"));
						resultMap.put(tenantId, details);
					}
					return resultMap;
				}
		);
	}

	private static final String PAYMENT_DETAILS_FOR_LABOUR_DEPARTMENT = "SELECT" +
			"        TO_CHAR(TO_TIMESTAMP(ept.last_modified_time / 1000), 'DD-MM-YYYY HH24:MI:SS') AS date_of_submission, " +
			"        ela.occupancy AS kary_type," +
			"        ela.address AS sthapna_pata," +
			"        ept.tenant_id AS ulb, " +
			"        (ept.tenant_id || '_' || ebb.applicationno) AS proposal_no," +
			"        ela.builtUpArea AS construction_area, " +
			"        fd.amount AS estimated_cess," +
			"        ept.gateway_txn_id AS transaction_id," +
			"        fd.amount AS cess_amount, " +
			"        TO_CHAR(TO_TIMESTAMP(ept.last_modified_time / 1000), 'DD-MM-YYYY HH24:MI:SS') AS transaction_date, " +
			"        ept.txn_status AS status," +
			"        (ebbd.txn_response::jsonb) ->> 'bank_ref_no' AS transaction_ref_no," +
			"        ept.txn_id AS receipt_no, " +
			"        fd.amount AS bank_amount, " +
			"        ept.tenant_id AS ulb_code, " +
			"        ept.tenant_id AS ulb_name" +
			"    FROM EG_PG_TRANSACTIONS ept" +
			"    JOIN EG_PG_TRANSACTIONS_DUMP ebbd ON ept.txn_id = ebbd.txn_id" +
			"    JOIN EG_BPA_BUILDINGPLAN ebb ON ept.consumer_code = ebb.applicationno" +
			"    JOIN EG_LAND_ADDRESS ela ON ela.landinfoid = ebb.landid" +
			"    JOIN FEE_DETAILS fd ON fd.bill_id = ept.bill_id " +
			"        AND UPPER(fd.charges_type_name) LIKE '%LAB%' " +
			"        AND UPPER(fd.feetype) LIKE '%POST%'" +
			"    WHERE ept.last_modified_time >= ? " +
			"      AND ept.last_modified_time < ? " +
			"      AND ebb.tenantid != 'cg.citya'";


	public List<LabourDepartmentDetails> getLabourDepartmentDetails(LocalDate inputDate) {
		if (inputDate == null) {
			return Collections.emptyList();
		}

		try {
			LocalDate previousDay = inputDate.minusDays(1);

			ZoneId zoneId = ZoneId.of("Asia/Kolkata");

			long startOfPreviousDayMs = previousDay.atStartOfDay(zoneId)
					.toInstant()
					.toEpochMilli();

			long endOfPreviousDayMs = inputDate.atStartOfDay(zoneId)
					.toInstant()
					.toEpochMilli();

			log.info("Fetching Labour Department data for previous day: {} (Epoch bounds: {} to {})",
					previousDay, startOfPreviousDayMs, endOfPreviousDayMs);

			List<LabourDepartmentDetails> resultList = jdbcTemplate.query(
					PAYMENT_DETAILS_FOR_LABOUR_DEPARTMENT,
					new BeanPropertyRowMapper<>(LabourDepartmentDetails.class),
					startOfPreviousDayMs,
					endOfPreviousDayMs
			);

			log.info("Fetched {} records", resultList.size());
			return resultList;

		} catch (Exception e) {
			log.error("Error fetching Labour Department details for date: {}", inputDate, e);
			throw new RuntimeException("Failed to fetch Labour Department details", e);
		}
	}
}
