import { PersonIcon, EmployeeModuleCard,CitizenHomeCard } from "@egovernments/digit-ui-react-components";
import React from "react";
import { useTranslation } from "react-i18next";

const WMSCard = () => {
  const ADMIN = Digit.Utils.hrmsAccess();
  if (!ADMIN) {
    const { t } = useTranslation();
    const tenantId = Digit.ULBService.getCurrentTenantId();
   
    const propsForModuleCard = {
        Icon : <PersonIcon/>,
        moduleName: t("CITIZEN_SERVICE_WMS"),
        kpis: [
            {
                count:  isLoading ? "-" : data?.length,
                label: t("WMS_COMMON_LIST"),
                link: `/digit-ui/citizen/wms/sor-list`
            },
         
        ],
        links: [
            {
                label: t("WMS_SOR_LIST"),
                link: `/digit-ui/citizen/wms/sor-list`
            },
            {
                label: t("WMS_SOR_CREATE"),
                link: `/digit-ui/citizen/wms/sor-create`
            },
            {
                label: t("WMS_SOR_DETAILS"),
                link: `/digit-ui/citizen/wms/sor-details/:id`
            }           
        ]
    }
    return <CitizenHomeCard {...propsForModuleCard} />;
  }
    const { t } = useTranslation();
    const tenantId = Digit.ULBService.getCurrentTenantId();
   
    const propsForModuleCard = {
        Icon : <PersonIcon/>,
        moduleName: t("CITIZEN_SERVICE_WMS"),
        kpis: [
            {
                // count:  isLoading ? "-" : data?.EmployeCount?.totalEmployee,
                label: t("WMS_COMMON_INBOX"),
                link: `/digit-ui/employee/wms/Inbox`
            },
         
        ],
        links: [
            {
                label: t("WMS_SOR_INBOX"),
                link: `/digit-ui/employee/wms/sor/Inbox`
            },
            {
                label: t("WMS_SCHEDULE_MASTER_INBOX"),
                link: `/digit-ui/citizen/wms/sdlmst/Inbox`
            },
            {
                label: t("WMS_PROJECT_MASTER_INBOX"),
                link: `/digit-ui/citizen/wms/prjmst/Inbox`
            }           
        ]
    }

    return <EmployeeModuleCard {...propsForModuleCard} />
};

export default WMSCard;