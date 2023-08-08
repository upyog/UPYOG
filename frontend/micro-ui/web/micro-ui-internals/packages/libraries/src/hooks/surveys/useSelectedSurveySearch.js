import { Surveys } from "../../services/elements/Surveys";
import { useQuery } from "react-query";

const useSelectedSurveySearch = (filters, config) => {
    console.log(config,"useSelectedSurveySearch")
  return useQuery(`search_selected_survey_${new Date()}`, () => Surveys.selectedSurveySearch(filters), { ...config });
};

export default useSelectedSurveySearch;
