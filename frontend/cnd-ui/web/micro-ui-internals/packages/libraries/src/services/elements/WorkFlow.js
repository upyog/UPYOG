import Urls from "../atoms/urls";
import { Request } from "../atoms/Utils/Request";
import cloneDeep from "lodash/cloneDeep";

const getThumbnails = async (ids, tenantId, documents = []) => {
  tenantId = window.location.href.includes("/obps/") || window.location.href.includes("/pt/") ? Digit.ULBService.getStateId() : tenantId;
  
  if (window.location.href.includes("/obps/")) {
    if (documents?.length > 0) {
      let workflowsDocs = [];
      documents?.map(doc => {
        if (doc?.url) {
          const thumbs = doc?.url?.split(",")?.[3] || doc?.url?.split(",")?.[0]
          workflowsDocs.push({
            thumbs: [thumbs],
            images: [Digit.Utils.getFileUrl(doc.url)]
          }) 
        }
      })
      return workflowsDocs?.[0];
    } else {
      return null;
    }
  } else {
    const res = await Digit.UploadServices.Filefetch(ids, tenantId);
    if (res.data.fileStoreIds && res.data.fileStoreIds.length !== 0) {
      return { 
        thumbs: res.data.fileStoreIds.map((o) => o.url.split(",")[3] || o.url.split(",")[0]), 
        images: res.data.fileStoreIds.map((o) => Digit.Utils.getFileUrl(o.url)) };
    } else {
      return null;
    }
  }
};

const makeCommentsSubsidariesOfPreviousActions = async (wf) => {
  const TimelineMap = new Map();
  
  for (const eventHappened of wf) {
    if (eventHappened?.documents) {
      eventHappened.thumbnailsToShow = await getThumbnails(eventHappened?.documents?.map(e => e?.fileStoreId), eventHappened?.tenantId, eventHappened?.documents)
    }
    if (eventHappened.action === "COMMENT") {
      const commentAccumulator = TimelineMap.get("tlCommentStack") || []
      TimelineMap.set("tlCommentStack", [...commentAccumulator, eventHappened])
    }
    else {
      const eventAccumulator = TimelineMap.get("tlActions") || []
      const commentAccumulator = TimelineMap.get("tlCommentStack") || []
      eventHappened.wfComments = [...commentAccumulator, ...eventHappened.comment ? [eventHappened] : []]
      TimelineMap.set("tlActions", [...eventAccumulator, eventHappened])
      TimelineMap.delete("tlCommentStack")
    }
  }
  const response = TimelineMap.get("tlActions")
  return response
}

const getAssignerDetails = (instance, nextStep, moduleCode) => {
  let assigner = instance?.assigner
  
  return assigner
}

export const WorkflowService = {
  init: (stateCode, businessServices) => {
    return Request({
      url: `${Urls.WorkFlow}`,
      useCache: true,
      method: "POST",
      params: { tenantId: stateCode, businessServices },
      auth: true,
    });
  },

  getByBusinessId: (stateCode, businessIds, params = {}, history = true) => {
    return Request({
      url: Urls.WorkFlowProcessSearch,
      useCache: false,
      method: "POST",
      params: { tenantId: stateCode, businessIds: businessIds, ...params, history },
      auth: true,
    });
  },

  getDetailsById: async ({ tenantId, id, moduleCode, role, getTripData }) => {
    const workflow = await Digit.WorkflowService.getByBusinessId(tenantId, id); 
    const applicationProcessInstance = cloneDeep(workflow?.ProcessInstances);
    const moduleCodeData =  moduleCode;
    const businessServiceResponse = (await Digit.WorkflowService.init(tenantId, moduleCodeData))?.BusinessServices[0]?.states;
    if (workflow && workflow.ProcessInstances) {
      const processInstances = workflow.ProcessInstances;
      const nextStates = processInstances[0]?.nextActions.map((action) => ({ action: action?.action, nextState: processInstances[0]?.state.uuid }));
      const nextActions = nextStates.map((id) => ({
        action: id.action,
        state: businessServiceResponse?.find((state) => state.uuid === id.nextState),
      }));
      /* To check state is updatable and provide edit option*/
      const currentState = businessServiceResponse?.find((state) => state.uuid === processInstances[0]?.state.uuid);
      if (currentState && currentState?.isStateUpdatable) {
        nextActions.push({ action: "EDIT", state: currentState });
      }

      const getStateForUUID = (uuid) => businessServiceResponse?.find((state) => state.uuid === uuid);

      const actionState = businessServiceResponse
        ?.filter((state) => state.uuid === processInstances[0]?.state.uuid)
        .map((state) => {
          let _nextActions = state.actions?.map?.((ac) => {
            let actionResultantState = getStateForUUID(ac.nextState);
            let assignees = actionResultantState?.actions?.reduce?.((acc, act) => {
              return [...acc, ...act.roles];
            }, []);
            return { ...actionResultantState, assigneeRoles: assignees, action: ac.action, roles: ac.roles };
          });
          return { ...state, nextActions: _nextActions, roles: state?.action, roles: state?.actions?.reduce((acc, el) => [...acc, ...el.roles], []) };
        })?.[0];

      if (processInstances.length > 0) {
        const TLEnrichedWithWorflowData = await makeCommentsSubsidariesOfPreviousActions(processInstances)
        let timeline = TLEnrichedWithWorflowData.map((instance, ind) => {
          let checkPoint = {
            performedAction: instance.action,
            status: instance.state.applicationStatus,
            state: instance.state.state,
            assigner: getAssignerDetails(instance, TLEnrichedWithWorflowData[ind - 1], moduleCode),
            rating: instance?.rating,
            wfComment: instance?.wfComments.map(e => e?.comment),
            wfDocuments: instance?.documents,
            thumbnailsToShow: { thumbs: instance?.thumbnailsToShow?.thumbs, fullImage: instance?.thumbnailsToShow?.images },
            assignes: instance.assignes,
            caption: instance.assignes ? instance.assignes.map((assignee) => ({ name: assignee.name, mobileNumber: assignee.mobileNumber })) : null,
            auditDetails: {
              created: Digit.DateUtils.ConvertEpochToDate(instance.auditDetails.createdTime),
              lastModified: Digit.DateUtils.ConvertEpochToDate(instance.auditDetails.lastModifiedTime),
            },
            timeLineActions: instance.nextActions
              ? instance.nextActions.filter((action) => action.roles.includes(role)).map((action) => action?.action)
              : null,
          };
          return checkPoint;
        });

      //Added the condition so that following filter can happen only for fsm and does not affect other module
      let nextStep = [];
      

        const details = {
          timeline,
          nextActions : nextActions,
          actionState,
          applicationBusinessService: workflow?.ProcessInstances?.[0]?.businessService,
          processInstances: applicationProcessInstance,
        };
        return details;
      }
    } else {
      throw new Error("error fetching workflow services");
    }
    return {};
  },

  getAllApplication: (tenantId, filters) => {
    return Request({
      url: Urls.WorkFlowProcessSearch,
      useCache: false,
      method: "POST",
      params: { tenantId, ...filters },
      auth: true,
    });
  },
};
