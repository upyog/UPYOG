package org.egov.edcr.contract;

import java.util.List;

import org.egov.infra.microservice.contract.ResponseInfo;
/**
 * Represents the response object for eDCR BPA APIs.
 *
 * <p>
 * This DTO acts as a wrapper for eDCR application details returned by
 * service endpoints. It contains response metadata, the list of eDCR
 * application records, and the total count of records returned.
 * </p>
 *
 * <p>
 * The response encapsulates:
 * <ul>
 * <li>Response information such as request status and metadata.</li>
 * <li>A collection of eDCR BPA application details.</li>
 * <li>Total number of eDCR records returned in the response.</li>
 * </ul>
 * </p>
 *
 * <p>
 * This object is primarily used for communication between eDCR services
 * and client applications during application search, retrieval, and
 * reporting operations.
 * </p>
 */
public class EdcrResponseBpa {

    private ResponseInfo responseInfo;

    private List<EdcrDetailBpa> edcrDetail;

    private int count;

    public ResponseInfo getResponseInfo() {
        return responseInfo;
    }

    public void setResponseInfo(ResponseInfo responseInfo) {
        this.responseInfo = responseInfo;
    }

    public List<EdcrDetailBpa> getEdcrDetail() {
        return edcrDetail;
    }

    public void setEdcrDetail(List<EdcrDetailBpa> edcrDetail) {
        this.edcrDetail = edcrDetail;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "EdcrResponse [responseInfo=" + responseInfo + ", edcrDetail=" + edcrDetail + "]";
    }

}
