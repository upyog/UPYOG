import { useEffect, useState } from "react";
import { OBPSService } from "../../elements/OBPS";
import { PTService } from "../../elements/PT";
import { useTranslation } from "react-i18next";

const ApplicationUpdateActions = async (applicationData, tenantId) => {
    let PTresponse={}
    if(applicationData?.BPA?.additionalDetails?.propertyAcknowldgementNumber ){
        const PTSearchResponse=applicationData?.BPA?.additionalDetails?.propertyID &&
        (await Digit.PTService.search({ tenantId, filters: { propertyIds: applicationData?.BPA?.additionalDetails?.propertyID } }));
        const {Properties}=PTSearchResponse;
        const PTWorkflow = await Digit.WorkflowService.getByBusinessId(tenantId, applicationData?.BPA?.additionalDetails?.propertyAcknowldgementNumber);
        let BPAWorkflowStatus= sessionStorage.getItem("BPA_WORKFLOW_STATUS");
        let PTAction;
        let action = sessionStorage.getItem("SELECTED_ACTION");
        if(BPAWorkflowStatus==="DOC_VERIFICATION_INPROGRESS"){
            if(action==="FORWARD"){
                PTAction="VERIFY"
            }
            else if(action ==="SEND_BACK_TO_CITIZEN"){
                PTAction="SENDBACKTOCITIZEN"
            }
            else{
                PTAction=action
            }
        }
        else{
            PTAction=action
        }
       
        const PropertyData=Properties[0];
        const updatedWorkflow={
            ...PropertyData.workflow,
            action: PTAction,
            assignes: [],
            businessService: "PT.CREATE",
            comment: "",
            moduleName: "PT",
        }
        const updatedProperty={
            ...PropertyData,
            workflow:updatedWorkflow,
        }
        const transformedData={
            Property:updatedProperty,
          
        }
        let nextActions=PTWorkflow.ProcessInstances[0].nextActions
        for(let i=0; i<nextActions.length; i++){
          if((PTAction===nextActions[i]?.action) && BPAWorkflowStatus!=="NOC_VERIFICATION_INPROGRESS"){
            PTresponse= await PTService.update(transformedData, tenantId)
          }
                
        }           
    }
  try {
    const response = await OBPSService.update(applicationData, tenantId); 
    return {response,PTresponse};
  } catch (error) {
    throw new Error(error?.response?.data?.Errors[0].message);
  }
};

export default ApplicationUpdateActions;