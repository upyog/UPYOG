package org.upyog.sv.service;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CHBEncryptionService {

	/*
	 * @Autowired private EncryptionDecryptionUtil encryptionDecryptionUtil;
	 * 
	 * public CommunityHallBookingDetail encryptObject(CommunityHallBookingRequest
	 * bookingRequest) { ApplicantDetail applicantDetail =
	 * bookingRequest.getHallsBookingApplication().getApplicantDetail();
	 * log.info("Applicant detail before encyption : " +
	 * applicantDetail.getApplicantMobileNo()); applicantDetail =
	 * encryptionDecryptionUtil.encryptObject(applicantDetail,
	 * CommunityHallBookingConstants.CHB_APPLICANT_DETAIL_ENCRYPTION_KEY,
	 * ApplicantDetail.class); log.info("Applicant detail after encyption : " +
	 * applicantDetail.getApplicantMobileNo());
	 * bookingRequest.getHallsBookingApplication().setApplicantDetail(
	 * applicantDetail); return bookingRequest.getHallsBookingApplication(); }
	 * 
	 * 
	 * public CommunityHallBookingDetail decryptObject(CommunityHallBookingDetail
	 * bookingDetail, RequestInfo requestInfo) { ApplicantDetail applicantDetail =
	 * bookingDetail.getApplicantDetail();
	 * log.info("Applicant detail before decryption : " +
	 * applicantDetail.getApplicantMobileNo()); applicantDetail =
	 * encryptionDecryptionUtil.decryptObject(applicantDetail,
	 * CommunityHallBookingConstants.CHB_APPLICANT_DETAIL_PLAIN_DECRYPTION_KEY,
	 * ApplicantDetail.class, requestInfo);
	 * 
	 * log.info("Applicant detail after decryption : " +
	 * applicantDetail.getApplicantMobileNo());
	 * bookingDetail.setApplicantDetail(applicantDetail);
	 * 
	 * return bookingDetail; }
	 * 
	 * public List<CommunityHallBookingDetail>
	 * decryptObject(List<CommunityHallBookingDetail> bookingDetails, RequestInfo
	 * requestInfo) { Map<String, CommunityHallBookingDetail> applicantDetailMap =
	 * bookingDetails.stream().collect(
	 * Collectors.toMap(CommunityHallBookingDetail::getBookingId,
	 * Function.identity()));
	 * 
	 * List<ApplicantDetail> applicantDetails = bookingDetails.stream().map(detail
	 * -> detail.getApplicantDetail()).collect(Collectors.toList());
	 * 
	 * log.info("Applicant detail before decryption : " +
	 * applicantDetails.get(0).getApplicantMobileNo()); applicantDetails =
	 * encryptionDecryptionUtil.decryptObject(applicantDetails,
	 * CommunityHallBookingConstants.CHB_APPLICANT_DETAIL_PLAIN_DECRYPTION_KEY,
	 * ApplicantDetail.class, requestInfo);
	 * 
	 * applicantDetails.stream().forEach( detail ->{
	 * if(applicantDetailMap.containsKey(detail.getBookingId())) {
	 * applicantDetailMap.get(detail.getBookingId()).setApplicantDetail(detail); }
	 * });
	 * 
	 * log.info("Applicant detail after decryption : " +
	 * applicantDetails.get(0).getApplicantMobileNo());
	 * //bookingDetail.setApplicantDetail(applicantDetail);
	 * 
	 * return bookingDetails; }
	 */

}
