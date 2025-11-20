package org.upyog.tp.service;

import org.egov.common.contract.request.RequestInfo;
import org.upyog.tp.web.models.treePruning.TreePruningBookingDetail;
import org.upyog.tp.web.models.treePruning.TreePruningBookingRequest;
import org.upyog.tp.web.models.treePruning.TreePruningBookingSearchCriteria;

import digit.models.coremodels.PaymentRequest;

import java.util.List;

public interface TreePruningService {

    public TreePruningBookingDetail createNewTreePruningBookingRequest(TreePruningBookingRequest treePruningBookingRequest);

    public List<TreePruningBookingDetail> getTreePruningBookingDetails(RequestInfo requestInfo, TreePruningBookingSearchCriteria treePruningBookingSearchCriteria);

    public Integer getApplicationsCount(TreePruningBookingSearchCriteria treePruningBookingSearchCriteria, RequestInfo requestInfo);

    public void updateTreePruningBooking(PaymentRequest paymentRequest, String applicationStatus);

    public TreePruningBookingDetail updateTreePruningBooking(TreePruningBookingRequest treePruningRequest, String applicationStatus);

}
