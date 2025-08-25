import React, { useEffect, useState } from "react";
import { Loader } from "@upyog/digit-ui-react-components";
import { useTranslation } from "react-i18next";
import StatusCount from "./StatusCount";

/**
 * Status component for displaying and managing application status filters.
 * Provides a list of status options with their counts and allows expanding/collapsing additional statuses.
 *
 * @param {Object} props - Component properties
 * @param {Function} props.onAssignmentChange - Callback for status selection changes
 * @param {Object} props.searchParams - Current search parameters
 * @param {Array} props.businessServices - List of business services to fetch statuses for
 * @param {Object} props.statusMap - Mapping of status codes to their configurations
 * @param {string} props.moduleCode - Current module identifier
 * @returns {JSX.Element|null} Status filter component or null if no states available
 */
const Status = ({ onAssignmentChange, searchParams, businessServices, statusMap, moduleCode }) => {
  const { t } = useTranslation();
  
  /**
   * State to control visibility of additional status options
   * @type {[boolean, Function]}
   */
  const [moreStatus, showMoreStatus] = useState(false);

  /**
   * Custom hook to fetch application status data
   * Returns status data and loading state
   */
  const { data: statusData, isLoading } = Digit.Hooks.useApplicationStatusGeneral({ businessServices }, {});

  const { userRoleStates } = statusData || {};

  /**
   * Translates a state code to its display text
   *
   * @param {Object} state - State object containing state code
   * @param {Function} t - Translation function
   * @returns {string} Translated state text
   */
  const translateState = (state, t) => {
    return t(`ES_EW_STATUS_${state.state || "CREATED"}`);
  };

  // Show loader while fetching status data
  if (isLoading) {
    return <Loader />;
  }

  // Only render if there are non-terminated states
  return userRoleStates?.filter((e) => !e.isTerminateState).length ? (
    <div className="status-container">
      <div className="filter-label" style={{ fontWeight: "normal" }}>
        {t("ES_INBOX_STATUS")}
      </div>
      {/* Display first 4 status options */}
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
      {/* Show more/less options if there are more than 4 statuses */}
      {userRoleStates?.filter((e) => !e.isTerminateState)?.slice(4).length > 0 ? (
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
      ) : null}
    </div>
  ) : null;
};

export default Status;
