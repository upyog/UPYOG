import React, { useState, useMemo } from "react";
import { useTranslation } from "react-i18next";

const DEFAULT_STATUS_OPTIONS = [
  { code: "PENDING_FOR_VERIFICATION", label: "WF_WORKS_PENDING_FOR_VERIFICATION" },
  { code: "PENDING_FOR_APPROVAL", label: "WF_WORKS_PENDING_FOR_APPROVAL" },
  { code: "APPROVED", label: "WF_WORKS_APPROVED" },
  { code: "INITIATED", label: "WF_WORKS_INITIATED" },
  { code: "REJECTED", label: "WF_WORKS_REJECTED" },
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

const FilterActionPlan = ({ filters = {}, onFilterChange, statusCounts = {}, statusMap = [] }) => {
  const { t } = useTranslation();

  const [assignee, setAssignee] = useState(filters.assignee || "ASSIGNED_TO_ALL");
  const [selectedStatuses, setSelectedStatuses] = useState(
    Array.isArray(filters.status) ? filters.status : filters.status ? [filters.status] : []
  );
  const [selectedScheme, setSelectedScheme] = useState(filters.scheme || "");
  const [selectedFY, setSelectedFY] = useState(filters.financialYear || "");

  // Use live statusMap from /inbox/v1/_search when available, else fallback defaults
  const statusOptions = useMemo(() => {
    if (Array.isArray(statusMap) && statusMap.length > 0) {
      return statusMap
        .filter((s) => s?.applicationstatus || s?.stateid)
        .map((s) => {
          const statusKey = s.applicationstatus || s.state || s.stateid;
          return {
            code: s.stateid || statusKey,
            statusKey,
            label: `WF_WORKS_${statusKey}`,
            count: Number(s.count) || 0,
          };
        });
    }
    return DEFAULT_STATUS_OPTIONS.map((item) => ({
      ...item,
      statusKey: item.code,
      count: statusCounts[item.code] ?? 0,
    }));
  }, [statusMap, statusCounts]);

  const handleStatusToggle = (code) => {
    setSelectedStatuses((prev) =>
      prev.includes(code) ? prev.filter((s) => s !== code) : [...prev, code]
    );
  };

  const handleAssigneeChange = (val) => {
    setAssignee(val);
    onFilterChange({
      ...filters,
      assignee: val === "ASSIGNED_TO_ME" ? "ASSIGNED_TO_ME" : undefined,
      status: selectedStatuses.length > 0 ? selectedStatuses : undefined,
      scheme: selectedScheme || undefined,
      financialYear: selectedFY || undefined,
    });
  };

  const handleApply = (e) => {
    e?.preventDefault();
    onFilterChange({
      assignee: assignee === "ASSIGNED_TO_ME" ? "ASSIGNED_TO_ME" : undefined,
      status: selectedStatuses.length > 0 ? selectedStatuses : undefined,
      scheme: selectedScheme || undefined,
      financialYear: selectedFY || undefined,
    });
  };

  const handleClear = () => {
    setAssignee("ASSIGNED_TO_ALL");
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

      {/* Assigned To Filter (matches PT Inbox) */}
      <div className="works-filter-section">
        <span className="works-filter-label">{t("ES_INBOX_ASSIGNED_TO", "Assigned To")}</span>
        <div style={{ display: "flex", gap: "8px", marginTop: "6px" }}>
          {[
            { code: "ASSIGNED_TO_ALL", label: t("ES_INBOX_ASSIGNED_TO_ALL", "All") },
            { code: "ASSIGNED_TO_ME", label: t("ES_INBOX_ASSIGNED_TO_ME", "Assigned to me") },
          ].map((opt) => {
            const active = assignee === opt.code;
            return (
              <button
                key={opt.code}
                type="button"
                onClick={() => handleAssigneeChange(opt.code)}
                style={{
                  flex: 1,
                  padding: "7px 10px",
                  borderRadius: "6px",
                  border: active ? "1.5px solid #584f74" : "1px solid #d0d5dd",
                  background: active ? "#f0e9f7" : "#ffffff",
                  color: active ? "#584f74" : "#344054",
                  fontWeight: active ? 600 : 500,
                  fontSize: "12.5px",
                  cursor: "pointer",
                }}
              >
                {opt.label}
              </button>
            );
          })}
        </div>
      </div>

      {/* Status Filter */}
      <div className="works-filter-section">
        <span className="works-filter-label">{t("WORKS_INBOX_STATUS", "Status")}</span>
        <div className="works-filter-checkboxes">
          {statusOptions.map((item) => {
            const isChecked = selectedStatuses.includes(item.code);
            return (
              <label key={item.code} className="works-filter-check-item">
                <div style={{ display: "flex", alignItems: "center" }}>
                  <input
                    type="checkbox"
                    checked={isChecked}
                    onChange={() => handleStatusToggle(item.code)}
                  />
                  <span>{t(item.label)}</span>
                </div>
                {item.count > 0 && <span className="works-filter-count-badge">{item.count}</span>}
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
