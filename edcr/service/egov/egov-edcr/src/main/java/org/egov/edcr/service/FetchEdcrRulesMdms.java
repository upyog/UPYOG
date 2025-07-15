package org.egov.edcr.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.egov.common.constants.MdmsFeatureConstants;
import org.egov.common.entity.edcr.EdcrMasterConfig;
import org.egov.common.entity.edcr.MdmsResponse;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.RuleKey;
import org.egov.commons.mdms.BpaMdmsUtil;
import org.egov.edcr.constants.EdcrRulesMdmsConstants;
//import org.egov.infra.mdms.controller.MDMSController;
import org.egov.infra.microservice.models.RequestInfo;
import org.jfree.util.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;


@Service
public class FetchEdcrRulesMdms {

	@Autowired
	private BpaMdmsUtil bpaMdmsUtil;

	private static Logger LOG = LogManager.getLogger(EdcrApplicationService.class);
	private Map<RuleKey, List<Object>> ruleMap = new HashMap<>();
	private boolean isMdmsReloaded = false; // to track if fallback MDMS call was already done


	public String getOccupancyName(Plan pl) {
		if (pl.getPlanInformation() == null || pl.getPlanInformation().getOccupancy() == null) {
			return null;
		}

		String occupancyName = pl.getPlanInformation().getOccupancy();

		Log.info("Occupancy Name : " + occupancyName);

		if (occupancyName.equalsIgnoreCase(EdcrRulesMdmsConstants.RESIDENTIAL)) {
			return EdcrRulesMdmsConstants.RESIDENTIAL;
		} else if (occupancyName.equalsIgnoreCase(EdcrRulesMdmsConstants.INDUSTRIAL)) {
			return EdcrRulesMdmsConstants.INDUSTRIAL;
		} else if (occupancyName.equalsIgnoreCase(EdcrRulesMdmsConstants.COMMERCIAL)) {
			return EdcrRulesMdmsConstants.COMMERCIAL;
		} else if (occupancyName.equalsIgnoreCase(EdcrRulesMdmsConstants.STORAGE)) {
			return EdcrRulesMdmsConstants.STORAGE;
		} else if (occupancyName.equalsIgnoreCase(EdcrRulesMdmsConstants.ASSEMBLY)) {
			return EdcrRulesMdmsConstants.ASSEMBLY;
		} else if (occupancyName.equalsIgnoreCase(EdcrRulesMdmsConstants.BUSINESS)) {
			return EdcrRulesMdmsConstants.BUSINESS;
		} else if (occupancyName.equalsIgnoreCase(EdcrRulesMdmsConstants.HAZARDOUS)) {
			return EdcrRulesMdmsConstants.HAZARDOUS;
		} else if (occupancyName.equalsIgnoreCase(EdcrRulesMdmsConstants.EDUCATIONAL)) {
			return EdcrRulesMdmsConstants.EDUCATIONAL;
		} else if (occupancyName.equalsIgnoreCase(EdcrRulesMdmsConstants.MEDICAL)) {
			return EdcrRulesMdmsConstants.MEDICAL;
		}

		return occupancyName;
	}
	
	
	private List<Map<String, Object>> riskTypeRules = new ArrayList<>();

	public String getRiskType(Plan pl) {
	    if (riskTypeRules.isEmpty()) {
	        ObjectMapper mapper = new ObjectMapper();

	        Object mdmsData = bpaMdmsUtil.mDMSCall(new RequestInfo(), EdcrRulesMdmsConstants.STATE);
	        MdmsResponse mdmsResponse = mapper.convertValue(mdmsData, MdmsResponse.class);
	        JSONArray jsonArray = mdmsResponse.getMdmsRes().get(EdcrRulesMdmsConstants.BPA).get(EdcrRulesMdmsConstants.RISK_TYPE_COMPUTATION);

	        for (int i = 0; i < jsonArray.size(); i++) {
	            @SuppressWarnings("unchecked")
	            Map<String, Object> rule = (Map<String, Object>) jsonArray.get(i);
	            riskTypeRules.add(rule);
	        }
	    }

	    BigDecimal plotArea = pl.getPlot().getArea();
	    BigDecimal height = pl.getVirtualBuilding().getBuildingHeight();

	    for (Map<String, Object> rule : riskTypeRules) {
	        BigDecimal fromPlotArea = new BigDecimal(rule.get("fromPlotArea").toString());
	        BigDecimal toPlotArea = new BigDecimal(rule.get("toPlotArea").toString());
	        BigDecimal fromHeight = new BigDecimal(rule.get("fromBuildingHeight").toString());
	        BigDecimal toHeight = new BigDecimal(rule.get("toBuildingHeight").toString());

	        boolean plotAreaInRange = plotArea.compareTo(fromPlotArea) >= 0 && plotArea.compareTo(toPlotArea) < 0;
	        //boolean heightInRange = height.compareTo(fromHeight) >= 0 && height.compareTo(toHeight) < 0;

	        if (plotAreaInRange) { //add height if required
	            return rule.get("riskType").toString();
	        }
	    }

	    return null;
	}

}
