import { Card, CardCaption, Header, Loader, OnGroundEventCard, WhatsNewCard } from "@nudmcdgnpm/upyog-ui-react-components-lts";
import React, { useEffect } from "react";
import { useTranslation } from "react-i18next";
import { Navigate, useLocation } from "react-router-dom";
import BroadcastWhatsNewCard from "../../components/Messages/BroadcastWhatsNewCard";

const NotificationsAndWhatsNew = ({ variant, parentRoute }) => {
  const { t } = useTranslation();
  const location = useLocation();
  const navigate = Digit.Hooks.useCustomNavigate();

  const tenantId = Digit.ULBService.getCitizenCurrentTenant();
  const {
    data: { unreadCount: preVisitUnseenNotificationCount } = {},
    isSuccess: preVisitUnseenNotificationCountLoaded,
    refetch,
  } = Digit.Hooks.useNotificationCount({
    tenantId,
    config: {
      enabled: !!Digit.UserService?.getUser()?.access_token,
    },
  });

  const { mutate, isSuccess } = Digit.Hooks.useClearNotifications();

  // CORRECT Way to refetch on isSuccess change
    useEffect(() => {
      if (isSuccess) {
        refetch();
      }
    }, [isSuccess]);

// In useEffect, only return a cleanup function or nothing. Returning null or other values will cause errors in React 19.
useEffect(() => {
  if (preVisitUnseenNotificationCount && tenantId) {
    mutate({ tenantId });
  }
}, [tenantId, preVisitUnseenNotificationCount]);


  const { data: EventsData, isLoading: EventsDataLoading } = Digit.Hooks.useEvents({ tenantId, variant });

  // if (!Digit.UserService?.getUser()?.access_token) {
  //     return <Redirect to={{ pathname: `/sv-ui/citizen/login`, state: { from: location.pathname + location.search } }} />
  // }

  if (EventsDataLoading) return <Loader />;

  if (EventsData?.length === 0) {
    return (
      <div className="CitizenEngagementNotificationWrapper">
        <Header>{`${t("CS_HEADER_NOTIFICATIONS")}`}</Header>
        <h1>Nothing to show</h1>
      </div>
    );
  }

  const VariantWiseRender = () => {
    switch (variant) {
      case "notifications":
        return <Header>{`${t("CS_HEADER_NOTIFICATIONS")} ${preVisitUnseenNotificationCount ? `(${preVisitUnseenNotificationCount})` : ""}`}</Header>;

      case "whats-new":
        return <Header>{t("CS_HEADER_WHATSNEW")}</Header>;

      default:
     //old:   return <Redirect to={{ pathname: `/sv-ui/citizen`, state: { from: location.pathname + location.search } }} />;
     return <Navigate to="/sv-ui/citizen" state={{ from: location.pathname + location.search }} replace />;

    }
  };

  function onEventCardClick(id) {
    navigate(parentRoute + "/events/details/" + id);
  }

  return (
    <div className="CitizenEngagementNotificationWrapper">
      <VariantWiseRender />
      {/* The key prop was missing; it was optional in React 17 (warning only), but is required for proper list rendering in React 19 */}
      {EventsData?.length ? (
        EventsData.map((DataParamsInEvent, index) => {
          const key = DataParamsInEvent.uuid || DataParamsInEvent.id || index;
          if (DataParamsInEvent?.eventType === "EVENTSONGROUND") {
            return (
              <OnGroundEventCard 
                key={key} 
                onClick={onEventCardClick} 
                {...DataParamsInEvent} 
              />
            );
          } else if (DataParamsInEvent?.eventType === "BROADCAST") {
            return <BroadcastWhatsNewCard key={key} {...DataParamsInEvent} />;
          } else {
            return <WhatsNewCard key={key} {...DataParamsInEvent} />;
          }
        })
      ) : (
        <Card>
          <CardCaption>{t("COMMON_INBOX_NO_DATA")}</CardCaption>
        </Card>
      )}
    </div>
  );
};

export default NotificationsAndWhatsNew;
