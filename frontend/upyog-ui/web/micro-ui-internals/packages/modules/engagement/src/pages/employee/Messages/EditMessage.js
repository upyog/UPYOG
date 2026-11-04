import { FormComposer, Header, Loader } from "@nudmcdgnpm/digit-ui-react-components";
import { format } from 'date-fns';
import React, { Fragment, useMemo } from "react";
import { useTranslation } from "react-i18next";
import { useParams } from "react-router-dom";
import { useQueryClient } from "@tanstack/react-query";
import { handleTodaysDate, isNestedArray, reduceDocsArray } from "../../../utils";
import { config } from "../../../config/NewMessageConfig";

const EditMessage = () => {
  const { t } = useTranslation();
  const navigate = Digit.Hooks.useCustomNavigate();
  const { id: MessageId } = useParams();
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const queryClient = useQueryClient();
  const updateEventMutation = Digit.Hooks.events.useUpdateEvent();
  const { isLoading, data } = Digit.Hooks.events.useInbox(tenantId, {},
    {
      eventTypes: "BROADCAST",
      ids: MessageId
    },
    {
      select: (data) => data?.events?.[0]
    });

  const onSubmit = (formData) => {
    const { fromDate, toDate, description, name, documents } = formData;


    const finalDocuments = isNestedArray(documents) ? reduceDocsArray(documents) : documents;

    const details = {
      events: [
        {
          ...data,
          source: "WEBAPP",
          status: "ACTIVE",
          eventType: "BROADCAST",
          tenantId: formData?.tenantId?.code,
          description,
          name,
          eventDetails: {
            documents: finalDocuments,
            fromDate: handleTodaysDate(`${fromDate}`),
            toDate: handleTodaysDate(`${toDate}`),
          }
        }
      ]
    }
    updateEventMutation.mutate(details, {
      onSuccess: (responseData) => {
        queryClient.clear();
        navigate("/upyog-ui/employee/engagement/messages/response?update=true", { state: { isSuccess: true, data: responseData } });
      },
      onError: (error) => {
        navigate("/upyog-ui/employee/engagement/messages/response?update=true", { state: { isSuccess: false, error } });
      }
    });
  }

  const defaultValues = useMemo(() => {
    const documents = data?.eventDetails?.documents
    return {
      name: data?.name,
      description: data?.description,
      documents: documents?.map(e => [e.fileName, { file: { name: e.fileName, type: e.documentType }, fileStoreId: { fileStoreId: e.fileStoreId, tenantId } }]),
      fromDate: data ? format(new Date(data?.eventDetails?.fromDate), 'yyyy-MM-dd') : null,
      toDate: data ? format(new Date(data?.eventDetails?.toDate), 'yyyy-MM-dd') : null,
    }
  }, [data])

  if (isLoading || updateEventMutation.isPending) {
    return (
      <Loader />
    );
  }

  return (
    <Fragment>
      <Header>{t("EDIT_NEW_PUBLIC_MESSAGE")}</Header>
      <FormComposer
        defaultValues={defaultValues}
        config={config}
        onSubmit={onSubmit}
        label={t("ACTION_EDIT_MESSAGE")}
      >
      </FormComposer>
    </Fragment>
  )
}

export default EditMessage;