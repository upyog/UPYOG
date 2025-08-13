import { Header, Loader, Card, CardLabel, TextInput, SubmitBar } from "@nudmcdgnpm/digit-ui-react-components";
import React, { useState,useEffect } from "react";
import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";
import PGRApplication from "./PGRAI-application";

{/*
The `PGRAIMyApplications` component:
 - A React functional component that displays a list of PGR_AI applications for the logged-in user.
- It fetches application data using the `useSearchPGRAI` hook and displays it in a structured format.
- Includes features like search functionality, pagination, and dynamic rendering of application details.
- Utilizes translation (`t`) for internationalization and tenant-specific data for filtering applications.
*/}
export const PGRAIMyApplications = () => {
    const { t } = useTranslation();
    const tenantId = Digit.ULBService.getCitizenCurrentTenant(true) || Digit.ULBService.getCurrentTenantId();
    const user = Digit.UserService.getUser().info;

 const [searchTerm, setSearchTerm] = useState("");
  
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
      ? { limit: "50", offset: off, tenantId }
      : { limit: "50", offset: "0", tenantId};

    const [filters, setFilters] = useState(initialFilters);

  const { isLoading, isError, error, data } = Digit.Hooks.pgrAi.useSearchPGRAI({ filters });
   
  useEffect(() => {
      setFilters(initialFilters);
    }, [filter]);

  const handleSearch = () => {
    const trimmedSearchTerm = searchTerm.trim();
    const searchFilters = {
      ...initialFilters,
      serviceRequestId: trimmedSearchTerm || undefined,
    };
    setFilters(searchFilters);
  };

  if (isLoading) {
    return <Loader />;
  }
  
  const filteredApplications = data?.ServiceWrappers || [];

  return (
      <React.Fragment>
        <Header>{`${t("PGR_AI_MY_BOOKINGS_HEADER")} (${filteredApplications.length})`}</Header>
        <Card>
          <div style={{ marginLeft: "16px" }}>
            <div style={{ display: "flex", flexDirection: "row", alignItems: "center", gap: "16px" }}>
              <div style={{ flex: 2 }}>
                <div style={{ display: "flex", flexDirection: "column" }}>
                  <CardLabel>{t("PGR_AI_GRIEVANCE_NO")}</CardLabel>
                  <TextInput
                    placeholder={t("Enter Booking No.")}
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                    style={{ width: "100%", padding: "8px", height: "150%" }}
                  />
                </div>
              </div>
              <div>
                <div style={{ marginTop: "17%" }}>
                  <SubmitBar label={t("ES_COMMON_SEARCH")} onSubmit={handleSearch} />
                  <p
                    className="link"
                    style={{ marginLeft: "30%", marginTop: "10px", display: "block" }}
                    onClick={() => setSearchTerm("")}
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
              <div key={index}>
                <PGRApplication application={application} tenantId={tenantId} buttonLabel={t("PGR_AI_SUMMARY")} />
              </div>
            ))}
          {filteredApplications.length === 0 && !isLoading && (
            <p style={{ marginLeft: "16px", marginTop: "16px" }}>{t("PGR_AI_NO_APPLICATION_FOUND_MSG")}</p>
          )}
      </div>
    </React.Fragment>
  );
};
