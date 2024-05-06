import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { EmployeeModuleCard, PropertyHouse } from "@egovernments/digit-ui-react-components";

const PTCard = () => {
  console.log("PTCard")
  const { t } = useTranslation();
  const parseValue = (value) => {
    try {
      return JSON.parse(value)
    } catch (e) {
      return value
    }
  }
  const getFromStorage = (key) => {
    const value = window.localStorage.getItem(key);
    return value && value !== "undefined" ? parseValue(value) : null;
  }
  const employeeToken = getFromStorage("Employee.token")
  const employeeInfo = getFromStorage("Employee.user-info")
  const getUserDetails = (access_token, info) => ({ token: access_token, access_token, info })

  const userDetails = getUserDetails(employeeToken, employeeInfo)
  
  console.log("userDetailsPTCard===",userDetails)
  let userRole='';
  if(userDetails && userDetails.info && userDetails.info?.roles) {
    userDetails.info.roles.map((role)=>{
      if(role?.code == "ASSIGNING_OFFICER") userRole = role.code;
    })
  }

  const [total, setTotal] = useState("-");
  const { data, isLoading, isFetching, isSuccess } = Digit.Hooks.useNewInboxGeneral({
    tenantId: Digit.ULBService.getCurrentTenantId(),
    ModuleCode: "PT",
    filters: { limit: 10, offset: 0, services: userRole && userRole=='ASSIGNING_OFFICER' ? ["ASMT"] : ["PT.CREATE", "PT.MUTATION", "PT.UPDATE"]},
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
    // {
    //   count: isLoading ? "-" : total?.totalCount,
    //   label: t("ES_COMMON_INBOX"),
    //   link: `/digit-ui/employee/pt/inbox`,
    // },
    // {
    //   label: t("ES_TITLE_NEW_REGISTRATION"),
    //   link: `/digit-ui/employee/pt/new-application`,
    //   role: "PT_CEMP"
    // },
    {
      label: t("SEARCH_PROPERTY"),
      link: `/digit-ui/employee/pt/search`,
      image: "https://mnptapp-terraform.s3.ap-south-1.amazonaws.com/images/MyApplication.png",
    },
    {
      label: t("ES_COMMON_APPLICATION_SEARCH"),
      link: `/digit-ui/employee/pt/application-search`,
      image: "https://mnptapp-terraform.s3.ap-south-1.amazonaws.com/images/Property-Transfer.png",
    },
  ]
  const PT_CEMP = Digit.UserService.hasAccess(["PT_CEMP"]) || false;
  const propsForModuleCard = {
    Icon: <PropertyHouse />,
    moduleName: t("ES_TITLE_PROPERTY_TAX"),
    kpis: [
      {
        count: total?.totalCount,
        label: t("ES_TITLE_INBOX"),
        link: `/digit-ui/employee/pt/inbox`,
        image: "https://mnptapp-terraform.s3.ap-south-1.amazonaws.com/images/MyProperty.png",
      },
      // {
        
      //   count: total?.nearingSlaCount,
      //   label: t("TOTAL_NEARING_SLA"),
      //   link: `/digit-ui/employee/pt/inbox`,
      // },
      {
        
        count: 7,
        label: t("Notices"),
        link: `/digit-ui/employee/pt/notices`,
        image: "https://mnptapp-terraform.s3.ap-south-1.amazonaws.com/images/My-Payments.png",
      }
    ],
    links:links.filter(link=>!link?.role||PT_CEMP),
  };
  console.log("propsForModuleCard--",propsForModuleCard)
  return <EmployeeModuleCard {...propsForModuleCard} />;
};

export default PTCard;
