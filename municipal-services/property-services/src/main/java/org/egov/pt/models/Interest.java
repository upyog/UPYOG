package org.egov.pt.models;

import java.util.List;

import lombok.Data;

@Data
public class Interest {
    private String groupBy;
    private List<Bucket> buckets;

}
