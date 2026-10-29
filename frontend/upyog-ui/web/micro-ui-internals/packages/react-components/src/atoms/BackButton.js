import React from "react";
import { ArrowLeft, ArrowLeftWhite } from "./svgindex";
import { useTranslation } from "react-i18next";
import { useLocation, useParams } from "react-router-dom";

const withRouter = (Component) => {
  return (props) => {
    const navigate = Digit.Hooks.useCustomNavigate();
    const location = useLocation();
    const params = useParams();
    
    const history = {
      push: (path, state) => navigate(path, { state }),
      replace: (path, state) => navigate(path, { replace: true, state }),
      go: (n) => navigate(n),
      goBack: () => {
        if (window.history.length > 1 && window.history.state && window.history.state.idx > 0) {
          navigate(-1);
        } else {
          // Fallback when accessed directly
          navigate(location.pathname.includes("/employee") ? "/upyog-ui/employee" : "/upyog-ui/citizen");
        }
      },
      goForward: () => navigate(1),
      location,
    };
    
    return <Component {...props} history={history} location={location} match={{ params }} />;
  };
};

const BackButton = ({ history, onClick, style, isSuccessScreen, isCommonPTPropertyScreen, getBackPageNumber, className = "", variant = "black" }) => {
  const { t } = useTranslation();
  return (
    <div
      className={`back-btn2 ${className}`}
      style={style ? style : {}}
      onClick={(e) => {
        if (onClick) {
          return onClick(e);
        }
        !isSuccessScreen
          ? !isCommonPTPropertyScreen
            ? window.location.href.includes("/citizen/fsm/new-application/street")
              ? window.history.go(typeof getBackPageNumber === "function" ? getBackPageNumber() : -4)
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

