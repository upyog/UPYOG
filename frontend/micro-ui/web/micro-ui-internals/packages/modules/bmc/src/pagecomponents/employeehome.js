import React from "react";
import { useTranslation } from "react-i18next";
import { useQueryClient } from "react-query";
import { Switch, useHistory, useLocation, useRouteMatch } from "react-router-dom";
const BMCEmployeeHome = ({ parentRoute }) => {
    const queryClient = useQueryClient();
    const match = useRouteMatch();
    const { t } = useTranslation();
    const { pathname } = useLocation();
    const history = useHistory();
    const stateId = Digit.ULBService.getStateId();
    let config = [];
    const [params, setParams, clearParams] = Digit.Hooks.useSessionStorage("BMC_Application", {});

    return (
        <Switch>
            <div>BMC Employee Home</div>
        </Switch>
    );
};

export default BMCEmployeeHome;