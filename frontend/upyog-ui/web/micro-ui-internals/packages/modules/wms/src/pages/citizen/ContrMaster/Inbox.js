import React, { useState, useCallback ,  useEffect  } from "react";
import { useTranslation } from "react-i18next";
import { format, isValid } from "date-fns";
import { Header ,LinkButton,Loader, Toast } from "@egovernments/digit-ui-react-components";
import DesktopInbox from "./../../../components/CmList/inbox/DesktopInbox";
import axios from 'axios';
import { Link } from "react-router-dom";

const Inbox = ({ tenants, parentRoute }) => {
  const { t } = useTranslation()
  Digit.SessionStorage.set("ENGAGEMENT_TENANTS", tenants);
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const cmGetTableData = Digit?.Hooks?.wms?.cm?.useWmsCMGet(tenantId);
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
  console.log("data ",data)
  console.log("dataBack ",dataBack)

const {isLoading,isSuccess } = cmGetTableData;
  // Using useEffect to call the API once mounted and set the data
//   useEffect(() => {
//     (async () => {
//       const result = await axios("https://62f0e3e5e2bca93cd23f2ada.mockapi.io/birth");
//       setData(result.data);
//       setDataBack(result.data)
//       console.log("gooo" ,result.data);
//     })();
//   }, []);

//  const { isLoading: is_Loading, isError: vendorCreateError, data: updateResponse, error: updateError, mutate }  = Digit?.Hooks?.wms?.cm?.useWmsCMSearch();
 const {  isError: vendorCreateError, data: updateResponse, error: updateError, mutate }  = Digit?.Hooks?.wms?.cm?.useWmsCMSearch();
useEffect(() => {
}, [searchParams]);

useEffect(() => {
    if (cmGetTableData.data){
        setData(cmGetTableData.data);
        setDataBack(cmGetTableData.data)
    } 
  }, [cmGetTableData.data]);

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
    //   ,
    //   {
    //     label: t("Email"),
    //     name: "mail"
    //   }
    ]
  }

  const links = [
    {
      text: t("Create Contractor Master"),
      link: "/digit-ui/citizen/wms/birth",
    }
  ]

  function filterTableList(PFMSVendorCode,vendorName){
    // console.log("PFMSVendorCode,vendorName ",{PFMSVendorCode,vendorName})
    let formData = "";
    // let outPut;
    if(vendorName!=null && PFMSVendorCode==null || vendorName!="" && PFMSVendorCode=="" || vendorName!=undefined && PFMSVendorCode==undefined){
      // alert("three")
     formData = `?vendorName=${vendorName}`
     return formData;
  }else if(PFMSVendorCode!=null && vendorName==null || PFMSVendorCode!="" && vendorName=="" || PFMSVendorCode!=undefined && vendorName==undefined){
  // alert("two")
   formData = `?PFMSVendorCode=${PFMSVendorCode}`
   return formData;
  }else if(PFMSVendorCode!="" && vendorName!="" || PFMSVendorCode!=null && vendorName!=null || PFMSVendorCode!=undefined && vendorName!=undefined){
    // alert("one")
     formData = `?PFMSVendorCode=${PFMSVendorCode}&vendorName=${vendorName}`
     return formData;
  }else{
  alert("Please fill any field")
}
  }

    
  const onSearch = async(params) => {
    console.log("params ",params)
    if(params==="reset" || (params?.PFMSVendorCode===null && params?.vendorName===null)){
      setData(dataBack)
      return false
    }
    // console.log("params PFMSVendorCode ",params?.PFMSVendorCode)
//     let formData = "";
//     if(params.vendorName!=null && params.PFMSVendorCode==null || params.vendorName!="" && params.PFMSVendorCode=="" || params.vendorName!=undefined && params.PFMSVendorCode==undefined){
//       alert("three")
//      formData = `?vendorName=${params.vendorName}`
//      await mutate(formData);   
//   }else if(params.PFMSVendorCode!=null && params.vendorName==null || params.PFMSVendorCode!="" && params.vendorName=="" || params.PFMSVendorCode!=undefined && params.vendorName==undefined){
//   alert("two")
//    formData = `?PFMSVendorCode=${params.PFMSVendorCode}`
//    await mutate(formData);
//   }else if(params.PFMSVendorCode!="" && params.vendorName!="" || params.PFMSVendorCode!=null && params.vendorName!=null || params.PFMSVendorCode!=undefined && params.vendorName!=undefined){
//     alert("one")
//      formData = `?PFMSVendorCode=${params?.PFMSVendorCode}&vendorName=${params?.vendorName}`
//      await mutate(formData);   
//   }else{
//   alert("Please fill any field")
// }
    //  await mutate();   
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

    return false;
    // console.log("params ",params)

    // return false
    console.log("params ",params)

    let updatedParams = { ...params };
    console.log("params updatedParams",updatedParams)

    if (!params?.ulb) {
    console.log("params ulb ",updatedParams)

      updatedParams = { ...params, ulb: { code: tenantId } }
    console.log("params ulb updatedParams second ",updatedParams)

    }
    setSearchParams({ ...searchParams, ...updatedParams });
    // console.log("params ulb searchParams ",searchParams)
    // console.log("params ulb data ",data)
    // console.log("params ulb data ",{"updatedParams?.babyFirstName!=''":updatedParams?.babyFirstName!='',"updatedParams?.babyLastName==''":updatedParams?.babyLastName==""})
    // console.log("params ulb babyFirstName ",{"updatedParams?.babyFirstName===''":updatedParams?.babyFirstName==='',"updatedParams?.babyLastName!=''":updatedParams?.babyLastName!=""})
    setData([])

if(updatedParams?.babyFirstName!="" && updatedParams?.babyLastName==""){
    const SearchData=dataBack?.filter((item)=>item?.babyFirstName?.toLowerCase().includes(updatedParams?.babyFirstName?.toLowerCase()))
    console.log("params ulb SearchData ",SearchData)
    setData(SearchData)
}else if(updatedParams?.babyFirstName=="" && updatedParams?.babyLastName!=""){
    const SearchData=dataBack?.filter((item)=>item?.babyLastName?.toLowerCase().includes(updatedParams?.babyLastName?.toLowerCase()))
    console.log("params ulb SearchData ",SearchData)
    setData(SearchData)
  }else{
    const FirstNameIndex=dataBack?.findIndex((item)=> item?.babyFirstName?.toLowerCase()===updatedParams?.babyFirstName?.toLowerCase())
    const LastNameIndex=dataBack?.findIndex((item)=> item?.babyLastName?.toLowerCase()===updatedParams?.babyLastName?.toLowerCase())
    console.log("FirstNameIndex ",{FirstNameIndex,LastNameIndex})
    if(FirstNameIndex === LastNameIndex){   
    const SearchData=dataBack?.filter((item)=>item?.babyFirstName?.toLowerCase().includes(updatedParams?.babyFirstName?.toLowerCase()) && item?.babyLastName?.toLowerCase().includes(updatedParams?.babyLastName?.toLowerCase()))
        setData(SearchData)
        }
  }
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

  // if (isLoading) {
  //   return (
  //     <Loader />
  //   );
  // } 
  return (
    <div>
      <Header>
        {t("Contractor Master")}
      </Header>
      <Link
                  to={`add`}
                >
                  {/* <LinkButton style={{ textAlign: "left" }} label={t("PT_VIEW_MORE_DETAILS")} /> */}
                  <LinkButton style={{display:"block", textAlign: "right" }} label={t("Add Contractor Master")} />
                </Link>
      
      <p>{}</p> 
      <DesktopInbox
        t={t}
        data={data}
        links={links}
        parentRoute={parentRoute}
        searchParams={searchParams}
        onSearch={onSearch}
        globalSearch={globalSearch}
        searchFields={getSearchFields()}
        onFilterChange={handleFilterChange}
        pageSizeLimit={pageSize}
        totalRecords={data?.totalCount}
        title={"Contractor Master"}
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

export default Inbox;