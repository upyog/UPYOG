import React, { useState } from "react";
import { useTranslation } from "react-i18next";
import { Link } from "react-router-dom";
import { Loader } from "@nudmcdgnpm/digit-ui-react-components";
import FilterActionPlan from "./FilterActionPlan";

const formatCurrency = (amt) => {
  if (amt == null || amt === "" || isNaN(amt)) return "—";
  return Number(amt).toLocaleString("en-IN", {
    style: "currency",
    currency: "INR",
    maximumFractionDigits: 2,
  });
};

const getStatusBadge = (status, t) => {
  const s = String(status || "").toUpperCase();
  let badgeClass = "status-pending";
  const label = s ? t(`WF_WORKS_${s}`) : "—";

  if (s === "APPROVED") {
    badgeClass = "status-approved";
  } else if (s.includes("INITIAT")) {
    badgeClass = "status-initiated";
  } else if (s.includes("REJECT")) {
    badgeClass = "status-rejected";
  }

  return (
    <span className={`works-status-badge ${badgeClass}`}>
      <span className="works-status-dot" aria-hidden="true" />
      <span>{label}</span>
    </span>
  );
};

const WorksMobileInbox = ({
  data = [],
  isLoading = false,
  totalCount = 0,
  pageSize = 10,
  pageOffset = 0,
  onNextPage,
  onPrevPage,
  searchParams = {},
  onSearch,
  filters = {},
  onFilterChange,
  statusCounts = {},
  statusMap = [],
  parentRoute = "/works-ui/employee/works",
}) => {
  const { t } = useTranslation();
  const [showFilterModal, setShowFilterModal] = useState(false);
  const [searchInput, setSearchInput] = useState(searchParams.applicationNumber || "");

  const handleSearchSubmit = (e) => {
    e?.preventDefault();
    onSearch({ ...searchParams, applicationNumber: searchInput.trim() || undefined });
  };

  return (
    <div className="works-mobile-inbox">
      {/* Quick Search & Filter Trigger */}
      <form className="works-mobile-search-row" onSubmit={handleSearchSubmit}>
        <input
          type="text"
          className="works-search-input"
          placeholder={t("WORKS_AP_SEARCH_APP_PLACEHOLDER", "Search by Plan No.")}
          value={searchInput}
          onChange={(e) => setSearchInput(e.target.value)}
        />
        <button type="submit" className="works-search-btn">
          {t("ES_COMMON_SEARCH", "Search")}
        </button>
        <button
          type="button"
          className="works-mobile-filter-trigger"
          onClick={() => setShowFilterModal(!showFilterModal)}
        >
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
            <polygon points="22 3 2 3 10 12.46 10 19 14 21 14 12.46 22 3" />
          </svg>
          <span>{t("ES_COMMON_FILTER", "Filter")}</span>
        </button>
      </form>

      {/* Mobile Filter Drawer */}
      {showFilterModal && (
        <div style={{ marginBottom: 12 }}>
          <FilterActionPlan
            filters={filters}
            onFilterChange={(f) => {
              onFilterChange(f);
              setShowFilterModal(false);
            }}
            statusCounts={statusCounts}
            statusMap={statusMap}
          />
        </div>
      )}

      {/* Cards List */}
      {isLoading ? (
        <div style={{ padding: 32, display: "grid", placeItems: "center" }}>
          <Loader />
        </div>
      ) : data.length > 0 ? (
        <div className="works-mobile-card-list">
          {data.map((item) => (
            <div key={item.applicationNumber} className="works-mobile-card">
              <div className="works-mobile-card-head">
                <Link
                  to={`${parentRoute}/application-details/${item.applicationNumber}`}
                  className="works-table-appno-link"
                >
                  {item.applicationNumber}
                </Link>
                {getStatusBadge(item.status, t)}
              </div>

              <div className="works-mobile-card-row">
                <span>{t("WORKS_AP_FINANCIAL_YEAR", "Financial Year")}:</span>
                <strong>{item.financialYear}</strong>
              </div>

              <div className="works-mobile-card-row">
                <span>{t("WORKS_AP_SCHEME", "Scheme")}:</span>
                <strong>{t(item.scheme, item.scheme)}</strong>
              </div>

              <div className="works-mobile-card-row">
                <span>{t("WORKS_AP_APPROVED_AMOUNT", "Approved Amount")}:</span>
                <strong style={{ color: "#0b4b66" }}>{formatCurrency(item.approvedAmount)}</strong>
              </div>

              <div className="works-mobile-card-foot">
                <Link
                  to={`${parentRoute}/application-details/${item.applicationNumber}`}
                  className="works-table-appno-link"
                >
                  {t("ES_COMMON_VIEW_DETAILS", "View Details")} →
                </Link>
              </div>
            </div>
          ))}

          {/* Pagination */}
          {totalCount > pageSize && (
            <div className="works-pagination-bar" style={{ borderRadius: 8 }}>
              <span>
                {pageOffset + 1} - {Math.min(pageOffset + pageSize, totalCount)} of {totalCount}
              </span>
              <div className="works-pagination-nav">
                <button
                  type="button"
                  className="works-pagination-btn"
                  disabled={pageOffset === 0}
                  onClick={onPrevPage}
                >
                  ← {t("CS_COMMON_PREVIOUS", "Prev")}
                </button>
                <button
                  type="button"
                  className="works-pagination-btn"
                  disabled={pageOffset + pageSize >= totalCount}
                  onClick={onNextPage}
                >
                  {t("CS_COMMON_NEXT", "Next")} →
                </button>
              </div>
            </div>
          )}
        </div>
      ) : (
        <div className="works-table-card" style={{ padding: 24 }}>
          <div className="works-table-empty">
            <h3>{t("COMMON_TABLE_NO_RECORD_FOUND", "No Records Found")}</h3>
            <p>{t("WORKS_INBOX_NO_RECORDS_DESC", "Try clearing or adjusting your search criteria and filters.")}</p>
          </div>
        </div>
      )}
    </div>
  );
};

export default WorksMobileInbox;

