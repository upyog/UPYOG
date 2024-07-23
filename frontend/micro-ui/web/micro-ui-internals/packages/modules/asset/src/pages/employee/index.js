import { PrivateRoute,BreadCrumb,AppContainer,BackButton } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { useTranslation } from "react-i18next";
import { Link, Switch, useLocation } from "react-router-dom";
import { ASSETLinks } from "../../Module";
import SearchApp from "./SearchApp";
import SearchReport from "./SearchReport";
import Inbox from "./Inbox";

const EmployeeApp = ({ path, url, userType }) => {
  console.log("pathhhh",path,url);
  const { t } = useTranslation();
  const location = useLocation();
  const mobileView = innerWidth <= 640;
  sessionStorage.removeItem("revalidateddone");
  const isMobile = window.Digit.Utils.browser.isMobile();

  const inboxInitialState = {
    searchParams: {
      uuid: { code: "ASSIGNED_TO_ALL", name: "ES_INBOX_ASSIGNED_TO_ALL" },
      services: ["asset-create"],
      applicationStatus: [],
      locality: [],

    },
  };

  
 

  const AssetBreadCrumbs = ({ location }) => {
    const { t } = useTranslation();
    const search = useLocation().search;
    const fromScreen = new URLSearchParams(search).get("from") || null;
    const { from : fromScreen2 } = Digit.Hooks.useQueryParams();
    const crumbs = [
      {
        path: "/digit-ui/employee",
        content: t("ES_COMMON_HOME"),
        show: true,
      },
      {
        path: "/digit-ui/employee/asset/assetservice/inbox",
        content: t("ES_TITLE_INBOX"),
        show: location.pathname.includes("asset/assetservice/inbox") ? true : false,
      },
     
    
      {
        path: "/digit-ui/employee/asset/assetservice/my-asset",
        content: t("ES_COMMON_APPLICATION_SEARCH"),
        show: location.pathname.includes("asset/assetservice/my-asset") || location.pathname.includes("/asset/assetservice/applicationsearch/application-details/") ? true : false,
      },
      
     
      
    ];
  
    return <BreadCrumb style={isMobile?{display:"flex"}:{}}  spanStyle={{maxWidth:"min-content"}} crumbs={crumbs} />;
  }

  
  const NewAssetAssignApplication = Digit?.ComponentRegistryService?.getComponent("AssignAssetApplication");
  const NewAssetReturnApplication = Digit?.ComponentRegistryService?.getComponent("returnAssets");
  const ApplicationDetails = Digit?.ComponentRegistryService?.getComponent("ApplicationDetails");
  const ASSETCreate = Digit?.ComponentRegistryService?.getComponent("AssetCreateNew");
  const Response = Digit?.ComponentRegistryService?.getComponent("AssetResponse");
  const ReturnResponse = Digit?.ComponentRegistryService?.getComponent("returnResponse");
  const isRes = window.location.href.includes("asset/response");
  const isNewRegistration = window.location.href.includes("new-asset") || window.location.href.includes("modify-application") || window.location.href.includes("asset/assetservice/application-details");

  return (
    <Switch>
      <AppContainer>
      <React.Fragment>
        <div className="ground-container">
          {!isRes ? <div style={isNewRegistration ? {marginLeft: "12px" } : {marginLeft:"-4px"}}><BackButton location={location} /> </div> : null}
          <PrivateRoute exact path={`${path}/`} component={() => <ASSETLinks matchPath={path} userType={userType} />} />
          <PrivateRoute
            path={`${path}/assetservice/inbox`}
            component={() => (
              <Inbox
                useNewInboxAPI={true}
                parentRoute={path}
                businessService="asset-create"
                filterComponent="AST_INBOX_FILTER"
                initialStates={inboxInitialState}
                isInbox={true}
              />
            )}
          />
          
          <PrivateRoute path={`${path}/assetservice/assign-assets/:id`} component={() => <NewAssetAssignApplication parentUrl={url} />} />
          <PrivateRoute path={`${path}/assetservice/return-assets/:id`} component={() => <NewAssetReturnApplication parentUrl={url} />} />
          <PrivateRoute path={`${path}/assetservice/new-assets`} component={() => <ASSETCreate parentUrl={url} />} />
          <PrivateRoute path={`${path}/assetservice/application-details/:id`} component={() => <ApplicationDetails parentRoute={path} />} />
          <PrivateRoute path={`${path}/assetservice/applicationsearch/application-details/:id`} component={() => <ApplicationDetails parentRoute={path} />} />
          <PrivateRoute path={`${path}/assetservice/assign-response`} component={(props) => <Response {...props} parentRoute={path} />} />
          <PrivateRoute path={`${path}/assetservice/return-response`} component={(props) => <ReturnResponse {...props} parentRoute={path} />} />
          <PrivateRoute path={`${path}/assetservice/search`} component={(props) => <Search {...props} t={t} parentRoute={path} />} />
          <PrivateRoute path={`${path}/assetservice/my-asset`} component={(props) => <SearchApp {...props} parentRoute={path} />} />
          <PrivateRoute path={`${path}/assetservice/report`} component={(props) => <SearchReport {...props} parentRoute={path} />} />

        </div>
      </React.Fragment>
      </AppContainer>
    </Switch>
  );
};

export default EmployeeApp;
