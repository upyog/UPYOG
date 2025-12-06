import React, { useState } from "react";
import { Loader, NotificationComponent, TreatmentQualityIcon } from "@egovernments/digit-ui-react-components";

function TQMPendingTask(props) {
  const tenantId = Digit.ULBService.getCurrentTenantId();
  
  const requestCriteria = {
    url: "/inbox/v2/_search",
    body: {
      inbox: {
        tenantId,
        processSearchCriteria: {
          businessService: ["PQM"],
          moduleName: "pqm",
          tenantId,
        },
        moduleSearchCriteria: {
          tenantId,
          sortBy: "createdTime",
          sortOrder: "DESC",
        },
        limit: 100,
        offset: 0,
      },
    },
    config: {
      select: (data) => {
        if(Digit.Utils.tqm.isUserNotLinkedToPlant()){
          return []
        }
        const items = data?.items;

        const tasks = items.map((i) => {
          const currentDate = new Date();
          const targetTimestamp = i?.businessObject?.scheduledDate;
          const targetDate = new Date(targetTimestamp);
          const remainingSLA = targetDate - currentDate;
          const dueDate = targetTimestamp ? Math.round(remainingSLA / (24 * 60 * 60 * 1000)) : 0;
          return {
            icon: <TreatmentQualityIcon />,
            id: i?.ProcessInstance?.businessId,
            title: `ES_ACTION_MESSAGE_${i?.ProcessInstance?.state?.actions?.[0]?.action}`,
            action: i?.ProcessInstance?.state?.actions?.[0]?.action,
            date: dueDate,
          };
        });
        return {tasks,totalCount:data?.totalCount};
      },
    },
  };

  const activePlantCode = Digit.SessionStorage.get("active_plant")?.plantCode ? [Digit.SessionStorage.get("active_plant")?.plantCode]:Digit.SessionStorage.get("user_plants")?.filter(row => row.plantCode)?.map(row => row.plantCode)

  if(activePlantCode?.length>0){
    requestCriteria.body.inbox.moduleSearchCriteria.plantCodes = [...activePlantCode]
  }

  const { isLoading, data: tqm, revalidate, isFetching } = Digit.Hooks.useCustomAPIHook(requestCriteria);

  if (isLoading) return <Loader />;

  return (
    !isLoading &&
    tqm && (
      <NotificationComponent
        heading="Pending Tasks"
        data={tqm?.tasks}
        // viewAllRoute={`/${window?.contextPath}/employee/tqm/inbox`}
        actionRoute={`/${window?.contextPath}/employee/tqm/test-details`}
        linkObj={{ pathname:`/${window?.contextPath}/employee/tqm/inbox`, state: {count:isLoading ? '-' : tqm?.totalCount} }}
      />
    )
  );
}

export default TQMPendingTask;
