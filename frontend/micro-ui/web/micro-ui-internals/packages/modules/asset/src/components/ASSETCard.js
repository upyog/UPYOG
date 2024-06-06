import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { EmployeeModuleCard, PropertyHouse } from "@upyog/digit-ui-react-components";

const ASSETCard = () => {
  const { t } = useTranslation();

  

  if (!Digit.Utils.assetAccess()) {
    return null;
  }
  const links=[
    
    // {
    //   label: t("ASSET_ADD"),
    //   link: `/digit-ui/employee/asset/assetservice/new-asset`,
    //   role: "ASSET_INITIATOR",
    // },
    {
      label: t("ASSET_ADD"),
      link: `/digit-ui/employee/asset/assetservice/new-assets`,
      role: "ASSET_INITIATOR",
    },
    {
      label: t("MY_ASSET_APPLICATION"),
      link: `/digit-ui/employee/asset/assetservice/my-asset`,
    },
    {
      label: t("AST_REPORT"),
      link: `/digit-ui/employee/asset/assetservice/report`,
    },
   
  ]
  const ASSET_INITIATOR = Digit.UserService.hasAccess(["ASSET_INITIATOR","ASSET_VERIFIER", "ASSET_APPROVER"]) || false;
  const propsForModuleCard = {
    // Icon: <PropertyHouse />,
    moduleName: t("TITLE_ASSET_MANAGEMENT"),
    kpis: [
      
    ],
    links:links.filter(link=>!link?.role||ASSET_INITIATOR),
  };

  return <EmployeeModuleCard {...propsForModuleCard} />;
};

export default ASSETCard;
