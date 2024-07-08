import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { EmployeeModuleCard, PropertyHouse } from "@nudmcdgnpm/digit-ui-react-components";

const EWCard = () => {
  const { t } = useTranslation();

  

  if (!Digit.Utils.ewAccess()) {
    return null;
  }
  const links=[
    //  {
    //    count: isLoading ? "-" : total?.totalCount,
    //    label: t("ES_COMMON_INBOX"),
    //    link: `/digit-ui/employee/ptr/petservice/inbox`,
    //  },
     {
       label: t("INBOX"),
       link: `/digit-ui/employee/ew/inbox`,
       role: "EW_CEMP"
     },
     {
      label: t("ES_COMMON_APPLICATION_SEARCH"),
      link: `/digit-ui/employee/ew/my-applications`,
   },
  ]
  const EW_CEMP = Digit.UserService.hasAccess(["EW_VENDOR"]) || false;
  const propsForModuleCard = {
    Icon: <PropertyHouse />,
    moduleName: t("TITLE_E_WASTE"),
    kpis: [],
    links:links.filter(link=>!link?.role||EW_CEMP),
  };

  return <EmployeeModuleCard {...propsForModuleCard} />;
};

export default EWCard;
