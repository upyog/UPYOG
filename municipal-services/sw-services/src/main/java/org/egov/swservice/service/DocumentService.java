package org.egov.swservice.service;

import org.egov.common.contract.request.RequestInfo;
import org.egov.swservice.repository.DocumentRepository;
import org.egov.swservice.web.models.DocumentRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DocumentService {

	@Autowired
	private DocumentRepository documentRepository;

	public void saveDocuments(DocumentRequest documentRequest, RequestInfo requestInfo) {
		documentRepository.saveDocuments(documentRequest.getDocuments());
	}
}