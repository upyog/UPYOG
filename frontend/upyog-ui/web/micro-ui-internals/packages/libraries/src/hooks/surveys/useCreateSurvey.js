import { Surveys } from "../../services/elements/Surveys";
import { mutationTemplate } from "../../common/mutationTemplate";

const useCreateSurveysDef = (filters, config) => {

  return mutationTemplate({ mutationFn: (filters) => Surveys.createSurvey(filters) });
};

export default useCreateSurveysDef;
