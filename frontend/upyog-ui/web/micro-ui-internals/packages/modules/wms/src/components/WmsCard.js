import { PersonIcon, EmployeeModuleCard } from "@egovernments/digit-ui-react-components";
import React from "react";
import { useTranslation } from "react-i18next";

const WMSCard = () => {
  const ADMIN = Digit.Utils.hrmsAccess();
  if (!ADMIN) {
    return null;
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