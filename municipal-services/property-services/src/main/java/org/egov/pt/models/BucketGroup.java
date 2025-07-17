package org.egov.pt.models;

import java.util.List;

public interface BucketGroup {
    void setGroupBy(String groupBy);
    void setBuckets(List<Bucket> buckets);
}
