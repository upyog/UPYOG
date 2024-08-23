import { AppContainer, BackButton, PrivateRoute } from "@egovernments/digit-ui-react-components";
import React from "react";
import { useTranslation } from "react-i18next";
import { Switch, useLocation, useRouteMatch } from "react-router-dom";

const App = () => {
    const { t } = useTranslation();
    const { path, url, ...match } = useRouteMatch();
    const location = useLocation();

    const wardMasterPage = Digit.ComponentRegistryService?.getComponent("wardMasterPage");
    const electoralMasterPage = Digit.ComponentRegistryService?.getComponent("electoralMasterPage");
    const religionMasterPage = Digit.ComponentRegistryService?.getComponent("religionMasterPage");
    const casteCategoryMasterPage = Digit.ComponentRegistryService?.getComponent("casteCategoryMasterPage");
    const bankMasterPage = Digit.ComponentRegistryService?.getComponent("bankMasterPage");
    const qualificationMasterPage = Digit.ComponentRegistryService?.getComponent("qualificationMasterPage");
    const sectorMasterPage = Digit.ComponentRegistryService?.getComponent("sectorMasterPage");
    const courseMasterPage = Digit.ComponentRegistryService?.getComponent("courseMasterPage");

    return (
        <React.Fragment>
            <div className="bmc-citizen-wrapper" style={{ width: "100%" }}>
                {!location.pathname.includes("/response") && <BackButton>{t("CS_COMMON_BACK")}</BackButton>}
                <Switch>
                    <AppContainer>
                        {/* <PrivateRoute path={`${path}/anc`} component={CreateComplaint} /> */}
                        <PrivateRoute exact path={`${path}/wardmaster`} component={wardMasterPage} />
                        <PrivateRoute exact path={`${path}/electoralmaster`} component={electoralMasterPage} />
                        <PrivateRoute exact path={`${path}/religionmaster`} component={religionMasterPage} />
                        <PrivateRoute exact path={`${path}/castecategory`} component={casteCategoryMasterPage} />
                        <PrivateRoute exact path={`${path}/bankmaster`} component={bankMasterPage} />
                        <PrivateRoute exact path={`${path}/sectormaster`} component={sectorMasterPage} />
                        <PrivateRoute exact path={`${path}/qualificationmaster`} component={qualificationMasterPage} />
                        <PrivateRoute exact path={`${path}/coursemaster`} component={courseMasterPage} />
                    </AppContainer>
                </Switch>
            </div>
        </React.Fragment>
    );
};

export default App;
