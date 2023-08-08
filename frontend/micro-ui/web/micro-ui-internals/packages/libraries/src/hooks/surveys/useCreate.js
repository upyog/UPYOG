import { Surveys } from "../../services/elements/Surveys";
import { useMutation } from "react-query";

const useCreateSurveys = (filters, config) => {
  console.log("useCreateSurveys");
  return useMutation((filters) => Surveys.create(filters));
};

export default useCreateSurveys;
