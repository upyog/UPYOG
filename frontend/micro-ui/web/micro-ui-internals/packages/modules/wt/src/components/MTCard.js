import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { EmployeeModuleCard,CHBIcon } from "@nudmcdgnpm/digit-ui-react-components";

/**
 * `MTCard` component is a module card that displays information related to the Mobile Toilet (MT) service.
 * It fetches data for the general inbox, displaying the total count and nearing SLA count for mobile Toilet requests.
 * The component provides links for navigating to various MT-related pages such as the inbox, request tanker, and application search.
 * It conditionally renders the links based on the user's role (MT_CEMP). If the user doesn't have access to the WT service, the component returns null.
 * The component uses React's `useEffect` hook to update the total count once data is successfully fetched.
 * 
 * @returns {JSX.Element} A module card displaying WT-related KPIs and links.
 */
const MTCard = () => {
  const { t } = useTranslation();

  const [total, setTotal] = useState("-");
  const { data, isLoading, isFetching, isSuccess } = Digit.Hooks.useNewInboxGeneral({
    tenantId: Digit.ULBService.getCurrentTenantId(),
    ModuleCode: "MT",
    filters: { limit: 10, offset: 0, services: ["mobileToilet"] },

    config: {
      select: (data) => {
        return {totalCount:data?.totalCount,nearingSlaCount:data?.nearingSlaCount} || "-";
      },
      enabled: Digit.Utils.mtAccess(),
    },
  });

  useEffect(() => {
    if (!isFetching && isSuccess) setTotal(data);
  }, [isFetching]);

  if (!Digit.Utils.mtAccess()) {
    return null;
  }
  const links=[
    {
      count: isLoading ? "-" : total?.totalCount,
      label: t("ES_COMMON_INBOX"),
      link: `/digit-ui/employee/wt/mt/inbox`,
    },
  ]
  const MT_CEMP = Digit.UserService.hasAccess(["MT_CEMP"]) || false;
  const propsForModuleCard = {
    Icon: <CHBIcon/>,
    moduleName: t("ACTION_TEST_MT"),
    kpis: [
      {
        count: total?.totalCount,
        label: t("ES_TITLE_INBOX"),
        link: `/digit-ui/employee/mt/inbox`,
      },
    ],
    links:links.filter(link=>!link?.role||MT_CEMP),
  };

  return <EmployeeModuleCard {...propsForModuleCard} />;
};

export default MTCard;
