import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { EmployeeModuleCard } from "@nudmcdgnpm/digit-ui-react-components";

const VENDORCard = () => {
  const { t } = useTranslation();

  const [total, setTotal] = useState("-");
  

  

  if (!Digit.Utils.vendorAccess()) {
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
      link: `/digit-ui/employee/vendor/registry/additionaldetails`
    },
    {
      label: t("VENDOR_NEW_REGISTERATION"),
      link: `/digit-ui/employee/vendor/registry/new-vendor`
    },
    {
      label: t("SEARCH_VENDOR"),
      link: `/digit-ui/employee/vendor/search-vendor`
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
  
  const VENDORRole = Digit.UserService.hasAccess(["VENDOR"]) || false;


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
