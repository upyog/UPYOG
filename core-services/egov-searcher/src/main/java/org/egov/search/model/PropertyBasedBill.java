package org.egov.search.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import org.egov.custom.mapper.billing.impl.Bill;

/**
 * Represents a group of bills associated with a single property.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropertyBasedBill {
    private String propertyId;
    private String tenantId;
    private List<Bill> bills;
}
