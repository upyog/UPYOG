import React from "react";
import { useTranslation } from "react-i18next";
import { EmployeeModuleCard, PropertyHouse } from "@nudmcdgnpm/digit-ui-react-components";

// Custom component for the employee card which shows links like - My Requests as well as Inbox
const CNDCard = () => {
  const { t } = useTranslation();
  const user = Digit.UserService.getUser().info;
  const tenantId = Digit.ULBService.getCitizenCurrentTenant(true) || Digit.ULBService.getCurrentTenantId();
  /**
   * This code calls the inbox general using tenantId ,modulecode and filters on that we get the total cunt and SLA count
   * once success it is setting that data in total variable and we are showing inside count for inbox
   */
  let filter1 =  { limit: "4", sortOrder: "ASC", sortBy: "createdTime", offset: "0",mobileNumber:user?.mobileNumber, tenantId };

  const { data } = Digit.Hooks.cnd.useCndSearchApplication({ filters: filter1 });

  if (!Digit.Utils.cndAccess()) {
    return null;
  }

  const links = [
    {
      count: data?.count,
      label: t("CND_INBOX"),
      link: `/cnd-ui/employee/cnd/inbox`,
    },
    {
      label: t("CND_MY_REQUEST"),
      link: `/cnd-ui/employee/cnd/my-applications`,
    },
  ]
  const CND_CEMP = Digit.UserService.hasAccess(["CND_CEMP"]) || false;

  const propsForModuleCard = {
    Icon: <PropertyHouse />,
    moduleName: <div style={{ width: "200px", wordWrap: "break-word" }}>{t("MODULE_CND")}</div>,
    kpis: [{
      count: data?.count,
      label: t("CND_APPLICANT_COUNT"),
      link: `/cnd-ui/employee/cnd/inbox`,
  }],
    links: links.filter(link => !link?.role || CND_CEMP),
  };

  return <EmployeeModuleCard {...propsForModuleCard} />;
};


export default CNDCard;
