import { FormComposer, Header, Loader } from "@nudmcdgnpm/digit-ui-react-components";
import { format } from 'date-fns';
import React, { Fragment } from "react";
import { useTranslation } from "react-i18next";
import { useParams } from "react-router-dom";
import { useQueryClient } from "@tanstack/react-query";
import { config } from "../../../../config/NewEventConfig";

const EditEvents = () => {
  const { t } = useTranslation();
  const navigate = Digit.Hooks.useCustomNavigate();
  const { id: EventId } = useParams();
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const queryClient = useQueryClient();
  const updateEventMutation = Digit.Hooks.events.useUpdateEvent();
  const { isLoading, data } = Digit.Hooks.events.useInbox(tenantId, {},
    {
      eventTypes: "EVENTSONGROUND",
      ids: EventId
    },
    {
      select: (data) => data?.events?.[0]
    });

  const onSubmit = (formData) => {
    const { fromDate, toDate, fromTime, toTime, address, organizer, fees, geoLocation } = formData;
    const details = {
      events: [
        {
          ...data,
          source: "WEBAPP",
          status: "ACTIVE",
          eventType: "EVENTSONGROUND",
          tenantId: formData?.tenantId?.code,
          description: formData?.description,
          name: formData?.name,
          eventcategory: formData?.eventCategory?.code,
          eventDetails: {
            ...data?.eventDetails,
            fromDate: new Date(`${fromDate} ${fromTime}`).getTime(),
            toDate: new Date(`${toDate} ${toTime}`).getTime(),
            fromTime,
            toTime,
            address,
            organizer,
            fees,
            latitude: geoLocation?.latitude,
            longitude: geoLocation?.longitude
          }
        }
      ]
    }
    updateEventMutation.mutate(details, {
      onSuccess: (responseData) => {
        queryClient.clear();
        navigate("/upyog-ui/employee/engagement/event/response?update=true", { state: { isSuccess: true, data: responseData } });
      },
      onError: (error) => {
        navigate("/upyog-ui/employee/engagement/event/response?update=true", { state: { isSuccess: false, error } });
      }
    });
  }

  if (isLoading || updateEventMutation.isPending) {
    return (
      <Loader />
    );
  }

  const defaultValues = {
    defaultTenantId: data?.tenantId,
    name: data?.name,
    fromDate: format(new Date(data?.eventDetails?.fromDate), 'yyyy-MM-dd'),
    toDate: format(new Date(data?.eventDetails?.toDate), 'yyyy-MM-dd'),
    organizer: data?.eventDetails?.organizer,
    fees: data?.eventDetails?.fees,
    description: data?.description,
    address: data?.eventDetails?.address,
    category: data?.eventCategory,
    fromTime: format(new Date(data?.eventDetails?.fromDate), 'HH:mm'),
    toTime: format(new Date(data?.eventDetails?.toDate), 'HH:mm'),
    geoLocation: {
      latitude: data?.eventDetails?.latitude,
      longitude: data?.eventDetails?.longitude,
    }
  }

  return (
    <Fragment>
      <Header>{t("ES_TITLE_EDIT_EVENTS")}</Header>
      <FormComposer
        defaultValues={defaultValues}
        config={config}
        onSubmit={onSubmit}
        label={t("EVENTS_SAVE_CHANGES")}
      >
      </FormComposer>
    </Fragment>
  )
}

export default EditEvents;