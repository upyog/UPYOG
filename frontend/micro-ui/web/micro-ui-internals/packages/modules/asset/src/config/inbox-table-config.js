  import { SubmitBar } from "@upyog/digit-ui-react-components";
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
    ASSET: {
      
      
      inboxColumns: (props) => [
      
        {
          Header: t("ES_ASSET_RESPONSE_CREATE_LABEL"),
          Cell: ({ row }) => {
            return (
              <div>
                <span className="link">
                  
                  <Link to={`${props.parentRoute}/assetservice/application-details/` + `${row?.original?.searchData?.["applicationNo"]}`}>

                    {row.original?.searchData?.["applicationNo"]}
                  </Link>
                </span>
              </div>
            );
          },
          mobileCell: (original) => GetMobCell(original?.searchData?.["applicationNo"]),
        },
        
        {
          Header: t("AST_ASSET_CATEGORY_LABEL"),
          Cell: ( row ) => {          
            return GetCell(`${row?.cell?.row?.original?.searchData?.["assetClassification"]}`)
            
          },
          mobileCell: (original) => GetMobCell(original?.searchData?.["assetClassification"]),
          
        },
        {
          Header: t("AST_PARENT_CATEGORY_LABEL"),
          Cell: ({ row }) => {
            return GetCell(`${row.original?.searchData?.["assetParentCategory"]}`);
           
          },
          mobileCell: (original) => GetMobCell(original?.searchData?.["assetParentCategory"]),
        },

        {
          Header: t("AST_NAME_LABEL"),
          Cell: ({ row }) => {
            return GetCell(`${row.original?.searchData?.["assetName"]}`);
          },
          mobileCell: (original) => GetMobCell(original?.searchData?.["assetName"]),
        },

        
        {
          Header: t("AST_DEPARTMENT_LABEL"),
          Cell: ({ row }) => {
            const wf = row.original?.workflowData;
            return GetCell(t(`${row?.original?.searchData?.["department"]}`));


          },
          mobileCell: (original) => GetMobCell(t(`AST_COMMON_STATUS_${original?.searchData?.["department"]}`)),
        

        },
        {
          Header: t("AST_TRANSFER"),
          Cell: ({ row }) => {
            return (
              <div>
                
                <span className="link">
                
                  <Link to={`${props.parentRoute}/assetservice/application-details/` + `${row?.original?.searchData?.["applicationNo"]}`}>

                    {t('AST_TRANSFER '+`${row?.original?.searchData?.["assetParentCategory"]}`)}
                  </Link>
                  
                </span>
              </div>
            );
          },
          mobileCell: (original) => GetMobCell(original?.searchData?.["applicationNo"]),
        },
        
      ],
      serviceRequestIdKey: (original) => original?.[t("AST_INBOX_UNIQUE_APPLICATION_NUMBER")]?.props?.children,

      
    },
  });
