import { PrivateRoute,BreadCrumb,AppContainer,BackButton } from "@upyog/digit-ui-react-components";
import React from "react";
import { useTranslation } from "react-i18next";
import { Link, Switch, useLocation } from "react-router-dom";
import { ASSETLinks } from "../../Module";
import SearchApp from "./SearchApp";
import SearchReport from "./SearchReport";
import Inbox from "./Inbox";

const Search = SearchApp; // Using SearchApp as Search component

const EmployeeApp = ({ path, url, userType }) => {
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
        path: "/upyog-ui/employee",
        content: t("ES_COMMON_HOME"),
        show: true,
      },
      {
        path: "/upyog-ui/employee/asset/assetservice/inbox",
        content: t("ES_TITLE_INBOX"),
        show: location.pathname.includes("asset/assetservice/inbox") ? false : false,
      },
    ];
    return <BreadCrumb style={isMobile?{display:"flex"}:{margin: "0 0 4px", color:"#000000" }}  spanStyle={{maxWidth:"min-content"}} crumbs={crumbs} />;
  }

  
  const NewAssetAssignApplication = Digit?.ComponentRegistryService?.getComponent("AssignAssetApplication");
  const DisposeApplication = Digit?.ComponentRegistryService?.getComponent("DisposeApplication");
  const MaintenanceApplication = Digit?.ComponentRegistryService?.getComponent("MaintenanceApplication");
  const EditAssetMaintenance = Digit?.ComponentRegistryService?.getComponent("EditAssetMaintenance");
  const EditResponse = Digit?.ComponentRegistryService?.getComponent("editResponse");
  const EditVendorResponse = Digit?.ComponentRegistryService?.getComponent("EditVendorResponse"); 
  const EditAsset = Digit?.ComponentRegistryService?.getComponent("editAsset");
  const NewAssetReturnApplication = Digit?.ComponentRegistryService?.getComponent("returnAssets");
  const ApplicationDetails = Digit?.ComponentRegistryService?.getComponent("ApplicationDetails");
  const ASSETCreate = Digit?.ComponentRegistryService?.getComponent("AssetCreateNew");
  const InventoryCreation = Digit?.ComponentRegistryService?.getComponent("InventoryCreation");
  const AssetRegistryUp = Digit?.ComponentRegistryService?.getComponent("AssetRegistryUp");
  const EditAssetRegistryUp = Digit?.ComponentRegistryService?.getComponent("EditAssetRegistryUp");
  const EditAssetVendorUp = Digit?.ComponentRegistryService?.getComponent("EditAssetVendorUp");
  const EditProcurementRequest = Digit?.ComponentRegistryService?.getComponent("EditProcurementRequest");
  const CreateProcurement = Digit?.ComponentRegistryService?.getComponent("ProcurementRequest");
  const VendorForUP = Digit?.ComponentRegistryService?.getComponent("VendorInventoryForUP");
  const EditProcurementResponse = Digit?.ComponentRegistryService?.getComponent("EditProcurementResponse");

  const AssetRegistryList = Digit?.ComponentRegistryService?.getComponent("AssetRegistryList");
  const AssetVendorList = Digit?.ComponentRegistryService?.getComponent("AssetVendorList");
  const ProcurementList = Digit?.ComponentRegistryService?.getComponent("ProcurementList");
  const Response = Digit?.ComponentRegistryService?.getComponent("AssetResponse");
  const Maintenance = Digit?.ComponentRegistryService?.getComponent("Maintenance");
  const EditMaintenance = Digit?.ComponentRegistryService?.getComponent("EditMaintenance");
  const DisposeResponse = Digit?.ComponentRegistryService?.getComponent("DisposeResponse");
  const ProcessDepreciationResponse = Digit?.ComponentRegistryService?.getComponent("ProcessDepreciationResponse");
  const ReturnResponse = Digit?.ComponentRegistryService?.getComponent("returnResponse");
  const isRes = window.location.href.includes("asset/response");
  const isNewRegistration = window.location.href.includes("new-assets") || window.location.href.includes("asset/assetservice/application-details");
  return (
    <Switch>
      <AppContainer>
      <React.Fragment>
        <div className="ground-container">
        {!isRes ? 
              <div style={isNewRegistration ? { marginLeft: "12px",display: "flex", alignItems: "center" } : { marginLeft: "-4px",display: "flex", alignItems: "center" }}>
                  <BackButton location={location} />
                  <span style={{ margin: "0 5px 16px", display: "inline-block" }}>|</span>
                  <AssetBreadCrumbs location={location} />
               
              </div>
          : null}
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
          <PrivateRoute path={`${path}/assetservice/maintenance-assets/:id`} component={() => <MaintenanceApplication parentUrl={url} />} />
          <PrivateRoute path={`${path}/assetservice/dispose-assets/:id`} component={() => <DisposeApplication parentUrl={url} />} />
          <PrivateRoute path={`${path}/assetservice/return-assets/:id`} component={() => <NewAssetReturnApplication parentUrl={url} />} />
          <PrivateRoute path={`${path}/assetservice/edit/:id`} component={() => <EditAsset parentUrl={url} />} />
          <PrivateRoute path={`${path}/assetservice/new-assets`} component={() => <ASSETCreate parentUrl={url} />} />
          <PrivateRoute path={`${path}/assetservice/application-details/:id`} component={() => <ApplicationDetails parentRoute={path} />} />
          <PrivateRoute path={`${path}/assetservice/applicationsearch/application-details/:id`} component={() => <ApplicationDetails parentRoute={path} />} />
          <PrivateRoute path={`${path}/assetservice/assign-response`} component={(props) => <Response {...props} parentRoute={path} />} />
          <PrivateRoute path={`${path}/assetservice/maintenance`} component={(props) => <Maintenance {...props} parentRoute={path} />} />
          <PrivateRoute path={`${path}/assetservice/edit-maintenance`} component={(props) => <EditMaintenance {...props} parentRoute={path} />} />
          <PrivateRoute path={`${path}/assetservice/maintenance-edit/:id`} component={() => <EditAssetMaintenance parentUrl={url} />} />
          <PrivateRoute path={`${path}/assetservice/asset-dispose-response`} component={(props) => <DisposeResponse {...props} parentRoute={path} />} />
          <PrivateRoute path={`${path}/assetservice/asset-process-depreciation-response`} component={(props) => <ProcessDepreciationResponse {...props} parentRoute={path} />} />
          <PrivateRoute path={`${path}/assetservice/return-response`} component={(props) => <ReturnResponse {...props} parentRoute={path} />} />
          <PrivateRoute path={`${path}/assetservice/search`} component={(props) => <Search {...props} t={t} parentRoute={path} />} />
          <PrivateRoute path={`${path}/assetservice/my-asset`} component={(props) => <SearchApp {...props} parentRoute={path} />} />
          <PrivateRoute path={`${path}/assetservice/report`} component={(props) => <SearchReport {...props} parentRoute={path} />} />
          <PrivateRoute path={`${path}/assetservice/edit-response`} component={(props) => <EditResponse {...props} parentRoute={path} />} />
          <PrivateRoute path={`${path}/assetservice-up/vendor`} component={(props) => <VendorForUP {...props} parentRoute={path} />} />
          <PrivateRoute path={`${path}/assetservice-up/asset-registry`} component={(props) => <AssetRegistryUp {...props} parentRoute={path} />} />
          <PrivateRoute path={`${path}/assetservice-up/procurement`} component={(props) => <CreateProcurement {...props} parentRoute={path} />} />
          <PrivateRoute path={`${path}/assetservice-up/inventory-creation`} component={(props) => <InventoryCreation {...props} parentRoute={path} />} />
          <PrivateRoute path={`${path}/assetservice/asset-edit/:id`} component={() => <EditAssetRegistryUp  parentRoute={path} />} />
          <PrivateRoute path={`${path}/assetservice-up/registry/list`} component={(props) => <AssetRegistryList {...props} parentRoute={path} />} />
          <PrivateRoute path={`${path}/assetservice-up/vendor-list`} component={(props) => <AssetVendorList {...props} parentRoute={path} />} />
          <PrivateRoute path={`${path}/assetservice/asset-vendor-edit/:id`} component={() => <EditAssetVendorUp  parentRoute={path} />} />
          <PrivateRoute path={`${path}/assetservice/edit-vendor-response`} component={(props) => <EditVendorResponse {...props} parentRoute={path} />} />
           <PrivateRoute path={`${path}/assetservice-up/procurement-list`} component={(props) => <ProcurementList {...props} parentRoute={path} />} />
           <PrivateRoute path={`${path}/assetservice/procurement-edit/:id`} component={() => <EditProcurementRequest  parentRoute={path} />} />
           <PrivateRoute path={`${path}/assetservice/edit-procurement-response`} component={(props) => <EditProcurementResponse {...props} parentRoute={path} />} />
        </div>
      </React.Fragment>
      </AppContainer>

    </Switch>
  );
};
export default EmployeeApp;
