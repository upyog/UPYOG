import React, { useState } from "react";
import { FormStep, CardLabel, TextInput, Dropdown, DatePicker, CheckBox } from "@egovernments/digit-ui-react-components";
import Timeline from "../../components/CRTimeline";
import { useTranslation } from "react-i18next";

const ChildDetails = ({ config, onSelect, userType, formData }) => {
    const stateId = Digit.ULBService.getStateId();
    const { t } = useTranslation();
    let validation = {};
    const { data: Menu } = Digit.Hooks.cr.useCRGenderMDMS(stateId, "common-masters", "GenderType");
    const [ChildDOB, setChildDOB] = useState(formData?.ChildDetails?.ChildDOB);
    const [Gender, selectGender] = useState(formData?.ChildDetails?.Gender);
    const [ChildAadharNo, setChildAadharNo] = useState(formData?.ChildDetails?.ChildAadharNo);
    const [ChildFirstNameEn, setChildFirstNameEn] = useState(formData?.ChildDetails?.ChildFirstNameEn);
    const [ChildMiddleNameEn, setChildMiddleNameEn] = useState(formData?.ChildDetails?.ChildMiddleNameEn);
    const [ChildLastNameEn, setChildLastNameEn] = useState(formData?.ChildDetails?.ChildLastNameEn);
    const [ChildFirstNameMl, setChildFirstNameMl] = useState(formData?.ChildDetails?.ChildFirstNameMl);
    const [ChildMiddleNameMl, setChildMiddleNameMl] = useState(formData?.ChildDetails?.ChildMiddleNameMl);
    const [ChildLastNameMl, setChildLastNameMl] = useState(formData?.ChildDetails?.ChildLastNameMl);
    const [isAdopted, setIsAdopted] = useState(formData?.ChildDetails?.isAdopted);
    const [isMultipleBirth, setIsMultipleBirth] = useState(formData?.ChildDetails?.isMultipleBirth);
    const [isFatherInfo, setIsFatherInfo] = useState(formData?.ChildDetails?.isFatherInfo);
    const [isMotherInfo, setIsMotherInfo] = useState(formData?.ChildDetails?.isMotherInfo);
    const [isBornOutSide, setIsBornOutSide] = useState(formData?.ChildDetails?.isBornOutSide);
    const [ChildPassportNo, setChildPassportNo] = useState(formData?.ChildDetails?.ChildPassportNo);
    const [ChildArrivalDate, setChildArrivalDate] = useState(formData?.ChildDetails?.ChildArrivalDate);

    const isEdit = window.location.href.includes("/edit-application/") || window.location.href.includes("renew-trade");

    let menu = [];
    Menu &&
        Menu.map((genderDetails) => {
            menu.push({ i18nKey: `CR_COMMON_GENDER_${genderDetails.code}`, code: `${genderDetails.code}`, value: `${genderDetails.code}` });
        });

    const onSkip = () => onSelect();

    function setselectGender(value) {
        selectGender(value);
    }
    function setSelectChildAadharNo(e) {
        setChildAadharNo(e.target.value);
    }
    function setselectChildDOB(value) {
        setChildDOB(value);
    }
    function setSelectChildFirstNameEn(e) {
        setChildFirstNameEn(e.target.value);
    }
    function setSelectChildMiddleNameEn(e) {
        setChildMiddleNameEn(e.target.value);
    }
    function setSelectChildLastNameEn(e) {
        setChildLastNameEn(e.target.value);
    }
    function setSelectChildFirstNameMl(e) {
        setChildFirstNameMl(e.target.value);
    }
    function setSelectChildMiddleNameMl(e) {
        setChildMiddleNameMl(e.target.value);
    }
    function setSelectChildLastNameMl(e) {
        setChildLastNameMl(e.target.value);
    }
    function setAdopted(e) {
        if (e.target.checked == true) {
            setIsAdopted(true);
        } else {
            setIsAdopted(false);
        }
    }
    function setMultipleBirth(e) {
        if (e.target.checked == true) {
            setIsMultipleBirth(true);
        } else {
            setIsMultipleBirth(false);
        }
    }
    function setFatherInfo(e) {
        if (e.target.checked == true) {
            setIsFatherInfo(true);
        } else {
            setIsFatherInfo(false);
        }
    }
    function setMotherInfo(e) {
        if (e.target.checked == true) {
            setIsMotherInfo(true);
        } else {
            setIsMotherInfo(false);
        }
    }
    function setBornOutSide(e) {
        if (e.target.checked == true) {
            setIsBornOutSide(true);
        } else {
            setIsBornOutSide(false);
        }
    }
    function setSelectPassportNo(e) {
        setChildPassportNo(e.target.value);
    }
    function setSelectArrivalDate(e) {
        setChildArrivalDate(e.target.value);
    }

    const goNext = () => {
        sessionStorage.setItem("ChildDOB", ChildDOB);
        sessionStorage.setItem("Gender", Gender.code);
        sessionStorage.setItem("ChildAadharNo", ChildAadharNo);
        sessionStorage.setItem("ChildFirstNameEn", ChildFirstNameEn);
        sessionStorage.setItem("ChildMiddleNameEn", ChildMiddleNameEn);
        sessionStorage.setItem("ChildLastNameEn", ChildLastNameEn);
        sessionStorage.setItem("ChildFirstNameMl", ChildFirstNameMl);
        sessionStorage.setItem("ChildMiddleNameMl", ChildMiddleNameMl);
        sessionStorage.setItem("ChildLastNameMl", ChildLastNameMl);
        sessionStorage.setItem("isAdopted", isAdopted);
        sessionStorage.setItem("isMultipleBirth", isMultipleBirth);
        sessionStorage.setItem("isFatherInfo", isFatherInfo);
        sessionStorage.setItem("isMotherInfo", isMotherInfo);
        sessionStorage.setItem("isBornOutSide", isBornOutSide);
        sessionStorage.setItem("ChildPassportNo", ChildPassportNo);
        sessionStorage.setItem("ChildArrivalDate", ChildArrivalDate);
        onSelect(config.key, { ChildDOB,Gender,ChildAadharNo,ChildFirstNameEn,ChildMiddleNameEn,ChildLastNameEn,ChildFirstNameMl,ChildMiddleNameMl,ChildLastNameMl,isAdopted,isMultipleBirth,isFatherInfo,isMotherInfo,isBornOutSide,ChildPassportNo,ChildArrivalDate });
    }
    return (
        <React.Fragment>
            {window.location.href.includes("/employee") ? <Timeline /> : null}
            <FormStep t={t} config={config} onSelect={goNext} onSkip={onSkip} isDisabled={!ChildDOB}>

                <div className="row">
                    <div className="col-md-12" ><h1 className="headingh1" ><span style={{ background: "#fff", padding: "0 10px" }}>{`${t("Registration Details")}`}</span> </h1>
                    </div>
                </div>
                <div className="row">
                    <div className="col-md-4" ><CardLabel>{t("CR_DATE_OF_BIRTH_TIME")}</CardLabel>
                        <DatePicker date={ChildDOB} name="ChildDOB" onChange={setselectChildDOB} controls={['calendar', 'time']} />
                    </div>
                    <div className="col-md-4" > <CardLabel>{`${t("CR_GENDER")}`}</CardLabel>
                        <Dropdown t={t} optionKey="code" isMandatory={false} option={menu} selected={Gender} select={setselectGender} disabled={isEdit} />
                    </div>
                    <div className="col-md-4"> <CardLabel>{`${t("Aadhar No")}`}</CardLabel>
                        <TextInput t={t} isMandatory={false} type={"text"} optionKey="i18nKey" name="ChildAadharNo" value={ChildAadharNo} onChange={setSelectChildAadharNo} disable={isEdit} {...(validation = {pattern: "^([0-9]){12}$", isRequired: true, type: "text", title: t("TL_INVALID_AADHAR_NO") })} />
                    </div>
                </div>
                <div className="row">
                    <div className="col-md-12" ><h1 className="headingh1" ><span style={{ background: "#fff", padding: "0 10px" }}>{`${t("Name of Child")}`}</span> </h1>
                    </div>
                </div>
                <div className="row">
                    <div className="col-md-4" > <CardLabel>{`${t("CR_FIRST_NAME")}`}</CardLabel>
                        <TextInput t={t} isMandatory={false} type={"text"} optionKey="i18nKey" name="ChildFirstNameEn" value={ChildFirstNameEn} onChange={setSelectChildFirstNameEn} disable={isEdit} {...(validation = { pattern: "^[a-zA-Z-.`' ]*$", isRequired: true, type: "text", title: t("TL_INVALID_TRADE_NAME") })} />
                    </div>
                    <div className="col-md-4" > <CardLabel>{`${t("CR_MIDDLE_NAME")}`}</CardLabel>
                        <TextInput t={t} isMandatory={false} type={"text"} optionKey="i18nKey" name="ChildMiddleNameEn" value={ChildMiddleNameEn} onChange={setSelectChildMiddleNameEn} disable={isEdit} {...(validation = { pattern: "^[a-zA-Z-.`' ]*$", isRequired: false, type: "text", title: t("TL_INVALID_TRADE_NAME") })} />
                    </div>
                    <div className="col-md-4" > <CardLabel>{`${t("CR_LAST_NAME")}`}</CardLabel>
                        <TextInput t={t} isMandatory={false} type={"text"} optionKey="i18nKey" name="ChildLastNameEn" value={ChildLastNameEn} onChange={setSelectChildLastNameEn} disable={isEdit} {...(validation = { pattern: "^[a-zA-Z-.`' ]*$", isRequired: true, type: "text", title: t("TL_INVALID_TRADE_NAME") })} />
                    </div>
                </div>
                <div className="row">
                    <div className="col-md-4" > <CardLabel>{`${t("CR_FIRST_NAME")}`}</CardLabel>
                        <TextInput t={t} isMandatory={false} type={"text"} optionKey="i18nKey" name="ChildFirstNameMl" value={ChildFirstNameMl} onChange={setSelectChildFirstNameMl} disable={isEdit} {...(validation = {isRequired: true, type: "text", title: t("TL_INVALID_TRADE_NAME") })} />
                    </div>
                    <div className="col-md-4" > <CardLabel>{`${t("CR_MIDDLE_NAME")}`}</CardLabel>
                        <TextInput t={t} isMandatory={false} type={"text"} optionKey="i18nKey" name="ChildMiddleNameMl" value={ChildMiddleNameMl} onChange={setSelectChildMiddleNameMl} disable={isEdit} {...(validation = { isRequired: false, type: "text", title: t("TL_INVALID_TRADE_NAME") })} />
                    </div>
                    <div className="col-md-4" > <CardLabel>{`${t("CR_LAST_NAME")}`}</CardLabel>
                        <TextInput t={t} isMandatory={false} type={"text"} optionKey="i18nKey" name="ChildLastNameMl" value={ChildLastNameMl} onChange={setSelectChildLastNameMl} disable={isEdit} {...(validation = { isRequired: true, type: "text", title: t("TL_INVALID_TRADE_NAME") })} />
                    </div>
                </div>
                <div className="row">
                    <div className="col-md-12" ><h1 className="headingh1" ><span style={{ background: "#fff", padding: "0 10px" }}>{`${t("Other Details")}`}</span> </h1>
                    </div>
                </div>
                <div className="row">

                    <div className="col-md-6" >
                        {/* <CardLabel>{`${t("CR_GENDER")}`}</CardLabel> */}
                        <CheckBox label={t("Adopted")} onChange={setAdopted} value={isAdopted} checked={isAdopted} />
                    </div>
                    <div className="col-md-6" >
                        {/* <CardLabel>{`${t("Multiple Birth")}`}</CardLabel> */}
                        <CheckBox label={t("Multiple Birth")} onChange={setMultipleBirth} value={isMultipleBirth} checked={isMultipleBirth} />
                    </div>
                </div>
                <div className="row">
                    <div className="col-md-6" >
                        {/* <CardLabel>{`${t("CR_GENDER")}`}</CardLabel> */}
                        <CheckBox label={t("Father Information Missing")} onChange={setFatherInfo} value={isFatherInfo} checked={isFatherInfo} />
                    </div>
                    <div className="col-md-6" >
                        {/* <CardLabel>{`${t("CR_GENDER")}`}</CardLabel> */}
                        <CheckBox label={t("Mother Information Missing")} onChange={setMotherInfo} value={isMotherInfo} checked={isMotherInfo} />
                    </div>
                </div>
                <div className="row">
                    <div className="col-md-12" >
                        <CheckBox label={t("Born OutSide India")} onChange={setBornOutSide} value={isBornOutSide} checked={isBornOutSide} />

                    </div>
                </div>
                <div className="row">

                    <div className="col-md-6" > <CardLabel>{`${t("Passport No")}`}</CardLabel>
                        <TextInput t={t} isMandatory={false} type={"text"} optionKey="i18nKey" name="ChildPassportNo" value={ChildPassportNo} onChange={setSelectPassportNo} disable={isEdit} {...(validation = { pattern: "^[a-zA-Z-.`' ]*$", isRequired: false, type: "text", title: t("TL_INVALID_TRADE_NAME") })} />
                    </div>
                    <div className="col-md-6" > <CardLabel>{`${t("Date of Arrival")}`}</CardLabel>
                        <TextInput t={t} isMandatory={false} type={"text"} optionKey="i18nKey" name="ChildArrivalDate" value={ChildArrivalDate} onChange={setSelectArrivalDate} disable={isEdit} {...(validation = { pattern: "^[a-zA-Z-.`' ]*$", isRequired: false, type: "text", title: t("TL_INVALID_TRADE_NAME") })} />
                    </div>
                </div>
            </FormStep>
        </React.Fragment>
    );
};
export default ChildDetails;
