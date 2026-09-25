import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { EmployeeModuleCard } from "@nudmcdgnpm/digit-ui-react-components";

const ASSETV2Card = () => {
  const { t } = useTranslation();

  const [total, setTotal] = useState("-");
  const { data, isLoading, isFetching, isSuccess } = Digit.Hooks.useNewInboxGeneral({
    tenantId: Digit.ULBService.getCurrentTenantId(),
    ModuleCode: "ASSET",
    filters: { limit: 10, offset: 0, services: ["asset-create"] },

    config: {
      select: (data) => {
        return {totalCount:data?.totalCount,nearingSlaCount:data?.nearingSlaCount} || "0";
      },
      enabled: Digit.Utils.assetAccess(),
    },
  });

  useEffect(() => {
    if (!isFetching && isSuccess) setTotal(data);
  }, [isFetching]);

  

  if (!Digit.Utils.assetAccess()) {
    return null;
  }
  const links=[
    {
      count: isLoading ? "0" : total?.totalCount,
      label: t("Inbox"),
      link: `/upyog-ui/employee/assetv2/assetservice/inbox`,
    },
    {
      label: t("ASSET_ADD"),
      link: `/upyog-ui/employee/assetv2/assetservice/new-assets`,
      role: "ASSET_INITIATOR"
    },
    {
      label: t("MY_ASSET_APPLICATION"),
      link: `/upyog-ui/employee/assetv2/assetservice/my-asset`,
    }
    // {
    //   label: t("AST_REPORT"),
    //   link: `/upyog-ui/employee/asset/assetservice/report`,
    // }
   
  ]
  const ASSETRole = Digit.UserService.hasAccess(["ASSET_INITIATOR"]) || false;
  const propsForModuleCard = {
    // Icon: <PropertyHouse />,
    moduleName: t("ASSET_MCD"),
    kpis: [
      {
        count: total?.totalCount,
        label: t("Inbox"),
        link: `/upyog-ui/employee/assetv2/assetservice/inbox`
      },
      
    ],
    links:links.filter(link=>!link?.role || ASSETRole ),
  };

  return <EmployeeModuleCard {...propsForModuleCard} />;
};

export default ASSETV2Card;
