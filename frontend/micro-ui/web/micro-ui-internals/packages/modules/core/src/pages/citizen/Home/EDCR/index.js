import React, { useState } from "react";
import { useTranslation } from "react-i18next";
import { useQueryClient } from "react-query";
import { Redirect, Route, Switch, useHistory, useLocation, useRouteMatch } from "react-router-dom";
import { newConfig as newConfigEDCR} from "../../../../config/edcrConfig";

//import { uuidv4 } from "../../../utils";
import EDCRAcknowledgement1 from "./EDCRAcknowledgement1";

const CreateEDCR1 = ({ parentRoute }) => {
  console.log("hio inside edcr")
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
  
  const stateId = Digit.ULBService.getStateId();
  let { data: newConfig } = Digit.Hooks.obps.SearchMdmsTypes.getFormConfig(stateId, []);
console.log("stateId" + stateId)
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
  
    const edcrRequest = {
      transactionNumber,
      edcrNumber: "",
      planFile: null,
      tenantId: "pg.citya",
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

  console.log("edcrRequest:", edcrRequest);
console.log("file:", file);

    let bodyFormData = new FormData();
    //bodyFormData.set("edcrRequest", JSON.stringify(edcrRequest));
    bodyFormData.append("edcrRequest", JSON.stringify(edcrRequest));
    bodyFormData.append("planFile", file);
    console.log("FormData size:", bodyFormData.size);
    console.log("bodyFormData coreeee== " + bodyFormData);

    Digit.EDCRService.anonymousCreate({ data: bodyFormData }, tenantId)
    
      .then((result, err) => {
        console.log("Result data:", result?.data);
      
        setIsSubmitBtnDisable(false);
        if (result?.data?.edcrDetail) {
          setParams(result?.data?.edcrDetail);
          history.replace(
            `/digit-ui/citizen/core/edcr/scrutiny/acknowledgement`,
            { data: result?.data?.edcrDetail }
          );
        }
      })
      .catch((e) => {
        setParams({ data: e?.response?.data?.errorCode ? e?.response?.data?.errorCode : "BPA_INTERNAL_SERVER_ERROR", type: "ERROR" });
        setIsSubmitBtnDisable(false);
        history.replace(
          `/digit-ui/citizen/core/edcr/scrutiny/acknowledgement`,
        
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
  newConfig = newConfig?.EdcrConfig ? newConfig?.EdcrConfig : newConfigEDCR;
  newConfig.forEach((obj) => {
    config = config.concat(obj.body.filter((a) => !a.hideInCitizen));
  });
  config.indexRoute = "home";

  //const EDCRAcknowledgement1 = Digit?.ComponentRegistryService?.getComponent('EDCRAcknowledgement1');
 // console.log("EDCRAcknowledgement1" , EDCRAcknowledgement1)
  console.log("=====" + match.path)
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
        
        <EDCRAcknowledgement1 data={params} onSuccess={onSuccess} />
       
      </Route>
      <Route>
      <Redirect to={`${match.path}/${config.indexRoute}`} />
        
      </Route>
      
    </Switch>
  );
};

export default CreateEDCR1;
