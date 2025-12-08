/**
 * 
 * 
 * @author Surya
 */
package org.egov.finance.inbox.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MasterDetail {
    private String name;
    private String filter;
}
