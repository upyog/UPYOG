package org.upyog.draft.web.controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.upyog.draft.service.DraftService;
import org.upyog.draft.web.models.DraftDeleteRequest;
import org.upyog.draft.web.models.DraftMarkSubmittedRequest;
import org.upyog.draft.web.models.DraftRequest;
import org.upyog.draft.web.models.DraftResponse;
import org.upyog.draft.web.models.DraftSearchRequest;

@RestController
@RequestMapping("/draft/v1")
public class DraftController {

    private final DraftService draftService;

    public DraftController(DraftService draftService) {
        this.draftService = draftService;
    }

    @PostMapping("/_save")
    public ResponseEntity<DraftResponse> save(@Valid @RequestBody DraftRequest request) {
        return new ResponseEntity<>(draftService.save(request), HttpStatus.OK);
    }

    @PostMapping("/_search")
    public ResponseEntity<DraftResponse> search(@Valid @RequestBody DraftSearchRequest request) {
        return new ResponseEntity<>(draftService.search(request), HttpStatus.OK);
    }

    @PostMapping("/_count")
    public ResponseEntity<DraftResponse> count(@Valid @RequestBody DraftSearchRequest request) {
        return new ResponseEntity<>(draftService.count(request), HttpStatus.OK);
    }

    @PostMapping("/_delete")
    public ResponseEntity<DraftResponse> delete(@Valid @RequestBody DraftDeleteRequest request) {
        DraftRequest draftRequest = DraftRequest.builder()
                .requestInfo(request.getRequestInfo())
                .draft(request.getDraft())
                .build();
        return new ResponseEntity<>(draftService.delete(draftRequest), HttpStatus.OK);
    }

    @PostMapping("/_markSubmitted")
    public ResponseEntity<DraftResponse> markSubmitted(@Valid @RequestBody DraftMarkSubmittedRequest request) {
        DraftRequest draftRequest = DraftRequest.builder()
                .requestInfo(request.getRequestInfo())
                .draft(request.getDraft())
                .build();
        return new ResponseEntity<>(draftService.markSubmitted(draftRequest), HttpStatus.OK);
    }
}
