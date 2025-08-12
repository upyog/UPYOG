import React,{useState,useEffect} from "react";
import { useTranslation } from "react-i18next";
import { EmployeeModuleCard, PropertyHouse } from "@nudmcdgnpm/digit-ui-react-components";
import { cndStyles } from "../utils/cndStyles";
/**
 *  Custom component for the Employee card which shows links like - My Requests as well as Inbox
 *  It will check if the user has access using cndUserAccess and accordingly Render the Card in UI
 */

const CNDCard = () => {
  const { t } = useTranslation();
  const [total, setTotal] = useState("-");
  const tenantId = Digit.ULBService.getCitizenCurrentTenant(true) || Digit.ULBService.getCurrentTenantId();
  const { data, isLoading, isFetching, isSuccess } = Digit.Hooks.useNewInboxGeneral({
    tenantId: tenantId,
    ModuleCode: "CND",
    filters: { limit: 10, offset: 0, services: ["cnd"] },
    config: {
      select: (data) => {
        return {totalCount:data?.totalCount,nearingSlaCount:data?.nearingSlaCount} || "-";
      },
      enabled: Digit.Utils.cndUserAccess(),
    },
  });

  useEffect(() => {
        if (!isFetching && isSuccess) setTotal(data);
      }, [isFetching]);

  if (!Digit.Utils.cndUserAccess()) {
    return null;
  }

  const links = [
    {
      count: total?.totalCount,
      label: t("CND_INBOX"),
      link: `/cnd-ui/employee/cnd/inbox`,
    },
    {
      label: t("CND_APPLY"),
      link: `/cnd-ui/employee/cnd/apply`,
    },
    {
      label: t("CND_MY_REQUEST"),
      link: `/cnd-ui/employee/cnd/my-request`,
    },
  ]
  const CND_CEMP = Digit.UserService.hasAccess(["CND_CEMP"]) || false;

  const propsForModuleCard = {
    Icon: <PropertyHouse />,
    moduleName: <div style={cndStyles.moduleCardHeader}>{t("MODULE_CND")}</div>,
    kpis: [{
      count: total?.totalCount,
      label: t("CND_APPLICANT_COUNT"),
      link: `/cnd-ui/employee/cnd/inbox`,
  }],
    links: links.filter(link => !link?.role || CND_CEMP),
  };

  return <EmployeeModuleCard {...propsForModuleCard} />;
};


export default CNDCard;
