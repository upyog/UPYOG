// Importing necessary components and hooks from external libraries and local files
import { Header, Loader } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next"; // Hook for translations
import EwasteApplication from "./Ewaste-application"; // Component for rendering individual E-Waste applications

// Main component for displaying the list of E-Waste applications submitted by the citizen
export const EWASTEMyApplications = () => {
  const { t } = useTranslation(); // Translation hook
  const tenantId = Digit.ULBService.getCitizenCurrentTenant(true) || Digit.ULBService.getCurrentTenantId(); // Fetching the tenant ID
  const user = Digit.UserService.getUser().info; // Fetching the logged-in user's information

  // Extracting the filter value from the URL
  let filter = window.location.href.split("/").pop();
  let t1; // Variable to calculate the offset for pagination
  let off; // Variable to store the offset value
  if (!isNaN(parseInt(filter))) {
    off = filter;
    t1 = parseInt(filter) + 50; // Incrementing the offset for the next page
  } else {
    t1 = 4; // Default limit for the first page
  }

  // Defining the filter object for the API call
  let filter1 = !isNaN(parseInt(filter))
    ? { limit: "50", sortOrder: "ASC", sortBy: "createdTime", offset: off, tenantId }
    : { limit: "4", sortOrder: "ASC", sortBy: "createdTime", offset: "0", mobileNumber: user?.mobileNumber, tenantId };

  // Fetching the list of E-Waste applications using a custom hook
  const { isLoading, isError, error, data } = Digit.Hooks.ew.useEWSearch({ filters: filter1 }, { filters: filter1 });

  // Extracting the list of applications from the API response
  const { EwasteApplication: applicationsList } = data || {};
  let combinedApplicationNumber = applicationsList?.length > 0 ? applicationsList?.map((ob) => ob?.applicationNumber) : [];

  // Defining arguments for fetching feedback data
  let serviceSearchArgs = {
    tenantId: tenantId,
    referenceIds: combinedApplicationNumber,
  };

  // Fetching feedback data for the applications
  const { isLoading: serviceloading, data: servicedata } = Digit.Hooks.useFeedBackSearch(
    { filters: { serviceSearchArgs } },
    { filters: { serviceSearchArgs }, enabled: combinedApplicationNumber?.length > 0 ? true : false, cacheTime: 0 }
  );

  // Function to determine the label for the action button (View or Track)
  function getLabelValue(curservice) {
    let foundValue = servicedata?.Service?.find((ob) => ob?.referenceId?.includes(curservice?.applicationNumber));

    if (foundValue) return t("CS_CF_VIEW");
    else return t("CS_CF_TRACK");
  }

  // Display a loader while the data is being fetched
  if (isLoading || serviceloading) {
    return <Loader />;
  }

  return (
    <React.Fragment>
      {/* Header displaying the total number of applications */}
      <Header>{`${t("EW_MY_REQUEST")} ${applicationsList ? `(${applicationsList.length})` : ""}`}</Header>
      <div>
        {/* Rendering the list of applications */}
        {applicationsList?.length > 0 &&
          applicationsList.map((application, index) => (
            <div key={index}>
              <EwasteApplication application={application} tenantId={user?.permanentCity} buttonLabel={getLabelValue(application)} />
            </div>
          ))}

        {/* Message to display if no applications are found */}
        {!applicationsList?.length > 0 && <p style={{ marginLeft: "16px", marginTop: "16px" }}>{t("EWASTE_NO_APPLICATION_FOUND_MSG")}</p>}

        {/* Load more button for pagination */}
        {applicationsList?.length !== 0 && (
          <div>
            <p style={{ marginLeft: "16px", marginTop: "16px" }}>
              <span className="link">{<Link to={`/digit-ui/citizen/ew/myApplication/${t1}`}>{t("EWASTE_LOAD_MORE_MSG")}</Link>}</span>
            </p>
          </div>
        )}
      </div>

      {/* Link to navigate to the page for creating a new application */}
      <p style={{ marginLeft: "16px", marginTop: "16px" }}>
        {t("EWASTE_TEXT_NOT_ABLE_TO_FIND_THE_APPLICATION")}{" "}
        <span className="link" style={{ display: "block" }}>
          <Link to="/digit-ui/citizen/ew/raiseRequest/productdetails">{t("EWASTE_COMMON_CLICK_HERE_TO_REGISTER_NEW_APPLICATION")}</Link>
        </span>
      </p>
    </React.Fragment>
  );
};
