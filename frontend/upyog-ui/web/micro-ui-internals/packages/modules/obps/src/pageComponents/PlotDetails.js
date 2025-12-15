import { Card,CardLabel, CardCaption, TextInput, CardHeader, TextArea, Label, StatusTable, Row, SubmitBar, Loader, FormStep } from "@upyog/digit-ui-react-components";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import Timeline from "../components/Timeline";

const PlotDetails = ({ formData, onSelect, config }) => {
  const { t } = useTranslation();
  const [holdingNumber, setHoldingNumber] = useState(formData?.holdingNumber||formData?.additionalDetails?.holdingNo||"");
  const [boundaryWallLength, setBoundaryWallLength] = useState("");
  const [registrationDetails, setRegistrationDetails] = useState(formData?.registrationDetails||formData?.additionalDetails?.registrationDetails||"");
  const [plotNo, setPlotNo] = useState(formData?.plotNo||formData?.additionalDetails?.plotNo||"");
  const [khataNo, setKhataNo] = useState(formData?.khataNo||formData?.additionalDetails?.khataNo||"");
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const checkingFlow = formData?.uiFlow?.flow ? formData?.uiFlow?.flow :formData?.selectedPlot||formData?.businessService==="BPA-PAP" ? "PRE_APPROVE":"";
  const state = Digit.ULBService.getStateId();
  const { data, isLoading } = Digit.Hooks.obps.useScrutinyDetails(state, formData?.data?.scrutinyNumber, {
    enabled:formData?.data?.scrutinyNumber.length!==8?true:false
  })
  
  const handleSubmit = (data) => {
    formData.plotNo=plotNo;
    formData.khataNo=khataNo;
    formData.holdingNumber=holdingNumber;
    formData.registrationDetails=registrationDetails;
    onSelect(config?.key, { ...data });
  }
  function selectRegistrationDetails(e) {
    setRegistrationDetails(e.target.value);
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
      <Timeline currentStep={checkingFlow==="PRE_APPROVE" ? 3 : 1 } flow={checkingFlow}/>
      {formData?.selectedPlot||formData?.businessService==="BPA-PAP" ? (
        <FormStep config={config} onSelect={handleSubmit} childrenAtTheBottom={false} t={t}  isDisabled={plotNo===""||khataNo===""||holdingNumber===""}>
        <div>
        <CardLabel>{t("PLOT_NUMBER")}<span className="check-page-link-button"> *</span></CardLabel>
          <TextInput
            t={t}
            type="text"
            isMandatory={false}
            value={plotNo}
            onChange={handleInputChange(setPlotNo)}
          />
          <CardLabel>{t("KHATA_NUMBER")}<span className="check-page-link-button"> *</span></CardLabel>
          <TextInput
            t={t}
            type="text"
            isMandatory={false}
            value={khataNo}
            onChange={handleInputChange(setKhataNo)}
          />
          <CardLabel>{t("BPA_HOLDING_NUMBER_LABEL")}<span className="check-page-link-button"> *</span></CardLabel>
           <TextInput
            t={t}
            type="text"
            isMandatory={false}
            value={holdingNumber}
            onChange={handleInputChange(setHoldingNumber)}
          />
          <CardLabel>{t("BPA_BOUNDARY_LAND_REG_DETAIL_LABEL")}</CardLabel>
          <TextArea
            t={t}
            isMandatory={false}
            type={"text"}
            optionKey="i18nKey"
            name="RegistrationDetails"
            onChange={selectRegistrationDetails}
            value={registrationDetails}
          />
          </div>
     
        
        
        
      </FormStep>
      ):
      <FormStep config={config} onSelect={handleSubmit} childrenAtTheBottom={false} t={t} _defaultValues={formData?.data} onSkip={onSkip}  >
        <StatusTable>
          <Row className="border-none" label={t(`BPA_PLOT_NUMBER_LABEL`)} text={data?.planDetail?.planInformation?.plotNo||"NA"} />
          <Row className="border-none" label={t(`BPA_KHATHA_NUMBER_LABEL`)} text={data?.planDetail?.planInformation?.khataNo||"NA"}/>
        </StatusTable>
        </FormStep>
      }
    </div>
  )
};

export default PlotDetails;