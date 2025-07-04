package org.upyog.service;

import jakarta.validation.Valid;

import org.upyog.web.models.CommonDetails;
import org.upyog.web.models.ModuleSearchRequest;

public interface CommonService {
    CommonDetails getApplicationCommonDetails(@Valid ModuleSearchRequest request);
}

