import React, { useState } from "react";
import { CardLabel, TypeSelectCard } from "@egovernments/digit-ui-react-components";
import { FormStep, RadioOrSelect, RadioButtons } from "@egovernments/digit-ui-react-components";
import Timeline from "../components/TLTimeline";

const SelectStructureType = ({ t, config, onSelect, userType, formData }) => {
  const [TradeStructureSubtype, setTradeStructureSubtype] = useState(formData?.TradeDetails?.TradeStructureSubtype);
  const isEdit = window.location.href.includes("/edit-application/")||window.location.href.includes("renew-trade");
  const menu = [
    { i18nKey: "TL_COMMON_YES", code: "IMMOVABLE" },
    { i18nKey: "TL_COMMON_NO", code: "MOVABLE" },
  ];

  const onSkip = () => onSelect();

  function selectTradeStructureSubtype(value) {
    setTradeStructureSubtype(value);
  }

  function goNext() {
    sessionStorage.setItem("TradeStructureSubtype", TradeStructureSubtype.i18nKey);
    onSelect(config.key, { TradeStructureSubtype });
  }
  return (
    <React.Fragment>
    {window.location.href.includes("/citizen") ? <Timeline /> : null}
    <FormStep t={t} config={config} onSelect={goNext} onSkip={onSkip} isDisabled={!TradeStructureSubtype}>
      
      <CardLabel>Place Of Activity</CardLabel>
      <RadioOrSelect />

      <CardLabel>Nature Of Structure</CardLabel>
      <RadioOrSelect />
      <RadioButtons
        t={t}
        optionsKey="i18nKey"
        isMandatory={config.isMandatory}
        options={menu}
        selectedOption={TradeStructureSubtype}
        onSelect={selectTradeStructureSubtype}
        disabled={isEdit}
      />
    </FormStep>
    </React.Fragment>
  );
};
export default SelectStructureType;
