import { Card, Loader } from "@nudmcdgnpm/digit-ui-react-components";
import React, { useState } from "react";
import { useTranslation } from "react-i18next";
import { Link } from "react-router-dom";
import { format } from "date-fns";
import { getActionButton } from "../utils";
import InboxLinks from "./inbox/InboxLink";
import SearchApplication from "./inbox/search";
import InboxFilter from "./inbox/NewInboxFilter";
import ApplicationTable from "./inbox/ApplicationTable";

/**
 * DesktopInbox component:
 * - Displays challan inbox with table, filters, and search
 * - Supports pagination, sorting, and navigation
 */

const DesktopInbox = ({ tableConfig, filterComponent, columns, statutes, ...props }) => {
  const { data } = props;
  const { t } = useTranslation();
  const [FilterComponent, setComp] = useState(() => Digit.ComponentRegistryService?.getComponent(filterComponent));
  const tenantId = Digit.ULBService.getCurrentTenantId();

  // challans, workFlowData

  const GetCell = (value) => <span className="cell-text">{value}</span>;

  const GetSlaCell = (value) => {
    if (isNaN(value)) return <span className="sla-cell-success">0</span>;
    return value < 0 ? <span className="sla-cell-error">{value}</span> : <span className="sla-cell-success">{value}</span>;
  };

  const convertEpochToDate = (dateEpoch) => {
    if (dateEpoch == null || dateEpoch == undefined || dateEpoch == "") {
      return "NA";
    }
    const dateFromApi = new Date(dateEpoch);
    let month = dateFromApi.getMonth() + 1;
    let day = dateFromApi.getDate();
    let year = dateFromApi.getFullYear();
    month = (month > 9 ? "" : "0") + month;
    day = (day > 9 ? "" : "0") + day;
    return `${day}/${month}/${year}`;
  };
  const stringReplaceAll = (str = "", searcher = "", replaceWith = "") => {
    if (searcher == "") return str;
    while (str.includes(searcher)) {
      str = str.replace(searcher, replaceWith);
    }
    return str;
  };
  const GetMobCell = (value) => <span className="sla-cell">{value}</span>;
  const inboxColumns = () => [
    {
      Header: t("UC_CHALLAN_NO"),
      Cell: ({ row }) => {
        return (
          <span className="challan-id-link">
            <Link to={`${props.parentRoute}/application/${row.original?.challanNo}/${tenantId}`}>
              {row.original?.["challanNo"]}
            </Link>
          </span>
        );
      },
      mobileCell: (original) => GetMobCell(original?.["challanNo"]),
    },
    {
      Header: t("UC_COMMON_TABLE_COL_PAYEE_NAME"),
      Cell: ({ row }) => {
        return <span className="challan-cell-payee">{row.original?.["name"] || "—"}</span>;
      },
      mobileCell: (original) => GetMobCell(original?.["name"]),
    },
    {
      Header: t("CHALLAN_OFFENCE_TYPE"),
      Cell: ({ row }) => {
        return <span className="challan-cell-offence">{row.original?.["offenceName"] || "—"}</span>;
      },
      mobileCell: (original) => GetMobCell(original?.["offenceName"]),
    },
    {
      Header: t("UC_COMMON_TOTAL_AMT"),
      Cell: ({ row }) => {
        const total = Number(row.original?.totalAmount) || 0;
        const waiver = Number(row.original?.feeWaiver) || 0;
        const finalAmount = total - waiver;
        return <span className="challan-amount-cell">₹ {finalAmount.toLocaleString("en-IN")}</span>;
      },
      mobileCell: (original) => GetMobCell(original?.["totalAmount"]),
    },
    {
      Header: t("UC_COMMON_TABLE_COL_STATUS"),
      Cell: ({ row }) => {
        const status = row.original?.challanStatus || "";
        const sUpper = status.toUpperCase();
        let badgeModifier = "challan-status-active";
        if (sUpper.includes("PAID")) {
          badgeModifier = "challan-status-paid";
        } else if (sUpper.includes("CANCEL") || sUpper.includes("EXPIRE") || sUpper.includes("REJECT")) {
          badgeModifier = "challan-status-cancelled";
        } else if (sUpper.includes("PENDING") || sUpper.includes("INITIATE")) {
          badgeModifier = "challan-status-pending";
        }
        return <span className={`challan-status-badge ${badgeModifier}`}>{t(status) || status}</span>;
      },
      mobileCell: (original) => GetMobCell(original?.workflowData?.state?.["state"]),
    },
    {
      Header: t("WF_INBOX_HEADER_CREATED_DATE"),
      Cell: ({ row }) => (
        <span className="challan-date-cell">
          {row.original?.date ? format(new Date(row.original?.date), "dd/MM/yyyy") : "—"}
        </span>
      ),
    },
  ];

  let result;
  if (data?.length === 0) {
    result = (
      <Card className="challan-empty-card">
        <div className="challan-empty-state">
          <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="#94a3b8" strokeWidth="1.75" strokeLinecap="round" strokeLinejoin="round">
            <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
            <polyline points="14 2 14 8 20 8"></polyline>
            <line x1="16" y1="13" x2="8" y2="13"></line>
            <line x1="16" y1="17" x2="8" y2="17"></line>
            <polyline points="10 9 9 9 8 9"></polyline>
          </svg>
          <p className="challan-empty-title">{t("CS_MYAPPLICATIONS_NO_APPLICATION")}</p>
          <p className="challan-empty-desc">{t("UC_NO_CHALLANS_MATCH_FILTER") || "No challans match the current filter or search criteria."}</p>
          <Link to="/upyog-ui/employee/challangeneration/generate-challan" className="challan-empty-btn">
            {t("UC_GENERATE_NEW_CHALLAN")}
          </Link>
        </div>
      </Card>
    );
  } else if (data?.length > 0) {
    result = (
      <ApplicationTable
        t={t}
        data={data}
        columns={inboxColumns(data)}
        className="challan-desktop-applicationtable"
        getCellProps={(cellInfo) => {
          return {
            style: {
              minWidth: cellInfo.column.Header === t("ES_INBOX_APPLICATION_NO") ? "240px" : "",
            },
          };
        }}
        onPageSizeChange={props.onPageSizeChange}
        currentPage={props.currentPage}
        onNextPage={props.onNextPage}
        onPrevPage={props.onPrevPage}
        onLastPage={props.onLastPage}
        onFirstPage={props.onFirstPage}
        pageSizeLimit={props.pageSizeLimit}
        onSort={props.onSort}
        disableSort={props.disableSort}
        sortParams={props.sortParams}
        totalRecords={props.totalRecords}
      />
    );
  }

  return (
    <div className="inbox-container challan-inbox-container">
      {!props.isSearch && (
        <div className="filters-container challan-filters-container">
          <InboxLinks parentRoute={props.parentRoute} businessService={props.businessService} />
          <div className="challan-filter-box">
            <InboxFilter
              defaultSearchParams={props.defaultSearchParams}
              onFilterChange={props.onFilterChange}
              searchParams={props.searchParams}
              type="desktop"
              statutes={statutes}
            />
          </div>
        </div>
      )}
      <div className="challan-main-container">
        <SearchApplication
          defaultSearchParams={props.defaultSearchParams}
          onSearch={props.onSearch}
          type="desktop"
          searchFields={props.searchFields}
          isInboxPage={!props?.isSearch}
          searchParams={props.searchParams}
        />
        <div className="challan-table-result">
          {result}
        </div>
      </div>
    </div>
  );
};

export default DesktopInbox;
