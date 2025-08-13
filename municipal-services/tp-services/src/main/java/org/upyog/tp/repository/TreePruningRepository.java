package org.upyog.tp.repository;

import java.util.List;
import org.upyog.tp.web.models.treePruning.TreePruningBookingDetail;
import org.upyog.tp.web.models.treePruning.TreePruningBookingRequest;
import org.upyog.tp.web.models.treePruning.TreePruningBookingSearchCriteria;

public interface TreePruningRepository {

    void saveTreePruningBooking(TreePruningBookingRequest treePruningBookingRequest);

    List<TreePruningBookingDetail> getTreePruningBookingDetails(TreePruningBookingSearchCriteria treePruningBookingSearchCriteria);

    Integer getApplicationsCount(TreePruningBookingSearchCriteria criteria);

    void updateTreePruningBooking(TreePruningBookingRequest treePruningBookingRequest);
}
