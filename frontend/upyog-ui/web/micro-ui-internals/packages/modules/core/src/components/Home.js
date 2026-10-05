import {
  BackButton,
  BillsIcon,
  CitizenHomeCard,
  CitizenInfoLabel,
  FSMIcon,
  Loader,
  MCollectIcon,
  OBPSIcon,
  PGRIcon,
  PTIcon,
  TLIcon,
  WSICon,
  PTRIcon,
  CHBIcon
} from "@nudmcdgnpm/digit-ui-react-components";
import React, { useState } from "react";
import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";
import EmployeeDashboard from "./EmployeeDashboard";

/* 
Feature :: Citizen All service screen cards
*/
export const processLinkData = (newData, code, t) => {
  const obj = newData?.[`${code}`];
  if (obj) {
    obj.map((link) => {
      (link.link = link["navigationURL"]), (link.i18nKey = t(link["name"]));
    });
  }
  const newObj = {
    links: obj?.reverse(),
    header: Digit.Utils.locale.getTransformedLocale(`ACTION_TEST_${code}`),
    iconName: `CITIZEN_${code}_ICON`,
  };
  if (code === "FSM") {
    const roleBasedLoginRoutes = [
      {
        role: "FSM_DSO",
        from: "/upyog-ui/citizen/fsm/dso-dashboard",
        dashoardLink: "CS_LINK_DSO_DASHBOARD",
        loginLink: "CS_LINK_LOGIN_DSO",
      },
    ];
    //RAIN-7297
    roleBasedLoginRoutes.map(({ role, from, loginLink, dashoardLink }) => {
      if (Digit.UserService.hasAccess(role))
        newObj?.links?.push({
          link: from,
          i18nKey: t(dashoardLink),
        });
      else
        newObj?.links?.push({
          link: `/upyog-ui/citizen/login`,
          state: { role: "FSM_DSO", from },
          i18nKey: t(loginLink),
        });
    });
  }

  return newObj;
};
const iconSelector = (code) => {
  switch (code) {
    case "PT":
      return <PTIcon className="fill-path-primary-main" />;
    case "WS":
      return <WSICon className="fill-path-primary-main" />;
    case "FSM":
      return <FSMIcon className="fill-path-primary-main" />;
    case "MCollect":
      return <MCollectIcon className="fill-path-primary-main" />;
    case "PGR":
      return <PGRIcon className="fill-path-primary-main" />;
    case "TL":
      return <TLIcon className="fill-path-primary-main" />;
    case "OBPS":
      return <OBPSIcon className="fill-path-primary-main" />;
    case "Bills":
      return <BillsIcon className="fill-path-primary-main" />;
    case "PTR":
      return <PTRIcon className="fill-path-primary-main" />;
    case "CHB":
      return <CHBIcon className="fill-path-primary-main" />;
    case "ADS":
      return <CHBIcon className="fill-path-primary-main" />;
    default:
      return <PTIcon className="fill-path-primary-main" />;
  }
};

const moduleColorMap = {
  PT: { color: "#1d4ed8", bg: "#eff6ff", border: "#dbeafe" },
  TL: { color: "#059669", bg: "#ecfdf5", border: "#d1fae5" },
  OBPS: { color: "#7c3aed", bg: "#f5f3ff", border: "#ede9fe" },
  BPA: { color: "#7c3aed", bg: "#f5f3ff", border: "#ede9fe" },
  WS: { color: "#0f766e", bg: "#f0fdfa", border: "#ccfbf1" },
  FSM: { color: "#d97706", bg: "#fffbeb", border: "#fef3c7" },
  Bills: { color: "#2563eb", bg: "#eff6ff", border: "#dbeafe" },
  Payment: { color: "#2563eb", bg: "#eff6ff", border: "#dbeafe" },
  PTR: { color: "#db2777", bg: "#fdf2f8", border: "#fce7f3" },
  CHB: { color: "#dc2626", bg: "#fef2f2", border: "#fee2e2" },
  ADS: { color: "#dc2626", bg: "#fef2f2", border: "#fee2e2" },
  PGR: { color: "#ea580c", bg: "#fff7ed", border: "#ffedd5" },
  MCollect: { color: "#86198f", bg: "#fdf4ff", border: "#fae8ff" },
};

