import { FormComposer, Header, Loader } from "@nudmcdgnpm/digit-ui-react-components";
import React, { Fragment } from "react";
import { useTranslation } from "react-i18next";
import { useQueryClient } from "@tanstack/react-query";
import { convertDateToMaximumPossibleValue } from "../../../../utils";
import { config } from "../../../../config/NewMessageConfig";



const NewEvents = () => {
  const { t } = useTranslation();
  const navigate = Digit.Hooks.useCustomNavigate();
  const queryClient = useQueryClient();
  const mutation = Digit.Hooks.events.useCreateEvent();

  const onSubmit = (data) => {
    const { fromDate, toDate, description, name, documents } = data;
    const details = {
      events: [
        {
          recepient: null,
          source: "WEBAPP",
          eventType: "BROADCAST",
          tenantId: data?.tenantId?.code,
          description,
          name,
          eventDetails: {
            documents,
            fromDate: convertDateToMaximumPossibleValue(new Date(`${fromDate}`))?.getTime(),
            toDate: convertDateToMaximumPossibleValue(new Date(`${toDate}`))?.getTime(),
          }
        }
      ]
    }
    mutation.mutate(details, {
      onSuccess: (responseData) => {
        queryClient.clear();
        navigate("/upyog-ui/employee/engagement/messages/response", { state: { isSuccess: true, data: responseData } });
      },
      onError: (error) => {
        navigate("/upyog-ui/employee/engagement/messages/response", { state: { isSuccess: false, error } });
      }
    });
  }

  if (mutation.isPending) {
    return <Loader />;
  }

  return (
    <Fragment>
      <Header>{t("ADD_NEW_PUBLIC_MESSAGE")}</Header>
      <FormComposer
        config={config}
        onSubmit={onSubmit}
        label={t("ACTION_ADD_NEW_MESSAGE")}
      >
      </FormComposer>
    </Fragment>
  )
}

export default NewEvents;