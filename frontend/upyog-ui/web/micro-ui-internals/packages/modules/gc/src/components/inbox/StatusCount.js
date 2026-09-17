import React from "react";
import { useTranslation } from "react-i18next";
import { CheckBox } from "@nudmcdgnpm/digit-ui-react-components";

/**
 * StatusCount Component
 * 
 * Renders a single status checkbox with its count for the inbox filter.
 * Shows the status name and the number of applications in that status.
 * 
 * Props:
 * - `status`: The status object containing name, code, uuid
 * - `searchParams`: Current filter params (used to determine if this status is checked)
 * - `onAssignmentChange`: Callback when checkbox is toggled
 * - `statusMap`: Map of status UUIDs to count values
 */
const StatusCount = ({ status, searchParams, onAssignmentChange, statusMap }) => {
  const { t } = useTranslation();

  return (
    <CheckBox
      className="status-count-checkbox"
      onChange={(e) => onAssignmentChange({ ...e, state: status.state }, status)}
      checked={searchParams?.applicationStatus?.some((e) => e.uuid === status.uuid)}
      label={`${status.name} (${statusMap?.find((e) => e.statusid === status.uuid)?.count || 0})`}
    />
  );
};

export default StatusCount;