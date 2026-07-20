import { AppContainer, BackButton, PrivateRoute } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { Route, Routes } from "react-router-dom";
import { shouldHideBackButton } from "../../utils";
import Search from "../employee/Search";
import { useTranslation } from "react-i18next";
import { PTMyPayments } from "./MyPayments";
import PaymentDetails from "../../utils/PaymentDetails"
const hideBackButtonConfig = [
  { screenPath: "property/new-application/acknowledgement" },
  { screenPath: "property/edit-application/acknowledgement" },
  //{ screenPath: "property/feedback-acknowledgement" }
];

const App = () => {
  const { path, url, ...match } = Digit.Hooks.useModuleBasePath();
  const { t } = useTranslation();
  const inboxInitialState = {
    searchParams: {},
  };



  const CreateProperty = Digit?.ComponentRegistryService?.getComponent("PTCreateProperty");
  const EditProperty = Digit?.ComponentRegistryService?.getComponent("PTEditProperty");
  const SearchPropertyComponent = Digit?.ComponentRegistryService?.getComponent("PTSearchPropertyComponent");
  const SearchResultsComponent = Digit?.ComponentRegistryService?.getComponent("PTSearchResultsComponent");
  const PTApplicationDetails = Digit?.ComponentRegistryService?.getComponent("PTApplicationDetails");
  const PTMyApplications = Digit?.ComponentRegistryService?.getComponent("PTMyApplications");
  const MyProperties = Digit?.ComponentRegistryService?.getComponent("PTMyProperties");
  const MutateProperty = Digit?.ComponentRegistryService?.getComponent("PTMutateProperty");
  const PropertyInformation = Digit?.ComponentRegistryService?.getComponent("PropertyInformation");
  const PropertyOwnerHistory = Digit?.ComponentRegistryService?.getComponent("PropertyOwnerHistory");
  const AssessmentDetails = Digit?.ComponentRegistryService?.getComponent("PTAssessmentDetails");

  return (
    <span className={"pt-citizen pt-auto-149"}>
      <AppContainer>
        {!shouldHideBackButton(hideBackButtonConfig) ? <BackButton>Back</BackButton> : ""}
        <Routes>
          <Route path={`property/new-application/*`} element={<PrivateRoute><CreateProperty /></PrivateRoute>} />
          <Route path={`property/edit-application/*`} element={<PrivateRoute><EditProperty /></PrivateRoute>} />
          <Route path={`property/citizen-search`} element={<SearchPropertyComponent />} />
          <Route path={`property/search-results`} element={<SearchResultsComponent />} />
          <Route path={`property/application/:acknowledgementIds/:tenantId`} element={<PrivateRoute><PTApplicationDetails /></PrivateRoute>} />
          <Route path={`property/my-applications`} element={<PrivateRoute><PTMyApplications /></PrivateRoute>} />
          <Route path={`property/my-properties`} element={<PrivateRoute><MyProperties /></PrivateRoute>} />
          <Route path={`property/my-properties/:offset`} element={<PrivateRoute><MyProperties /></PrivateRoute>} />
          <Route path={`property/my-payments`} element={<PrivateRoute><PTMyPayments /></PrivateRoute>} />
          <Route path={`property/property-mutation/*`} element={<PrivateRoute><MutateProperty /></PrivateRoute>} />
          <Route path={`property/properties/:propertyIds`} element={<PrivateRoute><PropertyInformation /></PrivateRoute>} />
          <Route path={`payment-details/:id`} element={<PrivateRoute><PaymentDetails parentRoute={path} /></PrivateRoute>} />
          <Route path={`property/transfer-ownership`} element={<PrivateRoute><MutateProperty /></PrivateRoute>} />
          <Route path={`property/owner-history/:tenantId/:propertyIds`} element={<PrivateRoute><PropertyOwnerHistory /></PrivateRoute>} />
          <Route path={`assessment-details/:id`} element={<PrivateRoute><AssessmentDetails parentRoute={path} /></PrivateRoute>} />
          <Route path={`property/search`} element={<PrivateRoute><Search t={t} parentRoute={path} /></PrivateRoute>} />
        </Routes>
      </AppContainer>
    </span>
  );
};

export default App;
