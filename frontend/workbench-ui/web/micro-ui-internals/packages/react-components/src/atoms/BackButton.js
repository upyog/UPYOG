import React from "react";
import { ArrowLeft, ArrowLeftWhite } from "./svgindex";
import { useTranslation } from "react-i18next";
// history prop removed — navigate use 
const BackButton = ({ style, isSuccessScreen, isCommonPTPropertyScreen, getBackPageNumber, className="" ,variant="black"}) => {
  const { t } = useTranslation();
  const navigate = Digit.Hooks.useCustomNavigate(); 
  return (
    <div className={`back-btn2 ${className}`} style={style ? style : {}} onClick={() => {!isSuccessScreen ?( !isCommonPTPropertyScreen ? (navigate(-1), window.location.href.includes("/citizen/pt/property/new-application/property-type") ? sessionStorage.setItem("docReqScreenByBack",true) : null) : navigate(getBackPageNumber()) ): null}}>
     {variant=="black"?( <React.Fragment><ArrowLeft />
      <p>{t("CS_COMMON_BACK")}</p></React.Fragment>):<ArrowLeftWhite />}
    </div>

  );
};
export default BackButton
