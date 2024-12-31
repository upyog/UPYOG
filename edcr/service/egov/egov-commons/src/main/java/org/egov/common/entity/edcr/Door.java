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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Door {
	
private static final long serialVersionUID = 105L;
    
private String name;

private List<BigDecimal> widths = new ArrayList<>();

private BigDecimal doorWidth;

private BigDecimal doorHeight;

private List<BigDecimal> nonHabitationDoorWidths = new ArrayList<>();

private BigDecimal  nonHabitationDoorWidth;

private BigDecimal  nonHabitationDoorHeight;

private int colorCode;

public String getName() {
	return name;
}

public void setName(String name) {
	this.name = name;
}

public List<BigDecimal> getNonHabitationDoorWidths() {
	return nonHabitationDoorWidths;
}

public void setNonHabitationDoorWidths(List<BigDecimal> nonHabitationDoorWidths) {
	this.nonHabitationDoorWidths = nonHabitationDoorWidths;
}

public List<BigDecimal> getWidths() {
	return widths;
}

public void setWidths(List<BigDecimal> widths) {
	this.widths = widths;
}


public int getColorCode() {
	return colorCode;
}

public void setColorCode(int colorCode) {
	this.colorCode = colorCode;
}

public BigDecimal getDoorWidth() {
	return doorWidth;
}

public void setDoorWidth(BigDecimal doorWidth) {
	this.doorWidth = doorWidth;
}

public BigDecimal getDoorHeight() {
	return doorHeight;
}

public void setDoorHeight(BigDecimal doorHeight) {
	this.doorHeight = doorHeight;
}

public BigDecimal getNonHabitationDoorWidth() {
	return nonHabitationDoorWidth;
}

public void setNonHabitationDoorWidth(BigDecimal nonHabitationDoorWidth) {
	this.nonHabitationDoorWidth = nonHabitationDoorWidth;
}

public BigDecimal getNonHabitationDoorHeight() {
	return nonHabitationDoorHeight;
}

public void setNonHabitationDoorHeight(BigDecimal nonHabitationDoorHeight) {
	this.nonHabitationDoorHeight = nonHabitationDoorHeight;
}


}