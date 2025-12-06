import { Header, Loader } from "@upyog/digit-ui-react-components";
import React from "react";
import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";
import EwasteApplication from "./Ewaste-application";

/**
 * Displays a list of E-Waste applications submitted by the citizen.
 * Includes pagination, application status tracking, and feedback display.
 * Provides navigation to create new applications and view existing ones.
 *
 * @returns {JSX.Element} List of applications with status and actions
 */
export const EWASTEMyApplications = () => {
  const { t } = useTranslation();
  const tenantId = Digit.ULBService.getCitizenCurrentTenant(true) || Digit.ULBService.getCurrentTenantId();
  const user = Digit.UserService.getUser().info;

  /**
   * Pagination configuration
   */
  let filter = window.location.href.split("/").pop();
  let t1;
  let off;
  if (!isNaN(parseInt(filter))) {
    off = filter;
    t1 = parseInt(filter) + 50;
  } else {
    t1 = 4;
  }

  /**
   * Search filters for API request
   */
  let filter1 = !isNaN(parseInt(filter))
    ? { limit: "50", sortOrder: "ASC", sortBy: "createdTime", offset: off, tenantId }
    : { limit: "4", sortOrder: "ASC", sortBy: "createdTime", offset: "0", mobileNumber: user?.mobileNumber, tenantId };

  const { isLoading, isError, error, data } = Digit.Hooks.ew.useEWSearch({ filters: filter1 }, { filters: filter1 });

  const { EwasteApplication: applicationsList } = data || {};
  let combinedApplicationNumber = applicationsList?.length > 0 ? applicationsList?.map((ob) => ob?.applicationNumber) : [];

  /**
   * Configuration for feedback search
   */
  let serviceSearchArgs = {
    tenantId: tenantId,
    referenceIds: combinedApplicationNumber,
  };

  const { isLoading: serviceloading, data: servicedata } = Digit.Hooks.useFeedBackSearch(
    { filters: { serviceSearchArgs } },
    { filters: { serviceSearchArgs }, enabled: combinedApplicationNumber?.length > 0 ? true : false, cacheTime: 0 }
  );

  /**
   * Determines button label based on feedback status
   * 
   * @param {Object} curservice Current application data
   * @returns {string} Translated label text
   */
  function getLabelValue(curservice) {
    let foundValue = servicedata?.Service?.find((ob) => ob?.referenceId?.includes(curservice?.applicationNumber));
    return foundValue ? t("CS_CF_VIEW") : t("CS_CF_TRACK");
  }

  if (isLoading || serviceloading) {
    return <Loader />;
  }


  return (
    <React.Fragment>
      <Header>{`${t("EW_MY_REQUEST")} ${applicationsList ? `(${applicationsList.length})` : ""}`}</Header>
      <div>
        {applicationsList?.length > 0 &&
          applicationsList.map((application, index) => (
            <div key={index}>
              <EwasteApplication application={application} tenantId={user?.permanentCity} buttonLabel={getLabelValue(application)} />
            </div>
          ))}
        {!applicationsList?.length > 0 && <p style={{ marginLeft: "16px", marginTop: "16px" }}>{t("EWASTE_NO_APPLICATION_FOUND_MSG")}</p>}

        {applicationsList?.length !== 0 && (
          <div>
            <p style={{ marginLeft: "16px", marginTop: "16px" }}>
              <span className="link">{<Link to={`/upyog-ui/citizen/ew/myApplication/${t1}`}>{t("EWASTE_LOAD_MORE_MSG")}</Link>}</span>
            </p>
          </div>
        )}
      </div>

      <p style={{ marginLeft: "16px", marginTop: "16px" }}>
        {t("EWASTE_TEXT_NOT_ABLE_TO_FIND_THE_APPLICATION")}{" "}
        <span className="link" style={{ display: "block" }}>
          <Link to="/upyog-ui/citizen/ew/raiseRequest/productdetails">{t("EWASTE_COMMON_CLICK_HERE_TO_REGISTER_NEW_APPLICATION")}</Link>
        </span>
      </p>
    </React.Fragment>
  );
};