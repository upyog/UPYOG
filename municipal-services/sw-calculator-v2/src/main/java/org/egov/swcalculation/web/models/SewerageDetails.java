package org.egov.swcalculation.web.models;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@EqualsAndHashCode
public class SewerageDetails {
	
	private String connectionNo;
	
	private long  connectionExecutionDate;

}
