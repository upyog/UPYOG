// import React from "react";
import React, { useEffect, useState } from "react";
import OBPSSearchApplication from "../../components/SearchApplication";
import Search from "../employee/Search";
import { useTranslation } from "react-i18next";
import { Routes, useLocation, Route } from "react-router-dom";
import { PrivateRoute, BackButton } from "@nudmcdgnpm/digit-ui-react-components";
// import NewBuildingPermit from "./NewBuildingPermit";
// import CreateEDCR from "./EDCR";
// import CreateOCEDCR from "./OCEDCR";
// import BPACitizenHomeScreen from "./home";
// import StakeholderRegistration from "./StakeholderRegistration";
import MyApplication from "./MyApplication";
import ApplicationDetails from "./ApplicationDetail";
// import OCBuildingPermit from "./OCBuildingPermit";
// import BpaApplicationDetail from "./BpaApplicationDetail";
// import BPASendToArchitect from "./BPASendToArchitect";
// import OCSendToArchitect from "./OCSendToArchitect";
// import BPASendBackToCitizen from "./BPASendBackToCitizen";
// import OCSendBackToCitizen from "./OCSendBackToCitizen";
// import Inbox from "./ArchitectInbox";
//import EdcrInbox from "./EdcrInbox";
import OBPSResponse from "../employee/OBPSResponse";
import Inbox from "../employee/Inbox";

const App = ({ path }) => {
  const location = useLocation();
  const { t } = useTranslation();
  let isCommonPTPropertyScreen = window.location.href.includes("/ws/create-application/property-details");
  let isAcknowledgement = window.location.href.includes("/acknowledgement") || window.location.href.includes("/disconnect-acknowledge");
  const BPACitizenHomeScreen = Digit?.ComponentRegistryService?.getComponent("BPACitizenHomeScreen");
  const CreateEDCR = Digit?.ComponentRegistryService?.getComponent("ObpsCreateEDCR");
  const CreateOCEDCR = Digit?.ComponentRegistryService?.getComponent("ObpsCreateOCEDCR");
  const NewBuildingPermit = Digit?.ComponentRegistryService?.getComponent("ObpsNewBuildingPermit");
  const OCBuildingPermit = Digit?.ComponentRegistryService?.getComponent("ObpsOCBuildingPermit");
  const StakeholderRegistration = Digit?.ComponentRegistryService?.getComponent("ObpsStakeholderRegistration");
  const PreApprovedPlan = Digit?.ComponentRegistryService?.getComponent("ObpsPreApprovedPlan")
  const EdcrInbox = Digit?.ComponentRegistryService?.getComponent("ObpsEdcrInbox");
  const BpaApplicationDetail = Digit?.ComponentRegistryService?.getComponent("ObpsCitizenBpaApplicationDetail");
  const BPASendToArchitect = Digit?.ComponentRegistryService?.getComponent("ObpsBPASendToArchitect");
  const OCSendToArchitect = Digit?.ComponentRegistryService?.getComponent("ObpsOCSendToArchitect");
  const BPASendBackToCitizen = Digit?.ComponentRegistryService?.getComponent("ObpsBPASendBackToCitizen");
  const OCSendBackToCitizen = Digit?.ComponentRegistryService?.getComponent("ObpsOCSendBackToCitizen");
  const isDocScreenAfterEdcr = sessionStorage.getItem("clickOnBPAApplyAfterEDCR") === "true" ? true : false
  return (
    <React.Fragment>
      <div className="ws-citizen-wrapper">
        {!location.pathname.includes("response") && !location.pathname.includes("openlink/stakeholder") && !location.pathname.includes("/acknowledgement") && !isDocScreenAfterEdcr && <BackButton style={{ border: "none" }}>{t("CS_COMMON_BACK")}</BackButton>}
        <Routes>
          <Route path="/home" element={<PrivateRoute><BPACitizenHomeScreen /></PrivateRoute>} />
          <Route path="/search/application" element={<PrivateRoute><Search parentRoute={path} /></PrivateRoute>} />
          <Route path="/search/obps-application" element={<PrivateRoute><Search parentRoute={path} /></PrivateRoute>} />
          <Route path="/edcrscrutiny/apply/*" element={<PrivateRoute><CreateEDCR /></PrivateRoute>} />
          <Route path="/edcrscrutiny/oc-apply/*" element={<PrivateRoute><CreateOCEDCR /></PrivateRoute>} />
          <Route path="/bpa/:applicationType/:serviceType/*" element={<PrivateRoute><NewBuildingPermit /></PrivateRoute>} />
          <Route path="/ocbpa/:applicationType/:serviceType/*" element={<PrivateRoute><OCBuildingPermit /></PrivateRoute>} />
          <Route path="/stakeholder/apply/*" element={<PrivateRoute><StakeholderRegistration /></PrivateRoute>} />
          <Route path="/preApprovedPlan/*" element={<PrivateRoute><PreApprovedPlan /></PrivateRoute>} />
          <Route path="/openlink/stakeholder/apply/*" element={<StakeholderRegistration />} />
          <Route path="/my-applications" element={<PrivateRoute><MyApplication /></PrivateRoute>} />
          <Route path="/bpa/inbox" element={<PrivateRoute><Inbox parentRoute={path} /></PrivateRoute>} />
          <Route path="/edcr/inbox" element={<PrivateRoute><EdcrInbox parentRoute={path} /></PrivateRoute>} />
          <Route path="/stakeholder/:id" element={<PrivateRoute><ApplicationDetails /></PrivateRoute>} />
          <Route path="/bpa/:id" element={<PrivateRoute><BpaApplicationDetail /></PrivateRoute>} />
          <Route path="/editApplication/bpa/:tenantId/:applicationNo/*" element={<PrivateRoute><BPASendToArchitect /></PrivateRoute>} />
          <Route path="/editApplication/ocbpa/:tenantId/:applicationNo/*" element={<PrivateRoute><OCSendToArchitect /></PrivateRoute>} />
          <Route path="/sendbacktocitizen/bpa/:tenantId/:applicationNo/*" element={<PrivateRoute><BPASendBackToCitizen /></PrivateRoute>} />
          <Route path="/sendbacktocitizen/ocbpa/:tenantId/:applicationNo/*" element={<PrivateRoute><OCSendBackToCitizen /></PrivateRoute>} />
          <Route path="/response" element={<PrivateRoute><OBPSResponse /></PrivateRoute>} />
        </Routes>
      </div>
    </React.Fragment>
  );
};

export default App;
