import { Surveys } from "../../services/elements/Surveys";
import { mutationTemplate } from "../../common/mutationTemplate";

const useDeleteSurveys = (filters, config) => {
  return mutationTemplate({ mutationFn: (filters) => Surveys.delete(filters) });
};

export default useDeleteSurveys;
