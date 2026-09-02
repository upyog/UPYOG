package org.egov.refund.service;

import org.egov.refund.web.contracat.RefundActionRequest;
import org.egov.refund.web.contracat.RefundGetRequest;
import org.egov.refund.web.contracat.RefundRequest;
import org.egov.refund.web.contracat.RefundResponse;
import org.egov.refund.web.contracat.RefundSearchRequest;
import org.egov.refund.web.contracat.RefundSearchResponse;

public interface RefundService {

	RefundResponse create(RefundRequest request);

	RefundResponse get(RefundGetRequest request);

	RefundSearchResponse search(RefundSearchRequest request);

	RefundResponse process(RefundActionRequest request);
	
	RefundResponse update(RefundRequest request);

}