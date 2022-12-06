package org.egov.filemgmnt.web.controllers;

import java.util.List;

import org.egov.filemgmnt.service.CommunicationFileManagementService;
import org.egov.filemgmnt.util.ResponseInfoFactory;
import org.egov.filemgmnt.web.models.CommunicationFile;
import org.egov.filemgmnt.web.models.CommunicationFileRequest;
import org.egov.filemgmnt.web.models.CommunicationFileResponse;
import org.egov.filemgmnt.web.models.CommunicationFileSearchCriteria;
import org.egov.filemgmnt.web.models.RequestInfoWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Communication File Management")
@RestController
@RequestMapping("/v1")
public class CommunicationFileManagementController {

    private final ResponseInfoFactory responseInfoFactory;
    private final CommunicationFileManagementService communicationService;

    @Autowired
    CommunicationFileManagementController(ResponseInfoFactory responseInfoFactory,
                                          CommunicationFileManagementService communicationService) {
        this.responseInfoFactory = responseInfoFactory;
        this.communicationService = communicationService;

    }

    @PostMapping("/officecommunication/_create")
    public ResponseEntity<CommunicationFileResponse> create(@RequestBody CommunicationFileRequest request) {
        List<CommunicationFile> files = communicationService.create(request);

        CommunicationFileResponse response = CommunicationFileResponse.builder()
                                                                      .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(),
                                                                                                                                          Boolean.TRUE))
                                                                      .communicationFiles(files)
                                                                      .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/officecommuication/_update")
    public ResponseEntity<CommunicationFileResponse> update(@RequestBody CommunicationFileRequest request) {

        List<CommunicationFile> files = communicationService.update(request);

        CommunicationFileResponse response = CommunicationFileResponse.builder()
                                                                      .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(),
                                                                                                                                          Boolean.TRUE))
                                                                      .communicationFiles(files)
                                                                      .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/officecommuication/_search")
    public ResponseEntity<CommunicationFileResponse> search(@RequestBody RequestInfoWrapper request,
                                                            @ModelAttribute CommunicationFileSearchCriteria criteria) {

        List<CommunicationFile> files = communicationService.search(criteria, request.getRequestInfo());

        CommunicationFileResponse response = CommunicationFileResponse.builder()
                                                                      .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(),
                                                                                                                                          Boolean.TRUE))
                                                                      .communicationFiles(files)
                                                                      .build();
        return ResponseEntity.ok(response);
    }
}
