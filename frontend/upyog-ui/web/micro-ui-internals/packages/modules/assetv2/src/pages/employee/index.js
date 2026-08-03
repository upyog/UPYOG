import { PrivateRoute,BreadCrumb,AppContainer,BackButton } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { useTranslation } from "react-i18next";
import { Link, useLocation, Routes, Route } from "react-router-dom";
import { ASSETV2Links } from "../../Module";
import SearchApp from "./SearchApp";
import SearchReport from "./SearchReport";
import Inbox from "./Inbox";

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
  const EditAsset = Digit?.ComponentRegistryService?.getComponent("editAsset");
  const NewAssetReturnApplication = Digit?.ComponentRegistryService?.getComponent("returnAssets");
  const ApplicationDetails = Digit?.ComponentRegistryService?.getComponent("ApplicationDetails");
  const ASSETCreate = Digit?.ComponentRegistryService?.getComponent("AssetCreateNew");
  const Response = Digit?.ComponentRegistryService?.getComponent("AssetResponse");
  const Maintenance = Digit?.ComponentRegistryService?.getComponent("Maintenance");
  const EditMaintenance = Digit?.ComponentRegistryService?.getComponent("EditMaintenance");
  const DisposeResponse = Digit?.ComponentRegistryService?.getComponent("DisposeResponse");
  const ProcessDepreciationResponse = Digit?.ComponentRegistryService?.getComponent("ProcessDepreciationResponse");
  const ReturnResponse = Digit?.ComponentRegistryService?.getComponent("returnResponse");
  const isRes = window.location.href.includes("asset/response");
  const isNewRegistration = window.location.href.includes("new-assets") || window.location.href.includes("asset/assetservice/application-details");

  return (
    <AppContainer>
      <React.Fragment>
        <div className="ground-container">
          {!isRes ? (
            <div style={isNewRegistration ? { marginLeft: "12px", display: "flex", alignItems: "center" } : { marginLeft: "-4px", display: "flex", alignItems: "center" }}>
              <BackButton location={location} />
              <span style={{ margin: "0 5px 16px", display: "inline-block" }}>|</span>
              <AssetBreadCrumbs location={location} />
            </div>
          ) : null}
          <Routes>
            <Route path={`/*`} element={<PrivateRoute><ASSETV2Links userType={userType} /></PrivateRoute>} />
            <Route
              path= "assetservice/inbox/*"
              element={
                <PrivateRoute>
                  <Inbox
                    useNewInboxAPI={true}
                    parentRoute={path}
                    businessService="asset-create"
                    filterComponent="AST_INBOX_FILTER"
                    initialStates={inboxInitialState}
                    isInbox={true}
                  />
                </PrivateRoute>
              }
            />
            <Route path= "assetservice/assign-assets/:id" element={<PrivateRoute><NewAssetAssignApplication /></PrivateRoute>} />
            <Route path= "assetservice/maintenance-assets/:id" element={<PrivateRoute><MaintenanceApplication /></PrivateRoute>} />
            <Route path= "assetservice/dispose-assets/:id" element={<PrivateRoute><DisposeApplication /></PrivateRoute>} />
            <Route path= "assetservice/return-assets/:id" element={<PrivateRoute><NewAssetReturnApplication /></PrivateRoute>} />
            <Route path= "assetservice/edit/:id" element={<PrivateRoute><EditAsset /></PrivateRoute>} />
            <Route path= "assetservice/new-assets/*" element={<PrivateRoute><ASSETCreate /></PrivateRoute>} />
            <Route path= "assetservice/application-details/:id" element={<PrivateRoute><ApplicationDetails /></PrivateRoute>} />
            <Route path= "assetservice/applicationsearch/application-details/:id" element={<PrivateRoute><ApplicationDetails /></PrivateRoute>} />
            <Route path= "assetservice/assign-response/*" element={<PrivateRoute><Response /></PrivateRoute>} />
            <Route path= "assetservice/maintenance/*" element={<PrivateRoute><Maintenance /></PrivateRoute>} />
            <Route path= "assetservice/edit-maintenance/*" element={<PrivateRoute><EditMaintenance /></PrivateRoute>} />
            <Route path= "assetservice/maintenance-edit/:id" element={<PrivateRoute><EditAssetMaintenance /></PrivateRoute>} />
            <Route path= "assetservice/asset-dispose-response/*" element={<PrivateRoute><DisposeResponse /></PrivateRoute>} />
            <Route path= "assetservice/asset-process-depreciation-response/*" element={<PrivateRoute><ProcessDepreciationResponse /></PrivateRoute>} />
            <Route path= "assetservice/return-response/*" element={<PrivateRoute><ReturnResponse /></PrivateRoute>} />
            {/* <Route path= "assetservice/search" element={<PrivateRoute><Search /></PrivateRoute>} /> */}
            <Route path= "assetservice/my-asset/*" element={<PrivateRoute><SearchApp /></PrivateRoute>} />
            <Route path= "assetservice/report/*" element={<PrivateRoute><SearchReport /></PrivateRoute>} />
            <Route path= "assetservice/edit-response/*" element={<PrivateRoute><EditResponse /></PrivateRoute>} />
          </Routes>
        </div>
      </React.Fragment>
    </AppContainer>
  );
};

export default EmployeeApp;
