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
          label: t("VERIFY APPLICATIONS"),
          link: `/${window?.contextPath}/employee/bmc/aadhaarverify`,
  
        }
        ,
        {
          label: t("RANDOMIZE APPLICATIONS"),
          link: `/${window?.contextPath}/employee/bmc/randmization`,
  
        },
        {
          label: t("APPROVE APPLICATIONS"),
          link: `/${window?.contextPath}/employee/bmc/crossverify`,
  
        },
        {
          label: t("APPROVE CROSSED VERIFIED APPLICATIONS"),
          link: `/${window?.contextPath}/employee/bmc/approve`,
  
        },
    ],longModuleName:false};
    return <EmployeeModuleCard {...propsForModuleCard} />;
  };
  
  export default BMCCard;