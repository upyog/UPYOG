import React, { useState } from "react";
import { useTranslation } from "react-i18next";
import { useQueryClient } from "react-query";
import { Redirect, Route, Switch, useHistory, useLocation, useRouteMatch } from "react-router-dom";
import { newConfig as newConfigEDCR} from "../../../../config/edcrConfig";
// import { uuidv4 } from "../../../utils";
import EDCRAcknowledgement from "./EDCRAcknowledgement";
import  { APPLICATION_PATH } from "./utils";

/*
  This file sets up the CreateAnonymousEDCR component, which manages the anonymous EDCR creation process.
  It initializes state variables, fetches configurations, and sets up routing for the EDCR workflow.
*/
const CreateAnonymousEDCR = ({ parentRoute }) => {
  const queryClient = useQueryClient();
  const match = useRouteMatch();
  const { t } = useTranslation();
  const { pathname } = useLocation();
  const history = useHistory();
  let config = [];
  const [params, setParams, clearParams] = Digit.Hooks.useSessionStorage("EDCR_CREATE", {});
  const [isShowToast, setIsShowToast] = useState(null);
  const [isSubmitBtnDisable, setIsSubmitBtnDisable] = useState(false);
  Digit.SessionStorage.set("EDCR_BACK", "IS_EDCR_BACK");
  
    // Fetching state ID and configuration for EDCR forms
  const stateId = Digit.ULBService.getStateId();
  let { data: newConfig } = Digit.Hooks.obps.SearchMdmsTypes.getFormConfig(stateId, []);
  const generateTransactionNumber = () => {
    return 'TRA' + Math.random().toString(36).substring(2, 9) + Date.now().toString(36);
  };
  
  function handleSelect(key, data, skipStep, index) {
    setIsSubmitBtnDisable(true);
  
    const loggedInuserInfo = Digit.UserService.getUser();
    const userInfo = { uuid: "1c79f77e-e847-4663-98a7-5aee31f185c5", tenantId: "pg.citya" };
    const transactionNumber = generateTransactionNumber();
    const applicantName = data?.applicantName;
    const file = data?.file;
    const tenantId = data?.tenantId?.code;
    const appliactionType = "BUILDING_PLAN_SCRUTINY";
    const applicationSubType = "NEW_CONSTRUCTION";
    
       // Construct EDCR request payload
    const edcrRequest = {
      transactionNumber,
      edcrNumber: "",
      planFile: null,
      tenantId: "pg.citya",
      coreArea: "NO",
      RequestInfo: {
        apiId: "",
        ver: "",
        ts: "",
        action: "",
        did: "",
        authToken: "4d3ee8fc-03dc-49c7-912a-090bc7a2f699",
        key: "",
        msgId: "",
        correlationId: "",
        userInfo: userInfo
      },
      applicantName,
      appliactionType,
      applicationSubType
    };
    let bodyFormData = new FormData();
    bodyFormData.append("edcrRequest", JSON.stringify(edcrRequest));
    bodyFormData.append("planFile", file);
   ;
     // API call to create EDCR request
    Digit.EDCRService.anonymousCreate({ data: bodyFormData }, tenantId)
    
      .then((result, err) => {
      
        setIsSubmitBtnDisable(false);
        if (result?.data?.edcrDetail) {
          setParams(result?.data?.edcrDetail);
          history.replace(
            `${APPLICATION_PATH}/citizen/core/edcr/scrutiny/acknowledgement`,
            { data: result?.data?.edcrDetail }
          );
        }
      })
      .catch((e) => {
        setParams({ data: e?.response?.data?.errorCode ? e?.response?.data?.errorCode : "BPA_INTERNAL_SERVER_ERROR", type: "ERROR" });
        setIsSubmitBtnDisable(false);
        history.replace(
          `${APPLICATION_PATH}/citizen/core/edcr/scrutiny/acknowledgement`
        );
        setIsShowToast({ key: true, label: e?.response?.data?.errorCode ? e?.response?.data?.errorCode : "BPA_INTERNAL_SERVER_ERROR" });
      });
      
  }
 

  const handleSkip = () => { };
  const handleMultiple = () => { };

  const onSuccess = () => {
    sessionStorage.removeItem("CurrentFinancialYear");
    queryClient.invalidateQueries("TL_CREATE_TRADE");
  };
    // Configuring form steps based on state-level configuration
  newConfig = newConfig?.EdcrConfig ? newConfig?.EdcrConfig : newConfigEDCR;
  newConfig.forEach((obj) => {
    config = config.concat(obj.body.filter((a) => !a.hideInCitizen));
  });
  config.indexRoute = "home";
  return (
    <Switch>
      {config.map((routeObj, index) => {
        const { component, texts, inputs, key } = routeObj;
        const Component = typeof component === "string" ? Digit.ComponentRegistryService.getComponent(component) : component;
        return (
          <Route path={`${match.path}/${routeObj.route}`} key={index}>
            <Component config={{ texts, inputs, key }} onSelect={handleSelect} onSkip={handleSkip} t={t} formData={params} onAdd={handleMultiple} isShowToast={isShowToast} isSubmitBtnDisable={isSubmitBtnDisable} setIsShowToast={setIsShowToast}/>
          </Route>
        );
      })}
      <Route path={`${match.path}/acknowledgement`}>
        <EDCRAcknowledgement data={params} onSuccess={onSuccess} />   
      </Route>
      <Route>
      <Redirect to={`${match.path}/${config.indexRoute}`} />  
      </Route>
    </Switch>
  );
};

export default CreateAnonymousEDCR;
