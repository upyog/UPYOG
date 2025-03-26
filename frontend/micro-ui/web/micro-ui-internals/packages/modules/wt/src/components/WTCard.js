import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { EmployeeModuleCard,CHBIcon } from "@nudmcdgnpm/digit-ui-react-components";

/**
 * `WTCard` component is a module card that displays information related to the Water Tanker (WT) service.
 * It fetches data for the general inbox, displaying the total count and nearing SLA count for water tanker requests.
 * The component provides links for navigating to various WT-related pages such as the inbox, request tanker, and application search.
 * It conditionally renders the links based on the user's role (WT_CEMP). If the user doesn't have access to the WT service, the component returns null.
 * The component uses React's `useEffect` hook to update the total count once data is successfully fetched.
 * 
 * @returns {JSX.Element} A module card displaying WT-related KPIs and links.
 */
const WTCard = () => {
  const { t } = useTranslation();

  const [total, setTotal] = useState("-");
  const { data, isLoading, isFetching, isSuccess } = Digit.Hooks.useNewInboxGeneral({
    tenantId: Digit.ULBService.getCurrentTenantId(),
    ModuleCode: "WT",
    filters: { limit: 10, offset: 0, services: ["watertanker"] },

    config: {
      select: (data) => {
        return {totalCount:data?.totalCount,nearingSlaCount:data?.nearingSlaCount} || "-";
      },
      enabled: Digit.Utils.wtAccess(),
    },
  });

  useEffect(() => {
    if (!isFetching && isSuccess) setTotal(data);
  }, [isFetching]);

  if (!Digit.Utils.wtAccess()) {
    return null;
  }
  const links=[
    {
      count: isLoading ? "-" : total?.totalCount,
      label: t("ES_COMMON_INBOX"),
      link: `/digit-ui/employee/wt/inbox`,
    },
    {
      label: t("ES_COMMON_APPLICATION_SEARCH"),
      link: `/digit-ui/employee/wt/my-bookings`,
    }
  ]
  const WT_CEMP = Digit.UserService.hasAccess(["WT_CEMP"]) || false;
  const propsForModuleCard = {
    Icon: <CHBIcon/>,
    moduleName: t("WT_MODULE_NAME"),
    kpis: [
      {
        count: total?.totalCount,
        label: t("ES_TITLE_INBOX"),
        link: `/digit-ui/employee/wt/inbox`,
      },
    ],
    links:links.filter(link=>!link?.role||WT_CEMP),
  };

  return <EmployeeModuleCard {...propsForModuleCard} />;
};

export default WTCard;
