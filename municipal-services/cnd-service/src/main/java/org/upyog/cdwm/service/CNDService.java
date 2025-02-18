package org.upyog.cdwm.service;

import org.upyog.cdwm.web.models.CNDApplicationDetail;
import org.upyog.cdwm.web.models.CNDApplicationRequest;

public interface CNDService {

    public CNDApplicationDetail createConstructionAndDemolitionRequest(CNDApplicationRequest cndApplicationRequest);
}
