import React from "react";
import { useTranslation } from "react-i18next";
import { Routes, useLocation, Route } from "react-router-dom";
import { BackButton, PrivateRoute } from "@nudmcdgnpm/upyog-ui-react-components-lts";
import DocumentCategories from "./Documents/DocumentCategories";
import ViewDocument from "./Documents/ViewDocument";
import Response from "./CitizenSurvey/Response";

const CitizenApp = ({ path, url, userType, tenants }) => {
  const location = useLocation();
  const { t } = useTranslation();
  const NotificationsOrWhatsNew = Digit.ComponentRegistryService.getComponent("NotificationsAndWhatsNew");
  const Events = Digit.ComponentRegistryService.getComponent("EventsListOnGround");
  const EventDetails = Digit.ComponentRegistryService.getComponent("EventDetails");
  const Documents = Digit.ComponentRegistryService.getComponent("DocumentList");
  const SurveyList = Digit.ComponentRegistryService.getComponent("SurveyList");
  const FillSurvey = Digit.ComponentRegistryService.getComponent("FillSurvey");
  const ShowSurvey = Digit.ComponentRegistryService.getComponent("ShowSurvey");
  return (
    <React.Fragment>
      <div className="engagement-citizen-wrapper">
        {!location.pathname.includes("response") && <BackButton>{t("CS_COMMON_BACK")}</BackButton>}
        <Routes>
          <Route 
            path="notifications" 
            element={<NotificationsOrWhatsNew variant="notifications" parentRoute={path} />} 
          />
          <Route 
            path="whats-new" 
            element={
              <PrivateRoute>
                <NotificationsOrWhatsNew variant="whats-new" parentRoute={path} />
              </PrivateRoute>
            }
          />
          <Route 
            path="events" 
            element={
              <PrivateRoute>
                <Events variant="events" parentRoute={path} />
              </PrivateRoute>
            }
          />
          <Route 
            path="events/details/:id" 
            element={
              <PrivateRoute>
                <EventDetails parentRoute={path} />
              </PrivateRoute>
            }
          />
          <Route 
            path="docs" 
            element={
              <PrivateRoute>
                <DocumentCategories t={t} path={path} />
              </PrivateRoute>
            }
          />
          <Route 
            path="documents/viewDocument" 
            element={
              <PrivateRoute>
                <ViewDocument t={t} path={path} />
              </PrivateRoute>
            }
          />
          <Route 
            path="documents/list/:category/:count" 
            element={
              <PrivateRoute>
                <Documents />
              </PrivateRoute>
            } 
          />
          <Route 
            path="surveys/list" 
            element={
              <PrivateRoute>
                <SurveyList />
              </PrivateRoute>
            } 
          />
          <Route 
            path="surveys/fill-survey" 
            element={
              <PrivateRoute>
                <FillSurvey />
              </PrivateRoute>
            } 
          />
          
          <Route 
            path="surveys/submit-response" 
            element={
              <PrivateRoute>
                <Response />
              </PrivateRoute>
            } 
          />
          
          <Route 
            path="surveys/show-survey" 
            element={
              <PrivateRoute>
                <ShowSurvey />
              </PrivateRoute>
            } 
          />
        </Routes>
      </div>
    </React.Fragment>
  );
};
export default CitizenApp;
