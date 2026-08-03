import { Surveys } from "../../services/elements/Surveys";
import { mutationTemplate } from "../../common/mutationTemplate";

const useSubmitResponse = (filters, config) => {
  return mutationTemplate({ mutationFn: (filters) => Surveys.submitResponse(filters) });
};

export default useSubmitResponse;
