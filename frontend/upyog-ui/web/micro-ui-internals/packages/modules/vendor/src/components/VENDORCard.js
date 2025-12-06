import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { EmployeeModuleCard } from "@upyog/digit-ui-react-components";

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
    //   link: `/upyog-ui/employee/asset/assetservice/inbox`,
    // },
    
    {
      label: t("SEARCH_VENDOR"),
      link: `/upyog-ui/employee/vendor/search-vendor?selectedTabs=VENDOR`
    },
    // {
    //   label: t("MY_ASSET_APPLICATION"),
    //   link: `/upyog-ui/employee/asset/assetservice/my-asset`,
    // }
    // {
    //   label: t("AST_REPORT"),
    //   link: `/upyog-ui/employee/asset/assetservice/report`,
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
        link: `/upyog-ui/employee/asset/assetservice/inbox`
      },
      
    ],
    links:links.filter(link=>!link?.role || VENDORRole ),
  };

  return <EmployeeModuleCard {...propsForModuleCard} />;
};

export default VENDORCard;
