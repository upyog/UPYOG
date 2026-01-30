package org.upyog.tp.web.controllers;

import java.util.List;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.upyog.tp.constant.TreePruningConstants;
import org.upyog.tp.service.TreePruningService;
import org.upyog.tp.util.TreePruningUtil;
import org.upyog.tp.validator.ValidatorService;
import org.upyog.tp.web.models.ResponseInfo;
import org.upyog.tp.web.models.treePruning.TreePruningBookingDetail;
import org.upyog.tp.web.models.treePruning.TreePruningBookingRequest;
import org.upyog.tp.web.models.treePruning.TreePruningBookingResponse;
import org.upyog.tp.web.models.treePruning.TreePruningBookingSearchCriteria;
import org.upyog.tp.web.models.treePruning.TreePruningSearchResponse;
import org.upyog.tp.web.models.ResponseInfo.StatusEnum;

import digit.models.coremodels.RequestInfoWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2025-01-16T15:46:56.897+05:30")

@Controller
@Slf4j
@Tag(name = "Tree Pruning", description = "APIs for Tree Pruning")
public class TreePruningController {

    @Autowired
    private TreePruningService treePruningService;

    @Autowired
    private ValidatorService validatorService;

    @PostMapping("/tree-pruning/v1/_create")
    @Operation(summary = "Create application details", description = "Create application details")
    public ResponseEntity<TreePruningBookingResponse> createTreePruningBooking(
            @RequestBody TreePruningBookingRequest treePruningbookingRequest) {
        log.info("treePruningbookingRequest : {}" , treePruningbookingRequest);
        validatorService.validateRequest(treePruningbookingRequest);
        TreePruningBookingDetail treePruningDetail = treePruningService.createNewTreePruningBookingRequest(treePruningbookingRequest);
        ResponseInfo info = TreePruningUtil.createReponseInfo(treePruningbookingRequest.getRequestInfo(),
                TreePruningConstants.TP_BOOKING_CREATED, StatusEnum.SUCCESSFUL);
        TreePruningBookingResponse response = TreePruningBookingResponse.builder()
                .treePruningBookingApplication(treePruningDetail)
                .responseInfo(info).build();
        return new ResponseEntity<TreePruningBookingResponse>(response, HttpStatus.OK);
    }

    @PostMapping("/tree-pruning/v1/_search")
    @Operation(summary = "Search application details", description = "Search application details")
    public ResponseEntity<TreePruningSearchResponse> searchTreePruningBookingDetails(
            @Valid @RequestBody RequestInfoWrapper requestInfoWrapper,
            @ModelAttribute TreePruningBookingSearchCriteria treePruningBookingSearchCriteria) {

        List<TreePruningBookingDetail> applications = null;
        Integer count = 0;

        applications = treePruningService.getTreePruningBookingDetails(requestInfoWrapper.getRequestInfo(),
                treePruningBookingSearchCriteria);

        count = treePruningService.getApplicationsCount(treePruningBookingSearchCriteria,
                requestInfoWrapper.getRequestInfo());

        /*
         * Create Response Info with success status and used utilize method to generate
         * standardized response
         */
        ResponseInfo responseInfo = TreePruningUtil.createReponseInfo(requestInfoWrapper.getRequestInfo(),
                TreePruningConstants.TP_BOOKING_DETAIL_FOUND, StatusEnum.SUCCESSFUL);
        /*
         * Build search response using builder and retrieve booking details and response
         * metadata
         */
        TreePruningSearchResponse response = TreePruningSearchResponse.builder()
                .treePruningBookingDetails(applications).responseInfo(responseInfo).count(count).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/tree-pruning/v1/_update")
    @Operation(summary = "Update application details", description = "Update application details")
    public ResponseEntity<TreePruningBookingResponse> treePruningUpdate(
            @RequestBody TreePruningBookingRequest treePruningRequest) {

        TreePruningBookingDetail treePruningDetail = treePruningService.updateTreePruningBooking(treePruningRequest, null);

        TreePruningBookingResponse response = TreePruningBookingResponse.builder().treePruningBookingApplication(treePruningDetail)
                .responseInfo(TreePruningUtil.createReponseInfo(treePruningRequest.getRequestInfo(),
                        TreePruningConstants.APPLICATION_UPDATED, StatusEnum.SUCCESSFUL))
                .build();
        return new ResponseEntity<TreePruningBookingResponse>(response, HttpStatus.OK);
    }
}
