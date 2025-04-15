import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { EmployeeModuleCard,PropertyHouse } from "@nudmcdgnpm/digit-ui-react-components";

/**
 * `CNDVendorCard` component is a module card which will render in Vendor Side login Only.
 * It fetches data for the general inbox, displaying the total count and nearing SLA count for Construction & Demolition.
 * The component provides links for navigating to various CND-related pages such as the inbox & application search.
 */

const CNDVendorCard = () => {
  const { t } = useTranslation();

  const [total, setTotal] = useState("-");
    const tenantId = Digit.ULBService.getCitizenCurrentTenant(true) || Digit.ULBService.getCurrentTenantId();
    const { data, isLoading, isFetching, isSuccess } = Digit.Hooks.useNewInboxGeneral({
      tenantId: tenantId,
      ModuleCode: "CND",
      filters: { limit: 10, offset: 0, services: ["cnd"] },

      config: {
        select: (data) => {
          return {totalCount:data?.totalCount,nearingSlaCount:data?.nearingSlaCount} || "-";
        },
        enabled: Digit.Utils.cndUserAccess(),
      },
    });
  useEffect(() => {
      if (!isFetching && isSuccess) setTotal(data);
    }, [isFetching]);
  
    if (!Digit.Utils.cndUserAccess()) {
      return null;
    }

  const links=[
    {
      label: t("ES_COMMON_INBOX"),
      link: `/cnd-ui/citizen/cnd/inbox`,
    },
    {
      label: t("ES_COMMON_APPLICATION_SEARCH"),
      link: `/cnd-ui/citizen/cnd/my-request`,
    }
  ]
  const propsForModuleCard = {
    Icon: <PropertyHouse/>,
    moduleName: t("MODULE_CND"),
    kpis: [
      {
        count: total?.totalCount,
        label: t("ES_TITLE_INBOX"),
        link: `/cnd-ui/employee/cnd/inbox`,
      }
    ],
    links,
  };

  return <EmployeeModuleCard {...propsForModuleCard} />;
};

export default CNDVendorCard;

