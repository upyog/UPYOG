import React, { useState, useEffect } from "react";
import { Header, Loader, TextInput, Dropdown, SubmitBar, CardLabel, Card } from "@nudmcdgnpm/digit-ui-react-components";
import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";
import GCApplication from "./gc-application";

export const GCMyApplications = () => {
  const { t } = useTranslation();
  const tenantId = Digit.ULBService.getCitizenCurrentTenant(true) || Digit.ULBService.getCurrentTenantId();
  const user = Digit.UserService.getUser().info;

  const [searchTerm, setSearchTerm] = useState("");
  const [status, setStatus] = useState(null);
  const [appliedFilters, setAppliedFilters] = useState({ mobileNumber: user?.mobileNumber });

  let filter = window.location.href.split("/").pop();
  let t1;
  let off;
  if (!isNaN(parseInt(filter))) {
    off = filter;
    t1 = parseInt(filter) + 50;
  } else {
    t1 = 4;
  }

  // Adjust this hook name if your GC search API hook is named differently 
  // (e.g., useGCSearchApplication or useGarbageSearch)

  const { isLoading, data } = Digit.Hooks.gc.useGCSearch({ 
    tenantId,
    filters: { ...appliedFilters, limit: t1 }
  });

  const handleSearch = () => {
    const trimmedSearchTerm = searchTerm.trim();
    setAppliedFilters({
      mobileNumber: user?.mobileNumber,
      ...(trimmedSearchTerm && { applicationNo: trimmedSearchTerm }),
      ...(status?.code && { status: status.code, applicationStatus: status.code })
    });
  };

  const clearAll = () => {
    setSearchTerm("");
    setStatus(null);
    setAppliedFilters({ mobileNumber: user?.mobileNumber });
  };

  if (isLoading) {
    return <Loader />;
  }

  const statusOptions = [
    { code: "APPLICATION_CREATED", value: t("GC_STATUS_INITIATED") },
    { code: "PENDINGPAYMENT", value: t("GC_STATUS_PENDINGPAYMENT") },
    { code: "APPROVED", value: t("GC_STATUS_APPROVED") },
    { code: "REJECTED", value: t("GC_STATUS_REJECTED") },
  ];

  let filteredApplications = data?.garbageAccounts || data?.GarbageApplications || data?.data || [];

  // Local filtering to ensure accurate results if backend search parameters are missing or exact-match only
  if (appliedFilters?.applicationNo) {
    filteredApplications = filteredApplications.filter((app) => {
      const appNo = app?.grbgApplication?.applicationNo || app?.applicationNo || "";
      return appNo.toLowerCase().includes(appliedFilters.applicationNo.toLowerCase());
    });
  }

  const searchStatus = appliedFilters?.status || appliedFilters?.applicationStatus;
  if (searchStatus) {
    filteredApplications = filteredApplications.filter((app) => {
      const appStatus = app?.applicationStatus || app?.status || app?.grbgApplication?.status || "";
      return appStatus === searchStatus;
    });
  }

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

        {filteredApplications.length !== 0 && data?.count > t1 && (
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
