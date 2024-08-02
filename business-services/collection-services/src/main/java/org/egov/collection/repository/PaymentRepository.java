package org.egov.collection.repository;
                                       
import static java.util.Collections.reverseOrder;
import static org.egov.collection.config.CollectionServiceConstants.KEY_FILESTOREID;
import static org.egov.collection.config.CollectionServiceConstants.KEY_ID;
import static org.egov.collection.repository.querybuilder.PaymentQueryBuilder.*;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.egov.collection.model.Payment;
import org.egov.collection.model.PaymentDetail;
import org.egov.collection.model.PaymentSearchCriteria;
import org.egov.collection.repository.querybuilder.PaymentQueryBuilder;
import org.egov.collection.repository.rowmapper.BillRowMapper;
import org.egov.collection.repository.rowmapper.PaymentRowMapper;
import org.egov.collection.web.contract.Bill;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class PaymentRepository {


    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private PaymentQueryBuilder paymentQueryBuilder;

    private PaymentRowMapper paymentRowMapper;
    
    private BillRowMapper billRowMapper;

    @Autowired
    public PaymentRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate, PaymentQueryBuilder paymentQueryBuilder, 
    		PaymentRowMapper paymentRowMapper, BillRowMapper billRowMapper) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.paymentQueryBuilder = paymentQueryBuilder;
        this.paymentRowMapper = paymentRowMapper;
        this.billRowMapper = billRowMapper;
    }




    @Transactional
    public void savePayment(Payment payment){
        try {

            List<MapSqlParameterSource> paymentDetailSource = new ArrayList<>();
            List<MapSqlParameterSource> billSource = new ArrayList<>();
            List<MapSqlParameterSource> billDetailSource = new ArrayList<>();
            List<MapSqlParameterSource> billAccountDetailSource = new ArrayList<>();

            for (PaymentDetail paymentDetail : payment.getPaymentDetails()) {
                paymentDetailSource.add(getParametersForPaymentDetailCreate(payment.getId(), paymentDetail));
                billSource.add(getParamtersForBillCreate(paymentDetail.getBill()));
                paymentDetail.getBill().getBillDetails().forEach(billDetail -> {
                    billDetailSource.add(getParamtersForBillDetailCreate(billDetail));
                    billDetail.getBillAccountDetails().forEach(billAccountDetail -> {
                        billAccountDetailSource.add(getParametersForBillAccountDetailCreate(billAccountDetail));
                    });
                });

            }
            namedParameterJdbcTemplate.update(INSERT_PAYMENT_SQL, getParametersForPaymentCreate(payment));
            namedParameterJdbcTemplate.batchUpdate(INSERT_PAYMENTDETAIL_SQL, paymentDetailSource.toArray(new MapSqlParameterSource[0]));
            namedParameterJdbcTemplate.batchUpdate(INSERT_BILL_SQL, billSource.toArray(new MapSqlParameterSource[0]));
            namedParameterJdbcTemplate.batchUpdate(INSERT_BILLDETAIL_SQL, billDetailSource.toArray(new MapSqlParameterSource[0]));
            namedParameterJdbcTemplate.batchUpdate(INSERT_BILLACCOUNTDETAIL_SQL,  billAccountDetailSource.toArray(new MapSqlParameterSource[0]));

        }catch (Exception e){
            log.error("Failed to persist payment to database", e);
            throw new CustomException("PAYMENT_CREATION_FAILED", e.getMessage());
        }
    }


    public List<Payment> fetchPayments(PaymentSearchCriteria paymentSearchCriteria) {
        Map<String, Object> preparedStatementValues = new HashMap<>();

        List<String> ids = fetchPaymentIdsByCriteria(paymentSearchCriteria);

        if(CollectionUtils.isEmpty(ids))
            return new LinkedList<>();

        String query = paymentQueryBuilder.getPaymentSearchQuery(ids, preparedStatementValues);
        log.info("Query: " + query);
        log.info("preparedStatementValues: " + preparedStatementValues);
        List<Payment> payments = namedParameterJdbcTemplate.query(query, preparedStatementValues, paymentRowMapper);
        if (!CollectionUtils.isEmpty(payments)) {
            Set<String> billIds = new HashSet<>();
            for (Payment payment : payments) {
                billIds.addAll(payment.getPaymentDetails().stream().map(detail -> detail.getBillId()).collect(Collectors.toSet()));
            }
            Map<String, Bill> billMap = getBills(billIds);
            for (Payment payment : payments) {
                payment.getPaymentDetails().forEach(detail -> {
                    detail.setBill(billMap.get(detail.getBillId()));
                });
            }
            payments.sort(reverseOrder(Comparator.comparingLong(Payment::getTransactionDate)));
        }

        return payments;
    }
    
    public Long getPaymentsCount (String tenantId, String businessService) {
    	
    	Map<String, Object> preparedStatementValues = new HashMap<>();
    	String query = paymentQueryBuilder.getPaymentCountQuery(tenantId, businessService, preparedStatementValues);
    	return namedParameterJdbcTemplate.queryForObject(query, preparedStatementValues, Long.class);
    }

    public List<Payment> fetchPaymentsForPlainSearch(PaymentSearchCriteria paymentSearchCriteria) {
        Map<String, Object> preparedStatementValues = new HashMap<>();
        String query = paymentQueryBuilder.getPaymentSearchQueryForPlainSearch(paymentSearchCriteria, preparedStatementValues);
        log.info("Query: " + query);
        log.info("preparedStatementValues: " + preparedStatementValues);
        List<Payment> payments = namedParameterJdbcTemplate.query(query, preparedStatementValues, paymentRowMapper);
        if (!CollectionUtils.isEmpty(payments)) {
            Set<String> billIds = new HashSet<>();
            for (Payment payment : payments) {
                billIds.addAll(payment.getPaymentDetails().stream().map(detail -> detail.getBillId()).collect(Collectors.toSet()));
            }
            Map<String, Bill> billMap = getBills(billIds);
            for (Payment payment : payments) {
                payment.getPaymentDetails().forEach(detail -> {
                    detail.setBill(billMap.get(detail.getBillId()));
                });
            }
            payments.sort(reverseOrder(Comparator.comparingLong(Payment::getTransactionDate)));
        }

        return payments;
    }


    
    private Map<String, Bill> getBills(Set<String> ids){
    	Map<String, Bill> mapOfIdAndBills = new HashMap<>();
        Map<String, Object> preparedStatementValues = new HashMap<>();
        preparedStatementValues.put("id", ids);
        String query = paymentQueryBuilder.getBillQuery();
        List<Bill> bills = namedParameterJdbcTemplate.query(query, preparedStatementValues, billRowMapper);
        bills.forEach(bill -> {
        	mapOfIdAndBills.put(bill.getId(), bill);
        });
        
        return mapOfIdAndBills;

    }



    public void updateStatus(List<Payment> payments){
        List<MapSqlParameterSource> paymentSource = new ArrayList<>();
        List<MapSqlParameterSource> paymentDetailSource = new ArrayList<>();
        List<MapSqlParameterSource> billSource = new ArrayList<>();
        try {

            for(Payment payment : payments){
                paymentSource.add(getParametersForPaymentStatusUpdate(payment));
                for (PaymentDetail paymentDetail : payment.getPaymentDetails()) {
                    paymentDetailSource.add(getParametersForPaymentDetailStatusUpdate(paymentDetail));
                    billSource.add(getParamtersForBillStatusUpdate(paymentDetail.getBill()));
                }
            }

            namedParameterJdbcTemplate.batchUpdate(COPY_PAYMENT_SQL, paymentSource.toArray(new MapSqlParameterSource[0]));
            namedParameterJdbcTemplate.batchUpdate(COPY_PAYMENTDETAIL_SQL, paymentDetailSource.toArray(new MapSqlParameterSource[0]));
            namedParameterJdbcTemplate.batchUpdate(COPY_BILL_SQL, billSource.toArray(new MapSqlParameterSource[0]));
            namedParameterJdbcTemplate.batchUpdate(STATUS_UPDATE_PAYMENT_SQL, paymentSource.toArray(new MapSqlParameterSource[0]));
            namedParameterJdbcTemplate.batchUpdate(STATUS_UPDATE_PAYMENTDETAIL_SQL, paymentDetailSource.toArray(new MapSqlParameterSource[0]));
            namedParameterJdbcTemplate.batchUpdate(STATUS_UPDATE_BILL_SQL, billSource.toArray(new MapSqlParameterSource[0]));
        }
        catch(Exception e){
            log.error("Failed to persist cancel Receipt to database", e);
            throw new CustomException("CANCEL_RECEIPT_FAILED", "Unable to cancel Receipt");
        }
    }


    public void updatePayment(List<Payment> payments){
        List<MapSqlParameterSource> paymentSource = new ArrayList<>();
        List<MapSqlParameterSource> paymentDetailSource = new ArrayList<>();
        List<MapSqlParameterSource> billSource = new ArrayList<>();
        List<MapSqlParameterSource> billDetailSource = new ArrayList<>();

        try {

            for (Payment payment : payments) {
                paymentSource.add(getParametersForPaymentUpdate(payment));
                payment.getPaymentDetails().forEach(paymentDetail -> {
                    paymentDetailSource.add(getParametersForPaymentDetailUpdate(paymentDetail));
                    billSource.add(getParamtersForBillUpdate(paymentDetail.getBill()));

                    paymentDetail.getBill().getBillDetails().forEach(billDetail -> {
                        billDetailSource.add(getParamtersForBillDetailUpdate(billDetail));
                    });

                });
            }
            namedParameterJdbcTemplate.batchUpdate(UPDATE_PAYMENT_SQL, paymentSource.toArray(new MapSqlParameterSource[0]));
            namedParameterJdbcTemplate.batchUpdate(UPDATE_PAYMENTDETAIL_SQL, paymentDetailSource.toArray(new MapSqlParameterSource[0]));
            namedParameterJdbcTemplate.batchUpdate(UPDATE_BILL_SQL, billSource.toArray(new MapSqlParameterSource[0]));
            namedParameterJdbcTemplate.batchUpdate(UPDATE_BILLDETAIL_SQL, billDetailSource.toArray(new MapSqlParameterSource[0]));
            namedParameterJdbcTemplate.batchUpdate(COPY_PAYMENT_SQL, paymentSource.toArray(new MapSqlParameterSource[0]));
            namedParameterJdbcTemplate.batchUpdate(COPY_PAYMENTDETAIL_SQL, paymentDetailSource.toArray(new MapSqlParameterSource[0]));
            namedParameterJdbcTemplate.batchUpdate(COPY_BILL_SQL, billSource.toArray(new MapSqlParameterSource[0]));
            namedParameterJdbcTemplate.batchUpdate(COPY_BILLDETAIL_SQL, billDetailSource.toArray(new MapSqlParameterSource[0]));
        }catch (Exception e){
            log.error("Failed to update receipt to database", e);
            throw new CustomException("RECEIPT_UPDATION_FAILED", "Unable to update receipt");
        }
    }


    public void updateFileStoreId(List<Map<String,String>> idToFileStoreIdMaps){

        List<MapSqlParameterSource> fileStoreIdSource = new ArrayList<>();

        idToFileStoreIdMaps.forEach(map -> {
            MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource();
            sqlParameterSource.addValue("id",map.get(KEY_ID));
            sqlParameterSource.addValue("filestoreid",map.get(KEY_FILESTOREID));
            fileStoreIdSource.add(sqlParameterSource);
        });

        namedParameterJdbcTemplate.batchUpdate(FILESTOREID_UPDATE_PAYMENT_SQL,fileStoreIdSource.toArray(new MapSqlParameterSource[0]));

    }

    public void updateFileStoreIdToNull(Payment payment){

     
      List<MapSqlParameterSource> fileStoreIdSource = new ArrayList<>();
	  
      MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource();
      sqlParameterSource.addValue("id",payment.getId());
      fileStoreIdSource.add(sqlParameterSource);

      namedParameterJdbcTemplate.batchUpdate(FILESTOREID_UPDATE_NULL_PAYMENT_SQL,fileStoreIdSource.toArray(new MapSqlParameterSource[0]));

    }

    public List<String> fetchPaymentIds(PaymentSearchCriteria paymentSearchCriteria) {

    	StringBuilder query = new StringBuilder("SELECT id from egcl_payment ");
    	boolean whereCluaseApplied= false ;
    	boolean isTenantPresent= true ;
        Map<String, Object> preparedStatementValues = new HashMap<>();
        preparedStatementValues.put("offset", paymentSearchCriteria.getOffset());
        preparedStatementValues.put("limit", paymentSearchCriteria.getLimit());
        if(paymentSearchCriteria.getTenantId() != null && !paymentSearchCriteria.getTenantId().equals("pb")) {
            query.append(" WHERE tenantid=:tenantid ");
            preparedStatementValues.put("tenantid", paymentSearchCriteria.getTenantId());
            whereCluaseApplied=true;
        }else {
        	isTenantPresent = false;
		whereCluaseApplied=false;
        	query.append(" WHERE id in (select paymentid from egcl_paymentdetail WHERE createdtime between :fromDate and :toDate) ");
        	preparedStatementValues.put("fromDate", paymentSearchCriteria.getFromDate());
                preparedStatementValues.put("toDate", paymentSearchCriteria.getToDate());
        } 
        
        if(paymentSearchCriteria.getBusinessServices() != null && isTenantPresent && whereCluaseApplied) {
        	if(whereCluaseApplied) {
            	query.append(" AND id in (select paymentid from egcl_paymentdetail where tenantid=:tenantid AND businessservice=:businessservice) ");
                preparedStatementValues.put("tenantid", paymentSearchCriteria.getTenantId());
                preparedStatementValues.put("businessservice", paymentSearchCriteria.getBusinessServices());

        	}
        }
        
        if(paymentSearchCriteria.getBusinessService() != null && isTenantPresent && whereCluaseApplied) {
            log.info("In side the repo before query: " + paymentSearchCriteria.getBusinessService() );
           query.append(" AND id in (select paymentid from egcl_paymentdetail where tenantid=:tenantid AND businessservice=:businessservice) ");
            preparedStatementValues.put("tenantid", paymentSearchCriteria.getTenantId());
            preparedStatementValues.put("businessservice", paymentSearchCriteria.getBusinessService());
        }
        
        if(paymentSearchCriteria.getFromDate() != null && isTenantPresent && whereCluaseApplied) {
          log.info("In side the repo before query: " + paymentSearchCriteria.getBusinessService() );
           query.append("  AND  createdtime between :fromDate and :toDate");
           preparedStatementValues.put("fromDate", paymentSearchCriteria.getFromDate());
           preparedStatementValues.put("toDate", paymentSearchCriteria.getToDate());

       }
     
        
        query.append(" ORDER BY createdtime offset " + ":offset " + "limit :limit"); 
        
        log.info("fetchPaymentIds query: " + query.toString() );
        return namedParameterJdbcTemplate.query(query.toString(), preparedStatementValues, new SingleColumnRowMapper<>(String.class));

    }

    public List<String> fetchPaymentIdsByCriteria(PaymentSearchCriteria paymentSearchCriteria) {
        Map<String, Object> preparedStatementValues = new HashMap<>();
        String query = paymentQueryBuilder.getIdQuery(paymentSearchCriteria, preparedStatementValues);
        return namedParameterJdbcTemplate.query(query, preparedStatementValues, new SingleColumnRowMapper<>(String.class));
	}

	
