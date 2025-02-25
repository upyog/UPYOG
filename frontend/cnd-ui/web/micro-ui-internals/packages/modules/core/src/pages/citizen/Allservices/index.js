import React from "react";
import { useTranslation } from "react-i18next";
import { AppModules } from "../../../components/AppModules";

const CitizenApp = ({ stateCode, modules, appTenants, sourceUrl, pathname}) => {
    const { t } = useTranslation();

    return <AppModules stateCode={stateCode} userType="citizen" modules={modules} appTenants={appTenants} />
}

export default CitizenApp
