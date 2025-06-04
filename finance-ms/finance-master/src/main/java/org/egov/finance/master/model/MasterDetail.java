package org.egov.finance.master.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MasterDetail {
    private String name;
    private String filter;
}
