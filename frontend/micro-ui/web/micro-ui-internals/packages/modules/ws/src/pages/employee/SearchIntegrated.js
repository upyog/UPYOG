import { Loader, Toast } from "@upyog/digit-ui-react-components";
import React, { useState, Fragment, useCallback, useEffect } from "react";
import { useTranslation } from "react-i18next";

const SearchIntegrated = ({ path }) => {
  const [isBothCallsFinished, setIsBothCallFinished] = useState(true);
  const { t } = useTranslation();
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const [payload, setPayload] = useState({});
  const [setLoading, setLoadingState] = useState(false);
  const SWater = Digit.ComponentRegistryService.getComponent("WSSearchWaterConnectionIntegrated");

  //const [businessServ, setBusinessServ] = useState([]);
  const getUrlPathName = window.location.pathname;
  const checkPathName = getUrlPathName.includes("water/search");
  const businessServ = checkPathName ? "WS" : "SW";
  const [showToast, setShowToast] = useState(null);
  const serviceConfig = {
    WATER: "WATER",
    SEWERAGE: "SEWERAGE",
  };

  const onSubmit = useCallback((_data) => {
    
    const { connectionNumber, oldConnectionNumber, mobileNumber, propertyId } = _data;   
    if (!connectionNumber && !oldConnectionNumber && !mobileNumber && !propertyId) {
      setShowToast({ error: true, label: "WS_HOME_SEARCH_CONN_RESULTS_DESC" });
      setTimeout(() => {
        setShowToast(false);
      }, 3000);
    } else {
      setPayload(
        Object.keys(_data)
          .filter((k) => _data[k])
          .reduce((acc, key) => ({ ...acc, [key]: typeof _data[key] === "object" ? _data[key].code : _data[key] }), {})
      );
    }
  });

  const config = {
    enabled: !!(payload && Object.keys(payload).length > 0),
  };

  let result12 = Digit.Hooks.ws.useSearchWS({ tenantId, filters: payload, config, bussinessService: "WS", t ,shortAddress:true});
  let result = Digit.Hooks.ws.useSearchWS({ tenantId, filters: payload, config, bussinessService: "SW", t ,shortAddress:true});
  


  const isMobile = window.Digit.Utils.browser.isMobile();

 
  const getData = () => {

    if (result?.data?.length == 0 &&  result12?.data?.length == 0) {
      return { display: "ES_COMMON_NO_CONNECTION" }
    } else if (result12?.data?.length > 0 &&  result?.data?.length == 0) {
     
      return result12?.data
    } 
    else if(result?.data?.length > 0 &&  result12?.data?.length == 0)
    {
      return result?.data
    }
    else if (result?.data?.length > 0 &&  result12?.data?.length > 0)
    {
return [...result.data,...result12.data]
    }
      else {
      return [];
    }
    
  }

  const isResultsOk = () => {
   
    return result12?.data?.length > 0 || result?.data?.length > 0? true : false;
  }


  return (
    <Fragment>
      <SWater
        t={t}
        tenantId={tenantId}
        onSubmit={onSubmit}
        data={getData()}
        count={10}
        resultOk={isResultsOk()}
        businessService={businessServ}
       
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

export default SearchIntegrated;
