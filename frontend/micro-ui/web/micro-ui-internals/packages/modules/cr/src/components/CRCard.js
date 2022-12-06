import { CaseIcon, EmployeeModuleCard } from "@egovernments/digit-ui-react-components";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { checkForEmployee } from "../utils";

const CRCard = () => {
    sessionStorage.setItem("breadCrumbUrl", "home");
    if (!Digit.Utils.tlAccess()) {
        return null;
    }
    const { t } = useTranslation();
    const tenantId = Digit.ULBService.getCurrentTenantId();
    const inboxSearchParams = { limit: 10, offset: 0 }
    const { isLoading, data: inboxData } = Digit.Hooks.tl.useInbox({
        tenantId,
        filters: { ...inboxSearchParams },
        config: {}
    });

    const [isStateLocalisation, setIsStateLocalisation] = useState(true);

    useEffect(() => {
        if (tenantId && isStateLocalisation) {
            setIsStateLocalisation(false);
            Digit.LocalizationService.getLocale({ modules: [`rainmaker-${tenantId}`], locale: Digit.StoreData.getCurrentLanguage(), tenantId: `${tenantId}` });
        }
    }, [tenantId]);


    let links = [
        // {
        //     count: isLoading ? "-" : inboxData?.totalCount,
        //     label: t("ES_COMMON_INBOX"),
        //     link: `/digit-ui/employee/tl/inbox`,
        // },
        // {
        //     label: t("TL_NEW_APPLICATION"),
        //     link: "/digit-ui/employee/tl/new-application",
        //     role: "TL_CEMP"
        // },
        // {
        //     label: t("TL_SEARCH_APPLICATIONS"),
        //     link: `/digit-ui/employee/tl/search/application`
        // },
        // {
        //     label: t("TL_SEARCH_LICENSE"),
        //     link: `/digit-ui/employee/tl/search/license`,
        //     role: "TL_CEMP"
        // }
        {
            label: t("Birth Registration"),
            link: `/digit-ui/employee/cr/cr-flow`,
        },
        {
            label: t("Death Registration"),
            link: `/digit-ui/employee/cr/death-flow`,
        },
    ]

    links = links.filter(link => link.role ? checkForEmployee(link.role) : true);

    const propsForModuleCard = {
        Icon: <CaseIcon />,
        moduleName: t("Civil Registration"),
        // TL_COMMON_TL
        kpis: [
            {
                count: isLoading ? "-" : inboxData?.totalCount,
                label: t("TOTAL_TL"),
                link: `/digit-ui/employee/tl/inbox`
            },
            {
                label: t("TOTAL_NEARING_SLA"),
                link: `/digit-ui/employee/tl/inbox`
            }
        ],
        links: links
    }
    return <EmployeeModuleCard {...propsForModuleCard} />
};

export default CRCard;

