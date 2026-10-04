import React, { useState } from "react";
import { ArrowForward } from "@nudmcdgnpm/digit-ui-react-components";
import { useTranslation } from "react-i18next";

const FAQComponent = props => {
  const { question, answer, index, lastIndex, subAnswer, acrynom} = props;
  const [isOpen, toggleOpen] = useState(false);
  const { t } = useTranslation();
  const selectedLanguage = Digit.StoreData.getCurrentLanguage();
  return (
    <div className="faqs border-none" onClick={() => toggleOpen(!isOpen)}>
          <div className="faq-question" className={`faq-question ${t(question).length > 30 && isOpen ? (Digit.Utils.browser.isMobile() ? "display-block" : "justify-revert") : "display-flex"}`}>
        <span className="dss-faqcomponent-text-style">
        {`${index}. `+ t(question)}
        </span>
        <span className={`${isOpen ? "faqicon rotate" : "faqicon"} dss-faqcomponent-wrapper`}>
            {isOpen ? <ArrowForward /> : <ArrowForward/>}
        </span>
      </div>

      <div 
        className="faq-answer" 
        style={isOpen ? { display: "block"} : { display: "none" }}
      >

       <div className="dss-faqcomponent-top-spacing"> 
       {answer?.map((obj,i) => 
        <span className="dss-faqcomponent-top-spacing-2">
        {t(obj.ans)}
        </span>)}
        {acrynom?.map((obj,i) => 
       <div> <span className="dss-faq-acr">
        {t(obj.acr)}
        </span>
         <span className="dss-faq-fullform">
         {t(obj.fullForm)}
         </span></div>)}
        
        {answer?.map((obj) => 
        <span className="dss-faqcomponent-spacing">
       {obj.point ? "•" : null}<div className="dss-faqcomponent-top-spacing-3">{t(obj.point)}</div>
        </span>)}
        </div>
        <div>{subAnswer?.map((obj) => 
        <span className="dss-faqcomponent-bottom-spacing">
        {t(obj.ans)}
        </span>)}
        {subAnswer?.map((obj) => 
        <span className="dss-faqcomponent-spacing">
        {obj.point ? "•" : null}<div className="dss-faqcomponent-top-spacing-3">{t(obj.point)}</div>
        </span>)}
       </div>
      </div>
      {!lastIndex ? <div className="cs-box-border"/> : null}
        </div>
  );
};

export default FAQComponent;