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
import org.egov.common.entity.edcr.*;
import org.egov.edcr.utility.DcrConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.egov.edcr.constants.CommonFeatureConstants.*;
import static org.egov.edcr.service.FeatureUtil.addScrutinyDetailtoPlan;
import static org.egov.edcr.service.FeatureUtil.mapReportDetails;

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
			scrutinyDetail8.addColumnHeading(1, DESCRIPTION);
			//scrutinyDetail8.addColumnHeading(3, FLOOR_NO);
			//scrutinyDetail8.addColumnHeading(3, Room);
			//scrutinyDetail8.addColumnHeading(4, REQUIRED);
			scrutinyDetail8.addColumnHeading(2, PROVIDED);
			scrutinyDetail8.addColumnHeading(3, STATUS);
			scrutinyDetail8.setKey(COMMON_ROAD_RESERVE);
        System.out.println("ii" + pl.getRoadReserveFront() +  pl.getRoadReserveRear());
         
        if(pl.getRoadReserveFront() != BigDecimal.ZERO &&  pl.getRoadReserveRear() != BigDecimal.ZERO) {
     	setReportOutputDetails(pl, ROAD_WIDTH_FRONT_AND_REAR,
				"" + pl.getRoadReserveFront() + METER +  AND_SPECIAL_CHAR +  pl.getRoadReserveRear() + METER, EMPTY_STRING, scrutinyDetail8);
		//LOG.info("Room Height Validation True: (Expected/Actual) " + "" + "/" + "");
        // setReportOutputDetails(pl, "Road Width Rear", "" + pl.getRoadReserveRear(), scrutinyDetail);
    
        }
        return pl;
    }
    private void setReportOutputDetails(Plan pl, String ruleDesc,  
			String actual, String status, ScrutinyDetail scrutinyDetail) {
        ReportScrutinyDetail detail = new ReportScrutinyDetail();
        detail.setDescription(ruleDesc);
        detail.setStatus(status);

        Map<String, String> details = mapReportDetails(detail);
        addScrutinyDetailtoPlan(scrutinyDetail, pl, details);
	}

    @Override
    public Map<String, Date> getAmendments() {
        return new LinkedHashMap<>();
    }
}
