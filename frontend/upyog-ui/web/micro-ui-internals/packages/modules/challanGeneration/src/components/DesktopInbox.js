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
          <div>
            <span className="link">
              <Link to={`${props.parentRoute}/application/${row.original?.challanNo}/${tenantId}`}>{row.original?.["challanNo"]}</Link>
            </span>
          </div>
        );
      },
      mobileCell: (original) => GetMobCell(original?.["challanNo"]),
    },
    {
      Header: t("UC_COMMON_TABLE_COL_PAYEE_NAME"),
      Cell: ({ row }) => {
        return GetCell(`${row.original?.["name"]}`);
      },
      mobileCell: (original) => GetMobCell(original?.["name"]),
    },
    {
      Header: t("CHALLAN_OFFENCE_TYPE"),
      Cell: ({ row }) => {
        return GetCell(`${row.original?.["offenceName"]}`);
      },
      mobileCell: (original) => GetMobCell(original?.["offenceName"]),
    },
    {
      Header: t("UC_COMMON_TOTAL_AMT"),
      Cell: ({ row }) => {
        const total = row.original?.totalAmount || 0;
        const waiver = row.original?.feeWaiver || 0;
        const finalAmount = total - waiver;

        return GetCell(finalAmount);
      },
      mobileCell: (original) => GetMobCell(original?.["totalAmount"]),
    },
    {
      Header: t("UC_COMMON_TABLE_COL_STATUS"),
      Cell: ({ row }) => {
        const wf = row.original?.challanStatus;
        return GetCell(t(`${row.original?.challanStatus}`));
      },
      mobileCell: (original) => GetMobCell(original?.workflowData?.state?.["state"]),
    },
    {
      Header: t("WF_INBOX_HEADER_CREATED_DATE"),
      Cell: ({ row }) => (row.original?.date ? GetCell(format(new Date(row.original?.date), "dd/MM/yyyy")) : ""),
    },
  ];

  let result;
    if (data?.length === 0) {
    result = (
      <Card className="cg-card-margin-top">
        {/* TODO Change localization key */}
        {t("CS_MYAPPLICATIONS_NO_APPLICATION")
          .split("\\n")
          .map((text, index) => (
            <p key={index} className="cg-text-center">
              {text}
            </p>
          ))}
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
    <div className="inbox-container cg-inbox-container-overflow">
      {!props.isSearch && (
        <div className="filters-container">
          <InboxLinks parentRoute={props.parentRoute} businessService={props.businessService} />
          <div>
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
      <div className="cg-flex-1">
        <SearchApplication
          defaultSearchParams={props.defaultSearchParams}
          onSearch={props.onSearch}
          type="desktop"
          searchFields={props.searchFields}
          isInboxPage={!props?.isSearch}
          searchParams={props.searchParams}
        />
        <div className={`result ${!props?.isSearch ? "cg-result-margin-left" : ""} cg-flex-1`}>
          {result}
        </div>
      </div>
    </div>
  );
};

export default DesktopInbox;
