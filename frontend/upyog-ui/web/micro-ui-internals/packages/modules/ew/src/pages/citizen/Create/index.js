import { Loader } from "@nudmcdgnpm/digit-ui-react-components";
import React, { Fragment } from "react";
import { useTranslation } from "react-i18next";
import { useQueryClient } from "@tanstack/react-query";
import { Route, useLocation,  Routes, Navigate } from "react-router-dom";
import { citizenConfig } from "../../../config/Create/citizenconfig";
import { EWDataConvert } from "../../../utils";

/**
 * Main component for E-Waste creation workflow.
 * Handles multi-step form navigation, data management and submission for E-Waste requests.
 * 
 * @param {Object} props Component properties
 * @param {string} props.parentRoute Base route for the creation workflow
 * @returns {JSX.Element} Multi-step form interface for E-Waste creation
 */
const EWCreate = ({ parentRoute }) => {

  const queryClient = useQueryClient();
  const match = Digit.Hooks.useModuleBasePath();
  const { t } = useTranslation();
  const { pathname } = useLocation();
  const navigate = Digit.Hooks.useCustomNavigate();
  const stateId = Digit.ULBService.getStateId();
  let config = [];
  const tenantId = Digit.ULBService.getCitizenCurrentTenant(true);
  const mutation = Digit.Hooks.ew.useEWCreateAPI(tenantId);
  const [params, setParams, clearParams] = Digit.Hooks.useSessionStorage("EWASTE_CREATE", {});
  let { data: commonFields, isLoading } = Digit.Hooks.pt.useMDMS(stateId, "PropertyTax", "CommonFieldsConfig");

  /**
   * Handles navigation between form steps
   * Determines next route based on current path and navigation parameters
   * 
   * @param {boolean} skipStep Whether to skip the current step
   * @param {number} index Current step index
   * @param {boolean} isAddMultiple Whether adding multiple items
   * @param {string} key Form section identifier
   */
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
    }
    else {
      nextPage = isMultiple && nextStep !== "map" ? `${nextStep}/${index}` : `${nextStep}`;
    }

    redirectWithHistory(nextPage);
  };

  /**
   * Handles form section data updates
   * Updates session storage with new form data
   * 
   * @param {string} key Section identifier
   * @param {Object} data Section form data
   * @param {boolean} skipStep Whether to skip next step
   * @param {number} index Current step index
   * @param {boolean} isAddMultiple Whether adding multiple items
   */

  const handleSubmit = () => {
    const formdata = EWDataConvert(params);
    formdata.EwasteApplication[0].tenantId = tenantId;
    mutation.mutate(formdata, {
      onSuccess: (response) => {
        clearParams();
        queryClient.invalidateQueries("EWASTE_CREATE");
        navigate("acknowledgement", {
          state: {
            data: response,
            isSuccess: true,
          },
        });
      },
  
      onError: (error) => {
        navigate("acknowledgement", {
          state: {
            data: null,
            isSuccess: false,
            error:error
          },
        });
      },
    });
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

  const handleSkip = () => { };
  const handleMultiple = () => { };

  /**
   * Handles successful form submission
   * Clears session storage and invalidates cached data
   */
  const onSuccess = () => {
    clearParams();
    queryClient.invalidateQueries("EWASTE_CREATE");
  };
  if (isLoading) {
    return <Loader />;
  }

  commonFields = citizenConfig;
  commonFields.forEach((obj) => {
    config = config.concat(obj.body.filter((a) => !a.hideInCitizen));
  });

  config.indexRoute = "productdetails";

  const CheckPage = Digit?.ComponentRegistryService?.getComponent("EWCheckPage");
  const EWASTEAcknowledgement = Digit?.ComponentRegistryService?.getComponent("EWASTEAcknowledgement");

  if (params && Object.keys(params).length > 0 && window.location.href.includes("/info") && sessionStorage.getItem("docReqScreenByBack") !== "true") {
    clearParams();
    queryClient.invalidateQueries("EWASTE_CREATE");
  }

  return (
    <Routes>
      {config.map((routeObj, index) => {
        const { component, texts, inputs, key } = routeObj;
        const Component = typeof component === "string" ? Digit.ComponentRegistryService.getComponent(component) : component;
        return (
          <Route
            path={`${routeObj.route}`}
            key={index}
            element={
              <Component config={{ texts, inputs, key }} onSelect={handleSelect} onSkip={handleSkip} t={t} formData={params} onAdd={handleMultiple} />
            }
          />
        );
      })}

      <Route path={`check`} element={<CheckPage onSubmit={handleSubmit} value={params} />} />
      <Route path={`acknowledgement`} element={<EWASTEAcknowledgement/>} />
      <Route path="*" element={<Navigate to={`${config.indexRoute}`} replace />} />
    </Routes>
  );
};

export default EWCreate;