import React, { useState, useEffect } from "react";
import { Dropdown, FormStep, CitizenConsentForm, Loader, CheckBox,Modal,Card ,CardHeader} from "@nudmcdgnpm/digit-ui-react-components";
import { Link } from "react-router-dom";

const SelectMobileNumber = ({ t, onSelect, showRegisterLink, mobileNumber, onMobileChange, config, canSubmit }) => {
  const [isCheckBox, setIsCheckBox] = useState(false);
  const [isCCFEnabled, setisCCFEnabled] = useState(false);
  const [mdmsConfig, setMdmsConfig] = useState("");
  const [error, setError]=useState("");
  const { isLoading, data } = Digit.Hooks.useCustomMDMS(Digit.ULBService.getStateId(), "common-masters", [{ name: "CitizenConsentForm" }]);
  const { data: cities } = Digit.Hooks.useTenants();
  const { data: { languages, stateInfo } = {} } = Digit.Hooks.useStore.getInitData();
  const [selectedLanguage, setSelectedLanguage] = useState(Digit.StoreData.getCurrentLanguage() || "en_IN");
  const [selectedCity, setSelectedCity] = useState(() => {
    const homeCity = Digit.ULBService.getCitizenCurrentTenant(true);
    return homeCity ? { code: homeCity } : null;
  });
  const [showToast, setShowToast] = useState(null);

  const checkDisbaled = () => !(mobileNumber.length === 10 && canSubmit)

  useEffect(()=> {
    if (data?.["common-masters"]?.CitizenConsentForm?.[0]?.isCitizenConsentFormEnabled) {
      setisCCFEnabled(data?.["common-masters"]?.CitizenConsentForm?.[0])
    }
  }, [data]);

  const onLinkClick = (e) => {
    setMdmsConfig(e.target.id)
}

  const checkLabels = () => {
    return (
    <span>
      {isCCFEnabled?.checkBoxLabels?.map((data, index) => {
        return <span key={data?.linkId || index}>
          {/* {index == 0 && "CCF"} */}
          {data?.linkPrefix && <span>{t(`${data?.linkPrefix}_`)}</span>}
          {data?.link && <span id={data?.linkId} onClick={(e) => { onLinkClick(e) }} style={{ color: "#a82227", cursor: "pointer" }}>{t(`${data?.link}_`)}</span>}
          {data?.linkPostfix && <span>{t(`${data?.linkPostfix}_`)}</span>}
          {(index == isCCFEnabled?.checkBoxLabels?.length - 1) && t("LABEL")}
        </span>
      })}
    </span>
    );
  };
  const validateMobileNumber=()=>{
      if(/^\d{0,10}$/.test(mobileNumber)){
        setError("")
      }
  };
  const handleMobileChange=(e)=>{
      const value=e.target.value;
      if(/^\d{0,10}$/.test(value)|| value===""){
        onMobileChange(e);
        validateMobileNumber();
      }
      else{
        setError(t("CORE_COMMON_PROFILE_MOBILE_NUMBER_INVALID"));
      }
  };
  if (isLoading) return <Loader />
  const register = async (e) => {
    const data = await Digit.DigiLockerService.register({ module: "SSO" });
    e.preventDefault()
    const redirectUrl = data.redirectURL
    console.log("data", data)
    localStorage.setItem("code_verfier_register", data?.dlReqRef)
    window.location.href = redirectUrl
  }

  const Heading = (props) => {
    return <h1 className="heading-m">{props.label}</h1>;
  };
  const Close = () => (
    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="#FFFFFF">
      <path d="M0 0h24v24H0V0z" fill="none" />
      <path d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12 19 6.41z" />
    </svg>
  );
  const CloseBtn = (props) => {
    return (
      <div className="icon-bg-secondary" onClick={props.onClick}>
        <Close />
      </div>
    );
  };
  const set=(e)=>{
    setShowToast(true)   
    register()
  }
  const closeModal =() =>{
    setShowToast(false)
  }
  const setModal=(e)=>{
    setShowToast(false)   
    register(e)
  }

  const handleLanguageSelection = (language) => {
    Digit.LocalizationService.changeLanguage(language.value, stateInfo.code)
    setSelectedLanguage(language.value)
  }

  const handleCitySelection = (city) => {
    Digit.SessionStorage.set("CITIZEN.COMMON.HOME.CITY", city);
    setSelectedCity(city)
  }

  return (
    <div className="login-mobile-step">
      <div className="login-form-header">
        <h2>{"Login to UPYOG"}</h2>
        <p>
          {"All the communications regarding the application will be sent to this mobile number."}
        </p>
      </div>

      <div className="login-form-body">
        <div className="login-field-group">
          <label className="login-label">{"Select Language"}</label>
          <div className="login-language">
            <ul className="login-language-list">
              {languages?.map((language) => (
                <li
                  key={language.label}
                  className={selectedLanguage === language.value ? "is-selected" : ""}
                  onClick={() => handleLanguageSelection(language)}
                  style={{ cursor: "pointer" }}
                >
                 {selectedLanguage === language.value ? (
                    <img src={"/images/check.svg"} alt="check icon"/>
                 ) : null}&nbsp;{language.label}
                </li>
              ))}
                <li><img src={"/images/search.svg"} alt="search icon"/></li>
            </ul>
          </div>
        </div>

        <div className="login-field-group">
            <label className="login-label">{"Select City"}</label>
          <div className="city-selection">
            <span className="login-input-icon" aria-hidden="true">
                <img src={"/images/search.svg"} alt="search icon"/>
            </span>
            <Dropdown
              className="city-selection-dropdown"
              selected={selectedCity}
              select={handleCitySelection}
              option={cities}
              optionKey="i18nKey"
              t={t}
              placeholder={"Search your city"}
            />
          </div>
        </div>

        <div className="login-field-group">
          <label className="login-label">{"Mobile Number"}</label>
          <div className="login-input-wrap">
            <span className="login-prefix">+91</span>
            <input
              type="tel"
              inputMode="numeric"
              pattern="[0-9]*"
              maxLength={10}
              value={mobileNumber || ""}
              placeholder={"Enter 10 digit mobile number"}
              onChange={handleMobileChange}
            />
          </div>

          <p className="login-helper">

            <span className="login-helper-icon" aria-hidden="true">
              <img src={"/images/secure.svg"} alt="secure"/>
            </span>
            {"We will send an OTP for verification"}
          </p>
          {error && <p className="login-error">{error}</p>}
        </div>
      </div>

      <div className="login-actions">
        <button
          type="button"
          className="login-primary-button"
          disabled={checkDisbaled()}
          onClick={() => onSelect({ mobileNumber })}
        >
          {t("CORE_COMMON_CONTINUE")}
        </button>

        <div className="login-divider">OR</div>

        <button
          className="login-secondary-button"
          type="button"
          onClick={(e) => setShowToast(true)}
        >
          <span className="login-secondary-button__content">
            <img src={"/images/digilocker.svg"} alt="digilocker" />
            {t("CORE_COMMON_DGILOCKER_REGISTER")}
          </span>
        </button>
      </div>

      <p className="login-security">
        <span className="login-security-icon" aria-hidden="true">
          <img src={"/images/secure.svg"} alt="secure"/>
        </span>
        {"Your information is safe and secure with us."}
      </p>

      {showToast && <Modal
        headerBarMain={<Heading label={"Consent"} />}
        headerBarEnd={<CloseBtn onClick={closeModal} />}
        actionCancelLabel={"Cancel"}
        actionCancelOnSubmit={closeModal}
        actionSaveLabel={"Ok"}
        actionSaveOnSubmit={(e)=>setModal(e)}
        formId="modal-action"
      >
        <div style={{ width: "100%" }}>
          <Card>
            <p>{"By selecting this option, I am providing my consent to associate my Upyog account with my DigiLocker ID"}</p>
          </Card>
        </div>
      </Modal>}
    </div>
  );
};

export default SelectMobileNumber;
