/**
 * index.js (Reports Page Router)
 *
 * Purpose:
 * Route manager for the reports module pages.
 *
 * Responsibilities:
 * - Renders breadcrumb index navigation headers.
 * - Maps dynamic report query routing paths (/search/:moduleName/:reportName).
 * - Enforces private routing verification around components.
 */

import { PrivateRoute } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { useTranslation } from "react-i18next";
import { Link, useLocation, Routes, Route } from "react-router-dom";
import Report from "./Report";
const EmployeeApp = ({ path, url, userType }) => {
    const { t } = useTranslation();
    const location = useLocation();
    const mobileView = innerWidth <= 640;
    const tenantId = Digit.ULBService.getCurrentTenantId();

    return (
        <React.Fragment>
            <div className="ground-container">
                <p className="breadcrumb employee-main-application-details" style={{ marginLeft: mobileView ? "2vw" : "revert" }}>
                    <Link to="/cnd-ui/employee" style={{ cursor: "pointer", color: "#666" }}>
                        {t("ES_COMMON_HOME")}
                    </Link>{" "}
                    / <span>{t("reports")}</span>
                </p>
                <Routes>
                    <Route path={`/search/:moduleName/:reportName`} element={<PrivateRoute><Report /></PrivateRoute>} />

                </Routes>
            </div>
        </React.Fragment>
    );
};

export default EmployeeApp;
