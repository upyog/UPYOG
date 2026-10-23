import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { format } from "date-fns";
import { getActionButton, printReciept } from "../utils";
import { Link } from "react-router-dom";

/**
 * MobileInbox component:
 * - Displays challan inbox in mobile-friendly card view
 * - Supports search, filter, and sorting
 */

const MobileInbox = ({
  data,
  defaultSearchParams = {},
  isLoading,
  isSearch,
  searchFields,
  onFilterChange,
  onSearch,
  onSort,
  parentRoute,
  searchParams,
  sortParams,
  linkPrefix,
  tableConfig,
  filterComponent,
}) => {
  const { t } = useTranslation();
  const GetMobCell = (value) => <span className="sla-cell">{value}</span>;
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
  const inboxColumns = (props) => [
    {
      Header: t("UC_CHALLAN_NO"),
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
      mobileCell: (original) => GetMobCell(t(`${original?.challanStatus}`)),
    },
    {
      Header: t("WF_INBOX_HEADER_CREATED_DATE"),
      Cell: ({ row }) => (row.original?.date ? GetCell(format(new Date(row.original?.date), "dd/MM/yyyy")) : ""),
      mobileCell: (original) => (original?.date ? format(new Date(original?.date), "dd/MM/yyyy") : ""),
    },
  ];

  const serviceRequestIdKey = (original) => original?.[t("UC_CHALLAN_NUMBER")]?.props?.children;

  const getData = () => {
    return data?.map((dataObj) => {
      const obj = {};
      const columns = inboxColumns();
      columns.forEach((el) => {
        if (el.mobileCell) obj[el.Header] = el.mobileCell(dataObj);
      });
      return obj;
    });
  };

  return (
    <div className="cg-padding-0">
      <div className="inbox-container">
        <div className="filters-container">
          <ApplicationCard
            t={t}
            data={getData()}
            defaultSearchParams={defaultSearchParams}
            onFilterChange={onFilterChange}
            isLoading={isLoading}
            isSearch={isSearch}
            onSearch={onSearch}
            onSort={onSort}
            searchParams={searchParams}
            searchFields={searchFields}
            linkPrefix={linkPrefix}
            sortParams={sortParams}
            serviceRequestIdKey={serviceRequestIdKey}
            filterComponent={filterComponent}
          />
        </div>
      </div>
    </div>
  );
};

export default MobileInbox;
