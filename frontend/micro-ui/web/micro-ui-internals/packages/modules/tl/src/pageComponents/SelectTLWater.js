import { CardLabel, CitizenInfoLabel, FormStep, Loader, TextInput } from "@egovernments/digit-ui-react-components";
import React, { useState } from "react";
import Timeline from "../components/TLTimeline";

const SelectTLWater = ({ t, config, onSelect, value, userType, formData }) => {
  let validation = {};
  const onSkip = () => onSelect();
  const [WaterDet, setWater] = useState(formData.TradeDetails?.VechicleNo);
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const stateId = Digit.ULBService.getStateId();
  const isEdit = window.location.href.includes("/edit-application/") || window.location.href.includes("renew-trade");
  
  function setSelectWaterDet(e) {
    setWater(e.target.value);
  }

  const goNext = () => {
    sessionStorage.setItem("WaterDet", WaterDet);
    onSelect(config.key, { WaterDet });
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
        isDisabled={!WaterDet}
      >
        <CardLabel>Water Details</CardLabel>
        {/* {`${t("TL_LOCALIZATION_TRADE_NAME")}`} */}
        <TextInput
          t={t}
          isMandatory={false}
          type={"text"}
          optionKey="i18nKey"
          name="WaterDet"
          value={WaterDet}
          onChange={setSelectWaterDet}
          disable={isEdit}
          {...(validation = { pattern: "^[a-zA-Z-.0-9`' ]*$", isRequired: true, type: "text", title: t("TL_INVALID_TRADE_NAME") })}
        />
      </FormStep>
      {<CitizenInfoLabel info={t("CS_FILE_APPLICATION_INFO_LABEL")} text={t("TL_LICENSE_ISSUE_YEAR_INFO_MSG") + FY} />}
    </React.Fragment>
  );
};

export default SelectTLWater;
