
import React, { useState, useEffect } from "react";
import { Header, Loader, TextInput, Dropdown, SubmitBar, CardLabel, Card } from "@upyog/digit-ui-react-components";
import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";
import WTApplication from "./wt-application";
import { APPLICATION_PATH } from "../../../utils";

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
  const [serviceType, setServiceType] = useState("watertanker"); // `serviceType` stores the actual service type used in API calls and updates only when the search button is clicked.

  const [tempServiceType, setTempServiceType] = useState("watertanker"); // Temporary state

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
    ? { limit: "50", sortOrder: "ASC", sortBy: "createdTime", offset: off, tenantId, MobileNumber:user?.mobileNumber }
    : { limit: "4", sortOrder: "ASC", sortBy: "createdTime", offset: "0", tenantId ,MobileNumber:user?.mobileNumber };

  useEffect(() => {
    setFilters(initialFilters);
  }, [filter]);

  // Conditionally enable hooks based on service type
  const { isLoading: isLoadingTanker, data: dataTanker } = Digit.Hooks.wt.useTankerSearchAPI(
    { filters },
    { enabled: !!filters && (serviceType === "watertanker") }
  );
  const { isLoading: isLoadingToilet, data: dataToilet } = Digit.Hooks.wt.useMobileToiletSearchAPI(
    { filters },
    { enabled: !!filters && (serviceType === "mobileToilet") }
  );
  const { isLoading: isLoadingTreePruning, data: dataTreePruning } = Digit.Hooks.wt.useTreePruningSearchAPI(
    { filters },
    { enabled: !!filters && (serviceType === "treePruning") }
  );

  // Use the results conditionally based on the `serviceType`
  let isLoading = false;
  let filteredData = [];

  if (serviceType === "watertanker") {
    isLoading = isLoadingTanker;
    filteredData = dataTanker?.waterTankerBookingDetail || [];
  } else if (serviceType === "mobileToilet") {
    isLoading = isLoadingToilet;
    filteredData = dataToilet?.mobileToiletBookingDetails || [];
  } else if (serviceType === "treePruning") {
    isLoading = isLoadingTreePruning;
    filteredData = dataTreePruning?.treePruningBookingDetails;
  } else {
    isLoading = isLoadingTanker || isLoadingToilet || isLoadingTreePruning;
    filteredData = [
      ...(dataToilet?.mobileToiletBookingDetails || []),
      ...(dataTanker?.waterTankerBookingDetail || []),
      ...(dataTreePruning?.treePruningBookingDetails || [])
    ];
  }

  const handleSearch = () => {
    setServiceType(tempServiceType); // Apply service type selection only on search

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

  const serviceOptions = [
    { label: t("MOBILE_TOILET"), code: "mobileToilet" },
    { label: t("WATER_TANKER"), code: "watertanker" },
    { label: t("TREE_PRUNING"), code: "treePruning" }
  ];

  const statusOptions = [
    { i18nKey: "Booking Created", code: "BOOKING_CREATED", value: t("WT_BOOKING_CREATED") },
    { i18nKey: "Booking Approved", code: "APPROVED", value: t("WT_BOOKING_APPROVED") },
    { i18nKey: "Tanker Delivered", code: "TANKER_DELIVERED", value: t("WT_TANKER_DELIVERED") },
    { i18nKey: "Vendor Assigned", code: "ASSIGN_VENDOR", value: t("WT_ASSIGN_VENDOR") },
    { i18nKey: "Rejected", code: "REJECT", value: t("WT_BOOKING_REJECTED") }
  ];

  const statusOptionForTreePruning = [
  {
    i18nKey: "BOOKING_CREATED",
    code: "BOOKING_CREATED",
    value: t("TP_BOOKING_CREATED")
  },
  {
    i18nKey: "PENDING_FOR_APPROVAL",
    code: "PENDING_FOR_APPROVAL",
    value: t("TP_PENDING_FOR_APPROVAL")
  },
  {
    i18nKey: "PAYMENT_PENDING",
    code: "PAYMENT_PENDING",
    value: t("TP_PAYMENT_PENDING")
  },
  {
    i18nKey: "TEAM_ASSIGNMENT_FOR_VERIFICATION",
    code: "TEAM_ASSIGNMENT_FOR_VERIFICATION",
    value: t("TP_TEAM_ASSIGNMENT_FOR_VERIFICATION")
  },
  {
    i18nKey: "TEAM_ASSIGNMENT_FOR_EXECUTION",
    code: "TEAM_ASSIGNMENT_FOR_EXECUTION",
    value: t("TP_TEAM_ASSIGNMENT_FOR_EXECUTION")
  },
  {
    i18nKey: "TREE_PRUNING_SERVICE_COMPLETED",
    code: "TREE_PRUNING_SERVICE_COMPLETED",
    value: t("TP_TREE_PRUNING_SERVICE_COMPLETED")
  }
];

  return (
    <React.Fragment>
      <Header>{`${t("MY_BOOKINGS")} (${filteredData.length})`}</Header>
      <Card>
        <div style={{ marginLeft: "16px" }}>
          <div style={{ display: "flex", flexDirection: "row", alignItems: "center", gap: "16px" }}>
            <div style={{ flex: 1 }}>
              <CardLabel>{t("SERVICE_TYPE")}</CardLabel>
              <Dropdown
                selected={serviceOptions.find((option) => option.code === tempServiceType)}
                select={(option) => {
                  setTempServiceType(option.code);
                  setStatus("");
                }}
                option={serviceOptions}
                placeholder={t("Select Service Type")}
                optionKey="label"
                t={t}
              />
            </div>
            <div style={{ flex: 1 }}>
              <CardLabel>{t("BOOKING_NO")}</CardLabel>
              <TextInput
                placeholder={t("Enter Booking No.")}
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
              />
            </div>
            <div style={{ flex: 1 }}>
              <div style={{ display: "flex", flexDirection: "column" }}>
                <CardLabel>{t("PT_COMMON_TABLE_COL_STATUS_LABEL")}</CardLabel>
                <Dropdown
                  className="form-field"
                  selected={status}
                  select={setStatus}
                  option={tempServiceType === 'treePruning' ? statusOptionForTreePruning : statusOptions}
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
            <Link to={`request-service/service-type`}>
              <SubmitBar style={{borderRadius:"30px",width:"20%" }} label={t("NEW_REQUEST")+" +"} />
            </Link>
        </div>
      </Card>
      <div>
        {filteredData.length > 0 &&
          filteredData.map((application, index) => (
            <div key={index}>
              <WTApplication
                application={application}
                tenantId={tenantId}
                buttonLabel={t("SUMMARY")}
              />
            </div>
          ))}
        {filteredData.length === 0 && !isLoading && (
          <p style={{ marginLeft: "16px", marginTop: "16px" }}>
            {t("NO_APPLICATION_FOUND_MSG")}
          </p>
        )}
        {filteredData.length !== 0 && ((dataToilet?.count || 0) + (dataTanker?.count || 0) + (dataTreePruning?.count || 0)) > t1 && (
          <div>
            <p style={{ marginLeft: "16px", marginTop: "16px" }}>
              <span className="link">
                <Link to={`${APPLICATION_PATH}/citizen/wt/status/${t1}`}>
                  {t("LOAD_MORE_MSG")}
                </Link>
              </span>
            </p>
          </div>
        )}
      </div>
    </React.Fragment>
  );
};
