import {AppContainer, PrivateRoute,BackButton } from "@egovernments/digit-ui-react-components";
import React from "react";
import { useTranslation } from "react-i18next";
import { Link, Switch, useLocation } from "react-router-dom";
import { shouldHideBackButton } from "../../utils";
const CitizenApp = ({ path, url, userType }) => {
  const { t } = useTranslation();
  const location = useLocation();
  const mobileView = innerWidth <= 640;
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const inboxInitialState = {
    searchParams: {
      tenantId: tenantId,
    },
  };
  const hideBackButtonConfig = [
    { screenPath: "" },
  ];
  const WmsSorList = Digit?.ComponentRegistryService?.getComponent("WmsSorList");
  const WmsSorCreate = Digit?.ComponentRegistryService?.getComponent("WmsSorCreate");
  const WmsSorDetails = Digit?.ComponentRegistryService?.getComponent("WmsSorDetails");  
  const WmsSorEdit = Digit?.ComponentRegistryService?.getComponent("WmsSorEdit");
  const WmsSorResponse = Digit?.ComponentRegistryService?.getComponent("WmsSorResponse");
  
  const WmsSchList = Digit?.ComponentRegistryService?.getComponent("WmsSchList");
  const WmsSchCreate = Digit?.ComponentRegistryService?.getComponent("WmsSchCreate");
  const WmsSchDetails = Digit?.ComponentRegistryService?.getComponent("WmsSchDetails");  
  const WmsSchEdit = Digit?.ComponentRegistryService?.getComponent("WmsSchEdit");
  const WmsSchResponse = Digit?.ComponentRegistryService?.getComponent("WmsSchResponse");

  const WmsPrjList = Digit?.ComponentRegistryService?.getComponent("WmsPrjList");
  const WmsPrjCreate = Digit?.ComponentRegistryService?.getComponent("WmsPrjCreate");
  const WmsPrjDetails = Digit?.ComponentRegistryService?.getComponent("WmsPrjDetails");  
  const WmsPrjEdit = Digit?.ComponentRegistryService?.getComponent("WmsPrjEdit");
  const WmsPrjResponse = Digit?.ComponentRegistryService?.getComponent("WmsPrjResponse");

  const PMCreate = Digit?.ComponentRegistryService?.getComponent("PMCreate");
  return (
    <span className={"pt-citizen"}>
    <Switch>
      <AppContainer>
      {shouldHideBackButton(hideBackButtonConfig) ? <BackButton>Back</BackButton> : ""}
        <div className="ground-container">
          
          <PrivateRoute
            path={`${path}/sor-home`}
            component={() => (
              <WmsSorList parentRoute={path} businessService="WMS" filterComponent="WMS_LIST_FILTER" initialStates={inboxInitialState} isInbox={true} />
            )}
          />
          <PrivateRoute path={`${path}/sor-create`} component={() => <WmsSorCreate />} />
          <PrivateRoute path={`${path}/sor-details/:id`} component={() => <WmsSorDetails />} />
          <PrivateRoute path={`${path}/sor-edit/:id`} component={() => <WmsSorEdit />} />
          <PrivateRoute path={`${path}/response`} component={(props) => <WmsSorResponse {...props} parentRoute={path} />} />
          
          <PrivateRoute
            path={`${path}/sch-home`}
            component={() => (
              <WmsSchList parentRoute={path} businessService="WMS" filterComponent="WMS_LIST_FILTER" initialStates={inboxInitialState} isInbox={true} />
            )}
          />
          <PrivateRoute path={`${path}/sch-create`} component={() => <WmsSchCreate />} />
          <PrivateRoute path={`${path}/sch-details/:id`} component={() => <WmsSchDetails />} />
          <PrivateRoute path={`${path}/sch-edit/:id`} component={() => <WmsSchEdit />} />
          <PrivateRoute path={`${path}/response`} component={(props) => <WmsSchResponse {...props} parentRoute={path} />} />

          <PrivateRoute
            path={`${path}/prjmst-home`}
            component={() => (
              <WmsPrjList parentRoute={path} businessService="WMS" filterComponent="WMS_LIST_FILTER" initialStates={inboxInitialState} isInbox={true} />
            )}
          />
          <PrivateRoute path={`${path}/prjmst-create`} component={() => <WmsPrjCreate />} />
          <PrivateRoute path={`${path}/prjmst-details/:id`} component={() => <WmsPrjDetails />} />
          <PrivateRoute path={`${path}/prjmst-edit/:id`} component={() => <WmsPrjEdit />} />
          <PrivateRoute path={`${path}/response`} component={(props) => <WmsPrjResponse {...props} parentRoute={path} />} />
          
          <PrivateRoute path={`${path}/pm-home`} component={() => <PMCreate />} />
          {/* <PrivateRoute path={`${path}/sor-update/:id`} component={() => <Update />} /> */}
        </div>
        </AppContainer>
      </Switch>
    </span>
  );
};

export default CitizenApp;