const CitizenHome = ({ modules = [], getCitizenMenu, fetchedCitizen, isLoading }) => {
  const { t } = useTranslation();
  const [searchTerm, setSearchTerm] = useState("");
  const [activeCategory, setActiveCategory] = useState("ALL");

  const paymentModule = modules.filter(({ code }) => code === "Payment")[0];
  const moduleArr = modules.filter(({ code }) => code !== "Payment");
  const moduleArray = paymentModule ? [paymentModule, ...moduleArr] : moduleArr;

  if (isLoading) {
    return <Loader />;
  }

  // Pre-process modules with their link data
  const processedModules = moduleArray
    .filter((mod) => mod)
    .map(({ code }) => {
      const mdmsDataObj = fetchedCitizen ? processLinkData(getCitizenMenu, code, t) : undefined;
      const rawLinks = mdmsDataObj?.links?.filter((ele) => ele?.link)?.sort((x, y) => (x?.orderNumber || 0) - (y?.orderNumber || 0)) || [];
      const links = rawLinks.map((item) => ({
        ...item,
        sidebarURL: item.sidebarURL ? item.sidebarURL.replace("digit-ui", "upyog-ui") : item.sidebarURL,
        navigationURL: item.navigationURL ? item.navigationURL.replace("digit-ui", "upyog-ui") : item.navigationURL,
        link: item.link ? item.link.replace("digit-ui", "upyog-ui") : item.link,
      }));
      const rawHeader = mdmsDataObj?.header;
      const header = rawHeader ? (t(rawHeader) !== rawHeader ? t(rawHeader) : rawHeader.replace("ACTION_TEST_", "").replace(/_/g, " ")) : code;
      const theme = moduleColorMap[code] || { color: "#1F1887", bg: "#EEF2FF", border: "#E0E7FF" };

      return {
        code,
        header,
        links,
        theme,
        isInfo: code === "OBPS",
      };
    })
    .filter((mod) => mod.links.length > 0);

  const totalAllServicesCount = processedModules.reduce((sum, m) => sum + m.links.length, 0);

  // Categories for pills with counts
  const categories = [
    { code: "ALL", label: t("ALL_SERVICES", "All Services"), count: totalAllServicesCount },
    ...processedModules.map((m) => ({ code: m.code, label: m.header, count: m.links.length })),
  ];

  // Filter based on search query & active category
  const filteredModules = processedModules
    .filter((mod) => activeCategory === "ALL" || mod.code === activeCategory)
    .map((mod) => {
      if (!searchTerm.trim()) return mod;
      const term = searchTerm.toLowerCase().trim();
      const matchesHeader = mod.header?.toLowerCase().includes(term);
      const matchedLinks = mod.links.filter((l) => (l.i18nKey || l.name || "").toLowerCase().includes(term));
      if (matchesHeader) return mod;
      if (matchedLinks.length > 0) return { ...mod, links: matchedLinks };
      return null;
    })
    .filter(Boolean);

  const totalFilteredServices = filteredModules.reduce((sum, m) => sum + m.links.length, 0);

  return (
    <div className="citizen-all-services-page">
      {/* Top Header Card */}
      <div className="services-header-card">
        <div className="header-top-row">
          <div className="header-left">
            <BackButton />
            <div className="header-title-wrap">
              <h1 className="page-main-title">{t("CITIZEN_ALL_SERVICES_TITLE", "All Citizen Services")}</h1>
              <p className="page-sub-title">
                {t(
                  "CITIZEN_ALL_SERVICES_SUBTITLE",
                  "Browse, apply and pay for municipal services, licenses, permits and certificates online."
                )}
              </p>
            </div>
          </div>

          <div className="header-search-wrap">
            <div className="search-input-group">
              <svg className="search-svg-icon" viewBox="0 0 20 20" fill="none" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
              </svg>
              <input
                type="text"
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                placeholder={t("SEARCH_SERVICES_PLACEHOLDER", "Search by service name...")}
                className="search-input-field"
              />
              {searchTerm && (
                <button
                  type="button"
                  onClick={() => setSearchTerm("")}
                  className="search-clear-button"
                  title={t("CLEAR", "Clear")}
                >
                  ✕
                </button>
              )}
            </div>
          </div>
        </div>

        {/* Category Pills Strip */}
        <div className="category-pills-bar">
          {categories.map((cat) => (
            <button
              key={cat.code}
              type="button"
              className={`cat-pill-btn ${activeCategory === cat.code ? "cat-pill-btn--active" : ""}`}
              onClick={() => setActiveCategory(cat.code)}
            >
              <span>{cat.label}</span>
              <span className="pill-counter">{cat.count}</span>
            </button>
          ))}
        </div>
      </div>

      {/* Results Overview Bar */}
      <div className="services-meta-bar">
        <div className="meta-left">
          <span className="meta-text">
            {t("SHOWING_RESULTS", "Showing")}{" "}
            <strong className="meta-highlight">{totalFilteredServices}</strong> {totalFilteredServices === 1 ? t("SERVICE", "service") : t("SERVICES", "services")}{" "}
            {t("IN", "in")}{" "}
            <strong className="meta-highlight">{filteredModules.length}</strong> {filteredModules.length === 1 ? t("CATEGORY", "category") : t("CATEGORIES", "categories")}
          </span>
          {searchTerm && (
            <span className="meta-filter-tag">
              {t("SEARCH_QUERY", "Search")}: "{searchTerm}"
              <button type="button" onClick={() => setSearchTerm("")} className="tag-remove-btn">✕</button>
            </span>
          )}
          {activeCategory !== "ALL" && (
            <span className="meta-filter-tag">
              {t("CATEGORY", "Category")}: {categories.find((c) => c.code === activeCategory)?.label}
              <button type="button" onClick={() => setActiveCategory("ALL")} className="tag-remove-btn">✕</button>
            </span>
          )}
        </div>
        {(searchTerm || activeCategory !== "ALL") && (
          <button
            type="button"
            onClick={() => {
              setSearchTerm("");
              setActiveCategory("ALL");
            }}
            className="reset-all-filters-btn"
          >
            {t("RESET_ALL_FILTERS", "Reset all filters")}
          </button>
        )}
      </div>

      {/* Services Grid */}
      {filteredModules.length > 0 ? (
        <div className="all-services-cards-grid">
          {filteredModules.map((mod) => (
            <div key={mod.code} className="module-service-card">
              <div className="module-card-header">
                <div
                  className={`module-card-icon module-theme-${mod.code?.toLowerCase() || "default"}`}
                >
                  {iconSelector(mod.code)}
                </div>
                <div className="module-card-meta">
                  <h2 className="module-card-title">{mod.header}</h2>
                  <span className="module-card-count">
                    {mod.links.length} {mod.links.length === 1 ? t("SERVICE", "service") : t("SERVICES", "services")}
                  </span>
                </div>
              </div>

              <div className="module-links-list">
                {mod.links.map((linkItem, i) => {
                  const isExternal =
                    linkItem?.parentModule?.toUpperCase() === "BIRTH" ||
                    linkItem?.parentModule?.toUpperCase() === "DEATH" ||
                    linkItem?.parentModule?.toUpperCase() === "FIRENOC";

                  const linkContent = (
                    <>
                      <div className="link-item-left">
                        <span className={`link-item-dot link-dot-${mod.code?.toLowerCase() || "default"}`} />
                        <span className="link-item-text">{linkItem.i18nKey}</span>
                      </div>
                      <svg className="link-item-chevron" viewBox="0 0 20 20" fill="currentColor">
                        <path fillRule="evenodd" d="M7.293 14.707a1 1 0 010-1.414L10.586 10 7.293 6.707a1 1 0 011.414-1.414l4 4a1 1 0 010 1.414l-4 4a1 1 0 01-1.414 0z" clipRule="evenodd" />
                      </svg>
                    </>
                  );

                  return (
                    <div key={linkItem?.id || i} className="module-link-row">
                      {isExternal ? (
                        <a href={linkItem.link} className="module-service-link">
                          {linkContent}
                        </a>
                      ) : (
                        <Link to={{ pathname: linkItem.link, state: linkItem.state }} className="module-service-link">
                          {linkContent}
                        </Link>
                      )}
                    </div>
                  );
                })}
              </div>

              {mod.isInfo && (
                <div className="module-card-info">
                  <CitizenInfoLabel
                    className="citizen-info-label-flush"
                    info={t("CS_FILE_APPLICATION_INFO_LABEL")}
                    text={t(`BPA_CITIZEN_HOME_STAKEHOLDER_INCLUDES_INFO_LABEL`)}
                  />
                </div>
              )}
            </div>
          ))}
        </div>
      ) : (
        <div className="services-empty-view">
          <div className="empty-icon-circle">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" className="empty-icon-svg">
              <path strokeLinecap="round" strokeLinejoin="round" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
            </svg>
          </div>
          <h3 className="empty-title">{t("NO_SERVICES_FOUND", "No matching services found")}</h3>
          <p className="empty-desc">
            {t("NO_SERVICES_FOUND_DESC", "We couldn't find any services matching your criteria. Try searching with different keywords or clear the filters.")}
          </p>
          <button
            type="button"
            onClick={() => {
              setSearchTerm("");
              setActiveCategory("ALL");
            }}
            className="empty-reset-btn"
          >
            {t("VIEW_ALL_SERVICES", "View All Services")}
          </button>
        </div>
      )}
    </div>
  );
};


const EmployeeHome = ({ modules }) => {
  const { t } = useTranslation();
  if (window.Digit.SessionStorage.get("PT_CREATE_EMP_TRADE_NEW_FORM")) window.Digit.SessionStorage.set("PT_CREATE_EMP_TRADE_NEW_FORM", {});

  return (
    <div className="employee-app-container">
      <EmployeeDashboard modules={modules} />
    </div>
  );
};

export const AppHome = ({ userType, modules, getCitizenMenu, fetchedCitizen, isLoading }) => {
  if (userType === "citizen") {
    return <CitizenHome modules={modules} getCitizenMenu={getCitizenMenu} fetchedCitizen={fetchedCitizen} isLoading={isLoading} />;
  }
  return <EmployeeHome modules={modules} />;
};
