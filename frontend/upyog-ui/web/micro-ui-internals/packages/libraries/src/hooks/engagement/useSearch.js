import { queryTemplate } from "../../common/queryTemplate";
import { Engagement } from "../../services/elements/Engagement";

/**
 * Search engagement data.
 */
const useSearch = (filters, config = {}) => {
  const queryKey = [
    "ENGAGEMENT_SEARCH",
    filters?.name,
    filters?.category,
    JSON.stringify(filters?.tenantIds),
    filters?.postedBy,
    filters?.offset,
    filters?.limit,
  ];

  const queryFn = () => Engagement.search(filters);

  return queryTemplate({
    queryKey,
    queryFn,
    config,
  });
};

export default useSearch;