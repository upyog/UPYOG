import React, { useState } from "react";
import { useTranslation } from "react-i18next";

const SCHEMES = [
  { code: "WORKS_SCHEME_FC15", label: "15th Finance Commission" },
  { code: "WORKS_SCHEME_MW", label: "Municipal Works" },
  { code: "WORKS_SCHEME_SBM", label: "Swachh Bharat Mission (SBM)" },
  { code: "WORKS_SCHEME_PMAY", label: "Pradhan Mantri Awas Yojana (PMAY)" },
  { code: "WORKS_SCHEME_NSDP", label: "National Slum Development Program (NSDP)" },
];

const SearchActionPlan = ({ searchParams = {}, onSearch, onClear }) => {
  const { t } = useTranslation();

  const [appNo, setAppNo] = useState(searchParams.applicationNumber || "");
  const [fy, setFy] = useState(searchParams.financialYear || "");
  const [scheme, setScheme] = useState(searchParams.scheme || "");

  const handleSearchSubmit = (e) => {
    e?.preventDefault();
    onSearch({
      applicationNumber: appNo.trim() || undefined,
      financialYear: fy || undefined,
      scheme: scheme || undefined,
    });
  };

  const handleClear = () => {
    setAppNo("");
    setFy("");
    setScheme("");
    onClear();
  };

  return (
    <form className="works-search-card" onSubmit={handleSearchSubmit}>
      <div className="works-search-header">{t("ES_COMMON_SEARCH_BY", "Search Applications")}</div>

      <div className="works-search-grid">
        <div className="works-search-field">
          <label className="works-search-label" htmlFor="works-search-appno">
            {t("WORKS_AP_APPLICATION_NO", "Action Plan / App No.")}
          </label>
          <input
            id="works-search-appno"
            type="text"
            className="works-search-input"
            placeholder={t("WORKS_AP_SEARCH_APP_PLACEHOLDER", "e.g. WORKS-AP-2026-0001")}
            value={appNo}
            onChange={(e) => setAppNo(e.target.value)}
          />
        </div>

        <div className="works-search-field">
          <label className="works-search-label" htmlFor="works-search-fy">
            {t("WORKS_AP_FINANCIAL_YEAR", "Financial Year")}
          </label>
          <select
            id="works-search-fy"
            className="works-search-input"
            value={fy}
            onChange={(e) => setFy(e.target.value)}
          >
            <option value="">{t("ES_COMMON_ALL", "All Financial Years")}</option>
            <option value="2025-26">2025-26</option>
            <option value="2024-25">2024-25</option>
            <option value="2023-24">2023-24</option>
          </select>
        </div>

        <div className="works-search-field">
          <label className="works-search-label" htmlFor="works-search-scheme">
            {t("WORKS_AP_SCHEME", "Scheme")}
          </label>
          <select
            id="works-search-scheme"
            className="works-search-input"
            value={scheme}
            onChange={(e) => setScheme(e.target.value)}
          >
            <option value="">{t("ES_COMMON_ALL", "All Schemes")}</option>
            {SCHEMES.map((s) => (
              <option key={s.code} value={s.code}>
                {t(s.code, s.label)}
              </option>
            ))}
          </select>
        </div>

        <div className="works-search-actions">
          <button type="submit" className="works-search-btn">
            {t("ES_COMMON_SEARCH", "Search")}
          </button>
          <button type="button" className="works-search-clear-btn" onClick={handleClear}>
            {t("ES_COMMON_CLEAR_SEARCH", "Clear")}
          </button>
        </div>
      </div>
    </form>
  );
};

export default SearchActionPlan;

