import { Loader, Toast } from "@egovernments/digit-ui-react-components";
import React, { useState, Fragment, useCallback, useEffect } from "react";
import { useTranslation } from "react-i18next";

const BulkBillGeneration = ({ path }) => {
  const [isBothCallsFinished, setIsBothCallFinished] = useState(true);
  const { t } = useTranslation();
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const [payload, setPayload] = useState({});
  const [setLoading, setLoadingState] = useState(false);
  const WSBulkBillSearch = Digit.ComponentRegistryService.getComponent("WSBulkBillSearch");
  // const [businessServ, setBusinessServ] = useState([]);
  const getUrlPathName = window.location.pathname;
  const checkPathName = getUrlPathName.includes("water/search-connection");
  const businessServ = checkPathName ? "WS" : "SW";
const [result1,setResult] =useState("")
  const [showToast, setShowToast] = useState(null);
  const serviceConfig = {
    WATER: "WATER",
    SEWERAGE: "SEWERAGE",
  };
console.log("gggggggggggggbulknill")
  const onSubmit = useCallback(() => {
 
      setPayload({"tenantId":"pg.citya","locality":"JLC476"});
    
  });

  const config = {
    enabled: !!(payload && Object.keys(payload).length > 0),
  };

  let result = Digit.WSService.WSMeterSearch({ filters: payload, config});
  
  const isMobile = window.Digit.Utils.browser.isMobile();

  if (result?.isLoading && isMobile) {
    return <Loader />
  }
  useEffect(async () => {
    setPayload({"tenantId":"pg.citya","locality":"JLC476"});
    let data = await Digit.WSService.WSMeterSearch({filters: payload})
    console.log("bbbbbbbbbbbbb",data)
    setResult(data.meterReadings)
  }, [])
  const getData = () => {
    console.log("result",result1)
    if (result1?.length == 0 ) {
      return { display: "ES_COMMON_NO_DATA" }
    } else if (result1?.length > 0) {
      return result1
    } else {
      return [];
    }
  }

  const isResultsOk = () => {
    return result?.data?.length > 0 ? true : false;
  }

  if(!result?.isLoading)
    result.data = result?.data?.map((item) => {
      if (item?.connectionNo?.includes("WS")) {
        item.service = serviceConfig.WATER;
      } else if (item?.connectionNo?.includes("SW")) {
        item.service = serviceConfig.SEWERAGE;
      }
      return item;
    });

  return (
    <Fragment>
      <WSBulkBillSearch
        t={t}
        tenantId={tenantId}
        onSubmit={onSubmit}
        data={getData()}
        count={result?.count}
        resultOk={isResultsOk()}
        businessService={businessServ}
        isLoading={result?.isLoading}
      />

      {showToast && (
        <Toast
          error={showToast.error}
          warning={showToast.warning}
          label={t(showToast.label)}
          onClose={() => {
            setShowToast(null);
          }}
        />
      )}
    </Fragment>
  );
};

export default BulkBillGeneration;
