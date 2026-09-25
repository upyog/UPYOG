import { Card, CardCaption, Header, Loader, OnGroundEventCard } from "@nudmcdgnpm/upyog-ui-react-components-lts";
import React from "react";
import { useTranslation } from "react-i18next";
import { useLocation } from "react-router-dom";

const EventsListOnGround = ({ variant, parentRoute }) => {
  const { t } = useTranslation();
  const location = useLocation();
  const navigate = Digit.Hooks.useCustomNavigate();

  const tenantId = Digit.ULBService.getCitizenCurrentTenant();
  const { data: { unreadCount: preVisitUnseenEventsCount } = {}, isSuccess: preVisitUnseenEventsCountLoaded } = Digit.Hooks.useNotificationCount({
    tenantId,
    config: {
      enabled: !!Digit.UserService?.getUser()?.access_token,
    },
  });

  const { data: EventsData, isLoading: EventsDataLoading } = Digit.Hooks.useEvents({ tenantId, variant });

   if (!Digit.UserService?.getUser()?.access_token) {
      localStorage.clear();
      sessionStorage.clear();
      navigate("/sv-ui/citizen/login", { state: { from: location.pathname + location.search }, replace: true });
      return null;
    }


  if (EventsDataLoading || !preVisitUnseenEventsCountLoaded) return <Loader />;

  function onEventCardClick(id) {
    navigate(parentRoute + "/events/details/" + id);
  }

  return (
    <div className="CitizenEngagementNotificationWrapper">
      <Header>{`${t("EVENTS_EVENTS_HEADER")}(${EventsData?.length})`}</Header>
      {/* The key prop was missing; it was optional in React 17 (warning only), but is required for proper list rendering in React 19 */}
      {EventsData.length ? (
        EventsData.map((DataParamsInEvent, index) => (
          <OnGroundEventCard 
            key={DataParamsInEvent.uuid || DataParamsInEvent.id || index} 
            onClick={onEventCardClick} 
            {...DataParamsInEvent} 
          />
        ))
      ) : (
        <Card>
          <CardCaption>{t("COMMON_INBOX_NO_DATA")}</CardCaption>
        </Card>
      )}
    </div>
  );
};

export default EventsListOnGround;
