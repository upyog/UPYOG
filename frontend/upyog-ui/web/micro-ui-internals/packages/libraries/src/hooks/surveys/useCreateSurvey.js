import { Surveys } from "../../services/elements/Surveys";
import { useMutation } from "react-query";

const useCreateSurveysDef = (filters, config) => {
  console.log("useCreateSurveysDef");

  return useMutation((filters) => Surveys.createSurvey(filters));
};

export default useCreateSurveysDef;
