import React, { useEffect, useState } from "react";
import { Dropdown, CloseSvg, SubmitBar } from "@upyog/digit-ui-react-components";
import { useTranslation } from "react-i18next";
import _ from "lodash";

/**
 * Filter Component
 * 
 * This component renders a set of filter options (Assignee, Sub complaint type, Locality) for filtering application data.
 * It's used in the PGR AI module and allows users to apply and clear filters.
 * The component handles syncing local filter state with parent state via callbacks.
 */


const Filter = ({ searchParams, onFilterChange, defaultSearchParams, statusMap, moduleCode, ...props }) => {
  const { t } = useTranslation();
  let { uuid } = Digit.UserService.getUser().info;
  const [_searchParams, setSearchParams] = useState(() => ({ ...searchParams, services: [] }));
  const [app_status, setAppStatus] = useState()
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const [selectAssigned, setSelectedAssigned] = useState();
  const [selectedLocality, setSelectedLocality] = useState();
  const { data: localities } = Digit.Hooks.useBoundaryLocalities(tenantId, "admin", {}, t);
  const serviceDefs = Digit.Hooks.pgr.useServiceDefs(tenantId, "PGR");
  const [selectedComplaintType, setSelectedComplaintType] = useState();



    const assignedToOptions =  [
      { code: "ASSIGNED_TO_ME", name: t("ASSIGNED_TO_ME"), i18nKey:"ASSIGNED_TO_ME" },
      { code: "ASSIGNED_TO_ALL", name: t("ASSIGNED_TO_ALL"), i18nKey:"ASSIGNED_TO_ALL" },
    ];

  const ApplicationTypeMenu = [
    {
      label: "PGR_AI_MODULE",
      value: "PGRAI",
    },
  ];

  let StatusFields = [
    {
      i18nKey: "PENDINGFORASSIGNMENT"
    },
    {
      i18nKey: "PENDINGFORREASSIGNMENT"
    },
    {
      i18nKey: "PENDINGATLME"
    },
    {
      i18nKey: "RESOLVED"
    },
    {
      i18nKey: "REJECTED"
    },
    {
      i18nKey: "CLOSEDAFTERREJECTION"
    },
    {
      i18nKey: "CLOSEDAFTERRESOLUTION"
    },
  ];


 /**
   * Updates local filter state and removes any keys listed in `filterParam.delete`
   */
  const localParamChange = (filterParam) => {
    let keys_to_delete = filterParam.delete;
    let _new = { ..._searchParams, ...filterParam };
    
    // Remove keys that should be deleted
    if (keys_to_delete) keys_to_delete.forEach((key) => delete _new[key]);
    delete filterParam.delete;
    setSearchParams({ ..._new });
  };

  /**
   * Triggers the parent filter handler with the updated filter state
   */
  const applyFilters = () => {
    if (_searchParams.services.length === 0) onFilterChange({ ..._searchParams, services: ApplicationTypeMenu.map((e) => e.value) });
    else 
    onFilterChange(_searchParams);
  };

  /**
   * Clears all selected filters and resets to default values
   */
  const clearAll = () => {
    setSearchParams({ ...defaultSearchParams, services: [] });
    onFilterChange({ ...defaultSearchParams });
    setAppStatus(null)
    setSelectedAssigned(null)
    setSelectedComplaintType(null)
    setSelectedLocality(null)
  };

  // Sync selected filters to local param state
  useEffect(() => {
    if(selectedLocality) localParamChange({ locality: selectedLocality?.code || "" });
    if(selectAssigned?.code==="ASSIGNED_TO_ME") localParamChange({ assignee: uuid });
    if(app_status) localParamChange({ status: app_status?.i18nKey || "" });
    if(selectedLocality) localParamChange({ locality: selectedLocality?.code || "" });
    if (selectedComplaintType) {
      localParamChange({ serviceCode: selectedComplaintType.serviceCode });
    }
  }, [selectedLocality, selectAssigned, app_status, selectedComplaintType])

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
                {t("PGR_AI_ASSIGNES")}:
              </div>
              <div>
                <Dropdown
                  selected={selectAssigned}
                  select={setSelectedAssigned}
                  option={assignedToOptions}
                  optionKey="i18nKey"
                  t={t}
                  placeholder={"Select"}
                />

              </div>

            </div>


            <div>
              <div className="filter-label" style={{ fontWeight: "normal" }}>
                {t("PGR_AI_STATUS")}:
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
              <div className="filter-label" style={{ fontWeight: "normal" }}>
                {t("CS_PGR_LOCALITY")}:
              </div>
              <div>
                <Dropdown
                  selected={selectedLocality}
                  select={setSelectedLocality}
                  option={localities}
                  optionKey="i18nkey"
                  t={t}
                  placeholder={"Select"}
                />
              </div>
            </div>
            <div>
              <div className="filter-label" style={{ fontWeight: "normal" }}>
                {t("CS_COMPLAINT_DETAILS_COMPLAINT_SUBTYPE")}:
              </div>
              <div>
                <Dropdown
                  selected={selectedComplaintType}
                  select={setSelectedComplaintType}
                  option={serviceDefs}
                  optionKey="i18nKey"
                  t={t}
                  placeholder="Select"
                />
              </div>
            </div>
            <div>
            </div>
            <div>
              <SubmitBar onSubmit={() => applyFilters()} label={t("ES_COMMON_APPLY")} />
            </div>
          </div>
        </div>
      </div>
    </React.Fragment>
  );
};

export default Filter;

