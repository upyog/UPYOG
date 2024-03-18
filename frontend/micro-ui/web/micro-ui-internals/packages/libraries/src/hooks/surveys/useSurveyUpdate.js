import { Surveys } from "../../services/elements/Surveys";
import { useMutation } from "react-query";

const useUpdateSurvey = (filters, config) => {
  console.log("updateSurvey")
  return useMutation((filters) => Surveys.updateSurvey(filters));
};

export default useUpdateSurvey;
