package org.egov.pt.models;

import java.util.List;

import lombok.Data;

@Data
public class Cess {
    private String groupBy;
    private List<Bucket> buckets;

}
