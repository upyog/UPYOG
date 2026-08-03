import { Surveys } from "../../services/elements/Surveys";
import { queryTemplate } from "../../common/queryTemplate";

const useSelectedSurveySearch = (filters, config) => {

  return queryTemplate({ queryKey: [`search_selected_survey_${new Date()}`], queryFn: () => Surveys.selectedSurveySearch(filters), config });
};

export default useSelectedSurveySearch;
