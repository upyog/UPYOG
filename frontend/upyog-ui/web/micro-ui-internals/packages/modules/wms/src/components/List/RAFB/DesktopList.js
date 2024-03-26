import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import ApplicationTable from "./ApplicationTable";
import { Card, Loader } from "@egovernments/digit-ui-react-components";
import InboxLinks from "./ApplicationLinks";
import SearchApplication from "./search";
import { Link } from "react-router-dom";

const DesktopInbox = ({ tableConfig, filterComponent, ...props} ) => {
  const { t } = useTranslation();
  const [FilterComponent, setComp] = useState(() => Digit.ComponentRegistryService?.getComponent(filterComponent));

  // const tenantIds = Digit.SessionStorage.get("HRMS_TENANTS");
  const tenantIds = Digit.SessionStorage.get("Digit.ENGAGEMENT_TENANTS");

  const GetCell = (value) => <span className="cell-text">{value}</span>;
  const GetSlaCell = (value) => {
    return value == "INACTIVE" ? <span className="sla-cell-error">{ value || ""}</span> : <span className="sla-cell-success">{ value || ""}</span>;
  };
  // const data = props?.data?.WMSContractAgreementApplications;
  const data = props?.data;
  console.log("data sssssssss ",data)
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
        Header: "Vendor Type",
        disableSortBy: true,
        Cell: ({ row }) => {
          return GetCell(`${Boolean(row.original?.vendor_type)?row.original?.vendor_type:"-"}`);
        },
      },
      {
        Header: "Vendor Name",
        disableSortBy: true,
        Cell: ({ row }) => {
          return GetCell(`${Boolean(row.original?.vendor_name)?row.original?.vendor_name:"-"}`);
        },
      },
      // {
      //   Header: "Witness Name",
      //   disableSortBy: true,
      //   Cell: ({ row }) => {
      //     return GetCell(`${Boolean(row.original?.witness_name)?row.original?.witness_name:"-"}`);
      //   },
      // },

      // {
      //   Header: "Agreement No",
      //   disableSortBy: true,
      //   Cell: ({ row }) => {
      //     // return <div>{cell?.row?.original?.agreement?.map(item=>GetCell(`${item?.agreement_no}`)).join(', ')}</div>
      //     return GetCell(`${Boolean(row.original?.agreement_no)?row.original?.agreement_no:"-"}`);
      //   },
      // },
      
      // {
      //   Header: "Agreement Date",
      //   disableSortBy: true,
      //   Cell: ({ row }) => {
      //     // return row.original.map(item=>GetCell(`${item?.agreement_date}`))
      //     return GetCell(`${Boolean(row.original?.agreement_date)?row.original?.agreement_date:"-"}`);
      //   },
      // },
      
      // {
      //   Header: "Deposit Type",
      //   disableSortBy: true,
      //   Cell: ({ row }) => {
      //     return GetCell(`${Boolean(row.original?.deposit_type)?row.original?.deposit_type:"-"}`);
      //   },
      // },
      // {
      //   Header: "Valid From Date",
      //   disableSortBy: true,
      //   Cell: ({ row }) => {
      //     return GetCell(`${Boolean(row.original?.valid_from_date)?row.original?.valid_from_date:"-"}`);
      //   },
      // },
      // {
      //   Header: "Valid Till Date",
      //   disableSortBy: true,
      //   Cell: ({ row }) => {
      //     return GetCell(`${Boolean(row.original?.valid_till_date)?row.original?.valid_till_date:"-"}`);
      //   },
      // },
      // {
      //   Header: "Department Name",
      //   disableSortBy: true,
      //   Cell: ({ row }) => {
      //     return GetCell(`${Boolean(row.original?.department_name_party1)?row.original?.department_name_party1:"-"}`);
      //   },
      // },
      // {
      //   Header: "Designation",
      //   disableSortBy: true,
      //   Cell: ({ row }) => {
      //     return GetCell(`${Boolean(row.original?.designation)?row.original?.designation:"-"}`);
      //   },
      // },
      // {
      //   Header: "Terms & Conditions",
      //   disableSortBy: true,
      //   Cell: ({ row }) => {
      //     return GetCell(`${Boolean(row?.original?.terms_and_conditions)? row.original?.terms_and_conditions :"-"}`);
      //   },
      // },
      
      {
        Header: "Status",
        disableSortBy: true,
        Cell: ({ row }) => {
          // return GetCell(`${Boolean(row?.original?.status)? row.original?.status :"-"}`);
          return GetCell(`${Boolean(row?.original?.status)? (row?.original?.status==="draft")? "Draft":(row?.original?.status==="pending")? "Pending" : "Approved" :"-"}`);
        },
      },
      
      {
        Header: "Action",
        disableSortBy: true,
        Cell: ({ row }) => {
          return (
            <React.Fragment>
             <span className="link">
              <Link to={`detail/${props.tenantId}/${row?.original?.uid}`}>View</Link>
            </span> 
            {' '} 
            <span className="link">
            <Link to={`edit/${props.tenantId}/${row?.original?.uid}`}>Edit</Link>
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
        t={t}
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
       {!props.isSearch && (
        <div className="filters-container">
          <InboxLinks
            parentRoute={props.parentRoute}
            title="Running Account"
            icon="account"
            allLinks={[
              {
                // text: "HR_COMMON_CREATE_EMPLOYEE_HEADER",
                text: "Create Running Account",
                link: "/upyog-ui/citizen/wms/running-account/add",
                businessService: "WMS",
                roles: ["WMS_ADMIN"],
              },
            ]}
            headerText={"WMS"}
            businessService={props.businessService}
          />
            <div>
            {
              <FilterComponent
                defaultSearchParams={props.defaultSearchParams}// not in use
                onFilterChange={props.onFilterChange}
                searchParams={props.searchParams}
                type="desktop"
                tenantIds={tenantIds}
              />
            }
          </div>  
        </div>
      )}
    
      {/* <div style={{ flex: 1,width:"100%" }}> */}
      <div style={{ flex: 1 }}>
          <SearchApplication
          t={t}
          defaultSearchParams={props.defaultSearchParams}
          onSearch={props.onSearch}
          type="desktop"
          tenantIds={tenantIds}
          searchFields={props.searchFields}
          isInboxPage={!props?.isSearch}
          searchParams={props.searchParams}
        />  

        {/* <div style={{"justify-content": "space-between","overflow-x":"auto"}}> */}
        <div className="result" style={{ marginLeft: !props?.isSearch ? "24px" : "", flex: 1 }}>
        {/* <div className="result" style={{ marginLeft: true ? "24px" : "" , flex: 1}}> */}
          {result}
        </div>
        </div>
      </div>
    // </div>
  );
};

export default DesktopInbox;
