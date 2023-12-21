  import React from "react";
  import { Link } from "react-router-dom";

  const GetCell = (value) => <span className="cell-text">{value}</span>;
  //console.log("cell value", GetCell.value)

  const GetSlaCell = (value) => {
    if (isNaN(value)) return <span className="sla-cell-success">0</span>;
    return value < 0 ? <span className="sla-cell-error">{value}</span> : <span className="sla-cell-success">{value}</span>;
  };

  const GetMobCell = (value) => <span className="sla-cell">{value}</span>;

  export const TableConfig = (t) => ({
    PTR: {
      // searchColumns: (props) => [
      //   {
      //     Header: t("PTR"),
      //     // accessor: "searchData.propertyId",
      //     disableSortBy: true,
      //     Cell: ({ row }) => {
      //       return (
      //         <div>
      //           <span className="link">
      //             <Link to={`${props.parentRoute}/pet-details/` + row.original?.searchData?.["applicationNumber"]}>
      //               {row.original?.searchData?.["applicationNumber"]}
      //             </Link>
      //           </span>
      //         </div>
      //       );
      //     },
      //     mobileCell: (original) => GetMobCell(original?.searchData?.["applicationNumber"]),
      //   },
      //   {
      //     Header: t("ES_INBOX_OWNER_NAME"),
      //     disableSortBy: true,
      //     Cell: ({ row }) => {
      //       const owners = row.original?.searchData["owners"] || [];
      //       const status = row.original?.searchData.status;
      //       const creationReason = row.original?.searchData.creationReason;
      //       const sortedOwners = [...owners.filter((a, b) => a.status === "ACTIVE"), ...owners.filter((a, b) => a.status !== "ACTIVE")];
      //       const _owner = status === "INWORKFLOW" && creationReason === "MUTATION" ? sortedOwners.reverse() : sortedOwners;

      //       return GetCell(`${_owner?.[0].name}`);
      //     },
      //     mobileCell: (original) => GetMobCell(original?.searchData?.["owners"]?.[0].name),
      //   },
      //   {
      //     Header: t("ES_INBOX_LOCALITY"),
      //     Cell: ({ row }) => {
      //       const tenantId = row.original?.searchData?.tenantId;
      //       return GetCell(t(`${tenantId.replace(".", "_").toUpperCase()}_REVENUE_${row.original?.searchData?.address?.locality?.code}`));
      //     },
      //     disableSortBy: true,
      //     mobileCell: (original) => GetMobCell(original?.searchData?.address?.locality?.name),
      //   },
      //   {
      //     Header: t("ES_SEARCH_PROPERTY_STATUS"),
      //     Cell: ({ row }) => {
      //       return GetCell(t(row.original?.searchData?.status));
      //     },
      //     disableSortBy: true,
      //     mobileCell: (original) => GetMobCell(original?.searchData?.status),
      //   },
      //   {
      //     Header: t("ES_SEARCH_TAX_DUE"),
      //     Cell: ({ row }) => {
      //       return GetCell("₹ " + row.original?.searchData?.due_tax);
      //     },
      //     disableSortBy: true,
      //     mobileCell: (original) => GetMobCell("₹ " + original?.searchData?.due_tax),
      //   },
      //   {
      //     Header: t("ES_SEARCH_ACTION"),
      //     Cell: ({ row }) => {
      //       return (
      //         <div>
      //           {row.original?.searchData?.due_tax > 0 && Digit.Utils.didEmployeeHasRole("PT_CEMP") ? (
      //             <span className="link">
      //               <Link to={`/digit-ui/employee/payment/collect/PT/` + row.original?.searchData?.["propertyId"]}>{t("ES_PT_COLLECT_TAX")}</Link>
      //             </span>
      //           ) : null}
      //         </div>
      //       );
      //     },
      //     disableSortBy: true,
      //     // mobileCell: (original) => GetMobCell(original?.searchData?.["propertyId"]),
      //   },
      // ],
      
      inboxColumns: (props) => [
      
        {
          Header: t("PTR_APPLICATION_NUMBER"),
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
            console.log("row",row)
            const wf = row.original?.workflowData;
            return GetCell(t(`${row?.original?.workflowData?.state?.["state"]}`));


          },
          mobileCell: (original) => GetMobCell(t(`ES_PTR_COMMON_STATUS_${original?.workflowData?.state?.["state"]}`)),
        

        },
        
      ],
      serviceRequestIdKey: (original) => original?.[t("PTR_INBOX_UNIQUE_APPLICATION_NUMBER")]?.props?.children,

      
    },
  });
