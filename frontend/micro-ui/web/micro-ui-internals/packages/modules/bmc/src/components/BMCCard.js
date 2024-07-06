import { EmployeeModuleCard, PersonIcon } from "@egovernments/digit-ui-react-components";
import React from "react";
import { useTranslation } from "react-i18next";

const BMCCard = () => {
 
    const { t } = useTranslation();
  
    const propsForModuleCard = {
      Icon: <PersonIcon/>,
      moduleName: t("BMC"),
      kpis: ["1"],
      links: [
        {
          label: t("Verify Applications"),
          link: `/${window?.contextPath}/employee/bmc/aadhaarverify`,
  
        }
        ,
        {
          label: t("Randomize Applications"),
          link: `/${window?.contextPath}/employee/bmc/randmization`,
  
        },
        {
          label: t("Cross Verify Randomized Applications"),
          link: `/${window?.contextPath}/employee/bmc/crossverify`,
  
        },
        {
          label: t("Approve Crossed Verified Applications"),
          link: `/${window?.contextPath}/employee/bmc/approve`,
  
        },
    ],longModuleName:false};
    return <EmployeeModuleCard {...propsForModuleCard} />;
  };
  
  export default BMCCard;