import { Loader, Timeline } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { useTranslation } from "react-i18next";
import { useQueryClient } from "@tanstack/react-query";
import { Navigate, Route, Routes, useLocation, useMatch } from "react-router-dom";
import { commonConfig } from "../../../config/Create/citizenconfig";
import { GCDataConvert } from "../../../utils";
import CheckPage from "./CheckPage";
import GCAcknowledgement from "./GCAcknowledgement";

/**
 * GCCreate component manages the flow of the GC creation process,
 * including rendering the appropriate form components based on the current route.
 * It handles user input, navigates between steps, and manages session storage for form data.
 */
const GCCreate = () => {
  const queryClient = useQueryClient();
  const { t } = useTranslation();
  const { pathname } = useLocation();
  const navigate = Digit.Hooks.useCustomNavigate();
  const stateId = Digit.ULBService.getStateId();

  let config = [];
  const tenantId = Digit.ULBService.getCitizenCurrentTenant(true) || Digit.ULBService.getCurrentTenantId();
  const mutation = Digit.Hooks.gc.useGCCreateAPI(tenantId);
  const [params, setParams, clearParams] = Digit.Hooks.useSessionStorage("GC_Create", {});
  
  let { data: commonFieldsData, isLoading } = Digit.Hooks.pt.useMDMS(stateId, "PropertyTax", "CommonFieldsConfig");

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
    if (nextStep === null) {
      return redirectWithHistory(`check`);
    }
    if (!isNaN(nextStep.split("/").pop())) {
      nextPage = `${nextStep}`;
    } else {
      nextPage = isMultiple && nextStep !== "map" ? `${nextStep}/${index}` : `${nextStep}`;
    }

    redirectWithHistory(nextPage);
  };

  // if (params && Object.keys(params).length > 0 && window.location.href.includes("/applicant-details") && sessionStorage.getItem("docReqScreenByBack") !== "true") {
  //   clearParams();
  //   queryClient.invalidateQueries("GC_Create");
  // }

  const clearSession = () => {
    clearParams();
    queryClient.invalidateQueries("GC_Create");
  };

  const handleSubmit = async () => {
    try {
      const formdata = GCDataConvert(params);
      
      mutation.mutate(formdata, {
        onSuccess: (response) => {
          clearSession();
          navigate("acknowledgement", {
            state: {
              data: response,
              isSuccess: true,
            },
          });
        },
        onError: (error) => {
          console.error("API Error:", error);
          navigate("acknowledgement", {
            state: {
              data: null,
              isSuccess: false,
              error: error
            },
          });
        }
      });
    } catch (err) {
      console.error("Data Conversion Error:", err);
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

  if (isLoading) {
    return <Loader />;
  }

  let commonFields = commonConfig;
  commonFields.forEach((obj) => {
    config = config.concat(obj.body.filter((a) => !a.hideInCitizen));
  });

  config.indexRoute = "applicant-details";

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
        <Route path="acknowledgement/*" element={<GCAcknowledgement />} />
        <Route path="/*" element={<Navigate to={`${config.indexRoute}`} replace />} />
      </Routes>
    </React.Fragment>
  );
};

export default GCCreate;