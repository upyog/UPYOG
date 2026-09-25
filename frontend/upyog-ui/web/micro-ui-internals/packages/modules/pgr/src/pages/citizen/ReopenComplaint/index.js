import React, { useMemo } from "react";

import { Route, Routes } from "react-router-dom";
// import UserOnboarding from "../UserOnboarding/index";
import { PgrRoutes, getRoute } from "../../../constants/Routes";
import ReasonPage from "./Reason";
import UploadPhoto from "./UploadPhoto";
import AddtionalDetails from "./AddtionalDetails";
import Response from "../Response";

const ReopenComplaint = ({ match, history, parentRoute }) => {
  
  const allParams = window.location.pathname.split("/")
  const id = allParams[allParams.length - 1]
  const tenantId = Digit.SessionStorage.get("CITIZEN.COMMON.HOME.CITY")?.code || Digit.ULBService.getCurrentTenantId();

  const complaintDetails = Digit.Hooks.pgr.useComplaintDetails({ tenantId: tenantId, id: id }).data;
  return (
    <Routes>
      <Route path={getRoute(match, PgrRoutes.ReasonPage)} element={<ReasonPage match={match} {...{ complaintDetails }} />} />
      <Route path={getRoute(match, PgrRoutes.UploadPhoto)} element={<UploadPhoto match={match} skip={true} {...{ complaintDetails }} />} />
      <Route path={getRoute(match, PgrRoutes.AddtionalDetails)} element={<AddtionalDetails match={match} parentRoute={parentRoute} {...{ complaintDetails }} />} />
      <Route path={getRoute(match, PgrRoutes.Response) + "/:id"} element={<Response match={match} />} />
      <Route path={getRoute(match, PgrRoutes.Response)} element={<Response match={match} />} />
    </Routes>
  );
};

export { ReopenComplaint };
