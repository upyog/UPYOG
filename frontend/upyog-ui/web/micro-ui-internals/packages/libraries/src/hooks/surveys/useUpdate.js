import { Surveys } from "../../services/elements/Surveys";
import { mutationTemplate } from "../../common/mutationTemplate";

const useUpdateSurvey = (filters, config) => {
  return mutationTemplate({ mutationFn: (filters) => Surveys.update(filters) });
};

export default useUpdateSurvey;
