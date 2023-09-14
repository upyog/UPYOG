import { PersonIcon, EmployeeModuleCard } from "@egovernments/digit-ui-react-components";
import React from "react";
import { useTranslation } from "react-i18next";

const WMSCard = () => {
  const ADMIN = Digit.Utils.wmsAccess();
  if (!ADMIN) {
    return null;
  }
    const { t } = useTranslation();
    const tenantId = Digit.ULBService.getCurrentTenantId();
   
    const propsForModuleCard = {
        Icon : <PersonIcon/>,
        moduleName: t("SOR-Details"),
        kpis: [
            {
                // count:  isLoading ? "-" : data?.EmployeCount?.totalEmployee,
                label: t("TOTAL Application"),
                link: `/digit-ui/citizen/wms/sor-list`
            },
         
        ],
        links: [
            {
                label: t("Inbox"),
                link: `/digit-ui/citizen/wms/sor-list`
            },
            {
                label: t("Create SOR-Registration"),
                link: `/digit-ui/citizen/wms/sor-create`
            }           
        ]
    }

    return <EmployeeModuleCard {...propsForModuleCard} />
};

export default WMSCard;