package org.egov.pt.models;

import java.math.BigInteger;

import lombok.Data;

@Data
public class Services {
	
	private BigInteger totalPropertiesRegistered=BigInteger.ZERO;
	private BigInteger propertiesPendingWithDocVerifier=BigInteger.ZERO;
	private BigInteger propertiesPendingWithFieldInspector=BigInteger.ZERO;
	private BigInteger propertiesPendingWithApprover=BigInteger.ZERO;
	private BigInteger propertiesRejected=BigInteger.ZERO;
	private BigInteger propertiesApproved=BigInteger.ZERO;
	private BigInteger propertiesSelfAssessed=BigInteger.ZERO;
	private BigInteger propertiesPendingSelfAssessment=BigInteger.ZERO;
	private BigInteger propertiesPaid=BigInteger.ZERO;
	private BigInteger propertiesWithAppealSubmitted=BigInteger.ZERO;
	private BigInteger appealsPending=BigInteger.ZERO;

}
