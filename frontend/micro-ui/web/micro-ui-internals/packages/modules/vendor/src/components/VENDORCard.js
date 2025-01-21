import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { EmployeeModuleCard } from "@nudmcdgnpm/digit-ui-react-components";

const VENDORCard = () => {
  const { t } = useTranslation();

  const [total, setTotal] = useState("-");
  // const { data, isLoading, isFetching, isSuccess } = Digit.Hooks.useNewInboxGeneral({
  //   tenantId: Digit.ULBService.getCurrentTenantId(),
  //   ModuleCode: "VENDOR",
  //   filters: { limit: 10, offset: 0, services: ["asset-create"] },

  //   config: {
  //     select: (data) => {
  //       return {totalCount:data?.totalCount,nearingSlaCount:data?.nearingSlaCount} || "0";
  //     },
  //     enabled: Digit.Utils.assetAccess(),
  //   },
  // });

  // useEffect(() => {
  //   if (!isFetching && isSuccess) setTotal(data);
  // }, [isFetching]);

  

  if (!Digit.Utils.assetAccess()) {
    return null;
  }
  const links=[
    // {
    //   count: isLoading ? "0" : total?.totalCount,
    //   label: t("Inbox"),
    //   link: `/digit-ui/employee/asset/assetservice/inbox`,
    // },
    {
      label: t("ADDITIONAL_VENDOR_DETAILS"),
      link: `/digit-ui/employee/vendor/new-application`,
      role: "VENDOR"
    },
    {
      label: t("VENDOR_NEW_REGISTERATION"),
      link: `/digit-ui/employee/vendor/registry/new-vendor`,
      role: "VENDOR"
    },
    {
      label: t("SEARCH_VENDOR"),
      link: `/digit-ui/employee/vendor/search-vendor`,
      role: "VENDOR"
    },
    // {
    //   label: t("MY_ASSET_APPLICATION"),
    //   link: `/digit-ui/employee/asset/assetservice/my-asset`,
    // }
    // {
    //   label: t("AST_REPORT"),
    //   link: `/digit-ui/employee/asset/assetservice/report`,
    // }
   
  ]
  const VENDORRole = Digit.UserService.hasAccess(["ASSET_INITIATOR"]) || false;


  const propsForModuleCard = {
    // Icon: <PropertyHouse />,
    moduleName: t("TITLE_VENDOR_MANAGEMENT"),
    kpis: [
      {
        count: total?.totalCount,
        label: t("Inbox"),
        link: `/digit-ui/employee/asset/assetservice/inbox`
      },
      
    ],
    links:links.filter(link=>!link?.role || VENDORRole ),
  };

  return <EmployeeModuleCard {...propsForModuleCard} />;
};

export default VENDORCard;
