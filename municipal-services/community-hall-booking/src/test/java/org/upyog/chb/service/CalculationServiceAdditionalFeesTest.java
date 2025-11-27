// package org.upyog.chb.service;
//
// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.ArgumentMatchers.*;
// import static org.mockito.Mockito.*;
//
// import java.math.BigDecimal;
// import java.time.LocalDate;
// import java.time.LocalTime;
// import java.util.Arrays;
// import java.util.List;
//
// import org.egov.common.contract.request.RequestInfo;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;
// import org.upyog.chb.config.CommunityHallBookingConfiguration;
// import org.upyog.chb.util.CalculationTypeCache;
// import org.upyog.chb.util.FeeCalculationUtil;
// import org.upyog.chb.util.MdmsUtil;
// import org.upyog.chb.web.models.AdditionalFeeRate;
// import org.upyog.chb.web.models.BookingSlotDetail;
// import org.upyog.chb.web.models.CalculationType;
// import org.upyog.chb.web.models.CommunityHallBookingDetail;
// import org.upyog.chb.web.models.CommunityHallBookingRequest;
// import org.upyog.chb.web.models.billing.DemandDetail;
// import org.upyog.chb.web.models.billing.TaxHeadMaster;
//
/// **
// * Integration test for additional fees calculation scenarios
// */
// @ExtendWith(MockitoExtension.class)
// public class CalculationServiceAdditionalFeesTest {
//
// @Mock
// private MdmsUtil mdmsUtil;
//
// @Mock
// private CalculationTypeCache calculationTypeCache;
//
// @Mock
// private CommunityHallBookingConfiguration config;
//
// @Mock
// private FeeCalculationUtil feeCalculationUtil;
//
// @InjectMocks
// private CalculationService calculationService;
//
// private CommunityHallBookingRequest bookingRequest;
// private RequestInfo requestInfo;
//
// @BeforeEach
// public void setUp() {
// // Set up mock booking request
// requestInfo = new RequestInfo();
//
// CommunityHallBookingDetail bookingDetail =
// CommunityHallBookingDetail.builder()
// .tenantId("pb.amritsar")
// .bookingSlotDetails(Arrays.asList(
// BookingSlotDetail.builder()
// .hallCode("HALL_001")
// .bookingDate(LocalDate.of(2025, 10, 15)) // Oct 15, 2025 (future event)
// .bookingFromTime(LocalTime.of(10, 0))
// .bookingToTime(LocalTime.of(18, 0))
// .build(),
// BookingSlotDetail.builder()
// .hallCode("HALL_001")
// .bookingDate(LocalDate.of(2025, 10, 16)) // Oct 16, 2025 (2 day booking)
// .bookingFromTime(LocalTime.of(10, 0))
// .bookingToTime(LocalTime.of(18, 0))
// .build()
// ))
// .build();
//
// bookingRequest = CommunityHallBookingRequest.builder()
// .requestInfo(requestInfo)
// .hallsBookingApplication(bookingDetail)
// .build();
//
// // Mock configuration
// when(config.getModuleName()).thenReturn("CHB");
// }
//
// @Test
// public void testScenario1_BookingAtEventTime() {
// // Mock tax head masters
// List<TaxHeadMaster> taxHeadMasters = Arrays.asList(
// TaxHeadMaster.builder().code("BOOKING_FEES").build(),
// TaxHeadMaster.builder().code("CHB_SERVICE_CHARGE").build(),
// TaxHeadMaster.builder().code("CHB_SECURITY_DEPOSIT").build(),
// TaxHeadMaster.builder().code("CGST").build(),
// TaxHeadMaster.builder().code("SGST").build()
// );
// when(mdmsUtil.getTaxHeadMasterList(any(), any(),
// any())).thenReturn(taxHeadMasters);
//
// // Mock calculation types (BOOKING_FEES)
// List<CalculationType> calculationTypes = Arrays.asList(
// CalculationType.builder()
// .feeType("BOOKING_FEES")
// .amount(new BigDecimal("7500")) // ₹7,500 per day
// .taxApplicable(true)
// .build()
// );
// when(calculationTypeCache.getcalculationType(any(), any(), any(),
// any())).thenReturn(calculationTypes);
//
// // Mock additional fees
// when(feeCalculationUtil.getCurrentFinancialYear()).thenReturn("2025-26");
//
// // ServiceCharge: ₹500 flat
// when(mdmsUtil.getServiceCharges(any(), any(),
// any())).thenReturn(Arrays.asList(
// AdditionalFeeRate.builder()
// .feeType("ServiceCharge")
// .flatAmount(new BigDecimal("500"))
// .fromFY("2025-26")
// .applicableAfterDays("0")
// .active(true)
// .taxApplicable(true)
// .build()
// ));
//
// // SecurityDeposit: ₹5,000 flat
// when(mdmsUtil.getSecurityDeposits(any(), any(),
// any())).thenReturn(Arrays.asList(
// AdditionalFeeRate.builder()
// .feeType("SecurityDeposit")
// .flatAmount(new BigDecimal("5000"))
// .fromFY("2025-26")
// .applicableAfterDays("0")
// .active(true)
// .taxApplicable(false)
// .refundable(true)
// .build()
// ));
//
// // No penalty/interest since event is in future
// when(mdmsUtil.getPenaltyFees(any(), any(),
// any())).thenReturn(Arrays.asList());
// when(mdmsUtil.getInterestAmounts(any(), any(),
// any())).thenReturn(Arrays.asList());
//
// // Mock fee calculation results
// when(feeCalculationUtil.calculateFeeAmount(any(), any(), eq(0),
// eq("2025-26")))
// .thenReturn(new BigDecimal("500")) // ServiceCharge
// .thenReturn(new BigDecimal("5000")); // SecurityDeposit
//
// // Mock tax rates (18% GST = 9% CGST + 9% SGST)
// List<CalculationType> taxRates = Arrays.asList(
// CalculationType.builder()
// .feeType("CGST")
// .amount(new BigDecimal("9"))
// .build(),
// CalculationType.builder()
// .feeType("SGST")
// .amount(new BigDecimal("9"))
// .build()
// );
// when(mdmsUtil.getTaxRatesMasterList(any(), any(), any(),
// any())).thenReturn(taxRates);
//
// // Execute calculation
// List<DemandDetail> demandDetails =
// calculationService.calculateDemand(bookingRequest);
//
// // Verify results
// assertNotNull(demandDetails);
//
// // Find specific demand details
// BigDecimal bookingFees = getDemandAmount(demandDetails, "BOOKING_FEES");
// BigDecimal serviceCharge = getDemandAmount(demandDetails,
// "CHB_SERVICE_CHARGE");
// BigDecimal securityDeposit = getDemandAmount(demandDetails,
// "CHB_SECURITY_DEPOSIT");
// BigDecimal cgst = getDemandAmount(demandDetails, "CGST");
// BigDecimal sgst = getDemandAmount(demandDetails, "SGST");
//
// // Expected calculations:
// // Booking Fees: ₹7,500 × 2 days = ₹15,000
// assertEquals(new BigDecimal("15000"), bookingFees);
//
// // ServiceCharge: ₹500 (flat)
// assertEquals(new BigDecimal("500"), serviceCharge);
//
// // SecurityDeposit: ₹5,000 (flat, non-taxable)
// assertEquals(new BigDecimal("5000"), securityDeposit);
//
// // Total Taxable = ₹15,000 + ₹500 = ₹15,500
// // CGST (9%) = ₹15,500 × 9% = ₹1,395
// // SGST (9%) = ₹15,500 × 9% = ₹1,395
// assertEquals(new BigDecimal("1395.00"), cgst);
// assertEquals(new BigDecimal("1395.00"), sgst);
//
// // Total: ₹15,000 + ₹500 + ₹5,000 + ₹1,395 + ₹1,395 = ₹23,290
// BigDecimal total = demandDetails.stream()
// .map(DemandDetail::getTaxAmount)
// .reduce(BigDecimal.ZERO, BigDecimal::add);
// assertEquals(new BigDecimal("23290.00"), total);
// }
//
// private BigDecimal getDemandAmount(List<DemandDetail> demandDetails, String
// taxHeadCode) {
// return demandDetails.stream()
// .filter(detail -> taxHeadCode.equals(detail.getTaxHeadMasterCode()))
// .findFirst()
// .map(DemandDetail::getTaxAmount)
// .orElse(BigDecimal.ZERO);
// }
// }