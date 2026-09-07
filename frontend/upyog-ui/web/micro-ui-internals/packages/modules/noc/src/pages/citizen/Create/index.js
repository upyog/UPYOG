/** 
 * Manages the NOC citizen application creation lifecycle, routing through configured steps,
 * persisting form state in session storage, and handling step navigation to check page and acknowledgement.
 */
import React from "react";
import { useTranslation } from "react-i18next";
import { Navigate, Route, Routes, useLocation } from "react-router-dom";
import { newConfig as newConfigNOC } from "../../../config/config";
import { convertToFireNOCPayload } from "../../../utils";
import { Loader, Timeline } from "@nudmcdgnpm/digit-ui-react-components";

import CheckPageLocal from "./CheckPage";
import NOCAcknowledgementLocal from "./NOCAcknowledgement";

const CreateNoc = ({ parentRoute }) => {
  const { t } = useTranslation();
  const { pathname } = useLocation();
  const navigate = Digit.Hooks.useCustomNavigate();
  const [params, setParams, clearParams] = Digit.Hooks.useSessionStorage("NOC_CREATE_APPLICATION", {});
  const stateId = Digit.ULBService.getStateId();
  const tenantId = params?.property?.location?.property?.city?.code || params?.location?.city?.code || Digit.ULBService.getCurrentTenantId();
  const createMutation = Digit.Hooks.noc.useFireNOCAPI(tenantId, true);
  const updateMutation = Digit.Hooks.noc.useFireNOCAPI(tenantId, false);

  React.useEffect(() => {
    if (pathname.includes("document-required")) {
      Digit.SessionStorage.del("NOC_CREATE_APPLICATION");
      Digit.SessionStorage.del("NOC_SUCCESSFUL_APPLICATION");
      clearParams();
    }
  }, [pathname]);

  const CheckPage = Digit?.ComponentRegistryService?.getComponent("NOCCheckPage");
  const NocAcknowledgement = Digit?.ComponentRegistryService?.getComponent("NOCAcknowledgement");

  let config = [];
  let { data: newConfig, isLoading } = Digit.Hooks.noc?.useMDMS?.getFormConfig
    ? Digit.Hooks.noc.useMDMS.getFormConfig(stateId, {})
    : { data: null, isLoading: false };

  const goNext = (skipStep, index, isAddMultiple, key) => {
    let currentPath = pathname.split("/").pop(),
      nextPage;
    let { nextStep = {} } = config.find((routeObj) => routeObj.route === currentPath);

    let redirectWithHistory = (to, state) => navigate(to, state != null ? { state } : undefined);
    if (skipStep) {
      redirectWithHistory = (to, state) => navigate(to, state != null ? { replace: true, state } : { replace: true });
    }
    if (nextStep === null) {
      return redirectWithHistory(`check`);
    }
    nextPage = `${nextStep}`;
    redirectWithHistory(nextPage);
  };

  /**
   * Handles step form updates and handles step submission.
   * On owner step submission, executes backend API mutation (INITIATE action via create or update)
   * to persist initial application state, assign application numbers, and navigate to the next step.
   */
  function handleSelect(key, data, skipStep, index, isAddMultiple = false) {
    if (key === "formData") {
      setParams({ ...data });
    } else if (key === "owners") {
      const updatedParams = {
        ...params,
        ownershipCategory: data.ownershipCategory,
        owners: { ...params.owners, owners: data.owners }
      };
      if (!params?.applicationNumber) {
        const formdata = convertToFireNOCPayload(updatedParams, {
          action: "INITIATE",
          isUpdate: false
        });
        createMutation.mutate(formdata, {
          onSuccess: (res) => {
            const createdApp = res?.FireNOCs?.[0];
            if (createdApp) {
              setParams({
                ...updatedParams,
                id: createdApp.id,
                applicationNumber: createdApp.fireNOCDetails?.applicationNumber,
                existingApplication: createdApp
              });
            } else {
              setParams(updatedParams);
            }
            goNext(skipStep, index, isAddMultiple, key);
          },
        });
      } else {
        const formdata = convertToFireNOCPayload(updatedParams, {
          action: "INITIATE",
          isUpdate: true,
          existingApplication: params.existingApplication
        });
        updateMutation.mutate(formdata, {
          onSuccess: (res) => {
            const updatedApp = res?.FireNOCs?.[0];
            if (updatedApp) {
              setParams({
                ...updatedParams,
                id: updatedApp.id,
                applicationNumber: updatedApp.fireNOCDetails?.applicationNumber,
                existingApplication: updatedApp
              });
            } else {
              setParams(updatedParams);
            }
            goNext(skipStep, index, isAddMultiple, key);
          },
        });
      }
    } else {
      setParams({ ...params, ...{ [key]: { ...params[key], ...data } } });
      goNext(skipStep, index, isAddMultiple, key);
    }
  }

  if (createMutation.isPending || updateMutation.isPending) {
    return <Loader />;
  }

  newConfig = newConfig ? newConfig : newConfigNOC;
  newConfig?.forEach((obj) => {
    config = config.concat(obj.body.filter((a) => !a.hideInCitizen));
  });
  config.indexRoute = "document-required";

  return (
    <React.Fragment>
      <Timeline config={config} />
      <Routes>
      {config?.map((routeObj, index) => {
        const { component, texts, inputs, key, isSkipEnabled, isMandatory } = routeObj;
        const Component = typeof component === "string"
          ? Digit.ComponentRegistryService.getComponent(component)
          : component;
        if (!Component) return null;
        return (
          <Route
            path={`${routeObj.route}`}
            key={index}
            element={
              <Component
                config={{ texts, inputs, key, isSkipEnabled, isMandatory }}
                onSelect={handleSelect}
                t={t}
                formData={params}
                userType="citizen"
              />
            }
          />
        );
      })}
      <Route path={`check`} element={<CheckPage onSubmit={() => navigate("acknowledgement", { replace: true })} value={params} />} />
      <Route path={`acknowledgement`} element={<NocAcknowledgement data={params} clearParams={clearParams} />} />
      <Route path="*" element={<Navigate to={`${config.indexRoute}`} />} />
    </Routes>
    </React.Fragment>
  );
};

export default CreateNoc;

