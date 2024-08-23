import { Card,CardLabel, CardCaption, TextInput, CardHeader, Label, StatusTable, Row, SubmitBar, Loader, FormStep } from "@upyog/digit-ui-react-components";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import Timeline from "../components/Timeline";

const PlotDetails = ({ formData, onSelect, config }) => {
  const { t } = useTranslation();
  const [holdingNumber, setHoldingNumber] = useState("");
  const [boundaryWallLength, setBoundaryWallLength] = useState("");
  const [registrationDetails, setRegistrationDetails] = useState("");
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const checkingFlow = formData?.uiFlow?.flow;
  const state = Digit.ULBService.getStateId();
  let [isNextDisabled,setIsNextDisabled] =useState(true);
  const { data, isLoading } = Digit.Hooks.obps.useScrutinyDetails(state, formData?.data?.scrutinyNumber)
  
  const handleSubmit = (data) => {
    onSelect(config?.key, { ...data });
  }

  const onSkip = () => onSelect();

  if (isLoading) {
    return <Loader />;
  }

function onChange(data){
  if(data.target.name=="boundaryWallLength" && isNaN(parseInt(data.target.value))==false)  {
    setIsNextDisabled(false);
  }
  else if(data.target.name=="boundaryWallLength" && isNaN(parseInt(data.target.value))==true){
    setIsNextDisabled(true);
  }
}
  return (
    <div>
      <Timeline flow= {checkingFlow === "OCBPA" ? "OCBPA" : ""}/>
      <FormStep config={config} onSelect={handleSubmit} childrenAtTheBottom={false} t={t} _defaultValues={formData?.data} onSkip={onSkip} onChange={onChange} isDisabled={isNextDisabled}>
        <StatusTable>
          <Row className="border-none" label={t(`BPA_PLOT_NUMBER_LABEL`)} text={data?.planDetail?.planInformation?.plotNo} />
          <Row className="border-none" label={t(`BPA_KHATHA_NUMBER_LABEL`)} text={data?.planDetail?.planInformation?.khataNo}/>
        </StatusTable>
      </FormStep>
    </div>
  )
};

export default PlotDetails;