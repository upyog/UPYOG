import React from "react";
import { useTranslation } from "react-i18next";
import { CheckBox } from "@nudmcdgnpm/digit-ui-react-components";

/**
 * StatusCount component:
 * - Renders a checkbox for a status item
 * - Handles selection and checked state logic
 */

const StatusCount = ({ status, searchParams, onAssignmentChange, businessServices, clearCheck, setclearCheck, setSearchParams, _searchParams }) => {
  const { t } = useTranslation();

  React.useEffect(() => {
    if (clearCheck) {
      setclearCheck(false);
    }
  }, [clearCheck]);

  return (
    <CheckBox
      onChange={(e) => onAssignmentChange(e, status)}
      checked={
        !clearCheck
          ? _searchParams?.status?.some((e) => e === status.code)
          : false
      }
      label={`${t(status.name)}`}
    />
  );
};

export default StatusCount;