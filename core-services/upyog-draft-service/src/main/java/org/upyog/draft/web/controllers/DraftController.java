package org.upyog.draft.web.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Draft", description = "Save, search, and lifecycle APIs for in-progress municipal applications")
public class DraftController {

    private final DraftService draftService;

    public DraftController(DraftService draftService) {
        this.draftService = draftService;
    }

    @PostMapping("/_save")
    @Operation(summary = "Save draft", description = "Create a new draft when draftId is absent, or update an existing draft. "
            + "Update requires draftId to exist in ug_draft_detail for the authenticated user; otherwise DRAFT_NOT_FOUND is returned.")
    public ResponseEntity<DraftResponse> save(@Valid @RequestBody DraftRequest request) {
        return new ResponseEntity<>(draftService.save(request), HttpStatus.OK);
    }

    @PostMapping("/_search")
    @Operation(summary = "Search drafts", description = "List drafts for the authenticated user")
    public ResponseEntity<DraftResponse> search(@Valid @RequestBody DraftSearchRequest request) {
        return new ResponseEntity<>(draftService.search(request), HttpStatus.OK);
    }

    @PostMapping("/_count")
    @Operation(summary = "Count drafts", description = "Count active drafts for the login dashboard widget")
    public ResponseEntity<DraftResponse> count(@Valid @RequestBody DraftSearchRequest request) {
        return new ResponseEntity<>(draftService.count(request), HttpStatus.OK);
    }

    @PostMapping("/_delete")
    @Operation(summary = "Delete draft", description = "Mark a draft as discarded by the citizen")
    public ResponseEntity<DraftResponse> delete(@Valid @RequestBody DraftDeleteRequest request) {
        DraftRequest draftRequest = DraftRequest.builder()
                .requestInfo(request.getRequestInfo())
                .draft(request.getDraft())
                .build();
        return new ResponseEntity<>(draftService.delete(draftRequest), HttpStatus.OK);
    }

    @PostMapping("/_markSubmitted")
    @Operation(summary = "Mark draft submitted", description = "Called by domain services after a successful application create")
    public ResponseEntity<DraftResponse> markSubmitted(@Valid @RequestBody DraftMarkSubmittedRequest request) {
        DraftRequest draftRequest = DraftRequest.builder()
                .requestInfo(request.getRequestInfo())
                .draft(request.getDraft())
                .build();
        return new ResponseEntity<>(draftService.markSubmitted(draftRequest), HttpStatus.OK);
    }
}
