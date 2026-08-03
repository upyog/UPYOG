import { AppContainer, BreadCrumb } from "@upyog/workbench-ui-react-components";
import React from "react";
import { useTranslation } from "react-i18next";
import { Route, Routes, useLocation } from "react-router-dom";
import DynamicSearchComponent from "./DynamicSearchComponent";
import IFrameInterface from "./IFrameInterface";
import WorkflowCompTest from "./WorkflowComponentTest";

const ProjectBreadCrumb = ({ location }) => {
  const { t } = useTranslation();
  const crumbs = [
    {
      path: `/${window?.contextPath}/employee`,
      content: t("HOME"),
      show: true,
    },
    {
      path: `/${window?.contextPath}/employee`,
      content: t(location.pathname.split("/").pop()),
      show: true,
    },
  ];
  return <BreadCrumb crumbs={crumbs} spanStyle={{ maxWidth: "min-content" }} />;
};

const App = ({ path, stateCode, userType, tenants }) => {
  const location = useLocation();
  const commonProps = { stateCode, userType, tenants, path };

  return (
    <AppContainer className="ground-container">
        <React.Fragment>
          <ProjectBreadCrumb location={location} />
        </React.Fragment>
        <Routes>
            <Route
          path={`${path}/search/:moduleName/:masterName`}
          element={<DynamicSearchComponent parentRoute={path} />}
        />

        <Route
          path={`${path}/create/:moduleName/:masterName`}
          element={<DynamicCreateComponent parentRoute={path} />}
        />

        <Route
          path={`${path}/iframe/:moduleName/:pageName`}
          element={<IFrameInterface {...commonProps} />}
        />

        <Route
          path={`${path}/workflow`}
          element={<WorkflowCompTest parentRoute={path} />}
        />
        </Routes>
      </AppContainer>
  );
};

export default App;
