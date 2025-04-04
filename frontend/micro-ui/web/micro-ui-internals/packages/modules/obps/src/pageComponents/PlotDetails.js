import { Card,CardLabel, CardCaption, TextInput, CardHeader, Label, StatusTable, Row, SubmitBar, Loader, FormStep } from "@upyog/digit-ui-react-components";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import Timeline from "../components/Timeline";

const PlotDetails = ({ formData, onSelect, config }) => {
  const { t } = useTranslation();
  const [holdingNumber, setHoldingNumber] = useState("");
  const [boundaryWallLength, setBoundaryWallLength] = useState("");
  const [registrationDetails, setRegistrationDetails] = useState("");
  const [plotNo, setPlotNo] = useState(formData?.plotNo||"");
  const [khataNo, setKhataNo] = useState(formData?.khataNo||"");
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const checkingFlow = formData?.uiFlow?.flow;
  const state = Digit.ULBService.getStateId();
  const { data, isLoading } = Digit.Hooks.obps.useScrutinyDetails(state, formData?.data?.scrutinyNumber)
  
  const handleSubmit = (data) => {
    formData.plotNo=plotNo;
    formData.khataNo=khataNo;
    onSelect(config?.key, { ...data });
  }
  const handleInputChange = (setter) => (e) => {
    setter(e.target.value);
  }
  const onSkip = () => onSelect();

  if (isLoading) {
    return <Loader />;
  }


  return (
    <div>
      <Timeline flow= {checkingFlow === "OCBPA" ? "OCBPA" : ""}/>
      <FormStep config={config} onSelect={handleSubmit} childrenAtTheBottom={false} t={t} _defaultValues={formData?.data} onSkip={onSkip}  >
      {formData?.data?.scrutinyNumber?.edcrNumber ? (
        <StatusTable>
          <Row className="border-none" label={t(`BPA_PLOT_NUMBER_LABEL`)} text={data?.planDetail?.planInformation?.plotNo||"NA"} />
          <Row className="border-none" label={t(`BPA_KHATHA_NUMBER_LABEL`)} text={data?.planDetail?.planInformation?.khataNo||"NA"}/>
        </StatusTable>
      ):""}
      {formData?.selectedPlot ? (
        <div>
        <CardLabel>{t("PLOT_NUMBER")}</CardLabel>
          <TextInput
            t={t}
            type="text"
            isMandatory={false}
            value={plotNo}
            onChange={handleInputChange(setPlotNo)}
          />
          <CardLabel>{t("KHATA_NUMBER")}</CardLabel>
          <TextInput
            t={t}
            type="text"
            isMandatory={false}
            value={khataNo}
            onChange={handleInputChange(setKhataNo)}
          />
          </div>
      ):""}
        
        
        
      </FormStep>
    </div>
  )
};

export default PlotDetails;