import React from "react";
import { useTranslation } from "react-i18next";
import { CheckBox } from "@upyog/digit-ui-react-components";

/**
 * Renders a checkbox component for filtering applications by their status.
 * Displays the status name along with the count of applications in that status.
 *
 * @param {Object} props - Component properties
 * @param {Object} props.status - Status object containing state and uuid information
 * @param {string} props.status.name - Display name of the status
 * @param {string} props.status.state - State code of the status
 * @param {string} props.status.uuid - Unique identifier for the status
 * @param {Object} props.searchParams - Current search parameters containing selected statuses
 * @param {Array} props.searchParams.applicationStatus - Array of currently selected status filters
 * @param {Function} props.onAssignmentChange - Callback triggered when status selection changes
 * @param {Array} props.statusMap - Array of status objects with counts
 * @param {Array} props.businessServices - Array of business services associated with the statuses
 * @returns {JSX.Element} Checkbox component for status filtering
 */
const StatusCount = ({ status, searchParams, onAssignmentChange, statusMap, businessServices }) => {
  const { t } = useTranslation();


  return (
    <CheckBox
      styles={{ height: "unset" }}
      onChange={(e) => onAssignmentChange({ ...e, state: status.state }, status)}
      checked={(() => {
        return searchParams?.applicationStatus.some((e) => e.uuid === status.uuid);
      })()}
      label={`${status.name} (${statusMap?.find((e) => e.statusid === status.uuid)?.count || "-"})`}
    />
  );
};

export default StatusCount;
