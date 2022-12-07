import React, { useState } from "react";
import { FormStep, CardLabel, TextInput, Dropdown, DatePicker, TextArea } from "@egovernments/digit-ui-react-components";
import Timeline from "../../components/CRTimeline";
import { useTranslation } from "react-i18next";

const BirthPlace = ({ config, onSelect, userType, formData }) => {
  const stateId = Digit.ULBService.getStateId();
  const { t } = useTranslation();
  let validation = {};
  const { data: Menu={} } = Digit.Hooks.cr.useCivilRegistrationMDMS(stateId, "birth-death-service", "PlaceMaster");
  // console.log(Menu["birth-death-service"]);  
  const [BirthPlace, selectBirthPlace] = useState(formData?.BirthPlace?.BirthPlace);
  const [BirthPlaceDescription, setBirthPlaceDeccription] = useState(formData?.BirthPlace?.BirthPlaceDescription);
  const isEdit = window.location.href.includes("/edit-application/") || window.location.href.includes("renew-trade");
  let menu = [];
  // Menu &&
  //   Menu.map((PlaceMaster) => {
  //     menu.push({ i18nKey: `CR_BIRTH_PLACE_${PlaceMaster.code}`, code: `${PlaceMaster.code}` });
  //   });
  Menu &&
  ((ob) => {
    menu.push(ob);
  });
  console.log(menu);  
  const onSkip = () => onSelect();

  function setselectBirthPlace(value) {
    selectBirthPlace(value);
  }
  function setSelectBirthPlaceDeccription(e) {
    setBirthPlaceDeccription(e.target.value);
  }
  const goNext = () => {
    sessionStorage.setItem("BirthPlace", BirthPlace.code);
    sessionStorage.setItem("BirthPlaceDescription", BirthPlaceDescription);
    onSelect(config.key, { BirthPlace, BirthPlaceDescription });
  }
  return (
    <React.Fragment>
      {window.location.href.includes("/employee") ? <Timeline /> : null}
      <FormStep t={t} config={config} onSelect={goNext} onSkip={onSkip} isDisabled={!menu}>
        <div className="row">
          <div className="col-md-12" ><h1 className="headingh1" ><span style={{ background: "#fff", padding: "0 10px" }}>{`${t("CR_BIRTH_PLACE")}`}</span> </h1>
          </div>
        </div>
        <div className="row">
          <div className="col-md-6" >
            <CardLabel>{t("CR_BIRTH_PLACE")}</CardLabel>
            <Dropdown t={t} optionKey="code" isMandatory={false} option={menu} selected={BirthPlace} select={setselectBirthPlace} disabled={isEdit} />
          </div>
        </div>
        <div className="row">
          <div className="col-md-6 " >
            <CardLabel>{`${t("CR_DESCRIPTION")}`}</CardLabel>
            <TextArea t={t} isMandatory={false} type={"text"} optionKey="i18nKey" name="BirthPlaceDescription" value={BirthPlaceDescription} onChange={setSelectBirthPlaceDeccription} disable={isEdit} {...(validation = { pattern: "^[a-zA-Z-.`' ]*$", isRequired: true, type: "text", title: t("TL_INVALID_TRADE_NAME") })} />
          </div>
        </div>

      </FormStep>
    </React.Fragment>
  );
};
export default BirthPlace;