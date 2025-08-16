// Importing necessary components and hooks from external libraries
import React from "react";
import { Link } from "react-router-dom"; // Component for navigation links

// Utility function to render a cell with text
const GetCell = (value) => <span className="cell-text">{value}</span>;

// Utility function to render a cell with SLA (Service Level Agreement) status
const GetSlaCell = (value) => {
  if (isNaN(value)) return <span className="sla-cell-success">0</span>; // Render "0" if the value is not a number
  return value < 0 ? <span className="sla-cell-error">{value}</span> : <span className="sla-cell-success">{value}</span>; // Render SLA status based on the value
};

// Utility function to render a cell for mobile view
const GetMobCell = (value) => <span className="sla-cell">{value}</span>;

// Configuration for the inbox table
export const TableConfig = (t) => ({
  EW: {
    // Configuration for the columns in the inbox table
    inboxColumns: (props) => [
      {
        Header: t("EW_REQUEST_ID"), // Header for the "Request ID" column
        Cell: ({ row }) => {
          return (
            <div>
              <span className="link">
                {/* Link to the application details page */}
                <Link to={`${props.parentRoute}/application-details/` + `${row?.original?.searchData?.["requestId"]}`}>
                  {row.original?.searchData?.["requestId"]}
                </Link>
              </span>
            </div>
          );
        },
        mobileCell: (original) => GetMobCell(original?.searchData?.["requestId"]), // Cell for mobile view
      },

      {
        Header: t("EW_APPLICANT_NAME"), // Header for the "Applicant Name" column
        Cell: (row) => {
          return GetCell(`${row?.cell?.row?.original?.searchData?.applicant?.["applicantName"]}`); // Render the applicant's name
        },
        mobileCell: (original) => GetMobCell(original?.["applicantName"]), // Cell for mobile view
      },

      {
        Header: t("EW_OWNER_MOBILE_NUMBER"), // Header for the "Owner Mobile Number" column
        Cell: ({ row }) => {
          return GetCell(`${row.original?.searchData?.applicant?.["mobileNumber"]}`); // Render the owner's mobile number
        },
        mobileCell: (original) => GetMobCell(original?.searchData?.applicant?.["mobileNumber"]), // Cell for mobile view
      },

      {
        Header: t("EW_STATUS"), // Header for the "Status" column
        Cell: ({ row }) => {
          return GetCell(`${row.original?.searchData?.["requestStatus"]}`); // Render the request status
        },
        mobileCell: (original) =>
          GetMobCell(t(`ES_EW_COMMON_STATUS_${original?.workflowData?.state?.["applicationStatus"]}`)), // Cell for mobile view with translated status
      },
    ],

    // Function to extract the unique service request ID from the data
    serviceRequestIdKey: (original) => original?.[t("EW_INBOX_UNIQUE_APPLICATION_NUMBER")]?.props?.children,
  },
});
