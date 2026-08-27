import { EmployeeModuleCard, PTIcon } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { useTranslation } from "react-i18next";

/**
 * ChallanGenerationCard component:
 * - Displays module card for challan generation
 * - Shows total challan count
 * - Provides navigation links for search and creation
 */

const ChallanGenerationCard = () => {
  if (!Digit.Utils.challanAccess()) {
    return null;
  }
  const { t } = useTranslation();
  const tenantId = Digit.ULBService.getCurrentTenantId();

  const { data, isFetching, isSuccess, isLoading } = Digit.Hooks.challangeneration.useInbox({
    tenantId: tenantId,
    ModuleCode: "Challan_Generation",
    filters: { limit: 10, offset: 0, services: ["Challan_Generation"] },

    config: {
      select: (data) => {
        return { totalCount: data?.totalCount, nearingSlaCount: data?.nearingSlaCount } || "-";
      },
      enabled: Digit.Utils.challanAccess(),
    },
  });

  const propsForModuleCard = {
    Icon: <PTIcon />,
    moduleName: t("Challan_Generation"),
    kpis: [
      {
        count: isLoading ? "-" : data?.totalCount,
        label: t("TOTAL_CHALLANS")
      },
    ],
    links: [
      {
        label: t("UC_SEARCH_CHALLAN_LABEL"),
        link: `/upyog-ui/employee/challangeneration/inbox`
      },
      {
        label: t("UC_GENERATE_NEW_CHALLAN"),
        link: `/upyog-ui/employee/challangeneration/generate-challan`
      },
    ]

  }
  console.log("data", data);
  return <EmployeeModuleCard {...propsForModuleCard} />
};

export default ChallanGenerationCard;

