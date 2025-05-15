  import React from "react";
  import { Link } from "react-router-dom";

  // applies a style to the inputs of cells
  const GetCell = (value) => <span className="cell-text">{value}</span>;
  const GetMobCell = (value) => <span className="sla-cell">{value}</span>;

/**
 * TableConfig component renders the table columns in inbox of construction & demolition employee side
 * @returns Returns columns for the inbox table
 */
  export const TableConfig = (t) => ({
    CND: {
      inboxColumns: (props) => [
        {
          Header: t("CND_APPLICATION_NUMBER"),
          Cell: ({ row }) => {
            return (
              <div>
                <span className="link">
                  <Link to={`${props.parentRoute}/application-details/` + `${row?.original?.searchData?.["applicationNumber"]}`}>
                    {row.original?.searchData?.["applicationNumber"]}
                  </Link>
                </span>
              </div>
            );
          },
          mobileCell: (original) => GetMobCell(original?.searchData?.["applicationNumber"]),
        },
        {
          Header: t("COMMON_APPLICANT_NAME"),
          Cell: ({ row }) => {
            return GetCell(t(`${row?.original?.searchData?.applicantDetail?.["nameOfApplicant"]}`));
          },
          mobileCell: (original) => GetMobCell(t(`${original?.searchData?.applicantDetail?.["nameOfApplicant"]}`)),
        },
        {
          Header: t("COMMON_MOBILE_NUMBER"),
          Cell: ({ row }) => {
            return GetCell(t(`${row?.original?.searchData?.["applicantMobileNumber"]}`));
          },
          mobileCell: (original) => GetMobCell(t(`${original?.searchData?.applicantDetail?.["mobileNumber"]}`)),
        },
        {
          Header: t("LOCALITY"),
          Cell: ({ row }) => {
            return GetCell(t(`${row?.original?.searchData?.addressDetail?.["locality"]}`));
          },
          mobileCell: (original) => GetMobCell(t(`${original?.searchData?.addressDetail?.["locality"]}`)),
        },
        {
          Header: t("CND_SCHEDULE_PICKUP"),
          Cell: ({ row }) => {
            return GetCell(t(`${row?.original?.searchData?.["requestedPickupDate"]}`));
          },
          mobileCell: (original) => GetMobCell(t(`${original?.searchData?.["requestedPickupDate"]}`)),
        },
        {
          Header: t("CND_APPLICATION_STATUS"),
          Cell: ({ row }) => {
            return GetCell(t(`${row?.original?.workflowData?.state?.["applicationStatus"]}`));
          },
          mobileCell: (original) => GetMobCell(t(`${original?.workflowData?.state?.["applicationStatus"]}`)),
        },
        
      ],
      serviceRequestIdKey: (original) => original?.[t("CND_INBOX_UNIQUE_APPLICATION_NUMBER")]?.props?.children,

      
    },
  });
