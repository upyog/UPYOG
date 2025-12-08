package org.egov.finance.voucher.service;

import java.io.Serializable;

import org.egov.finance.voucher.daoimpl.FiscalPeriodHibernateDAO;
import org.egov.finance.voucher.entity.CVoucherHeader;
import org.egov.finance.voucher.entity.FiscalPeriod;
import org.egov.finance.voucher.exception.ApplicationRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VouchernumberGeneratorImpl implements VouchernumberGenerator {

	@Autowired
	private FiscalPeriodHibernateDAO fiscalPeriodHibernateDAO;
	@Autowired
	private GenericSequenceNumberGenerator genericSequenceNumberGenerator;

	/**
	 *
	 * Format fundcode/vouchertype/seqnumber/month/financialyear but sequence is
	 * running number for a year
	 * 
	 * @throws Exception
	 *
	 */
	@Override
	public String getNextNumber(final CVoucherHeader vh) {
		String voucherNumber;

		String sequenceName;

		final FiscalPeriod fiscalPeriod = fiscalPeriodHibernateDAO.getFiscalPeriodByDate(vh.getVoucherDate());
		if (fiscalPeriod == null)
			throw new ApplicationRuntimeException("Fiscal period is not defined for the voucher date");
		sequenceName = "sq_" + vh.getFundId().getIdentifier() + "_" + vh.getVoucherNumberPrefix() + "_"
				+ fiscalPeriod.getName();
		final Serializable nextSequence = genericSequenceNumberGenerator.getNextSequence(sequenceName);

		voucherNumber = String.format("%s/%s/%08d/%02d/%s", vh.getFundId().getIdentifier(), vh.getVoucherNumberPrefix(),
				nextSequence, vh.getVoucherDate().getMonth() + 1, fiscalPeriod.getCFinancialYear().getFinYearRange());

		return voucherNumber;
	}

////	 @Autowired
////	    private FiscalPeriodHibernateDAO fiscalPeriodHibernateDAO;
//	  private static final String SEQUENCE_PREFIX = "sq_";
//	    private static final String VOUCHER_NUMBER_FORMAT = "%s/%s/%08d/%02d/%s";
//
//	    @Autowired
//	    private FiscalPeriodRepository fiscalPeriodRepository;
//
//	    @Autowired
//	    private GenericSequenceNumberGenerator genericSequenceNumberGenerator;
//
//	    @Override
//	    public String getNextNumber(final CVoucherHeader voucherHeader) {
//	        // Step 1: Fetch FiscalPeriod based on voucher date
//	        Date voucherDate = voucherHeader.getVoucherDate();
//	        FiscalPeriod fiscalPeriod = fiscalPeriodRepository.findByVoucherDateBetween(voucherDate);
//
//	        if (fiscalPeriod == null) {
//	            throw new ApplicationRuntimeException("Fiscal period is not defined for the voucher date: " + voucherDate);
//	        }
//
//	        // Step 2: Prepare sequence name
//	        String sequenceName = SEQUENCE_PREFIX +
//	                voucherHeader.getFundId().getIdentifier() + "_" +
//	                voucherHeader.getVoucherNumberPrefix() + "_" +
//	                fiscalPeriod.getName().replaceAll("[^a-zA-Z0-9]", "_");
//
//	        // Step 3: Get next sequence number
//	        Serializable nextSequence = genericSequenceNumberGenerator.getNextSequence(sequenceName);
//
//	        // Step 4: Format voucher number
//	        String voucherNumber = String.format(
//	                VOUCHER_NUMBER_FORMAT,
//	                voucherHeader.getFundId().getIdentifier(),
//	                voucherHeader.getVoucherNumberPrefix(),
//	                nextSequence,
//	                voucherDate.getMonth() + 1,
//	                fiscalPeriod.getFinancialYear().getFinYearRange()
//	        );
//
//	        return voucherNumber;
//	    }
}
