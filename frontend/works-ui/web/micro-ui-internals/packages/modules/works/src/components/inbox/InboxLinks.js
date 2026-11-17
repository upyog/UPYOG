import React from "react";
import { useTranslation } from "react-i18next";
import { Link } from "react-router-dom";

const InboxLinks = ({ parentRoute = "/works-ui/employee/works" }) => {
  const { t } = useTranslation();
  const basePath = parentRoute?.includes("/inbox")
    ? parentRoute.replace(/\/inbox.*$/, "")
    : (parentRoute || "/works-ui/employee/works");

  return (
    <div className="works-inbox-links-card">
      <div className="works-inbox-links-header">
        <span className="works-inbox-links-icon" aria-hidden="true">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
            <rect x="2" y="7" width="20" height="14" rx="2" ry="2" />
            <path d="M16 21V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v16" />
          </svg>
        </span>
        <span>{t("ACTION_TEST_WORKS", "Works Management")}</span>
      </div>

      <div className="works-inbox-links-body">
        <Link to={`${basePath}/create-action-plan`} className="works-inbox-link-item">
          <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
            <line x1="12" y1="5" x2="12" y2="19" />
            <line x1="5" y1="12" x2="19" y2="12" />
          </svg>
          <span>{t("WORKS_CREATE_ACTION_PLAN", "Create Action Plan")}</span>
        </Link>

        <Link to={`${basePath}/search-application`} className="works-inbox-link-item">
          <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
            <circle cx="11" cy="11" r="8" />
            <line x1="21" y1="21" x2="16.65" y2="16.65" />
          </svg>
          <span>{t("ES_COMMON_SEARCH_APPLICATION", "Search Applications")}</span>
        </Link>
      </div>
    </div>
  );
};

export default InboxLinks;

