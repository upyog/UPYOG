import { Header, Loader } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";
import PTApplication from "./pt-application";

export const PTMyApplications = () => {
  const { t } = useTranslation();
  const tenantId = Digit.ULBService.getCitizenCurrentTenant(true) || Digit.ULBService.getCurrentTenantId();
  const user = Digit.UserService.getUser()?.info;
  let filter = window.location.href.split("/").pop();
  let t1;
  let off;
  if (!isNaN(parseInt(filter))) {
    off = filter;
    t1 = parseInt(filter) + 50;
  } else {
    t1 = 4;
  }
  let filter1 = !isNaN(parseInt(filter))
    ? {
        limit: "50",
        sortOrder: "ASC",
        sortBy: "createdTime",
        offset: off,
        tenantId,
        status: "INWORKFLOW",
      }
    : {
        limit: "4",
        sortOrder: "ASC",
        sortBy: "createdTime",
        offset: "0",
        mobileNumber: user?.mobileNumber,
        tenantId,
        status: "INWORKFLOW",
      };

  const { isLoading, isError, error, data } = Digit.Hooks.pt.usePropertySearch(
    {
      filters: filter1,
    },
    {
      filters: filter1,
    }
  );

  const { Properties: applicationsList } = data || {};
  let combinedApplicationNumber = applicationsList?.length > 0 ? applicationsList?.map((ob) => ob?.acknowldgementNumber) : [];
  let serviceSearchArgs = {
    tenantId: tenantId,
    referenceIds: combinedApplicationNumber,
  };

  const { isLoading: serviceloading, data: servicedata } = Digit.Hooks.useFeedBackSearch(
    {
      filters: {
        serviceSearchArgs,
      },
    },
    {
      filters: {
        serviceSearchArgs,
      },
      enabled: combinedApplicationNumber?.length > 0 ? true : false,
      cacheTime: 0,
    }
  );

  function getLabelValue(curservice) {
    let foundValue = servicedata?.Service?.find((ob) => ob?.referenceId?.includes(curservice?.acknowldgementNumber));
    if (foundValue) return t("CS_CF_VIEW");
    else if (!foundValue && curservice?.status?.includes("ACTIVE")) return t("CS_CF_RATE_US");
    else return t("CS_CF_TRACK");
  }

  if (isLoading || serviceloading) {
    return <Loader />;
  }

  return (
    <div className="pt-citizen-my-applications-container">
      <div className="pt-citizen-my-apps-header-wrap">
        <div className="pt-citizen-my-apps-title-block">
          <Header className="pt-citizen-my-apps-title">
            {t("CS_TITLE_MY_APPLICATIONS")}
          </Header>
          {applicationsList && (
            <span className="pt-citizen-my-apps-count-badge">
              {applicationsList.length}
            </span>
          )}
        </div>
        <Link
          to="/upyog-ui/citizen/pt/property/new-application/info"
          className="pt-citizen-register-app-btn"
        >
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
            <line x1="12" y1="5" x2="12" y2="19" />
            <line x1="5" y1="12" x2="19" y2="12" />
          </svg>
          <span>{t("PT_COMMON_CLICK_HERE_TO_REGISTER_NEW_PROPERTY")}</span>
        </Link>
      </div>

      {applicationsList?.length > 0 ? (
        <div className="pt-citizen-applications-grid">
          {applicationsList.map((application, index) => (
            <PTApplication
              key={application?.acknowldgementNumber || index}
              application={application}
              tenantId={user?.permanentCity}
              buttonLabel={getLabelValue(application)}
            />
          ))}
        </div>
      ) : (
        <div className="pt-citizen-empty-apps-card">
          <div className="pt-citizen-empty-icon-wrap">
            <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="#94a3b8" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round">
              <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z" />
              <polyline points="14 2 14 8 20 8" />
              <line x1="16" y1="13" x2="8" y2="13" />
              <line x1="16" y1="17" x2="8" y2="17" />
              <polyline points="10 9 9 9 8 9" />
            </svg>
          </div>
          <h3 className="pt-citizen-empty-title">{t("PT_NO_APPLICATION_FOUND_MSG")}</h3>
          <p className="pt-citizen-empty-subtitle">{t("PT_TEXT_NOT_ABLE_TO_FIND_THE_APPLICATION")}</p>
          <Link
            to="/upyog-ui/citizen/pt/property/new-application/info"
            className="pt-citizen-register-app-btn"
          >
            {t("PT_COMMON_CLICK_HERE_TO_REGISTER_NEW_PROPERTY")}
          </Link>
        </div>
      )}

      {applicationsList?.length > 0 && (
        <div className="pt-citizen-apps-footer-card">
          <div className="pt-citizen-load-more-wrap">
            <Link
              to={`/upyog-ui/citizen/pt/property/my-applications/${t1}`}
              className="pt-citizen-load-more-btn"
            >
              {t("PT_LOAD_MORE_MSG")}
            </Link>
          </div>
          <div className="pt-citizen-help-text">
            <span>{t("PT_TEXT_NOT_ABLE_TO_FIND_THE_APPLICATION")}</span>{" "}
            <Link
              to="/upyog-ui/citizen/pt/property/new-application/info"
              className="pt-citizen-help-link"
            >
              {t("PT_COMMON_CLICK_HERE_TO_REGISTER_NEW_PROPERTY")}
            </Link>
          </div>
        </div>
      )}
    </div>
  );
};

