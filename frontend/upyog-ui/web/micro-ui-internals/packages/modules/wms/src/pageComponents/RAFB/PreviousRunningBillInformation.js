import { CardLabel, CitizenInfoLabel, FormStep, Loader, Table, TextInput } from "@egovernments/digit-ui-react-components";
import React, { useState, useEffect, useCallback } from "react";
import FormTableData from "../../components/List/RAFB/FormTableData";
import Timeline from "../../components/RAFB/Timeline";
// import { currentFinancialYear } from "../utils";

const PreviousRunningBillInformation = ({ t, config, onSelect, value, userType, formData, digitTest="testqwert" }) => {
  console.log("previous-running-bill config,formData ",{config,formData})
  let validation = {};
  const onSkip = () => onSelect();
  const [previousBills, setPreviousBills] = useState(formData.previous_bill?.previousBills);

  const tenantId = Digit.ULBService.getCurrentTenantId();
  const stateId = Digit.ULBService.getStateId();
  const isEdit = window.location.href.includes("/edit-application/") || window.location.href.includes("renew-trade");
  const { isLoading, data: fydata = {} } = Digit.Hooks.tl.useTradeLicenseMDMS(stateId, "egf-master", "FinancialYear");
console.log("fydata ",fydata)
  // let mdmsFinancialYear = fydata["egf-master"] ? fydata["egf-master"].FinancialYear.filter(y => y.module === "TL") : [];
  // let FY = mdmsFinancialYear && mdmsFinancialYear.length > 0 && mdmsFinancialYear.sort((x, y) => y.endingDate - x.endingDate)[0]?.code;
  function setSelectData(data) {
    console.log("previous_bill data ",data)// array[1,2]
    // console.log("previous_bill data.toString() ",data.toString())// converted array [1,2...] in string "1,2..."
    setPreviousBills(data);
  }

  useEffect(() => {
    localStorage.setItem("TLAppSubmitEnabled", "true");
  }, []);

  const goNext = () => {
    onSelect(config.key, [previousBills]);
  };
  if (isLoading) {
    return <Loader></Loader>
  }


  // table data start
  const [data, setData] = useState([]);
  const [dataBack, setDataBack] = useState([]);
  const [isTrue, setisTrue] = useState(false);
  const [pageSize, setPageSize] = useState(10);
  const [pageOffset, setPageOffset] = useState(0);
  const [searchParams, setSearchParams] = useState({
    eventStatus: [],
    range: {
      startDate: null,
      endDate: new Date(""),
      title: "",
    },
    // ulb: tenants,
    // ulb: tenants?.find(tenant => tenant?.code === tenantId)
  });

  const { isError: vendorCreateError, data: updateResponse, error: updateError, mutate } = Digit?.Hooks?.wms?.te?.useWmsTESearch(tenantId);
  const {data:RAFB_Data,isSuccess:RAFB_isSuccess,isLoading:RAFB_isLoading} = Digit?.Hooks?.wms?.rafb?.useWmsRAFBGet(tenantId, "getPriviousBill") || {};
  console.log("rafbData ",{RAFB_Data,RAFB_isSuccess,RAFB_isLoading})
  useEffect(() => {
    if (RAFB_Data) {
      setData(RAFB_Data);
      setDataBack(RAFB_Data);
    }
  }, [RAFB_Data]);
 
  const links = [
    {
      text: t("Create Contractor Master delete it"),
      link: "/digit-ui/citizen/wms/birth",
    },
  ];
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
      }
    ];
  };
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
  // table data end


  return (
    <React.Fragment>
      {window.location.href.includes("/citizen") ? <Timeline currentStep={2} /> : null}
      <FormStep
        config={config}
        onSelect={goNext}
        onSkip={onSkip}
        t={t}
        // isDisabled={!previousBills}
      >
        {/* <CardLabel>{`${t("WMS_RUNNING_ACCOUNT_PREVIOUS_RUNNING_BILL")}`}</CardLabel> */}
        <div>Previous Running Bill Information</div> 

        <FormTableData
        t={t}
        data={data}
        // data={dataDummy}
        links={links}
        // parentRoute={parentRoute}
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
        // onChange={setSelectData}
        setSelectData={setSelectData}
        // filterComponent={filterComponent}
        // filterComponent={"d"}
      />

      </FormStep>
      {/* {<CitizenInfoLabel info={t("CS_FILE_APPLICATION_INFO_LABEL")} text={t("TL_LICENSE_ISSUE_YEAR_INFO_MSG") + FY} />} */}
    </React.Fragment>
  );
};

export default PreviousRunningBillInformation;
