  import React from "react";
  import { Link } from "react-router-dom";

  // applies a style to the inputs of cells
  const GetCell = (value) => <span className="cell-text">{value}</span>;
  
  // maybe need later
  const GetSlaCell = (value) => {
    if (isNaN(value)) return <span className="sla-cell-success">0</span>;
    return value < 0 ? <span className="sla-cell-error">{value}</span> : <span className="sla-cell-success">{value}</span>;
  };

  const GetMobCell = (value) => <span className="sla-cell">{value}</span>;

/**
 * TableConfig component renders the table columns in inbox of street Vending employee side
 * @returns Returns columns for the inbox table
 */
  export const TableConfig = (t) => ({
    SV: {
      inboxColumns: (props) => [
        {
          Header: t("SV_APPLICATION_NUMBER"),
          Cell: ({ row }) => {
            return (
              <div>
                <span className="link">
                  
                  <Link to={`${props.parentRoute}/application-details/` + `${row?.original?.searchData?.["applicationNo"]}`}>

                    {row.original?.searchData?.["applicationNo"]}
                  </Link>
                </span>
              </div>
            );
          },
          mobileCell: (original) => GetMobCell(original?.searchData?.["applicationNumber"]),
        },

        {
          Header: t("SV_VENDOR_ID"),
          Cell: ( row ) => {
            console.log("data of inbox tableconfig row :: ", row)
            return GetCell(`${row?.cell?.row?.original?.searchData?.vendorDetail[0]?.["id"]}`)
          },
          mobileCell: (original) => GetMobCell(original?.searchData?.["applicantName"]),
        }, 

        {
          Header: t("SV_APPLICANT_NAME"),
          Cell: ( row ) => {
            return GetCell(`${row?.cell?.row?.original?.searchData?.vendorDetail[0]?.["name"]}`)
          },
          mobileCell: (original) => GetMobCell(original?.searchData?.["applicantName"]),
        }, 

        {
          Header: t("SV_VENDING_TYPE"),
          Cell: ( row ) => {
            return GetCell(`${row?.cell?.row?.original?.searchData?.vendorDetail[0]?.["relationshipType"]}`)
          },
          mobileCell: (original) => GetMobCell(original?.searchData?.["applicantName"]),
        },
        
        {
          Header: t("SV_VENDING_ZONE"),
          Cell: ( row ) => {
            return GetCell(`${row?.cell?.row?.original?.searchData?.["vendingZone"]}`)
          },
          mobileCell: (original) => GetMobCell(original?.searchData?.["applicantName"]),
        },
        
        {
          Header: t("SV_STATUS"),
          Cell: ({ row }) => {
            return GetCell(t(`${row?.original?.workflowData?.state?.["applicationStatus"]}`));
          },
          mobileCell: (original) => GetMobCell(t(`ES_SV_COMMON_STATUS_${original?.workflowData?.state?.["applicationStatus"]}`)),
        },
        
      ],
      serviceRequestIdKey: (original) => original?.[t("SV_INBOX_UNIQUE_APPLICATION_NUMBER")]?.props?.children,

      
    },
  });
