  import React from "react";
  import { Link } from "react-router-dom";

  // applies a style to the inputs of cells
  const GetCell = (value) => <span className="cell-text">{value}</span>;
  const GetMobCell = (value) => <span className="sla-cell">{value}</span>;

/**
 * TableConfig component renders the table columns in inbox
 * @returns Returns columns for the inbox table
 */
  export const TableConfig = (t) => ({
    PGRAI: {
      inboxColumns: (props) => [
        {
          Header: t("CS_COMMON_COMPLAINT_NO"),
          Cell: ({ row }) => {
            return (
              <div>
                <span className="link">
                  
                  <Link to={"/upyog-ui/employee/pgrai/complaint-details/" + `${row.original?.searchData?.service?.["serviceRequestId"]}`}>
                    {row.original?.searchData?.service?.["serviceRequestId"]}
                  </Link>
                </span>
              </div>
            );
          },
        },
        {
          Header: t("WF_INBOX_HEADER_LOCALITY"),
          Cell: ( row ) => {
            return GetCell(t(Digit.Utils.locale.getLocalityCode(row?.row.original?.searchData?.service?.address?.["locality"], row?.row.original?.searchData?.service?.["tenantId"])));
          },
        }, 
        {
          Header: t("CS_COMPLAINT_DETAILS_CURRENT_STATUS"),
          Cell: ( row ) => {
            return GetCell(`${row?.row.original?.searchData?.service?.["applicationStatus"]}`)
          },
        },
        {
          Header: t("CS_ADDCOMPLAINT_COMPLAINT_SUB_TYPE"),
          Cell: ( row ) => {
            return GetCell(t(`SERVICEDEFS.${(row?.row.original?.searchData?.service?.["serviceCode"].toUpperCase())}`))
          },
        }
      ],
      serviceRequestIdKey: (original) => original?.[t("CS_COMMON_COMPLAINT_NO")]?.props?.children,

      
    },
  });
