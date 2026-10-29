import { BackButton } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { useTranslation } from "react-i18next";
import { Link } from "react-router-dom";

const TLCitizenHomeScreen = () => {
  const { t } = useTranslation();
  const userInfo = Digit.UserService.getUser()?.info;

  // Fetch user's TL applications if logged in
  const { data: tlApplications, isLoading: isTLLoading } = Digit.Hooks.tl.useTLSearchApplication(
    {},
    {
      enabled: !!userInfo?.mobileNumber,
    },
    t
  );

  const totalApplications = userInfo?.mobileNumber
    ? !isTLLoading
      ? tlApplications?.length ?? 0
      : "-"
    : "-";

  const getTranslated = (key, fallback) => {
    const res = t(key);
    return !res || res === key ? fallback : res;
  };

  const pageTitle = getTranslated("ACTION_TEST_TRADE_LICENSE", "Trade License");
  const pageSubtitle = getTranslated(
    "TL_HOME_PAGE_SUBTITLE",
    "Apply for new commercial trade license, renew existing license, and track application status."
  );

  // Exact 3 services as originally defined in TLLinks
  const allServices = [
    {
      id: "apply-tl",
      title: getTranslated("TL_CREATE_TRADE", "Apply for Trade License"),
      desc: "Submit application for a new trade license for your commercial establishment or business unit.",
      link: "/upyog-ui/citizen/tl/tradelicence/new-application",
      tag: "New License",
      tagType: "blue",
      icon: (
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
          <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z" />
          <polyline points="14 2 14 8 20 8" />
          <line x1="12" y1="18" x2="12" y2="12" />
          <line x1="9" y1="15" x2="15" y2="15" />
        </svg>
      ),
    },
    {
      id: "renew-tl",
      title: getTranslated("TL_RENEWAL_HEADER", "Renewal of Trade License"),
      desc: "Renew your existing commercial trade license for the current financial year online.",
      link: "/upyog-ui/citizen/tl/tradelicence/renewal-list",
      tag: "Renewal",
      tagType: "amber",
      icon: (
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
          <polyline points="23 4 23 10 17 10" />
          <polyline points="1 20 1 14 7 14" />
          <path d="M3.51 9a9 9 0 0 1 14.85-3.36L23 10M1 14l4.64 4.36A9 9 0 0 0 20.49 15" />
        </svg>
      ),
    },
    {
      id: "my-applications",
      title: getTranslated("TL_MY_APPLICATIONS_HEADER", "My Applications"),
      desc: "Track real-time workflow status, inspection updates, and fee payments for your trade applications.",
      link: "/upyog-ui/citizen/tl/tradelicence/my-application",
      tag: "Live Status",
      tagType: "green",
      icon: (
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
          <circle cx="12" cy="12" r="10" />
          <polyline points="12 6 12 12 16 14" />
        </svg>
      ),
    },
  ];

  return (
    <div className="pt-portal-view">
      {/* 1. Hero Banner with Image Clearly Visible */}
      <div className="pt-portal-hero">
        <img src="/upyog-ui/images/dashboard-banner.jpg" alt="Trade License Banner" className="pt-portal-hero-img" />
        <div className="pt-portal-hero-shade" />

        <div className="pt-portal-hero-content">
          <div className="pt-portal-hero-nav">
            <BackButton className="pt-portal-back-btn" />
            <div className="pt-portal-badge">
              <span className="pt-badge-dot" />
              <span>{getTranslated("MODULE_TL", "TRADE LICENSE")}</span>
            </div>
          </div>

          <div className="pt-portal-hero-intro">
            <h1 className="pt-portal-title">{pageTitle}</h1>
            <p className="pt-portal-subtitle">{pageSubtitle}</p>
          </div>
        </div>
      </div>

      {/* 2. Seamless Activity Metrics Strip */}
      <div className="pt-portal-metrics-bar">
        <Link to="/upyog-ui/citizen/tl/tradelicence/my-application" className="pt-metric-link">
          <div className="pt-metric-icon icon--applications">
            <svg viewBox="0 0 24 24" fill="currentColor">
              <path d="M20 6h-4V4c0-1.11-.89-2-2-2h-4c-1.11 0-2 .89-2 2v2H4c-1.11 0-1.99.89-1.99 2L2 19c0 1.11.89 2 2 2h16c1.11 0 2-.89 2-2V8c0-1.11-.89-2-2-2zm-6 0h-4V4h4v2z" />
            </svg>
          </div>
          <div className="pt-metric-text">
            <span className="pt-metric-number">{totalApplications}</span>
            <span className="pt-metric-label">{getTranslated("TL_MY_APPLICATIONS_HEADER", "My Applications")}</span>
          </div>
        </Link>

        <span className="pt-metric-sep" />

        <Link to="/upyog-ui/citizen/tl/tradelicence/new-application" className="pt-metric-link">
          <div className="pt-metric-icon icon--properties">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <path d="M12 5v14M5 12h14" />
            </svg>
          </div>
          <div className="pt-metric-text">
            <span className="pt-metric-number">Apply</span>
            <span className="pt-metric-label">{getTranslated("TL_CREATE_TRADE", "New License")}</span>
          </div>
        </Link>

        <span className="pt-metric-sep" />

        <Link to="/upyog-ui/citizen/tl/tradelicence/renewal-list" className="pt-metric-link">
          <div className="pt-metric-icon icon--bills">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <polyline points="23 4 23 10 17 10" />
              <polyline points="1 20 1 14 7 14" />
              <path d="M3.51 9a9 9 0 0 1 14.85-3.36L23 10M1 14l4.64 4.36A9 9 0 0 0 20.49 15" />
            </svg>
          </div>
          <div className="pt-metric-text">
            <span className="pt-metric-number">Renew</span>
            <span className="pt-metric-label">{getTranslated("TL_RENEWAL_HEADER", "Renew License")}</span>
          </div>
        </Link>
      </div>

      {/* 3. Non-Card Seamless Services Matrix (Exact 3 items, 3 per row on desktop) */}
      <div className="pt-portal-matrix-container">
        <div className="pt-matrix-header">
          <div className="pt-matrix-title-wrap">
            <h2 className="pt-matrix-title">{getTranslated("TL_ALL_SERVICES_HEADER", "Trade License Services")}</h2>
            <span className="pt-matrix-subtitle">Apply for new license, renew existing license, and track applications</span>
          </div>
          <span className="pt-matrix-count">{allServices.length} Services</span>
        </div>

        <div className="pt-portal-matrix">
          {allServices.map((service, index) => (
            <Link key={service.id} to={service.link} className="pt-matrix-item">
              <div className="pt-item-num">0{index + 1}</div>
              <div className={`pt-item-icon icon--${service.tagType}`}>{service.icon}</div>
              <div className="pt-item-text">
                <div className="pt-item-heading">
                  <span className="pt-item-title">{service.title}</span>
                  <span className={`pt-item-tag tag--${service.tagType}`}>{service.tag}</span>
                </div>
                <p className="pt-item-desc">{service.desc}</p>
              </div>
              <div className="pt-item-arrow">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.2" strokeLinecap="round" strokeLinejoin="round">
                  <path d="M5 12h14M12 5l7 7-7 7" />
                </svg>
              </div>
            </Link>
          ))}
        </div>
      </div>
    </div>
  );
};

export default TLCitizenHomeScreen;
