import React from "react";
import { Link } from "react-router-dom";

const GetCell = (value) => <span className="cell-text">{value}</span>;

const GetSlaCell = (value) => {
  if (isNaN(value)) return <span className="sla-cell-success">0</span>;
  return value < 0 ? <span className="sla-cell-error">{value}</span> : <span className="sla-cell-success">{value}</span>;
};

const GetMobCell = (value) => <span className="sla-cell">{value}</span>;

export const TableConfig = (t) => ({
  EW: {
    inboxColumns: (props) => [
      {
        Header: t("EW_REQUEST_ID"),
        Cell: ({ row }) => {
          return (
            <div>
              <span className="link">
                <Link to={`${props.parentRoute}/application-details/` + `${row?.original?.searchData?.["requestId"]}`}>
                  {row.original?.searchData?.["requestId"]}
                </Link>
              </span>
            </div>
          );
        },

        mobileCell: (original) => GetMobCell(original?.searchData?.["requestId"]),
      },

      {
        Header: t("EW_APPLICANT_NAME"),
        Cell: (row) => {
          return GetCell(`${row?.cell?.row?.original?.searchData?.applicant?.["applicantName"]}`);
        },
        mobileCell: (original) => GetMobCell(original?.["applicantName"]),
      },
      {
        Header: t("EW_OWNER_MOBILE_NUMBER"),
        Cell: ({ row }) => {
          return GetCell(`${row.original?.searchData?.applicant?.["mobileNumber"]}`);
        },
        mobileCell: (original) => GetMobCell(original?.searchData?.applicant?.["mobileNumber"]),
      },

      {
        Header: t("EW_STATUS"),
        Cell: ({ row }) => {
          return GetCell(`${row.original?.searchData?.["requestStatus"]}`);
        },
        mobileCell: (original) => GetMobCell(t(`ES_EW_COMMON_STATUS_${original?.workflowData?.state?.["applicationStatus"]}`)),
      },
    ],
    serviceRequestIdKey: (original) => original?.[t("EW_INBOX_UNIQUE_APPLICATION_NUMBER")]?.props?.children,
  },
});
