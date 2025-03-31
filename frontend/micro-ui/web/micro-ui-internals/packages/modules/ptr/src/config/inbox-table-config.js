  /**
 * @file TableConfig.js
 * @description 
 * This file defines the table configuration for displaying pet registration (PTR) application details in the inbox view.
 * It defines column configurations for both desktop and mobile views.
 * 
 * @dependencies
 * - `React` for component rendering.
 * - `Link` from `react-router-dom` for navigation to the application details page.
 * 
 * @components
 * - `GetCell`: Renders a standard cell with the provided value.
 * - `GetSlaCell`: Renders a cell with SLA (Service Level Agreement) values, applying different styles for positive, negative, and invalid values.
 * - `GetMobCell`: Renders a cell for mobile view with the provided value.
 * 
 * @function `TableConfig`
 * - Generates the table configuration based on the provided translation function (`t`).
 * - Defines columns for:
 *   - Application Number (with a clickable link to details page).
 *   - Applicant Name.
 *   - Pet Type.
 *   - Breed Type.
 *   - Application Status (translated using the `t` function).
 * - Supports both desktop and mobile cell rendering.
 * 
 * @props
 * - `props`: Contains `parentRoute` for building the link to the application details page.
 * - `row`: Contains data for each table row, including:
 *   - `searchData`: Holds pet and applicant details.
 *   - `workflowData`: Contains application status.
 * 
 * @usage
 * - Used to display the pet registration inbox table with customizable column configurations.
 * 
 * @example
 * const tableConfig = TableConfig(t);
 * const columns = tableConfig.PTR.inboxColumns({ parentRoute: "/dashboard" });
 */

  import React from "react";
  import { Link } from "react-router-dom";

  const GetCell = (value) => <span className="cell-text">{value}</span>;
  

  const GetSlaCell = (value) => {
    if (isNaN(value)) return <span className="sla-cell-success">0</span>;
    return value < 0 ? <span className="sla-cell-error">{value}</span> : <span className="sla-cell-success">{value}</span>;
  };

  const GetMobCell = (value) => <span className="sla-cell">{value}</span>;

  export const TableConfig = (t) => ({
    PTR: {
      
      
      inboxColumns: (props) => [
      
        {
          Header: t("PTR_APPLICATION_NUMBER"),
          Cell: ({ row }) => {
            return (
              <div>
                <span className="link">
                  
                  <Link to={`${props.parentRoute}/petservice/application-details/` + `${row?.original?.searchData?.["applicationNumber"]}`}>

                    {row.original?.searchData?.["applicationNumber"]}
                  </Link>
                </span>
              </div>
            );
          },
          mobileCell: (original) => GetMobCell(original?.searchData?.["applicationNumber"]),
        },
        
        {
          Header: t("PTR_APPLICANT_NAME"),
          Cell: ( row ) => {
          
            return GetCell(`${row?.cell?.row?.original?.searchData?.["applicantName"]}`)
            
          },
          mobileCell: (original) => GetMobCell(original?.searchData?.["applicantName"]),
          
        },
        {
          Header: t("PTR_PET_TYPE"),
          Cell: ({ row }) => {
            return GetCell(`${row.original?.searchData?.petDetails?.["petType"]}`);
           
          },
          mobileCell: (original) => GetMobCell(original?.searchData?.petDetails?.["petType"]),
        },

        {
          Header: t("PTR_BREED_TYPE"),
          Cell: ({ row }) => {
            return GetCell(`${row.original?.searchData?.petDetails?.["breedType"]}`);
          },
          mobileCell: (original) => GetMobCell(original?.searchData?.petDetails?.["breedType"]),
        },

        
        {
          Header: t("PTR_STATUS"),
          Cell: ({ row }) => {
            
            const wf = row.original?.workflowData;
            return GetCell(t(`${row?.original?.workflowData?.state?.["applicationStatus"]}`));


          },
          mobileCell: (original) => GetMobCell(t(`ES_PTR_COMMON_STATUS_${original?.workflowData?.state?.["applicationStatus"]}`)),
        

        },
        
      ],
      serviceRequestIdKey: (original) => original?.[t("PTR_INBOX_UNIQUE_APPLICATION_NUMBER")]?.props?.children,

      
    },
  });
