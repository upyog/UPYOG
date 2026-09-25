package org.egov.echallan.repository;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.egov.echallan.config.ChallanConfiguration;
import org.egov.echallan.model.Challan;
import org.egov.echallan.model.ChallanRequest;
import org.egov.echallan.model.SearchCriteria;
import org.egov.echallan.producer.Producer;
import org.egov.echallan.repository.builder.ChallanQueryBuilder;
import org.egov.echallan.repository.rowmapper.ChallanCountRowMapper;
import org.egov.echallan.repository.rowmapper.ChallanRowMapper;
import org.egov.echallan.web.models.collection.Bill;
import org.egov.echallan.web.models.collection.PaymentDetail;
import org.egov.echallan.web.models.collection.PaymentRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.egov.echallan.repository.builder.ChallanQueryBuilder.*;


@Slf4j
@Repository
public class ChallanRepository {

    private final Producer producer;

    private final ChallanConfiguration config;

    private final JdbcTemplate jdbcTemplate;

    private final ChallanQueryBuilder queryBuilder;

    private final ChallanRowMapper rowMapper;

    private final RestTemplate restTemplate;

    private final ObjectMapper mapper;

    private final ChallanCountRowMapper countRowMapper;

    @Value("${egov.filestore.host}")
    private String fileStoreHost;

    @Value("${egov.filestore.setinactivepath}")
	private String fileStoreInactivePath;

    public ChallanRepository(Producer producer, ChallanConfiguration config, ChallanQueryBuilder queryBuilder,
    		JdbcTemplate jdbcTemplate, ChallanRowMapper rowMapper, RestTemplate restTemplate,
    		ObjectMapper mapper, ChallanCountRowMapper countRowMapper) {
        this.producer = producer;
        this.config = config;
        this.jdbcTemplate = jdbcTemplate;
        this.queryBuilder = queryBuilder;
        this.rowMapper = rowMapper;
        this.restTemplate = restTemplate;
        this.mapper = mapper;
        this.countRowMapper = countRowMapper;
    }



    /**
     * Pushes the request on save topic
     *
     * @param challanRequest The echallan create request
     */
    public void save(ChallanRequest challanRequest) {

        producer.push(config.getSaveChallanTopic(), challanRequest);
    }

    /**
     * Pushes the request on update topic
     *
     * @param challanRequest The echallan create request
     */
    public void update(ChallanRequest challanRequest) {

        producer.push(config.getUpdateChallanTopic(), challanRequest);
    }


    public List<Challan> getChallans(SearchCriteria criteria) {
        List<Object> preparedStmtList = new ArrayList<>();
        String query = queryBuilder.getChallanSearchQuery(criteria, preparedStmtList,false);
        return jdbcTemplate.query(query, rowMapper, preparedStmtList.toArray());
    }

    /**
     * gets the total count for a search request
     *
     * @param criteria The echallan search criteria
     */
    public int getChallanSearchCount(SearchCriteria criteria) {
        List<Object> preparedStmtList = new ArrayList<>();
        String query = queryBuilder.getChallanSearchQuery(criteria, preparedStmtList,true);

        Integer count = jdbcTemplate.queryForObject(query, Integer.class, preparedStmtList.toArray());
        return count != null ? count : 0;
    }


	public void updateFileStoreId(List<Challan> challans) {
		List<Object[]> rows = new ArrayList<>();

        challans.forEach(challan ->
        	rows.add(new Object[] {challan.getFilestoreid(),
        			challan.getId()}
        	        )
        );

        jdbcTemplate.batchUpdate(FILESTOREID_UPDATE_SQL,rows);

	}

	 public void setInactiveFileStoreId(String tenantId, List<String> fileStoreIds)  {
			String idList = fileStoreIds.toString().substring(1, fileStoreIds.toString().length() - 1).replace(", ", ",");
			String url = fileStoreHost + fileStoreInactivePath + "?tenantId=" + tenantId + "&fileStoreIds=" + idList;
			try {
				  restTemplate.postForObject(url, null, String.class) ;
			} catch (Exception e) {
				log.error("Error in calling fileStore "+e.getMessage());
			}

		}



