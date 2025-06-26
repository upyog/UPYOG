/*
 * eGov suite of products aim to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) <2015>  eGovernments Foundation
 *
 *     The updated version of eGov suite of products as by eGovernments Foundation
 *     is available at http://www.egovernments.org
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see http://www.gnu.org/licenses/ or
 *     http://www.gnu.org/licenses/gpl.html .
 *
 *     In addition to the terms of the GPL license to be adhered to in using this
 *     program, the following additional terms are to be complied with:
 *
 *         1) All versions of this program, verbatim or modified must carry this
 *            Legal Notice.
 *
 *         2) Any misrepresentation of the origin of the material is prohibited. It
 *            is required that all modified versions of this material be marked in
 *            reasonable ways as different from the original version.
 *
 *         3) This license does not grant any rights to any Long of the program
 *            with regards to rights under trademark law for use of the trade names
 *            or trademarks of eGovernments Foundation.
 *
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */
package org.egov.receipt.consumer.service;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.egov.mdms.service.MicroServiceUtil;
import org.egov.receipt.consumer.model.AccountDetail;
import org.egov.receipt.consumer.model.AppConfigValues;
import org.egov.receipt.consumer.model.Bill;
import org.egov.receipt.consumer.model.BillAccountDetail;
import org.egov.receipt.consumer.model.BillDetail;
import org.egov.receipt.consumer.model.BusinessService;
import org.egov.receipt.consumer.model.EgModules;
import org.egov.receipt.consumer.model.FinanceMdmsModel;
import org.egov.receipt.consumer.model.Function;
import org.egov.receipt.consumer.model.Functionary;
import org.egov.receipt.consumer.model.Fund;
import org.egov.receipt.consumer.model.InstrumentContract;
import org.egov.receipt.consumer.model.InstrumentVoucherContract;
import org.egov.receipt.consumer.model.ProcessStatus;
import org.egov.receipt.consumer.model.Receipt;
import org.egov.receipt.consumer.model.ReceiptReq;
import org.egov.receipt.consumer.model.RequestInfo;
import org.egov.receipt.consumer.model.Scheme;
import org.egov.receipt.consumer.model.TaxHeadMaster;
import org.egov.receipt.consumer.model.Tenant;
import org.egov.receipt.consumer.model.Voucher;
import org.egov.receipt.consumer.model.VoucherRequest;
import org.egov.receipt.consumer.model.VoucherResponse;
import org.egov.receipt.consumer.model.VoucherSearchCriteria;
import org.egov.receipt.consumer.model.VoucherSearchRequest;
import org.egov.receipt.consumer.repository.ServiceRequestRepository;
import org.egov.receipt.consumer.v2.model.BillAccountDetailV2;
import org.egov.receipt.custom.exception.VoucherCustomException;
import org.egov.reciept.consumer.config.PropertiesManager;
import org.egov.tracer.model.CustomException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class VoucherServiceImpl implements VoucherService {

	private static final String ADVANCE = "advance";
	@Autowired
	private PropertiesManager propertiesManager;
	@Autowired
	private MicroServiceUtil microServiceUtil;
	@Autowired
	private ServiceRequestRepository serviceRequestRepository;
	@Autowired
	private ObjectMapper mapper;

	final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
	final SimpleDateFormat ddMMMyyyyFormatter = new SimpleDateFormat("dd-MMM-yyyy");

	private static final String RECEIPTS_VOUCHER_TYPE = "Receipt";
	private static final String COLLECTIONS_EG_MODULES_ID = "10";
	private static final Logger LOGGER = LoggerFactory.getLogger(VoucherServiceImpl.class);
	private static final String COLLECTION_MODULE_NAME = "Collections";
	private LinkedHashMap<String, BigDecimal> amountMapwithGlcode;
	private static final String REVERSAL_VOUCHER_NAME = "JVGeneral";
	private static final String REVERSAL_VOUCHER_TYPE = "Journal Voucher";

	private static final String PROPERTY_TAX = "PT_TAX";

	private static final String GENERAL_CONSERVANCY = "PT_GENERAL_CONSERVANCY";

	private static final String STREET_LIGHTING = "PT_STREET_LIGHTING";

	private static final String ROUND_OFF = "PT_ROUNDOFF";
	private static final Object INTEREST = "PT_PROPERTY_INTEREST";

	@Override
	/**
	 * This method is use to create the voucher specifically for receipt request.
	 */
	public VoucherResponse createReceiptVoucher(ReceiptReq receiptRequest, FinanceMdmsModel finSerMdms,
			String collectionVersion) throws CustomException {
		Receipt receipt = receiptRequest.getReceipt().get(0);
		String tenantId = receipt.getTenantId();
//		final StringBuilder voucher_create_url = new StringBuilder(propertiesManager.getErpURLBytenantId(tenantId)
//				+ propertiesManager.getVoucherCreateUrl());
//		VoucherRequest voucherRequest = new VoucherRequest();
		Voucher voucher = new Voucher();
		voucher.setTenantId(tenantId);
		try {
			this.setVoucherDetails(voucher, receipt, tenantId, receiptRequest.getRequestInfo(), finSerMdms,
					collectionVersion);
		} catch (Exception e) {
			throw new CustomException(e.toString(), e.toString());
		}
//		voucherRequest.setVouchers(Collections.singletonList(voucher));
//		voucherRequest.setRequestInfo(receiptRequest.getRequestInfo());
//		voucherRequest.setTenantId(tenantId);
		try {
			return createVoucher(Collections.singletonList(voucher), receiptRequest.getRequestInfo(), tenantId);
		} catch (VoucherCustomException e) {
			throw new CustomException(e.toString(), e.toString());
		}
//		return mapper.convertValue(serviceRequestRepository.fetchResult(voucher_create_url, voucherRequest, tenantId), VoucherResponse.class);
	}

	@Override
	public VoucherResponse createVoucher(List<Voucher> vouchers, RequestInfo requestInfo, String tenantId)
			throws VoucherCustomException {
		// Set tenantId inside requestInfo object
		if (requestInfo.getTenantId() == null) {
			requestInfo.setTenantId(tenantId);
		}
		final StringBuilder voucher_create_url = new StringBuilder(
				propertiesManager.getErpURLBytenantId(tenantId) + propertiesManager.getVoucherCreateUrl());
		VoucherRequest voucherRequest = new VoucherRequest();
		voucherRequest.setVouchers(vouchers);
		voucherRequest.setRequestInfo(requestInfo);// ----in this requestInfo itselft we need pass tenantId
		voucherRequest.setTenantId(tenantId);

		// Set custom Host header
		String hostHeader = tenantId.split(Pattern.quote("."))[1];
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("SchemaName", hostHeader);

		Object response = serviceRequestRepository.fetchResultWithHeaders(voucher_create_url, voucherRequest, tenantId,
				headers);
		return mapper.convertValue(response, VoucherResponse.class);

		// return
		// mapper.convertValue(serviceRequestRepository.fetchResult(voucher_create_url,
		// voucherRequest, tenantId), VoucherResponse.class);
	}

	/**
	 * Function which is used to check whether the voucher creation is set to true
	 * or false in business mapping file.
	 */
	@Override
	public boolean isVoucherCreationEnabled(Receipt receipt, RequestInfo req, FinanceMdmsModel finSerMdms)
			throws CustomException {
//		Receipt receipt = req.getReceipt().get(0);
		String tenantId = receipt.getTenantId();
		Bill bill = receipt.getBill().get(0);
		String bsCode = bill.getBillDetails().get(0).getBusinessService();
		List<BusinessService> serviceByCode = null;
		try {
			serviceByCode = this.getBusinessServiceByCode(tenantId, bsCode, req, finSerMdms);
		} catch (Exception e) {
			throw new CustomException(e.toString(), e.toString());
		}
		return serviceByCode != null && !serviceByCode.isEmpty() ? serviceByCode.get(0).isVoucherCreationEnabled()
				: false;
	}

	/**
	 * Function is for cancelling the voucher based on voucher number
	 */
	@Override
	public VoucherResponse cancelReceiptVoucher(ReceiptReq receiptRequest, String tenantId, Set<String> voucherNumbers)
			throws VoucherCustomException {
		final StringBuilder voucher_cancel_url = new StringBuilder(
				propertiesManager.getErpURLBytenantId(tenantId) + propertiesManager.getVoucherCancelUrl());
		try {
			VoucherSearchRequest vSearchReq = this.getVoucherSearchReq(receiptRequest, voucherNumbers, tenantId);
			return mapper.convertValue(serviceRequestRepository.fetchResult(voucher_cancel_url, vSearchReq, tenantId),
					VoucherResponse.class);
		} catch (Exception e) {
			throw new VoucherCustomException(ProcessStatus.FAILED, "Failed to cancel voucher");
		}
	}

	/**
	 * 
	 * @param receiptRequest
	 * @return This function is use to set the voucher search params and return the
	 *         setted request
	 */
	private VoucherSearchRequest getVoucherSearchReq(ReceiptReq receiptRequest, Set<String> voucherNumbers,
			String tenantId) {
		VoucherSearchRequest vSearchReq = new VoucherSearchRequest();
		StringBuilder vouNumberBuilder = new StringBuilder();
		voucherNumbers.stream().forEach(vn -> vouNumberBuilder.append(vn).append(","));
		vSearchReq.setVoucherNumbers(vouNumberBuilder.toString());
		vSearchReq.setTenantId(tenantId);
		RequestInfo requestInfo = receiptRequest.getRequestInfo();
//		requestInfo.setAuthToken(propertiesManager.getSiAuthToken());
		vSearchReq.setRequestInfo(requestInfo);
		return vSearchReq;
	}

	/**
	 * 
	 * @param voucher
	 * @param receipt
	 * @param tenantId
	 * @throws Exception Function is use to set the specific details of voucher
	 *                   which is mendatory to create the voucher.
	 */
	private void setVoucherDetails(Voucher voucher, Receipt receipt, String tenantId, RequestInfo requestInfo,
			FinanceMdmsModel finSerMdms, String collectiobVersion) throws CustomException, VoucherCustomException {
		// Set tenantId inside requestInfo object
		if (requestInfo.getTenantId() == null) {
			requestInfo.setTenantId(tenantId);
		}
		BillDetail billDetail = receipt.getBill().get(0).getBillDetails().get(0);
		String receiptNumber = null;
		String consumerCode = null;
		if (collectiobVersion != null && collectiobVersion.equalsIgnoreCase("V2")) {
			receiptNumber = receipt.getPaymentId();
			consumerCode = receipt.getConsumerCode();
		} else {
			receiptNumber = billDetail.getReceiptNumber();
			consumerCode = billDetail.getConsumerCode();
		}
		String bsCode = billDetail.getBusinessService();
		List<BusinessService> serviceByCode = this.getBusinessServiceByCode(tenantId, bsCode, requestInfo, finSerMdms);
		List<TaxHeadMaster> taxHeadMasterByBusinessServiceCode = this.getTaxHeadMasterByBusinessServiceCode(tenantId,
				bsCode, requestInfo, finSerMdms);
		BusinessService businessService = serviceByCode.get(0);
		String businessServiceName = microServiceUtil.getBusinessServiceName(tenantId, bsCode, requestInfo, finSerMdms);
		voucher.setName(businessServiceName);
		voucher.setType(RECEIPTS_VOUCHER_TYPE);
		voucher.setFund(new Fund());
		voucher.getFund().setCode(businessService.getFund());
		voucher.setFunction(new Function());
		voucher.getFunction().setCode(businessService.getFunction());
		voucher.setDepartment(businessService.getDepartment());
		voucher.setFunctionary(new Functionary());
		voucher.setServiceName(bsCode);
		voucher.setReferenceDocument(receiptNumber);
		String functionaryCode = businessService.getFunctionary() != null
				& !StringUtils.isEmpty(businessService.getFunctionary()) ? businessService.getFunctionary() : null;
		voucher.getFunctionary().setCode(functionaryCode);
		voucher.setScheme(new Scheme());
		String schemeCode = businessService.getScheme() != null & !StringUtils.isEmpty(businessService.getScheme())
				? businessService.getScheme()
				: null;
		voucher.getScheme().setCode(schemeCode);
		voucher.setDescription(businessServiceName + " Receipt");
		// checking Whether manualReceipt date will be consider as
		// voucherdate
		if (billDetail.getManualReceiptDate() != null && billDetail.getManualReceiptDate().longValue() != 0
				&& isManualReceiptDateEnabled(tenantId, requestInfo)) {
			voucher.setVoucherDate(dateFormatter.format(new Date(billDetail.getManualReceiptDate())));
		} else {
			voucher.setVoucherDate(dateFormatter.format(new Date(billDetail.getReceiptDate())));
		}
		EgModules egModules = this.getModuleIdByModuleName(COLLECTION_MODULE_NAME, tenantId, requestInfo);
		voucher.setModuleId(Long.valueOf(egModules != null ? egModules.getId().toString() : COLLECTIONS_EG_MODULES_ID));

		voucher.setSource(propertiesManager.getReceiptViewSourceUrl() + "?selectedReceipts=" + receiptNumber
				+ "&serviceTypeId=" + bsCode);

		voucher.setLedgers(new ArrayList<>());
		final String serviceAttribute = getServiceAttributeByBusinessService(tenantId, requestInfo, businessService,
				consumerCode);
		LOGGER.info("Service Attribute  ::: {}", serviceAttribute);
		amountMapwithGlcode = new LinkedHashMap<>();
		// Setting glcode and amount in Map as key value pair.
		for (BillAccountDetailV2 bad : billDetail.getBillAccountDetails()) {
			BigDecimal adjustedAmount = bad.getAdjustedAmount();
			if (bad.getTaxHeadCode().toLowerCase().contains(ADVANCE))
				adjustedAmount = bad.getAmount().abs();
			if (bad.getTaxHeadCode().toLowerCase().contains(ADVANCE)
					|| adjustedAmount.compareTo(new BigDecimal(0)) != 0) {
				String taxHeadCode = bad.getTaxHeadCode();
				List<TaxHeadMaster> findFirst = taxHeadMasterByBusinessServiceCode.stream()
						.filter(tx -> serviceAttribute != null
								&& businessService.getServiceAttributeTaxHead().equals(taxHeadCode)
										? tx.getTaxhead().equals(taxHeadCode)
												&& serviceAttribute.equals(tx.getServiceAttribute())
										: tx.getTaxhead().equals(taxHeadCode))
						.collect(Collectors.toList());
				if (findFirst != null && findFirst.isEmpty()) {
					StringBuilder exception = new StringBuilder("Taxhead code ").append(taxHeadCode)
							.append(" is not mapped with BusinessServiceCode ").append(bsCode);
					if (serviceAttribute != null)
						exception.append("and Service Attribute ").append(serviceAttribute);
					throw new VoucherCustomException(ProcessStatus.FAILED, exception.toString());
				}
				String glcode = findFirst.get(0).getGlcode();
				if (amountMapwithGlcode.get(glcode) != null) {
					amountMapwithGlcode.put(glcode, amountMapwithGlcode.get(glcode).add(adjustedAmount));
				} else {
					amountMapwithGlcode.put(glcode, adjustedAmount);
				}
			}
		}

		Map<String, BigDecimal> amountMapwithGlcode = this.setNetReceiptAmount(receipt, requestInfo, tenantId, bsCode,
				finSerMdms);

		LOGGER.debug("amountMapwithGlcode  ::: {}", amountMapwithGlcode);
		// Iterating map and setting the ledger details to voucher.

		if (amountMapwithGlcode.isEmpty()) {
			throw new VoucherCustomException(ProcessStatus.NA, "This receipt does not require voucher creation.");
		}
		amountMapwithGlcode.forEach((gl, amt) -> {
			AccountDetail accountDetail = new AccountDetail();
			accountDetail.setGlcode(gl);
			if (amt.compareTo(new BigDecimal(0)) == 1) {
				accountDetail.setCreditAmount(amt.doubleValue());
				accountDetail.setDebitAmount(0d);
			} else {
				accountDetail.setDebitAmount(-amt.doubleValue());
				accountDetail.setCreditAmount(0d);
			}
			accountDetail.setFunction(new Function());
			accountDetail.getFunction().setCode(businessService.getFunction());
			voucher.getLedgers().add(accountDetail);
		});
	}

	private String getServiceAttributeByBusinessService(String tenantId, RequestInfo requestInfo,
			BusinessService businessService, String consumerCode) throws VoucherCustomException {
		// Set tenantId inside requestInfo object
		if (requestInfo.getTenantId() == null) {
			requestInfo.setTenantId(tenantId);
		}
		if (businessService.isServiceAttributeMappingEnabled()) {
			try {
				final List<?> list = Arrays.asList(consumerCode.split(":"));
				String formattedUrl = null;
				if (list.size() > 1)
					formattedUrl = String.format(businessService.getServiceAttributeUrl(), tenantId, list.get(0),
							list.get(1));
				else
					formattedUrl = String.format(businessService.getServiceAttributeUrl(), tenantId, list.get(0));

				final StringBuilder businessServiceUrl = new StringBuilder(
						propertiesManager.getBusinessServiceHostUrl()).append(formattedUrl);
				VoucherRequest request = new VoucherRequest();
				request.setRequestInfo(requestInfo);
				request.setTenantId(tenantId);
				LOGGER.info("Business service :: {}, Consumercode :: {}", businessService, consumerCode);
				Map<?, ?> apiResponse = (Map<?, ?>) serviceRequestRepository.fetchResult(businessServiceUrl, request,
						tenantId);
				LOGGER.info("Business service api response :: {}", apiResponse);
				Map<?, ?> responseSource = apiResponse;
				Object response = null;
				for (String str : Arrays.asList(businessService.getServiceAttributeKey().split("\\."))) {
					if (str.contains("~"))
						response = getResponse(responseSource, str);
					else
						response = responseSource.get(str);
					if (response instanceof Collection) {
						response = ((ArrayList<?>) response).get(0);
						responseSource = (Map<?, ?>) response;
					}
				}
				return (String) response;
			} catch (Exception e) {
				throw new VoucherCustomException(ProcessStatus.FAILED,
						"Failed to fetch service attribute: " + e.getMessage());
			}
		}
		return null;
	}

	private Object getResponse(Map<?, ?> responseSource, String str) {
		Object response;
		StringBuilder res = new StringBuilder();
		for (String key : Arrays.asList(str.split("~"))) {
			if (responseSource.get(key) != null) {
				res.append(responseSource.get(key)).append(".");
			}
		}
		response = res.length() != 0 ? res.deleteCharAt(res.length() - 1).toString() : null;
		return response;
	}

	/**
	 * 
	 * @param tenantId
	 * @return Function is used to check the config value for manual receipt date
	 *         consideration.
	 * @throws VoucherCustomException
	 */
	private boolean isManualReceiptDateEnabled(String tenantId, RequestInfo requestInfo) throws VoucherCustomException {
//		requestInfo.setAuthToken(propertiesManager.getSiAuthToken());
		// Set tenantId inside requestInfo object
		if (requestInfo.getTenantId() == null) {
			requestInfo.setTenantId(tenantId);
		}
		VoucherRequest request = new VoucherRequest(tenantId, requestInfo, null);
		StringBuilder url = new StringBuilder(
				propertiesManager.getErpURLBytenantId(tenantId) + propertiesManager.getManualReceiptDateConfigUrl());
		AppConfigValues convertValue = null;
		try {
			convertValue = mapper.convertValue(serviceRequestRepository.fetchResult(url, request, tenantId),
					AppConfigValues.class);
		} catch (Exception e) {
			if (LOGGER.isErrorEnabled())
				LOGGER.error(
						"ERROR while checking the consideration of manual receipt date. So the receipt date will be consider as the voucher date");
		}
		return convertValue != null ? convertValue.getValue().equalsIgnoreCase("Yes") : false;
	}

	/**
	 * 
	 * @param receipt
	 * @param tenantId
	 * @param businessCode
	 * @param finSerMdms
	 * @throws VoucherCustomException Function is used to set the paid amount as
	 *                                debit in finance system.
	 */
	private Map<String, BigDecimal> setNetReceiptAmount(Receipt receipt, RequestInfo requestInfo, String tenantId,
			String businessCode, FinanceMdmsModel finSerMdms) throws VoucherCustomException {
		// Set tenantId inside requestInfo object
		if (requestInfo.getTenantId() == null) {
			requestInfo.setTenantId(tenantId);
		}
		Map<String, BigDecimal> amountMapwithGlcode = new LinkedHashMap<>();
		BigDecimal interestAmountToUse = BigDecimal.ZERO;

		try {
			BillDetail billDetail = receipt.getBill().get(0).getBillDetails().get(0);
			BigDecimal amountPaid = billDetail.getAmountPaid();
			List<BillAccountDetailV2> accountDetails = billDetail.getBillAccountDetails();

			// Only include these tax heads
			Set<String> validTaxHeads = new HashSet<>(
					Arrays.asList(PROPERTY_TAX, GENERAL_CONSERVANCY, STREET_LIGHTING, ROUND_OFF));

			for (BillAccountDetailV2 detail : accountDetails) {
				String taxHeadCode = detail.getTaxHeadCode();
				String glcode = detail.getGlcode();
				BigDecimal amount = detail.getAmount();

				if (taxHeadCode == null || glcode == null)
					continue;

				if (taxHeadCode.equals(INTEREST)) {
					BigDecimal interestExpected = BigDecimal.ZERO; // billDetail.getTotalAmountForIntCal();
					interestAmountToUse = amount.compareTo(interestExpected) == 0 ? amount : interestExpected;
					// Do NOT merge into amountMapwithGlcode
				} else if (validTaxHeads.contains(taxHeadCode)) {
					if (amount != null && amount.compareTo(BigDecimal.ZERO) != 0) {
						amountMapwithGlcode.merge(glcode, amount, BigDecimal::add);
					}
				}
			}

			if (amountPaid != null && amountPaid.compareTo(BigDecimal.ZERO) != 0) {
				String instrumentType = receipt.getInstrument().getInstrumentType().getName();
				String glcode = microServiceUtil.getGlcodeByInstrumentType(tenantId, businessCode, requestInfo,
						finSerMdms, instrumentType);
				if (glcode == null) {
					throw new VoucherCustomException(ProcessStatus.FAILED,
							"Account code mapping is missing for Instrument Type " + instrumentType);
				}

				BigDecimal debitAmount = amountPaid.negate();
				if (debitAmount.compareTo(BigDecimal.ZERO) != 0) {
					amountMapwithGlcode.put(glcode, debitAmount);
				}
			}

			// Log final non-zero mappings for audit/debug
			amountMapwithGlcode.forEach((gl, amt) -> LOGGER.info("GL Code: {}, Amount: {}", gl, amt));

			// Optional: log interest for info/debug
			if (interestAmountToUse.compareTo(BigDecimal.ZERO) > 0) {
				LOGGER.info("PT_TIME_INTEREST amount calculated (not merged): {}", interestAmountToUse);
			}

		} catch (Exception e) {
			throw new VoucherCustomException(ProcessStatus.FAILED, "Error in setNetReceiptAmount: " + e.getMessage());
		}

		return amountMapwithGlcode;
	}

	/**
	 * 
	 * @param tenantId
	 * @param bsCode
	 * @return
	 * @throws Exception Function is used to get the Business Services based on
	 *                   business service code which is mapped in json file
	 */
	private List<BusinessService> getBusinessServiceByCode(String tenantId, String bsCode, RequestInfo requestInfo,
			FinanceMdmsModel finSerMdms) throws CustomException, VoucherCustomException {
		// Set tenantId inside requestInfo object
		if (requestInfo.getTenantId() == null) {
			requestInfo.setTenantId(tenantId);
		}
		List<BusinessService> businessServices = null;
		try {
			businessServices = microServiceUtil.getBusinessService(tenantId, bsCode, requestInfo, finSerMdms);
		} catch (VoucherCustomException e) {
			throw new CustomException(e.toString(), e.toString());
		}
		if (businessServices.isEmpty()) {
			throw new VoucherCustomException(ProcessStatus.FAILED,
					"Business service is not mapped with business code : " + bsCode);
		}
		List<BusinessService> collect = businessServices.stream().filter(bs -> bs.getCode().equals(bsCode))
				.collect(Collectors.toList());
		return collect;
	}

	/**
	 * 
	 * @param tenantId
	 * @param bsCode
	 * @return
	 * @throws Exception Function is used to get the TaxHeadMaster data which is
	 *                   mapped to business service code
	 */
	private List<TaxHeadMaster> getTaxHeadMasterByBusinessServiceCode(String tenantId, String bsCode,
			RequestInfo requestInfo, FinanceMdmsModel finSerMdms) throws CustomException {
		// Set tenantId inside requestInfo object
		if (requestInfo.getTenantId() == null) {
			requestInfo.setTenantId(tenantId);
		}
		List<TaxHeadMaster> taxHeadMasters = null;
		try {
			taxHeadMasters = microServiceUtil.getTaxHeadMasters(tenantId, bsCode, requestInfo, finSerMdms);
		} catch (VoucherCustomException e) {
			throw new CustomException(e.toString(), e.toString());
		}
		return taxHeadMasters;
	}

	/**
	 * 
	 * @param moduleName
	 * @param tenantId
	 * @return Function is used to return the module id which is configure in erp
	 *         setup based on module name
	 * @throws VoucherCustomException
	 */
	private EgModules getModuleIdByModuleName(String moduleName, String tenantId, RequestInfo requestInfo)
			throws VoucherCustomException {
//		requestInfo.setAuthToken(propertiesManager.getSiAuthToken());
		// Set tenantId inside requestInfo object
		if (requestInfo.getTenantId() == null) {
			requestInfo.setTenantId(tenantId);
		}
		VoucherRequest request = new VoucherRequest(tenantId, requestInfo, null);
		StringBuilder url = new StringBuilder(propertiesManager.getErpURLBytenantId(tenantId)
				+ propertiesManager.getModuleIdSearchUrl() + "?moduleName=" + moduleName);

		// Set custom Host header
		String hostHeader = tenantId.split(Pattern.quote("."))[1];
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("SchemaName", hostHeader);

		try {
			// return mapper.convertValue(serviceRequestRepository.fetchResult(url, request,
			// tenantId), EgModules.class);

			return mapper.convertValue(serviceRequestRepository.fetchResultWithHeaders(url, request, tenantId, headers),
					EgModules.class);

		} catch (Exception e) {
			LOGGER.error("ERROR while checking the module id for module name {}, default value 10 is considered.",
					moduleName);
		}
		return null;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.egov.receipt.consumer.service.VoucherService#isTenantEnabledInFinanceModule(org.egov.receipt.consumer.model.ReceiptReq,
	 *      org.egov.receipt.consumer.model.FinanceMdmsModel) Method which is used
	 *      to check whether Tenant is enabled in Finance module or not.
	 */
	@Override
	public boolean isTenantEnabledInFinanceModule(ReceiptReq req, FinanceMdmsModel finSerMdms)
			throws VoucherCustomException {
		Receipt receipt = req.getReceipt().get(0);
		String tenantId = receipt.getTenantId();
		Bill bill = receipt.getBill().get(0);
		String bsCode = req.getReceipt().stream().map(Receipt::getBill).flatMap(List::stream)
				.map(Bill::getBusinessService).filter(Objects::nonNull).collect(Collectors.joining(","));
		bsCode = bsCode != null && !bsCode.isEmpty() ? bsCode : bill.getBillDetails().get(0).getBusinessService();
		List<Tenant> tenantList = microServiceUtil.getFinanceTenantList(tenantId, bsCode, req.getRequestInfo(),
				finSerMdms);
		List<Tenant> collect = tenantList.stream().filter(tenant -> tenant.getCode().equals(tenantId))
				.collect(Collectors.toList());
		if (collect.isEmpty()) {
			throw new VoucherCustomException(ProcessStatus.NA,
					"TenantId " + tenantId + " is not enabled in Finance module.");
		}
		return true;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.egov.receipt.consumer.service.VoucherService#getVoucherByServiceAndRefDoc(org.egov.receipt.consumer.model.RequestInfo,
	 *      java.lang.String, java.lang.String, java.lang.String) Method which is
	 *      used to fetch the voucher details associated with business service and
	 *      reference documents.
	 */
	@Override
	public VoucherResponse getVoucherByServiceAndRefDoc(RequestInfo requestInfo, String tenantId, String serviceCode,
			String referenceDoc) throws VoucherCustomException, UnsupportedEncodingException {
//		requestInfo.setAuthToken(propertiesManager.getSiAuthToken());
		// Set tenantId inside requestInfo object
		if (requestInfo.getTenantId() == null) {
			requestInfo.setTenantId(tenantId);
		}
		VoucherRequest request = new VoucherRequest(tenantId, requestInfo, null);
		request.setRequestInfo(requestInfo);

		StringBuilder url = new StringBuilder(propertiesManager.getErpURLBytenantId(tenantId))
				.append(propertiesManager.getVoucherSearchByRefUrl()).append("?");
		if (serviceCode != null && !serviceCode.isEmpty()) {
			url.append("servicecode=").append(serviceCode);
		}
		if (referenceDoc != null & !referenceDoc.isEmpty()) {
			url.append("&referencedocument=").append(URLEncoder.encode(referenceDoc, "UTF-8"));
		}

		// Set custom Host header
		String hostHeader = tenantId.split(Pattern.quote("."))[1];
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("SchemaName", hostHeader);

		try {
			// return mapper.convertValue(serviceRequestRepository.fetchResult(url, request,
			// tenantId), VoucherResponse.class);
			return mapper.convertValue(serviceRequestRepository.fetchResultWithHeaders(url, request, tenantId, headers),
					VoucherResponse.class);
		} catch (Exception e) {
			LOGGER.error("ERROR while fetching the voucher based on Service and Reference document");
		}
		return null;
	}

	@Override
	public VoucherResponse getVouchers(VoucherSearchCriteria criteria, RequestInfo requestInfo, String tenantId)
			throws VoucherCustomException {
		// Set tenantId inside requestInfo object
		if (requestInfo.getTenantId() == null) {
			requestInfo.setTenantId(tenantId);
		}
		VoucherSearchRequest request = new VoucherSearchRequest();
		request.setRequestInfo(requestInfo);
		request.setTenantId(tenantId);
		criteria.setTenantId(tenantId);
		StringBuilder url = new StringBuilder(propertiesManager.getErpURLBytenantId(tenantId))
				.append(propertiesManager.getVoucherSearchUrl()).append("?");
		prepareQueryString(url, criteria);
		try {
			return mapper.convertValue(serviceRequestRepository.fetchResult(url, request, tenantId),
					VoucherResponse.class);
		} catch (Exception e) {
			LOGGER.error("ERROR while fetching the voucher based on Service and Reference document");
		}
		return null;
	}

	@Override
	public VoucherResponse processReversalVoucher(List<InstrumentContract> instruments, RequestInfo requestInfo) {
		Set<String> receiptVoucherNumbers = instruments.stream().map(InstrumentContract::getInstrumentVouchers)
				.flatMap(x -> x.stream()).map(InstrumentVoucherContract::getVoucherHeaderId)
				.collect(Collectors.toSet());
		Set<Long> payInSlipIds = instruments.stream().map(InstrumentContract::getPayinSlipId).map(Long::parseLong)
				.collect(Collectors.toSet());
		String tenantId = instruments.get(0).getTenantId();
		VoucherResponse reversalVoucherResponse = null;
		try {
			Long dishonorDate = instruments.get(0).getDishonor().getDishonorDate();
			VoucherResponse rvResponse = this.getVouchers(
					new VoucherSearchCriteria().builder().voucherNumbers(receiptVoucherNumbers).build(), requestInfo,
					tenantId);
			VoucherResponse pisResponse = this.getVouchers(
					new VoucherSearchCriteria().builder().ids(payInSlipIds).build(), requestInfo, tenantId);
			if (!rvResponse.getVouchers().isEmpty() && !pisResponse.getVouchers().isEmpty()) {
				Voucher reversalVoucher = rvResponse.getVouchers().get(0);
				Map<String, AccountDetail> rvGlcodeMap = rvResponse.getVouchers().get(0).getLedgers().stream()
						.collect(Collectors.toMap(AccountDetail::getGlcode, java.util.function.Function.identity()));
				Map<String, AccountDetail> pisGlCodeMap = pisResponse.getVouchers().get(0).getLedgers().stream()
						.collect(Collectors.toMap(AccountDetail::getGlcode, java.util.function.Function.identity()));
				List<AccountDetail> ledgerForReversalVoucher = this.prepareLedgerForReversalVoucher(rvGlcodeMap,
						pisGlCodeMap);
				reversalVoucher.setLedgers(ledgerForReversalVoucher);
				this.prepareVoucherDetailsForReversalVoucher(reversalVoucher, dishonorDate);
				reversalVoucherResponse = this.createVoucher(Collections.singletonList(reversalVoucher), requestInfo,
						tenantId);
				LOGGER.error("reversalVoucherResponse :: {}", reversalVoucherResponse.getVouchers());
			}

		} catch (VoucherCustomException e) {
			e.printStackTrace();
		}
		return reversalVoucherResponse;
	}

	private void prepareQueryString(StringBuilder url, VoucherSearchCriteria criteria) {
		if (criteria.getTenantId() != null && !criteria.getTenantId().isEmpty()) {
			url.append("tenantId=").append(criteria.getTenantId());
		}
		if (criteria.getIds() != null && !criteria.getIds().isEmpty()) {
			String collect = criteria.getIds().stream().map(id -> id.toString()).collect(Collectors.joining(", "));
			url.append("&ids=").append(collect);
		}
		if (criteria.getVoucherNumbers() != null && !criteria.getVoucherNumbers().isEmpty()) {
			url.append("&voucherNumbers=").append(String.join(", ", criteria.getVoucherNumbers()));
		}
	}

	private List<AccountDetail> prepareLedgerForReversalVoucher(Map<String, AccountDetail> rvGlcodeMap,
			Map<String, AccountDetail> pisGlCodeMap) {
		Double drMaountToBank = new Double(0);
		Double crMaountToBank = new Double(0);
		for (String pis : pisGlCodeMap.keySet()) {
			if (rvGlcodeMap.get(pis) != null) {
				drMaountToBank = rvGlcodeMap.get(pis).getDebitAmount();
				crMaountToBank = rvGlcodeMap.get(pis).getCreditAmount();
				rvGlcodeMap.remove(pis);
			} else {
				AccountDetail value = pisGlCodeMap.get(pis);
				value.setCreditAmount(crMaountToBank);
				value.setDebitAmount(drMaountToBank);
				rvGlcodeMap.put(pis, value);
			}
		}
		rvGlcodeMap.values().stream().forEach(ad -> {
			Double creditAmount = ad.getCreditAmount();
			Double debitAmount = ad.getDebitAmount();
			ad.setCreditAmount(debitAmount);
			ad.setDebitAmount(creditAmount);
		});

		return rvGlcodeMap.values().stream().collect(Collectors.toList());
	}

	private void prepareVoucherDetailsForReversalVoucher(Voucher reversalVoucher, Long dishonorDate) {
		reversalVoucher.setName(REVERSAL_VOUCHER_NAME);
		reversalVoucher.setType(REVERSAL_VOUCHER_TYPE);
		reversalVoucher.setVoucherNumber("");
		reversalVoucher.setVoucherDate(dateFormatter.format(new Date(dishonorDate)));
		reversalVoucher.setReferenceDocument(null);
		reversalVoucher.setServiceName(null);
	}
}
