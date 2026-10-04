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
                <p className={`breadcrumb employee-main-application-details ${mobileView ? "breadcrumb-margin-mobile" : ""}`}>
                    <Link to="/upyog-ui/employee" className="rpt-index-clickable">
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
