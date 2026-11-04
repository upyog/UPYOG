package org.upyog.dx.upyogdata.pfms.models;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

/**
 *  API response returned after a successfull data pushed by state.
 */
@Getter
@Setter
@Builder
public class PFMSTransactionResponse {
    private ResponseInfo responseInfo;
    private List<TransactionResult> transactions;

    /**
     * acknowledgement for a single transaction.
     * Returned as part of the batch response — does not represent DB state,
     * only confirms successful Kafka publish.
     */
    @Getter
    @Builder
    public static class TransactionResult {
        private String id;
        private String voucherNumber;
        private String status;
    }
}
