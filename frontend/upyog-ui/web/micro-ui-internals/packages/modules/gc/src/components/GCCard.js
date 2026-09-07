import React, {useState, useEffect} from "react";
import { useTranslation } from "react-i18next";
import { EmployeeModuleCard, PTIcon } from "@nudmcdgnpm/digit-ui-react-components";

/**
 * GCCard Component
 * 
 * Renders the employee module card for Garbage Collection (GC) on the employee dashboard.
 * Displays the total inbox count as a KPI and provides quick links to Inbox, Search, 
 * New Application, and Bills pages.
 * 
 * Fetches the inbox count using the `useNewInboxGeneral` hook and updates the display
 * after the data is successfully fetched.
 */
const GCCard = () => {
  const { t } = useTranslation();
  const tenantId = Digit.ULBService.getCurrentTenantId();

  const [total, setTotal] = useState("-");
  const { data, isFetching, isSuccess } = Digit.Hooks.useNewInboxGeneral({
    tenantId: tenantId,
    ModuleCode: "GC",
    filters: { limit: 10, offset: 0, services: ["garbage-service"] },

    config: {
      select: (data) => {
        return {totalCount:data?.totalCount,nearingSlaCount:data?.nearingSlaCount} || "-";
      },
      enabled: Digit.Utils.gcAccess(),
    },
  });

  useEffect(() => {
    if (!isFetching && isSuccess) setTotal(data);
  }, [isFetching]);

  if (!Digit.Utils.gcAccess()) {
    return null;
  }

  const propsForModuleCard = {

    Icon: <PTIcon />,
    moduleName: t("GC_MODULE_NAME"),
    kpis: [
      {
        count: total?.totalCount,
        label: t("ES_TITLE_INBOX"),
        link: `/upyog-ui/employee/gc/inbox`,
      }
    ],
    links: [
      { label: t("GC_INBOX"), link: `/upyog-ui/employee/gc/inbox` },
      { label: t("GC_SEARCH_APPLICATION"), link: `/upyog-ui/employee/gc/search` },
    ],
  };

  return <EmployeeModuleCard {...propsForModuleCard} />;
};

export default GCCard;
