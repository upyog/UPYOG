import React from "react";
import { ReopenComplaint } from "./ReopenComplaint/index";
import SelectRating from "./Rating/SelectRating";
import { PgrRoutes, getRoute } from "../../constants/Routes";
import { useLocation, Route, Routes } from "react-router-dom";
import { AppContainer, BackButton, PrivateRoute } from "@nudmcdgnpm/digit-ui-react-components";

import { CreateComplaint } from "./Create";
import { ComplaintsList } from "./ComplaintsList";
import ComplaintDetailsPage from "./ComplaintDetails";
import Response from "./Response";
import { useTranslation } from "react-i18next";

const App = () => {
  const { t } = useTranslation();
  const { path, url, ...match } = Digit.Hooks.useModuleBasePath();
  const location = useLocation();

  const CreateComplaint = Digit?.ComponentRegistryService?.getComponent("PGRCreateComplaintCitizen");
  const ComplaintsList = Digit?.ComponentRegistryService?.getComponent("PGRComplaintsList");
  const ComplaintDetailsPage = Digit?.ComponentRegistryService?.getComponent("PGRComplaintDetailsPage");
  const SelectRating = Digit?.ComponentRegistryService?.getComponent("PGRSelectRating");
  const Response = Digit?.ComponentRegistryService?.getComponent("PGRResponseCitzen");

  return (
    <React.Fragment>
      <div className="pgr-citizen-wrapper">
        {!location.pathname.includes("/response") && <BackButton>{t("CS_COMMON_BACK")}</BackButton>}
        <Routes>
          <Route path={`create-complaint/*`} element={<PrivateRoute><CreateComplaint /></PrivateRoute>} />
          <Route path="complaints/:id" element={<PrivateRoute><ComplaintDetailsPage /></PrivateRoute>} />
          <Route path="complaints/*" element={<PrivateRoute><ComplaintsList /></PrivateRoute>} />
          <Route
            path={`/reopen/*`}
            element={
              <PrivateRoute>
                <ReopenComplaint match={{ ...match, url, path: `/reopen` }} parentRoute={path} />
              </PrivateRoute>
            }
          />
          <Route path={`/rate/:id/*`} element={<PrivateRoute><SelectRating parentRoute={path} /></PrivateRoute>} />
          <Route path={`response`} element={<PrivateRoute><Response match={{ ...match, url, path }} /></PrivateRoute>} />
        </Routes>
      </div>
    </React.Fragment>
  );
};

export default App;
