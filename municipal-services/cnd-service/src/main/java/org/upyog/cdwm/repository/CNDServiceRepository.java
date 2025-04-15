package org.upyog.cdwm.repository;

import org.upyog.cdwm.web.models.CNDApplicationDetail;
import org.upyog.cdwm.web.models.CNDApplicationRequest;
import org.upyog.cdwm.web.models.CNDServiceSearchCriteria;
import org.upyog.cdwm.web.models.DocumentDetail;
import org.upyog.cdwm.web.models.FacilityCenterDetail;
import org.upyog.cdwm.web.models.WasteTypeDetail;

import java.util.List;

public interface CNDServiceRepository {

	void saveCNDApplicationDetail(CNDApplicationRequest cndApplicationRequest);

	List<CNDApplicationDetail> getCNDApplicationDetail(CNDServiceSearchCriteria cndServiceSearchCriteria);

	Integer getCNDApplicationsCount(CNDServiceSearchCriteria criteria);

	void updateCNDApplicationDetail(CNDApplicationRequest cndApplicationRequest);

	void saveCNDWasteAndDocumentDetail(List<WasteTypeDetail> wasteTypeDetails, List<DocumentDetail> documentDetails,
			String applicationId);

	List<WasteTypeDetail> getCNDWasteTypeDetail(CNDServiceSearchCriteria cndServiceSearchCriteria);
	
	List<DocumentDetail> getCNDDocumentDetail(CNDServiceSearchCriteria cndServiceSearchCriteria);

	List<FacilityCenterDetail> getCNDFacilityCenterDetail(CNDServiceSearchCriteria cndServiceSearchCriteria);

}
