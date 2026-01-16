import { CommonShareContainer, LoadingIndicator, Toast } from "components";
import commonConfig from "config/common";
import { fetchCurrentLocation, fetchLocalizationLabel, setRoute, toggleSnackbarAndSetText } from "egov-ui-kit/redux/app/actions";
import { fetchMDMSData } from "egov-ui-kit/redux/common/actions";
import { addBodyClass } from "egov-ui-kit/utils/commons";
import { getLocale } from "egov-ui-kit/utils/localStorageUtils";
import isEmpty from "lodash/isEmpty";
import React, { Component } from "react";
import { connect } from "react-redux";
import { withRouter } from "react-router";
import './index.css';
import Router from "./Router";
// import logoMseva from "egov-ui-kit/assets/images/logo-white.png";
import routes from "./Routes";
class App extends Component {
  constructor(props) {
    super(props);
    const { pathname: currentPath } = props.location;

    props.history.listen((location, action) => {
      const { pathname: nextPath } = location;
      addBodyClass(nextPath);
      props.toggleSnackbarAndSetText(false, { labelName: "", labelKey: "" }, "success");
    });

    addBodyClass(currentPath);
  }

  componentDidMount() {
    const { fetchLocalizationLabel, fetchCurrentLocation, fetchMDMSData } = this.props;
    let requestBody = {
      MdmsCriteria: {
        tenantId: commonConfig.tenantId,
        moduleDetails: [
          {
            moduleName: "common-masters",
            masterDetails: [
              {
                name: "StateInfo",
              },
            ],
          },
          {
            moduleName: "tenant",
            masterDetails: [
              {
                name: "tenants",
              },
              {
                name: "citymodule",
              },
            ],
          },
        ],
      },
    };
    // can be combined into one mdms call
    fetchLocalizationLabel(getLocale() || "en_IN");

    // current location
    fetchCurrentLocation();
    fetchMDMSData(requestBody);
  }

  componentWillReceiveProps(nextProps) {
    const { route: nextRoute, authenticated, location } = nextProps;
    const { route: currentRoute, history, setRoute } = this.props;

    if (nextRoute && currentRoute !== nextRoute) {
      history.push(nextRoute);
      setRoute("");
    }

    const isPrivacyPolicy = location && location.pathname && location.pathname.includes("privacy-policy");
    if (nextProps.hasLocalisation !== this.props.hasLocalisation && !authenticated && !isPrivacyPolicy) {
      nextProps.hasLocalisation && this.props.history.replace("/language-selection");
    }
  }

