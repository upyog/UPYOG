import React from "react";
import { useTranslation } from "react-i18next";
import { Link } from "react-router-dom";
import { Loader } from "@nudmcdgnpm/digit-ui-react-components";

const formatCurrency = (amt) => {
  if (amt == null || amt === "" || isNaN(amt)) return "—";
  return Number(amt).toLocaleString("en-IN", {
    style: "currency",
    currency: "INR",
    maximumFractionDigits: 2,
  });
};

const formatDate = (epoch) => {
  if (!epoch) return "—";
  const d = new Date(epoch);
  return d.toLocaleDateString("en-IN", {
    day: "2-digit",
    month: "short",
    year: "numeric",
  });
};

const getStatusBadge = (status, t) => {
  const s = String(status || "").toUpperCase();
  let badgeClass = "status-pending";
  let label = t(`WORKS_STATUS_${s}`, s);

  if (s.includes("APPROV")) {
    badgeClass = "status-approved";
    label = t("WORKS_STATUS_APPROVED", "Approved");
  } else if (s.includes("INITIAT")) {
    badgeClass = "status-initiated";
    label = t("WORKS_STATUS_INITIATED", "Initiated");
  } else if (s.includes("REJECT")) {
    badgeClass = "status-rejected";
    label = t("WORKS_STATUS_REJECTED", "Rejected");
  } else if (s.includes("VERIF")) {
    badgeClass = "status-pending";
    label = t("WORKS_STATUS_PENDING_FOR_VERIFICATION", "Pending Verification");
  }

  return (
    <span className={`works-status-badge ${badgeClass}`}>
      <span className="works-status-dot" aria-hidden="true" />
      <span>{label}</span>
    </span>
  );
};

const getSlaBadge = (slaDays, t) => {
  if (slaDays == null) return "—";
  const days = Number(slaDays);
  let slaClass = "sla-ok";
  if (days < 0) slaClass = "sla-breached";
  else if (days <= 5) slaClass = "sla-warn";

  return (
    <span className={`works-sla-badge ${slaClass}`}>
      {days < 0
        ? `${Math.abs(days)} ${t("DAYS_OVERDUE", "days overdue")}`
        : `${days} ${t("DAYS_REMAINING", "days left")}`}
    </span>
  );
};

const WorksTable = ({
  data = [],
  isLoading = false,
  totalCount = 0,
  pageSize = 10,
  pageOffset = 0,
  onPageSizeChange,
  onNextPage,
  onPrevPage,
  parentRoute = "/works-ui/employee/works",
}) => {
  const { t } = useTranslation();

  if (isLoading) {
    return (
      <div className="works-table-card">
        <div style={{ padding: 40, display: "grid", placeItems: "center" }}>
          <Loader />
        </div>
      </div>
    );
  }

  const startRecord = totalCount > 0 ? pageOffset + 1 : 0;
  const endRecord = Math.min(pageOffset + pageSize, totalCount);

  return (
    <div className="works-table-card">
      <div className="works-table-meta-bar">
        <div>
          <span>{t("WORKS_INBOX_SHOWING", "Showing")}: </span>
          <span className="works-table-count">
            {startRecord} - {endRecord}
          </span>
          <span> {t("WORKS_INBOX_OF", "of")} </span>
          <span className="works-table-count">{totalCount}</span>
          <span> {t("WORKS_INBOX_ACTION_PLANS", "Action Plans")}</span>
        </div>

        <div>
          <label htmlFor="works-page-size-select">
            {t("WORKS_INBOX_PER_PAGE", "Rows per page")}:
          </label>
          <select
            id="works-page-size-select"
            className="works-pagination-size-select"
            value={pageSize}
            onChange={(e) => onPageSizeChange(Number(e.target.value))}
          >
            <option value={10}>10</option>
            <option value={20}>20</option>
            <option value={50}>50</option>
          </select>
        </div>
      </div>

      <div className="works-table-scroll">
        <table className="works-table">
          <thead>
            <tr>
              <th>{t("WORKS_AP_APPLICATION_NO", "Action Plan No.")}</th>
              <th>{t("WORKS_AP_FINANCIAL_YEAR", "Financial Year")}</th>
              <th>{t("WORKS_AP_SCHEME", "Scheme")}</th>
              <th>{t("WORKS_AP_ULB", "ULB")}</th>
              <th>{t("WORKS_AP_TOTAL_PROJECTS", "Projects")}</th>
              <th style={{ textAlign: "right" }}>{t("WORKS_AP_APPROVED_AMOUNT", "Approved Amount")}</th>
              <th>{t("WORKS_INBOX_STATUS", "Status")}</th>
              <th>{t("WORKS_INBOX_SLA", "SLA")}</th>
              <th>{t("WORKS_INBOX_SUBMITTED_ON", "Date")}</th>
            </tr>
          </thead>
          <tbody>
            {data.length > 0 ? (
              data.map((item) => (
                <tr key={item.applicationNumber}>
                  <td>
                    <Link
                      to={`${parentRoute}/application-details/${item.applicationNumber}`}
                      className="works-table-appno-link"
                    >
                      {item.applicationNumber}
                    </Link>
                  </td>
                  <td>{item.financialYear}</td>
                  <td>
                    <div>{t(item.scheme, item.scheme)}</div>
                    {item.subScheme && (
                      <small style={{ color: "#667085" }}>{t(item.subScheme, item.subScheme)}</small>
                    )}
                  </td>
                  <td>{item.ulb?.split(".").pop()?.toUpperCase() || item.ulb}</td>
                  <td>
                    <span style={{ fontWeight: 600 }}>{item.projectsCount || 1}</span>
                    <small style={{ color: "#667085" }}> ({item.componentsCount || 1} items)</small>
                  </td>
                  <td style={{ textAlign: "right" }} className="works-amount-cell">
                    {formatCurrency(item.approvedAmount)}
                  </td>
                  <td>{getStatusBadge(item.status, t)}</td>
                  <td>{getSlaBadge(item.slaDays, t)}</td>
                  <td>{formatDate(item.createdTime)}</td>
                </tr>
              ))
            ) : (
              <tr>
                <td colSpan={9}>
                  <div className="works-table-empty">
                    <div className="works-table-empty-icon">
                      <svg width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5">
                        <circle cx="11" cy="11" r="8" />
                        <line x1="21" y1="21" x2="16.65" y2="16.65" />
                      </svg>
                    </div>
                    <h3>{t("COMMON_TABLE_NO_RECORD_FOUND", "No Records Found")}</h3>
                    <p>{t("WORKS_INBOX_NO_RECORDS_DESC", "Try clearing or adjusting your search criteria and filters.")}</p>
                  </div>
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>

      {totalCount > pageSize && (
        <div className="works-pagination-bar">
          <div>
            <span>
              {t("PAGE", "Page")} {Math.floor(pageOffset / pageSize) + 1} {t("OF", "of")}{" "}
              {Math.ceil(totalCount / pageSize)}
            </span>
          </div>

          <div className="works-pagination-nav">
            <button
              type="button"
              className="works-pagination-btn"
              disabled={pageOffset === 0}
              onClick={onPrevPage}
            >
              ← {t("CS_COMMON_PREVIOUS", "Previous")}
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
  );
};

export default WorksTable;

