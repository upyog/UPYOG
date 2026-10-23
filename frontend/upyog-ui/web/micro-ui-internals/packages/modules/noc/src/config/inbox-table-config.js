/**
 * @file TableConfig.js
 * @description Table column config for FIRENOC inbox (desktop + mobile).
 * Perf notes: Cell renderers are memoized (React.memo) since they're pure
 * value->markup components re-rendered per row. Repeated deep-path lookups
 * (owner, fireNOCDetails) are deduped via small accessor helpers instead of
 * being re-walked in every column's Cell/mobileCell.
 */

import React from "react";
import { Link } from "react-router-dom";

// ---- Cell renderers (memoized: pure, value-in -> markup-out) ----
const GetCell = React.memo(({ value }) => <span className="cell-text">{value}</span>);

const GetSlaCell = React.memo(({ value }) => {
  if (isNaN(value)) return <span className="sla-cell-success">0</span>;
  return (
    <span className={value < 0 ? "sla-cell-error" : "sla-cell-success"}>{value}</span>
  );
});

const GetMobCell = React.memo(({ value }) => <span className="sla-cell">{value}</span>);

// ---- Shared accessors (avoid re-walking the same deep path twice per row) ----
const getFireNOCDetails = (searchData) => searchData?.fireNOCDetails;
const getOwner = (searchData) => getFireNOCDetails(searchData)?.applicantDetails?.owners?.[0];
const getBuilding = (searchData) => getFireNOCDetails(searchData)?.buildings?.[0];

export const TableConfig = (t) => ({
  FIRENOC: {
    inboxColumns: (props) => [
      {
        Header: t("FN_APPLICATION_NUMBER_LABEL"),
        Cell: ({ row }) => {
          const applicationNumber = row?.original?.searchData?.applicationNumber;
          return (
            <div>
              <span className="link">
                <Link to={`${props.parentRoute}/firenoc/application-overview/${applicationNumber}`}>
                  {applicationNumber}
                </Link>
              </span>
            </div>
          );
        },
        mobileCell: (original) => (
          <GetMobCell value={original?.searchData?.applicationNumber} />
        ),
      },
      {
        Header: t("FN_APPLICANT_NAME"),
        Cell: ({ row }) => <GetCell value={getOwner(row?.original?.searchData)?.name} />,
        mobileCell: (original) => (
          <GetMobCell value={getOwner(original?.searchData)?.name} />
        ),
      },
      {
        Header: t("FN_MOBILE_NUMBER"),
        Cell: ({ row }) => <GetCell value={getOwner(row?.original?.searchData)?.mobileNumber} />,
        mobileCell: (original) => (
          <GetMobCell value={getOwner(original?.searchData)?.mobileNumber} />
        ),
      },
      {
        Header: t("FN_BUILDING_USAGE_TYPE"),
        Cell: ({ row }) => (
          <GetCell value={t(getBuilding(row?.original?.searchData)?.usageType)} />
        ),
        mobileCell: (original) => (
          <GetMobCell value={t(getBuilding(original?.searchData)?.usageType)} />
        ),
      },
      {
        Header: t("FN_NOC_TYPE"),
        Cell: ({ row }) => (
          <GetCell value={t(getFireNOCDetails(row?.original?.searchData)?.fireNOCType)} />
        ),
        mobileCell: (original) => (
          <GetMobCell value={getFireNOCDetails(original?.searchData)?.fireNOCType} />
        ),
      },
      {
        Header: t("FN_STATUS"),
        Cell: ({ row }) => (
          <GetCell value={t(row?.original?.workflowData?.state?.applicationStatus)} />
        ),
        mobileCell: (original) => (
          <GetMobCell
            value={t(`${original?.workflowData?.state?.applicationStatus}`)}
          />
        ),
      },
    ],
    serviceRequestIdKey: (original) =>
      original?.[t("FIRENOC_INBOX_UNIQUE_APPLICATION_NUMBER")]?.props?.children,
  },
});