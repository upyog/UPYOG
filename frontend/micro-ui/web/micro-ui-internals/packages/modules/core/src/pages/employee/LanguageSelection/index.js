import { Card, CustomButton, SubmitBar } from "@egovernments/digit-ui-react-components";
import React, { useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory, withRouter } from "react-router-dom";
import Background from "../../../components/Background";

const LanguageSelection = () => {
  const { data: storeData, isLoading } = Digit.Hooks.useStore.getInitData();
  const { t } = useTranslation();
  const history = useHistory();
  const { languages, stateInfo } = storeData || {};
  const selectedLanguage = Digit.StoreData.getCurrentLanguage();
  const [selected, setselected] = useState(selectedLanguage);
  const handleChangeLanguage = (language) => {
    setselected(language.value);
    Digit.LocalizationService.changeLanguage(language.value, stateInfo.code);
  };

  const handleSubmit = (event) => {
    history.push("/digit-ui/employee/user/login");
  };

  if (isLoading) return null;

  return (
    <Background>
      <div className="leftdiv">
        <div className="leftflex" >
          <h1 className="logostyle">
            <a href="" src={stateInfo?.logoUrl}>
              {/* <img className="bannerLogo" src={stateInfo?.logoUrl} alt="Digit" /> */}
              KSMART
            </a>
          </h1>
          <div style={{ color: "#fff", width:"75%", fontSize:"19px" }}>
            <h2 style={{fontSize:"28px",marginBottom:"18px", fontWeight:"bold"}}>Ksmart</h2>
            <p>Lorem ipsum dolor sit, amet consectetur adipisicing elit Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet consectetur adipisicing elit. Ipsum, expedita. </p>
          </div>
          <div> 2022&copy; Ksmart</div>
        </div>
      </div>
      <Card className="bannerCard removeBottomMargin" style={{margin:"0 auto"}}>
        <div className="bannerHeader">
          <p>{t(`TENANT_TENANTS_${stateInfo?.code.toUpperCase()}`)}</p>
        </div>
        <div className="language-selector" style={{ justifyContent: "space-around", marginBottom: "24px", padding: "0 5%" }}>
          {languages.map((language, index) => (
            <div className="language-button-container" key={index}>
              <CustomButton
                selected={language.value === selected}
                text={language.label}
                onClick={() => handleChangeLanguage(language)}
              ></CustomButton>
            </div>
          ))}
        </div>
        <SubmitBar style={{ width: "35%", borderRadius: ".25rem", fontSize: "14px" }} label={t(`CORE_COMMON_CONTINUE`)} onSubmit={handleSubmit} />
      </Card>
      <div className="EmployeeLoginFooter">
        <img
          alt="Powered by DIGIT"
          src={window?.globalConfigs?.getConfig?.("DIGIT_FOOTER_BW")}
          style={{ cursor: "pointer" }}
          onClick={() => {
            window.open(window?.globalConfigs?.getConfig?.("DIGIT_HOME_URL"), "_blank").focus();
          }}
        />{" "}
      </div>
    </Background>
  );
};

export default LanguageSelection;
