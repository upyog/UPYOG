import { BackButton } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { useTranslation } from "react-i18next";
import { Link } from "react-router-dom";

const WSCitizenHomeScreen = () => {
  const { t } = useTranslation();
  const userInfo = Digit.UserService.getUser()?.info;
  const tenantId = Digit.ULBService.getCitizenCurrentTenant(true) || Digit.ULBService.getCurrentTenantId();

  // Fetch WS & SW connections count if logged in
  const { data: wsConnData, isLoading: isWSConnLoading } = Digit.Hooks.ws.useMyApplicationSearch(
    {
      tenantId,
      filters: {
        searchType: "CONNECTION",
        mobileNumber: userInfo?.mobileNumber,
      },
      BusinessService: "WS",
      t,
    },
    {
      enabled: !!userInfo?.mobileNumber,
    }
  );

  const { data: swConnData, isLoading: isSWConnLoading } = Digit.Hooks.ws.useMyApplicationSearch(
    {
      tenantId,
      filters: {
        searchType: "CONNECTION",
        mobileNumber: userInfo?.mobileNumber,
      },
      BusinessService: "SW",
      t,
    },
    {
      enabled: !!userInfo?.mobileNumber,
    }
  );

  // Fetch WS & SW applications count if logged in
  const { data: wsAppData, isLoading: isWSAppLoading } = Digit.Hooks.ws.useMyApplicationSearch(
    {
      tenantId,
      filters: {
        searchType: "APPLICATION",
        mobileNumber: userInfo?.mobileNumber,
      },
      BusinessService: "WS",
      t,
    },
    {
      enabled: !!userInfo?.mobileNumber,
    }
  );

  const { data: swAppData, isLoading: isSWAppLoading } = Digit.Hooks.ws.useMyApplicationSearch(
    {
      tenantId,
      filters: {
        searchType: "APPLICATION",
        mobileNumber: userInfo?.mobileNumber,
      },
      BusinessService: "SW",
      t,
    },
    {
      enabled: !!userInfo?.mobileNumber,
    }
  );

  const connectionsCount = userInfo?.mobileNumber
    ? !isWSConnLoading && !isSWConnLoading
      ? (wsConnData?.WaterConnection?.length || 0) + (swConnData?.SewerageConnections?.length || 0)
      : "-"
    : "-";

  const applicationsCount = userInfo?.mobileNumber
    ? !isWSAppLoading && !isSWAppLoading
      ? (wsAppData?.WaterConnection?.length || 0) + (swAppData?.SewerageConnections?.length || 0)
      : "-"
    : "-";

  const getTranslated = (key, fallback) => {
    const res = t(key);
    return !res || res === key ? fallback : res;
  };

  const pageTitle = getTranslated("ACTION_TEST_WATER_AND_SEWERAGE", "Water & Sewerage");
  const pageSubtitle = getTranslated(
    "WS_HOME_PAGE_SUBTITLE",
    "Pay water dues online, apply for new connections, view consumption history, and download payment receipts."
  );

  // Unified list of all WS services (2 to 3 per row)
  const allServices = [
    {
      id: "search-pay",
      title: getTranslated("ACTION_TEXT_WS_SEARCH_AND_PAY", "Search & Pay Water Bills"),
      desc: "Find connection by consumer ID, old connection number, or mobile to view dues and make instant payments.",
      link: "/upyog-ui/citizen/ws/search",
      tag: "Instant Pay",
      tagType: "blue",
      icon: (
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
          <circle cx="11" cy="11" r="8" />
          <line x1="21" y1="21" x2="16.65" y2="16.65" />
        </svg>
      ),
    },
    {
      id: "my-bills",
      title: getTranslated("ACTION_TEST_WNS_MY_BILLS", "My Water & Sewerage Bills"),
      desc: "Access current demand statements, consumption bill copies, meter readings, and penalty arrears.",
      link: "/upyog-ui/citizen/ws/my-bills",
      tag: "Demands",
      tagType: "amber",
      icon: (
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
          <rect x="2" y="5" width="20" height="14" rx="2" />
          <line x1="2" y1="10" x2="22" y2="10" />
          <path d="M6 15h2M14 15h4" />
        </svg>
      ),
    },
    {
      id: "apply-connection",
      title: getTranslated("ACTION_TEST_APPLY_NEW_CONNECTION", "Apply for New Connection"),
      desc: "Submit new domestic, commercial, or institutional water supply and sewerage connection request.",
      link: "/upyog-ui/citizen/ws/create-application",
      tag: "Assessment",
      tagType: "green",
      icon: (
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
          <path d="M12 2.69l5.66 5.66a8 8 0 1 1-11.31 0z" />
          <line x1="12" y1="9" x2="12" y2="15" />
          <line x1="9" y1="12" x2="15" y2="12" />
        </svg>
      ),
    },
    {
      id: "my-connections",
      title: getTranslated("ACTION_TEXT_WS_MY_CONNECTION", "My Registered Connections"),
      desc: "View connections linked with your account, connection category, meter numbers, and water status.",
      link: "/upyog-ui/citizen/ws/my-connections",
      tag: "Ownership",
      tagType: "purple",
      icon: (
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
          <rect x="2" y="2" width="20" height="8" rx="2" ry="2" />
          <rect x="2" y="14" width="20" height="8" rx="2" ry="2" />
          <line x1="6" y1="6" x2="6.01" y2="6" />
          <line x1="6" y1="18" x2="6.01" y2="18" />
        </svg>
      ),
    },
    {
      id: "my-applications",
      title: getTranslated("ACTION_TEXT_WS_MY_APPLICATION", "Track WS Applications"),
      desc: "Monitor real-time workflow status, field inspection, estimation notices, and sanction letters.",
      link: "/upyog-ui/citizen/ws/my-applications",
      tag: "Status",
      tagType: "sky",
      icon: (
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
          <circle cx="12" cy="12" r="10" />
          <polyline points="12 6 12 12 16 14" />
        </svg>
      ),
    },
    {
      id: "my-payments",
      title: getTranslated("ACTION_TEST_MY_PAYMENTS", "Payment Receipts & History"),
      desc: "Download official digitally signed payment receipts, transaction logs, and clearance vouchers.",
      link: "/upyog-ui/citizen/ws/my-payments",
      tag: "Receipts",
      tagType: "rose",
      icon: (
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
          <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z" />
          <polyline points="14 2 14 8 20 8" />
          <path d="M9 15l2 2 4-4" />
        </svg>
      ),
    },
    {
      id: "how-it-works",
      title: getTranslated("WS_HOW_IT_WORKS", "How Water Services Work"),
      desc: "Understand water connection procedures, required documents, tariff slabs, and road cutting fees.",
      link: "/upyog-ui/citizen/ws-how-it-works",
      tag: "Guide",
      tagType: "indigo",
      icon: (
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
          <polygon points="12 2 2 7 12 12 22 7 12 2" />
          <polyline points="2 17 12 22 22 17" />
          <polyline points="2 12 12 17 22 12" />
        </svg>
      ),
    },
    {
      id: "faqs",
      title: getTranslated("WS_FAQ_S", "Frequently Asked Questions"),
      desc: "Quick answers to common queries regarding connection sanctions, billing cycles, meter checks, and transfers.",
      link: "/upyog-ui/citizen/ws-faq",
      tag: "Help",
      tagType: "teal",
      icon: (
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
          <circle cx="12" cy="12" r="10" />
          <path d="M9.09 9a3 3 0 0 1 5.83 1c0 2-3 3-3 3" />
          <line x1="12" y1="17" x2="12.01" y2="17" />
        </svg>
      ),
    },
  ];

  return (
    <div className="pt-portal-view">
      {/* 1. Hero Banner with Image Clearly Visible */}
      <div className="pt-portal-hero">
        <img src="/upyog-ui/images/dashboard-banner.jpg" alt="Water & Sewerage Banner" className="pt-portal-hero-img" />
        <div className="pt-portal-hero-shade" />

        <div className="pt-portal-hero-content">
          <div className="pt-portal-hero-nav">
            <BackButton className="pt-portal-back-btn" />
            <div className="pt-portal-badge">
              <span className="pt-badge-dot" />
              <span>{getTranslated("MODULE_WS", "WATER & SEWERAGE")}</span>
            </div>
          </div>

          <div className="pt-portal-hero-intro">
            <h1 className="pt-portal-title">{pageTitle}</h1>
            <p className="pt-portal-subtitle">{pageSubtitle}</p>
          </div>
        </div>
      </div>

      {/* 2. Seamless Activity Metrics Strip (No Card Boxes) */}
      <div className="pt-portal-metrics-bar">
        <Link to="/upyog-ui/citizen/ws/my-connections" className="pt-metric-link">
          <div className="pt-metric-icon icon--properties">
            <svg viewBox="0 0 24 24" fill="currentColor">
              <path d="M12 2.69l5.66 5.66a8 8 0 1 1-11.31 0z" />
            </svg>
          </div>
          <div className="pt-metric-text">
            <span className="pt-metric-number">{connectionsCount}</span>
            <span className="pt-metric-label">{getTranslated("ACTION_TEXT_WS_MY_CONNECTION", "My Connections")}</span>
          </div>
        </Link>

        <span className="pt-metric-sep" />

        <Link to="/upyog-ui/citizen/ws/my-bills" className="pt-metric-link">
          <div className="pt-metric-icon icon--bills">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <rect x="2" y="5" width="20" height="14" rx="2" />
              <line x1="2" y1="10" x2="22" y2="10" />
            </svg>
          </div>
          <div className="pt-metric-text">
            <span className="pt-metric-number">Pay Dues</span>
            <span className="pt-metric-label">{getTranslated("ACTION_TEST_WNS_MY_BILLS", "My Bills")}</span>
          </div>
        </Link>

        <span className="pt-metric-sep" />

        <Link to="/upyog-ui/citizen/ws/my-applications" className="pt-metric-link">
          <div className="pt-metric-icon icon--applications">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z" />
              <polyline points="14 2 14 8 20 8" />
            </svg>
          </div>
          <div className="pt-metric-text">
            <span className="pt-metric-number">{applicationsCount}</span>
            <span className="pt-metric-label">{getTranslated("ACTION_TEXT_WS_MY_APPLICATION", "My Applications")}</span>
          </div>
        </Link>

        <span className="pt-metric-sep" />

        <Link to="/upyog-ui/citizen/ws/my-payments" className="pt-metric-link">
          <div className="pt-metric-icon icon--receipts">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4" />
              <polyline points="7 10 12 15 17 10" />
              <line x1="12" y1="15" x2="12" y2="3" />
            </svg>
          </div>
          <div className="pt-metric-text">
            <span className="pt-metric-number">Receipts</span>
            <span className="pt-metric-label">{getTranslated("ACTION_TEST_MY_PAYMENTS", "Payment History")}</span>
          </div>
        </Link>
      </div>

      {/* 3. Non-Card Seamless Services Matrix (2 to 3 per row) */}
      <div className="pt-portal-matrix-container">
        <div className="pt-matrix-header">
          <div className="pt-matrix-title-wrap">
            <h2 className="pt-matrix-title">{getTranslated("WS_ALL_SERVICES_HEADER", "Water & Sewerage Services")}</h2>
            <span className="pt-matrix-subtitle">Access connection requests, utility dues calculation, meter details, and municipal records</span>
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

export default WSCitizenHomeScreen;
