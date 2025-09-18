import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { EmployeeModuleCard,CHBIcon } from "@upyog/digit-ui-react-components";
import { APPLICATION_PATH } from "../utils";

/**
 * `TPCard` component is a module card that displays information related to the Tree Pruning service.
 * It fetches data for the general inbox, displaying the total count and nearing SLA count for mobile Toilet requests.
 * The component provides links for navigating to various TP-related pages such as the inbox, request tanker, and application search.
 * It conditionally renders the links based on the user's role (TP_CEMP). If the user doesn't have access to the TP service, the component returns null.
 * The component uses React's `useEffect` hook to update the total count once data is successfully fetched.
 * 
 * @returns {JSX.Element} A module card displaying WT-related KPIs and links.
 */
const TPCard = () => {
  const { t } = useTranslation();

  const [total, setTotal] = useState("-");
  const { data, isLoading, isFetching, isSuccess } = Digit.Hooks.useNewInboxGeneral({
    tenantId: Digit.ULBService.getCurrentTenantId(),
    ModuleCode: "TP",
    filters: { limit: 10, offset: 0, services: ["treePruning"] },

    config: {
      select: (data) => {
        return {totalCount:data?.totalCount,nearingSlaCount:data?.nearingSlaCount} || "-";
      },
      enabled: Digit.Utils.tpAccess(),
    },
  });

  useEffect(() => {
    if (!isFetching && isSuccess) setTotal(data);
  }, [isFetching]);

  if (!Digit.Utils.tpAccess()) {
    return null;
  }
  const links=[
    {
      count: isLoading ? "-" : total?.totalCount,
      label: t("ES_COMMON_INBOX"),
      link: `${APPLICATION_PATH}/employee/wt/tp/inbox`,
    },
    {
      label: t("ES_COMMON_APPLICATION_SEARCH"),
      link: `${APPLICATION_PATH}/employee/wt/tp/my-bookings`,
    },
    {
      label: t("TP_APPLICATION_CREATE"),
      link: `${APPLICATION_PATH}/employee/wt/tp/request-service`,
    }
  ]
  const TP_CEMP = Digit.UserService.hasAccess(["TP_CEMP"]) || false;
  
  /**
 * Configuration object for the Module Card component.
 * 
 * - `Icon`: The icon to be displayed on the module card.
 * - `moduleName`: The name of the module displayed on the card, translated using the `t` function.
 * - `kpis`: An array of Key Performance Indicators (KPIs) to display on the card.
 *    - Each KPI includes:
 *      - `count`: The total count of items (e.g., applications or requests).
 *      - `label`: The label for the KPI, translated using the `t` function.
 *      - `link`: The URL to navigate to when the KPI is clicked.
 * - `links`: An array of links to be displayed on the card, filtered based on roles or conditions.
 */
  const propsForModuleCard = {
    Icon: <CHBIcon/>,
    moduleName: t("ACTION_TEST_TP"),
    kpis: [
      {
        count: total?.totalCount,
        label: t("ES_TITLE_INBOX"),
        link: `${APPLICATION_PATH}/employee/wt/tp/inbox`,
      },
    ],
    links:links.filter(link=>!link?.role||TP_CEMP),
  };

  return <EmployeeModuleCard {...propsForModuleCard} />;
};

export default TPCard;
