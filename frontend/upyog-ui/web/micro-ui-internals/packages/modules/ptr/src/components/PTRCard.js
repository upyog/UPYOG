/**
 * @file PTRCard.js
 * @description Displays the pet registration module card with KPIs and navigation links for the employee portal.
 * 
 * @components
 * - `EmployeeModuleCard`: Renders the module card with icon, KPIs, and navigation links.
 * - `PropertyHouse`: Icon representing property or house.
 * 
 * @hooks
 * - `useNewInboxGeneral`: Fetches the inbox data for the PTR module.
 * - `useEffect`: Updates the total count on successful data fetch.
 * 
 * @props
 * - `data`: Inbox data containing:
 *    - `totalCount`: Total applications count.
 *    - `nearingSlaCount`: Count of nearing SLA applications.
 * - `isLoading`: Boolean indicating data loading state.
 * - `isFetching`: Boolean indicating whether data is being fetched.
 * - `isSuccess`: Boolean indicating successful data fetch.
 * 
 * @variables
 * - `links`: Navigation links for inbox, new pet registration, and application search.
 * - `PTR_CEMP`: Boolean indicating if the user has access to specific PTR roles.
 * - `propsForModuleCard`: Configuration for the module card.
 */

import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { EmployeeModuleCard, PropertyHouse } from "@upyog/digit-ui-react-components";

const PTRCard = () => {
  const { t } = useTranslation();

  const [total, setTotal] = useState("-");
  const { data, isLoading, isFetching, isSuccess } = Digit.Hooks.useNewInboxGeneral({
    tenantId: Digit.ULBService.getCurrentTenantId(),
    ModuleCode: "PTR",
    filters: { limit: 10, offset: 0, services: ["ptr"] },

    config: {
      select: (data) => {
        return {totalCount:data?.totalCount,nearingSlaCount:data?.nearingSlaCount} || "-";
      },
      enabled: Digit.Utils.ptrAccess(),
    },
  });

  useEffect(() => {
    if (!isFetching && isSuccess) setTotal(data);
  }, [isFetching]);

  if (!Digit.Utils.ptrAccess()) {
    return null;
  }
  const links=[
    {
      count: isLoading ? "-" : total?.totalCount,
      label: t("ES_COMMON_INBOX"),
      link: `/upyog-ui/employee/ptr/petservice/inbox`,
    },
    {
      label: t("PTR_TITLE_NEW_PET_REGISTRATION"),
      link: `/upyog-ui/employee/ptr/petservice/new-application`,
      role: "PTR_CEMP"
    },
    {
      label: t("ES_COMMON_APPLICATION_SEARCH"),
      link: `/upyog-ui/employee/ptr/petservice/my-applications`,
    },
  ]
  const PTR_CEMP = Digit.UserService.hasAccess(["PTR_APPROVER", "PTR_CEMP", "PTR_VERIFIER"]) || false;
  const propsForModuleCard = {
    Icon: <PropertyHouse />,
    moduleName: t("PTR_TITLE_PET_REGISTRATION"),
    kpis: [
      {
        count: total?.totalCount,
        label: t("ES_TITLE_INBOX"),
        link: `/upyog-ui/employee/ptr/petservice/inbox`,
      },
    ],
    links:links.filter(link=>!link?.role||PTR_CEMP),
  };

  return <EmployeeModuleCard {...propsForModuleCard} />;
};

export default PTRCard;
