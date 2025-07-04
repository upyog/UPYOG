package org.egov.pt.models;

import java.util.List;

import lombok.Data;

@Data
public class TodaysMovedApplications implements BucketGroup{
    private String groupBy;
    private List<Bucket> buckets;

}