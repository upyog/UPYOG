import { Surveys } from "../../services/elements/Surveys";
import { useQuery } from "react-query";

const useCfdefinitionsearch = (filters, config) => {
  return useQuery([`search_surveys`,filters.Pagination,filters.ServiceDefinitionCriteria], () => Surveys.cfdefinitionsearch(filters), { ...config });
};

export default useCfdefinitionsearch;
