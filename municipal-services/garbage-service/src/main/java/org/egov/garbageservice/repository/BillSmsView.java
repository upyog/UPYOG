package org.egov.garbageservice.repository;

import java.math.BigDecimal;

/**
 * Projection interface representing a flattened view of bill and owner details used for SMS notification.
 *
 * <p>Provides getter methods for SMS notification construction and tracking.
 */
public interface BillSmsView {

    /**
     * Gets the unique identifier for the bill tracker.
     *
     * @return the bill tracker UUID
     */
    String getUuid();

    /**
     * Gets the garbage application ID.
     *
     * @return the garbage application ID
     */
    String getGrbgApplicationId();

    /**
     * Gets the tenant ID context.
     *
     * @return the tenant ID
     */
    String getTenantId();

    /**
     * Gets the billing month.
     *
     * @return the billing month
     */
    String getMonth();

    /**
     * Gets the billing year.
     *
     * @return the billing year
     */
    String getYear();

    /**
     * Gets the start date of the billing cycle.
     *
     * @return the cycle start date string
     */
    String getFromDate();

    /**
     * Gets the end date of the billing cycle.
     *
     * @return the cycle end date string
     */
    String getToDate();

    /**
     * Gets the linked bill ID.
     *
     * @return the bill ID
     */
    String getBillId();

    /**
     * Gets the total billed amount for garbage collection.
     *
     * @return the bill amount
     */
    BigDecimal getGrbgBillAmount();

    /**
     * Gets the recipient mobile number.
     *
     * @return the mobile number string
     */
    String getMobileNumber();

    /**
     * Gets the administrative ward name.
     *
     * @return the ward name
     */
    String getWard();

    /**
     * Gets the user ID of the last modifier.
     *
     * @return the last modifier user ID
     */
    String getLastModifiedBy();

    /**
     * Gets the owner or payer name.
     *
     * @return the owner name
     */
    String getOwnerName();
}
