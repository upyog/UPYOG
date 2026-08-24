import React, { useState } from "react";
import { FilterFormField, RadioButtons, CheckBox, Loader, SubmitBar, CloseSvg } from "@nudmcdgnpm/digit-ui-react-components";
import { useTranslation } from "react-i18next";

/**
 * Component for Filters -> inside inbox there will be a card on the left side with filters 
 * such as application Status, assigned to whom ( me or all)
 * 
 * Using this filters, employee can filters application as per user, business service and applicationStatus
 */

const Filter = ({ searchParams, onFilterChange, defaultSearchParams, statusMap, moduleCode, type, onClose }) => {
  const { t } = useTranslation();

  const [localParams, setLocalParams] = useState(() => ({
    assignee: searchParams?.assignee || "ASSIGNED_TO_ALL",
    businessService: searchParams?.businessService || null,
    applicationStatus: searchParams?.applicationStatus || [],
  }));

  const assigneeOptions = [
    { code: "ASSIGNED_TO_ME", name: t("ES_INBOX_ASSIGNED_TO_ME") },
    { code: "ASSIGNED_TO_ALL", name: t("ES_INBOX_ASSIGNED_TO_ALL") },
  ];

  const businessServices = [{
    code: "FIRENOC",
    active: true,
    roles: ["NOC_CEMP"],
    i18nKey: "WF_FIRENOC_BUSINESS_SERVICE"}];

  // Get statuses for selected business service from statusMap
  const selectedServiceCode = localParams.businessService?.code;
  const statusesForService = selectedServiceCode && statusMap
    ? Object.values(statusMap).filter((s) => s.businessservice === selectedServiceCode)
    : [];

  const handleApply = () => {
    onFilterChange({ ...localParams });
    if (type === "mobile") onClose?.();
  };

  const handleClear = () => {
    const reset = { assignee: "ASSIGNED_TO_ALL", businessService: null, applicationStatus: [] };
    setLocalParams(reset);
    onFilterChange({ ...defaultSearchParams, ...reset });
  };

  return (
    <div className="filter">
      <div className="filter-card">
        <div className="heading" style={{ alignItems: "center" }}>
          <div className="filter-label">{t("ES_COMMON_FILTER_BY")}:</div>
          <div className="clearAll" onClick={handleClear}>{t("ES_COMMON_CLEAR_ALL")}</div>
          {type === "mobile" && <span onClick={onClose}><CloseSvg /></span>}
        </div>

        {/* Assigned To */}
        <FilterFormField>
          <RadioButtons
            onSelect={(e) => setLocalParams((p) => ({ ...p, assignee: e.code }))}
            selectedOption={assigneeOptions.find((o) => o.code === localParams.assignee)}
            optionsKey="name"
            options={assigneeOptions}
          />
        </FilterFormField>

        {/* Business Service */}
        <FilterFormField>
          <div className="filter-label sub-filter-label" style={{ fontSize: "18px", fontWeight: "600" }}>
            {t("BUSINESS_SERVICE")}
          </div>
          <RadioButtons
            onSelect={(e) => setLocalParams((p) => ({ ...p, businessService: e, applicationStatus: [] }))}
            selectedOption={localParams.businessService}
            optionsKey="i18nKey"
            options={businessServices}
          />
        </FilterFormField>

        {/* Application Status — only shown when a business service is selected */}
        {localParams.businessService && (
          <FilterFormField>
            <div className="filter-label sub-filter-label" style={{ fontSize: "18px", fontWeight: "600" }}>
              {t("ACTION_TEST_APPLICATION_STATUS")}
            </div>
            {!statusMap ? (
              <Loader />
            ) : statusesForService.length === 0 ? null : (
              statusesForService.map((status) => (
                <CheckBox
                  key={status.statusid}
                  label={`${t(`WF_${status.businessservice}_${status.applicationstatus?.split("_").pop()}`)} (${status.count})`}
                  checked={localParams.applicationStatus.includes(status.statusid)}
                  onChange={(e) =>
                    setLocalParams((p) => ({
                      ...p,
                      applicationStatus: e.target.checked
                        ? [...p.applicationStatus, status.statusid]
                        : p.applicationStatus.filter((id) => id !== status.statusid),
                    }))
                  }
                />
              ))
            )}
          </FilterFormField>
        )}

        <SubmitBar onSubmit={handleApply} label={t("ES_COMMON_APPLY")} />
      </div>
    </div>
  );
};

export default Filter;
