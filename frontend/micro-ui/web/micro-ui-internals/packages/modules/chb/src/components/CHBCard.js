import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { EmployeeModuleCard,CHBIcon } from "@nudmcdgnpm/digit-ui-react-components";

const CHBCard = () => {
  const { t } = useTranslation();

  const [total, setTotal] = useState("-");
  const { data, isLoading, isFetching, isSuccess } = Digit.Hooks.useNewInboxGeneral({
    tenantId: Digit.ULBService.getCurrentTenantId(),
    ModuleCode: "CHB",
    filters: { limit: 10, offset: 0, services: ["chb"] },

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

  if (!Digit.Utils.chbAccess()) {
    return null;
  }
  const links=[
    {
      count: isLoading ? "-" : total?.totalCount,
      label: t("ES_COMMON_INBOX"),
      link: `/digit-ui/employee/chb/bookHall/inbox`,
    },
    {
      label: t("ES_COMMON_APPLICATION_SEARCH"),
      link: `/digit-ui/employee/chb/bookHall/my-applications`,
    },
  ]
  const CHB_CEMP = Digit.UserService.hasAccess(["CHB_APPROVER","CHB_VERIFIER"]) || false;
  const propsForModuleCard = {
    Icon: <CHBIcon/>,
    moduleName: t("ACTION_TEST_CHB"),
    kpis: [
      {
        count: total?.totalCount,
        label: t("ES_TITLE_INBOX"),
        link: `/digit-ui/employee/chb/searchHall/inbox`,
      },
    ],
    links:links.filter(link=>!link?.role||CHB_CEMP),
  };

  return <EmployeeModuleCard {...propsForModuleCard} />;
};

export default CHBCard;
