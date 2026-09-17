import React, { useState } from "react";
import { Loader } from "@nudmcdgnpm/digit-ui-react-components";
import { useTranslation } from "react-i18next";
import StatusCount from "./StatusCount";

/**
 * Status Component
 * 
 * Renders a list of application status checkboxes for filtering the inbox.
 * Fetches available workflow statuses using `useApplicationStatusGeneral` hook
 * for the given business services. Supports "Show More/Less" for long status lists.
 * Excludes terminate states from the display.
 * 
 * Props:
 * - `onAssignmentChange`: Callback when a status checkbox is toggled
 * - `searchParams`: Current filter parameters containing selected statuses
 * - `businessServices`: Array of business service codes to fetch statuses for
 * - `statusMap`: Map of status UUIDs to their count for display
 */
const Status = ({ onAssignmentChange, searchParams, businessServices, statusMap }) => {
  const { t } = useTranslation();
  const [moreStatus, showMoreStatus] = useState(false);

  const { data: statusData, isLoading } = Digit.Hooks.useApplicationStatusGeneral({ businessServices }, {});
  const { userRoleStates } = statusData || {};

  const translateState = (state) => {
    return t(`GC_STATUS_${state.applicationStatus}`);
  };

  if (isLoading) {
    return <Loader />;
  }

  return userRoleStates?.filter((e) => !e.isTerminateState).length > 0 ? (
    <div className="status-container">
      <div className="filter-label" style={{ fontWeight: "normal" }}>
        {t("PT_COMMON_TABLE_COL_STATUS_LABEL")}
      </div>
      {userRoleStates
        ?.filter((e) => !e.isTerminateState)
        ?.slice(0, 4)
        ?.map((option, index) => {
          return (
            <StatusCount
              businessServices={businessServices}
              key={index}
              onAssignmentChange={onAssignmentChange}
              status={{ name: translateState(option, t), code: option.applicationStatus, ...option }}
              searchParams={searchParams}
              statusMap={statusMap}
            />
          );
        })}
      {userRoleStates?.filter((e) => !e.isTerminateState)?.slice(4).length > 0 && (
        <React.Fragment>
          {moreStatus &&
            userRoleStates
              ?.filter((e) => !e.isTerminateState)
              ?.slice(4)
              ?.map((option, index) => {
                return (
                  <StatusCount
                    businessServices={businessServices}
                    key={option.uuid}
                    onAssignmentChange={onAssignmentChange}
                    status={{ name: translateState(option, t), code: option.applicationStatus, ...option }}
                    searchParams={searchParams}
                    statusMap={statusMap}
                  />
                );
              })}

          <div className="filter-button" onClick={() => showMoreStatus(!moreStatus)}>
            {" "}
            {moreStatus ? t("ES_COMMON_LESS") : t("ES_COMMON_MORE")}{" "}
          </div>
        </React.Fragment>
      )}
    </div>
  ) : null;
};

export default Status;