public List<String> fetchPropertyDetail(String consumerCode,String businessservice) {
		List<String> status = new ArrayList<String>();                                     
		status = new ArrayList<String>();           
		ObjectMapper objectMapper = new ObjectMapper();

		List<String> oldConnectionno = fetchOldConnectionNo(consumerCode,businessservice); 
		List<String> plotSize = fetchLandArea(consumerCode,businessservice);               
		List<String> usageCategory = fetchUsageCategory(consumerCode,businessservice);     
		List<String> propertyid = fetchpropertyid(consumerCode,businessservice);           
		List<String> adress = fetchadresss(consumerCode,businessservice); 
		 Set<String> consumerCodeSet = Collections.singleton(consumerCode);

		 List<String> additional = adddetails(consumerCodeSet, businessservice);
         List<String> meterdetails = meterinstallmentdate(consumerCodeSet, businessservice);
         List<String> meterid = meterid(consumerCodeSet, businessservice);
         String meterMake=null;
         String avarageMeterReading=null;
         String initialMeterReading=null;
         if (additional != null && !additional.isEmpty()) {
         

             for (String jsonString : additional) {
                 try {
                     Map<String, String> map = objectMapper.readValue(jsonString, new TypeReference<Map<String, String>>() {});
                     if (map.containsValue("meterMake"))
                     meterMake= (String) map.get("meterMake");
                     else 
                    	 meterMake="No Meter Make Found";
                     
                      
                     if (map.containsValue("avarageMeterReading"))
                    	 avarageMeterReading= (String) map.get("avarageMeterReading"); 
                         else 
                        	 avarageMeterReading="No avarageMeterReading  Found";        
                    
                     if (map.containsValue("initialMeterReading"))
                    	 initialMeterReading= (String) map.get("initialMeterReading");
                         else 
                        	 initialMeterReading="No initialMeterReading Found";
                     
                 } catch (Exception e) {
                     e.printStackTrace();
                 }
             }
         } 
                if(oldConnectionno.size()>0) {                                                      
		status.add(oldConnectionno.get(0));  }
		else { status.add("No Value Found");  }
		
		if(plotSize.size()>0 && !StringUtils.isBlank(plotSize.get(0)) )                                                              
		status.add(plotSize.get(0));  
		else
			status.add("No Value Found");  
		
		
		if(usageCategory.size()>0 && !StringUtils.isBlank(usageCategory.get(0)))                                                         
		status.add(usageCategory.get(0)); 
		else                                 
			status.add("No value present");    
		
		
		if(propertyid.size()>0 && !StringUtils.isBlank(propertyid.get(0)))                                                            
			status.add(propertyid.get(0));  
		else
			status.add("No value present");  
		
		
		if(adress.size()>0 && !StringUtils.isBlank(adress.get(0)))                                                                
			status.add(adress.get(0));   
		else                                 
			status.add("No value present"); 
		////
		
		if(meterdetails.size()>0 && !StringUtils.isBlank(meterdetails.get(0)))                                                                
			status.add(meterdetails.get(0));   
		else                                 
			status.add("No value present"); 
		
		
		if(meterid.size()>0 && !StringUtils.isBlank(meterid.get(0)))                                                                
			status.add(meterid.get(0));   
		else                                 
			status.add("No value present"); 
		
		
		
		 if (meterMake.isEmpty()|| meterMake=="")
        	 status.add("No Value Found");  
         else 
        	 status.add(meterMake);
		 
		 if (avarageMeterReading.isEmpty()|| avarageMeterReading=="")
        	 status.add("No Value Found");  
         else 
        	 status.add(avarageMeterReading);
		 
		 if (initialMeterReading.isEmpty()|| initialMeterReading=="")
        	 status.add("No Value Found");  
         else 
        	 status.add(initialMeterReading);
		 
		 
		return status;                                                                     	
	}                                                                                   	
	
	
	
	public List<String> fetchOldConnectionNo(String consumerCode, String businessservice) {
		List<String> res = new ArrayList<>();
		String queryString = "";
		Boolean Isapp = false;
		if (consumerCode.contains("WS_AP"))
			Isapp = true;
		if (Isapp) {
			if (businessservice.equals("WS")) {

				queryString = "select oldconnectionno from eg_ws_connection where applicationno='" + consumerCode + "'";
			} else {
				queryString = "select oldconnectionno from eg_sw_connection where applicationno='" + consumerCode + "'";

			}
		} else {
			if (businessservice.equals("WS")) {

				queryString = "select oldconnectionno from eg_ws_connection where connectionno='" + consumerCode + "'";
			} else {
				queryString = "select oldconnectionno from eg_sw_connection where connectionno='" + consumerCode + "'";

			}
		}
		log.info("Query: " + queryString);
		try {
			// res = jdbcTemplate.queryForList(queryString, String.class);
			res = namedParameterJdbcTemplate.query(queryString, new SingleColumnRowMapper<>(String.class));
		} catch (Exception ex) {
			log.error("Exception while reading bill scheduler status" + ex.getMessage());
		}
		return res;
	}
	
	public List<String> fetchLandArea(String consumerCode, String businessservice) {
		List<String> res = new ArrayList<>();
		Map<String, Object> preparedStatementValues = new HashMap<>();
		String queryString = "";
		Boolean Isapp = false;
		if (consumerCode.contains("WS_AP"))
			Isapp = true;
		if (Isapp) {
			if (businessservice.equals("WS")) {
				queryString = "select a2.landarea from eg_ws_connection a1 inner join eg_pt_property a2 on a1.property_id= a2.propertyid"
						+ " where a1.applicationno = '" + consumerCode + "'";
			} else {
				queryString = "select a2.landarea from eg_sw_connection a1 inner join eg_pt_property a2 on a1.property_id= a2.propertyid"
						+ " where a1.applicationno = '" + consumerCode + "'";
			}
		} else {
			if (businessservice.equals("WS")) {
				queryString = "select a2.landarea from eg_ws_connection a1 inner join eg_pt_property a2 on a1.property_id= a2.propertyid"
						+ " where a1.connectionno = '" + consumerCode + "'";
			} else {
				queryString = "select a2.landarea from eg_sw_connection a1 inner join eg_pt_property a2 on a1.property_id= a2.propertyid"
						+ " where a1.connectionno = '" + consumerCode + "'";
			}
		}
		log.info("Query: " + queryString);
		try {
			// res = jdbcTemplate.queryForList(queryString, String.class);
			res = namedParameterJdbcTemplate.query(queryString, preparedStatementValues,
					new SingleColumnRowMapper<>(String.class));
		} catch (Exception ex) {
			log.error("Exception while reading bill scheduler status" + ex.getMessage());
		}
		return res;
	}
	
	
	
	public List<String> fetchUsageCategory(String consumerCode,String businessservice) {
		List<String> res = new ArrayList<>();
		Map<String, Object> preparedStatementValues = new HashMap<>();
		 String queryString = "";  // Declare queryString outside the if-else block
			Boolean Isapp=false;
			if (consumerCode.contains("WS_AP") || consumerCode.contains("SW_AP"))
				Isapp=true;
	if (Isapp) {
	    if(businessservice.equals("WS")) {
		 queryString = "select a2.usagecategory from eg_ws_connection a1 inner join eg_pt_property a2 on a1.property_id= a2.propertyid"
				+ " where a1.applicationno = '"+consumerCode+"'";
	    }else {
	    	 queryString = "select a2.usagecategory from eg_sw_connection a1 inner join eg_pt_property a2 on a1.property_id= a2.propertyid"
	 				+ " where a1.applicationno = '"+consumerCode+"'";
	    }
	}
	else
	{
		  if(businessservice.equals("WS")) {
				 queryString = "select a2.usagecategory from eg_ws_connection a1 inner join eg_pt_property a2 on a1.property_id= a2.propertyid"
						+ " where a1.connectionno = '"+consumerCode+"'";
			    }else {
			    	 queryString = "select a2.usagecategory from eg_sw_connection a1 inner join eg_pt_property a2 on a1.property_id= a2.propertyid"
			 				+ " where a1.connectionno = '"+consumerCode+"'";
			    }	
	}
		log.info("Query: " +queryString);
		try {
		//	res = jdbcTemplate.queryForList(queryString, String.class);
			res = namedParameterJdbcTemplate.query(queryString, preparedStatementValues, new SingleColumnRowMapper<>(String.class));
		} catch (Exception ex) {
			log.error("Exception while reading bill scheduler status" + ex.getMessage());
		}
		return res;
	}

	public List<String> fetchpropertyid(String consumerCode,String businessservice) {
		List<String> res = new ArrayList<>();
		Map<String, Object> preparedStatementValues = new HashMap<>();
		 String queryString = "";  // Declare queryString outside the if-else block
			Boolean Isapp=false;
			if (consumerCode.contains("WS_AP") || consumerCode.contains("SW_AP")) 
				Isapp=true;
	if (Isapp) {
	    if(businessservice.equals("WS")) {
		 queryString = "select a2.propertyid from eg_ws_connection a1 inner join eg_pt_property a2 on a1.property_id= a2.propertyid"
				+ " where a1.applicationno = '"+consumerCode+"'";
	    }else {
	    	 queryString = "select a2.propertyid from eg_sw_connection a1 inner join eg_pt_property a2 on a1.property_id= a2.propertyid"
	 				+ " where a1.applicationno = '"+consumerCode+"'";
	    }}
	else {
		 if(businessservice.equals("WS")) {
			 queryString = "select a2.propertyid from eg_ws_connection a1 inner join eg_pt_property a2 on a1.property_id= a2.propertyid"
					+ " where a1.connectionno = '"+consumerCode+"'";
		    }else {
		    	 queryString = "select a2.propertyid from eg_sw_connection a1 inner join eg_pt_property a2 on a1.property_id= a2.propertyid"
		 				+ " where a1.connectionno = '"+consumerCode+"'";
		    }
		
	}
		log.info("Query: " +queryString);
		try {
		//	res = jdbcTemplate.queryForList(queryString, String.class);
			res = namedParameterJdbcTemplate.query(queryString, preparedStatementValues, new SingleColumnRowMapper<>(String.class));
		} catch (Exception ex) {
			log.error("Exception while reading bill scheduler status" + ex.getMessage());
		}
		return res;
	}
	public List<String> fetchadresss(String consumerCode,String businessservice) {
		List<String> res = new ArrayList<>();
		Map<String, Object> preparedStatementValues = new HashMap<>();
		 String queryString = "";  // Declare queryString outside the if-else block
			Boolean Isapp=false;
			if (consumerCode.contains("WS_AP") || consumerCode.contains("SW_AP")) 
				Isapp=true;
	if (Isapp)
	{
	    if(businessservice.equals("WS")) {
	    	 queryString = "select CONCAT(doorno,'.',buildingname,'.',city) as address from eg_ws_connection a1 "
	 				+ " inner join eg_pt_property a2 on a1.property_id = a2.propertyid "
	 				+ " inner join eg_pt_address a3 on a2.id=a3.propertyid "
	 				+ " where a1.applicationno='"+consumerCode+"';";
	 			       
	    }else {               
	    	queryString = "select CONCAT(doorno,'.',buildingname,'.',city) as address from eg_sw_connection a1 "
					+ " inner join eg_pt_property a2 on a1.property_id = a2.propertyid "
					+ " inner join eg_pt_address a3 on a2.id=a3.propertyid "
					+ " where a1.applicationno='"+consumerCode+"';";
				       
	    }
	    }
	else {
		 if(businessservice.equals("WS")) {
			 queryString = "select CONCAT(doorno,'.',buildingname,'.',city) as address from eg_ws_connection a1 "
						+ " inner join eg_pt_property a2 on a1.property_id = a2.propertyid "
						+ " inner join eg_pt_address a3 on a2.id=a3.propertyid "
						+ " where a1.connectionno='"+consumerCode+"';";
					       
		    }else {
		    	queryString = "select   CONCAT(doorno,'.',buildingname,'.',city) as address from eg_sw_connection a1 "
						+ " inner join eg_pt_property a2 on a1.property_id = a2.propertyid "
						+ " inner join eg_pt_address a3 on a2.id=a3.propertyid "
						+ " where a1.connectionno='"+consumerCode+"';";
					     
		    }
		
	}
		log.info("Query: " +queryString);
		try {
		//	res = jdbcTemplate.queryForList(queryString, String.class);
			res = namedParameterJdbcTemplate.query(queryString, preparedStatementValues, new SingleColumnRowMapper<>(String.class));
		} catch (Exception ex) {
			log.error("Exception while reading bill scheduler status" + ex.getMessage());
		}
		return res;
	}



	
    
	public List<String> fetchUsageCategoryByApplicationno(Set<String> consumerCodes) {
		List<String> res = new ArrayList<>();
		String consumercode = null;
		 Iterator<String> iterate = consumerCodes.iterator();
		 while(iterate.hasNext()) {
			    consumercode =   iterate.next();			  
		}		
		Map<String, Object> preparedStatementValues = new HashMap<>();
		String queryString;
		if (consumercode.contains("WS_AP")) {
		    queryString = "select a2.usagecategory from eg_ws_connection a1 "
				+ " inner join eg_pt_property a2 on a1.property_id = a2.propertyid "
				+ " inner join eg_pt_address a3 on a2.id=a3.propertyid "
				+ " where a1.applicationno='"+consumercode+"'"
			        + " and a2.status='ACTIVE';";
		log.info("Query for fetchPaymentIdsByCriteria: " +queryString);
		} else {
			queryString = "select a2.usagecategory from eg_sw_connection a1 "
					+ " inner join eg_pt_property a2 on a1.property_id = a2.propertyid "
					+ " inner join eg_pt_address a3 on a2.id=a3.propertyid "
					+ " where a1.applicationno='"+consumercode+"'"
				        + " and a2.status='ACTIVE';";
			log.info("Query for fetchPaymentIdsByCriteria: " +queryString);
		}
		try {
			res = namedParameterJdbcTemplate.query(queryString, preparedStatementValues, new SingleColumnRowMapper<>(String.class));
		} catch (Exception ex) {
			log.error("Exception while reading usage category" + ex.getMessage());
		}
		return res;
	}
	public List<String> fetchAddressByApplicationno(Set<String> consumerCodes) {
		List<String> res = new ArrayList<>();
		String consumercode = null;
		 Iterator<String> iterate = consumerCodes.iterator();
		 while(iterate.hasNext()) {
			    consumercode =   iterate.next();			  
		}
		Map<String, Object> preparedStatementValues = new HashMap<>();
		String queryString;
		if (consumercode.contains("WS_AP")) {
		 queryString = "select CONCAT(doorno,buildingname,city) as address from eg_ws_connection a1 "
				+ " inner join eg_pt_property a2 on a1.property_id = a2.propertyid "
				+ " inner join eg_pt_address a3 on a2.id=a3.propertyid "
				+ " where a1.applicationno='"+consumercode+"'"
			        + " and a2.status='ACTIVE';";
		log.info("Query for fetchAddressByApplicationno: " +queryString);
		}
		else {
			 queryString = "select CONCAT(doorno,buildingname,city) as address from eg_sw_connection a1 "
						+ " inner join eg_pt_property a2 on a1.property_id = a2.propertyid "
						+ " inner join eg_pt_address a3 on a2.id=a3.propertyid "
						+ " where a1.applicationno='"+consumercode+"'"
				                + " and a2.status='ACTIVE';";
				log.info("Query for fetchAddressByApplicationno: " +queryString);
		}
		try {
			res = namedParameterJdbcTemplate.query(queryString, preparedStatementValues, new SingleColumnRowMapper<>(String.class));
		} catch (Exception ex) {
			log.error("Exception while reading usage category" + ex.getMessage());
		}
		return res;
	}


	//RECE
	
	public List<String> fetchUsageCategoryByApplicationnos(Set<String> consumerCodes,String businesssrvice) {
		List<String> res = new ArrayList<>();
		String consumercode = null;
		 Iterator<String> iterate = consumerCodes.iterator();
		 while(iterate.hasNext()) {
			    consumercode =   iterate.next();			  
		}		
		Map<String, Object> preparedStatementValues = new HashMap<>();
		String queryString;
		if (businesssrvice.contains("WS")) {
			queryString = "select a2.usagecategory  FROM eg_ws_connection a1 INNER JOIN eg_pt_property a2 ON a1.property_id = a2.propertyid  where    a1.status='Active' and a1.connectionno   ='"+consumercode+"';";
		log.info("Query for fetchPaymentIdsByCriteria: " +queryString);
		} else {
			queryString = "select a2.usagecategory  FROM eg_sw_connection a1 INNER JOIN eg_pt_property a2 ON a1.property_id = a2.propertyid  where    a1.status='Active' and a1.connectionno   ='"+consumercode+"';";

			log.info("Query for fetchPaymentIdsByCriteria: " +queryString);
		}
		try {
			res = namedParameterJdbcTemplate.query(queryString, preparedStatementValues, new SingleColumnRowMapper<>(String.class));
		} catch (Exception ex) {
			log.error("Exception while reading usage category" + ex.getMessage());
		}
		return res;
	}
	public List<String> fetchAddressByApplicationnos(Set<String> consumerCodes,String businesssrvice) {
		List<String> res = new ArrayList<>();
		String consumercode = null;
		 Iterator<String> iterate = consumerCodes.iterator();
		 while(iterate.hasNext()) {
			    consumercode =   iterate.next();			  
		}
		Map<String, Object> preparedStatementValues = new HashMap<>();
		String queryString;
		if (businesssrvice.contains("WS")) {
			 queryString = "select concat(a3.doorno,',',a3.plotno,',',a3.buildingname,',',a3.street,',',a3.landmark,',',a3.district ,',',a3.region,',',a3.city )  FROM eg_ws_connection a1 INNER JOIN eg_pt_property a2 ON a1.property_id = a2.propertyid  inner join eg_pt_address as a3 on a2.id=a3.propertyid where   a1.status='Active' and a1.connectionno   ='"+consumercode+"';";
		log.info("Query for fetchAddressByApplicationno: " +queryString);
		}
		else {
			 queryString = "select concat(a3.doorno,',',a3.plotno,',',a3.buildingname,',',a3.street,',',a3.landmark,',',a3.district ,',',a3.region,',',a3.city ) FROM eg_sw_connection a1 INNER JOIN eg_pt_property a2 ON a1.property_id = a2.propertyid  inner join eg_pt_address as a3 on a2.id=a3.propertyid where   a1.status='Active' and a1.connectionno   ='"+consumercode+"';";

				log.info("Query for fetchAddressByApplicationno: " +queryString);
		}
		try {
			res = namedParameterJdbcTemplate.query(queryString, preparedStatementValues, new SingleColumnRowMapper<>(String.class));
		} catch (Exception ex) {
			log.error("Exception while reading usage category" + ex.getMessage());
		}
		return res;
	}
	
	// for propertyid//
	
	public List<String> fetchPropertyid(Set<String> consumerCodes,String businesssrvice) {
		List<String> res = new ArrayList<>();
		String consumercode = null;
		 Iterator<String> iterate = consumerCodes.iterator();
		 while(iterate.hasNext()) {
			    consumercode =   iterate.next();			  
		}		
		Map<String, Object> preparedStatementValues = new HashMap<>();
		String queryString;
		if (businesssrvice.contains("WS")) {
			queryString = "select a1.property_id  FROM eg_ws_connection a1 INNER JOIN eg_pt_property a2 ON a1.property_id = a2.propertyid where    a1.status='Active' and a1.connectionno   ='"+consumercode+"';";
		log.info("Query for fetchPaymentIdsByCriteria: " +queryString);
		} else {
			queryString = "select a1.property_id  FROM eg_sw_connection a1 INNER JOIN eg_pt_property a2 ON a1.property_id = a2.propertyid where    a1.status='Active' and a1.connectionno   ='"+consumercode+"';";

			log.info("Query for fetchPaymentIdsByCriteria: " +queryString);
		}
		try {
			res = namedParameterJdbcTemplate.query(queryString, preparedStatementValues, new SingleColumnRowMapper<>(String.class));
		} catch (Exception ex) {
			log.error("Exception while reading usage category" + ex.getMessage());
		}
		return res;
	}
	
	
	public List<String> adddetails(Set<String> consumerCodes,String businesssrvice) {
		List<String> res = new ArrayList<>();
		String consumercode = null;
		 Iterator<String> iterate = consumerCodes.iterator();
		 while(iterate.hasNext()) {
			    consumercode =   iterate.next();			  
		}		
		Map<String, Object> preparedStatementValues = new HashMap<>();
		String queryString;
		if (businesssrvice.contains("WS")) {
			queryString = "select a1.additionaldetails  FROM eg_ws_connection a1  where a1.connectionno   ='"+consumercode+"';";
		log.info("Query for fetchPaymentIdsByCriteria: " +queryString);
		} else {
			queryString = "select a1.additionaldetails  FROM eg_sw_connection a1 where  a1.connectionno   ='"+consumercode+"';";

			log.info("Query for fetchPaymentIdsByCriteria: " +queryString);
		}
		try {
			res = namedParameterJdbcTemplate.query(queryString, preparedStatementValues, new SingleColumnRowMapper<>(String.class));
		} catch (Exception ex) {
			log.error("Exception while reading usage category" + ex.getMessage());
		}
		return res;
	}
	
	
	
	
	
	
	public List<String> meterinstallmentdate(Set<String> consumerCodes,String businesssrvice) {
		List<String> res = new ArrayList<>();
		String consumercode = null;
		 Iterator<String> iterate = consumerCodes.iterator();
		 while(iterate.hasNext()) {
			    consumercode =   iterate.next();			  
		}		
		Map<String, Object> preparedStatementValues = new HashMap<>();
		String queryString;
		if (businesssrvice.contains("WS")) {
			queryString = "select a2.meterinstallationdate FROM eg_ws_connection a1   inner join eg_ws_service as a2 on a1.id=a2.connection_id where a1.connectionno   ='"+consumercode+"';";
		log.info("Query for fetchPaymentIdsByCriteria: " +queryString);
		} else {
			queryString = "select a2.meterinstallationdate FROM eg_sw_connection a1 inner join eg_ws_service as a2 on a1.id=a2.connection_id where  a1.connectionno   ='"+consumercode+"';";

			log.info("Query for fetchPaymentIdsByCriteria: " +queryString);
		}
		try {
			res = namedParameterJdbcTemplate.query(queryString, preparedStatementValues, new SingleColumnRowMapper<>(String.class));
		} catch (Exception ex) {
			log.error("Exception while reading usage category" + ex.getMessage());
		}
		return res;
	}
	
	public List<String> meterid(Set<String> consumerCodes,String businesssrvice) {
		List<String> res = new ArrayList<>();
		String consumercode = null;
		 Iterator<String> iterate = consumerCodes.iterator();
		 while(iterate.hasNext()) {
			    consumercode =   iterate.next();			  
		}		
		Map<String, Object> preparedStatementValues = new HashMap<>();
		String queryString;
		if (businesssrvice.contains("WS")) {
			queryString = "select a2.meterid FROM eg_ws_connection a1   inner join eg_ws_service as a2 on a1.id=a2.connection_id where a1.connectionno   ='"+consumercode+"';";
		log.info("Query for fetchPaymentIdsByCriteria: " +queryString);
		} else {
			queryString = "select a2.meterid FROM eg_sw_connection a1 inner join eg_ws_service as a2 on a1.id=a2.connection_id where  a1.connectionno   ='"+consumercode+"';";

			log.info("Query for fetchPaymentIdsByCriteria: " +queryString);
		}
		try {
			res = namedParameterJdbcTemplate.query(queryString, preparedStatementValues, new SingleColumnRowMapper<>(String.class));
		} catch (Exception ex) {
			log.error("Exception while reading usage category" + ex.getMessage());
		}
		return res;
	}
	
	
	/**
	 * API is to get the distinct ifsccode from payment
	 * 
	 * @return ifsccode list
	 */
	public List<String> fetchIfsccode() {

		return namedParameterJdbcTemplate.query("SELECT distinct ifsccode from egcl_payment where ifsccode is not null ",
				new SingleColumnRowMapper<>(String.class));

	}
