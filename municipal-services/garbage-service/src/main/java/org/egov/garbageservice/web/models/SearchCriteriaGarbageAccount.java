package org.egov.garbageservice.web.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.tracer.annotations.CustomSafeHtml;

import java.util.List;

@AllArgsConstructor
@Data
@Builder(toBuilder = true)
/**
 * Search filters for finding garbage accounts (tenant, status, mobile, property, dates, etc.).
 * Embedded in SearchCriteriaGarbageAccountRequest for authenticated and open search APIs.
 */
@NoArgsConstructor
public class SearchCriteriaGarbageAccount {

    private List<Long> id;

    private List<Long> garbageId;

    private List<String> propertyId;

    private List<String> uuid;

    private List<String> user_uuid;

    private List<String> type;

    private List<String> name;

    private List<String> mobileNumber;

    private List<String> createdBy;

    private List<String> applicationNumber;

    @CustomSafeHtml
    private String tenantId;

    private List<String> status;

    private List<String> statusList;

    private List<String> channels;

    private List<String> wardNames;

    private List<String> oldGarbageIds;

    private List<String> unitTypes;

    private List<String> unitCategories;

    private Boolean isOwner;

    @CustomSafeHtml
    private String parentAccount;

    @CustomSafeHtml
    private String orderBy = "DESC";

    private Long startId;

    private Long endId;

    private Boolean isActiveSubAccount;

    private Boolean isActiveAccount;

    private Boolean isUserUuidNull;

    private Boolean isMonthlyBilling;

    @Builder.Default
    private Boolean isPayNow = false;

    @Builder.Default
    private Boolean isSchedulerCall = false;

    private Long offset;

    private Long limit;

    @CustomSafeHtml
    private String userType;

    private Long fromDate;

    private Long toDate;

    /**
     * Creates a deep copy of the current search criteria instance.
     *
     * @return a new {@link SearchCriteriaGarbageAccount} instance with duplicated criteria fields
     */

    public SearchCriteriaGarbageAccount copy() {
        return SearchCriteriaGarbageAccount.builder().id(copyList(this.id)).garbageId(copyList(this.garbageId))
                .propertyId(copyList(this.propertyId)).uuid(copyList(this.uuid)).user_uuid(copyList(this.user_uuid))
                .type(copyList(this.type)).name(copyList(this.name)).mobileNumber(copyList(this.mobileNumber))
                .createdBy(copyList(this.createdBy)).applicationNumber(copyList(this.applicationNumber))
                .tenantId(this.tenantId).status(copyList(this.status)).statusList(copyList(this.statusList)).isOwner(this.isOwner)
                .parentAccount(this.parentAccount).orderBy(this.orderBy).startId(this.startId).endId(this.endId)
                .isActiveSubAccount(this.isActiveSubAccount).isActiveAccount(this.isActiveAccount)
                .isSchedulerCall(this.isSchedulerCall).offset(this.offset).limit(this.limit)
                .channels(copyList(this.channels)).wardNames(copyList(this.wardNames))
                .oldGarbageIds(copyList(this.oldGarbageIds)).unitTypes(copyList(this.unitTypes))
                .unitCategories(copyList(this.unitCategories)).isPayNow(this.isPayNow)
                .isMonthlyBilling(this.isMonthlyBilling).userType(this.userType)
                .isUserUuidNull(this.isUserUuidNull).fromDate(this.fromDate).toDate(this.toDate).build();
    }

    /**
     * Helper method to safely duplicate a list without mutating the source list.
     *
     * @param <T>          the element type of the list
     * @param originalList the source list to copy
     * @return a new list containing the same elements, or null if the source list was null
     */

    private <T> List<T> copyList(List<T> originalList) {
        return originalList != null ? new java.util.ArrayList<>(originalList) : null;
    }

}
