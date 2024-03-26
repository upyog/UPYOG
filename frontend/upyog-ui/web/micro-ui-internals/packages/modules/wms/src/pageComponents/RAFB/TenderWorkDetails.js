import { CardLabel, CitizenInfoLabel, FormStep, Loader, Table, TextInput } from "@egovernments/digit-ui-react-components";
import React, { useState, useEffect } from "react";
import Timeline from "../../components/RAFB/Timeline";
// import { currentFinancialYear } from "../utils";
//delete this(static data) and Fetch data from Tender Entry module and filter   
const TenderWorkDetail={"workName":"ABC","estimatedWorkCost":"1234543","tenderType":"Tender Type A","percentageType":"10","amount":"34567"}

const TenderWorkDetails = ({ t, config, onSelect, value, userType, formData, digitTest="testqwert" }) => {
  console.log("Select Work Order No config,formData ",{config,formData})
  let validation = {};
  const onSkip = () => onSelect();
  // const [tenderDetails, setTenderDetails] = useState({"workName":formData.TenderWorkDetail?.workName,"estimatedWorkCost":formData.TenderWorkDetail?.estimatedWorkCost ,"tenderType": formData.TenderWorkDetail?.tenderType, "percentageType": formData.TenderWorkDetail?.percentageType ,"amount": formData.TenderWorkDetail?.amount}||{"workName":"a","estimatedWorkCost":"","tenderType":"","value":"","amount":""} ); 
  const [tenderDetails, setTenderDetails] = useState({"workName":TenderWorkDetail?.workName,"estimatedWorkCost":TenderWorkDetail?.estimatedWorkCost ,"tenderType": TenderWorkDetail?.tenderType, "percentageType": TenderWorkDetail?.percentageType ,"amount": TenderWorkDetail?.amount}||{"workName":"","estimatedWorkCost":"","tenderType":"","value":"","amount":""} ); 
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const stateId = Digit.ULBService.getStateId();
  const isEdit = window.location.href.includes("/edit-application/") || window.location.href.includes("renew-trade");
  const { isLoading, data: fydata = {} } = Digit.Hooks.tl.useTradeLicenseMDMS(stateId, "egf-master", "FinancialYear");
console.log("tenderDetails ",{"workName":TenderWorkDetail?.workName,"estimatedWorkCost":TenderWorkDetail?.estimatedWorkCost ,"tenderType": TenderWorkDetail?.tenderType, "value": TenderWorkDetail?.value ,"amount": TenderWorkDetail?.amount})
  let mdmsFinancialYear = fydata["egf-master"] ? fydata["egf-master"].FinancialYear.filter(y => y.module === "TL") : [];
  let FY = mdmsFinancialYear && mdmsFinancialYear.length > 0 && mdmsFinancialYear.sort((x, y) => y.endingDate - x.endingDate)[0]?.code;
  function setSelectWorkName(e) {
    setTenderDetails({...tenderDetails,[e.target.name]:e.target.value});
  }

  useEffect(() => {
    localStorage.setItem("TLAppSubmitEnabled", "true");
  }, []);

  const goNext = () => {
    // const getCurrentFinancialYear = () => {
    //   var today = new Date();
    //   var curMonth = today.getMonth();
    //   var fiscalYr = "";
    //   if (curMonth > 3) {
    //     var nextYr1 = (today.getFullYear() + 1).toString();
    //     fiscalYr = today.getFullYear().toString() + "-" + nextYr1;
    //   } else {
    //     var nextYr2 = today.getFullYear().toString();
    //     fiscalYr = (today.getFullYear() - 1).toString() + "-" + nextYr2.slice(-2);
    //   }
    //   return fiscalYr;
    // };

    // sessionStorage.setItem("CurrentFinancialYear", FY);
    // sessionStorage.setItem("CurrentFinancialYear", getCurrentFinancialYear());
    // sessionStorage.setItem("CurrentFinancialYear", currentFinancialYear());
    onSelect(config.key, tenderDetails );
  };
  if (isLoading) {
    return <Loader />
  }

  return (
    <React.Fragment>
      {window.location.href.includes("/citizen") ? <Timeline currentStep={4} /> : null}
      <FormStep
        config={config}
        onSelect={goNext}
        onSkip={onSkip}
        t={t}
        // isDisabled={tenderDetails?.workName}
      >
        <CardLabel>{`${t("WMS_RUNNING_ACCOUNT_FINAL_BILL_TENDER_WORK_NAME")}`}</CardLabel>
        <TextInput
          t={t}
          isMandatory={false}
          type={"text"}
          // optionKey="i18nKey"
          name="workName"
          value={tenderDetails.workName}
          onChange={setSelectWorkName}
          disable={true}
          {...(validation = { pattern: "^[a-zA-Z-0-9_@/#&+-.`' ]*$", isRequired: true, title: t("TL_INVALID_TRADE_NAME") })}
        />
        <CardLabel>{`${t("WMS_RUNNING_ACCOUNT_FINAL_BILL_TENDER_ESTIMATED_WORK_COST")}`}</CardLabel>
        <TextInput
          t={t}
          isMandatory={false}
          type={"text"}
          // optionKey="i18nKey"
          name="estimatedWorkCost"
          value={tenderDetails.estimatedWorkCost}
          onChange={setSelectWorkName}
          disable={true}
          {...(validation = { pattern: "^[a-zA-Z-0-9_@/#&+-.`' ]*$", isRequired: true, title: t("TL_INVALID_TRADE_NAME") })}
        />
        <CardLabel>{`${t("WMS_RUNNING_ACCOUNT_FINAL_BILL_TENDER_TENDER_TYPE")}`}</CardLabel>
        <TextInput
          t={t}
          isMandatory={false}
          type={"text"}
          // optionKey="i18nKey"
          name="tenderType"
          value={tenderDetails.tenderType}
          onChange={setSelectWorkName}
          disable={true}
          {...(validation = { pattern: "^[a-zA-Z-0-9_@/#&+-.`' ]*$", isRequired: true, title: t("TL_INVALID_TRADE_NAME") })}
        />
        <CardLabel>{`${t("WMS_RUNNING_ACCOUNT_FINAL_BILL_TENDER_PERCENTAGE_TYPE")}`}</CardLabel>
        <TextInput
          t={t}
          isMandatory={false}
          type={"text"}
          // optionKey="i18nKey"
          name="percentageType"
          value={tenderDetails.percentageType}
          onChange={setSelectWorkName}
          disable={true}
          {...(validation = { pattern: "^[a-zA-Z-0-9_@/#&+-.`' ]*$", isRequired: true, title: t("TL_INVALID_TRADE_NAME") })}
        />
        <CardLabel>{`${t("WMS_RUNNING_ACCOUNT_FINAL_BILL_TENDER_AMOUNT")}`}</CardLabel>
        <TextInput
          t={t}
          isMandatory={false}
          type={"text"}
          // optionKey="i18nKey"
          name="amount"
          value={tenderDetails.amount}
          onChange={setSelectWorkName}
          disable={true}
          {...(validation = { pattern: "^[a-zA-Z-0-9_@/#&+-.`' ]*$", isRequired: true, title: t("TL_INVALID_TRADE_NAME") })}
        />
      </FormStep>
      {<CitizenInfoLabel info={t("CS_FILE_APPLICATION_INFO_LABEL")} text={t("TL_LICENSE_ISSUE_YEAR_INFO_MSG") + FY} />}
    </React.Fragment>
  );
};

export default TenderWorkDetails;
