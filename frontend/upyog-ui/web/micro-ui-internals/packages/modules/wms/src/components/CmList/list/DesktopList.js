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
  const data = props?.data?.WMSContractorApplications;
  // const data = props?.data;
console.log("data ",data)
  // const [FilterComponent, setComp] = useState(() => Digit.ComponentRegistryService?.getComponent(filterComponent));

  const columns = React.useMemo(() => {
    return [
      {
        Header: "PFMS Vendor ID",
        disableSortBy: true,
        Cell: ({ row }) => {
          return (
            <span className="link">
              <Link to={`details/${props.tenantId}/${row.original.vendor_id}`}>{row.original.pfms_vendor_code}</Link>
            </span>
          );
        },
      },
      {
        Header: "Vendor Type",
        disableSortBy: true,
        Cell: ({ row }) => {
          return GetCell(`${row.original?.vendor_type}`);
        },
      },
      {
        Header: "Vendor Name",
        Cell: ({ row }) => {
          
          return GetCell(`${row.original?.vendor_name}`);
          // return (
            // <div className="tooltip">
            //   {" "}
            //   {GetCell(`${row.original?.user?.roles.length}`)}
            //   <span className="tooltiptext" style={{whiteSpace: "nowrap"}}>
            //     {row.original?.user?.roles.map((ele, index) => (
            //       <span>
            //         {`${index + 1}. ` + `ACCESSCONTROL_ROLES_ROLES_${ele.code}`} <br />{" "}
            //       </span>
            //     ))}
            //   </span>
            // </div>
          // );
        },
        disableSortBy: true,
      },
      {
        Header: "Mobile Number",
        disableSortBy: true,
        Cell: ({ row }) => {
          
          return GetCell(`${row.original?.mobile_number}`);

          // return GetCell(
          //   `${
              
          //       "COMMON_MASTERS_DESIGNATION_" + row.original?.assignments?.sort((a, b) => new Date(a.fromDate) - new Date(b.fromDate))[0]?.designation
          //      || ""
          //   }`
          // );
        },
      },
      {
        Header: "UID Number",
        disableSortBy: true,
        Cell: ({ row }) => {
          return GetCell(`${row.original?.uid_number}`);

          // return GetCell(
          //   `${
              
          //       "COMMON_MASTERS_DEPARTMENT_" + row.original?.assignments?.sort((a, b) => new Date(a.fromDate) - new Date(b.fromDate))[0]?.department
          //     || ""
          //   }`
          // );
        },
      },
      // {
      //   Header: "VAT Number",
      //   disableSortBy: true,
      //   Cell: ({ row }) => {
      //     return GetCell(`${row.original?.vat_number}`);},
      // },
      // {
      //   Header: "Bank, Branch & IFSC Code",
      //   disableSortBy: true,
      //   Cell: ({ row }) => {
      //     return GetCell(`${row.original?.bank_branch_ifsc_code}`);},
      // },
      // {
      //   Header: "Function",
      //   disableSortBy: true,
      //   Cell: ({ row }) => {
      //     return GetCell(`${row.original?.function}`);},
      // },
      // {
      //   Header: "Vendor Class",
      //   disableSortBy: true,
      //   Cell: ({ row }) => {
      //     return GetCell(`${row.original?.vendor_class}`);},
      // },
      // {
      //   Header: "PF Account Number",
      //   disableSortBy: true,
      //   Cell: ({ row }) => {
      //     return GetCell(`${row.original?.epfo_account_number}`);},
      // },
      // {
      //   Header: "Sub Type",
      //   disableSortBy: true,
      //   Cell: ({ row }) => {
      //     return GetCell(`${row.original?.vendor_sub_type}`);},
      // },
      // {
      //   Header: "Pay To",
      //   disableSortBy: true,
      //   Cell: ({ row }) => {
      //     return GetCell(`${row.original?.payto}`);},
      // },
      // {
      //   Header: "Email Id",
      //   disableSortBy: true,
      //   Cell: ({ row }) => {
      //     return GetCell(`${row.original?.email}`);},
      // },
      // {
      //   Header: "GST Number",
      //   disableSortBy: true,
      //   Cell: ({ row }) => {
      //     return GetCell(`${row.original?.gst_number}`);},
      // },
      // {
      //   Header: "PAN Number",
      //   disableSortBy: true,
      //   Cell: ({ row }) => {
      //     return GetCell(`${row.original?.pan_number}`);},
      // },
      // {
      //   Header: "Bank Account Number",
      //   disableSortBy: true,
      //   Cell: ({ row }) => {
      //     return GetCell(`${row.original?.bank_account_number}`);},
      // },
      // {
      //   Header: "Primary Account Head",
      //   disableSortBy: true,
      //   Cell: ({ row }) => {
      //     return GetCell(`${row.original?.primary_account_head}`);},
      // },
      // {
      //   Header: "Address",
      //   disableSortBy: true,
      //   Cell: ({ row }) => {
      //     return GetCell(`${row.original?.address}`);},
      // },

      // {
      //   Header: "Vendor Status",
      //   disableSortBy: true,
      //   Cell: ({ row }) => {
          
      //     // return GetCell(`${row.original?.vendor_status}`)
      //     // return GetCell(`${row.original?.vendor_status==="Active" ? <span style={{"color":"red"}}>{row.original?.vendor_status}</span> : <span style={{"color":"red"}}>{row.original?.vendor_status}</span>}`);

      //     return GetSlaCell(`${row.original?.vendor_status==="Active" ? row.original?.vendor_status.toUpperCase() : row.original?.vendor_status.toUpperCase()}`);
      //   },
      // },
      // {
      //   Header: "Allow Direct Payment",
      //   disableSortBy: true,
      //   Cell: ({ row }) => {
      //     return GetCell(`${row.original?.allow_direct_payment}`)
      //   },
      // },
      // {
      //   Header: "Action",
      //   disableSortBy: true,
      //   Cell: ({ row }) => {
      //     return (
      //       <React.Fragment>
      //       <span className="link">
      //         <Link to={`details/${props.tenantId}/${row.original.vendor_id}`}>View</Link>
      //       </span> 
      //       {' '}
      //       <span className="link">
      //       <Link to={`edit/${props.tenantId}/${row.original.vendor_id}`}>Edit</Link>
      //     </span>
      //     </React.Fragment>
            
            
      //     )
      //   },
      //  },

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
  // alert(props.isLoading)

  // if (data?.length === undefined) {
  //   return (
  //     <Loader />
  //   );
  // } // alert(data?.length)
  
  let result;
  if (props.isLoading) {
    result = <Loader />;
  } else if (props.isLoading===false && data?.length === undefined){
    result = <Loader />
  } 
  else if (data?.length === 0) {
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
        <div className="result" style={{ marginLeft: !props?.isSearch ? "0px" : "" }}>
          {result}
        </div>
        {/* </div> */}
      </div>
    </div>
  );
};

export default DesktopInbox;