public List<String> fetchConsumerCodeByReceiptNumber(String receiptnumber) {
		List<String> res = new ArrayList<>();
		Map<String, Object> preparedStatementValues = new HashMap<>();
		String queryString = "select bill.consumercode from egcl_paymentdetail pd, egcl_bill bill "
				+ " where bill.id=pd.billid  "
				+ " and pd.receiptnumber='"+receiptnumber+"'";
		log.info("Query: " +queryString);
		try {
			res = namedParameterJdbcTemplate.query(queryString, preparedStatementValues, new SingleColumnRowMapper<>(String.class));
		} catch (Exception ex) {
			log.error("Exception while reading usage category" + ex.getMessage());
		}
		return res;
	}
	/**
	 * API, All payments with @param ifsccode, additional details updated
	 * with @param additionaldetails
	 * 
	 * @param additionaldetails
	 * @param ifsccode
	 */

	@Transactional
	public void updatePaymentBankDetail(JsonNode additionaldetails, String ifsccode) {
		List<MapSqlParameterSource> parameterSource = new ArrayList<>();
		parameterSource.add(getParametersForBankDetailUpdate(additionaldetails, ifsccode));

		/**
		 * UPDATE_PAYMENT_BANKDETAIL_SQL query adds the bankdetails data to
		 * existing object type additionaldetails ex: object type
		 * additionaldetails data {"isWhatsapp": false }
		 */
		namedParameterJdbcTemplate.batchUpdate(UPDATE_PAYMENT_BANKDETAIL_SQL,
				parameterSource.toArray(new MapSqlParameterSource[0]));

		List<MapSqlParameterSource> emptyAddtlParameterSource = new ArrayList<>();
		emptyAddtlParameterSource.add(getParametersEmptyDtlBankDetailUpdate(additionaldetails, ifsccode));
		/**
		 * UPDATE_PAYMENT_BANKDETAIL_EMPTYADDTL_SQL query update the bankdetails
		 * to empty/null additionaldetails. ex: empty or 'null'
		 * additionaldetails data.
		 */
		namedParameterJdbcTemplate.batchUpdate(UPDATE_PAYMENT_BANKDETAIL_EMPTYADDTL_SQL,
				emptyAddtlParameterSource.toArray(new MapSqlParameterSource[0]));

		/**
		 * UPDATE_PAYMENT_BANKDETAIL_ARRAYADDTL_SQL query adds bankdetails data
		 * to existing array type additionaldetails. ex: array additional data
		 * :[{"bankName": "State Bank of India", "branchName": "Chandigarh Main
		 * Branch"}]
		 * 
		 */
		namedParameterJdbcTemplate.batchUpdate(UPDATE_PAYMENT_BANKDETAIL_ARRAYADDTL_SQL,
				emptyAddtlParameterSource.toArray(new MapSqlParameterSource[0]));

	}
}
