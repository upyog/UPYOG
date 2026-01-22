package org.egov.pt.models;

import java.util.List;

import lombok.Data;

@Data
public class Transactions implements BucketGroup{
    private String groupBy;
    private List<Bucket> buckets;

}
