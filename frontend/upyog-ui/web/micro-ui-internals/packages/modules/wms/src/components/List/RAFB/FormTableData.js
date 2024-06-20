import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import ApplicationTable from "./ApplicationTable";
import { Card, CheckBox, Loader } from "@egovernments/digit-ui-react-components";
import InboxLinks from "./ApplicationLinks";
import SearchApplication from "./searchFormData";
import { Link } from "react-router-dom";

const FormTableData = ({ tableConfig, filterComponent, setSelectData, ...props }) => {
  const { t } = useTranslation();
  const [FilterComponent, setComp] = useState(() => Digit.ComponentRegistryService?.getComponent(filterComponent));

  // const tenantIds = Digit.SessionStorage.get("HRMS_TENANTS");
  const tenantIds = Digit.SessionStorage.get("Digit.ENGAGEMENT_TENANTS");

  const GetCell = (value) => <span className="cell-text">{value}</span>;
  const GetSlaCell = (value) => {
    return value == "INACTIVE" ? <span className="sla-cell-error">{value || ""}</span> : <span className="sla-cell-success">{value || ""}</span>;
  };
  // const data = props?.data?.WMSContractAgreementApplications;
  const data = props?.data;
  console.log("data sssssssss ", data);
  // debugger
  let selectedRowData = [];
  // let getFilterData =[];
  // const [selectedRowData, setselectedRowData]=useState([])
  const onToggle = (rowData) => {
    console.log("rowData ", { rowData });
    let flag = 0;
    selectedRowData.forEach((itendata) => {
      if (JSON.stringify(itendata) === JSON.stringify(rowData)) {
        selectedRowData = selectedRowData.filter((item) => item.uid !== rowData.uid ?  item.uid : false);
        flag = 1;
      }
    });
    if (flag == 0) {
      selectedRowData.push(rowData.uid);
    }
    flag = 0;
    setSelectData(selectedRowData)
  };
  console.log("rowData push in ", selectedRowData);
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
      // {
      //   Header: "#",
      //   id: "checkbox",
      //   width: 30,
      //   Cell: ({ row },rest) => (
      //     <div>
      //       {/* {console.log("rowrowrow ",row)} */}
      //       <CheckBox
      //         key={row.original.uid}
      //         // onToggle={() => onToggle(row.original)}
      //         onClick={() => onToggle(row.original)}
      //         // onChange={() => onToggle(row.original)}
      //         value={row.original.included}
      //         {...row.getToggleRowSelectedProps()}
      //       />
      //     </div>
      //   ),
      // },
      {
        Header: "RA Bill Date",
        disableSortBy: true,
        Cell: ({ row }) => {
          return GetCell(`${Boolean(row.original?.billDate) ? row.original?.billDate : "-"}`);
        },
      },
      {
        Header: "RA Bill No",
        disableSortBy: true,
        Cell: ({ row }) => {
          return GetCell(`${Boolean(row.original?.bilNo) ? row.original?.bilNo : "-"}`);
        },
      },
      {
        Header: "RA Bill Amount",
        disableSortBy: true,
        Cell: ({ row }) => {
          return GetCell(`${Boolean(row.original?.billAmount)?row.original?.billAmount:"-"}`);
        },
      },

      {
        Header: "Tax Amount",
        disableSortBy: true,
        Cell: ({ row }) => {
          return GetCell(`${Boolean(row.original?.deductionAmount)?row.original?.deductionAmount:"-"}`);
        },
      },
      {
        Header: "Remark",
        disableSortBy: true,
        Cell: ({ row }) => {
          return GetCell(`${Boolean(row.original?.remark)?row.original?.remark:"-"}`);
        },
      }
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
        {"No Record Found".split("\\n").map((text, index) => (
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
        showCheckbox={true}
        setSelectData={setSelectData}
        // selectableRows
      />
    );
  }

  return (
    <div className="inbox-container">
      {/* {!props.isSearch && (
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
      )} */}

      {/* <div style={{ flex: 1,width:"100%" }}> */}
      <div style={{ flex: 1 }}>
        {/* <SearchApplication
          t={t}
          defaultSearchParams={props.defaultSearchParams}
          onSearch={props.onSearch}
          type="desktop"
          tenantIds={tenantIds}
          searchFields={props.searchFields}
          isInboxPage={!props?.isSearch}
          searchParams={props.searchParams}
        />   */}

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

export default FormTableData;
