import React, { useState, useEffect } from "react";
import { Header, Loader, TextInput, Dropdown, SubmitBar, CardLabel, Card } from "@nudmcdgnpm/digit-ui-react-components";
import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";
import GCApplication from "./gc-application";
import { getGCStatusOptions } from "../../../utils";

/**
 * GCMyApplications Component
 * 
 * Renders the "My Applications" page for citizens, displaying a list of GC applications
 * associated with the user's mobile number. Provides search/filter functionality by
 * application number and status. Supports load-more pagination.
 * 
 * Uses `useGCSearch` hook to fetch applications and applies local client-side filtering
 * for accurate search results.
 */
export const GCMyApplications = () => {
  const { t } = useTranslation();
  const tenantId = Digit.ULBService.getCitizenCurrentTenant(true) || Digit.ULBService.getCurrentTenantId();
  const user = Digit.UserService.getUser().info;

  const [searchTerm, setSearchTerm] = useState("");
  const [status, setStatus] = useState(null);
  const [filters, setFilters] = useState(null);

  let filter = window.location.href.split("/").pop();
  let t1;
  let off;
  if (!isNaN(parseInt(filter))) {
    off = filter;
    t1 = parseInt(filter) + 50;
  } else {
    t1 = 4;
  }

  let initialFilters = !isNaN(parseInt(filter))
    ? { mobileNumber: user?.mobileNumber, limit: "50", offset: off }
    : { mobileNumber: user?.mobileNumber, limit: "4", offset: "0" };

  useEffect(() => {
    setFilters(initialFilters);
  }, [filter]);

  const { isLoading, data } = Digit.Hooks.gc.useGCSearch({
    tenantId,
    filters: filters || initialFilters,
  });

  const handleSearch = () => {
    const trimmedSearchTerm = searchTerm.trim();
    setFilters({
      mobileNumber: user?.mobileNumber,
      ...(trimmedSearchTerm && { applicationNumber: trimmedSearchTerm }),
      ...(status?.code && { status: status.code }),
    });
  };

  const clearAll = () => {
    setSearchTerm("");
    setStatus(null);
    setFilters(initialFilters);
  };

  if (isLoading) {
    return <Loader />;
  }

  const statusOptions = getGCStatusOptions(t);

  let filteredApplications = data?.garbageAccounts || data?.GarbageApplications || data?.data || [];
  const totalCount = data?.applicationCount || data?.count || 0;

  return (
    <React.Fragment>
      <Header>{`${t("GC_MY_APPLICATIONS_HEADER")} (${filteredApplications.length})`}</Header>
      <Card>
        <div style={{ marginLeft: "16px" }}>
          <div style={{ display: "flex", flexDirection: "row", alignItems: "center", gap: "16px" }}>
            <div style={{ flex: 1 }}>
              <div style={{ display: "flex", flexDirection: "column" }}>
                <CardLabel>{t("GC_APPLICATION_NUMBER_LABEL")}</CardLabel>
                <TextInput
                  placeholder={t("GC_SEARCH_APP_NO_PLACEHOLDER")}
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                  style={{ width: "100%", padding: "8px", height: "150%" }}
                />
              </div>
            </div>
            <div style={{ flex: 1 }}>
              <div style={{ display: "flex", flexDirection: "column" }}>
                <CardLabel>{t("PT_COMMON_TABLE_COL_STATUS_LABEL")}</CardLabel>
                <Dropdown
                  className="form-field"
                  selected={status}
                  select={setStatus}
                  option={statusOptions}
                  placeholder={t("CS_COMMON_SELECT_STATUS")}
                  optionKey="value"
                  style={{ width: "100%" }}
                  t={t}
                />
              </div>
            </div>
            <div>
              <div style={{ marginTop: "17%" }}>
                <SubmitBar label={t("ES_COMMON_SEARCH")} onSubmit={handleSearch} />
                <p
                  className="link"
                  style={{ marginLeft: "30%", marginTop: "10px", display: "block" }}
                  onClick={clearAll}
                >
                  {t(`ES_COMMON_CLEAR_ALL`)}
                </p>
              </div>
            </div>
          </div>
        </div>
      </Card>
      <div>
        {filteredApplications.length > 0 &&
          filteredApplications.map((application, index) => (
            <div key={application.applicationNo || index}>
              <GCApplication 
                application={application} 
                tenantId={tenantId} 
              />
            </div>
          ))}
        {filteredApplications.length === 0 && !isLoading && (
          <p style={{ marginLeft: "16px", marginTop: "16px" }}>{t("GC_NO_APPLICATION_FOUND_MSG")}</p>
        )}

        {filteredApplications.length !== 0 && totalCount >= t1 && (
          <div>
            <p style={{ marginLeft: "16px", marginTop: "16px" }}>
              <span className="link">
                <Link to={`/upyog-ui/citizen/gc/my-applications/${t1}`}>{t("CS_LOAD_MORE")}</Link>
              </span>
            </p>
          </div>
        )}
      </div>
    </React.Fragment>
  );
};

export default GCMyApplications;
