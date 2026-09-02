package org.egov.refund.service.impl;


import org.egov.refund.Repository.RefundSequenceRepository;
import org.egov.refund.service.RefundNumberService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RefundNumberServiceImpl implements RefundNumberService {

    private final RefundSequenceRepository refundSequenceRepository;

    public RefundNumberServiceImpl(
            RefundSequenceRepository refundSequenceRepository) {
        this.refundSequenceRepository = refundSequenceRepository;
    }

    @Transactional
    public String generateRefundNo(
            String moduleName,
            String businessService,
            String consumerCode) {

        Long sequence = refundSequenceRepository.getNextSequence(
                moduleName,
                businessService,
                consumerCode
        );

        return String.format(
                "REF-%s-%s-%06d",
                moduleName,
                consumerCode,
                sequence
        );
    }

   
}