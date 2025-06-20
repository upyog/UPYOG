package org.egov.pt.models;

import java.util.List;

import lombok.Data;

@Data
public class PropertyTax {
    private String groupBy;
    private List<Bucket> buckets;

}
