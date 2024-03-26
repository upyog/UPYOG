import React, { useState, useCallback, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { format, isValid } from "date-fns";
import { Header, LinkButton, Loader, Toast } from "@egovernments/digit-ui-react-components";
import DesktopInbox from "../../../components/List/CM/CA/DesktopList";
import axios from "axios";
import { Link } from "react-router-dom";

const View = ({ tenants, parentRoute }) => {
  const {t} = useTranslation();
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
  const [dataBack, setDataBack] = useState();
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
          status:res?.status,
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
      {
        label: t("Witness Name"),
        name: "witness_name",
        // isMendatory:true
      },
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

  //Filter server side
  // function filterTableList(departmentName, prebidMeetingDate) {
  //   let formData = "";
  //   if (
  //     (prebidMeetingDate != null && departmentName == null) ||
  //     (prebidMeetingDate != "" && departmentName == "") ||
  //     (prebidMeetingDate != undefined && departmentName == undefined)
  //   ) {
  //     formData = `?prebidMeetingDate=${prebidMeetingDate}`;
  //     return formData;
  //   } else if (
  //     (departmentName != null && prebidMeetingDate == null) ||
  //     (departmentName != "" && prebidMeetingDate == "") ||
  //     (departmentName != undefined && prebidMeetingDate == undefined)
  //   ) {
  //     formData = `?departmentName=${departmentName}`;
  //     return formData;
  //   } else if (
  //     (departmentName != "" && prebidMeetingDate != "") ||
  //     (departmentName != null && prebidMeetingDate != null) ||
  //     (departmentName != undefined && prebidMeetingDate != undefined)
  //   ) {
  //     formData = `?departmentName=${departmentName}&prebidMeetingDate=${prebidMeetingDate}`;
  //     return formData;
  //   } else {
  //     alert("Please fill any field");
  //   }
  // }

  const [searchQuery, setSearchQuery] = useState({
    vendor_type: "",
    vendor_name: "",
    witness_name: "",
  });

  const [filteredData, setFilteredData] = useState(data);

  // const handleInputChange = (e) => {
  //   const { name, value } = e.target;
  //   setSearchQuery({
  //     ...searchQuery,
  //     [name]: value
  //   });
  // };

  // const searchData = () => {
  //   const { name, age, city } = searchQuery;
  //   const filteredData = initialData.filter(
  //     (item) =>
  //       (name === "" || item.name.toLowerCase().includes(name.toLowerCase())) &&
  //       (isNaN(age) || item.age === parseInt(age)) &&
  //       (city === "" || item.city.toLowerCase().includes(city.toLowerCase()))
  //   );
  //   setFilteredData(filteredData);
  // };

  const onSearch = async (params) => {
    console.log("search field ", params);
    // const {vendor_type:name,vendor_name:age,witness_name:city}=params;
    const { vendor_type, vendor_name, witness_name } = params;
    console.log("vendor_type,vendor_name,witness_name ", { vendor_type, vendor_name, witness_name });
    // const { name, value } = e.target;
    setSearchQuery({
      ...searchQuery,
      vendor_type: vendor_type ? vendor_type : "",
      vendor_name: vendor_name ? vendor_name : "",
      witness_name: witness_name ? witness_name : "",
    });
    // if (params === "reset" || (vendor_type === null && vendor_name === null)) {
    if (params === "reset") {
      alert("Reset block excuted");
      setData(dataBack);
      return false;
    }
    if(Boolean(searchQuery.vendor_type) || Boolean(searchQuery.vendor_name) || Boolean(searchQuery.witness_name)){
      filterData()
    }

    // if (params === "reset" || (params?.departmentName === null && params?.prebidMeetingDate === null)) {
    //   setData(dataBack);
    //   return false;
    // }
    // const filterData = filterTableList(params?.departmentName, params?.prebidMeetingDate);
    // await mutate(filterData);
  };

const filterData=()=>{
  // debugger;
    console.log(dataBack);
    console.log("searchQuery ", searchQuery);

    const filteredData = dataBack?.filter(
      (item) =>
        item?.vendor_type?.toLowerCase()?.includes(searchQuery?.vendor_type?.toLowerCase()) &&
        item?.vendor_name?.toLowerCase()?.includes(searchQuery?.vendor_name?.toLowerCase()) &&
        item?.witness_name?.toLowerCase()?.includes(searchQuery?.witness_name?.toLowerCase())
    );

    // setFilteredData(filteredData);

    console.log("search field filterData ", filteredData);
    setData(filteredData);
}
  const handleFilterChange = (data) => {
    setSearchParams({ ...searchParams, ...data });
  };

  const globalSearch = (rows, columnIds) => {
    // return rows;
    return rows?.filter((row) =>
      searchParams?.babyLastName ? row.original?.babyLastName?.toUpperCase().startsWith(searchParams?.babyLastName.toUpperCase()) : true
    );
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

  // if(is_Loading){
  //   alert("ddddddddddd")
  // }

  if (isLoading) {
    return <Loader />;
  }
  return (
    <div>
      <Header>{t("WMS_CA_ADD_CONTRACT_AGREEMENT_LIST")}</Header>
      <Link to={`add`}>
        {/* <LinkButton style={{ textAlign: "left" }} label={t("PT_VIEW_MORE_DETAILS")} /> */}
        <LinkButton style={{ display: "block", textAlign: "right" }} label={t("Add")} />
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
        title={"Agreement Contractor List"}
        iconName={"calender"}
        currentPage={parseInt(pageOffset / pageSize)}
        onNextPage={fetchNextPage}
        onPrevPage={fetchPrevPage}
        onPageSizeChange={handlePageSizeChange}
        isLoading={isLoading}
        tenantId={tenantId}
      />
      <style>
        {`
      
      iframe{background-color:blue;display:none}
      `}
      </style>
    </div>
  );
};

export default View;