  render() {
    const { toast, loading, defaultUrl, hasLocalisation } = this.props;
    let loginScreens = false;
    let logginScreensUrls = ['/employee/user/login', '/employee/forgot-password', '/employee/language-selection'];
    if (logginScreensUrls.includes(window.location.pathname)) {
      loginScreens = true;
    }    
    let sourceUrl = `${window.location.origin}/employee`;
    sourceUrl="https://s3.ap-south-1.amazonaws.com/egov-qa-assets";  // changes for the image configured in s3 bucket

    let dashboardScreen = false;
    if (window.location.pathname.includes('integration/dss')) {
      dashboardScreen = true;
    }

    let isFixedFooter=false;
    let otherScreensUrls = []
    /*    DSS Module fixed footer removed since it is already shown in dashboard app internally
    ['/employee/integration/dss/home', '/employee/integration/dss/propertytax','/employee/integration/dss/tradelicense','/employee/integration/dss/overview','/employee/integration/dss/pgr'];*/
    if (otherScreensUrls.includes(window.location.pathname)) {
      isFixedFooter = true;
    }
    const pdfUrl = "https://pg-egov-assets.s3.ap-south-1.amazonaws.com/Upyog+Code+and+Copyright+License_v1.pdf";

    return (
      <div >
      <div style={{minHeight:'calc(100vh - 3em)'}}>
        <Router routes={routes} hasLocalisation={hasLocalisation} defaultUrl={defaultUrl} />
       </div>
        {toast && toast.open && !isEmpty(toast.message) && <Toast open={toast.open} message={toast.message} variant={toast.variant} />}
        {loading && <LoadingIndicator />}
        <CommonShareContainer componentId="rainmaker-common-share" />

        {!loginScreens && !dashboardScreen && isFixedFooter&& <div className={"jk-footer"} style={{ width: '100%', position: 'fixed', bottom: 0, color:"#22394d" }} >
            {/* <img style={{ display: "inline-flex", height: '1.4em' }} className={"jk-footer-image-cursor"} alt={"Powered by DIGIT"} src={`${sourceUrl}/digit-footer.png`} onError={"this.src='./../digit-footer.png'"} onClick={() => {
              window.open('https://www.digit.org/', '_blank').focus();
            }}></img>
            <span style={{ margin: "0 10px" }}>|</span> */}
            <span style={{ cursor: "pointer", fontSize: "16px", fontWeight: "400"}} onClick={() => { window.open('https://niua.in/', '_blank').focus();}} >Housing and Urban Development Deparment</span>
            <span style={{ margin: "0 10px" }}>|</span>
            <span style={{ cursor: "pointer", fontSize: "16px", fontWeight: "400"}} onClick={() => { window.open(pdfUrl, '_blank').focus();}}>Copyright © 2026 Government of Jammu & Kashmir</span>
        </div>}

        {!loginScreens && !dashboardScreen && !isFixedFooter&&<div style={{ width: '100%', display: 'flex', flexFlow: 'column', position:"fixed", bottom: "0" }}>
          <div style={{ display: 'flex', justifyContent: 'center', color:"#22394d" }}>
            {/* <img style={{ display: "inline-flex", height: '1.4em' }} className={"jk-footer-image-cursor"} alt={"Powered by DIGIT"} src={`${sourceUrl}/digit-footer.png`} onError={"this.src='./../digit-footer.png'"} onClick={() => {
              window.open('https://www.digit.org/', '_blank').focus();
            }}></img>
            <span style={{ margin: "0 10px" }}>|</span> */}
            <span style={{ cursor: "pointer", fontSize: "16px", fontWeight: "400"}} onClick={() => { window.open('https://niua.in/', '_blank').focus();}} >Housing and Urban Development Deparment</span>
            <span style={{ margin: "0 10px" }}>|</span>
            <span style={{ cursor: "pointer", fontSize: "16px", fontWeight: "400"}} onClick={() => { window.open(pdfUrl, '_blank').focus();}}>Copyright © 2026 Government of Jammu & Kashmir</span>
          </div>
        </div>}
        
        {loginScreens && <div style={{ width: '100%', position: 'fixed', bottom: 0 }} >
          <div style={{ display: 'flex', justifyContent: 'center', color:"white" }}>
            {/* <img style={{ display: "inline-flex", height: '1.4em' }} className={"jk-footer-image-cursor"} alt={"Powered by DIGIT"} src={`${sourceUrl}/digit-footer-bw.png`} onError={"this.src='./../digit-footer.png'"} onClick={() => {
              window.open('https://www.digit.org/', '_blank').focus();
            }}></img>
            <span style={{ margin: "0 10px" }}>|</span> */}
            <span style={{ cursor: "pointer", fontSize: "16px", fontWeight: "400"}} onClick={() => { window.open('https://niua.in/', '_blank').focus();}} >Housing and Urban Development Deparment</span>
            <span style={{ margin: "0 10px" }}>|</span>
            <span style={{ cursor: "pointer", fontSize: "16px", fontWeight: "400"}} onClick={() => { window.open(pdfUrl, '_blank').focus();}}>Copyright © 2026 Government of Jammu & Kashmir</span>
          </div>
        </div>}
      </div>
    );
  }
}

const mapStateToProps = (state, ownProps) => {
  const { route, toast } = state.app;
  const { auth } = state;
  const { authenticated } = auth || false;
  const props = {};
  const { spinner } = state.common;
  const { stateInfoById } = state.common || [];
  let hasLocalisation = false;
  let defaultUrl = process.env.REACT_APP_NAME === "Citizen" ? "/user/register" : "/user/login";
  if (stateInfoById && stateInfoById.length > 0) {
    hasLocalisation = stateInfoById[0].hasLocalisation;
    defaultUrl = stateInfoById[0].defaultUrl;
  }
  const loading = ownProps.loading || spinner;
  if (route && route.length) {
    props.route = route;
  }
  if (toast && toast.open && toast.message && !isEmpty(toast.message)) {
    props.toast = toast;
  }
  return {
    ...props,
    loading,
    hasLocalisation,
    defaultUrl,
    authenticated,
  };
};

const mapDispatchToProps = (dispatch) => {
  return {
    fetchLocalizationLabel: (locale) => dispatch(fetchLocalizationLabel(locale)),
    toggleSnackbarAndSetText: (open, message, error) => dispatch(toggleSnackbarAndSetText(open, message, error)),
    fetchMDMSData: (criteria) => dispatch(fetchMDMSData(criteria)),
    fetchCurrentLocation: () => dispatch(fetchCurrentLocation()),
    setRoute: (route) => dispatch(setRoute(route)),
  };
};

export default withRouter(
  connect(
    mapStateToProps,
    mapDispatchToProps
  )(App)
);
