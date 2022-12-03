import React, { useState } from "react";
import { FormStep, CardLabel, TextInput, Dropdown, DatePicker, NewRadioButton } from "@egovernments/digit-ui-react-components";
// import Timeline from "../components/TLTimeline";
import { useTranslation } from "react-i18next";
import AdressOutside from "./AdressOutside";

const AdressInside = ({ config, onSelect, userType, formData }) => {
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
  return (
    <div className="maindiv">
    <div className="inner">
      <CardLabel>{t("houseno")}</CardLabel>
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
      <CardLabel>{t("res_assc_no")}</CardLabel>
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
      <CardLabel>{t("house_ml")}</CardLabel>
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
      <CardLabel>{t("house_en")}</CardLabel>
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
      <CardLabel>{t("street_ml")}</CardLabel>
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
      <CardLabel>{t("street_en")}</CardLabel>
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
      <CardLabel>{t("locality_ml")}</CardLabel>
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
      <CardLabel>{t("locality_ml")}</CardLabel>
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
      <CardLabel>{t("locality_ml")}</CardLabel>
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
      <CardLabel>{t("cityml")}</CardLabel>
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
      <CardLabel>{t("cityml")}</CardLabel>
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
    </div>
    <div className="inner">
      <CardLabel>{t("ward_id")}</CardLabel>
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
      <CardLabel>{t("taluk_id")}</CardLabel>
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
      <CardLabel>{t("village_id")}</CardLabel>
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
    </div>
    <div className="inner">
      <CardLabel>{t("poid")}</CardLabel>
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
    </div>
  </div>
    );
};
export default AdressInside;