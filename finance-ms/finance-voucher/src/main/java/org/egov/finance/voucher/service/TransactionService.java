package org.egov.finance.voucher.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.egov.finance.voucher.daoimpl.AccountdetailtypeHibernateDAO;
import org.egov.finance.voucher.entity.AccountDetailType;
import org.egov.finance.voucher.entity.CChartOfAccounts;
import org.egov.finance.voucher.entity.CVoucherHeader;
import org.egov.finance.voucher.exception.ApplicationRuntimeException;
import org.egov.finance.voucher.model.Transaxtion;
import org.egov.finance.voucher.model.TransaxtionParameter;
import org.egov.finance.voucher.repository.ChartOfAccountsRepository;
import org.egov.finance.voucher.repository.FunctionRepository;
import org.egov.finance.voucher.util.VoucherConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TransactionService {

	private static final Logger LOGGER = LoggerFactory.getLogger(TransactionService.class);

	@Autowired
	private ChartOfAccountsRepository chartOfAccountsRepository;

	@Autowired
	private FunctionRepository functionRepository;

	@Autowired
	private AccountdetailtypeHibernateDAO accountdetailtypeHibernateDAO;

	public List<Transaxtion> createTransaction(Map<String, Object> headerDetails,
			List<Map<String, Object>> accountdetails, List<Map<String, Object>> subledgerDetails, CVoucherHeader vh) {
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("Start | createTransaction ");
		final List<Transaxtion> transaxtionList = new ArrayList<Transaxtion>();
		try {
			Integer voucherLineId = 1;
			for (final Map<String, Object> accDetailMap : accountdetails) {
				final String glcode = accDetailMap.get(VoucherConstant.GLCODE).toString();

				final String debitAmount = accDetailMap.get(VoucherConstant.DEBITAMOUNT).toString();
				final String creditAmount = accDetailMap.get(VoucherConstant.CREDITAMOUNT).toString();
				String functionId = null;
				String functioncode = null;
				if (null != accDetailMap.get(VoucherConstant.NARRATION))
					accDetailMap.get(VoucherConstant.NARRATION).toString();
				if (null != accDetailMap.get(VoucherConstant.FUNCTIONCODE)
						&& "" != accDetailMap.get(VoucherConstant.FUNCTIONCODE)) {
					functioncode = accDetailMap.get(VoucherConstant.FUNCTIONCODE).toString();

					functionId = functionRepository
							.findByCode(accDetailMap.get(VoucherConstant.FUNCTIONCODE).toString()).getId().toString();

//					functionId = functionDAO
//							.getFunctionByCode(accDetailMap.get(VoucherConstant.FUNCTIONCODE).toString()).getId()
//							.toString();
					if (LOGGER.isDebugEnabled())
						LOGGER.debug("functionId>>>>>>>> " + functionId);
				}

				final CChartOfAccounts chartOfAcc = chartOfAccountsRepository.findByGlcode(glcode);
				// final CChartOfAccounts chartOfAcc =
				// chartOfAccountsDAO.getCChartOfAccountsByGlCode(glcode);
				/*
				 * VoucherDetail voucherDetail = new VoucherDetail();
				 * voucherDetail.setLineId(lineId++); voucherDetail.setVoucherHeaderId(vh);
				 * voucherDetail.setGlCode(chartOfAcc.getGlcode());
				 * voucherDetail.setAccountName(chartOfAcc.getName());
				 * voucherDetail.setDebitAmount(new BigDecimal(debitAmount));
				 * voucherDetail.setCreditAmount(new BigDecimal(creditAmount));
				 * voucherDetail.setNarration(narration); // insert into voucher detail.
				 * insertIntoVoucherDetail(voucherDetail); vh.addVoucherDetail(voucherDetail);
				 */
				final Transaxtion transaction = new Transaxtion();
				transaction.setGlCode(chartOfAcc.getGlcode());
				transaction.setGlName(chartOfAcc.getName());
				transaction.setVoucherLineId(String.valueOf(voucherLineId++));
				transaction.setVoucherHeaderId(vh.getId().toString());
				transaction.setCrAmount(creditAmount);
				transaction.setDrAmount(debitAmount);
				transaction.setFunctionId(functionId);
				if (headerDetails != null && headerDetails.get("billid") != null)
					transaction.setBillId((Long) headerDetails.get("billid"));

				final ArrayList reqParams = new ArrayList();
				for (final Map<String, Object> sublegDetailMap : subledgerDetails) {

					final String detailGlCode = sublegDetailMap.get(VoucherConstant.GLCODE).toString();
					final String detailtypeid = sublegDetailMap.get(VoucherConstant.DETAILTYPEID).toString();
					if (sublegDetailMap.containsKey(VoucherConstant.FUNCTIONCODE)
							&& null != sublegDetailMap.get(VoucherConstant.FUNCTIONCODE)
							&& "" != sublegDetailMap.get(VoucherConstant.FUNCTIONCODE)) {
						final String detailFunctionCode = sublegDetailMap.get(VoucherConstant.FUNCTIONCODE).toString();
						if (glcode.equals(detailGlCode) && functioncode != null
								&& functioncode.equals(detailFunctionCode)) {
							final TransaxtionParameter reqData = new TransaxtionParameter();
							final AccountDetailType adt = (AccountDetailType) accountdetailtypeHibernateDAO
									.findById(Integer.valueOf(detailtypeid), false);
							reqData.setDetailName(adt.getAttributename());
							reqData.setGlcodeId(chartOfAcc.getId().toString());
							if (null != sublegDetailMap.get(VoucherConstant.DEBITAMOUNT)
									&& new BigDecimal(sublegDetailMap.get(VoucherConstant.DEBITAMOUNT).toString())
											.compareTo(BigDecimal.ZERO) != 0)
								reqData.setDetailAmt(sublegDetailMap.get(VoucherConstant.DEBITAMOUNT).toString());
							else
								reqData.setDetailAmt(sublegDetailMap.get(VoucherConstant.CREDITAMOUNT).toString());

							reqData.setDetailKey(sublegDetailMap.get(VoucherConstant.DETAILKEYID).toString());
							reqData.setDetailTypeId(detailtypeid);
							reqData.setTdsId(
									sublegDetailMap.get("tdsId") != null ? sublegDetailMap.get("tdsId").toString()
											: null);
							reqParams.add(reqData);
						}
					} else if (glcode.equals(detailGlCode)) {
						final TransaxtionParameter reqData = new TransaxtionParameter();
						final AccountDetailType adt = (AccountDetailType) accountdetailtypeHibernateDAO
								.findById(Integer.valueOf(detailtypeid), false);
						reqData.setDetailName(adt.getAttributename());
						reqData.setGlcodeId(chartOfAcc.getId().toString());
						if (null != sublegDetailMap.get(VoucherConstant.DEBITAMOUNT)
								&& new BigDecimal(sublegDetailMap.get(VoucherConstant.DEBITAMOUNT).toString())
										.compareTo(BigDecimal.ZERO) != 0)
							reqData.setDetailAmt(sublegDetailMap.get(VoucherConstant.DEBITAMOUNT).toString());
						else
							reqData.setDetailAmt(sublegDetailMap.get(VoucherConstant.CREDITAMOUNT).toString());

						reqData.setDetailKey(sublegDetailMap.get(VoucherConstant.DETAILKEYID).toString());
						reqData.setDetailTypeId(detailtypeid);
						reqData.setTdsId(
								sublegDetailMap.get("tdsId") != null ? sublegDetailMap.get("tdsId").toString() : null);
						reqParams.add(reqData);
					}

				}
				if (reqParams != null && reqParams.size() > 0)
					transaction.setTransaxtionParameters(reqParams);
				transaxtionList.add(transaction);
			}
		} catch (final ApplicationRuntimeException e) {
			LOGGER.error("Exception occured while posting data into voucher detail and transaction");
			throw new ApplicationRuntimeException(
					"Exception occured while posting data into voucher detail and transaction" + e.getMessage());
		}
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("END | createTransaction ");
		return transaxtionList;
	}

}
