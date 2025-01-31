package org.egov.pg.service.gateways.paytm;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data // Lombok annotation to generate getters, setters, toString, equals, and hashCode
public class PaymentStatusResponse {
    private Head head;
    private Body body;

    @Data
    public static class Head {
        private String responseTimestamp;
        private String channelId;
        private String version;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true) // Ignores unknown JSON properties
    public static class Body {
        private String paytmMid;
        private String paytmTid;
        private String transactionDateTime;
        private String merchantTransactionId;
        private String merchantReferenceNo;
        private String transactionAmount;
        private String acquirementId;
        private String retrievalReferenceNo;
        private String authCode;
        private String issuerMaskCardNo;
        private String issuingBankName;
        private String originalAmount;
        private String issuingBankId;
        private String price;
        private String bankResponseCode;
        private String bankResponseMessage;
        private String bankMid;
        private String bankTid;
        private ResultInfo resultInfo;
        private Object merchantExtendedInfo; // Assuming it can be null
        private Object extendedInfo; // Assuming it can be null
        private String aid;
        private String payMethod;
        private String cardType;
        private String cardScheme;
        private String acquiringBank;
        private ProductDetails productDetails;
        private EmiDetails emiDetails;
        private CashbackDetails cashbackDetails;
    }

    @Data
    public static class ResultInfo {
        private String resultStatus;
        private String resultCode;
        private String resultMsg;
        private String resultCodeId;
    }

    @Data
    public static class ProductDetails {
        private String manufacturer;
        private String category;
        private String productSerialNoType;
        private String productSerialNoValue;
        private String productCode;
        private String modelName;
    }

    @Data
    public static class EmiDetails {
        private String txnType;
        private String baseAmount;
        private String tenure;
        private String emiInterestRate;
        private String emiMonthlyAmount;
        private String emiTotalAmount;
    }

    @Data
    public static class CashbackDetails {
        private String bankOfferApplied;
        private String bankOfferType;
        private String bankOfferAmount;
        private String subventionCreated;
        private String subventionType;
        private String subventionOfferAmount;
    }
}
