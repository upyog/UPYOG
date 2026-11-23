import React, { useState } from "react";
import { useTranslation } from "react-i18next";

const STATUS_OPTIONS = [
  { code: "PENDING_FOR_VERIFICATION", label: "WORKS_STATUS_PENDING_FOR_VERIFICATION", defaultLabel: "Pending Verification" },
  { code: "PENDING_APPROVAL", label: "WORKS_STATUS_PENDING_APPROVAL", defaultLabel: "Pending Approval" },
  { code: "APPROVED", label: "WORKS_STATUS_APPROVED", defaultLabel: "Approved" },
  { code: "INITIATED", label: "WORKS_STATUS_INITIATED", defaultLabel: "Initiated" },
  { code: "REJECTED", label: "WORKS_STATUS_REJECTED", defaultLabel: "Rejected" },
];

const SCHEME_OPTIONS = [
  { code: "MUNICIPAL_WORKS", label: "Municipal Works" },
  { code: "FINANCE_COMMISSION", label: "Finance Commission" },
  { code: "FC_15", label: "15th Finance Commission" },
  { code: "NATIONAL_HEALTH_SCHEME", label: "National Health Scheme" },
  { code: "NSDP", label: "National Slum Development Program (NSDP)" },
  { code: "PMAY", label: "Pradhan Mantri Awas Yojana (PMAY)" },
  { code: "SBM", label: "Swachh Bharat Mission (SBM)" },
];

const FINANCIAL_YEARS = ["2026-27", "2025-26", "2024-25", "2023-24"];

const FilterActionPlan = ({ filters = {}, onFilterChange, statusCounts = {} }) => {
  const { t } = useTranslation();

  const [selectedStatuses, setSelectedStatuses] = useState(
    Array.isArray(filters.status) ? filters.status : filters.status ? [filters.status] : []
  );
  const [selectedScheme, setSelectedScheme] = useState(filters.scheme || "");
  const [selectedFY, setSelectedFY] = useState(filters.financialYear || "");

  const handleStatusToggle = (code) => {
    setSelectedStatuses((prev) =>
      prev.includes(code) ? prev.filter((s) => s !== code) : [...prev, code]
    );
  };

  const handleApply = (e) => {
    e?.preventDefault();
    onFilterChange({
      status: selectedStatuses.length > 0 ? selectedStatuses : undefined,
      scheme: selectedScheme || undefined,
      financialYear: selectedFY || undefined,
    });
  };

  const handleClear = () => {
    setSelectedStatuses([]);
    setSelectedScheme("");
    setSelectedFY("");
    onFilterChange({});
  };

  return (
    <div className="works-filter-card">
      <div className="works-filter-header">
        <h3 className="works-filter-title">{t("ES_COMMON_FILTERS", "Filters")}</h3>
        <button type="button" className="works-filter-clear-btn" onClick={handleClear}>
          {t("ES_COMMON_CLEAR_ALL", "Clear all")}
        </button>
      </div>

      {/* Status Filter */}
      <div className="works-filter-section">
        <span className="works-filter-label">{t("WORKS_INBOX_STATUS", "Status")}</span>
        <div className="works-filter-checkboxes">
          {STATUS_OPTIONS.map((item) => {
            const isChecked = selectedStatuses.includes(item.code);
            const count = statusCounts[item.code] ?? 0;
            return (
              <label key={item.code} className="works-filter-check-item">
                <div style={{ display: "flex", alignItems: "center" }}>
                  <input
                    type="checkbox"
                    checked={isChecked}
                    onChange={() => handleStatusToggle(item.code)}
                  />
                  <span>{t(item.label, item.defaultLabel)}</span>
                </div>
                {count > 0 && <span className="works-filter-count-badge">{count}</span>}
              </label>
            );
          })}
        </div>
      </div>

      {/* Scheme Filter */}
      <div className="works-filter-section">
        <span className="works-filter-label">{t("WORKS_AP_SCHEME", "Scheme")}</span>
        <select
          className="works-search-input"
          value={selectedScheme}
          onChange={(e) => setSelectedScheme(e.target.value)}
        >
          <option value="">{t("ES_COMMON_ALL", "All Schemes")}</option>
          {SCHEME_OPTIONS.map((scheme) => (
            <option key={scheme.code} value={scheme.code}>
              {t(scheme.code, scheme.label)}
            </option>
          ))}
        </select>
      </div>

      {/* Financial Year Filter */}
      <div className="works-filter-section">
        <span className="works-filter-label">{t("WORKS_AP_FINANCIAL_YEAR", "Financial Year")}</span>
        <select
          className="works-search-input"
          value={selectedFY}
          onChange={(e) => setSelectedFY(e.target.value)}
        >
          <option value="">{t("ES_COMMON_ALL", "All Financial Years")}</option>
          {FINANCIAL_YEARS.map((fy) => (
            <option key={fy} value={fy}>
              {fy}
            </option>
          ))}
        </select>
      </div>

      <button type="button" className="works-filter-apply-btn" onClick={handleApply}>
        {t("ES_COMMON_APPLY", "Apply Filters")}
      </button>
    </div>
  );
};

export default FilterActionPlan;

