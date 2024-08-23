import React from "react";

import { BackButton, PrivateRoute } from "@egovernments/digit-ui-react-components";
import { Switch, useLocation, useRouteMatch } from "react-router-dom";

import { useTranslation } from "react-i18next";

const App = ({}) => {
  const { t } = useTranslation();
  const { path, url, ...match } = useRouteMatch();
  const location = useLocation();
  const ApplicationDetail = Digit?.ComponentRegistryService?.getComponent("ApplicationDetail");
  const Aadhar = Digit?.ComponentRegistryService?.getComponent("AadhaarVerification");
  const AadhaarFullForm = Digit?.ComponentRegistryService?.getComponent("AadhaarFullForm");
  const SelectSchemePage = Digit?.ComponentRegistryService?.getComponent("SelectSchemePage");
  const BMCReviewPage = Digit?.ComponentRegistryService?.getComponent("BMCReviewPage");
  // const AadhaarSatutsVerificationPage = Digit?.ComponentRegistryService?.getComponent("AadhaarSatutsVerificationPage");
  return (
    <React.Fragment>
      <div className="bmc-citizen-wrapper" style={{ width: "100%" }}>
        {!location.pathname.includes("/response") && <BackButton>{t("CS_COMMON_BACK")}</BackButton>}
        <Switch>
          {/* <AppContainer> */}
            <PrivateRoute exact path={`${path}/applicationDetails`} component={ApplicationDetail} />
            <PrivateRoute exact path={`${path}/aadhaarLogin`} component={Aadhar} />
            <PrivateRoute exact path={`${path}/aadhaarForm`} component={AadhaarFullForm} />
            <PrivateRoute exact path={`${path}/selectScheme`} component={SelectSchemePage} />
            <PrivateRoute exact path={`${path}/review`} component={BMCReviewPage} />
            {/* <PrivateRoute exact path={`${path}/aadhaarSatutsVerificationPage`} component={AadhaarSatutsVerificationPage} /> */}
            {/* <PrivateRoute exact path={`${path}/aadhaarVerify`} component={AadhaarVerifyPage} />
            <PrivateRoute exact path={`${path}/aadhaarEmployee`} component={AadhaarEmployeePage} />
            <PrivateRoute exact path={`${path}/randmization`} component={RandmizationPage} />
            <PrivateRoute exact path={`${path}/crossverify`} component={CrossVerifyPage} />
            <PrivateRoute exact path={`${path}/approve`} component={ApprovePage} />
            <PrivateRoute exact path={`${path}/wardwiseapplication`} component={wardWiseApplication} />
            <PrivateRoute exact path={`${path}/schemewiseapplication`} component={schemeWiseApplication} />
            <PrivateRoute exact path={`${path}/coursewiseapplication`} component={courseWiseApplication} />
          {/* </AppContainer> */}
        </Switch>
      </div>
    </React.Fragment>
  );
};

export default App;
