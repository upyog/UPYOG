package org.upyog.sv.service;

import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.upyog.sv.constants.StreetVendingConstants;
import org.upyog.sv.util.EncryptionDecryptionUtil;
import org.upyog.sv.web.models.StreetVendingDetail;
import org.upyog.sv.web.models.StreetVendingRequest;
import org.upyog.sv.web.models.VendorDetail;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class StreetVendingEncryptionService {

    @Autowired
    private EncryptionDecryptionUtil encryptionDecryptionUtil;

    public StreetVendingDetail encryptObject(StreetVendingRequest streetVendingRequest) {
        List<VendorDetail> vendorDetails = streetVendingRequest.getStreetVendingDetail().getVendorDetail();
        
        if (vendorDetails == null || vendorDetails.isEmpty()) {
            log.warn("No Vendor Details available for encryption.");
            return streetVendingRequest.getStreetVendingDetail();
        }

        VendorDetail vendorDetail = vendorDetails.get(0);
        log.info("Applicant detail before encryption: {}", vendorDetail.getMobileNo());

        VendorDetail encryptedDetail = encryptionDecryptionUtil.encryptObject(
                vendorDetail,
                StreetVendingConstants.SV_APPLICANT_DETAIL_ENCRYPTION_KEY,
                VendorDetail.class
        );

        log.info("Applicant detail after encryption: {}", encryptedDetail.getMobileNo());
        vendorDetails.set(0, encryptedDetail); // Updating the list
        streetVendingRequest.getStreetVendingDetail().setVendorDetail(vendorDetails);

        return streetVendingRequest.getStreetVendingDetail();
    }

    public StreetVendingDetail decryptObject(StreetVendingDetail streetVendingDetail, RequestInfo requestInfo) {
        if (streetVendingDetail == null || streetVendingDetail.getVendorDetail() == null) {
            log.warn("No StreetVendingDetail or VendorDetail available for decryption.");
            return streetVendingDetail;
        }

        VendorDetail vendorDetail = streetVendingDetail.getVendorDetail().get(0);
        log.info("Applicant detail before decryption: {}", vendorDetail.getMobileNo());

        VendorDetail decryptedDetail = encryptionDecryptionUtil.decryptObject(
                vendorDetail,
                StreetVendingConstants.SV_APPLICANT_DETAIL_PLAIN_DECRYPTION_KEY,
                VendorDetail.class,
                requestInfo
        );

        log.info("Applicant detail after decryption: {}", decryptedDetail.getMobileNo());
        streetVendingDetail.getVendorDetail().set(0, decryptedDetail);

        return streetVendingDetail;
    }

    public List<StreetVendingDetail> decryptObject(List<StreetVendingDetail> streetVendingDetails, RequestInfo requestInfo) {
        if (streetVendingDetails == null || streetVendingDetails.isEmpty()) {
            log.warn("No StreetVendingDetails available for decryption.");
            return streetVendingDetails;
        }

        streetVendingDetails.forEach(detail -> {
            if (detail.getVendorDetail() != null && !detail.getVendorDetail().isEmpty()) {
                VendorDetail vendorDetail = detail.getVendorDetail().get(0);
                log.info("Applicant detail before decryption: {}", vendorDetail.getMobileNo());

                VendorDetail decryptedDetail = encryptionDecryptionUtil.decryptObject(
                        vendorDetail,
                        StreetVendingConstants.SV_APPLICANT_DETAIL_PLAIN_DECRYPTION_KEY,
                        VendorDetail.class,
                        requestInfo
                );

                log.info("Applicant detail after decryption: {}", decryptedDetail.getMobileNo());
                detail.getVendorDetail().set(0, decryptedDetail);
            }
        });

        return streetVendingDetails;
    }
}
