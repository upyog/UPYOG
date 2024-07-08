import { 
  CardLabel, 
  FormStep, 
  Loader, 
  RadioButtons, 
  TextInput, 
  UploadFile,
  LabelFieldPair,
  TextArea,
  SubmitBar, 
  CitizenInfoLabel,
  CardHeader ,
  Toast,
  DatePicker,
  Header,
  CardSectionHeader,
  StatusTable, 
  Row,
  InfoBannerIcon,
  ActionBar,
  Dropdown,
  InfoIcon
} from "@nudmcdgnpm/digit-ui-react-components";
import React, { useEffect, useState } from "react";
import { useHistory, useRouteMatch } from "react-router-dom";
import DisconnectTimeline from "../components/DisconnectTimeline";
import { stringReplaceAll, createPayloadOfWSDisconnection, updatePayloadOfWSDisconnection, convertDateToEpoch ,updatePayloadOfWSRestoration,createPayloadOfWSReconnection} from "../utils";
import { addDays, format } from "date-fns";

const WSRestorationForm = ({ t, config, onSelect, userType }) => {
  let validation = {};
  const stateCode = Digit.ULBService.getStateId();
  const tenantId = Digit.ULBService.getCurrentTenantId();

  const isMobile = window.Digit.Utils.browser.isMobile();
  const applicationData = Digit.SessionStorage.get("WS_DISCONNECTION");
  const history = useHistory();
  const match = useRouteMatch();
  
  const [disconnectionData, setDisconnectionData] = useState({
      type: applicationData.WSDisconnectionForm ? applicationData.WSDisconnectionForm.type : "",
      date: applicationData.WSDisconnectionForm ? applicationData.WSDisconnectionForm.date : "",
      reason: applicationData.WSDisconnectionForm ?  applicationData.WSDisconnectionForm.reason : "",
      documents: applicationData.WSDisconnectionForm ? applicationData.WSDisconnectionForm.documents : []
  });
  const [documents, setDocuments] = useState(applicationData.WSDisconnectionForm ? applicationData.WSDisconnectionForm.documents : []);
  const [error, setError] = useState(null);
  const [disconnectionTypeList, setDisconnectionTypeList] = useState([]);
  const [checkRequiredFields, setCheckRequiredFields] = useState(false);
  const [isEnableLoader, setIsEnableLoader] = useState(false);

  const { isMdmsLoading, data: mdmsData } = Digit.Hooks.ws.useMDMS(stateCode, "ws-services-masters", ["disconnectionType"]);
  const { isLoading: wsDocsLoading, data: wsDocs } =  Digit.Hooks.ws.WSSearchMdmsTypes.useWSServicesMasters(stateCode, "DisconnectionDocuments");
  const {isLoading: slaLoading, data: slaData } = Digit.Hooks.ws.useDisconnectionWorkflow({tenantId});
  const isReSubmit = window.location.href.includes("resubmit");
  const {
    isLoading: creatingWaterApplicationLoading,
    isError: createWaterApplicationError,
    data: createWaterResponse,
    error: createWaterError,
    mutate: waterMutation,
  } = Digit.Hooks.ws.useWaterCreateAPI("WATER");

  const {
    isLoading: updatingWaterApplicationLoading,
    isError: updateWaterApplicationError,
    data: updateWaterResponse,
    error: updateWaterError,
    mutate: waterUpdateMutation,
  } = Digit.Hooks.ws.useWSApplicationActions("WATER");


  const {
    isLoading: creatingSewerageApplicationLoading,
    isError: createSewerageApplicationError,
    data: createSewerageResponse,
    error: createSewerageError,
    mutate: sewerageMutation,
  } = Digit.Hooks.ws.useWaterCreateAPI("SEWERAGE");

  const {
    isLoading: updatingSewerageApplicationLoading,
    isError: updateSewerageApplicationError,
    data: updateSewerageResponse,
    error: updateSewerageError,
    mutate: sewerageUpdateMutation,
  } = Digit.Hooks.ws.useWSApplicationActions("SEWERAGE");


  const closeToastOfError = () => { setError(null); };

  useEffect(() => {
    const oldData = {...disconnectionData};
    oldData['documents'] = documents;
    setDisconnectionData(oldData);
  }, [documents]);
  

  useEffect(() => {
    const disconnectionTypes = mdmsData?.["ws-services-masters"]?.disconnectionType || []; 
    disconnectionTypes?.forEach(data => data.i18nKey = `WS_DISCONNECTIONTYPE_${stringReplaceAll(data?.code?.toUpperCase(), " ", "_")}`);
console.log("disconnectionTypes",disconnectionTypes)
    setDisconnectionTypeList(disconnectionTypes);
  }, [mdmsData]);

  useEffect(() => {
    Digit.SessionStorage.set("WS_DISCONNECTION", {...applicationData, WSDisconnectionForm: disconnectionData});
  }, [disconnectionData]);
  const handleSubmit = () => onSelect(config.key, { WSDisConnectionForm: disconnectionData });

  const handleEmployeeSubmit = () => {
    onSelect(config.key, { WSDisConnectionForm: {...disconnectionData, documents:documents} });
  };


  const onSkip = () => onSelect();

  const filedChange = (val) => {
    const oldData = {...disconnectionData};
    oldData[val.code]=val;
    setDisconnectionData(oldData);
  }

  const onSubmit = async (data) => {
    const appDate= new Date();
    const proposedDate= format(addDays(appDate, slaData?.slaDays), 'yyyy-MM-dd').toString();

    if( convertDateToEpoch(data?.date)  <= convertDateToEpoch(proposedDate)){
      setError({key: "error", message: "PROPOSED_DISCONNECTION_INVALID_DATE"});
      setTimeout(() => {
        setError(false);
      }, 3000);
    }

    else if( disconnectionData?.reason?.value === "" || disconnectionData?.reason === "" || disconnectionData?.date === "" ){
      setError({ warning: true, message: "PLEASE_FILL_MANDATORY_DETAILS" });
      setTimeout(() => {
        setError(false);
      }, 3000);
    }

    else {
      const payload = await createPayloadOfWSReconnection(data, applicationData, applicationData?.applicationData?.serviceType);
      if(payload?.WaterConnection?.water){
        payload.WaterConnection.isdisconnection = false;
        payload.WaterConnection.isDisconnectionTemporary=true;
        payload.WaterConnection["reconnectionReason"] = payload.WaterConnection.disconnectionReason;
        payload.WaterConnection.disconnectionReason ="";
        payload["reconnectRequest"] = true;
        payload.disconnectRequest = false
        if (waterMutation) {
          setIsEnableLoader(true);
          await waterMutation(payload, {
            onError: (error, variables) => {
              setIsEnableLoader(false);
              setError({ key: "error", message: error?.response?.data?.Errors?.[0].message ? error?.response?.data?.Errors?.[0].message : error });
              setTimeout(closeToastOfError, 5000);
            },
            onSuccess: async (data, variables) => {
              let response = await updatePayloadOfWSRestoration(data?.WaterConnection?.[0], "WATER");
              let waterConnectionUpdate = { WaterConnection: response };
              waterConnectionUpdate = {...waterConnectionUpdate, disconnectRequest: false,reconnectRequest:true}
              console.log("response")
              await waterUpdateMutation(waterConnectionUpdate, {
                onError: (error, variables) => {
                  setIsEnableLoader(false);
                  setError({ key: "error", message: error?.response?.data?.Errors?.[0].message ? error?.response?.data?.Errors?.[0].message : error });
                  setTimeout(closeToastOfError, 5000);
                },
                onSuccess: (data, variables) => {
                  Digit.SessionStorage.set("WS_DISCONNECTION", {...applicationData, DisconnectionResponse: data?.WaterConnection?.[0]});
                  history.push(`/digit-ui/employee/ws/ws-restoration-response?applicationNumber=${data?.WaterConnection?.[0]?.applicationNo}`);                
                },
              })
            },
          });
        }
      }
      else if(payload?.SewerageConnection?.sewerage){
        payload.SewerageConnection.isdisconnection = false;
        payload.SewerageConnection["reconnectionReason"] = payload.SewerageConnection.disconnectionReason;
        payload.SewerageConnection.disconnectionReason ="";
        payload.SewerageConnection.isDisconnectionTemporary=true;
        payload["reconnectRequest"] = true;
        payload.disconnectRequest = false
        if (sewerageMutation) {
          setIsEnableLoader(true);
          await sewerageMutation(payload, {

            onError: (error, variables) => {
              setIsEnableLoader(false);
              setError({ key: "error", message: error?.response?.data?.Errors?.[0].message ? error?.response?.data?.Errors?.[0].message : error });
              setTimeout(closeToastOfError, 5000);
            },
            onSuccess: async (data, variables) => {
              let response = await updatePayloadOfWSRestoration(data?.SewerageConnections?.[0], "SEWERAGE");
              let sewerageConnectionUpdate = { SewerageConnection: response };
              sewerageConnectionUpdate = {...sewerageConnectionUpdate, disconnectRequest:false,reconnectRequest:true};
              await sewerageUpdateMutation(sewerageConnectionUpdate, {
                onError: (error, variables) => {
                  setIsEnableLoader(false);
                  setError({ key: "error", message: error?.response?.data?.Errors?.[0].message ? error?.response?.data?.Errors?.[0].message : error });
                  setTimeout(closeToastOfError, 5000);
                },
                onSuccess: (data, variables) => {
                  Digit.SessionStorage.set("WS_DISCONNECTION", {...applicationData, DisconnectionResponse: data?.SewerageConnections?.[0]});
                  history.push(`/digit-ui/employee/ws/ws-restoration-response?applicationNumber=${data?.SewerageConnections?.[0]?.applicationNo}`);              
                },
              })
            },
          });
        }
      }
    }
  
  } ;

  if (isMdmsLoading || wsDocsLoading || isEnableLoader || slaLoading) return <Loader />


if(userType === 'citizen') {
    return (
      <div>
        {userType === "citizen" && (<DisconnectTimeline currentStep={1} />)}
        <FormStep
          config={config}
          onSelect={handleSubmit}
          onSkip={onSkip}
          t={t}       
        >
          
          <div style={{padding:"0px 10px 10px 10px"}}>
          <CardHeader>{ isReSubmit ? t("RESUBMIT_RESTORATION_FORM") : t("WS_APPLICATION_FORM")}</CardHeader>
          <StatusTable>
            <Row key={t("PDF_STATIC_LABEL_CONSUMER_NUMBER_LABEL")} label={`${t("PDF_STATIC_LABEL_CONSUMER_NUMBER_LABEL")}`} text={applicationData?.connectionNo} className="border-none" />
          </StatusTable> 
     
            <CardLabel className="card-label-smaller" style={{display: "inline"}}>
            {t("WS_RESTORATION_PROPOSED_DATE") + "*"}
          </CardLabel>
          <div className="field">
          <DatePicker
            date={disconnectionData?.date}
            onChange={(date) => {
              setDisconnectionData({ ...disconnectionData, date: date });
            }}
          ></DatePicker>
          </div>

            <LabelFieldPair>
              <CardLabel className="card-label-smaller" style={{display: "inline"}}>{t("WS_DISCONNECTION_REASON")+ "*"}</CardLabel>              
                <TextArea
                  isMandatory={false}
                  optionKey="i18nKey"
                  t={t}
                  name={"reason"}
                  value={disconnectionData.reason?.value}
                  onChange={(e) => filedChange({code:"reason" , value:e.target.value})}
                />              
            </LabelFieldPair>
            <SubmitBar
              label={t("CS_COMMON_NEXT")}
              onSubmit={() => {
                const appDate= new Date();
                const proposedDate= format(addDays(appDate, slaData?.slaDays), 'yyyy-MM-dd').toString();
                history.push(match.path.replace("restoration-application", "check"));
                
              }}
              disabled={
                disconnectionData?.reason?.value === "" || disconnectionData?.reason === "" || disconnectionData?.date === ""
                ? true 
                : false}
             />
             {error && <Toast error={error?.key === "error" ? true : false} label={t(error?.message)} onClose={() => setError(null)} />}
          </div>
        </FormStep>
        <CitizenInfoLabel style={{ margin: "0px" }} textStyle={{ color: "#0B0C0C" }} text={t(`WS_DISONNECT_APPL_INFO`)} info={t("CS_COMMON_INFO")} />
      </div>
    );
  }
console.log("applicationData",applicationData)
  return (
    <div style={{ margin: "16px" }}>
    <Header styles={{fontSize: "32px", marginLeft: "18px"}}>{t("WS_WATER_AND_SEWERAGE_RESTORATION")}</Header>
    <FormStep
          config={config}
          onSelect={handleEmployeeSubmit}
          onSkip={onSkip}
          t={t}       
    >
      <div style={{padding:"10px",paddingTop:"20px",marginTop:"10px"}}>
      <CardSectionHeader>{t("CS_TITLE_APPLICATION_DETAILS")}</CardSectionHeader>
      <StatusTable>
        <Row key={t("PDF_STATIC_LABEL_CONSUMER_NUMBER_LABEL")} label={`${t("PDF_STATIC_LABEL_CONSUMER_NUMBER_LABEL")}`} text={applicationData?.applicationData?.connectionNo} className="border-none" />
        <Row key={t("PDF_STATIC_LABEL_TYPE_OF_SERVICE_LABEL")} label={`${t("PDF_STATIC_LABEL_TYPE_OF_SERVICE_LABEL")}`} text={applicationData?.applicationData?.serviceType} className="border-none" />
        <Row key={t("PDF_STATIC_LABEL_PROPERTY_ID_LABEL")} label={`${t("PDF_STATIC_LABEL_PROPERTY_ID_LABEL")}`} text={applicationData?.applicationData?.propertyId} className="border-none" />
      </StatusTable>        
     
          
          <LabelFieldPair>
          <CardLabel style={{ marginTop: "-5px", fontWeight: "700", display: "inline" }} className="card-label-smaller">
            {t("WS_RESTORATION_PROPOSED_DATE")+ "*"} 
            <div className={`tooltip`} style={{position: "absolute", marginLeft: "4px"}}>
            <InfoIcon/>
            <span className="tooltiptext" style={{
                    whiteSpace: Digit.Utils.browser.isMobile() ? "unset" : "nowrap",
                    fontSize: "medium",
                  }}>
                    {t("SHOULD_BE_DATE")+ " " + slaData?.slaDays + " " + t("DAYS_OF_APPLICATION_DATE")}
                  </span>
            </div>
          </CardLabel>
          <div className="field">
          <DatePicker
            date={disconnectionData?.date}
            onChange={(date) => {
              setDisconnectionData({ ...disconnectionData, date: date });
            }}
          ></DatePicker>
          </div>
          
          </LabelFieldPair>
          <LabelFieldPair>
              <CardLabel style={{ marginTop: "-5px", fontWeight: "700", display: "inline" }} className="card-label-smaller">{t("WS_RESTORATION_REASON") + "*"}</CardLabel>              
              <div className="field">
                <TextArea
                  isMandatory={false}
                  optionKey="i18nKey"
                  t={t}
                  name={"reason"}
                  value={disconnectionData.reason?.value}
                  onChange={(e) => filedChange({code:"reason" , value:e.target.value})}
                />  
                </div>            
          </LabelFieldPair>
                  {error && <Toast error={error?.key === "error" ? true : false} label={t(error?.message)} warning={error?.warning} onClose={() => setError(null)} />}
      </div>


    </FormStep>
    <ActionBar style={{ display: "flex", justifyContent: "flex-end", alignItems: "baseline" }}>
          {
            <SubmitBar
              label={t("ACTION_TEST_SUBMIT")}
              onSubmit={() => onSubmit(disconnectionData)}
              style={{ margin: "10px 10px 0px 0px" }}
              // disabled={
              //   wsDocsLoading || documents.length < 2 || disconnectionData?.reason?.value === "" || disconnectionData?.reason === "" || disconnectionData?.date === "" || disconnectionData?.type === ""
              //   ? true 
              //   : false}
            />}
     </ActionBar>
    </div>
  );

};


export default WSRestorationForm;