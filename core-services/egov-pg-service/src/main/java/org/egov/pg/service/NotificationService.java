package org.egov.pg.service;

import lombok.extern.slf4j.Slf4j;
import org.egov.pg.config.AppProperties;
import org.egov.pg.models.CollectionPayment;
import org.egov.pg.models.CollectionPaymentRequest;
import org.egov.pg.models.Refund;
import org.egov.pg.models.RefundRequest;
import org.egov.pg.models.Transaction;
import org.egov.pg.utils.NotificationUtil;
import org.egov.pg.web.models.SMSRequest;
import org.egov.pg.web.models.TransactionRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static org.egov.pg.constants.PgConstants.PG_MODULE;
import static org.egov.pg.constants.PgConstants.PG_REFUND_NOTIFICATION;

@Service
@Slf4j
public class NotificationService {

    @Autowired
    private AppProperties appProperties;

    @Autowired
    private NotificationUtil notificationUtil;




    public void smsNotification(TransactionRequest transactionRequest, String topic){

        if (appProperties.getIsSMSEnable() != null && appProperties.getIsSMSEnable()
                && transactionRequest.getRequestInfo().getUserInfo() !=null
                && transactionRequest.getRequestInfo().getUserInfo().getType().equalsIgnoreCase("SYSTEM")) {
            List<SMSRequest> smsRequests = getSmsRequest(transactionRequest, topic);
            if (!CollectionUtils.isEmpty(smsRequests)) {
                notificationUtil.sendSMS(smsRequests);
            }
        }

    }

    public void refundInitiateSmsNotification(RefundRequest refundRequest, String topic) {
        if (appProperties.getIsSMSEnable() != null && appProperties.getIsSMSEnable()
                && refundRequest.getRequestInfo() != null
                && refundRequest.getRequestInfo().getUserInfo() != null
                && refundRequest.getRequestInfo().getUserInfo().getType().equalsIgnoreCase("SYSTEM")) {
            List<SMSRequest> smsRequests = getRefundInitiateSmsRequest(refundRequest, topic);
            if (!CollectionUtils.isEmpty(smsRequests)) {
                notificationUtil.sendSMS(smsRequests);
            }
        }
    }

    public void refundSuccessSmsNotification(CollectionPaymentRequest paymentRequest, String topic) {
        if (appProperties.getIsSMSEnable() != null && appProperties.getIsSMSEnable()
                && paymentRequest.getRequestInfo() != null
                && paymentRequest.getRequestInfo().getUserInfo() != null
                && paymentRequest.getRequestInfo().getUserInfo().getType().equalsIgnoreCase("SYSTEM")) {
            List<SMSRequest> smsRequests = getRefundSuccessSmsRequest(paymentRequest, topic);
            if (!CollectionUtils.isEmpty(smsRequests)) {
                notificationUtil.sendSMS(smsRequests);
            }
        }
    }

    public List<SMSRequest> getSmsRequest(TransactionRequest transactionRequest, String topic){
        List<SMSRequest> smsRequests = new ArrayList<>();
        String txnStatus = String.valueOf(transactionRequest.getTransaction().getTxnStatus());
        String finalMsg = getFinalMessage(transactionRequest, txnStatus, topic);
        String mobileNumber = transactionRequest.getTransaction().getUser().getMobileNumber();
        if(!StringUtils.isEmpty(finalMsg)){
            SMSRequest req = SMSRequest.builder().mobileNumber(mobileNumber).message(finalMsg).build();
            smsRequests.add(req);
        }
        return smsRequests;
    }

    public List<SMSRequest> getRefundInitiateSmsRequest(RefundRequest refundRequest, String topic) {
        List<SMSRequest> smsRequests = new ArrayList<>();
        String refundStatus = String.valueOf(refundRequest.getRefund().getStatus());
        String finalMsg = getRefundFinalMessage(refundRequest, refundStatus, topic);
        String mobileNumber = refundRequest.getRequestInfo().getUserInfo().getMobileNumber();
        if (!StringUtils.isEmpty(finalMsg) && !StringUtils.isEmpty(mobileNumber)) {
            smsRequests.add(SMSRequest.builder().mobileNumber(mobileNumber).message(finalMsg).build());
        }
        return smsRequests;
    }

