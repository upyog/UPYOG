import { Card, CustomButton, SubmitBar } from "@nudmcdgnpm/digit-ui-react-components";
import React, { useState } from "react";
import { useTranslation } from "react-i18next";

import Background from "../../../components/Background";

const LanguageSelection = () => {
  const { data: storeData, isLoading } = Digit.Hooks.useStore.getInitData();
  const { t } = useTranslation();
  const navigate = Digit.Hooks.useCustomNavigate();
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
    navigate("/upyog-ui/employee/user/login");
  };

  if (isLoading) return null;

  return (
    <Background>
      <Card className="bannerCard removeBottomMargin">
       
        <div className="language-selector core-index-bottom-spacing">
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
        <SubmitBar className="core-assets-qrcode-fullwidth" label={t(`CORE_COMMON_CONTINUE`)} onSubmit={handleSubmit} />
      </Card>

      <div className="core-change-password-centered-2">
        <div className="core-change-password-flex-row">
          {/* <span className="custom-style" onClick={() => { window.open('https://www.digit.org/', '_blank').focus();}} >Powered by DIGIT</span>
          <span className="custom-style">|</span> */}
          <a className="footer-legal-link--mobile" href="#" target='_blank'>UPYOG License</a>

          <span  className="upyog-copyright-footer core-change-password-spacing" >|</span>
          <span  className="upyog-copyright-footer" className="footer-legal-link--mobile" onClick={() => { window.open('https://niua.in/', '_blank').focus();}} >Copyright © 2022 National Institute of Urban Affairs</span>
          
          {/* <a className="custom-style" href="#" target='_blank'>UPYOG License</a> */}

        </div>
        <div className="upyog-copyright-footer-web">
          <span className="" className="footer-legal-link" onClick={() => { window.open('https://niua.in/', '_blank').focus();}} >Copyright © 2022 National Institute of Urban Affairs</span>
          </div>
      </div>
    </Background>
  );
};

export default LanguageSelection;
