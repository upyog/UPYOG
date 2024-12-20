package org.hpud.service;

import org.hpud.entity.LandingPage;
import org.hpud.errorhandlers.HPUDLandingPageException;
import org.hpud.model.LandingPageRequest;
import org.hpud.model.RegistrationRequest;
import org.hpud.model.RegistrationResponse;
import org.hpud.repository.LandingPageRepository;
import org.hpud.util.LandingPageUtil;
import org.hpud.util.RegistrationPageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LandingPageService {

	@Autowired
	LandingPageRepository repo;

	@Autowired
	RegistrationPageUtil regUtil;

	public void fetchCount(LandingPageRequest request, LandingPageRequest response) throws HPUDLandingPageException {
		LandingPage pageObj = null;
		LandingPageUtil util = new LandingPageUtil();
		int count = 0;
		int count1 = repo.countTotal();
		try {
			pageObj = new LandingPage();
			pageObj.setBrowserName(request.getBrowserName());
			pageObj.setCount(count + 1);
			pageObj.setIpAddress(request.getIpAddress());
			repo.save(pageObj);
			response.setCount(count1);

		} catch (Exception e) {
			throw new HPUDLandingPageException(e.getMessage());
		}
	}

	public RegistrationResponse fetchRegistrationData(RegistrationRequest request, RegistrationResponse response)
			throws HPUDLandingPageException {
		try {
			if (request.getRegistrationNo().equalsIgnoreCase("HP/2023/123")) {
				response.setBusinessName(regUtil.BUSINESS_NAME);
				response.setCategoryOfBusiness(regUtil.CATEGORY_OF_BUSINESS);
				response.setDateOfCommencementOfBusiness(regUtil.DATE_OF_COMMENCEMENT);
				response.setNameOfEmployer(regUtil.NAME_OF_EMPLOYER);
				response.setNatureOfBusiness(regUtil.NATURE_OF_BUSINESS);
				response.setPostalAddress(regUtil.POSTAL_ADDRESS);
				response.setValidUptoDate(regUtil.VALID_UPTO_DATE);
			} else if (request.getRegistrationNo().equalsIgnoreCase("HP/2024/578")) {
				response.setBusinessName(regUtil.BUSINESS_NAME1);
				response.setCategoryOfBusiness(regUtil.CATEGORY_OF_BUSINESS1);
				response.setDateOfCommencementOfBusiness(regUtil.DATE_OF_COMMENCEMENT1);
				response.setNameOfEmployer(regUtil.NAME_OF_EMPLOYER1);
				response.setNatureOfBusiness(regUtil.NATURE_OF_BUSINESS1);
				response.setPostalAddress(regUtil.POSTAL_ADDRESS1);
				response.setValidUptoDate(regUtil.VALID_UPTO_DATE1);
			}
		}

		catch (Exception e) {
			throw new HPUDLandingPageException(e.getMessage());
		}
		return response;

	}

}
