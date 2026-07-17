import { Loader, Timeline } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { useTranslation } from "react-i18next";
import { useQueryClient } from "@tanstack/react-query";
import { Navigate, Route, Routes, useLocation } from "react-router-dom";
import { commonConfig } from "../../config/Create/citizenconfig";
import { GCDataConvert } from "../../utils";
import CheckPage from "../citizen/Create/CheckPage";
import GCAcknowledgement from "../citizen/Create/GCAcknowledgement";

/**
 * CreateApplication Component
 * Renders the exact same Step-by-Step wizard from the citizen side,
 * formatted and routed for the Employee dashboard.
 */
const CreateApplication = () => {
  const queryClient = useQueryClient();
  const { t } = useTranslation();
  const { pathname } = useLocation();
  const navigate = Digit.Hooks.useCustomNavigate();
  const stateId = Digit.ULBService.getStateId();

  let config = [];
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const mutation = Digit.Hooks.gc.useGCCreateAPI(tenantId);
  const [params, setParams, clearParams] = Digit.Hooks.useSessionStorage("GC_Create_Emp", {});
  let { isLoading } = Digit.Hooks.pt.useMDMS(stateId, "PropertyTax", "CommonFieldsConfig");

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

  if (params && Object.keys(params).length > 0 && window.location.href.includes("/applicant-details") && sessionStorage.getItem("docReqScreenByBack") !== "true") {
    clearParams();
    queryClient.invalidateQueries("GC_Create_Emp");
  }

  const clearSession = () => {
    clearParams();
    queryClient.invalidateQueries("GC_Create_Emp");
  };

  const handleSubmit = async () => {
    try {
      let formdata = GCDataConvert(params);

      // Unwrap the nested garbageAccount object if it exists
      if (formdata?.garbageAccounts?.[0]?.garbageAccount) {
        formdata.garbageAccounts[0] = formdata.garbageAccounts[0].garbageAccount;
      }

      // Ensure tenantId aligns correctly for the employee context
      if (formdata?.garbageAccounts?.[0]) {
        formdata.garbageAccounts[0].tenantId = tenantId;
        if (formdata.garbageAccounts[0].propertyLocation) {
          formdata.garbageAccounts[0].propertyLocation.tenantId = tenantId;
          formdata.garbageAccounts[0].propertyLocation.city = tenantId;
        }
        formdata.garbageAccounts[0].workflow = { action: "APPLY" };
      }
      
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
      <div style={{ marginLeft: "15px", marginBottom: "20px" }}>
        <Timeline config={config} />
      </div>
      <div style={{ padding: "0 15px" }}>
        <Routes>
          {config.map((routeObj, index) => {
            const { component, texts, inputs, key, additionaFields } = routeObj;
            const Component = typeof component === "string" ? Digit.ComponentRegistryService.getComponent(component) : component;
            return (
              <Route
                  path={`${routeObj.route}`}
                  key={index}
                  // Passing userType="employee" so your components know to use 50% width fields instead of 100%
                  element={<Component config={{ texts, inputs, key, additionaFields }} onSelect={handleSelect} t={t} formData={params} userType="employee" />}
              />
            );
          })}

          <Route path="check/*" element={<CheckPage onSubmit={handleSubmit} value={params} />} />
          <Route path="acknowledgement/*" element={<GCAcknowledgement />} />
          <Route path="/*" element={<Navigate to={`${config.indexRoute}`} replace />} />
        </Routes>
      </div>
    </React.Fragment>
  );
};

export default CreateApplication;