import React from "react";
import { useTranslation } from "react-i18next";
import { EmployeeModuleCard, PTIcon } from "@nudmcdgnpm/digit-ui-react-components";

const GCCard = () => {
  const { t } = useTranslation();
  const tenantId = Digit.ULBService.getCurrentTenantId();

  const propsForModuleCard = {
    Icon: <PTIcon />,
    moduleName: t("GC_MODULE_NAME"),
    kpis: [],
    links: [
      { label: t("GC_INBOX"), link: `/upyog-ui/employee/gc/inbox` },
      { label: t("GC_SEARCH_APPLICATION"), link: `/upyog-ui/employee/gc/search` },
      { label: t("GC_NEW_APPLICATION"), link: `/upyog-ui/employee/gc/new-application` },
      { label: t("GC_BILLS"), link: `/upyog-ui/employee/gc/bills` },
    ],
  };

  return <EmployeeModuleCard {...propsForModuleCard} />;
};

export default GCCard;
