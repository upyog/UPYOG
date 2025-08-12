import { Surveys } from "../../services/elements/Surveys";
import { useMutation } from "react-query";

const useSubmitSurveyResponse = (filters, config) => {
    return useMutation((filters) => Surveys.submitSurveyResponse(filters));
};

export default useSubmitSurveyResponse;
