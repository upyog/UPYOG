package org.egov.pt.models;

import org.egov.pt.models.Assessment.ModeOfPayment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class CurrentYearAssesmentDetails {
	private ModeOfPayment modeOfPaymentSelected;
	private String finalBillGenerationDate;
	private boolean canBifurcate;
	private boolean asmtFoundForCurrentYear;
	private boolean asmtActiveStatus;
	
	

	
	
}
