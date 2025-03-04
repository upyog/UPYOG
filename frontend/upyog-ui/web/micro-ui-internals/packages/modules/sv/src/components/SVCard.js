import React,{useState,useEffect} from "react";
import { useTranslation } from "react-i18next";
import { EmployeeModuleCard, PropertyHouse } from "@upyog/digit-ui-react-components";

// custom component for the employee card
const SVCard = () => {
  const { t } = useTranslation();
  /**
   * Thiscode calls the inbox general using tenantId ,modulecode and filters on that we get the total cunt and SLA count
   * once success it is setting that data in total variable and we are showing inside count for inbox
   */
  const [total, setTotal] = useState("0");
  const { data, isLoading, isFetching, isSuccess } = Digit.Hooks.useNewInboxGeneral({
    tenantId: Digit.ULBService.getCurrentTenantId(),
    ModuleCode: "SV",
    filters: { limit: 10, offset: 0, services: ["street-vending"] },

    config: {
      select: (data) => {
        return {totalCount:data?.totalCount,nearingSlaCount:data?.nearingSlaCount} || "-";
      },
      enabled: Digit.Utils.svAccess(),
    },
  });

  //this useeffect is dependent on isFetching so when API gets all the data and isSuccess is set to true only then it will set the Data in setTotal
  useEffect(() => {
    if (!isFetching && isSuccess) setTotal(data);
  }, [isFetching]);

  if (!Digit.Utils.svAccess()) {
    return null;
  }

  const links = [
    {
      count: isLoading ? "-" : total?.totalCount,
      label: t("SV_INBOX"),
      link: `/upyog-ui/employee/sv/inbox`,
    },
    {
      label: t("SV_CREATE_APPLICATION"),
      link: `/upyog-ui/employee/sv/apply`,
    },
    {
      label: t("ES_COMMON_APPLICATION_SEARCH"),
      link: `/upyog-ui/employee/sv/my-applications`,
    },
  ]
  const SV_CEMP = Digit.UserService.hasAccess(["SV_VENDOR"]) || false;

  const propsForModuleCard = {
    Icon: <PropertyHouse />,
    moduleName: <div style={{ width: "200px", wordWrap: "break-word" }}>{t("MODULE_SV")}</div>,
    kpis: [{
      count: total?.totalCount,
      label: t("ES_TITLE_INBOX"),
      link: `/upyog-ui/employee/sv/inbox`,
  }],
    links: links.filter(link => !link?.role || SV_CEMP),
  };

  return <EmployeeModuleCard {...propsForModuleCard} />;
};


export default SVCard;
