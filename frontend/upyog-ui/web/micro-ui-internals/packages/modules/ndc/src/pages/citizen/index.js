import { BreadCrumb, AppContainer, BackButton, PrivateRoute } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { Routes, Route, useLocation } from "react-router-dom";
import { useTranslation } from "react-i18next";

const NDCBreadCrumbs = ({ location }) => {
  const { t } = useTranslation();
  const crumbs = [
    {
      path: "/upyog-ui/citizen",
      content: t("ES_COMMON_HOME"),
      show: true,
    },
    {
      path: "/upyog-ui/citizen/ndc-home",
      content: `${t("NDC")} Home`,
      show: location.pathname.includes("ndc/") ? true : false,
    },
    {
      path: "/upyog-ui/citizen/ptr-home",
      content: t("PET_NDCSERVICE"),
      show: location.pathname.includes("ptr/petservice/test") ? true : false,
    },
  ];
  return <BreadCrumb crumbs={crumbs} />;
};

const App = () => {
  const { t } = useTranslation();
  const location = useLocation();
  const NewNDCStepForm = Digit.ComponentRegistryService.getComponent("NewNDCStepFormCitizen");
  const MyApplications = Digit.ComponentRegistryService.getComponent("MyApplications");
  const NDCResponseCitizen = Digit.ComponentRegistryService.getComponent("NDCResponseCitizen");
  const ApplicationOverview = Digit?.ComponentRegistryService?.getComponent("CitizenApplicationOverview");
  const isResponse = window.location.href.includes("/response");
  const isMobile = window.Digit.Utils.browser.isMobile();

  return (
    <span className={"chb-citizen ndc-width-100-pad"}>
      <AppContainer>
        {!isResponse ? (
          <div className={window.location.href.includes("application-overview") || isMobile ? 'ndc-margin-left-10' : ''}>
            <NDCBreadCrumbs location={location} />
          </div>
        ) : null}
        <Routes>
          <Route path="new-application/*" element={<PrivateRoute><NewNDCStepForm /></PrivateRoute>} />
          <Route path="my-application/*" element={<PrivateRoute><MyApplications /></PrivateRoute>} />
          <Route path="response/:id" element={<PrivateRoute><NDCResponseCitizen /></PrivateRoute>} />
          <Route path="search/application-overview/:id" element={<PrivateRoute><ApplicationOverview /></PrivateRoute>} />
        </Routes>
      </AppContainer>
    </span>
  );
};

export default App;