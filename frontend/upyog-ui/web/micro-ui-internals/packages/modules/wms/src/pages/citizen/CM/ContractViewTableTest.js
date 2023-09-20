import React, { useEffect, useState } from "react";
import axios from 'axios';
import { LinkButton, Loader, Table } from "@egovernments/digit-ui-react-components";
import { Link } from "react-router-dom";
// import { useTranslation } from "react-i18next";
const ContractViewTableTest = ({filterComponent}) =>{
    // const { t } = useTranslation();
    const tenantId = Digit.ULBService.getCurrentTenantId();    
    // const tenantIds = Digit.SessionStorage.get("Digit.Citizen.tenantId");
    // const [FilterComponent, setComp] = useState(() => Digit.ComponentRegistryService?.getComponent(filterComponent));
    // const inboxInitialState = {
    //   searchParams: {
    //     tenantId: tenantId,
    //   },
    // };
    // const [searchParams, setSearchParams] = useState(() => {
    //   return inboxInitialState.searchParams || {};
    // });
    // const handleFilterChange = (filterParam) => {
    //   // let keys_to_delete = filterParam.delete;
    //   // // let _new = { ...searchParams, ...filterParam };
    //   // if (keys_to_delete) keys_to_delete.forEach((key) => delete _new[key]);
    //   // filterParam.delete;
    //   // delete _new.delete;
    //   // setSearchParams({ ..._new });
    // };
    const [data, setData]=useState()
    const onSubmit=()=>{
      /* use customiseCreateFormData hook to make some chnages to the Employee object */
      Digit.WMSService.ContractorMaster.get(tenantId).then((result,err)=>{
          setData(result) 
      }).catch((e) => {
      console.log("err");
     });
    };

    const filterData=()=>{
      const filterRecords = data?.filter((item)=> item.pfms_vendor_code.includes("ser") && item?.uid_number.includes("sdfs"))
      setData(filterRecords)  
      }
useEffect(()=>{
  onSubmit();
},[])

const columns = React.useMemo(() => {
    return [
      {
        Header: "pfms_vendor_code",
        disableSortBy: true,
        Cell: ({ row }) => {
          return (
            <span className="link">
              <Link  to={`/upyog-ui/employee/hrms/details/${row.original.tenantId}/${row.original.pfms_vendor_code}`}>{row?.original?.pfms_vendor_code}</Link>
            </span>
          );
        },
      },
      {
        Header:"vendor_name",
        disableSortBy: true,
        Cell: ({ row }) => {
          return (`${row?.original?.vendor_name}`);
        //   return GetCell(`${row?.original?.vendor_name}`);
        },
      },
      {
        Header: "Vendor Type",
        Cell: ({ row }) => {
          return (
            <div className="tooltip">
              {" "}
              (`${row.original?.vendor_type?.name}`)
              {/* <span className="tooltiptext" style={{whiteSpace: "nowrap"}}>
                {row.original?.user?.roles.map((ele, index) => (
                  <span>
                    {`${index + 1}. ` + t(`ACCESSCONTROL_ROLES_ROLES_${ele.code}`)} <br />{" "}
                  </span>
                ))}
              </span> */}
            </div>
          );
        },
        disableSortBy: true,
      },
      {
        Header: "Mobile Number",
        disableSortBy: true,
        Cell: ({ row }) => {
            return(`${row.original?.mobile_number}`)

           
        //   return GetCell(
        //     `${
        //       t(
        //         "COMMON_MASTERS_DESIGNATION_" + row.original?.assignments?.sort((a, b) => new Date(a.fromDate) - new Date(b.fromDate))[0]?.designation
        //       ) || ""
        //     }`
        //   );
        },
      },
      {
        Header: "UID Number",
        disableSortBy: true,
        Cell: ({ row }) => {
            return(`${row.original?.uid_number}`)

            
        //   return GetCell(
        //     `${
        //       t(
        //         "COMMON_MASTERS_DEPARTMENT_" + row.original?.assignments?.sort((a, b) => new Date(a.fromDate) - new Date(b.fromDate))[0]?.department
        //       ) || ""
        //     }`
        //   );
        },
      },
      {
        Header: "VAT Number",
        disableSortBy: true,
        Cell: ({ row }) => {
          return (`${row.original?.VATNumber}`);
        //   return GetSlaCell(`${row.original?.isActive ? "ACTIVE" : "INACTIVE"}`);
        },
      },
      {
        Header: "Bank Branch",
        disableSortBy: true,
        Cell: ({ row }) => {
          return (`${row.original?.bank_branch?.name}`);
        //   return GetSlaCell(`${row.original?.isActive ? "ACTIVE" : "INACTIVE"}`);
        },
      },
      {
        Header: "Function",
        disableSortBy: true,
        Cell: ({ row }) => {
          return (`${row.original?.function?.name}`);
        },
      },
      {
        Header: "Vendor Class",
        disableSortBy: true,
        Cell: ({ row }) => {
          return (`${row.original?.vendor_class?.name}`);
        },
      },
      {
        Header: "EPFO Account Number",
        disableSortBy: true,
        Cell: ({ row }) => {
          return (`${row.original?.epfo_account_number}`);
        },
      },
      {
        Header: "Vendor Sub Type",
        disableSortBy: true,
        Cell: ({ row }) => {
          return (`${row.original?.vendor_sub_type?.name}`);
        },
      },
      {
        Header: "Pay To",
        disableSortBy: true,
        Cell: ({ row }) => {
          return (`${row.original?.payto}`);
        },
      },
      {
        Header: "Email",
        disableSortBy: true,
        Cell: ({ row }) => {
          return (`${row.original?.email}`);
        },
      },
      {
        Header: "GST Number",
        disableSortBy: true,
        Cell: ({ row }) => {
          return (`${row.original?.gst_number}`);
        },
      },
      {
        Header: "Pan Number",
        disableSortBy: true,
        Cell: ({ row }) => {
          return (`${row.original?.pan_number}`);
        },
      },
      {
        Header: "Bank Account Number",
        disableSortBy: true,
        Cell: ({ row }) => {
          return (`${row.original?.bank_account_number}`);
        },
      },
      {
        Header: "Primary Account Head",
        disableSortBy: true,
        Cell: ({ row }) => {
          return (`${row.original?.primary_account_head?.name}`);
        },
      },
      {
        Header: "Address",
        disableSortBy: true,
        Cell: ({ row }) => {
          return (`${row.original?.address}`);
        },
      },
      {
        Header: "Status",
        disableSortBy: true,
        Cell: ({ row }) => {
          return (`${row.original?.isActive ? "ACTIVE" : "INACTIVE"}`);
        //   return GetSlaCell(`${row.original?.isActive ? "ACTIVE" : "INACTIVE"}`);
        },
      },
    {
        Header: "Action",
        disableSortBy: true,
        Cell: ({ row }) => {
            return(<span className="link">
              <button type="button" onClick={`${row.original?.id}`}>Edit {row.original?.id}</button>
              <button type="button" onClick={`${row.original?.id}`}>View {row.original?.id}</button>
            </span>)
            // return(`${row.original?.id }`);
        },
      },
    ];
  }, []);
    return (
        <div>
         {!data ? <div style={{"fintSize":"100px"}}>Loading.......................</div> :
        //  {!data?.data?.ResponseInfo?.status==="successful" ? <div style={{"fintSize":"100px"}}>Loading.......................</div> :
         <React.Fragment>
          <button onClick={()=>filterData()}>Click</button>

          {/* <FilterComponent
                defaultSearchParams={inboxInitialState}
                onFilterChange={handleFilterChange}
                searchParams={searchParams}
                type="desktop"
                tenantIds={tenantIds}
              /> */}
              
          {/* <FilterComponent
                defaultSearchParams='dd'
                onFilterChange="onFilterChange"
                searchParams="searchParams"
                type="desktop"
                tenantIds="pg"
              /> */}
          <Table
    // t="Contract Table View"
    data={data}
    columns={columns}
    getCellProps={(cellInfo) => {
        // console.log(cellInfo)
        return {
          style: {
            maxWidth: "150px",
            padding: "20px 18px",
            fontSize: "16px",
            minWidth: "150px",
          },
        };
      }}
    // onNextPage={onNextPage}
    // onPrevPage={onPrevPage}
    // currentPage={currentPage}
    // totalRecords={data.length}
    // onPageSizeChange={onPageSizeChange}
    // pageSizeLimit={pageSizeLimit}
  /> 
         </React.Fragment> 
        }
        </div> 
        )
}
export default ContractViewTableTest