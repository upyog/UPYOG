import React from "react";
import { CheckBox, Loader } from "@nudmcdgnpm/digit-ui-react-components";
import { useTranslation } from "react-i18next";

/**
 * Status Component
 * 
 * This component displays a list of complaint status filters with their counts.
 * It allows users to filter complaints by their current status in the system.
 * 
 * Features:
 * - Shows each status option as a checkbox with count of complaints in that status
 * - Displays a loader while status counts are being fetched
 * - Maintains selected status filters through the pgrfilters prop
 * - Updates parent component when status filters are changed
 * 
 * Used in the complaints dashboard to help users filter complaints by their
 * current processing status (e.g., pending, resolved, rejected).
 */

const Status = ({ complaints, onAssignmentChange, pgrfilters }) => {
  const { t } = useTranslation();
  const complaintsWithCount = Digit.Hooks.pgr.useComplaintStatusCount(complaints);
  let hasFilters = pgrfilters?.applicationStatus?.length;
  return (
    <div className="status-container">
      <div className="filter-label">{t("ES_PGR_FILTER_STATUS")}</div>
      {complaintsWithCount.length === 0 && <Loader />}
      {complaintsWithCount.map((option, index) => {
        return (
          <CheckBox
            key={index}
            onChange={(e) => onAssignmentChange(e, option)}
            checked={hasFilters ? (pgrfilters.applicationStatus.filter((e) => e.code === option.code).length !== 0 ? true : false) : false}
            label={`${option.name} (${option.count || 0})`}
          />
        );
      })}
    </div>
  );
};

export default Status;
