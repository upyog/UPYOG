import { ActionBar, Dropdown } from "@nudmcdgnpm/digit-ui-react-components";
import React, { useState } from "react";
import { CustomButton, Menu } from "@nudmcdgnpm/digit-ui-react-components";

const ChangeLanguage = (prop) => {
  const isDropdown = prop.dropdown || false;
  const { data: storeData, isLoading } = Digit.Hooks.useStore.getInitData();
  const { languages, stateInfo } = storeData || {};
  const selectedLanguage = Digit.StoreData.getCurrentLanguage();
  const [selected, setselected] = useState(selectedLanguage);
  const handleChangeLanguage = (language) => {
    setselected(language.value);
    Digit.LocalizationService.changeLanguage(language.value, stateInfo.code);
  };

  if (isLoading) return null;

  if (isDropdown) {
    const currentLangLabel = languages?.find((language) => language.value === selected)?.label || selected;

    return (
      <div className={prop.classes || "nav-lang-change"}>
        <Dropdown
          option={languages}
          selected={languages.find((language) => language.value === selectedLanguage)}
          optionKey={"label"}
          select={handleChangeLanguage}
          showArrow={false}
          freeze={true}
          customSelector={
            <div className="nav-lang-selector">
              <svg className="nav-lang-globe-icon" width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                <circle cx="12" cy="12" r="10"></circle>
                <line x1="2" y1="12" x2="22" y2="12"></line>
                <path d="M12 2a15.3 15.3 0 0 1 4 10 15.3 15.3 0 0 1-4 10 15.3 15.3 0 0 1-4-10 15.3 15.3 0 0 1 4-10z"></path>
              </svg>
              <span className="nav-lang-text">{currentLangLabel}</span>
              <svg className="nav-dropdown-chevron" width="12" height="12" viewBox="0 0 24 24" fill="currentColor">
                <path d="M7 10l5 5 5-5z" />
              </svg>
            </div>
          }
        />
      </div>
    );
  } else {
    return (
      <React.Fragment>
        <div className="core-change-language-bottom-spacing">Language</div>
        <div className="language-selector">
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
      </React.Fragment>
    );
  }
};

export default ChangeLanguage;
