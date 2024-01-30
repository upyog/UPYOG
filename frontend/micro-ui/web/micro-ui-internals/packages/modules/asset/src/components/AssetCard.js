import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { EmployeeModuleCard, PropertyHouse } from "@egovernments/digit-ui-react-components";

const AssetCard = () => {
  const { t } = useTranslation();

  const [total, setTotal] = useState("-");
  const { data, isLoading, isFetching, isSuccess } = Digit.Hooks.useNewInboxGeneral({
    tenantId: Digit.ULBService.getCurrentTenantId(),
    ModuleCode: "Astmgt",
    // filters: { limit: 10, offset: 0, services: ["PT.CREATE", "PT.UPDATE"] },
    filters: { limit: 10, offset: 0, services: ["asset"] },

    config: {
      select: (data) => {
        return {totalCount:data?.totalCount,nearingSlaCount:data?.nearingSlaCount} || "-";
      },
      enabled: Digit.Utils.ptAccess(),
    },
  });

  useEffect(() => {
    if (!isFetching && isSuccess) setTotal(data);
  }, [isFetching]);

  if (!Digit.Utils.ptAccess()) {
    return null;
  }
  const links=[
    
    {
      label: t("AST_TITLE_ASSET_MANAGEMENT"),
      link: `/digit-ui/employee/assetservice/new-asset`,
      role: "PT_CEMP"
    },
    {
      label: t("AST_TITLE_APPLICATION_SEARCH"),
      link: `/digit-ui/employee/assetservice/my-assets`,
    },
  ]
  const PT_CEMP = Digit.UserService.hasAccess(["PT_CEMP"]) || false;
  const propsForModuleCard = {
    Icon: <PropertyHouse />,
    moduleName: t("AST_TITLE_ASSET_MANAGEMENT"),
    kpis: [
      // {
      //   count: total?.totalCount,
      //   label: t("ES_TITLE_INBOX"),
      //   link: `/digit-ui/employee/ptr/petservice/inbox`,
      // },
      // {
        
      //   count: total?.nearingSlaCount,
      //   label: t("TOTAL_NEARING_SLA"),
      //   link: `/digit-ui/employee/pt/inbox`,
      // }
    ],
    links:links.filter(link=>!link?.role||PT_CEMP),
  };

  return <EmployeeModuleCard {...propsForModuleCard} />;
};

export default AssetCard;
