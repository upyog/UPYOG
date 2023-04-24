import { PTService } from "../../elements/PT";

const ApplicationUpdateActions = async (applicationData, tenantId) => {
  try {
    applicationData.Property.workflow["comment"]=applicationData.Property.workflow["comments"]
    delete applicationData.Property.workflow["comments"]
    console.log("ApplicationUpdateActions",applicationData)
    const response = await PTService.update(applicationData, tenantId);
    return response;
  } catch (error) {
    throw new Error(error?.response?.data?.Errors[0].message);
  }
};

export default ApplicationUpdateActions;
