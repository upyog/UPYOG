/**
 * @file FireNocCard.js
 * @description Displays the Fire Noc module card with KPIs and navigation links
 * 
 * @components
 * - `EmployeeModuleCard`: Renders the module card with icon, KPIs, and navigation links.
 * - `PropertyHouse`: Icon representing property or house.
 * 
 */

import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { EmployeeModuleCard, PropertyHouse } from "@nudmcdgnpm/digit-ui-react-components";

const FireNocCard = () => {
  const { t } = useTranslation();

  const [total, setTotal] = useState("-");
  const { data, isLoading, isFetching, isSuccess } = Digit.Hooks.useNewInboxGeneral({
    tenantId: Digit.ULBService.getCurrentTenantId(),
    ModuleCode: "FIRENOC",
    filters: { limit: 10, offset: 0, services: ["FIRENOC"] },

    config: {
      select: (data) => {
        return {totalCount:data?.totalCount,nearingSlaCount:data?.nearingSlaCount} || "-";
      },
      enabled: Digit.Utils.NOCAccess(),
    },
  });

  useEffect(() => {
    if (!isFetching && isSuccess) setTotal(data);
  }, [isFetching]);


  if (!Digit.Utils.NOCAccess()) {
    return null;
  }
  const links=[
    {
      label: t("ES_COMMON_INBOX"),
      link: `/upyog-ui/employee/noc/firenoc/inbox`,
    },
    {
      label: t("ES_SEARCH_APPLICATIONS"),
      link: `/upyog-ui/employee/noc/firenoc/my-applications`,
    },
  ]
  const FIRENOC_CEMP = Digit.UserService.hasAccess(["NOC_CEMP","NOC_DOC_VERIFIER","NOC_FIELD_INSPECTOR","NOC_APPROVER"]) || false;
  const propsForModuleCard = {
    Icon: <PropertyHouse />,
    moduleName: t("NOC_FIRE_NOC_CARD"),
    kpis: [
      {
        count: total?.totalCount,
        label: t("ES_INBOX_APPLICATION"),
        link: `/upyog-ui/employee/noc/firenoc/inbox`,
      },
    ],
    links:links.filter(link=>!link?.role||FIRENOC_CEMP),
  };

  return <EmployeeModuleCard {...propsForModuleCard} />;
};

export default FireNocCard;
