import { Card,CardLabel, CardCaption, TextInput, CardHeader, Label, StatusTable, Row, SubmitBar, Loader, FormStep } from "@upyog/digit-ui-react-components";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import Timeline from "../components/Timeline";

const PlotDetails = ({ formData, onSelect, config }) => {
  const isEditApplication =  window.location.href.includes("editApplication");
  const[editConfig,setEditConfig]=useState(config);
  const { t } = useTranslation();
  const [boundaryWallLength, setBoundaryWallLength] = useState("");
  const [registrationDetails, setRegistrationDetails] = useState("");
  const [wardnumber, setWardNumber] = useState("");
  const [zonenumber, setZoneNumber] = useState("");
  const [khasraNumber, setkhasraNumber] = useState("");
  const [architectid, setarchitectid] = useState("");
  const [bathnumber, setbathnumber] = useState("");
  const [kitchenNumber, setkitchenNumber] = useState("");
  const [approxinhabitants, setapproxinhabitants] = useState("");
  const [distancefromsewer, setdistancefromsewer] = useState("");
  const [sourceofwater, setsourceofwater] = useState("");
  const [materialused, setmaterialused] = useState("");
  const [materialusedinfloor, setmaterialusedinfloor] = useState("");
  const [materialusedinroofs, setmaterialusedinroofs] = useState("");
  const [propertyuid, setpropertyuid] = useState("");
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const checkingFlow = formData?.uiFlow?.flow;
  const state = Digit.ULBService.getStateId();
  useEffect(() => {
    if(isEditApplication){
      const newConfig={
        ...config,
        inputs:config.inputs.map(input=>{
          if(input.name==="boundaryWallLength"){
            return {...input,disable:true};
          }
          return input;
        })
      };
      setEditConfig(newConfig);
        }
  }, [checkingFlow,isEditApplication]);

  const { data, isLoading } = Digit.Hooks.obps.useScrutinyDetails(state, formData?.data?.scrutinyNumber)
  
  const handleSubmit = (data) => {
    onSelect(editConfig?.key, { ...data });
  }

  const onSkip = () => onSelect();

  if (isLoading) {
    return <Loader />;
  }

  return (
    <div>
      <Timeline flow= {checkingFlow === "OCBPA" ? "OCBPA" : ""}/>
      <FormStep config={editConfig} onSelect={handleSubmit} childrenAtTheBottom={false} t={t} _defaultValues={formData?.additionalDetails} onSkip={onSkip} >
        <StatusTable>
          <Row className="border-none" label={t(`BPA_BOUNDARY_PLOT_AREA_LABEL`)} text={data?.planDetail?.planInformation?.plotArea ? `${data?.planDetail?.planInformation?.plotArea} ${t(`BPA_SQ_MTRS_LABEL`)}` : "NA"} />
          <Row className="border-none" label={t(`BPA_PLOT_NUMBER_LABEL`)} text={data?.planDetail?.planInformation?.plotNo} />
          <Row className="border-none" label={t(`BPA_KHATHA_NUMBER_LABEL`)} text={data?.planDetail?.planInformation?.khataNo}/>
        </StatusTable>
      </FormStep>
    </div>
  )
};

export default PlotDetails;