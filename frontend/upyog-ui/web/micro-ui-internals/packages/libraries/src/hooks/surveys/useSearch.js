import { Surveys } from "../../services/elements/Surveys";
import { queryTemplate } from "../../common/queryTemplate";

const useSearch = (filters, config) => {
  return queryTemplate({ queryKey: ["search_surveys", filters?.uuid, filters?.title, filters?.tenantIds, filters?.postedBy, filters?.offset, filters?.limit], queryFn: () => Surveys.search(filters), config });
};

export default useSearch;
