import React, { useEffect, useState } from "react";
import { fromUnixTime, format } from "date-fns";
import {
  Card,
  CardHeader,
  Label,
  SearchIconSvg,
  Toast,
  StatusTable,
  TextInput,
  Row,
  CardCaption,
  SubmitBar,
  Loader,
} from "@upyog/digit-ui-react-components";
import Timeline from "../components/Timeline";
import { useTranslation } from "react-i18next";
import { scrutinyDetailsData } from "../utils";

const BasicDetails = ({ formData, onSelect, config }) => {
  const [showToast, setShowToast] = useState(null);
  const [basicData, setBasicData] = useState(formData?.selectedPlot||formData?.data?.edcrDetails);
  const checkingFlow = formData?.uiFlow?.flow ? formData?.uiFlow?.flow :formData?.selectedPlot ? "PRE_APPROVE":"";
  
  const [scrutinyNumber, setScrutinyNumber] = useState(formData?.data?.scrutinyNumber || formData?.selectedPlot?.drawingNo);
  const [isDisabled, setIsDisabled] = useState(formData?.data?.scrutinyNumber || formData?.selectedPlot?.drawingNo ? true : false);
  const { t } = useTranslation();
  const stateCode = Digit.ULBService.getStateId();
  const isMobile = window.Digit.Utils.browser.isMobile();
  const { isMdmsLoading, data: mdmsData } = Digit.Hooks.obps.useMDMS(stateCode, "BPA", ["RiskTypeComputation"]);
  const riskType = Digit.Utils.obps.calculateRiskType(
    mdmsData?.BPA?.RiskTypeComputation,
    basicData?.planDetail?.plot?.area,
    basicData?.planDetail?.blocks
  ) || "LOW";
  let user = Digit.SessionStorage.get("User")?.info?.name;
  

  const handleKeyPress = async (event) => {
    if (event.key === "Enter") {
      if (!scrutinyNumber?.edcrNumber) return;
      const details = await scrutinyDetailsData(scrutinyNumber?.edcrNumber, stateCode);
      if (details?.type == "ERROR") {
        setShowToast({ message: details?.message });
        setBasicData(null);
      }
      if (details?.edcrNumber) {
        setBasicData(details);
        setShowToast(null);
      }
    }
  };

  const closeToast = () => {
    setShowToast(null);
  };

  const handleSearch = async (event) => {
    const details = await scrutinyDetailsData(scrutinyNumber?.edcrNumber, stateCode);
    if (details?.type == "ERROR") {
      setShowToast({ message: details?.message });
      setBasicData(null);
    }
    if (details?.edcrNumber) {
      setBasicData(details);
      setShowToast(null);
    }
  };

  const handleSubmit = (event) => {
    onSelect(config?.key, {
      scrutinyNumber,
      applicantName: basicData?.planDetail?.planInformation?.applicantName || user,
      occupancyType: basicData?.planDetail?.planInformation?.occupancy || basicData?.drawingDetail?.occupancy,
      applicationType: basicData?.drawingDetail?.applicationType||basicData?.appliactionType,
      serviceType: basicData?.applicationSubType || basicData?.drawingDetail?.serviceType,
      applicationDate: basicData?.applicationDate||format(new Date(), "dd/MM/yyyy"),
      riskType: Digit.Utils.obps.calculateRiskType(
        mdmsData?.BPA?.RiskTypeComputation,
        basicData?.planDetail?.plot?.area,
        basicData?.planDetail?.blocks
      ) || "LOW",
      edcrDetails: basicData,
    });
  };

  let disableVlaue = sessionStorage.getItem("isEDCRDisable");
  disableVlaue = disableVlaue?JSON.parse(disableVlaue):true;

  const getDetails = async () => {
    const details = await scrutinyDetailsData(scrutinyNumber?.edcrNumber||scrutinyNumber, stateCode);
    if (details?.type == "ERROR") {
      setShowToast({ message: details?.message });
      setBasicData(null);
    }
    if (details?.edcrNumber) {
      setBasicData(details||formData?.selectedPlot);
      setShowToast(null);
    }
  };

  if (disableVlaue) {
    let edcrApi = sessionStorage.getItem("isEDCRAPIType");
    edcrApi = edcrApi ? JSON.parse(edcrApi) : false;
    if (!edcrApi || !basicData) {
      sessionStorage.setItem("isEDCRAPIType", JSON.stringify(true));
      getDetails();
    }
  }

  return (
    <div>
      {showToast && <Toast error={true} label={t(`${showToast?.message}`)} onClose={closeToast} isDleteBtn={true} />}
      <Timeline currentStep={checkingFlow==="PRE_APPROVE" ? 2 : 1 } flow={checkingFlow}/>
      <div className={isMobile ? "obps-search" : ""} style={!isMobile ? { margin: "8px" } : {}}>
        <Label>{scrutinyNumber?.edcrNumber && !scrutinyNumber?.edcrNumber.includes("PAP") ? t(`OBPS_SEARCH_EDCR_NUMBER`) : t("DRAWING_NUMBER")}</Label>
        <TextInput
          className="searchInput"
          onKeyPress={handleKeyPress}
          onChange={event => setScrutinyNumber({ edcrNumber: event.target.value||formData?.selectedPlot?.drawingNo })} 
          value={scrutinyNumber?.edcrNumber || scrutinyNumber} 
          signature={true} 
          signatureImg={!disableVlaue && !formData?.selectedPlot && <SearchIconSvg className="signature-img" onClick={!disableVlaue && scrutinyNumber?.edcrNumber ? () => handleSearch() : null} />}
          disable={disableVlaue}
          style={{ marginBottom: "10px" }}
        />
      </div>
      {basicData && (
        <Card>
          <CardCaption>{scrutinyNumber?.edcrNumber && !scrutinyNumber?.edcrNumber.includes("PAP")?t(`BPA_SCRUTINY_DETAILS`):null}</CardCaption>
          <CardHeader>{t(`BPA_BASIC_DETAILS_TITLE`)}</CardHeader>
          <StatusTable>
            <Row
              className="border-none"
              label={t(`BPA_BASIC_DETAILS_APP_DATE_LABEL`)}
              text={basicData?.applicationDate ? format(new Date(basicData?.applicationDate), "dd/MM/yyyy") : format(new Date(), "dd/MM/yyyy")}
            />
            <Row className="border-none" label={t(`BPA_BASIC_DETAILS_APPLICATION_TYPE_LABEL`)} text={t(basicData?.drawingDetail?.applicationType || `WF_BPA_${basicData?.appliactionType}`)} />
            <Row className="border-none" label={t(`BPA_BASIC_DETAILS_SERVICE_TYPE_LABEL`)} text={t(basicData?.applicationSubType || basicData?.drawingDetail?.serviceType)} />
            <Row className="border-none" label={t(`BPA_BASIC_DETAILS_OCCUPANCY_LABEL`)} text={basicData?.planDetail?.planInformation?.occupancy || basicData?.drawingDetail?.occupancy} />
            <Row className="border-none" label={t(`BPA_BASIC_DETAILS_RISK_TYPE_LABEL`)} text={t(`WF_BPA_${riskType}`|| "Low")} />
            <Row
              className="border-none"
              label={t(`BPA_BASIC_DETAILS_APPLICATION_NAME_LABEL`)}
              text={basicData?.planDetail?.planInformation?.applicantName || user}
            />
          </StatusTable>
          {riskType ? <SubmitBar label={t(`CS_COMMON_NEXT`)} onSubmit={handleSubmit} disabled={!scrutinyNumber} /> : <Loader />}
        </Card>
      )}
    </div>
  );
};

export default BasicDetails;
