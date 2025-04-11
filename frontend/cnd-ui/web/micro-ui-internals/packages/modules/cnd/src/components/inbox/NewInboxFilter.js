import React, { useState } from "react";
import { Dropdown, CloseSvg, SubmitBar } from "@nudmcdgnpm/digit-ui-react-components";
import { useQueryClient } from "react-query";
import { useTranslation } from "react-i18next";
import Status from "./Status";
import _ from "lodash";

/**
 * The Filter component creates a UI for applying filters to a list of applications, 
 * with options for selecting application status and other filter criteria. 
 * It handles state management for filter parameters and provides controls to apply or clear filters.
 * 
 * Key features:
 * Manages filter state with React useState hooks
 * Includes dropdown for application status selection
 * Uses a Status component for handling specific status filters
 * Provides apply and clear all functionality
 * Adapts UI between mobile and desktop views
 */

const Filter = ({ searchParams, onFilterChange, defaultSearchParams, statusMap, moduleCode, ...props }) => {
  const { t } = useTranslation();
  const client = useQueryClient();

  const [_searchParams, setSearchParams] = useState(() => ({ ...searchParams, services: [] }));
  const [app_status, setAppStatus] = useState()

  const ApplicationTypeMenu = [
    {
      label: "CND_NEW_REQUEST",
      value: "cnd",
    },
  ];

  //TODO: Will Add the Real Status Later
  let StatusFields = [
    {
      i18nKey: "TEST1"
    },
    {
      i18nKey: "TEST2"
    }
  ];

  
// Helper function to update local filter state
// Allows for selective deletion of parameters and merging of new filter values
  const localParamChange = (filterParam) => {
    let keys_to_delete = filterParam.delete;
    let _new = { ..._searchParams, ...filterParam };
    if (keys_to_delete) keys_to_delete.forEach((key) => delete _new[key]);
    delete filterParam.delete;
    setSearchParams({ ..._new });
  };

  // Apply filters logic with special handling for empty services
// If no services are selected, default to all application types
  const applyLocalFilters = () => {
    if (_searchParams.services.length === 0) onFilterChange({ ..._searchParams, services: ApplicationTypeMenu.map((e) => e.value) });
    else 
    onFilterChange(_searchParams);
  };

  const clearAll = () => {
    setSearchParams({ ...defaultSearchParams, services: [] });
    onFilterChange({ ...defaultSearchParams });
    setAppStatus(null)
  };


  if (window.location.href.includes("employee")){
  return (
    <React.Fragment>
      <div className="filter">
        <div className="filter-card">
          <div className="heading" style={{ alignItems: "center" }}>
            <div className="filter-label" style={{ display: "flex", alignItems: "center" }}>
              <span>
                <svg width="17" height="17" viewBox="0 0 22 22" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path
                    d="M0.66666 2.48016C3.35999 5.9335 8.33333 12.3335 8.33333 12.3335V20.3335C8.33333 21.0668 8.93333 21.6668 9.66666 21.6668H12.3333C13.0667 21.6668 13.6667 21.0668 13.6667 20.3335V12.3335C13.6667 12.3335 18.6267 5.9335 21.32 2.48016C22 1.60016 21.3733 0.333496 20.2667 0.333496H1.71999C0.613327 0.333496 -0.01334 1.60016 0.66666 2.48016Z"
                    fill="#505A5F"
                  />
                </svg>
              </span>
              <span style={{ marginLeft: "8px", fontWeight: "normal" }}>{t("ES_COMMON_FILTER_BY")}:</span>
            </div>
            <div className="clearAll" onClick={clearAll}>
              {t("ES_COMMON_CLEAR_ALL")}
            </div>
            {props.type === "desktop" && (
              <span className="clear-search" onClick={clearAll} style={{ border: "1px solid #e0e0e0", padding: "6px" }}>
                <svg width="17" height="17" viewBox="0 0 16 22" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path
                    d="M8 5V8L12 4L8 0V3C3.58 3 0 6.58 0 11C0 12.57 0.46 14.03 1.24 15.26L2.7 13.8C2.25 12.97 2 12.01 2 11C2 7.69 4.69 5 8 5ZM14.76 6.74L13.3 8.2C13.74 9.04 14 9.99 14 11C14 14.31 11.31 17 8 17V14L4 18L8 22V19C12.42 19 16 15.42 16 11C16 9.43 15.54 7.97 14.76 6.74Z"
                    fill="#505A5F"
                  />
                </svg>
              </span>
            )}
            {props.type === "mobile" && (
              <span onClick={props.onClose}>
                <CloseSvg />
              </span>
            )}
          </div>
          <div>


            <div>
              <div className="filter-label" style={{ fontWeight: "normal" }}>
                {t("CND_APPLICATION_STATUS")}:
              </div>
              <div>
                <Dropdown
                  selected={app_status}
                  select={setAppStatus}
                  option={StatusFields}
                  optionKey="i18nKey"
                  t={t}
                  placeholder={"Select"}
                />

              </div>
            </div>

            <div>
              <Status
                searchParams={_searchParams}
                businessServices={_searchParams.services}
                statusMap={statusMap || client.getQueryData(`INBOX_STATUS_MAP_${moduleCode}`)}
                moduleCode={moduleCode}
                onAssignmentChange={(e, status) => {
                  if (e.target.checked) localParamChange({ applicationStatus: [..._searchParams?.applicationStatus, status] });
                  else {
                    let applicationStatus = _searchParams?.applicationStatus.filter((e) => e.state !== status.state);
                    localParamChange({ applicationStatus });
                  }
                }}
              />
            </div>
            <div>
              <SubmitBar onSubmit={() => applyLocalFilters()} label={t("ES_COMMON_APPLY")} />
            </div>
          </div>
        </div>
      </div>
    </React.Fragment>
  )}
  else return null;
};

export default Filter;
