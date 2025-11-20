package org.upyog.sv.service;

import java.util.List;
import java.util.stream.Collectors;

import org.egov.common.contract.request.RequestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.upyog.sv.constants.StreetVendingConstants;
import org.upyog.sv.util.EncryptionDecryptionUtil;
import org.upyog.sv.web.models.BankDetail;
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
		BankDetail bankDetail = streetVendingRequest.getStreetVendingDetail().getBankDetail();

		if (vendorDetails == null || vendorDetails.isEmpty()) {
			log.warn("No Vendor Details available for encryption.");
			return streetVendingRequest.getStreetVendingDetail();
		}

		log.info("Applicant detail before encryption: {}", vendorDetails);

		List<VendorDetail> encryptedVendorDetails = vendorDetails.stream()
				.map(vendorDetail -> encryptionDecryptionUtil.encryptObject(vendorDetail,
						StreetVendingConstants.SV_APPLICANT_DETAIL_ENCRYPTION_KEY, VendorDetail.class))
				.collect(Collectors.toList());
		if (bankDetail != null) {
			BankDetail encryptedBankDetail = encryptionDecryptionUtil.encryptObject(bankDetail,
					StreetVendingConstants.SV_APPLICANT_DETAIL_ENCRYPTION_KEY, BankDetail.class);

			streetVendingRequest.getStreetVendingDetail().setBankDetail(encryptedBankDetail);
		}
		streetVendingRequest.getStreetVendingDetail().setVendorDetail(encryptedVendorDetails);

		return streetVendingRequest.getStreetVendingDetail();
	}

	public StreetVendingDetail decryptObject(StreetVendingDetail streetVendingDetail, RequestInfo requestInfo) {
		if (streetVendingDetail == null || streetVendingDetail.getVendorDetail() == null) {
			log.warn("No StreetVendingDetail or VendorDetail available for decryption.");
			return streetVendingDetail;
		}

		List<VendorDetail> vendorDetails = streetVendingDetail.getVendorDetail();
		log.info("Applicant detail before decryption: {}", vendorDetails);

		List<VendorDetail> decryptedDetail = encryptionDecryptionUtil.decryptObject(vendorDetails,
				StreetVendingConstants.SV_APPLICANT_DETAIL_PLAIN_DECRYPTION_KEY, VendorDetail.class, requestInfo);

		streetVendingDetail.setVendorDetail(decryptedDetail);

		BankDetail bankDetail = streetVendingDetail.getBankDetail();
		if (bankDetail != null) {
			BankDetail decryptedBankDetail = encryptionDecryptionUtil.decryptObject(bankDetail,
					StreetVendingConstants.SV_APPLICANT_DETAIL_PLAIN_DECRYPTION_KEY, BankDetail.class, requestInfo);

			streetVendingDetail.setBankDetail(decryptedBankDetail);
		}
		return streetVendingDetail;
	}

	public List<StreetVendingDetail> decryptObject(List<StreetVendingDetail> streetVendingDetails,
			RequestInfo requestInfo) {
		if (streetVendingDetails == null || streetVendingDetails.isEmpty()) {
			log.warn("No StreetVendingDetails available for decryption.");
			return streetVendingDetails;
		}

		for (StreetVendingDetail detail : streetVendingDetails) {

			if (detail.getVendorDetail() != null && !detail.getVendorDetail().isEmpty()) {
				List<VendorDetail> vendorDetails = detail.getVendorDetail();
				log.info("Applicant detail before decryption: {}", vendorDetails);

				List<VendorDetail> decryptedDetail = encryptionDecryptionUtil.decryptObject(vendorDetails,
						StreetVendingConstants.SV_APPLICANT_DETAIL_PLAIN_DECRYPTION_KEY, VendorDetail.class,
						requestInfo);

				detail.setVendorDetail(decryptedDetail);
			}

			if (detail.getBankDetail() != null) {
				BankDetail bankDetail = detail.getBankDetail();
				BankDetail decryptedBankDetail = encryptionDecryptionUtil.decryptObject(bankDetail,
						StreetVendingConstants.SV_APPLICANT_DETAIL_PLAIN_DECRYPTION_KEY, BankDetail.class, requestInfo);

				log.info("Bank detail after decryption: {}", decryptedBankDetail.getAccountNumber());
				detail.setBankDetail(decryptedBankDetail);
			}
		}

		return streetVendingDetails;
	}
}
