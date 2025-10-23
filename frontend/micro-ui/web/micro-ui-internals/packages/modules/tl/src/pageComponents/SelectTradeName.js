import { CardLabel, CitizenInfoLabel, FormStep, Loader, TextInput } from "@upyog/digit-ui-react-components";
import React, { useState, useEffect } from "react";
import Timeline from "../components/TLTimeline";
import { currentFinancialYear } from "../utils";

const SelectTradeName = ({ t, config, onSelect, value, userType, formData }) => {
  let validation = {};
  const onSkip = () => onSelect();
  const [TradeName, setTradeName] = useState(formData.TradeDetails?.TradeName);
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const stateId = Digit.ULBService.getStateId();
  const isEdit = window.location.href.includes("/edit-application/") || window.location.href.includes("renew-trade");
  const { isLoading, data: fydata = {} } = Digit.Hooks.tl.useTradeLicenseMDMS(stateId, "egf-master", "FinancialYear");

  let mdmsFinancialYear = fydata["egf-master"] ? fydata["egf-master"].FinancialYear.filter(y => y.module === "TL") : [];
  let FY = mdmsFinancialYear && mdmsFinancialYear.length > 0 && mdmsFinancialYear.sort((x, y) => y.endingDate - x.endingDate)[0]?.code;
  function setSelectTradeName(e) {
    setTradeName(e.target.value);
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
    sessionStorage.setItem("CurrentFinancialYear", currentFinancialYear());
    onSelect(config.key, { TradeName });
  };
  if (isLoading) {
    return <Loader></Loader>
  }

  return (
    <React.Fragment>
      {window.location.href.includes("/citizen") ? <Timeline /> : null}
      <FormStep
        config={config}
        onSelect={goNext}
        onSkip={onSkip}
        t={t}
        isDisabled={!TradeName}
      >
        <CardLabel>{`${t("TL_LOCALIZATION_TRADE_NAME")}`}</CardLabel>
        <TextInput
          t={t}
          isMandatory={false}
          type={"text"}
          optionKey="i18nKey"
          name="TradeName"
          value={TradeName}
          onChange={setSelectTradeName}
          disable={isEdit}
          {...(validation = { pattern: "^[a-zA-Z-0-9_@/#&+-.`' ]*$", isRequired: true, title: t("TL_INVALID_TRADE_NAME") })}
        />
      </FormStep>
      {<CitizenInfoLabel info={t("CS_FILE_APPLICATION_INFO_LABEL")} text={t("TL_LICENSE_ISSUE_YEAR_INFO_MSG") + FY} />}
    </React.Fragment>
  );
};

export default SelectTradeName;
