import { FormComposer, Header } from "@upyog/digit-ui-react-components";
import React, { Fragment, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";
import { config } from "../../../../config/NewEventConfig";

const NewEvents = () => {
  const { t } = useTranslation();
  const history = useHistory();
  
  const onSubmit = (data) => {
    const { fromDate, toDate, fromTime, toTime, address, organizer, fees, geoLocation = {} } = data;
  
    let selectedSubType=Digit.SessionStorage.get("selectedSubType")
    let eventType=Digit.SessionStorage.get("eventType")
    let event=Digit.SessionStorage.get("event")
    let city =Digit.SessionStorage.get("locality")
    console.log("datadata",selectedSubType,eventType,event,city,data)
    const details = {
      events: [
        {
          source: "WEBAPP",
          eventType: "EVENTSONGROUND",
          tenantId: data?.tenantId?.code,
          description: data?.description,
          name: data?.name,
          eventcategory: selectedSubType?.key,
          eventDetails: {
            fromDate: 1721324760000,
            toDate: 1721713560000,
            fromTime:"23:16",
            toTime: "11:16",
            address:"231-G",
            organizer:"MEA",
            fees,
            eventType,
            selectedSubType,
            additionalDetails:{city:city},
            ...geoLocation
          }
        }
      ]
    }
    history.push("/digit-ui/employee/engagement/event/response", details)
  }

  const [mutationHappened, setMutationHappened, clear] = Digit.Hooks.useSessionStorage("EMPLOYEE_EVENT_MUTATION_HAPPENED", false);
  const [errorInfo, setErrorInfo, clearError] = Digit.Hooks.useSessionStorage("EMPLOYEE_EVENT_ERROR_DATA", false);
  const [successData, setsuccessData, clearSuccessData] = Digit.Hooks.useSessionStorage("EMPLOYEE_EVENT_MUTATION_SUCCESS_DATA", false);

  useEffect(() => {
    setMutationHappened(false);
    clearSuccessData();
    clearError();
  }, []);

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