import React, { useState } from "react";
import { FormStep, CardLabel, TextInput, Dropdown, DatePicker, CheckBox } from "@egovernments/digit-ui-react-components";
import Timeline from "../../components/CRTimeline";
import { useTranslation } from "react-i18next";

const ChildDetails = ({ config, onSelect, userType, formData }) => {
    const stateId = Digit.ULBService.getStateId();
    const { t } = useTranslation();
    let validation = {};
    const {data: Menu} = Digit.Hooks.cr.useCRGenderMDMS(stateId, "common-masters", "GenderType");
    const [ChildDOB, setChildDOB] = useState(formData?.owners?.ChildDOB);
    const [Gender, selectGender] = useState(formData?.ChildDetails?.Gender);
    const [ChildAadharNo, setChildAadharNo] = useState(formData?.ChildDetails?.ChildAadharNo);
    const [ChildFirstNameEn, setChildFirstNameEn] = useState(formData?.ChildDetails?.ChildFirstNameEn);
    const [isCorrespondenceAddress, setIsCorrespondenceAddress] = useState(formData?.ChildDetails?.isCorrespondenceAddress);
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
    function setCorrespondenceAddress(e) {
    }

    const goNext = () => {
        sessionStorage.setItem("Gender", setPlaceofActivity.code);
        onSelect(config.key, { ChildDOB });
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
                        <TextInput t={t} isMandatory={false} type={"text"} optionKey="i18nKey" name="ChildAadharNo" value={ChildAadharNo} onChange={setSelectChildAadharNo} disable={isEdit} {...(validation = { pattern: "^[a-zA-Z-.`' ]*$", isRequired: true, type: "text", title: t("TL_INVALID_TRADE_NAME") })} />
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
                        <TextInput t={t} isMandatory={false} type={"text"} optionKey="i18nKey" name="TradeName" value={TradeName} onChange={setSelectTradeName} disable={isEdit} {...(validation = { pattern: "^[a-zA-Z-.`' ]*$", isRequired: true, type: "text", title: t("TL_INVALID_TRADE_NAME") })} />
                    </div>
                    <div className="col-md-4" > <CardLabel>{`${t("CR_LAST_NAME")}`}</CardLabel>
                        <TextInput t={t} isMandatory={false} type={"text"} optionKey="i18nKey" name="TradeName" value={TradeName} onChange={setSelectTradeName} disable={isEdit} {...(validation = { pattern: "^[a-zA-Z-.`' ]*$", isRequired: true, type: "text", title: t("TL_INVALID_TRADE_NAME") })} />
                    </div>
                </div>
                <div className="row">
                    <div className="col-md-4" > <CardLabel>{`${t("CR_FIRST_NAME")}`}</CardLabel>
                        <TextInput t={t} isMandatory={false} type={"text"} optionKey="i18nKey" name="TradeName" value={TradeName} onChange={setSelectTradeName} disable={isEdit} {...(validation = { pattern: "^[a-zA-Z-.`' ]*$", isRequired: true, type: "text", title: t("TL_INVALID_TRADE_NAME") })} />
                    </div>
                    <div className="col-md-4" > <CardLabel>{`${t("CR_MIDDLE_NAME")}`}</CardLabel>
                        <TextInput t={t} isMandatory={false} type={"text"} optionKey="i18nKey" name="TradeName" value={TradeName} onChange={setSelectTradeName} disable={isEdit} {...(validation = { pattern: "^[a-zA-Z-.`' ]*$", isRequired: true, type: "text", title: t("TL_INVALID_TRADE_NAME") })} />
                    </div>
                    <div className="col-md-4"> <CardLabel>{`${t("CR_LAST_NAME")}`}</CardLabel>
                        <TextInput t={t} isMandatory={false} type={"text"} optionKey="i18nKey" name="TradeName" value={TradeName} onChange={setSelectTradeName} disable={isEdit} {...(validation = { pattern: "^[a-zA-Z-.`' ]*$", isRequired: true, type: "text", title: t("TL_INVALID_TRADE_NAME") })} />
                    </div>
                </div>
                <div className="row">
                    <div className="col-md-12" ><h1 className="headingh1" ><span style={{ background: "#fff", padding: "0 10px" }}>{`${t("Other Details")}`}</span> </h1>
                    </div>
                </div>
                <div className="row">
                    
                    <div className="col-md-6" >
                        {/* <CardLabel>{`${t("CR_GENDER")}`}</CardLabel> */}
                        <CheckBox label={t("Adopted")} onChange={setCorrespondenceAddress} value={isCorrespondenceAddress} checked={isCorrespondenceAddress}  />
                    </div>
                    <div className="col-md-6" >
                        {/* <CardLabel>{`${t("Multiple Birth")}`}</CardLabel> */}
                        <CheckBox label={t("Multiple Birth")} onChange={setCorrespondenceAddress} value={isCorrespondenceAddress} checked={isCorrespondenceAddress}  />
                    </div>
                </div>
                <div className="row">
                    <div className="col-md-6" >
                        {/* <CardLabel>{`${t("CR_GENDER")}`}</CardLabel> */}
                        <CheckBox label={t("Father Information Missing")} onChange={setCorrespondenceAddress} value={isCorrespondenceAddress} checked={isCorrespondenceAddress}  />
                    </div>
                    <div className="col-md-6" >
                        {/* <CardLabel>{`${t("CR_GENDER")}`}</CardLabel> */}
                        <CheckBox label={t("Mother Information Missing")} onChange={setCorrespondenceAddress} value={isCorrespondenceAddress} checked={isCorrespondenceAddress}  />
                    </div>
                </div>
                <div className="row">
                    <div className="col-md-12" >
                    <CheckBox label={t("Born OutSide India")} onChange={setCorrespondenceAddress} value={isCorrespondenceAddress} checked={isCorrespondenceAddress}  />

                        </div>
                </div>
                <div className="row">
                    
                    <div className="col-md-6" > <CardLabel>{`${t("Passport No")}`}</CardLabel>
                        <TextInput t={t} isMandatory={false} type={"text"} optionKey="i18nKey" name="TradeName" value={TradeName} onChange={setSelectTradeName} disable={isEdit} {...(validation = { pattern: "^[a-zA-Z-.`' ]*$", isRequired: true, type: "text", title: t("TL_INVALID_TRADE_NAME") })} />
                    </div>
                    <div className="col-md-6" > <CardLabel>{`${t("Date of Arrival")}`}</CardLabel>
                        <TextInput t={t} isMandatory={false} type={"text"} optionKey="i18nKey" name="TradeName" value={TradeName} onChange={setSelectTradeName} disable={isEdit} {...(validation = { pattern: "^[a-zA-Z-.`' ]*$", isRequired: true, type: "text", title: t("TL_INVALID_TRADE_NAME") })} />
                    </div>
                </div>
            </FormStep>
        </React.Fragment>
    );
};
export default ChildDetails;
