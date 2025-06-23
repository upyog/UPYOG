package org.egov.finance.voucher.service;

import java.util.List;

import org.egov.finance.voucher.entity.CChartOfAccountDetail;
import org.egov.finance.voucher.repository.CChartOfAccountDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChartOfAccountDetailService  {

	 private final CChartOfAccountDetailRepository repository;

	    @Autowired
	    public ChartOfAccountDetailService(CChartOfAccountDetailRepository repository) {
	        this.repository = repository;
	    }

	    public List<CChartOfAccountDetail> getByGlcodeId(Long glcodeId) {
	        return repository.findByGlCodeId_Id(glcodeId);
	    }

	    public CChartOfAccountDetail getByGlcodeIdAndDetailTypeId(Long glcodeId, Integer accountDetailTypeId) {
	        return repository.findByGlCodeId_IdAndDetailTypeId_Id(glcodeId, accountDetailTypeId)
	                .orElse(null); // Or throw an exception if needed
	    }

	    public CChartOfAccountDetail getByGlcodeAndDetailTypeId(String glcode, Long long1) {
	        return repository.findByGlCodeId_GlcodeAndDetailTypeId_Id(glcode, long1)
	                .orElse(null);
	    }

		public List<org.egov.finance.voucher.model.CChartOfAccountDetailModel> findAllBy(String string) {
			// TODO Auto-generated method stub
			return null;
		}}
