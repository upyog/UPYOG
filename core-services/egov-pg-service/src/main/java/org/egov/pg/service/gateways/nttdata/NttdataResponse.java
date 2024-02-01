package org.egov.pg.service.gateways.nttdata;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class NttdataResponse {

    private Boolean success;
    private String code;
    private String message;
    private String transactionId;
    private String merchantId;
    private String amount;
    private String providerReferenceId;
    private String paymentState;
    private String payResponseCode;
}
