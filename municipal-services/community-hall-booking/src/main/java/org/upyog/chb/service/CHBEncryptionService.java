package org.upyog.chb.service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.egov.common.contract.request.RequestInfo;
import org.springframework.stereotype.Service;
import org.upyog.chb.constants.CommunityHallBookingConstants;
import org.upyog.chb.util.EncryptionDecryptionUtil;
import org.upyog.chb.web.models.ApplicantDetail;
import org.upyog.chb.web.models.VenueBookingDetail;
import org.upyog.chb.web.models.VenueBookingRequest;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CHBEncryptionService {

	private final EncryptionDecryptionUtil encryptionDecryptionUtil;

	public CHBEncryptionService(EncryptionDecryptionUtil encryptionDecryptionUtil) {
		this.encryptionDecryptionUtil = encryptionDecryptionUtil;
	}

	public VenueBookingDetail encryptObject(VenueBookingRequest bookingRequest) {
		ApplicantDetail applicantDetail = bookingRequest.getVenueBookingApplication().getApplicantDetail();
		log.info("Applicant detail before encyption : " + applicantDetail.getApplicantMobileNo());
		applicantDetail = encryptionDecryptionUtil.encryptObject(applicantDetail,
				CommunityHallBookingConstants.CHB_APPLICANT_DETAIL_ENCRYPTION_KEY, ApplicantDetail.class);
		log.info("Applicant detail after encyption : " + applicantDetail.getApplicantMobileNo());
		bookingRequest.getVenueBookingApplication().setApplicantDetail(applicantDetail);
		return bookingRequest.getVenueBookingApplication();
	}
	
	
	public VenueBookingDetail decryptObject(VenueBookingDetail bookingDetail, RequestInfo requestInfo) {
		ApplicantDetail applicantDetail = bookingDetail.getApplicantDetail();
		log.info("Applicant detail before decryption : " + applicantDetail.getApplicantMobileNo());
		applicantDetail = encryptionDecryptionUtil.decryptObject(applicantDetail, 
				CommunityHallBookingConstants.CHB_APPLICANT_DETAIL_PLAIN_DECRYPTION_KEY, ApplicantDetail.class, requestInfo);
				
		log.info("Applicant detail after decryption : " + applicantDetail.getApplicantMobileNo());
		bookingDetail.setApplicantDetail(applicantDetail);

		return bookingDetail;
	}
	
	public List<VenueBookingDetail> decryptObject(List<VenueBookingDetail> bookingDetails, RequestInfo requestInfo) {
		Map<String, VenueBookingDetail> applicantDetailMap = bookingDetails.stream().collect(
				Collectors.toMap(VenueBookingDetail::getBookingId, Function.identity()));
		
		List<ApplicantDetail> applicantDetails = bookingDetails.stream().map(VenueBookingDetail::getApplicantDetail).toList();
		
		log.info("Applicant detail before decryption : " + applicantDetails.get(0).getApplicantMobileNo());
		applicantDetails = encryptionDecryptionUtil.decryptObject(applicantDetails, 
				CommunityHallBookingConstants.CHB_APPLICANT_DETAIL_PLAIN_DECRYPTION_KEY, ApplicantDetail.class, requestInfo);
		
		applicantDetails.forEach(detail -> {
			if(applicantDetailMap.containsKey(detail.getBookingId())) {
				applicantDetailMap.get(detail.getBookingId()).setApplicantDetail(detail);
			}
		});
				
		log.info("Applicant detail after decryption : " + applicantDetails.get(0).getApplicantMobileNo());

		return bookingDetails;
	}

}
