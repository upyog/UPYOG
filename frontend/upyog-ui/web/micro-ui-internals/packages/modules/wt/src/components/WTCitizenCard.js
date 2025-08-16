import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { EmployeeModuleCard,CHBIcon } from "@nudmcdgnpm/digit-ui-react-components";
import { APPLICATION_PATH } from "../utils";

/**
 * `WTCitizenCard` component is a module card that displays information related to the Water Tanker (WT) service.
 * It fetches data for the general inbox, displaying the total count and nearing SLA count for water tanker requests.
 * The component provides links for navigating to various WT-related pages such as the inbox, request tanker, and application search.
 * @returns {JSX.Element} A module card displaying WT-related KPIs and links.
 */
const WTCitizenCard = () => {
  const { t } = useTranslation();

  const [total, setTotal] = useState("-");
    const tenantId = Digit.ULBService.getCitizenCurrentTenant(true) || Digit.ULBService.getCurrentTenantId();
    const { data, isLoading, isFetching, isSuccess } = Digit.Hooks.useNewInboxGeneral({
      tenantId: tenantId,
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
      label: t("ES_COMMON_INBOX"),
      link: `${APPLICATION_PATH}/citizen/wt/inbox`,
    },
    {
      label: t("ES_COMMON_APPLICATION_SEARCH"),
      link: `${APPLICATION_PATH}/citizen/wt/my-bookings`,
    }
  ]
  const propsForModuleCard = {
    Icon: <CHBIcon/>,
    moduleName: t("WT_MODULE_NAME"),
    kpis: [
      {
        count: total?.totalCount,
        label: t("ES_TITLE_INBOX"),
        link: `${APPLICATION_PATH}/employee/wt/inbox`,
      }
    ],
    links,
  };

  return <EmployeeModuleCard {...propsForModuleCard} />;
};

export default WTCitizenCard;

