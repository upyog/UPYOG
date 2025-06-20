package org.egov.pt.models;

import java.util.List;

import lombok.Data;

@Data
public class Transactions {
    private String groupBy;
    private List<Bucket> buckets;

}
