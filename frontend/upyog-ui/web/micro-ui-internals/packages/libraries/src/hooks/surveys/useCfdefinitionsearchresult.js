import { Surveys } from "../../services/elements/Surveys";
import { queryTemplate } from "../../common/queryTemplate";

const useCfdefinitionsearchresult = (filters, config) => {
  return queryTemplate({ queryKey: [`useCfdefinitionsearchresult_search_surveys_${new Date()}`], queryFn: () => Surveys.cfdefinitionsearch(filters), config });
};

export default useCfdefinitionsearchresult;
