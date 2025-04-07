import React from "react";
import { useTranslation } from "react-i18next";
import { EmployeeModuleCard, PropertyHouse } from "@nudmcdgnpm/digit-ui-react-components";
/**
 *  Custom component for the Employee card which shows links like - My Requests as well as Inbox
 *  It will check if the user has access using cndUserAccess and accordingly Render the Card in UI
 */

const CNDCard = () => {
  const { t } = useTranslation();
  const user = Digit.UserService.getUser().info;
  const tenantId = Digit.ULBService.getCitizenCurrentTenant(true) || Digit.ULBService.getCurrentTenantId();
  let filter1 =  { limit: "4", sortOrder: "ASC", sortBy: "createdTime", offset: "0",mobileNumber:user?.mobileNumber, tenantId };
  // Through the search Call i am extracting the Counts to show the total Application
  const { data } = Digit.Hooks.cnd.useCndSearchApplication({ filters: filter1 });

  if (!Digit.Utils.cndUserAccess()) {
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
      link: `/cnd-ui/employee/cnd/my-request`,
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
