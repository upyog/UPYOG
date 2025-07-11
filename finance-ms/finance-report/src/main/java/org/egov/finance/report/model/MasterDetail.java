/**
 * 
 * 
 * @author Surya
 */
package org.egov.finance.report.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MasterDetail {
    private String name;
    private String filter;
}
