package org.egov.refund.service;

import java.util.List;

import org.egov.refund.model.Refund;
import org.egov.refund.web.contracat.RefundActionRequest;
import org.egov.refund.web.contracat.RefundGetRequest;
import org.egov.refund.web.contracat.RefundRequest;
import org.egov.refund.web.contracat.RefundSearchRequest;

public interface RefundService {

	Refund create(RefundRequest request);

	Refund get(RefundGetRequest request);

	List<Refund> search(RefundSearchRequest request);

	Refund process(RefundActionRequest request);
	
	Refund update(RefundRequest request);

}