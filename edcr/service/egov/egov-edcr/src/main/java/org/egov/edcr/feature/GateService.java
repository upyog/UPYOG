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
public class GateService  extends FeatureProcess {
	private static final Logger LOG = LogManager.getLogger(GateService.class);
    private static final String RULE_32 = "4.4.4 (XI)";
    public static final String MAIN_GATE_DESCRIPTION = "Main gate";
    
	@Override
	    public Plan validate(Plan plan) {
	        return plan;
	    }

	@Override
	public Plan process(Plan plan) {

	    LOG.info("Starting Common Main Gate Processing");

	    scrutinyDetail = new ScrutinyDetail();
	    scrutinyDetail.setKey("Common_MainGate");
	    scrutinyDetail.addColumnHeading(1, RULE_NO);
	    scrutinyDetail.addColumnHeading(2, DESCRIPTION);
	    //scrutinyDetail.addColumnHeading(3, REQUIRED);
	    scrutinyDetail.addColumnHeading(4, PROVIDED);
	    scrutinyDetail.addColumnHeading(5, STATUS);

	    Map<String, String> details = new HashMap<>();
	    details.put(RULE_NO, RULE_32);
	    details.put(DESCRIPTION, MAIN_GATE_DESCRIPTION);

	    HashMap<String, String> errors = new HashMap<>();

	    BigDecimal gateHeight = BigDecimal.ZERO;
	    boolean gateDefined = false;

	    // --- Gate extraction ---
	    if (plan.getGate() != null &&
	    	    plan.getGate().getGates() != null &&
	    	    !plan.getGate().getGates().isEmpty() &&
	    	    plan.getGate().getGates().stream().anyMatch(g -> g != null && g.getHeight() != null)) {

	        LOG.info("Gate information found in the plan. Processing gate measurements.");

	        for (Measurement gate : plan.getGate().getGates()) {
	            if (gate != null && gate.getHeight() != null) {
	                gateHeight = gate.getHeight().setScale(2, RoundingMode.HALF_UP);
	                gateDefined = true;
	                LOG.info("Gate length extracted: {}", gateHeight);
	            }
	        }

	    } else {
	        //LOG.error("Main gate not defined in the drawing.");
	        //errors.put("Main gate not defined", "Main gate not defined in the drawing");
	        //plan.addErrors(errors);
	    }

	    // --- Validation ---
//	    if (!gateDefined) {
//	        //LOG.warn("Gate details missing. Marking as Not Accepted.");
//	        details.put(PROVIDED, "0.00");
//	        details.put(STATUS, Result.Not_Accepted.getResultVal());
//	    } else if (gateHeight.compareTo(BigDecimal.ZERO) > 0) {
//	        LOG.info("Gate length is greater than zero. Accepted.");
//	        details.put(PROVIDED, gateHeight.toPlainString() + " m");
//	        details.put(STATUS, Result.Accepted.getResultVal());
//
//	    } else {
//	        //LOG.warn("Gate length is zero or negative. Not Accepted.");
//	        details.put(PROVIDED, gateHeight.toPlainString() + " m");
//	        details.put(STATUS, Result.Not_Accepted.getResultVal());
//	    }
	    
	    if (gateDefined) {
		    if (gateHeight.compareTo(BigDecimal.ZERO) > 0) {
		        LOG.info("Gate length is greater than zero. Accepted.");
		        details.put(PROVIDED, gateHeight.toPlainString() + " m");
		        details.put(STATUS, Result.Accepted.getResultVal());
		        scrutinyDetail.getDetail().add(details);
			    plan.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
		    } else {
		        //LOG.warn("Gate length is zero or negative. Not Accepted.");
		        details.put(PROVIDED, gateHeight.toPlainString() + " m");
		        details.put(STATUS, Result.Not_Accepted.getResultVal());
		        scrutinyDetail.getDetail().add(details);
			    plan.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
		    }
	    }
	    

	    LOG.info("Completed Main Gate validation.");

	    return plan;
	}

	    
	    @Override
	    public Map<String, Date> getAmendments() {
	        return new LinkedHashMap<>();
	    }
}
