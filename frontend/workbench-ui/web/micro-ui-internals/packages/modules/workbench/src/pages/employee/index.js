import React, { useEffect } from "react";
import { Routes, Route, useLocation } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { PrivateRoute, AppContainer, BreadCrumb } from "@upyog/workbench-ui-react-components";
import LocalisationSearch from "./LocalisationSearch";
import ApplyWorkflow from "./ApplyWorkflow";
import MDMSSearch from "./MDMSSearch";
import MDMSAdd from "./MDMSAdd";
import MDMSAddV2 from "./MDMSAddV2";
import MDMSEdit from "./MDMSEdit";
import MDMSView from "./MDMSView";
import MDMSSearchv2 from "./MDMSSearchv2";
import MDMSManageMaster from "./MDMSManageMaster";
import LocalisationAdd from "./LocalisationAdd";

const WorkbenchBreadCrumb = ({ location, defaultPath }) => {
  const { t } = useTranslation();
  const search = useLocation().search;
  const fromScreen = new URLSearchParams(search).get("from") || null;
  const pathVar = location.pathname.replace(defaultPath + '/', "").split("?")?.[0];
  const { masterName, moduleName, uniqueIdentifier } = Digit.Hooks.useQueryParams()

  const crumbs = [
    {
      path: `/${window?.contextPath}/employee`,
      content: t("WORKBENCH_HOME"),
      show: true,
    },
    {
      path: `/${window?.contextPath}/employee/workbench/manage-master-data`,
      content: t(`WBH_MANAGE_MASTER_DATA`),
      show: pathVar.includes("mdms-") ? true : false,
      // query:`moduleName=${moduleName}&masterName=${masterName}`
    },
    {
      path: `/${window?.contextPath}/employee/workbench/localisation-search`,
      content: t(`LOCALISATION_SEARCH`),
      show: pathVar.includes("localisation-") ? true : false,
      isBack: pathVar.includes("localisation-search") ? true : false
      // query:`moduleName=${moduleName}&masterName=${masterName}`
    },

    {
      path: `/${window?.contextPath}/employee/workbench/mdms-search-v2`,
      query: `moduleName=${moduleName}&masterName=${masterName}`,
      content: t(`${Digit.Utils.workbench.getMDMSLabel(pathVar, masterName, moduleName)}`),
      show: (masterName && moduleName) ? true : false,
      isBack: pathVar.includes("mdms-search-v2") ? true : false
    },
    {
      path: `/${window?.contextPath}/employee/workbench/mdms-view`,
      content: t(`MDMS_VIEW`),
      show: pathVar.includes("mdms-edit") ? true : false,
      query: `moduleName=${moduleName}&masterName=${masterName}&uniqueIdentifier=${uniqueIdentifier}`
    },
    {
      path: `/${window?.contextPath}/employee/masters/response`,
      content: t(`${Digit.Utils.workbench.getMDMSLabel(pathVar, "", "")}`),
      show: Digit.Utils.workbench.getMDMSLabel(pathVar, "", "", ["mdms-search-v2", "localisation-search"]) ? true : false,
    },

  ];
  return <BreadCrumb className="workbench-bredcrumb" crumbs={crumbs} spanStyle={{ maxWidth: "min-content" }} />;
};

const App = ({ path }) => {
  const location = useLocation();
  const MDMSCreateSession = Digit.Hooks.useSessionStorage("MDMS_add", {});
  const [sessionFormData, setSessionFormData, clearSessionFormData] = MDMSCreateSession;

  const MDMSViewSession = Digit.Hooks.useSessionStorage("MDMS_view", {});
  const [sessionFormDataView, setSessionFormDataView, clearSessionFormDataView] = MDMSViewSession

  useEffect(() => {
    // Function to clear session storage for keys with specific prefixes
    const clearSessionStorageWithPrefix = (prefix) => {
      Object.keys(sessionStorage).forEach((key) => {
        if (key.startsWith(`Digit.${prefix}`)) {
          sessionStorage.removeItem(key);
        }
      });
    };
    const currentUrl = window.location.href;
    if (!currentUrl.includes("mdms-add-v2") && !currentUrl.includes("mdms-add-v4") && !currentUrl.includes("mdms-view")) {
      clearSessionStorageWithPrefix('MDMS_add');
    }
    if (!currentUrl.includes("mdms-view")) {
      clearSessionStorageWithPrefix('MDMS_view');
    }
    if (!currentUrl.includes("mdms-edit")) {
      clearSessionStorageWithPrefix('MDMS_edit');
    }
  }, [window.location.href]);

  useEffect(() => {
    if (!window.location.href.includes("mdms-add-v2") && sessionFormData && Object.keys(sessionFormData) != 0) {
      clearSessionFormData();
    }
    if (!window.location.href.includes("mdms-view") && sessionFormDataView) {
      clearSessionFormDataView();
    }
  }, [location]);

  return (
    <React.Fragment>
      <WorkbenchBreadCrumb location={location} defaultPath={path} />
      <AppContainer className="workbench">
        <Routes>
          <Route 
            path="sample" 
            element={<PrivateRoute><div>Sample Screen loaded</div></PrivateRoute>} 
          />
          <Route 
            path="localisation-search" 
            element={<PrivateRoute><LocalisationSearch /></PrivateRoute>} 
          />
          <Route 
            path="mdms-search" 
            element={<PrivateRoute><MDMSSearch /></PrivateRoute>} 
          />
          <Route 
            path="mdms-add" 
            element={<PrivateRoute><MDMSAdd FormSession={MDMSCreateSession} parentRoute={path} /></PrivateRoute>} 
          />
          <Route 
            path="mdms-add-v2" 
            element={<PrivateRoute><MDMSAddV2 parentRoute={path} /></PrivateRoute>} 
          />
          <Route 
            path="mdms-view" 
            element={<PrivateRoute><MDMSView parentRoute={path} /></PrivateRoute>} 
          />
          <Route 
            path="mdms-edit" 
            element={<PrivateRoute><MDMSEdit parentRoute={path} /></PrivateRoute>} 
          />
          <Route 
            path="manage-master-data" 
            element={<PrivateRoute><MDMSManageMaster parentRoute={path} /></PrivateRoute>} 
          />
          <Route 
            path="mdms-search-v2" 
            element={<PrivateRoute><MDMSSearchv2 parentRoute={path} /></PrivateRoute>} 
          />
          <Route 
            path="localisation-add" 
            element={<PrivateRoute><LocalisationAdd parentRoute={path} /></PrivateRoute>} 
          />
          <Route 
            path="apply-workflow" 
            element={<PrivateRoute><ApplyWorkflow parentRoute={path} /></PrivateRoute>} 
          />
        </Routes>
        </AppContainer>
    </React.Fragment>
  );
};

export default App;
