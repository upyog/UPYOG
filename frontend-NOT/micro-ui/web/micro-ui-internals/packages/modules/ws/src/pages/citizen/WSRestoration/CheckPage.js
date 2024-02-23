import {
    Card, CardHeader, CardSubHeader, CardText,Toast,
    CitizenInfoLabel, LinkButton, Row, StatusTable, SubmitBar, EditIcon, Header, CardSectionHeader, Loader
  } from "@egovernments/digit-ui-react-components";
  import React, { useState } from "react";
  import { useTranslation } from "react-i18next";
  import { useHistory, useRouteMatch, Link } from "react-router-dom";
  import DisconnectTimeline from "../../../components/DisconnectTimeline";
  import WSDocument from "../../../pageComponents/WSDocument";
import { convertDateToEpoch, convertEpochToDate, createPayloadOfWSDisconnection, updatePayloadOfWSRestoration } from "../../../utils";
  
  const CheckPage = () => {
    const { t } = useTranslation();
    const history = useHistory();
    const match = useRouteMatch();
    const value = Digit.SessionStorage.get("WS_DISCONNECTION");
    const [documents, setDocuments] = useState( value.WSDisconnectionForm.documents || []);
    let routeLink = `/digit-ui/citizen/ws/restoration-application`;
    // if(window.location.href.includes("/edit-application/"))
    // routeLink=`/digit-ui/citizen/ws/edit-disconnect-application`
    const [error, setError] = useState(null);
    function routeTo(jumpTo) {
        location.href=jumpTo;
    }
    const [isEnableLoader, setIsEnableLoader] = useState(false);

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
    
    const closeToastOfError = () => { setShowToast(null); };

    const onSubmit = async (data) => {
      const payload = await createPayloadOfWSDisconnection(data, {applicationData: value}, value.serviceType);
      if(payload?.WaterConnection?.water){
        payload.WaterConnection.isdisconnection = false;
        payload.WaterConnection["reconnectionReason"] = payload.WaterConnection.disconnectionReason;
        payload.WaterConnection.disconnectionReason ="";
        payload.WaterConnection.isDisconnectionTemporary=true;
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
              await waterUpdateMutation(waterConnectionUpdate, {
                onError: (error, variables) => {
                  setIsEnableLoader(false);
                  setError({ key: "error", message: error?.response?.data?.Errors?.[0].message ? error?.response?.data?.Errors?.[0].message : error });
                  setTimeout(closeToastOfError, 5000);
                },
                onSuccess: (data, variables) => {
                  Digit.SessionStorage.set("WS_DISCONNECTION", {...value, DisconnectionResponse: data?.WaterConnection?.[0]});
                  history.push(`/digit-ui/citizen/ws/restoration-acknowledge`);
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
              sewerageConnectionUpdate = {...sewerageConnectionUpdate, disconnectRequest: false,reconnectRequest:true};
              await sewerageUpdateMutation(sewerageConnectionUpdate, {
                onError: (error, variables) => {
                  setIsEnableLoader(false);
                  setError({ key: "error", message: error?.response?.data?.Errors?.[0].message ? error?.response?.data?.Errors?.[0].message : error });
                  setTimeout(closeToastOfError, 5000);
                },
                onSuccess: (data, variables) => {
                  Digit.SessionStorage.set("WS_DISCONNECTION", {...value, DisconnectionResponse: data?.SewerageConnections?.[0]});
                  history.push(`/digit-ui/citizen/ws/restoration-acknowledge`);
                },
              })
            },
          });
        }
      }
    } ;

  if(isEnableLoader) {
    return <Loader/>
  }

  return(
    <React.Fragment>
    <Header styles={{fontSize:"32px"}}>{t("WS_COMMON_SUMMARY")}</Header>
    <DisconnectTimeline currentStep={3} />
  
    <Card style={{paddingRight:"16px"}}>
      <div style={{display: "inline"}}>
      <CardHeader styles={{fontSize:"28px"}}>{t("WS_RESTORATION_APPLICATION_DETAILS")}</CardHeader>
      <LinkButton
        label={<EditIcon style={{ marginTop: "-20px", float: "right", position: "relative", bottom: "32px" }} />}
        style={{ width: "100px", display:"inline" }}
        onClick={() => routeTo(`${routeLink}/application-form`)}
      />
      </div>
      <StatusTable>
        <Row className="border-none" label={t("WS_RESTORATION_CONSUMER_NUMBER")} text={value.connectionNo}/>
        <Row className="border-none" label={t("WS_RESTORATION_PROPOSED_DATE")} text={convertEpochToDate(convertDateToEpoch(value.WSDisconnectionForm.date))}/>
        <Row className="border-none" label={t("WS_RESTORATION_REASON")} text={value.WSDisconnectionForm.reason.value}/>         
      </StatusTable>
    </Card>
 
    <Card style={{paddingRight:"16px"}}>
   
        <SubmitBar label={t("CS_COMMON_SUBMIT")} onSubmit={() => onSubmit(value?.WSDisconnectionForm)} />
      </Card>
      {error && <Toast error={error?.key === "error" ? true : false} label={t(error?.message)} onClose={() => setError(null)} />}
    </React.Fragment>
    )
  }
  export default CheckPage;
  