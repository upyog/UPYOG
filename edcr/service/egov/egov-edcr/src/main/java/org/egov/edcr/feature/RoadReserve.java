/*
 * eGov  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
 * accountability and the service delivery of the government  organizations.
 *
 *  Copyright (C) <2019>  eGovernments Foundation
 *
 *  The updated version of eGov suite of products as by eGovernments Foundation
 *  is available at http://www.egovernments.org
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see http://www.gnu.org/licenses/ or
 *  http://www.gnu.org/licenses/gpl.html .
 *
 *  In addition to the terms of the GPL license to be adhered to in using this
 *  program, the following additional terms are to be complied with:
 *
 *      1) All versions of this program, verbatim or modified must carry this
 *         Legal Notice.
 *      Further, all user interfaces, including but not limited to citizen facing interfaces,
 *         Urban Local Bodies interfaces, dashboards, mobile applications, of the program and any
 *         derived works should carry eGovernments Foundation logo on the top right corner.
 *
 *      For the logo, please refer http://egovernments.org/html/logo/egov_logo.png.
 *      For any further queries on attribution, including queries on brand guidelines,
 *         please contact contact@egovernments.org
 *
 *      2) Any misrepresentation of the origin of the material is prohibited. It
 *         is required that all modified versions of this material be marked in
 *         reasonable ways as different from the original version.
 *
 *      3) This license does not grant any rights to any user of the program
 *         with regards to rights under trademark law for use of the trade names
 *         or trademarks of eGovernments Foundation.
 *
 *  In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */

package org.egov.edcr.feature;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.egov.common.entity.dcr.helper.OccupancyHelperDetail;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.PlanInformation;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.Road;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.utility.DcrConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoadReserve extends FeatureProcess {

    @Override
    public Plan validate(Plan pl) {
    	
           return pl;
       }
    @Override
    public Plan process(Plan pl) {
    	 List<Road> roadReserves = pl.getRoadReserves();
    	 
    	 ScrutinyDetail scrutinyDetail8 = new ScrutinyDetail();
			//scrutinyDetail8.addColumnHeading(1, RULE_NO);
			scrutinyDetail8.addColumnHeading(2, DESCRIPTION);
			//scrutinyDetail8.addColumnHeading(3, FLOOR_NO);
			//scrutinyDetail8.addColumnHeading(3, Room);
			//scrutinyDetail8.addColumnHeading(4, REQUIRED);
			scrutinyDetail8.addColumnHeading(5, PROVIDED);
			scrutinyDetail8.addColumnHeading(6, STATUS);
			scrutinyDetail8.setKey("Common_" + "Road Reserve ");
        System.out.println("ii" + pl.getRoadReserveFront() +  pl.getRoadReserveRear());
         
     	setReportOutputDetails(pl, "Road Width Front And Rear",
				"" + pl.getRoadReserveFront() + " & " +  pl.getRoadReserveRear(), "", scrutinyDetail8);
		//LOG.info("Room Height Validation True: (Expected/Actual) " + "" + "/" + "");
        // setReportOutputDetails(pl, "Road Width Rear", "" + pl.getRoadReserveRear(), scrutinyDetail);
    
       
        return pl;
    }
    private void setReportOutputDetails(Plan pl, String ruleDesc,  
			String actual, String status, ScrutinyDetail scrutinyDetail) {
		Map<String, String> details = new HashMap<>();
	//	details.put(RULE_NO, ruleNo);
		details.put(DESCRIPTION, ruleDesc);
		//details.put(FLOOR_NO, ruleDesc);
		
//		details.put(Room, room);
//		details.put(REQUIRED, expected);
		details.put(PROVIDED, actual);
		details.put(STATUS, status);
		scrutinyDetail.getDetail().add(details);
		pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
	}

    @Override
    public Map<String, Date> getAmendments() {
        return new LinkedHashMap<>();
    }
}
