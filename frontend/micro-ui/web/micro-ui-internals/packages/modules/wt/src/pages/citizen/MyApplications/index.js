import React, { useState, useEffect } from "react";
import { Header, Loader, TextInput, Dropdown, SubmitBar, CardLabel, Card } from "@nudmcdgnpm/digit-ui-react-components";
import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";
import WTApplication from "./wt-application";


/**
 * `WTMyApplications` is a React component that displays a list of water tanker service applications for the current user.
 * The component:
 * - Fetches data related to water tanker applications using the `useTankerSearchAPI` hook.
 * - Displays a search form where users can filter applications by:
 *   - Booking Number (`searchTerm`).
 *   - Status (`status`), such as "Booked", "Cancelled", or "Pending for Payment".
 * - Handles pagination to show a limited number of applications at a time, with the ability to load more.
 * - Displays application details by mapping over the fetched data and rendering `WTApplication` components for each application.
 * - Includes UI elements like:
 *   - A search bar for filtering by Booking Number.
 *   - A dropdown for filtering by application status.
 *   - A "Search" button to trigger the search based on the provided filters.
 *   - A "Clear All" link to reset the filters.
 * - Provides a link to create a new water tanker service request.
 * 
 * The component also handles:
 * - Displaying a loading spinner while fetching data.
 * - Showing a message if no applications match the search criteria.
 * - Rendering a "Load More" link if there are additional applications to display beyond the current set.
 * 
 * @returns {JSX.Element} A list of filtered water tanker applications with search and filter options.
 */
export const WTMyApplications = () => {
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
    ? { limit: "50", sortOrder: "ASC", sortBy: "createdTime", offset: off, tenantId }
    : { limit: "4", sortOrder: "ASC", sortBy: "createdTime", offset: "0", tenantId };

  useEffect(() => {
    setFilters(initialFilters);
  }, [filter]);

  // Use the search hook with dynamic filters
  const { isLoading, data } = Digit.Hooks.wt.useTankerSearchAPI({ filters });

  const handleSearch = () => {
    const trimmedSearchTerm = searchTerm.trim();
    const searchFilters = {
      ...initialFilters,
      bookingNo: trimmedSearchTerm || undefined,
      status: status?.code || undefined,
    };
    
    // Update the filters state to trigger refetch
    setFilters(searchFilters);
  };

  if (isLoading) {
    return <Loader />;
  }

  const statusOptions = [
    { i18nKey: "Booked", code: "BOOKED", value: t("CHB_BOOKED") },
    { i18nKey: "Booking in Progres", code: "BOOKING_CREATED", value: t("CHB_BOOKING_IN_PROGRES") },
    { i18nKey: "Pending For Payment", code: "PENDING_FOR_PAYMENT", value: t("PENDING_FOR_PAYMENT") },
    { i18nKey: "Booking Expired", code: "EXPIRED", value: t("EXPIRED") },
    { i18nKey: "Cancelled", code: "CANCELLED", value: t("CANCELLED") }
  ];

  const filteredApplications = data?.waterTankerBookingDetail || [];

  return (
    <React.Fragment>
      <Header>{`${t("WT_MY_APPLICATION")} (${filteredApplications.length})`}</Header>
      <Card>
        <div style={{ marginLeft: "16px" }}>
          <div style={{ display: "flex", flexDirection: "row", alignItems: "center", gap: "16px" }}>
            <div style={{ flex: 1 }}>
              <div style={{ display: "flex", flexDirection: "column" }}>
                <CardLabel>{t("WT_BOOKING_NO")}</CardLabel>
                <TextInput
                  placeholder={t("Enter Booking No.")}
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                  style={{ width: "100%", padding: "8px", height: '150%' }}
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
                  placeholder={t("Select Status")}
                  optionKey="value"
                  style={{ width: "100%" }}
                  t={t}
                />
              </div>
            </div>
            <div>
              <div style={{marginTop:"17%"}}>
              <SubmitBar label={t("ES_COMMON_SEARCH")} onSubmit={handleSearch} />
              <p className="link" style={{marginLeft:"30%",marginTop:"10px",display: "block"}}
                      onClick={() => {setSearchTerm(""),setStatus("") }}>{t(`ES_COMMON_CLEAR_ALL`)}
                </p>
              </div>
            </div>
          </div>
            <Link to="/digit-ui/citizen/wt/request-service/info">
              <SubmitBar style={{borderRadius:"30px",width:"20%" }} label={t("WT_NEW_REQUEST")+" +"} />
            </Link>
        </div>
      </Card>
      <div>
        {filteredApplications.length > 0 &&
          filteredApplications.map((application, index) => (
            <div key={index}>
              <WTApplication
                application={application}
                tenantId={tenantId}
                buttonLabel={t("WT_SUMMARY")}
              />
            </div>
          ))}
        {filteredApplications.length === 0 && !isLoading && (
          <p style={{ marginLeft: "16px", marginTop: "16px" }}>
            {t("WT_NO_APPLICATION_FOUND_MSG")}
          </p>
        )}

        {filteredApplications.length !== 0 && data?.count>t1 && (
          <div>
            <p style={{ marginLeft: "16px", marginTop: "16px" }}>
              <span className="link">
                <Link to={`/digit-ui/citizen/wt/status/${t1}`}>
                  {t("WT_LOAD_MORE_MSG")}
                </Link>
              </span>
            </p>
          </div>
        )}
      </div>
    </React.Fragment>
  );
};
