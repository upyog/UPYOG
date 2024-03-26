import React, { useState, useCallback ,  useEffect  } from "react";
import { useTranslation } from "react-i18next";
import { format, isValid } from "date-fns";
import { Header ,LinkButton,Loader, Toast } from "@egovernments/digit-ui-react-components";
// import DesktopInbox from "../../../../../components/CmList/AccountHeadList/DesktopList";
import DesktopInbox from "../../../../../components/List/TE/Department/DesktopList";
import axios from 'axios';
import { Link } from "react-router-dom";

const View = ({ tenants, parentRoute }) => {
  const { t } = useTranslation()
  Digit.SessionStorage.set("ENGAGEMENT_TENANTS", tenants);
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const departmentList = Digit?.Hooks?.wms?.te?.useWMSTEMaster(tenantId, "WMS_DEPARTMENT_ALL_RECORD") || {};
console.log("departmentList ",departmentList)

//   Digit.Hooks.hrms.useHRMSCreate(tenantId)
  const [pageSize, setPageSize] = useState(10);
  const [pageOffset, setPageOffset] = useState(0);
  const [searchParams, setSearchParams] = useState({
    eventStatus: [],
    range: {
      startDate: null,
      endDate: new Date(""),
      title: ""
    },
    ulb: tenants
    // ulb: tenants?.find(tenant => tenant?.code === tenantId)
  });
  // const [searchParams, setSearchParams] = useState()

  let isMobile = window.Digit.Utils.browser.isMobile();
  const [data, setData] = useState([]);
  const [dataBack, setDataBack] = useState();
  const [isTrue, setisTrue] = useState(false);

const {isLoading,isSuccess } = departmentList;

//  const { isLoading: is_Loading, isError: vendorCreateError, data: updateResponse, error: updateError, mutate }  = Digit?.Hooks?.wms?.cm?.useWmsCMSearch();
 const {  isError: vendorCreateError, data: updateResponse, error: updateError, mutate }  = Digit?.Hooks?.wms?.cm?.useWmsCMSearch();
//  const dataDummy =[
//   {"vendor_id":1,"vendor_class_name":"EY Noida","vendor_class_status":"Active"},
//   {"vendor_id":2,"vendor_class_name":"EY Airocity","vendor_class_status":"Inactive"},
// ]
useEffect(() => {
  setisTrue(true)
  departmentList.refetch()
}, []);

useEffect(() => {
    if (departmentList.data){
        setData(departmentList.data);
        setDataBack(departmentList.data)
    } 
  }, [departmentList.data,isTrue]);

  useEffect(() => {
    setData(updateResponse)   
  }, [updateResponse]);
  
  const getSearchFields = () => {
    return [
    //   {
    //     label: t("EVENTS_ULB_LABEL"),
    //     name: "ulb",
    //     type: "ulb",
    //   },
      {
        label: t("PFMS Vendor ID"),
        name: "PFMSVendorCode"
      },
      {
        label: t("Vendor Name"),
        name: "vendorName"
      }
      ,
      {
        label: t("Email"),
        name: "mail"
      }
    ]
  }

  const links = [
    {
      text: t("Create Contractor Master"),
      link: "/digit-ui/citizen/wms/birth",
    }
  ]

  function filterTableList(PFMSVendorCode,vendorName){
    let formData = "";
    if(vendorName!=null && PFMSVendorCode==null || vendorName!="" && PFMSVendorCode=="" || vendorName!=undefined && PFMSVendorCode==undefined){
     formData = `?vendorName=${vendorName}`
     return formData;
  }else if(PFMSVendorCode!=null && vendorName==null || PFMSVendorCode!="" && vendorName=="" || PFMSVendorCode!=undefined && vendorName==undefined){
   formData = `?PFMSVendorCode=${PFMSVendorCode}`
   return formData;
  }else if(PFMSVendorCode!="" && vendorName!="" || PFMSVendorCode!=null && vendorName!=null || PFMSVendorCode!=undefined && vendorName!=undefined){
     formData = `?PFMSVendorCode=${PFMSVendorCode}&vendorName=${vendorName}`
     return formData;
  }else{
  alert("Please fill any field")
}
  }

    
  const onSearch = async(params) => {
    if(params==="reset" || (params?.PFMSVendorCode===null && params?.vendorName===null)){
      setData(dataBack)
      return false
    }
         const filterData = filterTableList(params?.PFMSVendorCode,params?.vendorName)
     await mutate(filterData);
// mutate(formData, {
//       onError: (error, variables) => {
//         setShowToast({ key: "error", action: error });
//         setTimeout(closeToast, 5000);
//       },
//       onSuccess: (params, variables) => {
//         setShowToast({ key: "success", action: "ADD_VEHICLE" });
//         setTimeout(closeToast, 5000);
//         queryClient.invalidateQueries("FSM_VEICLES_SEARCH");
//         setTimeout(() => {
//           closeToast();
//           history.push(`/upyog-ui/employee/fsm/registry`);
//         }, 5000);
//       },
//     });

    // cmSearchTablerecords()
    // setSearchParams(`?PFMSVendorCode=${params?.PFMSVendorCode}&vendorName=${params?.vendorName}`);
    // console.log("cmSearchTablerecords second ",cmSearchTablerecords)

   
}

  const handleFilterChange = (data) => {
    setSearchParams({ ...searchParams, ...data })
  }

  const globalSearch = (rows, columnIds) => {
    // return rows;
    return rows?.filter(row =>
     
      (searchParams?.babyLastName ? row.original?.babyLastName?.toUpperCase().startsWith(searchParams?.babyLastName.toUpperCase()) : true) 
     ) }

  const fetchNextPage = useCallback(() => {
    setPageOffset((prevPageOffSet) => ((parseInt(prevPageOffSet) + parseInt(pageSize))));
  }, [pageSize])

  const fetchPrevPage = useCallback(() => {
    setPageOffset((prevPageOffSet) => ((parseInt(prevPageOffSet) - parseInt(pageSize))));
  }, [pageSize])

  const handlePageSizeChange = (e) => {
    setPageSize(Number(e.target.value));
    // setPageSize((prevPageSize) => (e.target.value));
  };

  // if(is_Loading){
  //   alert("ddddddddddd")
  // }

  if (isLoading) {
    return (
      <Loader />
    );
  } 
  return (
    <div>
      <Header>
        {t("Department List")}
      </Header>
      <Link
                  to={`add`}
                >
                  {/* <LinkButton style={{ textAlign: "left" }} label={t("PT_VIEW_MORE_DETAILS")} /> */}
                  <LinkButton style={{display:"block", textAlign: "right" }} label={t("Add")} />
                </Link>
      
      <p>{}</p> 
      <DesktopInbox
        t={t}
        data={data}
        // data={dataDummy}
        links={links}
        parentRoute={parentRoute}
        searchParams={searchParams}
        onSearch={onSearch}
        globalSearch={globalSearch}
        searchFields={getSearchFields()}
        onFilterChange={handleFilterChange}
        pageSizeLimit={pageSize}
        totalRecords={data?.totalCount}
        title={"Department List"}
        iconName={"calender"}
        currentPage={parseInt(pageOffset / pageSize)}
        onNextPage={fetchNextPage}
        onPrevPage={fetchPrevPage}
        onPageSizeChange={handlePageSizeChange}
        isLoading={isLoading}
        tenantId = {tenantId}
      />
    </div>
  );
}

export default View;