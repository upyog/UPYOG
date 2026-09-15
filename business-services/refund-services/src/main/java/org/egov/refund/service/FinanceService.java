package org.egov.refund.service;


import org.egov.refund.web.contracat.RefundRequest;

public interface FinanceService {

    void processRefund(RefundRequest refund);
}