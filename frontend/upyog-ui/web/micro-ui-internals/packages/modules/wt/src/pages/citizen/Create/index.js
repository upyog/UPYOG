import React from "react";
import { useTranslation } from "react-i18next";
import { useQueryClient } from "@tanstack/react-query";
import { Navigate, Route, Routes, useLocation, useMatch } from "react-router-dom";
import { commonConfig } from "../../../config/config";
import { Timeline } from "@nudmcdgnpm/digit-ui-react-components";
import { treePruningPayload, waterTankerPayload, mobileToiletPayload } from "../../../utils";


const WTCreate = () => {
  const queryClient = useQueryClient();
  const { t } = useTranslation();
  const { pathname } = useLocation();
  const navigate = Digit.Hooks.useCustomNavigate();

  let config = [];
  const tenantId = Digit.ULBService.getCitizenCurrentTenant(true) || Digit.ULBService.getCurrentTenantId();
  const wtMutation = Digit.Hooks.wt.useTankerCreateAPI(tenantId);
  const mtMutation = Digit.Hooks.wt.useMobileToiletCreateAPI(tenantId);
  const tpMutation = Digit.Hooks.wt.useTreePruningCreateAPI(tenantId);
  const [params, setParams, clearParams] = Digit.Hooks.useSessionStorage("WT_Create", {});

  // Sets the serviceType in case of employee side for WT, MT, and TP
  if ((!params.serviceType || Object.keys(params.serviceType).length === 0) && pathname.includes("employee")) {
    if (pathname.includes("mt")) {
      setParams({
        "serviceType": {
          "serviceType": {
            "code": "MobileToilet",
            "i18nKey": "Mobile Toilet",
            "value": "Mobile Toilet"
          }
        }
      })
    } else if (pathname.includes("tp")) {
      setParams({
        "serviceType": {
          "serviceType": {
            "code": "TREE_PRUNING",
            "i18nKey": "Tree Pruning",
            "value": "Tree Pruning"
          }
        }
      })
    }
    else {
      setParams({
        "serviceType": {
          "serviceType": {
            "code": "WT", "i18nKey": "Water Tanker", "value": "Water Tanker"
          }
        }
      })
    }
  }

  const goNext = (skipStep, index, isAddMultiple, key) => {
    let currentPath = pathname.split("/").pop(),
      lastchar = currentPath.charAt(currentPath.length - 1),
      isMultiple = false,
      nextPage;
    if (Number(parseInt(currentPath)) || currentPath == "0" || currentPath == "-1") {
      if (currentPath == "-1" || currentPath == "-2") {
        currentPath = pathname.slice(0, -3);
        currentPath = currentPath.split("/").pop();
        isMultiple = true;
      } else {
        currentPath = pathname.slice(0, -2);
        currentPath = currentPath.split("/").pop();
        isMultiple = true;
      }
    } else {
      isMultiple = false;
    }
    if (!isNaN(lastchar)) {
      isMultiple = true;
    }
    let { nextStep = {} } = config.find((routeObj) => routeObj.route === currentPath);

    let redirectWithHistory = (to, state) => navigate(to, state != null ? { state } : undefined);
    if (skipStep) {
      redirectWithHistory = (to, state) => navigate(to, state != null ? { replace: true, state } : { replace: true });
    }
    if (isAddMultiple) {
      nextStep = key;
    }
    // Change next step to "toiletRequest-details" if the current step is "request-details" and the service type code is not "WT".
    if (nextStep === "request-details") { 
        const code = params?.serviceType?.serviceType?.code;
        if (code === "MobileToilet") {
          nextStep = "toiletRequest-details";
        } else if (code === "TREE_PRUNING") {
          nextStep = "treePruningRequest-details"; 
        }
    } 
    if (nextStep === null) {
      return redirectWithHistory(`check`);
    }
    if (!isNaN(nextStep.split("/").pop())) {
      nextPage = `${nextStep}`;
    }
    else {
      nextPage = isMultiple && nextStep !== "map" ? `${nextStep}/${index}` : `${nextStep}`;
    }

    redirectWithHistory(nextPage);
  };

 

  if (params && Object.keys(params).length>0 && window.location.href.includes("/service-type") && sessionStorage.getItem("docReqScreenByBack") !== "true") {
    clearParams();
    queryClient.invalidateQueries("WT_Create");
  }

   const clearSession = () => {
    clearParams();
    queryClient.invalidateQueries("WT_Create");
  };

  const handleSubmit = async () => {
    if (params?.serviceType?.serviceType?.code === "WT") {
      const formdata = waterTankerPayload(params);
      formdata.waterTankerBookingDetail.tenantId = tenantId;
            
          wtMutation.mutate(formdata, {
            onSuccess: (response) => {
              clearSession();
              navigate("wt-acknowledgement", {
                state: {
                  data: response,
                  isSuccess: true,
                },
              });
            },
        
            onError: (error) => {
              navigate("wt-acknowledgement", {
                state: {
                  data: null,
                  isSuccess: false,
                  error:error
                },
              });
            },
          });
    }
    if (params?.serviceType?.serviceType?.code === "MobileToilet") {
      const formdata = mobileToiletPayload(params);
      formdata.mobileToiletBookingDetail.tenantId = tenantId;
          mtMutation.mutate(formdata, {
            onSuccess: (response) => {
              clearSession();
              navigate("mt-acknowledgement", {
                state: {
                  data: response,
                  isSuccess: true,
                },
              });
            },
        
            onError: (error) => {
              navigate("mt-acknowledgement", {
                state: {
                  data: null,
                  isSuccess: false,
                  error:error
                },
              });
            },
          });
    }
    if (params?.serviceType?.serviceType?.code === "TREE_PRUNING") {
         const formdata = treePruningPayload(params);
         formdata.treePruningBookingDetail.tenantId = tenantId;  
          tpMutation.mutate(formdata, {
            onSuccess: (response) => {
              clearSession();
              navigate("tp-acknowledgement", {
                state: {
                  data: response,
                  isSuccess: true,
                },
              });
            },
        
            onError: (error) => {
              navigate("tp-acknowledgement", {
                state: {
                  data: null,
                  isSuccess: false,
                  error:error
                },
              });
            },
          });
    }
  };

  function handleSelect(key, data, skipStep, index, isAddMultiple = false) {
    if (key === "owners") {
      let owners = params.owners || [];
      owners[index] = data;
      setParams({ ...params, ...{ [key]: [...owners] } });
    } else if (key === "units") {
      let units = params.units || [];
      // if(index){units[index] = data;}else{
      units = data;

      setParams({ ...params, units });
    } else {
      setParams({ ...params, ...{ [key]: { ...params[key], ...data } } });
    }
    goNext(skipStep, index, isAddMultiple, key);
  }

  let commonFields = commonConfig;
  commonFields.forEach((obj) => {
    config = config.concat(obj.body.filter((a) => !a.hideInCitizen));
  });

  // Changes the indexRoute based on the pathname
  config.indexRoute = pathname.includes("citizen")? "service-type" : "info";

  const CheckPage = Digit?.ComponentRegistryService?.getComponent("WTCheckPage");
  const WTAcknowledgement = Digit?.ComponentRegistryService?.getComponent("WTAcknowledgement");
  const MTAcknowledgement = Digit?.ComponentRegistryService?.getComponent("MTAcknowledgement");
  const TPAcknowledgement = Digit?.ComponentRegistryService?.getComponent("TPAcknowledgement");

  return (
    <React.Fragment>
      <Timeline config={config} />
      <Routes>
        {config.map((routeObj, index) => {
          const { component, texts, inputs, key, additionaFields } = routeObj;
          const Component = typeof component === "string" ? Digit.ComponentRegistryService.getComponent(component) : component;
          return (
            <Route
              path={`${routeObj.route}`}
              key={index}
              element={<Component config={{ texts, inputs, key, additionaFields }} onSelect={handleSelect} t={t} formData={params} />}
            />
          );
        })}
        <Route path="check/*" element={<CheckPage onSubmit={handleSubmit} value={params} />} />
        <Route path="wt-acknowledgement/*" element={<WTAcknowledgement />} />
        <Route path="mt-acknowledgement/*" element={<MTAcknowledgement />} />
        <Route path="tp-acknowledgement/*" element={<TPAcknowledgement />} />
        <Route path="/*" element={<Navigate to={`${config.indexRoute}`} />} />
      </Routes>
    </React.Fragment>
  );
};

export default WTCreate;