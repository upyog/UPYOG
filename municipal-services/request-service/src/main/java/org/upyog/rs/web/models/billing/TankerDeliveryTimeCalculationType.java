package org.upyog.rs.web.models.billing;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * TankerDeliveryTimeCalculationType is a model class that represents the
 * configuration for calculating the delivery time and associated charges
 * for water tanker services.
 * 
 * Key Fields:
 * - deliveryTimeName: The name or label for the delivery time configuration.
 * - deliveryHours: The number of hours required for delivery.
 * - amount: The cost associated with the delivery time.
 * - active: A flag indicating whether this configuration is active or not.
 * 
 * Annotations:
 * - @Builder: Enables the builder pattern for creating instances of this class.
 * - @AllArgsConstructor: Automatically generates a constructor with all fields.
 * - @NoArgsConstructor: Automatically generates a no-argument constructor.
 * - @Setter: Automatically generates setter methods for all fields.
 * - @Getter: Automatically generates getter methods for all fields.
 * - @ToString: Automatically generates a toString method for this class.
 */

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class TankerDeliveryTimeCalculationType {

    private String deliveryTimeName;
    private int deliveryHours;
    private BigDecimal amount;
    private boolean active;
}
