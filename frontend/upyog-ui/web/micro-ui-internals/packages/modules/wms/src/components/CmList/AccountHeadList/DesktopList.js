import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import ApplicationTable from "./ApplicationTable";
import { Card, Loader } from "@egovernments/digit-ui-react-components";
import InboxLinks from "./ApplicationLinks";
import SearchApplication from "./search";
import { Link } from "react-router-dom";

const DesktopInbox = ({ tableConfig, ...props} ) => {
  const { t } = useTranslation();
  // const tenantIds = Digit.SessionStorage.get("HRMS_TENANTS");
  const tenantIds = Digit.SessionStorage.get("Digit.ENGAGEMENT_TENANTS");

  const GetCell = (value) => <span className="cell-text">{value}</span>;
  const GetSlaCell = (value) => {
    return value == "INACTIVE" ? <span className="sla-cell-error">{ value || ""}</span> : <span className="sla-cell-success">{ value || ""}</span>;
  };
  const data = props?.data?.WMSPrimaryAccountHeadApplications;
  // const data = props?.data;
// console.log("data props ",props )
// console.log("data props data ",props?.data )
// console.log("data ",data)
  // const [FilterComponent, setComp] = useState(() => Digit.ComponentRegistryService?.getComponent(filterComponent));

  const columns = React.useMemo(() => {
    return [
      // {
      //   Header: "Vendor Type",
      //   disableSortBy: true,
      //   Cell: ({ row }) => {
      //     return (
      //       <span className="link">
      //         <Link to={`details/${props.tenantId}/${row.original.name}`}>{row.original.name}</Link>
      //       </span>
      //     );
      //   },
      // },
      {
        Header: "Accounthead Head Name",
        disableSortBy: true,
        Cell: ({ row }) => {
          return GetCell(`${row.original?.primary_accounthead_name}`);
        },
      },
      {
        Header: "Account Head Number",
        disableSortBy: true,
        Cell: ({ row }) => {
          return GetCell(`${row.original?.primary_accounthead_accountno}`);
        },
      },
      {
        Header: "Accounthead Head Location",
        disableSortBy: true,
        Cell: ({ row }) => {
          return GetCell(`${row.original?.primary_accounthead_location}`);
        },
      },
      {
        Header: "Status",
        disableSortBy: true,
        Cell: ({ row }) => {
          
          // return GetCell(`${row.original?.vendor_status}`)
          // return GetCell(`${row.original?.vendor_status==="Active" ? <span style={{"color":"red"}}>{row.original?.vendor_status}</span> : <span style={{"color":"red"}}>{row.original?.vendor_status}</span>}`);

          return GetSlaCell(`${row?.original?.account_status==="Active" ? row?.original?.account_status?.toUpperCase() : row?.original?.account_status?.toUpperCase()}`);
        },
      },
      {
        Header: "Action",
        disableSortBy: true,
        Cell: ({ row }) => {
          return (
            <React.Fragment>
            {/* <span className="link">
              <Link to={`details/${props.tenantId}/${row.original.bank_id}`}>View</Link>
            </span> 
            {' '} */}
            <span className="link">
            <Link to={`edit/${props.tenantId}/${row?.original?.primary_accounthead_id}`}>Edit</Link>
          </span>
          </React.Fragment>
            
            
          )
        },
       },

    ];
  }, []);

  // console.log({"props.currentPage":props.currentPage,
  //   "props.onNextPage":props.onNextPage,
  //   "props.onPrevPage":props.onPrevPage,
  //   "props.pageSizeLimit":props.pageSizeLimit,
  //   "props.onSort":props.onSort,
  //   "props.disableSort":props.disableSort,
  //   "props.onPageSizeChange":props.onPageSizeChange,
  //   "props.sortParams":props.sortParams,
  //   "props.totalRecords":props.totalRecords})
  let result;
  if (props.isLoading) {
    result = <Loader />;
  } else if (data?.length === 0) {
    result = (
      <Card style={{ marginTop: 20 }}>
        {/* TODO Change localization key */}
        {/* {"COMMON_TABLE_NO_RECORD_FOUND" */}
        {"No Record Found"
        
          .split("\\n")
          .map((text, index) => (
            <p key={index} style={{ textAlign: "center" }}>
              {text}
            </p>
          ))}
      </Card>
    );
  } else if (data?.length > 0) {
    result = (
      <ApplicationTable
        // t={t}
        data={data}
        columns={columns}
        getCellProps={(cellInfo) => {
          return {
            style: {
              maxWidth: "150px",
              padding: "20px 18px",
              fontSize: "16px",
              minWidth: "150px",
            },
          };
        }}
        currentPage={props.currentPage}
        onNextPage={props.onNextPage}
        onPrevPage={props.onPrevPage}
        pageSizeLimit={props.pageSizeLimit}
        onSort={props.onSort}
        disableSort={props.disableSort}
        onPageSizeChange={props.onPageSizeChange}
        sortParams={props.sortParams}
        totalRecords={props.totalRecords}
      />
    );
  }

  return (
    <div className="inbox-container" >
      {/* {!props.isSearch && ( 
        <div className="filters-container">
          <InboxLinks
            parentRoute={props.parentRoute}
            allLinks={[
              {
                text: "HR_COMMON_CREATE_EMPLOYEE_HEADER",
                link: "/upyog-ui/employee/hrms/create",
                businessService: "hrms",
                roles: ["HRMS_ADMIN"],
              },
            ]}
            headerText={"WMS"}
            businessService={props.businessService}
          />
          {/* <div>
            {
              <FilterComponent
                defaultSearchParams={props.defaultSearchParams}
                onFilterChange={props.onFilterChange}
                searchParams={props.searchParams}
                type="desktop"
                tenantIds={tenantIds}
              />
            }
          </div> /}
        </div>
      )}
    */}
      <div style={{ flex: 1,width:"100%" }}>
        {/* <SearchApplication
          defaultSearchParams={props.defaultSearchParams}
          onSearch={props.onSearch}
          type="desktop"
          tenantIds={tenantIds}
          searchFields={props.searchFields}
          isInboxPage={!props?.isSearch}
          searchParams={props.searchParams}
        /> */}
        {/* <div style={{"justify-content": "space-between","overflow-x":"auto"}}> */}
        <div className="result" style={{ marginLeft: !props?.isSearch ? "0px" : "" }}>
          {result}
        </div>
        {/* </div> */}
      </div>
    </div>
  );
};

export default DesktopInbox;
