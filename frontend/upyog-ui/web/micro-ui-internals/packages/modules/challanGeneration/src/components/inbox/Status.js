import React, { useState } from "react";
import { Loader } from "@nudmcdgnpm/digit-ui-react-components";
import { useTranslation } from "react-i18next";
import StatusCount from "./StatusCount";

/**
 * Status component:
 * - Renders list of status checkboxes
 * - Uses StatusCount for each status item
 * - Supports filter selection and reset
 */

const Status = ({ onAssignmentChange, searchParams, businessServices, clearCheck, setclearCheck, statutes, _searchParams }) => {
  const { t } = useTranslation();
  const translateState = (state) => {
    return `${state.applicationstatus || "ACTIVE"}`;
  };

  return (
    <div className="status-container">
      <div className="filter-label cg-font-normal">
        {t("UC_COMMON_TABLE_COL_STATUS")}
      </div>
      {statutes?.map((option, index) => {
        return (
          <StatusCount
            key={index}
            clearCheck={clearCheck}
            setclearCheck={setclearCheck}
            _searchParams={_searchParams}
            onAssignmentChange={onAssignmentChange}
            status={{ name: translateState(option), code: option.applicationstatus }}
            searchParams={searchParams}
          />
        );
      })}
    </div>
  );
};

export default Status;