    public List<SMSRequest> getRefundSuccessSmsRequest(CollectionPaymentRequest paymentRequest, String topic) {
        List<SMSRequest> smsRequests = new ArrayList<>();
        String finalMsg = getPaymentRefundFinalMessage(paymentRequest, topic);
        String mobileNumber = paymentRequest.getPayment().getMobileNumber();
        if (!StringUtils.isEmpty(finalMsg) && !StringUtils.isEmpty(mobileNumber)) {
            smsRequests.add(SMSRequest.builder().mobileNumber(mobileNumber).message(finalMsg).build());
        }
        return smsRequests;
    }

    private String getFinalMessage(TransactionRequest transactionRequest, String txnStatus, String topic) {
        String tenantId = transactionRequest.getTransaction().getTenantId();
        String localizationMessage = notificationUtil.getLocalizationMessages(tenantId, transactionRequest.getRequestInfo(),PG_MODULE);

        Transaction transaction = transactionRequest.getTransaction();

        String message = notificationUtil.getCustomizedMsg(txnStatus, localizationMessage);
        if (message == null) {
            log.info("No message Found for topic : " + topic);
            return message;
        }

        if(message.contains("{amount}"))
            message = message.replace("{amount}",transaction.getTxnAmount());

        if(message.contains("{consumerCode}"))
            message = message.replace("{consumerCode}",transaction.getConsumerCode());

        if(message.contains("{txnId}"))
            message = message.replace("{txnId}",transaction.getTxnId());

        if(message.contains("{Payment failure reason}"))
            message = message.replace("{Payment failure reason}",transaction.getTxnStatusMsg());

        if (message.contains("{payment link}")) {
            String businessService = notificationUtil.getBusinessService(transactionRequest);
            String paymentLink = appProperties.getNotificationHost() + appProperties.getApplicationPayLink();
            paymentLink = paymentLink.replace("$consumerCode", transaction.getConsumerCode());
            paymentLink = paymentLink.replace("$tenantId", transaction.getTenantId());
            paymentLink = paymentLink.replace("$businessService", businessService);
            message = message.replace("{payment link}", notificationUtil.getShortnerURL(paymentLink));
        }

        return message;
    }

    private String getRefundFinalMessage(RefundRequest refundRequest, String refundStatus, String topic) {
        Refund refund = refundRequest.getRefund();
        String localizationMessage = notificationUtil.getLocalizationMessages(refund.getTenantId(),
                refundRequest.getRequestInfo(), PG_MODULE);

        String message = notificationUtil.getCustomizedMsgByNotificationType(PG_REFUND_NOTIFICATION, refundStatus, localizationMessage);
        if (message == null) {
            log.info("No refund message Found for topic : " + topic);
            return null;
        }

        if (message.contains("{amount}")) {
            message = message.replace("{amount}",String.valueOf(refund.getRefundAmount()));
        }

        if (message.contains("{txnId}")) {
            message = message.replace("{txnId}", refund.getRefundId());
        }

        if (message.contains("{Refund failure reason}") && refund.getGatewayStatusMsg() != null) {
            message = message.replace("{Refund failure reason}", refund.getGatewayStatusMsg());
        }

        return message;
    }

    private String getPaymentRefundFinalMessage(CollectionPaymentRequest paymentRequest, String topic) {
        CollectionPayment payment = paymentRequest.getPayment();
        String localizationMessage = notificationUtil.getLocalizationMessages(payment.getTenantId(),
                paymentRequest.getRequestInfo(), PG_MODULE);

        String message = notificationUtil.getCustomizedMsgByNotificationType(PG_REFUND_NOTIFICATION, "SUCCESS", localizationMessage);
        if (message == null) {
            log.info("No payment refund message Found for topic : " + topic);
            return null;
        }

        if (message.contains("{amount}")) {
            message = message.replace("{amount}", String.valueOf(payment.getTotalAmountPaid()));
        }

        if (message.contains("{txnId}")) {
            message = message.replace("{txnId}", payment.getTransactionNumber());
        }

        return message;
    }


}
