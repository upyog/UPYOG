import React, { useState } from "react";
import { RemoveableTag, CloseSvg, CheckBox, Localities, SubmitBar } from "@upyog/digit-ui-react-components";
import { useQueryClient } from "react-query";
import { useTranslation } from "react-i18next";
import Status from "./Status";
import _ from "lodash";

/**
 * Filter component for the E-Waste inbox that provides advanced filtering capabilities.
 * Allows users to filter applications based on locality, service type, and status.
 * Supports both mobile and desktop views with different layout optimizations.
 *
 * @param {Object} props - Component properties
 * @param {Object} props.searchParams - Current search parameters being applied
 * @param {Function} props.onFilterChange - Callback function triggered when filters change
 * @param {Object} props.defaultSearchParams - Default search parameters for reset
 * @param {Object} props.statusMap - Map of available application statuses
 * @param {string} props.moduleCode - Current module identifier
 * @returns {JSX.Element} Filter component with various filtering options
 */
const Filter = ({ searchParams, onFilterChange, defaultSearchParams, statusMap, moduleCode, ...props }) => {
  const { t } = useTranslation();
  const client = useQueryClient();
  const [_searchParams, setSearchParams] = useState(() => ({ ...searchParams, services: [] }));

  /**
   * Available application types that can be filtered
   * @type {Array<{label: string, value: string}>}
   */
  const ApplicationTypeMenu = [
    {
      label: "EW_NEW_REQUEST",
      value: "ewst",
    },
  ];

  /**
   * Updates local search parameters while handling parameter deletion
   * Merges new parameters with existing ones and removes specified keys
   *
   * @param {Object} filterParam - New filter parameters to apply
   * @param {string[]} [filterParam.delete] - Keys to remove from parameters
   */
  const localParamChange = (filterParam) => {
    let keys_to_delete = filterParam.delete;
    let _new = { ..._searchParams, ...filterParam };
    if (keys_to_delete) keys_to_delete.forEach((key) => delete _new[key]);
    delete filterParam.delete;
    setSearchParams({ ..._new });
  };

  /**
   * Applies the current local filters to the main search
   * If no services are selected, applies all available services by default
   */
  const applyLocalFilters = () => {
    if (_searchParams.services.length === 0) {
      onFilterChange({ ..._searchParams, services: ApplicationTypeMenu.map((e) => e.value) });
    } else {
      onFilterChange(_searchParams);
    }
  };

  /**
   * Resets all filters to their default values
   * Clears both local state and parent component filters
   */
  const clearAll = () => {
    setSearchParams({ ...defaultSearchParams, services: [] });
    onFilterChange({ ...defaultSearchParams });
  };

  const tenantId = Digit.ULBService.getCurrentTenantId();

  /**
   * Handles service selection/deselection
   * Updates services list and related application statuses
   *
   * @param {Event} e - Change event from checkbox
   * @param {string} label - Service label being selected/deselected
   */
  const onServiceSelect = (e, label) => {
    if (e.target.checked) {
      localParamChange({ services: Array.isArray(_searchParams.services) ? [..._searchParams.services, label] : [label] });
    } else {
      localParamChange({
        services: _searchParams.services.filter((o) => o !== label),
        applicationStatus: _searchParams.applicationStatus?.filter((e) => e.stateBusinessService !== label),
      });
    }
  };

  /**
   * Handles locality selection
   * Adds new locality to the existing list of selected localities
   *
   * @param {Object} d - Selected locality data
   */
  const selectLocality = (d) => {
    localParamChange({ locality: [...(_searchParams?.locality || []), d] });
  };

  return (
    <React.Fragment>
      <div className="filter">
        <div className="filter-card">
          <div className="heading" style={{ alignItems: "center" }}>
            <div className="filter-label" style={{ display: "flex", alignItems: "center" }}>
              <span>
                {/* Icon for the filter heading */}
                <svg width="17" height="17" viewBox="0 0 22 22" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path
                    d="M0.66666 2.48016C3.35999 5.9335 8.33333 12.3335 8.33333 12.3335V20.3335C8.33333 21.0668 8.93333 21.6668 9.66666 21.6668H12.3333C13.0667 21.6668 13.6667 21.0668 13.6667 20.3335V12.3335C13.6667 12.3335 18.6267 5.9335 21.32 2.48016C22 1.60016 21.3733 0.333496 20.2667 0.333496H1.71999C0.613327 0.333496 -0.01334 1.60016 0.66666 2.48016Z"
                    fill="#505A5F"
                  />
                </svg>
              </span>
              <span style={{ marginLeft: "8px", fontWeight: "normal" }}>{t("ES_COMMON_FILTER_BY")}:</span> {/* Filter heading */}
            </div>
            <div className="clearAll" onClick={clearAll}>
              {t("ES_COMMON_CLEAR_ALL")} {/* Clear all filters */}
            </div>
            {props.type === "desktop" && (
              <span className="clear-search" onClick={clearAll} style={{ border: "1px solid #e0e0e0", padding: "6px" }}>
                {/* Icon for clearing filters */}
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
                <CloseSvg /> {/* Close button for mobile view */}
              </span>
            )}
          </div>
          <div>
            {/* Locality filter */}
            <div>
              <div className="filter-label" style={{ fontWeight: "normal" }}>
                {t("ES_INBOX_LOCALITY")}:
              </div>
              <Localities selectLocality={selectLocality} tenantId={tenantId} boundaryType="revenue" /> {/* Locality dropdown */}
              <div className="tag-container">
                {_searchParams?.locality?.map((locality, index) => {
                  return (
                    <RemoveableTag
                      key={index}
                      text={t(locality.i18nkey)} // Display the locality name
                      onClick={() => {
                        localParamChange({ locality: _searchParams?.locality.filter((loc) => loc.code !== locality.code) }); // Remove the selected locality
                      }}
                    />
                  );
                })}
              </div>
            </div>
            {/* Application type filter */}
            <div>
              {ApplicationTypeMenu.map((e, index) => {
                const checked = _searchParams?.services?.includes(e.value); // Check if the service is selected
                return (
                  <CheckBox
                    key={index + "service"}
                    label={t(e.label)} // Display the service label
                    value={e.label}
                    checked={checked} // Set the checkbox state
                    onChange={(event) => onServiceSelect(event, e.value)} // Handle service selection
                  />
                );
              })}
            </div>
            {/* Status filter */}
            <div>
              <Status
                searchParams={_searchParams} // Current search parameters
                businessServices={_searchParams.services} // Selected services
                statusMap={statusMap || client.getQueryData(`INBOX_STATUS_MAP_${moduleCode}`)} // Status map
                moduleCode={moduleCode} // Module code
                onAssignmentChange={(e, status) => {
                  if (e.target.checked) {
                    localParamChange({ applicationStatus: [..._searchParams?.applicationStatus, status] }); // Add the selected status
                  } else {
                    let applicationStatus = _searchParams?.applicationStatus.filter((e) => e.state !== status.state); // Remove the unselected status
                    localParamChange({ applicationStatus });
                  }
                }}
              />
            </div>
            {/* Apply filters button */}
            <div>
              <SubmitBar onSubmit={() => applyLocalFilters()} label={t("ES_COMMON_APPLY")} /> {/* Apply filters */}
            </div>
          </div>
        </div>
      </div>
    </React.Fragment>
  );
};

export default Filter; // Exporting the component for use in other parts of the application