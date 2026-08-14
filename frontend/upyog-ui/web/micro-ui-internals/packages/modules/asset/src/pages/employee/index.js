import { PrivateRoute,BreadCrumb,AppContainer,BackButton } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { useTranslation } from "react-i18next";
import { Link, useLocation, Routes, Route } from "react-router-dom";
import { ASSETLinks } from "../../Module";
import SearchApp from "./SearchApp";
import SearchReport from "./SearchReport";
import Inbox from "./Inbox";
import "../../css/asset-inline-auto.css";
const EmployeeApp = ({
  path,
  url,
  userType
}) => {
  const {
    t
  } = useTranslation();
  const location = useLocation();
  const mobileView = innerWidth <= 640;
  sessionStorage.removeItem("revalidateddone");
  const isMobile = window.Digit.Utils.browser.isMobile();
  const inboxInitialState = {
    searchParams: {
      uuid: {
        code: "ASSIGNED_TO_ALL",
        name: "ES_INBOX_ASSIGNED_TO_ALL"
      },
      services: ["asset-create"],
      applicationStatus: [],
      locality: []
    }
  };
  const AssetBreadCrumbs = ({
    location
  }) => {
    const {
      t
    } = useTranslation();
    const search = useLocation().search;
    const fromScreen = new URLSearchParams(search).get("from") || null;
    const {
      from: fromScreen2
    } = Digit.Hooks.useQueryParams();
    const crumbs = [{
      path: "/upyog-ui/employee",
      content: t("ES_COMMON_HOME"),
      show: true
    }, {
      path: "/upyog-ui/employee/asset/assetservice/inbox",
      content: t("ES_TITLE_INBOX"),
      show: location.pathname.includes("asset/assetservice/inbox") ? false : false
    }];
    return <BreadCrumb style={isMobile ? { display: "flex"} : { margin: "0 0 4px", color: "#000000" }} spanStyle={{ maxWidth: "min-content" }} crumbs={crumbs} />;
  };
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
  const EnhancedReport = Digit?.ComponentRegistryService?.getComponent("EnhancedReport");

  return (
    <AppContainer>
      <React.Fragment>
        <div className="ground-container">
          {!isRes ? (
            <div className={`registration-form ${isNewRegistration ?  "default" : "compact"}`}>
              <BackButton location={location} />
              <span className="asset-auto-224">|</span>
              <AssetBreadCrumbs location={location} />
            </div>
          ) : null}
          <Routes>
            <Route path="/*" element={<PrivateRoute><ASSETLinks userType={userType} /></PrivateRoute>} />
            <Route
              path= "assetservice/inbox/*"
              element={
                <PrivateRoute>
                  <Inbox
                    useNewInboxAPI={true}
                    businessService="asset-create"
                    filterComponent="AST_INBOX_FILTER"
                    initialStates={inboxInitialState}
                    isInbox={true}
                    parentRoute={path}
                  />
                </PrivateRoute>
              }
            />
            <Route path= "assetservice/assign-assets/:id" element={<PrivateRoute><NewAssetAssignApplication /></PrivateRoute>} />
            <Route path= "assetservice/maintenance-assets/:id" element={<PrivateRoute><MaintenanceApplication /></PrivateRoute>} />
            <Route path= "assetservice/dispose-assets/:id" element={<PrivateRoute><DisposeApplication /></PrivateRoute>} />
            <Route path= "assetservice/return-assets/:id" element={<PrivateRoute><NewAssetReturnApplication  /></PrivateRoute>} />
            <Route path= "assetservice/edit/:id" element={<PrivateRoute><EditAsset /></PrivateRoute>} />
            <Route path= "assetservice/new-assets/*" element={<PrivateRoute><ASSETCreate /></PrivateRoute>} />
            <Route path= "assetservice/application-details/:id" element={<PrivateRoute><ApplicationDetails /></PrivateRoute>} />
            <Route path= "assetservice/applicationsearch/application-details/:id" element={<PrivateRoute><ApplicationDetails /></PrivateRoute>} />
            <Route path= "assetservice/assign-response/*" element={<PrivateRoute><Response  /></PrivateRoute>} />
            <Route path= "assetservice/maintenance/*" element={<PrivateRoute><Maintenance  /></PrivateRoute>} />
            <Route path= "assetservice/edit-maintenance/*" element={<PrivateRoute><EditMaintenance  /></PrivateRoute>} />
            <Route path= "assetservice/maintenance-edit/:id" element={<PrivateRoute><EditAssetMaintenance /></PrivateRoute>} />
            <Route path= "assetservice/asset-dispose-response/*" element={<PrivateRoute><DisposeResponse  /></PrivateRoute>} />
            <Route path= "assetservice/asset-process-depreciation-response/*" element={<PrivateRoute><ProcessDepreciationResponse  /></PrivateRoute>} />
            <Route path= "assetservice/return-response/*" element={<PrivateRoute><ReturnResponse  /></PrivateRoute>} />
            {/* <Route path= "assetservice/search/*" element={<PrivateRoute><Search /></PrivateRoute>} /> */}
            <Route path= "assetservice/my-asset/*" element={<PrivateRoute><SearchApp  /></PrivateRoute>} />
            <Route path= "assetservice/report/*" element={<PrivateRoute><SearchReport  /></PrivateRoute>} />
            <Route path= "assetservice/edit-response/*" element={<PrivateRoute><EditResponse /></PrivateRoute>} />
            
            <Route path="AssetapplicationReport/*" element={<PrivateRoute><EnhancedReport parentRoute={path} moduleName="rainmaker-asset" reportName="AssetapplicationReport" /></PrivateRoute>} />
            <Route path="LandReport/*" element={<PrivateRoute><EnhancedReport parentRoute={path} moduleName="rainmaker-asset" reportName="LandReport" /></PrivateRoute>} />
            <Route path="AssetapplicationReportULBwise/*" element={<PrivateRoute><EnhancedReport parentRoute={path} moduleName="rainmaker-asset" reportName="AssetapplicationReportULBwise" /></PrivateRoute>} />
            <Route path="AssetCountReport/*" element={<PrivateRoute><EnhancedReport parentRoute={path} moduleName="rainmaker-asset" reportName="AssetCountReport" /></PrivateRoute>} />
<Route path="AssetMaintenanceReport/*" element={<PrivateRoute><EnhancedReport parentRoute={path} moduleName="rainmaker-asset" reportName="AssetMaintenanceReport" /></PrivateRoute>} />
<Route path="AssetDisposalReport/*" element={<PrivateRoute><EnhancedReport parentRoute={path} moduleName="rainmaker-asset" reportName="AssetDisposalReport" /></PrivateRoute>} />
<Route path="AssetAssignmentReport/*" element={<PrivateRoute><EnhancedReport parentRoute={path} moduleName="rainmaker-asset" reportName="AssetAssignmentReport" /></PrivateRoute>} />
<Route path="DetailedBuildingAssetsReport/*" element={<PrivateRoute><EnhancedReport parentRoute={path} moduleName="rainmaker-asset" reportName="DetailedBuildingAssetsReport" /></PrivateRoute>} />
<Route path="PlantsMachineryReport/*" element={<PrivateRoute><EnhancedReport parentRoute={path} moduleName="rainmaker-asset" reportName="PlantsMachineryReport" /></PrivateRoute>} />  
              <Route
                path="LandReport"
                element={
                  <PrivateRoute>
                    <EnhancedReport parentRoute={path} moduleName="rainmaker-asset" reportName="LandReport" />
                  </PrivateRoute>
                }
              />

              <Route
                path="AssetapplicationReportULBwise"
                element={
                  <PrivateRoute>
                    <EnhancedReport parentRoute={path} moduleName="rainmaker-asset" reportName="AssetapplicationReportULBwise" />
                  </PrivateRoute>
                }
              />

              <Route
                path="AssetCountReport"
                element={
                  <PrivateRoute>
                    <EnhancedReport parentRoute={path} moduleName="rainmaker-asset" reportName="AssetCountReport" />
                  </PrivateRoute>
                }
              />

              <Route
                path="AssetMaintenanceReport"
                element={
                  <PrivateRoute>
                    <EnhancedReport parentRoute={path} moduleName="rainmaker-asset" reportName="AssetMaintenanceReport" />
                  </PrivateRoute>
                }
              />

              <Route
                path="AssetDisposalReport"
                element={
                  <PrivateRoute>
                    <EnhancedReport parentRoute={path} moduleName="rainmaker-asset" reportName="AssetDisposalReport" />
                  </PrivateRoute>
                }
              />

              <Route
                path="AssetAssignmentReport"
                element={
                  <PrivateRoute>
                    <EnhancedReport parentRoute={path} moduleName="rainmaker-asset" reportName="AssetAssignmentReport" />
                  </PrivateRoute>
                }
              />

              <Route
                path="DetailedBuildingAssetsReport"
                element={
                  <PrivateRoute>
                    <EnhancedReport parentRoute={path} moduleName="rainmaker-asset" reportName="DetailedBuildingAssetsReport" />
                  </PrivateRoute>
                }
              />

              <Route
                path="PlantsMachineryReport"
                element={
                  <PrivateRoute>
                    <EnhancedReport parentRoute={path} moduleName="rainmaker-asset" reportName="PlantsMachineryReport" />
                  </PrivateRoute>
                }
              />
          </Routes>
        </div>
      </React.Fragment>
    </AppContainer>
  );
};
export default EmployeeApp;
