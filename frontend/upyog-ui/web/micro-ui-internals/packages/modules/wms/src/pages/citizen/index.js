import {AppContainer, PrivateRoute } from "@egovernments/digit-ui-react-components";
import React from "react";
import { useTranslation } from "react-i18next";
import { Link, Switch, useLocation } from "react-router-dom";

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
  if(path!=undefined )
  {
    //alert("in:"+path);
  }
  const List = Digit?.ComponentRegistryService?.getComponent("WmsSorList");
  const Details = Digit?.ComponentRegistryService?.getComponent("WmsSorDetails");
  const Create = Digit?.ComponentRegistryService?.getComponent("WmsSorCreate");
  const PMCreate = Digit?.ComponentRegistryService?.getComponent("PMCreate");
  
  const Update = Digit?.ComponentRegistryService?.getComponent("WmsSorUpdate");
  const Edit = Digit?.ComponentRegistryService?.getComponent("WmsSorEdit");
  const Response = Digit?.ComponentRegistryService?.getComponent("Response");
  return (
    <span className={"pt-citizen"}>
    <Switch>
      <AppContainer>

        <div className="ground-container">
          
          <PrivateRoute
            path={`${path}/sor-home`}
            component={() => (
              <List parentRoute={path} businessService="WMS" filterComponent="WMS_LIST_FILTER" initialStates={inboxInitialState} isInbox={true} />
            )}
          />
          <PrivateRoute path={`${path}/pm-home`} component={() => <PMCreate />} />
          <PrivateRoute path={`${path}/sor-create`} component={() => <Create />} />
          <PrivateRoute path={`${path}/response`} component={(props) => <Response {...props} parentRoute={path} />} />
          <PrivateRoute path={`${path}/sor-details/:id`} component={() => <Details />} />
          <PrivateRoute path={`${path}/sor-edit/:id`} component={() => <Edit />} />
          {/* <PrivateRoute path={`${path}/sor-update/:id`} component={() => <Update />} /> */}
        </div>
        </AppContainer>
      </Switch>
    </span>
  );
};

export default CitizenApp;
