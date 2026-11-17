import React from "react";
import { Routes, Route, Navigate } from "react-router-dom";
import { PrivateRoute } from "@nudmcdgnpm/digit-ui-react-components";
import ThemeProvider from "../../theme/ThemeProvider";
import CreateActionPlan from "./CreateActionPlan";
import Inbox from "./Inbox";

const ComingSoon = ({ title }) => (
    <div style={{ padding: 16 }}>
        <h2>{title}</h2>
        <p>Not built yet.</p>
    </div>
);

const EmployeeApp = ({ path }) => {
    const basePath = path?.includes("/inbox") ? path.replace(/\/inbox.*$/, "") : (path || "/works-ui/employee/works");
    return (
        <ThemeProvider>
            <Routes>
                <Route path="inbox" element={<PrivateRoute><Inbox parentRoute={basePath} /></PrivateRoute>} />
                <Route path="search-application" element={<PrivateRoute><ComingSoon title="Search Application" /></PrivateRoute>} />
                <Route path="application-details/:applicationNumber" element={<PrivateRoute><ComingSoon title="Application Details" /></PrivateRoute>} />
                <Route path="create-action-plan" element={<PrivateRoute><CreateActionPlan parentRoute={basePath} /></PrivateRoute>} />
                <Route path="*" element={<Navigate to={`${basePath}/inbox`} replace />} />
            </Routes>
        </ThemeProvider>
    );
};

export default EmployeeApp;