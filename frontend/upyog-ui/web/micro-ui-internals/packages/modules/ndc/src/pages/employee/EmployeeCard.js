import React, { useMemo, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { EmployeeModuleCard } from "@nudmcdgnpm/digit-ui-react-components";
import { useLocation } from "react-router-dom";
import { businessServiceList } from "../../utils";

// This component renders a card on the employee dashboard that displays key metrics and links related to NDC (Non-Domestic Connection) applications. It uses hooks to fetch inbox data and displays total count, nearing SLA count, and links to inbox and search pages.
const NDCEmployeeHomeCard = () => {
  const { t } = useTranslation();
  const location = useLocation();
  const tenantId = Digit.ULBService.getCurrentTenantId();

  if (!Digit.Utils.NDCAccess()) return null;

  const searchFormDefaultValues = {};

  const filterFormDefaultValues = {
    moduleName: "NDC",
    applicationStatus: "",
    locality: [],
    assignee: "ASSIGNED_TO_ALL",
    businessServiceArray: businessServiceList(true) || [],
  };

  const tableOrderFormDefaultValues = {
    // sortBy: "",
    limit: 10,
    offset: 0,
    // sortOrder: "DESC"
  };

  const formInitValue = {
    filterForm: filterFormDefaultValues,
    searchForm: searchFormDefaultValues,
    tableForm: tableOrderFormDefaultValues,
  };

  const { isLoading: isInboxLoading, data: { table, statuses, totalCount, nearingSlaCount } = {} } = Digit.Hooks.ndc.useInbox({
    tenantId,
    filters: { ...formInitValue },
    config: { enabled: formInitValue?.filterForm?.businessServiceArray?.length > 0 },
  });

  const ComplaintIcon = () => (
    <svg xmlns="http://www.w3.org/2000/svg" height="24" viewBox="0 0 24 24" width="24">
      <path d="M0 0h24v24H0z" fill="none"></path>
      <path d="M20 2H4c-1.1 0-1.99.9-1.99 2L2 22l4-4h14c1.1 0 2-.9 2-2V4c0-1.1-.9-2-2-2zm-7 9h-2V5h2v6zm0 4h-2v-2h2v2z" fill="white"></path>
    </svg>
  );

  useEffect(() => {
    if (location.pathname === "/upyog-ui/employee") {
      Digit.SessionStorage.del("NDC.INBOX");
    }
  }, [location.pathname]);

  const propsForModuleCard = useMemo(
    () => ({
      Icon: <ComplaintIcon />,
      moduleName: t("ACTION_TEST_NDC"),
      kpis: [
        {
          count: !isInboxLoading ? totalCount : "",
          label: t("TOTAL_FSM"),
          link: `/upyog-ui/employee/ndc/inbox`,
        },
        { count: !isInboxLoading ? nearingSlaCount : "-", label: t("TOTAL_NEARING_SLA"), link: `/upyog-ui/employee/ndc/inbox` },
      ],
      links: [
        {
          count: totalCount,
          label: t("ES_COMMON_INBOX"),
          link: `/upyog-ui/employee/ndc/inbox`,
        },
        {
          count: totalCount,
          label: t("Create Application"),
          link: `/upyog-ui/employee/ndc/create`,
        }
        // {
        //   label: t("ES_COMMON_APPLICATION_SEARCH"),
        //   link: `/upyog-ui/employee/noc/search`,
        // },
      ],
    }),
    [isInboxLoading, totalCount]
  );

  return Digit.Utils.NDCAccess() ? <EmployeeModuleCard {...propsForModuleCard} /> : null;
};

export default NDCEmployeeHomeCard;
