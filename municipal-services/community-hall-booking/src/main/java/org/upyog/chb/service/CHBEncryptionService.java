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

@Service
@Slf4j
public class CHBEncryptionService {

	@Autowired
	private EncryptionDecryptionUtil encryptionDecryptionUtil;

	public CommunityHallBookingDetail encryptObject(CommunityHallBookingRequest bookingRequest) {
		ApplicantDetail applicantDetail = bookingRequest.getHallsBookingApplication().getApplicantDetail();
		log.info("Applicant detail before encyption : " + applicantDetail);
		applicantDetail = encryptionDecryptionUtil.encryptObject(applicantDetail,
				CommunityHallBookingConstants.CHB_APPLICANT_DETAIL_ENCRYPTION_KEY, ApplicantDetail.class);
		log.info("Applicant detail after encyption : " + applicantDetail);
		bookingRequest.getHallsBookingApplication().setApplicantDetail(applicantDetail);
		return bookingRequest.getHallsBookingApplication();
	}
	
	
	public CommunityHallBookingDetail decryptObject(CommunityHallBookingDetail bookingDetail, RequestInfo requestInfo) {
		ApplicantDetail applicantDetail = bookingDetail.getApplicantDetail();
		log.info("Applicant detail before decryption : " + applicantDetail);
		applicantDetail = encryptionDecryptionUtil.decryptObject(applicantDetail, 
				CommunityHallBookingConstants.CHB_APPLICANT_DETAIL_PLAIN_DECRYPTION_KEY, ApplicantDetail.class, requestInfo);
				
		log.info("Applicant detail after decryption : " + applicantDetail);
		bookingDetail.setApplicantDetail(applicantDetail);

		return bookingDetail;
	}
	
	public List<CommunityHallBookingDetail> decryptObject(List<CommunityHallBookingDetail> bookingDetails, RequestInfo requestInfo) {
		Map<String, CommunityHallBookingDetail> applicantDetailMap = bookingDetails.stream().collect(
				Collectors.toMap(CommunityHallBookingDetail::getBookingId, Function.identity()));
		
		List<ApplicantDetail> applicantDetails = bookingDetails.stream().map(detail -> detail.getApplicantDetail()).collect(Collectors.toList());
		
		log.info("Applicant detail before decryption : " + applicantDetails);
		applicantDetails = encryptionDecryptionUtil.decryptObject(applicantDetails, 
				CommunityHallBookingConstants.CHB_APPLICANT_DETAIL_PLAIN_DECRYPTION_KEY, ApplicantDetail.class, requestInfo);
		
		applicantDetails.stream().forEach( detail ->{
			if(applicantDetailMap.containsKey(detail.getBookingId())) {
				applicantDetailMap.get(detail.getBookingId()).setApplicantDetail(detail);
			}
		});
				
		log.info("Applicant detail after decryption : " + applicantDetails);
		//bookingDetail.setApplicantDetail(applicantDetail);

		return bookingDetails;
	}

}
