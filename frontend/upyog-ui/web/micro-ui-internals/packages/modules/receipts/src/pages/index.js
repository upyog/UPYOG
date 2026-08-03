import { PrivateRoute } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { useTranslation } from "react-i18next";
import { Link, useLocation, Routes, Route } from "react-router-dom";
import { getDefaultReceiptService } from "../utils";

const EmployeeApp = ({ path, url, userType }) => {
  const { t } = useTranslation();
  const location = useLocation();
  const mobileView = innerWidth <= 640;
  const tenantId = Digit.ULBService.getCurrentTenantId();

  const inboxInitialState = {
    searchParams: {
      tenantId: tenantId,
      businessServices: getDefaultReceiptService(),
    },
  };

  const ReceiptInbox = Digit.ComponentRegistryService.getComponent("ReceiptInbox");
  const ReceiptAcknowledgement = Digit.ComponentRegistryService.getComponent("ReceiptAcknowledgement");
  const ReceiptDetails = Digit.ComponentRegistryService.getComponent("ReceiptDetails");

  return (
    <React.Fragment>
      <div className="ground-container">
        <p className="breadcrumb" style={{ marginLeft: mobileView ? "2vw" : "revert" }}>
          <Link to="/upyog-ui/employee" style={{ cursor: "pointer", color: "#666" }}>
            {t("ES_COMMON_HOME")}
          </Link>{" "}
          / <span>{location.pathname === "/upyog-ui/employee/receipts/inbox" ? t("CR_COMMON_HEADER") : t("CR_COMMON_HEADER")}</span>
        </p>
        <Routes>
          <Route
            path={`/inbox`}
            element={
              <PrivateRoute>
                <ReceiptInbox
                  parentRoute={path}
                  businessService="receipts"
                  filterComponent="RECEIPTS_INBOX_FILTER"
                  initialStates={inboxInitialState}
                  isInbox={true}
                />
              </PrivateRoute>
            }
          />
          <Route path={`/inprogress`} element={<PrivateRoute><h2>{t("CR_RECEIPTS_SCREENS_UNDER_CONSTRUCTION")}</h2></PrivateRoute>} />
          <Route path={`/response`} element={<PrivateRoute><ReceiptAcknowledgement parentRoute={path} /></PrivateRoute>} />
          <Route path={`/details/:service/:id`} element={<PrivateRoute><ReceiptDetails /></PrivateRoute>} />
        </Routes>
      </div>
    </React.Fragment>
  );
};

export default EmployeeApp;
