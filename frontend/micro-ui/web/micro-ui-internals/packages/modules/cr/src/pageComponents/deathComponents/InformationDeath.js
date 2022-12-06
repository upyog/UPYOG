import React, { useState } from "react";
import { FormStep, CardLabel, TextInput, Dropdown, DatePicker } from "@egovernments/digit-ui-react-components";
import Timeline from "../../components/CRTimeline";
import { useTranslation } from "react-i18next";

const InformationDeath = ({ config, onSelect, userType, formData }) => {
  const stateId = Digit.ULBService.getStateId();
  const { t } = useTranslation();
  let validation = {};
  const { data: place = {}, isLoad } = Digit.Hooks.tl.useTradeLicenseMDMS(stateId, "TradeLicense", "PlaceOfActivity");
  const [setPlaceofActivity, setSelectedPlaceofActivity] = useState(formData?.TradeDetails?.setPlaceofActivity);
  const isEdit = window.location.href.includes("/edit-application/") || window.location.href.includes("renew-trade");
  const [TradeName, setTradeName] = useState(null);
  const [CommencementDate, setCommencementDate] = useState();
  let naturetypecmbvalue = null;
  let cmbPlace = [];
  place &&
    place["TradeLicense"] &&
    place["TradeLicense"].PlaceOfActivity.map((ob) => {
      cmbPlace.push(ob);
    });

  const onSkip = () => onSelect();

  function selectPlaceofactivity(value) {
    naturetypecmbvalue = value.code.substring(0, 4);
    setSelectedPlaceofActivity(value);
  }

  function setSelectTradeName(e) {
    setTradeName(e.target.value);
  }
  function selectCommencementDate(value) {
    setCommencementDate(value);
  }

  const goNext = () => {
    // sessionStorage.setItem("PlaceOfActivity", setPlaceofActivity.code);
    // onSelect(config.key, { setPlaceofActivity });
  };
  return (
    <React.Fragment>
      {window.location.href.includes("/citizen") ? <Timeline /> : null}
      <FormStep t={t} config={config} onSelect={goNext} onSkip={onSkip} isDisabled={!CommencementDate}>
        <header className="tittle">Information Deceased </header>
        <div className="maindeath">
          <div className="maindiv">
            <div className="inner">
              <CardLabel>{t("deceased_fname_en")}</CardLabel>
              <TextInput
                t={t}
                isMandatory={false}
                type={"text"}
                optionKey="i18nKey"
                name="First Name"
                value={TradeName}
                onChange={setSelectTradeName}
                disable={isEdit}
                {...(validation = { pattern: "^[a-zA-Z-.`' ]*$", isRequired: true, type: "text", title: t("TL_INVALID_TRADE_NAME") })}
              />
            </div>
            <div className="inner">
              <CardLabel>{t("deceased_mname_en")}</CardLabel>
              <TextInput
                t={t}
                isMandatory={false}
                type={"text"}
                optionKey="i18nKey"
                name="First Name"
                value={TradeName}
                onChange={setSelectTradeName}
                disable={isEdit}
                {...(validation = { pattern: "^[a-zA-Z-.`' ]*$", isRequired: true, type: "text", title: t("TL_INVALID_TRADE_NAME") })}
              />
            </div>
            <div className="inner">
              <CardLabel>{t("deceased_lname_en")}</CardLabel>
              <TextInput
                t={t}
                isMandatory={false}
                type={"text"}
                optionKey="i18nKey"
                name="First Name"
                value={TradeName}
                onChange={setSelectTradeName}
                disable={isEdit}
                {...(validation = { pattern: "^[a-zA-Z-.`' ]*$", isRequired: true, type: "text", title: t("TL_INVALID_TRADE_NAME") })}
              />
            </div>
            <div className="inner">
              <CardLabel>{t("deceased_fname_ml")}</CardLabel>
              <TextInput
                t={t}
                isMandatory={false}
                type={"text"}
                optionKey="i18nKey"
                name="First Name"
                value={TradeName}
                onChange={setSelectTradeName}
                disable={isEdit}
                {...(validation = { pattern: "^[a-zA-Z-.`' ]*$", isRequired: true, type: "text", title: t("TL_INVALID_TRADE_NAME") })}
              />
            </div>
            <div className="inner">
              <CardLabel>{t("deceased_mname_ml")}</CardLabel>
              <TextInput
                t={t}
                isMandatory={false}
                type={"text"}
                optionKey="i18nKey"
                name="First Name"
                value={TradeName}
                onChange={setSelectTradeName}
                disable={isEdit}
                {...(validation = { pattern: "^[a-zA-Z-.`' ]*$", isRequired: true, type: "text", title: t("TL_INVALID_TRADE_NAME") })}
              />
            </div>
            <div className="inner">
              <CardLabel>{t("deceased_mname_ml")}</CardLabel>
              <TextInput
                t={t}
                isMandatory={false}
                type={"text"}
                optionKey="i18nKey"
                name="First Name"
                value={TradeName}
                onChange={setSelectTradeName}
                disable={isEdit}
                {...(validation = { pattern: "^[a-zA-Z-.`' ]*$", isRequired: true, type: "text", title: t("TL_INVALID_TRADE_NAME") })}
              />
            </div>
            <div className="inner">
              <CardLabel>{t("gender")}</CardLabel>
              <Dropdown
                t={t}
                optionKey="code"
                isMandatory={false}
                option={cmbPlace}
                selected={setPlaceofActivity}
                select={selectPlaceofactivity}
                disabled={isEdit}
              />
            </div>
            <div className="inner">
              <CardLabel>{t("Dob Deceased")}</CardLabel>
              <DatePicker date={CommencementDate} name="CommencementDate" onChange={selectCommencementDate} />
            </div>
            <div className="inner">
              <CardLabel>{t("age age_unit")}</CardLabel>
              <div style={{ display: "flex" }}>
                <TextInput
                  style={{ width: "94%" }}
                  t={t}
                  isMandatory={false}
                  type={"text"}
                  optionKey="i18nKey"
                  name="First Name"
                  value={TradeName}
                  onChange={setSelectTradeName}
                  disable={isEdit}
                  {...(validation = { pattern: "^[a-zA-Z-.`' ]*$", isRequired: true, type: "text", title: t("TL_INVALID_TRADE_NAME") })}
                />

        
                <Dropdown
                  style={{ width: "94%" }}
                  t={t}
                  optionKey="code"
                  isMandatory={false}
                  option={cmbPlace}
                  selected={setPlaceofActivity}
                  select={selectPlaceofactivity}
                  disabled={isEdit}
                />
              </div>
            </div>
            <div className="inner">
              <CardLabel>{t("adhar_no_deceased")}</CardLabel>
              <Dropdown
                t={t}
                optionKey="code"
                isMandatory={false}
                option={cmbPlace}
                selected={setPlaceofActivity}
                select={selectPlaceofactivity}
                disabled={isEdit}
              />
            </div>
            <div className="inner">
              <CardLabel>{t("passportno")}</CardLabel>
              <Dropdown
                t={t}
                optionKey="code"
                isMandatory={false}
                option={cmbPlace}
                selected={setPlaceofActivity}
                select={selectPlaceofactivity}
                disabled={isEdit}
              />
            </div>
            <div className="inner">
              <CardLabel>{t("nationality_deceased")}</CardLabel>
              <Dropdown
                t={t}
                optionKey="code"
                isMandatory={false}
                option={cmbPlace}
                selected={setPlaceofActivity}
                select={selectPlaceofactivity}
                disabled={isEdit}
              />
            </div>
            <div className="inner">
              <CardLabel>{t("religion_decaesedss")}</CardLabel>
              <Dropdown
                t={t}
                optionKey="code"
                isMandatory={false}
                option={cmbPlace}
                selected={setPlaceofActivity}
                select={selectPlaceofactivity}
                disabled={isEdit}
              />
            </div>
          </div>
        </div>
      </FormStep>
    </React.Fragment>
  );
};
export default InformationDeath;
