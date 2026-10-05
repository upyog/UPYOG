import { BackButton } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { useTranslation } from "react-i18next";
import { Link } from "react-router-dom";

const PTCitizenHomeScreen = () => {
  const { t } = useTranslation();
  const userInfo = Digit.UserService.getUser()?.info;
  const stateCode = Digit.ULBService.getStateId();
  const tenantId = Digit.ULBService.getCitizenCurrentTenant(true) || Digit.ULBService.getCurrentTenantId() || stateCode;

  // Fetch user's registered properties count if logged in
  const { data: propertiesData, isLoading: isPropertiesLoading } = Digit.Hooks.pt.usePropertySearchNew(
    {
      filters: {
        limit: "50",
        sortOrder: "ASC",
        sortBy: "createdTime",
        offset: "0",
        mobileNumber: userInfo?.mobileNumber,
        tenantId,
        status: "ACTIVE,INACTIVE",
      },
      searchedFrom: "myPropertyCitizen",
    },
    {
      filters: {
        mobileNumber: userInfo?.mobileNumber,
        tenantId,
      },
      enabled: !!userInfo?.mobileNumber,
    }
  );

  // Fetch user's in-workflow applications count if logged in
  const { data: applicationsData, isLoading: isApplicationsLoading } = Digit.Hooks.pt.usePropertySearch(
    {
      filters: {
        limit: "50",
        sortOrder: "ASC",
        sortBy: "createdTime",
        offset: "0",
        mobileNumber: userInfo?.mobileNumber,
        tenantId,
        status: "INWORKFLOW",
      },
    },
    {
      filters: {
        mobileNumber: userInfo?.mobileNumber,
        tenantId,
      },
      enabled: !!userInfo?.mobileNumber,
    }
  );

  const propertiesCount = userInfo?.mobileNumber
    ? !isPropertiesLoading
      ? propertiesData?.Properties?.length ?? 0
      : "-"
    : "-";

  const applicationsCount = userInfo?.mobileNumber
    ? !isApplicationsLoading
      ? applicationsData?.Properties?.length ?? 0
      : "-"
    : "-";

  const getTranslated = (key, fallback) => {
    const res = t(key);
    return !res || res === key ? fallback : res;
  };

  const pageTitle = getTranslated("ACTION_TEST_PROPERTY_TAX", "Property Tax");
  const pageSubtitle = getTranslated(
    "PT_HOME_PAGE_SUBTITLE",
    "Pay dues online, self-assess new properties, apply for mutation, and download receipts."
  );

  // Unified list of all PT services (2 to 3 per row)
  const allServices = [
    {
      id: "search-pay",
      title: getTranslated("PT_SEARCH_AND_PAY", "Search & Pay Property Tax"),
      desc: "Find property by ID, owner name, or mobile number to view dues and make instant payments.",
      link: "/upyog-ui/citizen/pt/property/citizen-search",
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
      title: getTranslated("CS_TITLE_MY_BILLS", "My Property Bills & Dues"),
      desc: "Check current and pending property tax demands, payment history, and annual statements.",
      link: "/upyog-ui/citizen/payment/my-bills/PT",
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
      id: "register-property",
      title: getTranslated("PT_CREATE_PROPERTY", "Register New Property"),
      desc: "Submit self-assessment for newly acquired, built, or previously unassessed properties.",
      link: "/upyog-ui/citizen/pt/property/new-application",
      tag: "Assessment",
      tagType: "green",
      icon: (
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
          <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z" />
          <polyline points="9 22 9 12 15 12 15 22" />
          <line x1="12" y1="5" x2="12" y2="9" />
          <line x1="10" y1="7" x2="14" y2="7" />
        </svg>
      ),
    },
    {
      id: "my-properties",
      title: getTranslated("PT_MY_PROPERTIES", "My Registered Properties"),
      desc: "View properties registered under your mobile number, unit structures, and assessment orders.",
      link: "/upyog-ui/citizen/pt/property/my-properties",
      tag: "Ownership",
      tagType: "purple",
      icon: (
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
          <rect x="3" y="3" width="7" height="7" />
          <rect x="14" y="3" width="7" height="7" />
          <rect x="14" y="14" width="7" height="7" />
          <rect x="3" y="14" width="7" height="7" />
        </svg>
      ),
    },
    {
      id: "mutation",
      title: getTranslated("PT_PROPERTY_MUTATION", "Property Mutation / Transfer"),
      desc: "Apply for legal ownership transfer of property due to sale deed, inheritance, or gift.",
      link: "/upyog-ui/citizen/pt/property/property-mutation",
      tag: "Mutation",
      tagType: "orange",
      icon: (
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
          <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2" />
          <circle cx="9" cy="7" r="4" />
          <path d="M23 21v-2a4 4 0 0 0-3-3.87" />
          <path d="M16 3.13a4 4 0 0 1 0 7.75" />
        </svg>
      ),
    },
    {
      id: "my-applications",
      title: getTranslated("PT_MY_APPLICATION", "Track PT Applications"),
      desc: "Monitor real-time workflow status and field inspection progress for submitted applications.",
      link: "/upyog-ui/citizen/pt/property/my-applications",
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
      title: getTranslated("PT_MY_PAYMENTS_HEADER", "Payment Receipts & History"),
      desc: "Download verified municipal tax receipts, payment acknowledgements, and No Due Certificates.",
      link: "/upyog-ui/citizen/pt/property/my-payments",
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
      title: getTranslated("PT_HOW_IT_WORKS", "How Property Tax Works"),
      desc: "Understand property assessment rules, rebate schemes, tax computation, and payment steps.",
      link: "/upyog-ui/citizen/pt-how-it-works",
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
      title: getTranslated("PT_FAQ_S", "Frequently Asked Questions"),
      desc: "Quick answers to common questions regarding tax calculation, due dates, mutations, and receipts.",
      link: "/upyog-ui/citizen/pt-faq",
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
        <img src="/upyog-ui/images/dashboard-banner.jpg" alt="Property Tax Banner" className="pt-portal-hero-img" />
        <div className="pt-portal-hero-shade" />

        <div className="pt-portal-hero-content">
          <div className="pt-portal-hero-nav">
            <BackButton className="pt-portal-back-btn" />
            <div className="pt-portal-badge">
              <span className="pt-badge-dot" />
              <span>{getTranslated("MODULE_PT", "PROPERTY TAX")}</span>
            </div>
          </div>

          <div className="pt-portal-hero-intro">
            <h1 className="pt-portal-title">{pageTitle}</h1>
            <p className="pt-portal-subtitle">{pageSubtitle}</p>
          </div>
        </div>
      </div>

      {/* 2. Seamless Activity Metrics Strip (No Cards) */}
      <div className="pt-portal-metrics-bar">
        <Link to="/upyog-ui/citizen/pt/property/my-properties" className="pt-metric-link">
          <div className="pt-metric-icon icon--properties">
            <svg viewBox="0 0 24 24" fill="currentColor">
              <path d="M19 9.3V4h-3v2.6L12 3 2 12h3v8h5v-6h4v6h5v-8h3l-3-2.7z" />
            </svg>
          </div>
          <div className="pt-metric-text">
            <span className="pt-metric-number">{propertiesCount}</span>
            <span className="pt-metric-label">{getTranslated("PT_MY_PROPERTIES", "My Properties")}</span>
          </div>
        </Link>

        <span className="pt-metric-sep" />

        <Link to="/upyog-ui/citizen/payment/my-bills/PT" className="pt-metric-link">
          <div className="pt-metric-icon icon--bills">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <rect x="2" y="5" width="20" height="14" rx="2" />
              <line x1="2" y1="10" x2="22" y2="10" />
            </svg>
          </div>
          <div className="pt-metric-text">
            <span className="pt-metric-number">Pay Dues</span>
            <span className="pt-metric-label">{getTranslated("CS_TITLE_MY_BILLS", "My Bills")}</span>
          </div>
        </Link>

        <span className="pt-metric-sep" />

        <Link to="/upyog-ui/citizen/pt/property/my-applications" className="pt-metric-link">
          <div className="pt-metric-icon icon--applications">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z" />
              <polyline points="14 2 14 8 20 8" />
            </svg>
          </div>
          <div className="pt-metric-text">
            <span className="pt-metric-number">{applicationsCount}</span>
            <span className="pt-metric-label">{getTranslated("PT_MY_APPLICATION", "My Applications")}</span>
          </div>
        </Link>

        <span className="pt-metric-sep" />

        <Link to="/upyog-ui/citizen/pt/property/my-payments" className="pt-metric-link">
          <div className="pt-metric-icon icon--receipts">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4" />
              <polyline points="7 10 12 15 17 10" />
              <line x1="12" y1="15" x2="12" y2="3" />
            </svg>
          </div>
          <div className="pt-metric-text">
            <span className="pt-metric-number">Receipts</span>
            <span className="pt-metric-label">{getTranslated("PT_MY_PAYMENTS_HEADER", "Payment History")}</span>
          </div>
        </Link>
      </div>

      {/* 3. Non-Card Seamless Services Matrix (2 to 3 per row) */}
      <div className="pt-portal-matrix-container">
        <div className="pt-matrix-header">
          <div className="pt-matrix-title-wrap">
            <h2 className="pt-matrix-title">{getTranslated("PT_ALL_SERVICES_HEADER", "Property Tax Services")}</h2>
            <span className="pt-matrix-subtitle">Access self-assessment, dues calculation, mutations, and municipal records</span>
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

export default PTCitizenHomeScreen;
