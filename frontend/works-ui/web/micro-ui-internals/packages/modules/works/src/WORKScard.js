import { PersonIcon, EmployeeModuleCard } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { useTranslation } from "react-i18next";

// Visible to any logged-in employee holding a role starting with WMS_
const hasWmsRole = () => {
    const userRoles = Digit.UserService.getUser()?.info?.roles?.map((role) => role?.code) || [];
    return userRoles.some((code) => typeof code === "string" && code.startsWith("WMS_"));
};

const WORKSCard = () => {
    const { t } = useTranslation();

    if (!hasWmsRole()) {
        return null;
    }

    const propsForModuleCard = {
        Icon: <PersonIcon />,
        moduleName: t("ACTION_TEST_WORKS"),
        kpis: [],
        links: [
            {
                label: t("ES_COMMON_INBOX"),
                link: `/works-ui/employee/works/inbox`,
            },
            {
                label: t("WORKS_CREATE_ACTION_PLAN"),
                link: `/works-ui/employee/works/create-action-plan`,
            },
            {
                label: t("ES_COMMON_SEARCH_APPLICATION"),
                link: `/works-ui/employee/works/search-application`,
            },
        ],
    };

    return <EmployeeModuleCard {...propsForModuleCard} />;
};

export default WORKSCard;
