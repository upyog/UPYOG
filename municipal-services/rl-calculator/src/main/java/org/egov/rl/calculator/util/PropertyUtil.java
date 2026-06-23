package org.egov.rl.calculator.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;
import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.mdms.model.*;
import org.egov.rl.calculator.repository.Repository;
import org.egov.rl.calculator.web.models.GetBillCriteria;
import org.egov.rl.calculator.web.models.RLProperty;
import org.egov.rl.calculator.web.models.TaxRate;
import org.egov.rl.calculator.web.models.property.AuditDetails;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.egov.rl.calculator.util.RLConstants.RL_MASTER_MODULE_NAME;

@Component
@Slf4j
public class PropertyUtil{

	
		@Autowired
		private Repository restRepo;

		@Autowired
		private ObjectMapper mapper;

		@Autowired
		private Configurations config;
		
		 public AuditDetails getAuditDetails(String by, Boolean isCreate) {
		    	
		        Long time = System.currentTimeMillis();
		        
		        if(isCreate)
		            return AuditDetails.builder().createdBy(by).createdTime(time).lastModifiedBy(by).lastModifiedTime(time).build();
		        else
		            return AuditDetails.builder().lastModifiedBy(by).lastModifiedTime(time).build();
		    }


		public List<RLProperty> getCalculateAmount(String propertyId, RequestInfo requestInfo, String tenantId, String moduleName) {

			List<RLProperty> calculateAmount = new ArrayList<RLProperty>();
			StringBuilder uri = new StringBuilder();
			uri.append(config.getMdmsHost()).append(config.getMdmsEndpoint());

			MdmsCriteriaReq mdmsCriteriaReq = getMdmsRequestCalculationType(requestInfo, tenantId, moduleName,propertyId);

			Object o = restRepo.fetchResult(uri, mdmsCriteriaReq);
		
			MdmsResponse mdmsResponse = mapper.convertValue(o , MdmsResponse.class);
			if (mdmsResponse.getMdmsRes().get(RL_MASTER_MODULE_NAME) == null) {
				throw new CustomException("FEE_NOT_AVAILABLE", "Property rent/lease fee is not available.");
			}
			JSONArray jsonArray = mdmsResponse.getMdmsRes().get(RL_MASTER_MODULE_NAME).get("RLProperty");

			try {
				calculateAmount = mapper.readValue(jsonArray.toJSONString(),
						mapper.getTypeFactory().constructCollectionType(List.class, RLProperty.class));
			} catch (JsonProcessingException e) {
				log.info("Exception occured while converting amount for allotment " + e);
			}

			return calculateAmount;

		}

		public List<TaxRate> getHeadTaxAmount(RequestInfo requestInfo, String tenantId, String moduleName) {

			List<TaxRate> calculateAmount = new ArrayList<TaxRate>();
			StringBuilder uri = new StringBuilder();
			uri.append(config.getMdmsHost()).append(config.getMdmsEndpoint());

			MdmsCriteriaReq mdmsCriteriaReq = getMdmsRequestTaxHead(requestInfo, tenantId, moduleName);

			Object o = restRepo.fetchResult(uri, mdmsCriteriaReq);
		
			MdmsResponse mdmsResponse = mapper.convertValue(o , MdmsResponse.class);
			if (mdmsResponse.getMdmsRes().get(RL_MASTER_MODULE_NAME) == null) {
				throw new CustomException("FEE_NOT_AVAILABLE", "Property tax fee is not available.");
			}
			JSONArray jsonArray = mdmsResponse.getMdmsRes().get(RL_MASTER_MODULE_NAME).get("TaxRates");

			try {
				calculateAmount = mapper.readValue(jsonArray.toJSONString(),
						mapper.getTypeFactory().constructCollectionType(List.class, TaxRate.class));
			} catch (JsonProcessingException e) {
				log.info("Exception occured while converting amount for allotment: " + e);
			}

			return calculateAmount;

		}

		private MdmsCriteriaReq getMdmsRequestCalculationType(RequestInfo requestInfo, String tenantId, String moduleName,String propertyId) {
			
			MdmsCriteriaReq mdmsCriteriaReq = new MdmsCriteriaReq();
			mdmsCriteriaReq.setRequestInfo(requestInfo); // from your context
			
			MdmsCriteria mdmsCriteria = new MdmsCriteria();
			mdmsCriteria.setTenantId(tenantId);
			
			ModuleDetail moduleDetail = new ModuleDetail();
			moduleDetail.setModuleName("rentAndLease");
			MasterDetail masterDetail = new MasterDetail();
			masterDetail.setName("RLProperty");
			masterDetail.setFilter("$.[?(@.propertyId == '"+propertyId+"')]");
			moduleDetail.setMasterDetails(Arrays.asList(masterDetail));
			mdmsCriteria.setModuleDetails(Arrays.asList(moduleDetail));
			mdmsCriteriaReq.setMdmsCriteria(mdmsCriteria);			
			return mdmsCriteriaReq;
			
		}
		
