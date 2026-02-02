import { PTService } from "../../elements/PT";

const AssessmentUpdateActions = async (applicationData, tenantId) => {
  try {
    const response = await PTService.assessmentUpdate(applicationData, tenantId);
    return response;
  } catch (error) {
    throw new Error(error?.response?.data?.Errors[0].message);
  }
};

export default AssessmentUpdateActions;
