import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { EmployeeModuleCard,CHBIcon } from "@upyog/digit-ui-react-components";

/*
 * This component renders the Advertisement module card for the employee interface.
 * It provides quick access to relevant ADS functionalities based on user permissions. 
*/
const ADSCard = () => {
  const { t } = useTranslation();

  const [total, setTotal] = useState("-");
 
  if (!Digit.Utils.adsAccess()) {
    return null;
  }
  const links=[
  
    {
      label: t("ADS_BOOK"),
      link: `/upyog-ui/employee/ads/bookad/searchads`,
    },
    //in progress for search application page on employee side
    {
      label: t("ADS_SEARCH_BOOKINGS"),
      link: `/upyog-ui/employee/ads/my-applications`,
    },
  ]
  const ADS_CEMP = Digit.UserService.hasAccess(["ADS_CEMP"]) || false;
  const propsForModuleCard = {
    Icon: <CHBIcon/>,
    moduleName: t("ADS_ADVERTISEMENT_MODULE"),
    
    links:links.filter(link=>!link?.role||ADS_CEMP),
  };

  return <EmployeeModuleCard {...propsForModuleCard} />;
};

export default ADSCard;
