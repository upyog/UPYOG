import React, { useState, useCallback, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { format, isValid } from "date-fns";
import { Header, LinkButton, Loader, Toast } from "@egovernments/digit-ui-react-components";
import DesktopInbox from "../../../../components/List/RAFB/DesktopList";
import axios from "axios";
import { Link } from "react-router-dom";

const WmsRAFBList = ({ tenants, parentRoute, filterComponent }) => {
  const { t } = useTranslation();
  Digit.SessionStorage.set("ENGAGEMENT_TENANTS", tenants);
  const tenantId = Digit.ULBService.getCurrentTenantId();
  //   const bankList = Digit?.Hooks?.wms?.cm?.useWMSMaster(tenantId,"WMS_BANK_BRANCH_TYPE","_view");
  const bankList = Digit?.Hooks?.wms?.ca?.useWmsCAGet(tenantId, "AllData") || {};
  // alert("ddddd")
  //   const { data: citizenTypes, isLoading } = Digit?.Hooks?.wms?.cm?.useWMSMaster(tenantId, "WMS_V_TYPE_LIST") || {};
  console.log("contact agrrement bankList ", bankList);
  //   Digit.Hooks.hrms.useHRMSCreate(tenantId)
  const [pageSize, setPageSize] = useState(10);
  const [pageOffset, setPageOffset] = useState(0);
  const [searchParams, setSearchParams] = useState({
    eventStatus: [],
    range: {
      startDate: null,
      endDate: new Date(""),
      title: "",
    },
    ulb: tenants,
    // ulb: tenants?.find(tenant => tenant?.code === tenantId)
  });
  // const [searchParams, setSearchParams] = useState()
  // debugger;
  let isMobile = window.Digit.Utils.browser.isMobile();
  const [data, setData] = useState([]);
  const [dataBack, setDataBack] = useState([]);
  const [isTrue, setisTrue] = useState(false);

  const { isLoading, isSuccess } = bankList;

  //  const { isLoading: is_Loading, isError: vendorCreateError, data: updateResponse, error: updateError, mutate }  = Digit?.Hooks?.wms?.cm?.useWmsCMSearch();
  const { isError: vendorCreateError, data: updateResponse, error: updateError, mutate } = Digit?.Hooks?.wms?.te?.useWmsTESearch(tenantId);

  useEffect(() => {
    setisTrue(true);
    bankList.refetch();
    localStorage.removeItem("resId");
  }, []);

  const [sdata1, setSdata1] = useState();
  useEffect(() => {
    const sdata = [];
    if (bankList.data) {
      bankList.data.forEach((res) => {
        console.log("sdata1 res ", res);
        sdata.push({
          ...data,
          uid: res?.id,
          status: res?.status,
          ...res?.WMSContractAgreementApplication[0]?.contractors[0],
          ...res?.WMSContractAgreementApplication[0]?.party2_witness[0],
        });
        // setData([{...data,"uid":res?.id,...res.contractors[0],...res.party2_witness[0],...res.agreement[0],...res.sDPGBGDetails[0],...res.party1Details[0],...res.termsAndConditions[0]}]);
      });
      console.log("bankList.data agreement sdata", sdata);

      setData(sdata);
      setDataBack(sdata);

      // setData(bankList.data);
      // setDataBack(bankList);
    }
  }, [bankList.data, isTrue]);

  console.log("sdata1 data ", data);

  useEffect(() => {
    setData(updateResponse);
  }, [updateResponse]);

  const getSearchFields = () => {
    return [
      // {
      //   label: t("EVENTS_ULB_LABEL"),
      //   name: "ulb",
      //   type: "ulb",
      // },
      {
        label: t("Vendor Type"),
        name: "vendor_type",
      },
      {
        label: t("Vendor Name"),
        name: "vendor_name",
      },
      // {
      //   label: t("Status"),
      //   name: "vendor_status",
      // },
      // {
      //   label: t("Witness Name"),
      //   name: "witness_name",
      //   // isMendatory:true
      // },
      // {
      //   label: t("From Date"),
      //   name: "from_Date",
      //   type: "date"
      // },
      //   ,
      //   {
      //     label: t("Pre-bid Meeting Date"),
      //     name: "mail"
      //   }
    ];
  };

  const links = [
    {
      text: t("Create Contractor Master"),
      link: "/digit-ui/citizen/wms/birth",
    },
  ];

  // useEffect(() => {
  //   filterData();
  // }, [searchQuery]);

  //Top right side Filter
  const onSearch = async (params) => {
    console.log("paramsparams ", params);
    // delete params.delete
    // setSearchQuery(Object.values(params))

    if (params === "reset") {
      // alert("Reset block excuted");
      setData(dataBack);
      return false;
    }
    if(params?.vendor_type===null && params?.vendor_name===null && params?.vendor_status===null){
      setData(dataBack);
      return false
    }
    filterData(params);
  };
  const filterData = (params) => {
    console.log("dataBack ",dataBack)
    const dataBackFi = dataBack.filter((item) => item.vendor_type);
    console.log("dataBack dataBackFi",dataBackFi)

    const filteredData = dataBackFi.filter((item) => { 
      return (
        item.vendor_type.toString().includes(params?.vendor_type) &&
        item.vendor_name.toString().includes(params?.vendor_name)
        // &&
        // item.status.toString().includes(params?.vendor_status)
        
        // &&
        // item?.witness_name?.toLowerCase()?.includes(searchQuery?.witness_name?.toLowerCase())
      );
    
    });
    setData(filteredData);
  };
  const onSearch_old = async (params) => {
    console.log("paramsparams ", params);
    // delete params.delete
    // setSearchQuery(Object.values(params))

    if (params === "reset") {
      alert("Reset block excuted");
      setData(dataBack);
      return false;
    }
    filterData(params); //old
  };
  
  // const filterData=()=>{
  const filterData_old = (params) => {
    //old
    //     const filteredArray = searchQuery.filter(item => item);
    // const filterItem = filteredArray.length>0?
    // filteredArray.map((item)=>data.filter((dataItem)=>dataItem.vendor_type === item))
    // :[...data]
    // setData(filterItem)
    //     console.log("searchQuery.length ",filteredArray)

    //     old
    const filteredData = dataBack?.filter(
      (item) => item?.vendor_type?.includes(params?.vendor_type) || item?.vendor_name?.toLowerCase()?.includes(params?.vendor_name?.toLowerCase())
      // &&
      // item?.witness_name?.toLowerCase()?.includes(searchQuery?.witness_name?.toLowerCase())
    );
    console.log("search field filterData ", filteredData);
    setData(filteredData);
  };

  //Left side Filter
  const handleFilterChange = (data) => {
    console.log("data data data ", data);
    globalSearch(data);
    // setSearchParams({ ...searchParams, ...data });
  };

  const globalSearch = (params, columnIds) => {
    const dataBackFi = dataBack.filter((item) => item.vendor_type);
    const filteredData = dataBackFi.filter((item) => {
      return (
        item.vendor_type.toString().toLowerCase().includes(params?.vendor_type.toLowerCase()) &&
        item.vendor_name.toString().toLowerCase().includes(params?.vendor_name?.toLowerCase())
      );
    });
    setData(filteredData);
  // return rows?.filter((row) =>
  //   searchParams?.babyLastName ? row.original?.babyLastName?.toUpperCase().startsWith(searchParams?.babyLastName.toUpperCase()) : true
  // );
};

  // const globalSearch = (rows, columnIds) => {// not in use
  const globalSearch_old = (params, columnIds) => {
    // not in use
    // return rows;
    const filteredData = dataBack?.filter(
      (item) => item?.vendor_type?.includes(params?.vendor_type) || item?.vendor_name?.toLowerCase()?.includes(params?.vendor_name?.toLowerCase())
      // &&
      // item?.witness_name?.toLowerCase()?.includes(searchQuery?.witness_name?.toLowerCase())
    );
    console.log("search field filterData ", filteredData);
    setData(filteredData);

    // return rows?.filter((row) =>
    //   searchParams?.babyLastName ? row.original?.babyLastName?.toUpperCase().startsWith(searchParams?.babyLastName.toUpperCase()) : true
    // );
  };


  const fetchNextPage = useCallback(() => {
    setPageOffset((prevPageOffSet) => parseInt(prevPageOffSet) + parseInt(pageSize));
  }, [pageSize]);

  const fetchPrevPage = useCallback(() => {
    setPageOffset((prevPageOffSet) => parseInt(prevPageOffSet) - parseInt(pageSize));
  }, [pageSize]);

  const handlePageSizeChange = (e) => {
    setPageSize(Number(e.target.value));
    // setPageSize((prevPageSize) => (e.target.value));
  };

  if (isLoading) {
    return <Loader />;
  }
  return (
    <div>
      <Header>{t("WMS_RUNNING_ACCOUNT_FINAL_BILL")}</Header>
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
        title={"Running Account / Final Bill"}
        iconName={"account"}
        currentPage={parseInt(pageOffset / pageSize)}
        onNextPage={fetchNextPage}
        onPrevPage={fetchPrevPage}
        onPageSizeChange={handlePageSizeChange}
        isLoading={isLoading}
        tenantId={tenantId}
        filterComponent={filterComponent}
        // filterComponent={"d"}
      />
      <style>
        {`
      
      iframe{background-color:blue;display:none}
      `}
      </style>
    </div>
  );
};

export default WmsRAFBList;
