package org.egov.refund.web.contracat;



import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.JsonNode;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaymentWorkflow {

    
    @NotNull
    private String paymentId;

    @NotNull
    private PaymentAction action;

    
    @NotNull
    private String tenantId;

    
    private String reason;

    private JsonNode additionalDetails;

    /**
     * Current status of the transaction
     */
    public enum PaymentAction {
        CANCEL("CANCEL"),
        DISHONOUR("DISHONOUR"),
        REMIT("REMIT"),
    	REFUND("REFUND");

        private String value;

        PaymentAction(String value) {
            this.value = value;
        }

        @JsonCreator
        public static PaymentAction fromValue(String text) {
            for (PaymentAction b : PaymentAction.values()) {
                if (String.valueOf(b.value).equals(text)) {
                    return b;
                }
            }
            return null;
        }

        @Override
        @JsonValue
        public String toString() {
            return String.valueOf(value);
        }
    }


}
