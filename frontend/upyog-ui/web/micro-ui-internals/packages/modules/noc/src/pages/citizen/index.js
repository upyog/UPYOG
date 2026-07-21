import { AppContainer, BackButton, PrivateRoute, ArrowLeft } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { useTranslation } from "react-i18next";
import { Route, Routes, Navigate, useLocation, useNavigate } from "react-router-dom";

const App = () => {
  const { t } = useTranslation();
  const { path } = Digit.Hooks.useModuleBasePath();
  const location = useLocation();
  const navigate = useNavigate();

  const CreateNoc = Digit?.ComponentRegistryService?.getComponent("NOCCreateApplication") || (() => <div>NOCCreateApplication Placeholder</div>);
  const MyApplications = Digit?.ComponentRegistryService?.getComponent("NOCMyApplications") || (() => <div>NOCMyApplications Placeholder</div>);
  const ApplicationDetails = Digit?.ComponentRegistryService?.getComponent("NOCApplicationDetails") || (() => <div>NOCApplicationDetails Placeholder</div>);
  const CitizenHome = Digit?.ComponentRegistryService?.getComponent("NOCCitizenHome") || (() => <div>NOCCitizenHome Placeholder</div>);
  console.log("path", path);

  const isAcknowledgementPage = location.pathname.includes("/acknowledgement");
  const isNewApplication = location.pathname.includes("noc/new-application");
  const isDocumentRequiredPage = location.pathname.includes("new-application/document-required");

  return (
    <span className={"noc-citizen"}>
      <AppContainer>
        {!isAcknowledgementPage && isNewApplication && (
          isDocumentRequiredPage ? (
            <div className="back-btn2" onClick={() => navigate(`${path}-home`)}>
              <ArrowLeft />
              <p>{t("CS_COMMON_BACK")}</p>
            </div>
          ) : (
            <BackButton>
              {t("CS_COMMON_BACK")}
            </BackButton>
          )
        )}
        <Routes>
          <Route path={`home`} element={<PrivateRoute><CitizenHome /></PrivateRoute>} />
          <Route path={`new-application/*`} element={<PrivateRoute><CreateNoc path={path} /></PrivateRoute>} />
          <Route path={`my-applications/*`} element={<PrivateRoute><MyApplications /></PrivateRoute>} />
          <Route path={`application-details/:id`} element={<PrivateRoute><ApplicationDetails /></PrivateRoute>} />
        </Routes>
      </AppContainer>
    </span>
  );
};

export default App;
