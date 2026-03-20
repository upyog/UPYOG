import { Card, CustomButton, SubmitBar } from "@upyog/digit-ui-react-components";
import React, { useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";
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
    history.push("/mycity-ui/employee/user/login");
  };

  if (isLoading) return null;

  return (
    <React.Fragment>
      <style>
{`@media (min-width: 780px) {
    .banner .bannerCard, .loginFormStyleEmployee .employeeCard {
        min-width: 270px !important;
        background-color: ghostwhite !important;
    }
}`}
      </style>
    <Background>
      <Card className="bannerCard removeBottomMargin customLanguageCard" style={{width: "400px"}}>
      <div
          style={{
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
            marginBottom: "16px",
            paddingBottom: "13px",
            borderBottom: "1px solid #192771"
          }}
        >
          <a
            href="#"
            target="_blank"
            rel="noreferrer"
            style={{ display: "flex", alignItems: "center", textDecoration: "none" }}
          >
            <img
              src="https://upload.wikimedia.org/wikipedia/commons/9/99/Seal_of_Uttarakhand.svg"
              alt="Uttarakhand Seal"
              style={{ minWidth: "47px", height: "47px", marginRight: "10px" }}
            />
            <div style={{ marginLeft: "12px", textAlign: "left" }}>
              <h3 style={{ color: "#000000", fontSize: "20px", margin: "0" }}>
                <strong>NagarSewa Portal</strong>
              </h3>
              <p style={{ fontSize: "14px", color: "#000", margin: "2px 0 0 0" }}>
                Government of Uttarakhand
              </p>
            </div>
          </a>
        </div>
        <div
          style={{
            fontSize: "16px",
            fontWeight: "600",
            color: "#333",
            marginBottom: "16px",
            textAlign: "center"
          }}
        >
          {"Select Language"}
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
        <SubmitBar style={{ width: "100%" }} label={t(`CORE_COMMON_CONTINUE`)} onSubmit={handleSubmit} />
      </Card>

      <div style={{ width: '100%', position: 'fixed', bottom: 0,backgroundColor:"white",textAlign:"center" }}>
        <div style={{ display: 'flex', justifyContent: 'center', color:"black" }}>
          {/* <span style={{ cursor: "pointer", fontSize: window.Digit.Utils.browser.isMobile()?"12px":"12px", fontWeight: "400"}} onClick={() => { window.open('https://www.digit.org/', '_blank').focus();}} >Powered by DIGIT</span>
          <span style={{ margin: "0 10px" ,fontSize: window.Digit.Utils.browser.isMobile()?"12px":"12px"}}>|</span> */}
          <a style={{ cursor: "pointer", fontSize: window.Digit.Utils.browser.isMobile()?"12px":"12px", fontWeight: "400"}} href="#" target='_blank'>NagarSewa License</a>

          <span  className="upyog-copyright-footer" style={{ margin: "0 10px",fontSize:"12px" }} >|</span>
          <span  className="upyog-copyright-footer" style={{ cursor: "pointer", fontSize: window.Digit.Utils.browser.isMobile()?"12px":"12px", fontWeight: "400"}} onClick={() => { window.open('https://niua.in/', '_blank').focus();}} >Copyright © 2025 Government of Uttarakhand</span>
          
          {/* <a style={{ cursor: "pointer", fontSize: "16px", fontWeight: "400"}} href="#" target='_blank'>UPYOG License</a> */}

        </div>
        <div className="upyog-copyright-footer-web">
          <span className="" style={{ cursor: "pointer", fontSize:  window.Digit.Utils.browser.isMobile()?"14px":"16px", fontWeight: "400"}} onClick={() => { window.open('https://niua.in/', '_blank').focus();}} >Copyright © 2025 Government of Uttarakhand</span>
          </div>
      </div>
    </Background>
    </React.Fragment>
  );
};

export default LanguageSelection;
