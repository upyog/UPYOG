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

package org.egov.common.entity.edcr;

import com.fasterxml.jackson.annotation.JsonValue;

public enum OccupancyType {

	OCCUPANCY_A1("Residential"), // singlefamily,
	OCCUPANCY_A2("Special Residential"), 
	OCCUPANCY_A3("Hostel Educational"), 
	OCCUPANCY_A4("Apartment/Flat"),
	OCCUPANCY_A5("Professional Office"), 
	OCCUPANCY_A6("Independent Floor"),  
	
	OCCUPANCY_B1("Educational"), 
	OCCUPANCY_B2("Educational HighSchool"),
	OCCUPANCY_B3("Higher Educational Institute"), 
	
	OCCUPANCY_C("Nursing Home/Hospital"), 
	OCCUPANCY_C1("Medical IP"), 
	OCCUPANCY_C2("Medical OP"), 
	OCCUPANCY_C3("Medical Admin"), 
	
	OCCUPANCY_D("Assembly"), 
	OCCUPANCY_D1("Assembly Worship"), 
	OCCUPANCY_D2("Bus Terminal"), 
	
	OCCUPANCY_E("Office/Business"), 
	 
	//OCCUPANCY_F1("Commercial Parking Plaza"), OCCUPANCY_F2("Commercial Parking Appurtenant"), OCCUPANCY_F3("Hotels"), OCCUPANCY_F4("Kiosk"),
	OCCUPANCY_F("Commercial"),
	OCCUPANCY_F1("Commercial"),
	OCCUPANCY_F2("Restaurants/ Banquet Halls"),
	OCCUPANCY_F3("Hotels/Motels"),
	OCCUPANCY_F4("FShops/Showrooms/Commercial Complexes etc."),
	OCCUPANCY_F5("Private Offices"),
	OCCUPANCY_F6("Banks"),
	OCCUPANCY_F7("Club/ Guest House / Lodging & Boarding / Service Apartments"),
	OCCUPANCY_F8("Dhaba"),
	OCCUPANCY_F9("Cinemas and Auditoriums"),
	OCCUPANCY_F10("Video Games Parlors"),
	OCCUPANCY_F11("Boutiques"),
	OCCUPANCY_F12("Petrol Filling Station – Four W"),
	OCCUPANCY_F13("Petrol Filling Station – Two W"),
	OCCUPANCY_F14("Petrol Filling Station Service"),
	OCCUPANCY_F15("Petrol Station"),
	OCCUPANCY_F16("Compressed Natural Gas Station"),
	OCCUPANCY_F17("Miniplex/Multiplex"),
	
//	OCCUPANCY_G("Industrial"), 
//	OCCUPANCY_G1("Industrial Plotted - General, Textile, Knitwear, Sports"),
//	OCCUPANCY_G2("Industrial Plotted - Information Technology"),
//	OCCUPANCY_G3("Industrial Flatted"),
	OCCUPANCY_G("Industrial"), 
	OCCUPANCY_G1("Factory"),
	OCCUPANCY_G2("Storage"),
	OCCUPANCY_G3("Hazard Industries"),
	OCCUPANCY_G4("Retail Service Industry"),
	OCCUPANCY_G5("General Industry- Industry Plotted"),
	OCCUPANCY_G6("General industry Flatted"),
	OCCUPANCY_G7("Informational Technology- Industry Plotted"),
	OCCUPANCY_G8("Informational Technology- Flatted"),
	OCCUPANCY_G9("Textile Industry"),
	OCCUPANCY_G10("Knitwear Industry"),
	OCCUPANCY_G11("Sports Industry"),
	OCCUPANCY_G12("Wholesale Trade/ Ware House/ Integrated Freight complex (Standalone)"),
	
	OCCUPANCY_H("Storage"),
	
	OCCUPANCY_I1("Hazardous (I1)"), 
	OCCUPANCY_I2("Hazardous (I2)"),
	
	OCCUPANCY_J("Petrol Pumps"), 
	OCCUPANCY_J1("Filling Station"), 
	OCCUPANCY_J2("Filling Cum Service Station"), 
	OCCUPANCY_J3("Compressed Natural Gas (CNG) Mother Station"),
	
	OCCUPANCY_K("Hotel/Motel"),
	
//	OCCUPANCY_L("Public Building"), 
//	OCCUPANCY_L1("General Public/Semi-public buildings"), 
//	OCCUPANCY_L2("Government Offices / Integrated Office Complex"),
//	OCCUPANCY_L3("Educational Buildings - Nursery School"), 
//	OCCUPANCY_L4("Educational Buildings - Primary School"), 
//	OCCUPANCY_L5("Educational Buildings - Higher Secondary School"),
//	OCCUPANCY_L6("Educational Buildings - College"), 
//	OCCUPANCY_L7("Education & Research Centre (Large Campus)"),
	
	//OCCUPANCY_L("Public Building"), 
	OCCUPANCY_L1("General Public / Semi-public buildings"), 
	OCCUPANCY_L2("Government offices - Integrated office complex buildings"),
	OCCUPANCY_L3("Educational (Nursery School)"), 
	OCCUPANCY_L4("Educational (Primary School)"), 
	OCCUPANCY_L5("Educational (College)"),
	OCCUPANCY_L6("Education & Research Centre (Large Campus)"), 
	OCCUPANCY_L7("Marriage Palace"),
	OCCUPANCY_L8("Nursing Home / Hospital"),
	OCCUPANCY_L9("Creche / Day Care facilities"),
	
	OCCUPANCY_M("Marriage Place"),
	
	OCCUPANCY_R("Mixed Land Use")
	
	;

	
	@JsonValue
	private final String occupancyTypeVal;

	OccupancyType(String aTypeVal) {
		this.occupancyTypeVal = aTypeVal;
	}

	public String getOccupancyType() {
		return occupancyTypeVal;
	}

	public String getOccupancyTypeVal() {
		return occupancyTypeVal;
	}
}
