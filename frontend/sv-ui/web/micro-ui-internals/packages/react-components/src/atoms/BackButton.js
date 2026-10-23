import React from "react";
import { ArrowLeft, ArrowLeftWhite } from "./svgindex";
import { useTranslation } from "react-i18next";
import { useLocation, useParams } from "react-router-dom";

// Create withRouter HOC directly in this file to avoid circular dependency
const withRouter = (Component) => {
  return (props) => {
  const navigate = Digit.Hooks.useCustomNavigate();
    const location = useLocation();
    const params = useParams();
    
    const history = {
      push: (path, state) => navigate(path, { state }),
      replace: (path, state) => navigate(path, { replace: true, state }),
      go: (n) => navigate(n),
      goBack: () => navigate(-1),
      goForward: () => navigate(1),
      location,
    };
    
    return <Component {...props} history={history} location={location} match={{ params }} />;
  };
};

const BackButton = ({ history, style, isSuccessScreen, isCommonPTPropertyScreen, getBackPageNumber, className = "", variant = "black" }) => {
  const { t } = useTranslation();
  return (
    <div
      className={`back-btn2 ${className}`}
      style={style ? style : {}}
      onClick={() => {
        !isSuccessScreen
          ? !isCommonPTPropertyScreen
            ? window.location.href.includes("/citizen/fsm/new-application/street")
              ? window.history.go(getBackPageNumber())
              : (history.goBack(),
                window.location.href.includes("/citizen/pt/property/new-application/property-type")
                  ? sessionStorage.setItem("docReqScreenByBack", true)
                  : null)
            : null
          : null;
      }}
    >
      {variant == "black" ? (
        <React.Fragment>
          <ArrowLeft />
          <p>{t("CS_COMMON_BACK")}</p>
        </React.Fragment>
      ) : (
        <ArrowLeftWhite />
      )}
    </div>
  );
};
export default withRouter(BackButton);
