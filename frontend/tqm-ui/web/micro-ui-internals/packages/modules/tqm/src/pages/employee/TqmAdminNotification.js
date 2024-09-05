import { Loader, Notification } from "@egovernments/digit-ui-react-components";
import React from "react";
import { useTranslation } from "react-i18next";

const TqmAdminNotification = () => {
  const { t } = useTranslation();
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const userInfo = Digit.UserService.getUser();
  const userRoles = userInfo.info.roles.map((roleData) => roleData.code);
  
  if(!userRoles.includes("PQM_ADMIN")){
    return null
  }

  const requestCriteria = {
    url: "/egov-user-event/v1/events/_search",
    params: {tenantId,recepients:userInfo?.info?.uuid},
    body: {},
    config:{
      enabled: userRoles.includes("PQM_ADMIN") ? true : false
    }
  };

  const { isLoading, data} = Digit.Hooks.useCustomAPIHook(requestCriteria);

  const formatTimeAgo = (timestamp) => {
    const currentTime = Date.now();
    const timeDifference = currentTime - timestamp;
    const daysAgo = Math.floor(timeDifference / (1000 * 60 * 60 * 24));
    const monthsAgo = Math.floor(daysAgo / 30);

    if (monthsAgo >= 1) {
      return monthsAgo === 1 ? t("ES_TQM_MONTH_AGO") : t(`ES_TQM_MONTHS_AGO`, { NO_OF_MONTH: monthsAgo});
    } else {
      return daysAgo === 1 ? t("ES_TQM_DAY_AGO") : t(`ES_TQM_DAYS_AGO`, { NO_OF_DAY: daysAgo});
    }
  };

  if(isLoading){
    return <Loader />
  }

  const mappedActions = data.events.map(event => {
    return {
      header: event?.eventCategory || t("ES_DEFAULT_NOTIFICATION"),  
      eventNotificationText: event?.description,  
      actionUrl: event?.actions?.actionUrls?.[0].actionUrl, 
      code: event?.actions?.actionUrls?.[0].code,  
      timePastAfterEventCreation: formatTimeAgo(event?.auditDetails?.createdTime),

    };
  });

  return (
    <div>
      <Notification actions={mappedActions} />
    </div>
  );
};

export default TqmAdminNotification;
