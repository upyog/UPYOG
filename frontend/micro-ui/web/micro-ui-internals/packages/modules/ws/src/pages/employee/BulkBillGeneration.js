import { Loader, Toast } from "@upyog/digit-ui-react-components";
import React, { useState, Fragment, useCallback, useEffect } from "react";
import { useTranslation } from "react-i18next";

const BulkBillGeneration = ({ path }) => {
  const [isBothCallsFinished, setIsBothCallFinished] = useState(true);
  const { t } = useTranslation();
  const [payload, setPayload] = useState({});
  const [setLoading, setLoadingState] = useState(false);
  const WSBulkBillSearch = Digit.ComponentRegistryService.getComponent("WSBulkBillSearch");
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const getUrlPathName = window.location.pathname;
  const checkPathName = getUrlPathName.includes("water/search-connection");
  const businessServ = checkPathName ? "WS" : "SW";
  const [showToast, setShowToast] = useState(null);
 
  const onSubmit = ((data) => {
      setPayload({"locality":data?.locality?.code});   
  });

  const config = {
    enabled: !!(payload && Object.keys(payload).length > 0),
  };

  let result = Digit.Hooks.ws.useBulkSearchWS({ tenantId,filters: payload, config});
 
  const isMobile = window.Digit.Utils.browser.isMobile();

  if (result?.isLoading && isMobile) {
    return <Loader />
  }
 
  const getData = () => {
    console.log("result",result)
    if (result?.meterReadings.length == 0 ) {
      return { display: "ES_COMMON_NO_DATA" }
    } else if (result?.meterReadings.length > 0) {
      let meterReadings=result?.meterReadings.map((data)=>{
        return {"billingPeriod":data.billingPeriod,"connectionNo":data.connectionNo,"lastReading":data.lastReading,"lastReadingDate":data.lastReadingDate,"meterStatus":data.meterStatus,"currentReadingDate":"","currentReading":""}})
      return meterReadings
    } else {
      return [];
    }
  }

  const isResultsOk = () => {
    return result?.meterReadings?.length > 0 ? true : false;
  }

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
