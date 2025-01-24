package org.egov.ptr.util;

import static org.egov.ptr.util.PTRConstants.CALCULATION_TYPE;
import static org.egov.ptr.util.PTRConstants.PET_MASTER_MODULE_NAME;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.mdms.model.MasterDetail;
import org.egov.mdms.model.MdmsCriteria;
import org.egov.mdms.model.MdmsCriteriaReq;
import org.egov.mdms.model.MdmsResponse;
import org.egov.mdms.model.ModuleDetail;
import org.egov.ptr.config.PetConfiguration;
import org.egov.ptr.models.CalculationType;
import org.egov.ptr.repository.ServiceRequestRepository;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;

@Component
@Slf4j
public class PetUtil extends CommonUtils {

	@Autowired
	private PetConfiguration configs;

	@Autowired
	private ServiceRequestRepository restRepo;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private PetConfiguration config;

	/**
	 * Utility method to fetch bill for validation of payment
	 *
	 * @param propertyId
	 * @param tenantId
	 * @param request    //
	 */

	public List<CalculationType> getcalculationType(RequestInfo requestInfo, String tenantId, String moduleName) {

		List<CalculationType> calculationTypes = new ArrayList<CalculationType>();
		StringBuilder uri = new StringBuilder();
		uri.append(config.getMdmsHost()).append(config.getMdmsEndpoint());

		MdmsCriteriaReq mdmsCriteriaReq = getMdmsRequestCalculationType(requestInfo, tenantId, moduleName);
		MdmsResponse mdmsResponse = mapper.convertValue(restRepo.fetchResult(uri, mdmsCriteriaReq), MdmsResponse.class);
		if (mdmsResponse.getMdmsRes().get(PET_MASTER_MODULE_NAME) == null) {
			throw new CustomException("FEE_NOT_AVAILABLE", "Pet registration fee not available.");
		}
		JSONArray jsonArray = mdmsResponse.getMdmsRes().get(PET_MASTER_MODULE_NAME).get(CALCULATION_TYPE);

		try {
			calculationTypes = mapper.readValue(jsonArray.toJSONString(),
					mapper.getTypeFactory().constructCollectionType(List.class, CalculationType.class));
		} catch (JsonProcessingException e) {
			log.info("Exception occured while converting calculation type  for pet registration: " + e);
		}

		return calculationTypes;

	}

	private MdmsCriteriaReq getMdmsRequestCalculationType(RequestInfo requestInfo, String tenantId, String moduleName) {

		MasterDetail masterDetail = new MasterDetail();
		masterDetail.setName(CALCULATION_TYPE);
		List<MasterDetail> masterDetailList = new ArrayList<>();
		masterDetailList.add(masterDetail);

		ModuleDetail moduleDetail = new ModuleDetail();
		moduleDetail.setMasterDetails(masterDetailList);
		moduleDetail.setModuleName(moduleName);
		List<ModuleDetail> moduleDetailList = new ArrayList<>();
		moduleDetailList.add(moduleDetail);

		MdmsCriteria mdmsCriteria = new MdmsCriteria();
		mdmsCriteria.setTenantId(tenantId);
		mdmsCriteria.setModuleDetails(moduleDetailList);

		MdmsCriteriaReq mdmsCriteriaReq = new MdmsCriteriaReq();
		mdmsCriteriaReq.setMdmsCriteria(mdmsCriteria);
		mdmsCriteriaReq.setRequestInfo(requestInfo);

		return mdmsCriteriaReq;
	}

	public long calculateNextMarch31At8PMInEpoch() {
		LocalDate today = LocalDate.now();
		LocalDate nextMarch31 = LocalDate.of(today.getYear(), Month.MARCH, 31);
		if (today.isAfter(nextMarch31)) {
			nextMarch31 = nextMarch31.plusYears(1);
		}
		LocalDateTime nextMarch31At8PM = LocalDateTime.of(nextMarch31, LocalTime.of(20, 0));
		return nextMarch31At8PM.atZone(ZoneId.systemDefault()).toEpochSecond();
	}
	
	public long getTodaysEpoch() {
		  LocalDate today = LocalDate.now();
		  System.out.println("Current date: " + today);
		  LocalDateTime startOfDay = today.atStartOfDay();
		  System.out.println("Start of day: " + startOfDay);
		  return startOfDay.atZone(ZoneId.systemDefault()).toEpochSecond();
		}
	
	public static long getFinancialYearStart() {
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);

        // If current month is Jan-March, start year should be last year (e.g., FY 2023-2024 starts in 2023)
        if (currentMonth < Calendar.APRIL) {
            currentYear -= 1;
        }

        calendar.set(currentYear, Calendar.APRIL, 1, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTimeInMillis();
    }

    public static long getFinancialYearEnd() {
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);

        // If current month is Jan-March, end year should be current year (e.g., FY 2023-2024 ends in 2024)
        if (currentMonth < Calendar.APRIL) {
            currentYear -= 1;
        }

        calendar.set(currentYear + 1, Calendar.MARCH, 31, 23, 59, 59);
        calendar.set(Calendar.MILLISECOND, 999);

        return calendar.getTimeInMillis();
    }
}
