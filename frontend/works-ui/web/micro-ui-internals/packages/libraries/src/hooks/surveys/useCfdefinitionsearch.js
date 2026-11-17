import { Surveys } from "../../services/elements/Surveys";
import { queryTemplate } from "../../common/queryTemplate";

const useCfdefinitionsearch = (filters, config) => {
  return queryTemplate({ queryKey: ["search_surveys", filters.Pagination, filters.ServiceDefinitionCriteria], queryFn: () => Surveys.cfdefinitionsearch(filters), config });
};

export default useCfdefinitionsearch;
