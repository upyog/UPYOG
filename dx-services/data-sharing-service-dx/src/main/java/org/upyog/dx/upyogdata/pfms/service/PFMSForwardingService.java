package org.upyog.dx.upyogdata.pfms.service;

public interface PFMSForwardingService {
    void forwardInitiatedTransactions(String triggeredBy);
    void retryFailedTransactions(String triggeredBy);
}