	public void updateChallanOnCancelReceipt(Map<String, Object> messagePayload) {
		PaymentRequest paymentRequest = mapper.convertValue(messagePayload, PaymentRequest.class);

		List<PaymentDetail> paymentDetails = paymentRequest.getPayment().getPaymentDetails();
		List<Object[]> rows = new ArrayList<>();
		for (PaymentDetail paymentDetail : paymentDetails) {
			Bill bill = paymentDetail.getBill();
			rows.add(new Object[] {bill.getConsumerCode(),
        			bill.getBusinessService()}
        	        );
		}
		jdbcTemplate.batchUpdate(CANCEL_RECEIPT_UPDATE_SQL,rows);

	}

    /**
     * DB Repository that makes jdbc calls to the db and fetches echallan count.
     *
     * @param tenantId
     * @return
     */
    public Map<String,String> fetchChallanCount(String tenantId){
        Map<String,String> response = new HashMap<>();
        List<Object> preparedStmtList = new ArrayList<>();

        String query = queryBuilder.getChallanCountQuery(tenantId, preparedStmtList);

        try {
            response = jdbcTemplate.query(query, countRowMapper, preparedStmtList.toArray());
        }catch(Exception e) {
            log.error("Exception while making the db call: ",e);
            log.error("query; "+query);
        }
        return response;
    }



	public Map<String,Integer> fetchDynamicData(String tenantId) {

		Map<String, Integer> dynamicData = new HashMap<>();

		if (tenantId == null || tenantId.trim().isEmpty()) {
			dynamicData.put("totalCollection", 0);
			dynamicData.put("totalServices", 0);
			return dynamicData;
		}

		List<Object> preparedStmtListTotalCollection = new ArrayList<>();
		String query = queryBuilder.getTotalCollectionQuery(tenantId, preparedStmtListTotalCollection);

		Integer totalCollection = jdbcTemplate.queryForObject(query, Integer.class, preparedStmtListTotalCollection.toArray());
		if (totalCollection == null) {
			totalCollection = 0;
		}

		List<Object> preparedStmtListTotalServices = new ArrayList<>();
		query = queryBuilder.getTotalServicesQuery(tenantId, preparedStmtListTotalServices);

		Integer totalServices = jdbcTemplate.queryForObject(query, Integer.class, preparedStmtListTotalServices.toArray());
		if (totalServices == null) {
			totalServices = 0;
		}

		dynamicData.put("totalCollection", totalCollection);
		dynamicData.put("totalServices", totalServices);

		return dynamicData;

	}

	/**
	 * Persists document details directly to DB
	 * This is done directly via JDBC instead of persister due to array handling issues
	 *
	 * @param challan The challan with documents to persist
	 */
	public void saveDocuments(Challan challan) {
		if (challan.getUploadedDocumentDetails() == null || challan.getUploadedDocumentDetails().isEmpty()) {
			return;
		}

		String sql = "INSERT INTO public.eg_challan_document_detail(document_detail_id, challan_id, document_type, " +
				"filestore_id, createdby, lastmodifiedby, createdtime, lastmodifiedtime) " +
				"VALUES (?, ?, ?, ?, ?, ?, ?, ?) " +
				"ON CONFLICT (document_detail_id) DO UPDATE SET " +
				"challan_id = EXCLUDED.challan_id, " +
				"document_type = EXCLUDED.document_type, " +
				"filestore_id = EXCLUDED.filestore_id, " +
				"lastmodifiedby = EXCLUDED.lastmodifiedby, " +
				"lastmodifiedtime = EXCLUDED.lastmodifiedtime";

		List<Object[]> batchArgs = new ArrayList<>();

		challan.getUploadedDocumentDetails().forEach(doc ->
			batchArgs.add(new Object[]{
				doc.getDocumentDetailId(),
				doc.getChallanId(),
				doc.getDocumentType(),
				doc.getFileStoreId(),
				challan.getAuditDetails().getCreatedBy(),
				challan.getAuditDetails().getLastModifiedBy(),
				challan.getAuditDetails().getCreatedTime(),
				challan.getAuditDetails().getLastModifiedTime()
			})
		);

		try {
			jdbcTemplate.batchUpdate(sql, batchArgs);
			log.info("Successfully persisted {} documents for challan {}", batchArgs.size(), challan.getId());
		} catch (Exception e) {
			log.error("Error persisting documents for challan {}: {}", challan.getId(), e.getMessage(), e);
		}
	}

}
