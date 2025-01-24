//package org.upyog.request.service;
//
//import java.util.List;
//import java.util.Map;
//import java.util.function.Function;
//import java.util.stream.Collectors;
//import org.egov.common.contract.request.RequestInfo;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.upyog.request.service.constant.RequestServiceConstants;
//import org.upyog.request.service.util.EncryptionDecryptionUtil;
//import org.upyog.request.service.web.models.ApplicantDetail;
//import org.upyog.request.service.web.models.WaterTankerBookingDetail;
//import org.upyog.request.service.web.models.WaterTankerBookingRequest;
//import lombok.extern.slf4j.Slf4j;
//
//@Service
//@Slf4j
//public class RequestServiceEncryptionService {
//
//	@Autowired
//	private EncryptionDecryptionUtil encryptionDecryptionUtil;
//
//	public WaterTankerBookingDetail encryptObject(WaterTankerBookingRequest waterTankerBookingRequest) {
//		ApplicantDetail applicantDetail = waterTankerBookingRequest.getWaterTankerBookingDetail().getApplicantDetail();
//		log.info("Applicant detail before encyption : " + applicantDetail.getMobileNumber());
//		applicantDetail = encryptionDecryptionUtil.encryptObject(applicantDetail,
//				RequestServiceConstants.RS_APPLICANT_DETAIL_ENCRYPTION_KEY, ApplicantDetail.class);
//		log.info("Applicant detail after encyption : " + applicantDetail.getMobileNumber());
//		waterTankerBookingRequest.getWaterTankerBookingDetail().setApplicantDetail(applicantDetail);
//		return waterTankerBookingRequest.getWaterTankerBookingDetail();
//	}
//
//	public WaterTankerBookingDetail decryptObject(WaterTankerBookingDetail waterTankerbookingDetail, RequestInfo requestInfo) {
//		ApplicantDetail applicantDetail = waterTankerbookingDetail.getApplicantDetail();
//		log.info("Applicant detail before decryption : " + applicantDetail.getMobileNumber());
//		applicantDetail = encryptionDecryptionUtil.decryptObject(applicantDetail, 
//				RequestServiceConstants.RS_APPLICANT_DETAIL_PLAIN_DECRYPTION_KEY, ApplicantDetail.class, requestInfo);
//				
//		log.info("Applicant detail after decryption : " + applicantDetail.getMobileNumber());
//		waterTankerbookingDetail.setApplicantDetail(applicantDetail);
//
//		return waterTankerbookingDetail;
//	}
//	
//	public List<WaterTankerBookingDetail> decryptObject(List<WaterTankerBookingDetail> waterTankerbookingDetail, RequestInfo requestInfo) {
//		Map<String, WaterTankerBookingDetail> applicantDetailMap = waterTankerbookingDetail.stream().collect(
//				Collectors.toMap(WaterTankerBookingDetail::getBookingId, Function.identity()));
//		
//		List<ApplicantDetail> applicantDetails = waterTankerbookingDetail.stream().map(detail -> detail.getApplicantDetail()).collect(Collectors.toList());
//		
//		log.info("Applicant detail before decryption : " + applicantDetails.get(0).getMobileNumber());
//		applicantDetails = encryptionDecryptionUtil.decryptObject(applicantDetails, 
//				RequestServiceConstants.RS_APPLICANT_DETAIL_PLAIN_DECRYPTION_KEY, ApplicantDetail.class, requestInfo);
//		
//		applicantDetails.stream().forEach( detail ->{
//			if(applicantDetailMap.containsKey(detail.getBookingId())) {
//				applicantDetailMap.get(detail.getBookingId()).setApplicantDetail(detail);
//			}
//		});
//				
//		log.info("Applicant detail after decryption : " + applicantDetails.get(0).getMobileNumber());
//		//bookingDetail.setApplicantDetail(applicantDetail);
//
//		return waterTankerbookingDetail;
//	}
//
//}
