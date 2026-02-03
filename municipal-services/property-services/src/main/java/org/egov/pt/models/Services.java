package org.egov.pt.models;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

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
