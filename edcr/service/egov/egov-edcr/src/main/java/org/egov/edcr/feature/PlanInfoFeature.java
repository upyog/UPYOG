package org.egov.edcr.feature;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.egov.common.entity.edcr.Measurement;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.springframework.stereotype.Service;

@Service
public class PlanInfoFeature extends FeatureProcess {
	
	private static final Logger LOG = LogManager.getLogger(PlanInfoFeature.class);
    private static final String RULE_32 = "4.4.4 (XI)";
    public static final String SITE_DIMENSION_DESCRIPTION = "Site dimensions";

    @Override
    public Plan validate(Plan pl) {
        // TODO Auto-generated method stub
        return pl;
    }

    @Override
    public Plan process(Plan plan) {
    	
	    LOG.info("Starting Common PlanInfo features Processing");

	    scrutinyDetail = new ScrutinyDetail();
	    scrutinyDetail.setKey("Common_SiteDimensions");
	    scrutinyDetail.addColumnHeading(1, RULE_NO);
	    scrutinyDetail.addColumnHeading(2, DESCRIPTION);
	    //scrutinyDetail.addColumnHeading(3, REQUIRED);
	    scrutinyDetail.addColumnHeading(4, PROVIDED);
	    scrutinyDetail.addColumnHeading(5, STATUS);

	    Map<String, String> details = new HashMap<>();
	    details.put(RULE_NO, "-");
	    details.put(DESCRIPTION, SITE_DIMENSION_DESCRIPTION);

	    HashMap<String, String> errors = new HashMap<>();
	    
	    if (plan.getPlanInformation() != null 
	    		&& plan.getPlanInformation() != null
	    		&& plan.getPlanInformation().getDepthOfPlot() != null
	    		&& plan.getPlanInformation().getDepthOfPlot().compareTo(BigDecimal.ZERO) > 0
	    		&& plan.getPlanInformation().getWidthOfPlot() != null
	    		&& plan.getPlanInformation().getWidthOfPlot().compareTo(BigDecimal.ZERO) > 0){
	        LOG.info("Plan information found in the plan. Processing site dimension.");
	        String avgWidAndDep=  "AVERAGE WIDTH ("+ plan.getPlanInformation().getWidthOfPlot().toPlainString()+")"
	        		+ " & DEPTH(" + plan.getPlanInformation().getDepthOfPlot().toPlainString() +")";
	        details.put(PROVIDED, avgWidAndDep);
	        details.put(STATUS, Result.Accepted.getResultVal());	        
	    } else {
	        LOG.error("Site Dimension not defined in the drawing.");
	        errors.put("Site Dimension not defined", "Site Dimensions (Depth and Widht) not defined in the drawing");
	        plan.addErrors(errors);
	    }

	    scrutinyDetail.getDetail().add(details);
	    plan.getReportOutput().getScrutinyDetails().add(scrutinyDetail);

	    LOG.info("Completed Site Dimension validation.");
    	
        return plan;
    }

    @Override
    public Map<String, Date> getAmendments() {
        return new LinkedHashMap<>();
    }

}
