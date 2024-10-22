//package org.upyog.adv.service;
//
//import java.util.List;
//import java.util.Map;
//import java.util.function.Function;
//import java.util.stream.Collectors;
//
//import org.egov.common.contract.request.RequestInfo;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.upyog.adv.constants.BookingConstants;
//import org.upyog.adv.util.EncryptionDecryptionUtil;
//import org.upyog.adv.web.models.ApplicantDetail;
//import org.upyog.adv.web.models.BookingDetail;
//import org.upyog.adv.web.models.BookingRequest;
//
//import lombok.extern.slf4j.Slf4j;
//
//@Service
//@Slf4j
//public class EncryptionService {
//
//	@Autowired
//	private EncryptionDecryptionUtil encryptionDecryptionUtil;
//
//	public BookingDetail encryptObject(BookingRequest bookingRequest) {
//		ApplicantDetail applicantDetail = bookingRequest.getBookingApplication().getApplicantDetail();
//		log.info("Applicant detail before encyption : " + applicantDetail.getApplicantMobileNo());
//		applicantDetail = encryptionDecryptionUtil.encryptObject(applicantDetail,
//				BookingConstants.ADV_APPLICANT_DETAIL_ENCRYPTION_KEY, ApplicantDetail.class);
//		log.info("Applicant detail after encyption : " + applicantDetail.getApplicantMobileNo());
//		bookingRequest.getBookingApplication().setApplicantDetail(applicantDetail);
//		return bookingRequest.getBookingApplication();
//	}
//
//	public BookingDetail decryptObject(BookingDetail bookingDetail, RequestInfo requestInfo) {
//		ApplicantDetail applicantDetail = bookingDetail.getApplicantDetail();
//		log.info("Applicant detail before decryption : " + applicantDetail.getApplicantMobileNo());
//		applicantDetail = encryptionDecryptionUtil.decryptObject(applicantDetail,
//				BookingConstants.ADV_APPLICANT_DETAIL_PLAIN_DECRYPTION_KEY, ApplicantDetail.class, requestInfo);
//
//		log.info("Applicant detail after decryption : " + applicantDetail.getApplicantMobileNo());
//		bookingDetail.setApplicantDetail(applicantDetail);
//
//		return bookingDetail;
//	}
//
//	public List<BookingDetail> decryptObject(List<BookingDetail> bookingDetails, RequestInfo requestInfo) {
//		Map<String, BookingDetail> applicantDetailMap = bookingDetails.stream()
//				.collect(Collectors.toMap(BookingDetail::getBookingId, Function.identity()));
//
//		List<ApplicantDetail> applicantDetails = bookingDetails.stream().map(detail -> detail.getApplicantDetail())
//				.collect(Collectors.toList());
//
//		log.info("Applicant detail before decryption : " + applicantDetails.get(0).getApplicantMobileNo());
//		applicantDetails = encryptionDecryptionUtil.decryptObject(applicantDetails,
//				BookingConstants.ADV_APPLICANT_DETAIL_PLAIN_DECRYPTION_KEY, ApplicantDetail.class, requestInfo);
//
//		applicantDetails.stream().forEach(detail -> {
//			if (applicantDetailMap.containsKey(detail.getBookingId())) {
//				applicantDetailMap.get(detail.getBookingId()).setApplicantDetail(detail);
//			}
//		});
//
//		log.info("Applicant detail after decryption : " + applicantDetails.get(0).getApplicantMobileNo());
//		// bookingDetail.setApplicantDetail(applicantDetail);
//
//		return bookingDetails;
//	}
//
//}
