package org.egov.finance.master.model;

import java.util.ArrayList;
import java.util.List;

import org.egov.finance.master.customannotation.SafeHtml;

import lombok.Data;

@Data
public class AccountDetailModel {
	
	private Long id;
    private Long orderId;

    @SafeHtml
    private String glcode;

    private Double debitAmount;
    private Double creditAmount;

    private Long functionId;

    private List<SubledgerDetailModel> subledgerDetails = new ArrayList<>();

}
