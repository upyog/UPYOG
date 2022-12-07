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
  const { data: taluk = {}, istalukLoad } = Digit.Hooks.cr.useCivilRegistrationMDMS(stateId, "common-masters", "mtaluk");
  const [setPlaceofActivity, setSelectedPlaceofActivity] = useState(formData?.TradeDetails?.setPlaceofActivity);
  const [setTaluk, setSelectTaluk] = useState(formData?.DeathDetails?.setTaluk);

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
  let cmbtaluk = [];
  taluk &&
    taluk["common-masters"] &&
    taluk["common-masters"].mtaluk.map((ob) => {
      cmbtaluk.push(ob);
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
    sessionStorage.setItem("PlaceOfActivity", setPlaceofActivity.code);
    onSelect(config.key, { setPlaceofActivity });
  };
  const [check, setCheck] = useState(false);
  const [state, setState] = useState({
    houseNo: "",
    residenNo: "",
    houseMl: "",
    houseEn: "",
    streetMl: "",
    streetEn: "",
    localityml: "",
    localityml1: "",
    citymMl: "",
    cityEn: "",
    wardId: "",
  });
  const onChange = (e) => {
    const { name, value } = e.target;
    setState((prevState) => ({
      ...prevState,
      [name]: value,
    }));
  };

  return (
    <React.Fragment>
      {window.location.href.includes("/citizen") ? <Timeline /> : null}
      <FormStep t={t} config={config} onSelect={goNext} onSkip={onSkip} isDisabled={!CommencementDate}>
        <div className="maindiv">
          <div className="inner">
            <CardLabel>{t("houseno")}</CardLabel>
            <TextInput
              t={t}
              isMandatory={false}
              type={"text"}
              optionKey="i18nKey"
              name="houseNo"
              value={TradeName}
              onChange={onChange}
              disable={isEdit}
              autoComplete="{false}"
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
              name="residenNo"
              value={TradeName}
              onChange={onChange}
              autoComplete="{false}"
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
              name="houseMl"
              value={TradeName}
              onChange={onChange}
              autoComplete="{false}"
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
              name="houseEn"
              value={TradeName}
              onChange={onChange}
              autoComplete="{false}"
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
              name="streetMl"
              value={TradeName}
              autoComplete="{false}"
              onChange={onChange}
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
              name="streetEn"
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
              name="localityml"
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
              onChange={onChange}
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
              name="localityml1"
              value={TradeName}
              onChange={onChange}
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
              name="citymMl"
              value={TradeName}
              onChange={onChange}
              disable={isEdit}
              {...(validation = { pattern: "^[a-zA-Z-.`' ]*$", isRequired: true, type: "text", title: t("TL_INVALID_TRADE_NAME") })}
            />
          </div>
          <div className="inner">
            <CardLabel>{t("cityEn")}</CardLabel>
            <TextInput
              style={{ width: "94%" }}
              t={t}
              isMandatory={false}
              type={"text"}
              optionKey="i18nKey"
              name="cityEn"
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
              onChange={onchange}
              selected={setPlaceofActivity}
              select={selectPlaceofactivity}
              disabled={isEdit}
              name="wardId"
            />
          </div>
          <div className="inner">
            <CardLabel>{t("taluk_id")}</CardLabel>
            <Dropdown t={t} optionKey="name" isMandatory={false} option={cmbtaluk} selected={setTaluk} select={setSelectTaluk} disabled={isEdit} />
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
      </FormStep>
      {/* /////////////////////////////////////////////////////////////////// */}
      <div className="maindiv">
        <hr className="aligncss"></hr>
        <div className="check">
          <label htmlFor="checkbox">Same as Above</label>
          <input type="checkbox" value="false" name="checkbox" onChange={() => setCheck(!check)} />
          <div className="inner">
            <CardLabel>{t("houseno")}</CardLabel>
            <TextInput
              t={t}
              isMandatory={false}
              type={"text"}
              optionKey="i18nKey"
              name="houseNo"
              value={check ? state.houseNo : ""}
              // value={TradeName}
              onChange={onChange}
              disable={isEdit}
              {...(validation = { pattern: "^[a-zA-Z-.`' ]*$", isRequired: true, type: "text", title: t("TL_INVALID_TRADE_NAME") })}
            />
          </div>
          <div className="inner">
            <CardLabel>{t("poid")}</CardLabel>
            <TextInput
              t={t}
              isMandatory={false}
              type={"text"}
              optionKey="i18nKey"
              name="residenNo"
              value={check ? state.residenNo : ""}
              // value={TradeName}
              onChange={onChange}
              disable={isEdit}
              {...(validation = { pattern: "^[a-zA-Z-.`' ]*$", isRequired: true, type: "text", title: t("TL_INVALID_TRADE_NAME") })}
            />
          </div>
          <div className="inner">
            <CardLabel>{t("poid")}</CardLabel>
            <TextInput
              t={t}
              isMandatory={false}
              type={"text"}
              optionKey="i18nKey"
              name="houseMl"
              value={check ? state.houseMl : ""}
              // value={TradeName}
              onChange={onChange}
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
              name="houseEn"
              value={check ? state.houseEn : ""}
              // value={TradeName}
              onChange={onChange}
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
              name="streetMl"
              value={check ? state.streetMl : ""}
              autoComplete="{false}"
              onChange={onChange}
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
              name="streetEn"
              value={check ? state.streetEn : ""}
              onChange={onChange}
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
              name="localityml"
              option={cmbPlace}
              selected={setPlaceofActivity}
              select={selectPlaceofactivity}
              disabled={isEdit}
              value={check ? state.localityml : ""}
            />
          </div>
          <div className="inner">
            <CardLabel>{t("locality_ml")}</CardLabel>
            <TextInput
              t={t}
              isMandatory={false}
              type={"text"}
              optionKey="i18nKey"
              name="localityml1"
              value={check ? state.localityml1 : ""}
              onChange={onChange}
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
              name="citymMl"
              value={check ? state.citymMl : ""}
              onChange={onChange}
              disable={isEdit}
              {...(validation = { pattern: "^[a-zA-Z-.`' ]*$", isRequired: true, type: "text", title: t("TL_INVALID_TRADE_NAME") })}
            />
          </div>
          <div className="inner">
            <CardLabel>{t("cityen")}</CardLabel>
            <TextInput
              t={t}
              isMandatory={false}
              type={"text"}
              optionKey="i18nKey"
              name="cityEn"
              value={check ? state.cityEn : ""}
              onChange={onChange}
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
              onChange={onChange}
              disabled={isEdit}
              name="wardId"
              value={check ? state.wardId : ""}
            />
          </div>
        </div>
      </div>
    </React.Fragment>
  );
};
export default AdressInside;
