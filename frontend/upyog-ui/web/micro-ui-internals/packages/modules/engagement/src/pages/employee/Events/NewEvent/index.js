import { FormComposer, Header, Loader } from "@nudmcdgnpm/digit-ui-react-components";
import React, { Fragment } from "react";
import { useTranslation } from "react-i18next";
import { useQueryClient } from "@tanstack/react-query";
import { config } from "../../../../config/NewEventConfig";

const NewEvents = () => {
  const { t } = useTranslation();
  const navigate = Digit.Hooks.useCustomNavigate();
  const queryClient = useQueryClient();
  const mutation = Digit.Hooks.events.useCreateEvent();

  const onSubmit = (data) => {
    const { fromDate, toDate, fromTime, toTime, address, organizer, fees, geoLocation = {} } = data;
    const details = {
      events: [
        {
          source: "WEBAPP",
          eventType: "EVENTSONGROUND",
          tenantId: data?.tenantId?.code,
          description: data?.description,
          name: data?.name,
          eventcategory: data?.eventCategory?.code,
          eventDetails: {
            fromDate: new Date(`${fromDate} ${fromTime}`).getTime(),
            toDate: new Date(`${toDate} ${toTime}`).getTime(),
            fromTime,
            toTime,
            address,
            organizer,
            fees,
            ...geoLocation
          }
        }
      ]
    }
    mutation.mutate(details, {
      onSuccess: (responseData) => {
        queryClient.clear();
        navigate("/upyog-ui/employee/engagement/event/response", { state: { isSuccess: true, data: responseData } });
      },
      onError: (error) => {
        navigate("/upyog-ui/employee/engagement/event/response", { state: { isSuccess: false, error } });
      }
    });
  }

  if (mutation.isPending) {
    return <Loader />;
  }

  return (
    <Fragment>
      <Header>{t("ES_TITLE_NEW_EVENTS")}</Header>
      <FormComposer
        config={config}
        onSubmit={onSubmit}
        label={t("EVENTS_CREATE_EVENT")}
      >
      </FormComposer>
    </Fragment>
  )
}

export default NewEvents;