package org.upyog.dx.upyogdata.pfms.service;

import org.upyog.dx.upyogdata.pfms.models.PFMSCreateTransactionRequest;

/**
 * PFMS transaction ingest from state ULB systems.
 */
public interface PFMSService {

    /**
     * Validates, enriches, and publishes PFMS voucher data for asynchronous persistence.
     *
     * @param request inbound API contract payload
     * @return {@link org.upyog.dx.upyogdata.pfms.models.PFMSTransactionResponse} with assigned ids
     */
    Object createTransaction(PFMSCreateTransactionRequest request);
}
