import React from "react";
import { useTranslation } from "react-i18next";
import { EmployeeModuleCard, PropertyHouse } from "@nudmcdgnpm/digit-ui-react-components";

// custom component for the employee card
const SVCard = () => {
  const { t } = useTranslation();

  if (!Digit.Utils.ewAccess()) {
    return null;
  }
  const links = [
    {
      label: t("SV_INBOX"),
      link: `/digit-ui/employee/sv/inbox`,
    },
    {
      label: t("SV_CREATE_APPLICATION"),
      link: `/digit-ui/employee/sv/apply`,
    },
    {
      label: t("ES_COMMON_APPLICATION_SEARCH"),
      link: `/digit-ui/employee/sv/my-applications`,
    },
  ]
  const SV_CEMP = Digit.UserService.hasAccess(["EW_VENDOR"]) || false;

  const propsForModuleCard = {
    Icon: <PropertyHouse />,
    moduleName: t("MODULE_SV"),
    kpis: [],
    links: links.filter(link => !link?.role || SV_CEMP),
  };

  return <EmployeeModuleCard {...propsForModuleCard} />;
};

export default SVCard;
