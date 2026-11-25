import React from "react";
import { Routes, Route, Navigate } from "react-router-dom";
import { PrivateRoute } from "@nudmcdgnpm/digit-ui-react-components";
import ThemeProvider from "../../theme/ThemeProvider";
import CreateActionPlan from "./CreateActionPlan";
import Inbox from "./Inbox";
import ApplicationDetails from "./ApplicationDetails";
import SearchApplication from "./SearchApplication";
import ActionPlanAcknowledgement from "./ActionPlanAcknowledgement";

const EmployeeApp = ({ path }) => {
    const basePath = path?.includes("/inbox") ? path.replace(/\/inbox.*$/, "") : (path || "/works-ui/employee/works");
    return (
        <ThemeProvider>
            <Routes>
                <Route path="inbox" element={<PrivateRoute><Inbox parentRoute={basePath} /></PrivateRoute>} />
                <Route path="search-application" element={<PrivateRoute><SearchApplication parentRoute={basePath} /></PrivateRoute>} />
                <Route path="application-details/:id" element={<PrivateRoute><ApplicationDetails parentRoute={basePath} /></PrivateRoute>} />
                <Route path="create-action-plan" element={<PrivateRoute><CreateActionPlan parentRoute={basePath} /></PrivateRoute>} />
                <Route path="acknowledgement" element={<PrivateRoute><ActionPlanAcknowledgement parentRoute={basePath} /></PrivateRoute>} />
                <Route path="acknowledgement/:applicationNumber" element={<PrivateRoute><ActionPlanAcknowledgement parentRoute={basePath} /></PrivateRoute>} />
                <Route path="*" element={<Navigate to={`${basePath}/inbox`} replace />} />
            </Routes>
        </ThemeProvider>
    );
};

export default EmployeeApp;