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
  let sourceUrl = "https://s3.ap-south-1.amazonaws.com/egov-qa-assets";
  const pdfUrl = "https://pg-egov-assets.s3.ap-south-1.amazonaws.com/Upyog+Code+and+Copyright+License_v1.pdf";

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
            <h2 style={{fontSize:"28px",marginBottom:"18px", fontWeight:"bold"}}>KSMART</h2>
            <p>Kerala Solution For Administrative Reformation And Transformation</p>
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
      {/* <div style={{ width: '100%',  position: "absolute", bottom: 0 }}>
        <div style={{ display: 'flex', justifyContent: 'center', color:"white" }}>
          <img style={{ cursor: "pointer", display: "inline-flex", height: '1.4em' }} alt={"Powered by DIGIT"} src={`${sourceUrl}/digit-footer-bw.png`} onError={"this.src='./../digit-footer.png'"} onClick={() => {
            window.open('https://www.digit.org/', '_blank').focus();
          }}></img>
          <span style={{ margin: "0 10px" }}>|</span>
          <span style={{ cursor: "pointer", fontSize: "16px", fontWeight: "400"}} onClick={() => { window.open('https://niua.in/', '_blank').focus();}} >Copyright © 2022 National Institute of Urban Affairs</span>
          <span style={{ margin: "0 10px" }}>|</span>
          <a style={{ cursor: "pointer", fontSize: "16px", fontWeight: "400"}} href={pdfUrl} target='_blank'>UPYOG License</a>
        </div>
      </div> */}
    </Background>
  );
};

export default LanguageSelection;
