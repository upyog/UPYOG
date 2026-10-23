import { Surveys } from "../../services/elements/Surveys";
import { mutationTemplate } from "../../common/mutationTemplate";

const useSubmitSurveyResponse = (filters, config) => {
  return mutationTemplate({ mutationFn: (filters) => Surveys.submitSurveyResponse(filters) });
};

export default useSubmitSurveyResponse;
