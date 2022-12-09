import React, { useState } from "react";
import { FormStep, CardLabel, TextInput, Dropdown, DatePicker, CheckBox } from "@egovernments/digit-ui-react-components";
import Timeline from "../../components/CRTimeline";
import { useTranslation } from "react-i18next";

const Address = ({ config, onSelect, userType, formData }) => {
    const stateId = Digit.ULBService.getStateId();
    const { t } = useTranslation();
    let validation = {};
    const { data: Taluk={} } = Digit.Hooks.cr.useCivilRegistrationMDMS(stateId, "common-masters", "mtaluk");
    const { data: Village={} } = Digit.Hooks.cr.useCivilRegistrationMDMS(stateId, "common-masters", "Village");
    const { data: District={} } = Digit.Hooks.cr.useCivilRegistrationMDMS(stateId, "common-masters", "District");

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
    const [isPrsentAddress, setIsPrsentAddress] = useState(formData?.AddressDetails?.isPrsentAddress);
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
    let cmbTaluk = [];
    let cmbVillage = [];
    let cmbDistrict = [];

    Taluk &&
    Taluk["common-masters"] &&
    Taluk["common-masters"].mtaluk.map((ob) => {
        cmbTaluk.push(ob);
    });
    Village &&
    Village["common-masters"] &&
    Village["common-masters"].Village.map((ob) => {
        cmbVillage.push(ob);
    });
    District &&
    District["common-masters"] &&
    District["common-masters"].District.map((ob) => {
        cmbDistrict.push(ob);
    });

    const onSkip = () => onSelect();

    function setSelectPresentBuldingNo(e) {
        setPresentBuldingNo(e.target.value);
        if(isPrsentAddress){
            setPermanentBuldingNo(PresentBuldingNo);
            setPermanentHouseNo(PresentHouseNo);
            setPermanentLocalityNameEn(PresentLocalityNameEn);
            setPermanentLocalityNameMl(PresentLocalityNameMl);
            setPermanentCityNameEn(PresentCityNameEn);
            setPermanentCityNameMl(PresentCityNameMl);
            setPermanentVillage(PresentVillage);
            setPermanentLBName(PresentLBName);
            setPermanentDistrict(PresentDistrict);
            setPermanentTaluk(PresentTaluk);
            setPermanentPostOffice(PresentPostOffice);
            setPermanentPincode(PresentPincode);
        }
    }
    function setSelectPresentHouseNo(e) {
        setPresentHouseNo(e.target.value);
        if(isPrsentAddress){
            setPermanentHouseNo(PresentHouseNo);
        }
    }
    function setSelectPresentLocalityNameEn(e) {
        setPresentLocalityNameEn(e.target.value);
        if(isPrsentAddress){
            setPermanentLocalityNameEn(PresentLocalityNameEn);
        }
    }
    function setSelectPresentLocalityNameMl(e) {
        setPresentLocalityNameMl(e.target.value);
        if(isPrsentAddress){
            setPermanentLocalityNameMl(PresentLocalityNameMl);
        }
    }
    function setSelectPresentCityNameEn(e) {
        setPresentCityNameEn(e.target.value);
        if(isPrsentAddress){
            setPermanentCityNameEn(PresentCityNameEn);
        }
    }
    function setSelectPresentCityNameMl(e) {
        setPresentCityNameMl(e.target.value);
        if(isPrsentAddress){
            setPermanentCityNameMl(PresentCityNameMl);
        }
    }
    function setSelectPresentVillage(value) {
        setPresentVillage(value);
        if(isPrsentAddress){
            setPermanentVillage(PresentVillage);
        }
    }
    function setSelectPresentLBName(value) {
        setPresentLBName(value);
        if(isPrsentAddress){
            setPermanentLBName(PresentLBName);
        }
    }
    function setSelectPresentTaluk(value) {
        setPresentTaluk(value);
        if(isPrsentAddress){
            setPermanentTaluk(PresentTaluk);
        }
    }
    function setSelectPresentDistrict(value) {
        setPresentDistrict(value);
        if(isPrsentAddress){
            setPermanentDistrict(PresentDistrict);
        }
    }
    function setSelectPresentPostOffice(value) {
        setPresentPostOffice(value);
        if(isPrsentAddress){
            setPermanentPostOffice(PresentPostOffice);
        }
    }
    function setSelectPresentPincode(e) {
        setPresentPincode(e.target.value);
        if(isPrsentAddress){
            setPermanentPincode(PresentPincode);
        }
    }
    //Permanent Address Function
    function setSelectPermanentBuldingNo(e) {
        setPermanentBuldingNo(e.target.value);
    }
    function setSelectPermanentHouseNo(e) {
        setPermanentHouseNo(e.target.value);
    }
    function setSelectPermanentLocalityNameEn(e) {
        setPermanentLocalityNameEn(e.target.value);
    }
    function setSelectPermanentLocalityNameMl(e) {
        setPermanentLocalityNameMl(e.target.value);
    }
    function setSelectPermanentCityNameEn(e) {
        setPermanentCityNameEn(e.target.value);
    }
    function setSelectPermanentCityNameMl(e) {
        setPermanentCityNameMl(e.target.value);
    }
    function setSelectPermanentVillage(value) {
        setPermanentVillage(value);
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
    function setSameAsPresent(e) {
        setIsPrsentAddress(e.target.checked);
        if (e.target.checked == true) {
            setPermanentBuldingNo(PresentBuldingNo);
            setPermanentHouseNo(PresentHouseNo);
            setPermanentLocalityNameEn(PresentLocalityNameEn);
            setPermanentLocalityNameMl(PresentLocalityNameMl);
            setPermanentCityNameEn(PresentCityNameEn);
            setPermanentCityNameMl(PresentCityNameMl);
            setPermanentVillage(PresentVillage);
            setPermanentLBName(PresentLBName);
            setPermanentDistrict(PresentDistrict);
            setPermanentTaluk(PresentTaluk);
            setPermanentPostOffice(PresentPostOffice);
            setPermanentPincode(PresentPincode);
        } else {
            setPermanentBuldingNo('');
            setPermanentHouseNo('');
            setPermanentLocalityNameEn('');
            setPermanentLocalityNameMl('');
            setPermanentCityNameEn('');
            setPermanentCityNameMl('');
            setPermanentVillage('');
            setPermanentLBName('');
            setPermanentDistrict('');
            setPermanentTaluk('');
            setPermanentPostOffice('');
            setPermanentPincode('');
        }
    }
    const goNext = () => {
        sessionStorage.setItem("PresentBuldingNo", PresentBuldingNo);
        sessionStorage.setItem("PresentHouseNo", PresentHouseNo);
        sessionStorage.setItem("PresentLocalityNameEn", PresentLocalityNameEn);
        sessionStorage.setItem("PresentLocalityNameMl", PresentLocalityNameMl);
        sessionStorage.setItem("PresentCityNameEn", PresentCityNameEn);
        sessionStorage.setItem("PresentCityNameMl", PresentCityNameMl);
        sessionStorage.setItem("PresentVillage", PresentVillage.code);
        sessionStorage.setItem("PresentLBName", PresentLBName.code);
        sessionStorage.setItem("PresentDistrict", PresentDistrict.code);
        sessionStorage.setItem("PresentTaluk", PresentTaluk.code);
        sessionStorage.setItem("PresentPostOffice", PresentPostOffice.code);
        sessionStorage.setItem("PresentPincode", PresentPincode.code);
      
        sessionStorage.setItem("PermanentBuldingNo", PermanentBuldingNo);
        sessionStorage.setItem("PermanentHouseNo", PermanentHouseNo);
        sessionStorage.setItem("PermanentLocalityNameEn", PermanentLocalityNameEn);
        sessionStorage.setItem("PermanentLocalityNameMl", PermanentLocalityNameMl);
        sessionStorage.setItem("PermanentCityNameEn", PermanentCityNameEn);
        sessionStorage.setItem("PermanentCityNameMl", PermanentCityNameMl);
        sessionStorage.setItem("PermanentVillage", PermanentVillage.code);
        sessionStorage.setItem("PermanentLBName", PermanentLBName.code);
        sessionStorage.setItem("PermanentDistrict", PermanentDistrict.code);
        sessionStorage.setItem("PermanentTaluk", PermanentTaluk.code);
        sessionStorage.setItem("PermanentPostOffice", PermanentPostOffice.code);
        sessionStorage.setItem("PermanentPincode", PermanentPincode.code);

        onSelect(config.key, { PresentBuldingNo,PresentHouseNo,PresentLocalityNameEn,
        PresentLocalityNameMl,PresentCityNameEn,PresentCityNameMl,PresentVillage,PresentLBName,PresentDistrict,PresentTaluk,PresentPostOffice,PresentPincode,
        PermanentBuldingNo,PermanentHouseNo,PermanentLocalityNameEn,PermanentLocalityNameMl,PermanentCityNameEn,PermanentCityNameMl,PermanentVillage,PermanentLBName,
        PermanentDistrict,PermanentTaluk,PermanentPostOffice,PermanentPincode });
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
                        <TextInput t={t} isMandatory={false} type={"text"} optionKey="i18nKey" name="PresentBuldingNo" value={PresentBuldingNo}  onChange={setSelectPresentBuldingNo} disable={isEdit}  {...(validation = { pattern: "^[a-zA-Z-.`' ]*$", isRequired: true, type: "text", title: t("TL_INVALID_TRADE_NAME") })} />
                    </div>
                    <div className="col-md-6" ><CardLabel>{t("CR_HOUSE_NO")}</CardLabel>
                        <TextInput t={t} isMandatory={false} type={"text"} optionKey="i18nKey" name="PresentHouseNo" value={PresentHouseNo} onChange={setSelectPresentHouseNo} disable={isEdit}  {...(validation = { pattern: "^[a-zA-Z-.`' ]*$", isRequired: true, type: "text", title: t("TL_INVALID_TRADE_NAME") })} />
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
                        <Dropdown t={t} optionKey="name" isMandatory={false} option={cmbVillage} selected={PresentVillage} select={setSelectPresentVillage} disabled={isEdit} />
                    </div>
                    <div className="col-md-6" ><CardLabel>{t("CS_COMMON_LB_NAME")}</CardLabel>
                        <Dropdown t={t} optionKey="code" isMandatory={false} option={cmbTaluk} selected={PresentLBName} select={setSelectPresentLBName} disabled={isEdit} />
                    </div>
                </div>
                <div className="row">
                    <div className="col-md-6" ><CardLabel>{t("CS_COMMON_TALUK")}</CardLabel>
                        <Dropdown t={t} optionKey="name" isMandatory={false} option={cmbTaluk} selected={PresentTaluk} select={setSelectPresentTaluk} disabled={isEdit} />
                    </div>
                    <div className="col-md-6" ><CardLabel>{t("CS_COMMON_DISTRICT")}</CardLabel>
                        <Dropdown t={t} optionKey="name" isMandatory={false} option={cmbDistrict} selected={PresentDistrict} select={setSelectPresentDistrict} disabled={isEdit} />
                    </div>
                </div>
                <div className="row">
                    <div className="col-md-6" ><CardLabel>{t("CS_COMMON_POST_OFFICE")}</CardLabel>
                        <Dropdown t={t} optionKey="name" isMandatory={false} option={cmbDistrict} selected={PresentPostOffice} select={setSelectPresentPostOffice} disabled={isEdit} />
                    </div>
                    <div className="col-md-6" ><CardLabel>{t("CS_COMMON_PIN_CODE")}</CardLabel>
                        <TextInput t={t} isMandatory={false} type={"text"} optionKey="i18nKey" name="PresentPincode" value={PresentPincode} onChange={setSelectPresentPincode} disable={isEdit}    {...(validation = { pattern: "^[a-zA-Z-.`' ]*$", isRequired: true, type: "number",maxLength:6,minLength:6, title: t("TL_INVALID_TRADE_NAME") })} />
                    </div>
                </div>
                <div className="row">
                    <div className="col-md-12" >
                        {/* <CardLabel>{`${t("CR_GENDER")}`}</CardLabel> */}
                        <CheckBox label={t("Permanent Address is Same as Present Address")} onInputChange={setSameAsPresent} onChange={setSameAsPresent} value={isPrsentAddress} checked={isPrsentAddress} />
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
                        <Dropdown t={t} optionKey="name" isMandatory={false} option={cmbVillage} selected={PermanentVillage} select={setSelectPermanentVillage} disabled={isEdit} />
                    </div>
                    <div className="col-md-6" ><CardLabel>{t("CS_COMMON_LB_NAME")}</CardLabel>
                        <Dropdown t={t} optionKey="code" isMandatory={false} option={cmbPlace} selected={PermanentLBName} select={setSelectPermanentLBName} disabled={isEdit} />
                    </div>
                </div>
                <div className="row">
                    <div className="col-md-6" ><CardLabel>{t("CS_COMMON_TALUK")}</CardLabel>
                        <Dropdown t={t} optionKey="name" isMandatory={false} option={cmbTaluk} selected={PermanentTaluk} select={setSelectPermanentTaluk} disabled={isEdit} />
                    </div>
                    <div className="col-md-6" ><CardLabel>{t("CS_COMMON_DISTRICT")}</CardLabel>
                        <Dropdown t={t} optionKey="name" isMandatory={false} option={cmbDistrict} selected={PermanentDistrict} select={setSelectPermanentDistrict} disabled={isEdit} />
                    </div>
                </div>
                <div className="row">
                    <div className="col-md-6" ><CardLabel>{t("CS_COMMON_POST_OFFICE")}</CardLabel>
                        <Dropdown t={t} optionKey="name" isMandatory={false} option={cmbDistrict} selected={PermanentPostOffice} select={setSelectPermanentPostOffice} disabled={isEdit} />
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
