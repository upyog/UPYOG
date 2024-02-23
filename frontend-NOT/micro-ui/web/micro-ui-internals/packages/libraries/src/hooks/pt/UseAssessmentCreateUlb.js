import { PTService } from "../../services/elements/PT";
import { useMutation } from "react-query";

const UseAssessmentCreateUlb = (tenantId, config = {}) => {
  console.log("Using AssessmentCreateUlb",tenantId)
  return useMutation((data) => PTService.assessmentCreateUlb(data, tenantId));
};

export default UseAssessmentCreateUlb;
