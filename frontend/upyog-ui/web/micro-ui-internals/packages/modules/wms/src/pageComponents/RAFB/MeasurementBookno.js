import { CardLabel, CitizenInfoLabel, FormStep, Loader, TextInput } from "@egovernments/digit-ui-react-components";
import React, { useState, useEffect } from "react";
import Timeline from "../../components/RAFB/Timeline";
// import { currentFinancialYear } from "../utils";

const MeasurementBookno = ({ t, config, onSelect, value, userType, formData, digitTest="testqwert" }) => {
  console.log("MeasurementBookno config,formData ",{config,formData})
  let validation = {};
  const onSkip = () => onSelect();
  const [MbNo, setMbNo] = useState(formData.mbNotPaid?.mbNumber);
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const stateId = Digit.ULBService.getStateId();
  const isEdit = window.location.href.includes("/edit-application/") || window.location.href.includes("renew-trade");
  const { isLoading, data: fydata = {} } = Digit.Hooks.tl.useTradeLicenseMDMS(stateId, "egf-master", "FinancialYear");

  let mdmsFinancialYear = fydata["egf-master"] ? fydata["egf-master"].FinancialYear.filter(y => y.module === "TL") : [];
  let FY = mdmsFinancialYear && mdmsFinancialYear.length > 0 && mdmsFinancialYear.sort((x, y) => y.endingDate - x.endingDate)[0]?.code;
  function setSelectWorkOrderNo(e) {
    setWorkOrderNo(e.target.value);
  }

  useEffect(() => {
    localStorage.setItem("TLAppSubmitEnabled", "true");
  }, []);

  const goNext = () => {
    onSelect();
    // onSelect(config.key, { MbNo });
  };
  if (isLoading) {
    return <Loader></Loader>
  }

  return (
    <React.Fragment>
      {window.location.href.includes("/citizen") ? <Timeline  currentStep={3} /> : null}
      <FormStep
        config={config}
        onSelect={goNext}
        onSkip={onSkip}
        t={t}
        isDisabled={!MbNo}
      >
        <CardLabel>{`${t("WMS_RUNNING_ACCOUNT_FINAL_BILL_MB_NO")}`}</CardLabel>
        <TextInput
          t={t}
          isMandatory={true}
          type={"text"}
          optionKey="i18nKey"
          name="WorkOrderNumber"
          value={MbNo}
          onChange={setSelectWorkOrderNo}
          // disable={isEdit}
          disable={true}
          {...(validation = { pattern: "^[a-zA-Z-0-9_@/#&+-.`' ]*$", isRequired: true, title: t("TL_INVALID_TRADE_NAME") })}
        />
      </FormStep>
      {<CitizenInfoLabel info={t("CS_FILE_APPLICATION_INFO_LABEL")} text={t("TL_LICENSE_ISSUE_YEAR_INFO_MSG") + FY} />}
    </React.Fragment>
  );
};

export default MeasurementBookno;
