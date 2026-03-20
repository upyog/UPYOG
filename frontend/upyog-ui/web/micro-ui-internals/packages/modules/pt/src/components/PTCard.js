import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";
// import { PropertyHouse } from "@upyog/digit-ui-react-components";
import { EmployeeModuleCard } from "../../../../react-components/src/index"

function PropertyHouse({ className, styles }) {
  return (
    <svg className={className} fill="#FFFFFF" style={{ ...styles }} width="24" height="24" viewBox="0 0 40 40" xmlns="http://www.w3.org/2000/svg">
      <path d="M13.6167 9.5L1.66667 17.4667V35H10V21.6667H16.6667V35H25V17.0833L13.6167 9.5Z" />
      <path d="M16.6667 5V7.51667L20 9.73333L22.8833 11.6667H25V13.0833L28.3333 15.3167V18.3333H31.6667V21.6667H28.3333V25H31.6667V28.3333H28.3333V35H38.3333V5H16.6667ZM31.6667 15H28.3333V11.6667H31.6667V15Z" />
    </svg>
  );
}

const PTCard = () => {
  const { t } = useTranslation();

  const [total, setTotal] = useState("-");
  const { data, isLoading, isFetching, isSuccess } = Digit.Hooks.useNewInboxGeneral({
    tenantId: Digit.ULBService.getCurrentTenantId(),
    ModuleCode: "PT",
    filters: { limit: 10, offset: 0, services: ["PT.CREATE", "PT.MUTATION", "PT.UPDATE"] },
    config: {
      select: (data) => {
        return { totalCount: data?.totalCount, nearingSlaCount: data?.nearingSlaCount } || "-";
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
  const links = [
    {
      count: isLoading ? "-" : total?.totalCount,
      label: t("ES_COMMON_INBOX"),
      link: `/mycity-ui/employee/pt/inbox`,
    },
    {
      label: t("ES_TITLE_NEW_REGISTRATION"),
      link: `/mycity-ui/employee/pt/new-application`,
      role: "PT_CEMP"
    },
    {
      label: t("SEARCH_PROPERTY"),
      link: `/mycity-ui/employee/pt/search`,
    },
    {
      label: t("ES_COMMON_APPLICATION_SEARCH"),
      link: `/mycity-ui/employee/pt/application-search`,
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
        link: `/mycity-ui/employee/pt/inbox`,
      },
      {

        count: total?.nearingSlaCount,
        label: t("TOTAL_NEARING_SLA"),
        link: `/mycity-ui/employee/pt/inbox`,
      }
    ],
    links: links.filter(link => !link?.role || PT_CEMP),
  };

  return <EmployeeModuleCard {...propsForModuleCard} />;
};

export default PTCard;
