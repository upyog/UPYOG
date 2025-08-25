import React from "react";
import { useTranslation } from "react-i18next";
import { CheckBox } from "@upyog/digit-ui-react-components";
/**
 * Component Developed in such a way that it will render Checkbox to show the Number of Status so that employee can filter the data as per
 * the Application Status
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
