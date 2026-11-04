import { Header, Loader } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { useTranslation } from "react-i18next";
import { Link } from "react-router-dom";
import MyProperty from "./my-properties";

export const MyProperties = () => {
  const { t } = useTranslation();
  const tenantId = Digit.ULBService.getCitizenCurrentTenant(true) || Digit.ULBService.getCurrentTenantId();
  const user = Digit.UserService.getUser()?.userInfo;
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
        status: "ACTIVE,INACTIVE",
      }
    : {
        limit: "4",
        sortOrder: "ASC",
        sortBy: "createdTime",
        offset: "0",
        mobileNumber: user?.mobileNumber,
        tenantId,
        status: "ACTIVE,INACTIVE",
      };

  const { isLoading, isError, error, data } = Digit.Hooks.pt.usePropertySearchNew(
    {
      filters: filter1,
      searchedFrom: "myPropertyCitizen",
    },
    {
      filters: filter1,
    }
  );

  if (isLoading) {
    return <Loader />;
  }

  const { Properties: applicationsList } = data || {};

  return (
    <div className="pt-citizen-my-properties-container">
      <div className="pt-citizen-my-properties-header-wrap">
        <div className="pt-citizen-my-properties-title-block">
          <Header className="pt-citizen-my-properties-title">
            {t("PT_MY_PROPERTIES_HEADER")}
          </Header>
          {applicationsList && (
            <span className="pt-citizen-my-properties-count-badge">
              {applicationsList.length}
            </span>
          )}
        </div>
        <Link
          to="/upyog-ui/citizen/pt/property/new-application/info"
          className="pt-citizen-register-prop-btn"
        >
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
            <line x1="12" y1="5" x2="12" y2="19" />
            <line x1="5" y1="12" x2="19" y2="12" />
          </svg>
          <span>{t("PT_COMMON_CLICK_HERE_TO_REGISTER_NEW_PROPERTY")}</span>
        </Link>
      </div>

      {applicationsList?.length > 0 ? (
        <div className="pt-citizen-properties-grid">
          {applicationsList.map((application, index) => (
            <MyProperty key={application?.propertyId || index} application={application} />
          ))}
        </div>
      ) : (
        <div className="pt-citizen-empty-properties-card">
          <div className="pt-citizen-empty-icon-wrap">
            <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="#94a3b8" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round">
              <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z" />
              <polyline points="9 22 9 12 15 12 15 22" />
            </svg>
          </div>
          <h3 className="pt-citizen-empty-title">{t("PT_NO_PROP_FOUND_MSG")}</h3>
          <p className="pt-citizen-empty-subtitle">{t("PT_TEXT_NOT_ABLE_TO_FIND_THE_APPLICATION")}</p>
          <Link
            to="/upyog-ui/citizen/pt/property/new-application/info"
            className="pt-citizen-register-prop-btn"
          >
            {t("PT_COMMON_CLICK_HERE_TO_REGISTER_NEW_PROPERTY")}
          </Link>
        </div>
      )}

      {applicationsList?.length > 0 && (
        <div className="pt-citizen-properties-footer-card">
          <div className="pt-citizen-load-more-wrap">
            <Link
              to={`/upyog-ui/citizen/pt/property/my-properties/${t1}`}
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

