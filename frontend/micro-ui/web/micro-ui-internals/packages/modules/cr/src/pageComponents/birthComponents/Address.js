import React, { useState } from "react";
import { FormStep, CardLabel, TextInput, Dropdown, DatePicker, TextArea } from "@egovernments/digit-ui-react-components";
import Timeline from "../../components/CRTimeline";
import { useTranslation } from "react-i18next";

const Address = ({ config, onSelect, userType, formData }) => {
    const stateId = Digit.ULBService.getStateId();
    const { t } = useTranslation();
    let validation = {};
    const { data: place = {}, isLoad } = Digit.Hooks.tl.useTradeLicenseMDMS(stateId, "TradeLicense", "PlaceOfActivity");
    const [setPlaceofActivity, setSelectedPlaceofActivity] = useState(formData?.TradeDetails?.setPlaceofActivity);
    const isEdit = window.location.href.includes("/edit-application/") || window.location.href.includes("renew-trade");
    const [PresentBuldingNo, setPresentBuldingNo] = useState(formData?.AddressDetails?.PresentBuldingNo);
    const [PresentHouseNo, setPresentHouseNo] = useState(formData?.AddressDetails?.PresentHouseNo);
    const [PresentLocalityNameEn, setPresentLocalityNameEn] = useState(formData?.AddressDetails?.PresentLocalityNameEn);
    const [PresentLocalityNameMl, setPresentLocalityNameMl] = useState(formData?.AddressDetails?.PresentLocalityNameMl);
    const [PresentCityNameEn, setPresentCityNameEn] = useState(formData?.AddressDetails?.PresentCityNameEn);
    const [PresentCityNameMl, setPresentCityNameMl] = useState(formData?.AddressDetails?.PresentCityNameMl);
    const [PresentVillage, setPresentVillage] = useState(formData?.AddressDetails?.PresentVillage);
    const [PresentLBName, setPresentLBName] = useState(formData?.AddressDetails?.PresentLBName); 
    const [PresentDistrict , setPresentDistrict] = useState(formData?.AddressDetails?.PresentLBName); 
    const [PresentTaluk, setPresentTaluk] = useState(formData?.AddressDetails?.PresentTaluk);
    const [PresentPostOffice, setPresentPostOffice] = useState(formData?.AddressDetails?.PresentPostOffice);
    const [PresentPincode, setPresentPincode] = useState(formData?.AddressDetails?.PresentPincode);

    const [PermanentBuldingNo, setPermanentBuldingNo] = useState(formData?.AddressDetails?.PermanentBuldingNo);
    const [PermanentHouseNo, setPermanentHouseNo] = useState(formData?.AddressDetails?.PermanentHouseNo);
    const [PermanentLocalityNameEn, setPermanentLocalityNameEn] = useState(formData?.AddressDetails?.PermanentLocalityNameEn);
    const [PermanentLocalityNameMl, setPermanentLocalityNameMl] = useState(formData?.AddressDetails?.PermanentLocalityNameMl);
    const [PermanentCityNameEn, setPermanentCityNameEn] = useState(formData?.AddressDetails?.PermanentCityNameEn);
    const [PermanentCityNameMl, setPermanentCityNameMl] = useState(formData?.AddressDetails?.PermanentCityNameMl);
    const [PermanentVillage, setPermanentVillage] = useState(formData?.AddressDetails?.PermanentVillage);
    const [PermanentLBName, setPermanentLBName] = useState(formData?.AddressDetails?.PermanentLBName); 
    const [PermanentDistrict , setPermanentDistrict] = useState(formData?.AddressDetails?.PermanentDistrict); 
    const [PermanentTaluk, setPermanentTaluk] = useState(formData?.AddressDetails?.PermanentTaluk);
    const [PermanentPostOffice, setPermanentPostOffice] = useState(formData?.AddressDetails?.PermanentPostOffice);
    const [PermanentPincode, setPermanentPincode] = useState(formData?.AddressDetails?.PermanentPincode);

    let cmbPlace = [];
    place &&
        place["TradeLicense"] &&
        place["TradeLicense"].PlaceOfActivity.map((ob) => {
            cmbPlace.push(ob);
        });

    const onSkip = () => onSelect();

    function setSelectPresentBuldingNo(e) {
        setPresentBuldingNo(e.target.value);
    }
    function setSelectPresentHouseNo(value) {
        setPresentHouseNo(value);
    }
    function setSelectPresentLocalityNameEn(e) {
        setPresentLocalityNameEn(e.target.value);
    }
    function setSelectPresentLocalityNameMl(value) {
        setPresentLocalityNameMl(value);
    }
    function setSelectPresentCityNameEn(e) {
        setPresentCityNameEn(e.target.value);
    }
    function setSelectPresentCityNameMl(value) {
        setPresentCityNameMl(value);
    }
    function setSelectPresentVillage(e) {
        setPresentVillage(e.target.value);
    }
    function setSelectPresentLBName(value) {
        setPresentLBName(value);
    }
    function setSelectPresentTaluk(value) {
        setPresentTaluk(value);
    }
    function setSelectPresentDistrict(value) {
        setPresentDistrict(value);
    }
    function setSelectPresentPostOffice(value) {
        setPresentPostOffice(value);
    }
    function setSelectPresentPincode(e) {
        setPresentPincode(e.target.value);
    }
    //Permanent Address Function
    function setSelectPermanentBuldingNo(e) {
        setPermanentBuldingNo(e.target.value);
    }
    function setSelectPermanentHouseNo(value) {
        setPermanentHouseNo(value);
    }
    function setSelectPermanentLocalityNameEn(e) {
        setPermanentLocalityNameEn(e.target.value);
    }
    function setSelectPermanentLocalityNameMl(value) {
        setPermanentLocalityNameMl(value);
    }
    function setSelectPermanentCityNameEn(e) {
        setPermanentCityNameEn(e.target.value);
    }
    function setSelectPermanentCityNameMl(value) {
        setPermanentCityNameMl(value);
    }
    function setSelectPermanentVillage(e) {
        setPermanentVillage(e.target.value);
    }
    function setSelectPermanentLBName(value) {
        setPermanentLBName(value);
    }
    function setSelectPermanentTaluk(value) {
        setPermanentTaluk(value);
    }
    function setSelectPermanentDistrict(value) {
        setPermanentDistrict(value);
    }
    function setSelectPermanentPostOffice(value) {
        setPermanentPostOffice(value);
    }
    function setSelectPermanentPincode(e) {
        setPermanentPincode(e.target.value);
    }
    const goNext = () => {
        sessionStorage.setItem("PlaceOfActivity", setPlaceofActivity.code);
        onSelect(config.key, { setPlaceofActivity });
    }
    return (
        <React.Fragment>
            {window.location.href.includes("/employee") ? <Timeline /> : null}
            <FormStep t={t} config={config} onSelect={goNext} onSkip={onSkip} isDisabled={!PresentBuldingNo}>
                <div className="row">
                    <div className="col-md-12" ><h1 className="headingh1" ><span style={{ background: "#fff", padding: "0 10px" }}>{`${t("CR_PRESENT_ADDRESS")}`}</span> </h1>
                    </div>
                </div>
                <div className="row">
                    <div className="col-md-6" ><CardLabel>{t("CR_BUILDING_NO")}</CardLabel>
                        <TextInput t={t} isMandatory={false} type={"text"} optionKey="i18nKey" name="PresentBuldingNo" value={PresentBuldingNo} onChange={setSelectPresentBuldingNo} disable={isEdit}    {...(validation = { pattern: "^[a-zA-Z-.`' ]*$", isRequired: true, type: "text", title: t("TL_INVALID_TRADE_NAME") })} />
                    </div>
                    <div className="col-md-6" ><CardLabel>{t("CR_HOUSE_NO")}</CardLabel>
                        <TextInput t={t} isMandatory={false} type={"text"} optionKey="i18nKey" name="PresentHouseNo" value={PresentHouseNo} onChange={setSelectPresentHouseNo} disable={isEdit}    {...(validation = { pattern: "^[a-zA-Z-.`' ]*$", isRequired: true, type: "text", title: t("TL_INVALID_TRADE_NAME") })} />
                    </div>
                </div>
                <div className="row">
                    <div className="col-md-6" ><CardLabel>{t("CR_LOCALITY_EN")}</CardLabel>
                        <TextInput t={t} isMandatory={false} type={"text"} optionKey="i18nKey" name="PresentLocalityNameEn" value={PresentLocalityNameEn} onChange={setSelectPresentLocalityNameEn} disable={isEdit}    {...(validation = { pattern: "^[a-zA-Z-.`' ]*$", isRequired: true, type: "text", title: t("TL_INVALID_TRADE_NAME") })} />
                    </div>
                    <div className="col-md-6" ><CardLabel>{t("CR_LOCALITY_ML")}</CardLabel>
                        <TextInput t={t} isMandatory={false} type={"text"} optionKey="i18nKey" name="PresentLocalityNameMl" value={PresentLocalityNameMl} onChange={setSelectPresentLocalityNameMl} disable={isEdit}    {...(validation = { pattern: "^[a-zA-Z-.`' ]*$", isRequired: true, type: "text", title: t("TL_INVALID_TRADE_NAME") })} />
                    </div>
                </div>
                <div className="row">
                    <div className="col-md-6" ><CardLabel>{t("CR_CITY_EN")}</CardLabel>
                        <TextInput t={t} isMandatory={false} type={"text"} optionKey="i18nKey" name="PresentCityNameEn" value={PresentCityNameEn} onChange={setSelectPresentCityNameEn} disable={isEdit}    {...(validation = { pattern: "^[a-zA-Z-.`' ]*$", isRequired: true, type: "text", title: t("TL_INVALID_TRADE_NAME") })} />
                    </div>
                    <div className="col-md-6" ><CardLabel>{t("CR_CITY_ML")}</CardLabel>
                        <TextInput t={t} isMandatory={false} type={"text"} optionKey="i18nKey" name="PresentCityNameMl" value={PresentCityNameMl} onChange={setSelectPresentCityNameMl} disable={isEdit}    {...(validation = { pattern: "^[a-zA-Z-.`' ]*$", isRequired: true, type: "text", title: t("TL_INVALID_TRADE_NAME") })} />
                    </div>
                </div>
                <div className="row">
                    <div className="col-md-6" ><CardLabel>{t("CS_COMMON_VILLAGE")}</CardLabel>
                        <Dropdown t={t} optionKey="code" isMandatory={false} option={cmbPlace} selected={PresentVillage} select={setSelectPresentVillage} disabled={isEdit} />
                    </div>
                    <div className="col-md-6" ><CardLabel>{t("CS_COMMON_LB_NAME")}</CardLabel>
                        <Dropdown t={t} optionKey="code" isMandatory={false} option={cmbPlace} selected={PresentLBName} select={setSelectPresentLBName} disabled={isEdit} />
                    </div>
                </div>
                <div className="row">
                    <div className="col-md-6" ><CardLabel>{t("CS_COMMON_TALUK")}</CardLabel>
                        <Dropdown t={t} optionKey="code" isMandatory={false} option={cmbPlace} selected={PresentTaluk} select={setSelectPresentTaluk} disabled={isEdit} />
                    </div>
                    <div className="col-md-6" ><CardLabel>{t("CS_COMMON_DISTRICT")}</CardLabel>
                        <Dropdown t={t} optionKey="code" isMandatory={false} option={cmbPlace} selected={PresentDistrict} select={setSelectPresentDistrict} disabled={isEdit} />
                    </div>
                </div>
                <div className="row">
                    <div className="col-md-6" ><CardLabel>{t("CS_COMMON_POST_OFFICE")}</CardLabel>
                        <Dropdown t={t} optionKey="code" isMandatory={false} option={cmbPlace} selected={PresentPostOffice} select={setSelectPresentPostOffice} disabled={isEdit} />
                    </div>
                    <div className="col-md-6" ><CardLabel>{t("CS_COMMON_PIN_CODE")}</CardLabel>
                        <TextInput t={t} isMandatory={false} type={"text"} optionKey="i18nKey" name="PresentPincode" value={PresentPincode} onChange={setSelectPresentPincode} disable={isEdit}    {...(validation = { pattern: "^[a-zA-Z-.`' ]*$", isRequired: true, type: "number",maxLength:6,minLength:6, title: t("TL_INVALID_TRADE_NAME") })} />
                    </div>
                </div>
                <div className="row">
                    <div className="col-md-12" ><h1 className="headingh1" ><span style={{ background: "#fff", padding: "0 10px" }}>{`${t("CR_PERMANENT_ADDRESS")}`}</span> </h1>
                    </div>
                </div>
                <div className="row">
                    <div className="col-md-6" ><CardLabel>{t("CR_BUILDING_NO")}</CardLabel>
                        <TextInput t={t} isMandatory={false} type={"text"} optionKey="i18nKey" name="PermanentBuldingNo" value={PermanentBuldingNo} onChange={setSelectPermanentBuldingNo} disable={isEdit}    {...(validation = { pattern: "^[a-zA-Z-.`' ]*$", isRequired: true, type: "text", title: t("TL_INVALID_TRADE_NAME") })} />
                    </div>
                    <div className="col-md-6" ><CardLabel>{t("CR_HOUSE_NO")}</CardLabel>
                        <TextInput t={t} isMandatory={false} type={"text"} optionKey="i18nKey" name="PermanentHouseNo" value={PermanentHouseNo} onChange={setSelectPermanentHouseNo} disable={isEdit}    {...(validation = { pattern: "^[a-zA-Z-.`' ]*$", isRequired: true, type: "text", title: t("TL_INVALID_TRADE_NAME") })} />
                    </div>
                </div>
                <div className="row">
                    <div className="col-md-6" ><CardLabel>{t("CR_LOCALITY_EN")}</CardLabel>
                        <TextInput t={t} isMandatory={false} type={"text"} optionKey="i18nKey" name="PermanentLocalityNameEn" value={PermanentLocalityNameEn} onChange={setSelectPermanentLocalityNameEn} disable={isEdit}    {...(validation = { pattern: "^[a-zA-Z-.`' ]*$", isRequired: true, type: "text", title: t("TL_INVALID_TRADE_NAME") })} />
                    </div>
                    <div className="col-md-6" ><CardLabel>{t("CR_LOCALITY_ML")}</CardLabel>
                        <TextInput t={t} isMandatory={false} type={"text"} optionKey="i18nKey" name="PermanentLocalityNameMl" value={PermanentLocalityNameMl} onChange={setSelectPermanentLocalityNameMl} disable={isEdit}    {...(validation = { pattern: "^[a-zA-Z-.`' ]*$", isRequired: true, type: "text", title: t("TL_INVALID_TRADE_NAME") })} />
                    </div>
                </div>
                <div className="row">
                    <div className="col-md-6" ><CardLabel>{t("CR_CITY_EN")}</CardLabel>
                        <TextInput t={t} isMandatory={false} type={"text"} optionKey="i18nKey" name="PermanentCityNameEn" value={PermanentCityNameEn} onChange={setSelectPermanentCityNameEn} disable={isEdit} {...(validation = { pattern: "^[a-zA-Z-.`' ]*$", isRequired: true, type: "text", title: t("TL_INVALID_TRADE_NAME") })} />
                    </div>
                    <div className="col-md-6" ><CardLabel>{t("CR_CITY_ML")}</CardLabel>
                        <TextInput t={t} isMandatory={false} type={"text"} optionKey="i18nKey" name="PermanentCityNameMl" value={PermanentCityNameMl} onChange={setSelectPermanentCityNameMl} disable={isEdit} {...(validation = { pattern: "^[a-zA-Z-.`' ]*$", isRequired: true, type: "text", title: t("TL_INVALID_TRADE_NAME") })} />
                    </div>
                </div>
                <div className="row">
                    <div className="col-md-6" ><CardLabel>{t("CS_COMMON_VILLAGE")}</CardLabel>
                        <Dropdown t={t} optionKey="code" isMandatory={false} option={cmbPlace} selected={PermanentVillage} select={setSelectPermanentVillage} disabled={isEdit} />
                    </div>
                    <div className="col-md-6" ><CardLabel>{t("CS_COMMON_LB_NAME")}</CardLabel>
                        <Dropdown t={t} optionKey="code" isMandatory={false} option={cmbPlace} selected={PermanentLBName} select={setSelectPermanentLBName} disabled={isEdit} />
                    </div>
                </div>
                <div className="row">
                    <div className="col-md-6" ><CardLabel>{t("CS_COMMON_TALUK")}</CardLabel>
                        <Dropdown t={t} optionKey="code" isMandatory={false} option={cmbPlace} selected={PermanentTaluk} select={setSelectPermanentTaluk} disabled={isEdit} />
                    </div>
                    <div className="col-md-6" ><CardLabel>{t("CS_COMMON_DISTRICT")}</CardLabel>
                        <Dropdown t={t} optionKey="code" isMandatory={false} option={cmbPlace} selected={PermanentDistrict} select={setSelectPermanentDistrict} disabled={isEdit} />
                    </div>
                </div>
                <div className="row">
                    <div className="col-md-6" ><CardLabel>{t("CS_COMMON_POST_OFFICE")}</CardLabel>
                        <Dropdown t={t} optionKey="code" isMandatory={false} option={cmbPlace} selected={PermanentPostOffice} select={setSelectPermanentPostOffice} disabled={isEdit} />
                    </div>
                    <div className="col-md-6" ><CardLabel>{t("CS_COMMON_PIN_CODE")}</CardLabel>
                        <TextInput t={t} isMandatory={false} type={"text"} optionKey="i18nKey" name="PermanentPincode" value={PermanentPincode} onChange={setSelectPermanentPincode} disable={isEdit}    {...(validation = { pattern: "^[a-zA-Z-.`' ]*$", isRequired: true, type: "number",maxLength:6,minLength:6, title: t("TL_INVALID_TRADE_NAME") })} />
                    </div>
                </div>
            </FormStep>
        </React.Fragment>
    );
};
export default Address;
