package org.egov.schedulerservice.contract.bill;

import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.egov.schedulerservice.contract.bill.Bill.StatusEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class GenerateBillCriteria {

//	@SafeHtml
	@NotNull
	@Size(max = 256)
	private String tenantId;

//	@SafeHtml
	@Size(max = 64)
	private String demandId;
	
	private Set<String> consumerCode;

//	@SafeHtml
	@NotNull
	@Size(max = 256)
	private String businessService;

	@Email
	private String email;

	@Pattern(regexp = "^[0-9]{10}$", message = "MobileNumber should be 10 digit number")
	private String mobileNumber;
	
	public DemandCriteria toDemandCriteria() {
		
		Set<String> consumerCodeSet = new HashSet<>();
		consumerCodeSet.addAll(consumerCode);
		
		Set<String> demandIdSet = new HashSet<>();
		demandIdSet.add(demandId);
		
		return DemandCriteria.builder()
				.businessService(businessService)
				.consumerCode(consumerCodeSet)
				.mobileNumber(mobileNumber)
				.isPaymentCompleted(false)
				.demandId(demandIdSet)
				.tenantId(tenantId)
				.email(email)
				.build();
	}
	
	/**
	 * Converts Gen Bill criteria to search bill criteria to fetch only active bills
	 * 
	 * @return BillSearchCriteria
	 */
	public BillSearchCriteria toBillSearchCriteria() {

		return BillSearchCriteria.builder()
				.consumerCode(consumerCode)
				.mobileNumber(mobileNumber)
				.status(StatusEnum.ACTIVE)
				.service(businessService)
				.tenantId(tenantId)
				.isOrderBy(true)
				.email(email)
				.build();
	}

}
