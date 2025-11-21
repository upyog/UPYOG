package org.upyog.chb.service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.egov.common.contract.request.RequestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.upyog.chb.constants.CommunityHallBookingConstants;
import org.upyog.chb.util.EncryptionDecryptionUtil;
import org.upyog.chb.web.models.ApplicantDetail;
import org.upyog.chb.web.models.CommunityHallBookingDetail;
import org.upyog.chb.web.models.CommunityHallBookingRequest;

import lombok.extern.slf4j.Slf4j;

/**
 * This service class handles encryption and decryption operations for sensitive data
 * in the Community Hall Booking module.
 * 
 * Purpose:
 * - To ensure the security and confidentiality of sensitive data, such as applicant details.
 * - To provide reusable methods for encrypting and decrypting data in the module.
 * 
 * Dependencies:
 * - EncryptionDecryptionUtil: Utility class for performing encryption and decryption operations.
 * - CommunityHallBookingConstants: Provides constants such as encryption keys.
 * 
 * Features:
 * - Encrypts sensitive fields in booking requests, such as applicant mobile numbers.
 * - Decrypts sensitive fields when required for processing or display.
 * - Logs the state of data before and after encryption or decryption for debugging purposes.
 * 
 * Methods:
 * 1. encryptObject:
 *    - Encrypts sensitive fields in the CommunityHallBookingRequest object.
 *    - Uses the encryption key defined in CommunityHallBookingConstants.
 * 
 * 2. decryptObject:
 *    - Decrypts sensitive fields in the CommunityHallBookingRequest object.
 *    - Ensures that the original data is restored for processing or display.
 * 
 * Usage:
 * - This class is automatically managed by Spring and injected wherever encryption or decryption
 *   operations are required.
 * - It ensures consistent and secure handling of sensitive data across the module.
 */
@Service
@Slf4j
public class CHBEncryptionService {

	@Autowired
	private EncryptionDecryptionUtil encryptionDecryptionUtil;

	public CommunityHallBookingDetail encryptObject(CommunityHallBookingRequest bookingRequest) {
		ApplicantDetail applicantDetail = bookingRequest.getHallsBookingApplication().getApplicantDetail();
		log.info("Applicant detail before encyption : " + applicantDetail.getApplicantMobileNo());
		applicantDetail = encryptionDecryptionUtil.encryptObject(applicantDetail,
				CommunityHallBookingConstants.CHB_APPLICANT_DETAIL_ENCRYPTION_KEY, ApplicantDetail.class);
		log.info("Applicant detail after encyption : " + applicantDetail.getApplicantMobileNo());
		bookingRequest.getHallsBookingApplication().setApplicantDetail(applicantDetail);
		return bookingRequest.getHallsBookingApplication();
	}
	
	
	public CommunityHallBookingDetail decryptObject(CommunityHallBookingDetail bookingDetail, RequestInfo requestInfo) {
		ApplicantDetail applicantDetail = bookingDetail.getApplicantDetail();
		log.info("Applicant detail before decryption : " + applicantDetail.getApplicantMobileNo());
		applicantDetail = encryptionDecryptionUtil.decryptObject(applicantDetail, 
				CommunityHallBookingConstants.CHB_APPLICANT_DETAIL_PLAIN_DECRYPTION_KEY, ApplicantDetail.class, requestInfo);
				
		log.info("Applicant detail after decryption : " + applicantDetail.getApplicantMobileNo());
		bookingDetail.setApplicantDetail(applicantDetail);

		return bookingDetail;
	}
	
	public List<CommunityHallBookingDetail> decryptObject(List<CommunityHallBookingDetail> bookingDetails, RequestInfo requestInfo) {
		Map<String, CommunityHallBookingDetail> applicantDetailMap = bookingDetails.stream().collect(
				Collectors.toMap(CommunityHallBookingDetail::getBookingId, Function.identity()));
		
		List<ApplicantDetail> applicantDetails = bookingDetails.stream().map(detail -> detail.getApplicantDetail()).collect(Collectors.toList());
		
		log.info("Applicant detail before decryption : " + applicantDetails.get(0).getApplicantMobileNo());
		applicantDetails = encryptionDecryptionUtil.decryptObject(applicantDetails, 
				CommunityHallBookingConstants.CHB_APPLICANT_DETAIL_PLAIN_DECRYPTION_KEY, ApplicantDetail.class, requestInfo);
		
		applicantDetails.stream().forEach( detail ->{
			if(applicantDetailMap.containsKey(detail.getBookingId())) {
				applicantDetailMap.get(detail.getBookingId()).setApplicantDetail(detail);
			}
		});
				
		log.info("Applicant detail after decryption : " + applicantDetails.get(0).getApplicantMobileNo());
		//bookingDetail.setApplicantDetail(applicantDetail);

		return bookingDetails;
	}

}