       private MdmsCriteriaReq getMdmsRequestTaxHead(RequestInfo requestInfo, String tenantId, String moduleName) {
			
			MdmsCriteriaReq mdmsCriteriaReq = new MdmsCriteriaReq();
			mdmsCriteriaReq.setRequestInfo(requestInfo); // from your context
			
			MdmsCriteria mdmsCriteria = new MdmsCriteria();
			mdmsCriteria.setTenantId(tenantId);
			
			ModuleDetail moduleDetail = new ModuleDetail();
			moduleDetail.setModuleName("rentAndLease");
			MasterDetail masterDetail = new MasterDetail();
			masterDetail.setName("TaxRates");
//			masterDetail.setFilter("$.[?(@.propertyId == '"+propertyId+"')]");
			moduleDetail.setMasterDetails(Arrays.asList(masterDetail));
			mdmsCriteria.setModuleDetails(Arrays.asList(moduleDetail));
			mdmsCriteriaReq.setMdmsCriteria(mdmsCriteria);			
			return mdmsCriteriaReq;
			
		}

		public StringBuilder getFetchBillURL(String tenantId, String consumerCode) {
			return new StringBuilder()
					.append(config.getBillingServiceHost())
					.append(config.getBillSearchEndPoint())
					.append(RLConstants.URL_PARAMS_SEPARATER)
					.append(RLConstants.TENANT_ID_FIELD_FOR_SEARCH_URL).append(tenantId)
					.append(RLConstants.SEPARATER)
					.append(RLConstants.CONSUMER_CODE_SEARCH_FIELD_NAME).append(consumerCode)
					.append(RLConstants.SEPARATER)
					.append(RLConstants.BUSINESSSERVICE_FIELD_FOR_SEARCH_URL).append(RLConstants.RL_SERVICE_NAME);
		}

		public StringBuilder getDemandSearchUrl(GetBillCriteria getBillCriteria) {
			StringBuilder builder = new StringBuilder();
			if (CollectionUtils.isEmpty(getBillCriteria.getConsumerCodes())) {
				builder = builder.append(config.getBillingServiceHost())
						.append(config.getDemandSearchEndPoint()).append(RLConstants.URL_PARAMS_SEPARATER)
						.append(RLConstants.TENANT_ID_FIELD_FOR_SEARCH_URL).append(getBillCriteria.getTenantId())
						.append(RLConstants.SEPARATER)
						.append(RLConstants.CONSUMER_CODE_SEARCH_FIELD_NAME).append(getBillCriteria.getApplicationNumber())
						.append(RLConstants.SEPARATER)
						.append(RLConstants.DEMAND_STATUS_PARAM).append(RLConstants.DEMAND_STATUS_ACTIVE);
			}
			else {

				builder = builder.append(config.getBillingServiceHost())
						.append(config.getDemandSearchEndPoint()).append(RLConstants.URL_PARAMS_SEPARATER)
						.append(RLConstants.TENANT_ID_FIELD_FOR_SEARCH_URL).append(getBillCriteria.getTenantId())
						.append(RLConstants.SEPARATER)
						.append(RLConstants.CONSUMER_CODE_SEARCH_FIELD_NAME).append(StringUtils.join(getBillCriteria.getConsumerCodes(), ","))
						.append(RLConstants.SEPARATER)
						.append(RLConstants.PAYMENT_COMPLETED)
						.append(RLConstants.SEPARATER)
						.append(RLConstants.DEMAND_STATUS_PARAM).append(RLConstants.DEMAND_STATUS_ACTIVE);

			}
			if (getBillCriteria.getFromDate() != null && getBillCriteria.getToDate() != null)
				builder = builder.append(RLConstants.DEMAND_START_DATE_PARAM).append(getBillCriteria.getFromDate())
						.append(RLConstants.SEPARATER)
						.append(RLConstants.DEMAND_END_DATE_PARAM).append(getBillCriteria.getToDate())
						.append(RLConstants.SEPARATER);

			return builder;
		}

	}
