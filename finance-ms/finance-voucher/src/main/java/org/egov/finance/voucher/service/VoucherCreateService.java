package org.egov.finance.voucher.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.egov.finance.voucher.entity.CVoucherHeader;
import org.egov.finance.voucher.entity.Voucher;
import org.egov.finance.voucher.exception.ApplicationRuntimeException;
import org.egov.finance.voucher.exception.TaskFailedException;
import org.egov.finance.voucher.exception.ValidationException;
import org.egov.finance.voucher.model.AccountDetailModel;
import org.egov.finance.voucher.model.SubledgerDetailModel;
import org.egov.finance.voucher.model.request.VoucherRequest;
import org.egov.finance.voucher.model.response.VoucherResponse;
import org.egov.finance.voucher.util.VoucherConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class VoucherCreateService {

	@Autowired
	private CreateVoucher createVoucher;

	@Autowired
	private ChartOfAccountDetailService chartOfAccountDetailService;
	@Autowired
	private AppConfigValueService appConfigValuesService;

	public VoucherResponse processVoucherCreate(VoucherRequest voucherRequest) {
		VoucherResponse response = VoucherResponse.builder().build();

		for (Voucher voucher : voucherRequest.getVouchers()) {
			try {
				Map<String, Object> headerDetails = buildHeaderDetails(voucher);
				List<Map<String, Object>> accountdetails = new ArrayList<>();
				List<Map<String, Object>> subledgerDetails = new ArrayList<>();

				for (AccountDetailModel ac : voucher.getLedgers()) {
					Map<String, Object> detailMap = new HashMap<>();
					detailMap.put(VoucherConstant.GLCODE, ac.getGlcode());
					detailMap.put(VoucherConstant.DEBITAMOUNT, ac.getDebitAmount());
					detailMap.put(VoucherConstant.CREDITAMOUNT, ac.getCreditAmount());

					if (ac.getFunctionId() != null)
						detailMap.put(VoucherConstant.FUNCTIONCODE, ac.getFunctionId());

					accountdetails.add(detailMap);

					for (SubledgerDetailModel sl : ac.getSubledgerDetails()) {
						if (chartOfAccountDetailService.getByGlcodeAndDetailTypeId(ac.getGlcode(),
								sl.getAccountDetailTypeId()) != null) {

							Map<String, Object> subledgertDetailMap = new HashMap<>();
							subledgertDetailMap.put(VoucherConstant.GLCODE, ac.getGlcode());
							subledgertDetailMap.put(VoucherConstant.DETAILAMOUNT, sl.getAmount());
							subledgertDetailMap.put(VoucherConstant.DETAIL_TYPE_ID, sl.getAccountDetailTypeId());
							subledgertDetailMap.put(VoucherConstant.DETAIL_KEY_ID, sl.getDetailKeyId());
							subledgerDetails.add(subledgertDetailMap);
						}
					}
				}

				CVoucherHeader voucherHeader = createVoucher.createVoucher(headerDetails, accountdetails,
						subledgerDetails);
				voucher.setId(voucherHeader.getId());
				voucher.setVoucherNumber(voucherHeader.getVoucherNumber());
				response.getVouchers().add(voucher);

			} catch (ValidationException | ApplicationRuntimeException e) {
				throw e;
			} catch (ParseException e) {
				throw new ApplicationRuntimeException(e.getMessage());
			} catch (TaskFailedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return response;
	}

	private Map<String, Object> buildHeaderDetails(Voucher voucher) throws ParseException {
		Map<String, Object> headerDetails = new HashMap<>();
		SimpleDateFormat fm = new SimpleDateFormat("dd/MM/yyyy");
		Date vDate = fm.parse(voucher.getVoucherDate());

		headerDetails.put(VoucherConstant.DEPARTMENTCODE, voucher.getDepartment());
		headerDetails.put(VoucherConstant.VOUCHERNAME, voucher.getName());
		headerDetails.put(VoucherConstant.VOUCHERTYPE, voucher.getType());
		headerDetails.put(VoucherConstant.VOUCHERNUMBER, voucher.getVoucherNumber());
		headerDetails.put(VoucherConstant.VOUCHERDATE, vDate);
		headerDetails.put(VoucherConstant.DESCRIPTION, voucher.getDescription());
		headerDetails.put(VoucherConstant.MODULEID, voucher.getModuleId());

		if (voucher.getSource() != null)
			headerDetails.put(VoucherConstant.SOURCEPATH, voucher.getSource());

		if (voucher.getReferenceDocument() != null && !voucher.getReferenceDocument().isEmpty())
			headerDetails.put(VoucherConstant.REFERENCEDOC, voucher.getReferenceDocument());

		if (voucher.getServiceName() != null && !voucher.getServiceName().isEmpty())
			headerDetails.put(VoucherConstant.SERVICE_NAME, voucher.getServiceName());

		if (voucher.getFund() != null)
			headerDetails.put(VoucherConstant.FUNDCODE, voucher.getFund().getCode());

		if (voucher.getFunction() != null)
			headerDetails.put(VoucherConstant.FUNCTIONCODE, voucher.getFunction().getCode());

		if (voucher.getFunctionary() != null)
			headerDetails.put(VoucherConstant.FUNCTIONARYCODE, voucher.getFunctionary().getCode());

		if (voucher.getScheme() != null)
			headerDetails.put(VoucherConstant.SCHEMECODE, voucher.getScheme().getCode());

		if (voucher.getSubScheme() != null)
			headerDetails.put(VoucherConstant.SUBSCHEMECODE, voucher.getSubScheme().getCode());

		return headerDetails;
	}

}
