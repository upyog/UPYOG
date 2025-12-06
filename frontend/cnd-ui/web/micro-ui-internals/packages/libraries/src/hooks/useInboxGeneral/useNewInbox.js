import { useTranslation } from "react-i18next";
import { useQuery, useQueryClient } from "react-query";

import { filterFunctions } from "./newFilterFn";
import { getSearchFields } from "./searchFields";
import { InboxGeneral } from "../../services/elements/InboxService";
import { CNDService } from "../../services/elements/CND";


// Function to configure inbox search parameters based on the module
const inboxConfig = (tenantId, filters) => ({
  CND: {
    services: ["cnd"],
    searchResponseKey: "cndApplicationDetail",
    businessIdsParamForSearch: "applicationNumber",
    businessIdAliasForSearch: "applicationNumber",
    fetchFilters: filterFunctions.CND,
    _searchFn: () => CNDService.search({ tenantId, filters }),
  }

});

// Custom hook for fetching inbox data
const useNewInboxGeneral = ({ tenantId, ModuleCode, filters, middleware = [], config = {} }) => {
  const client = useQueryClient();
  const { t } = useTranslation();
  const { fetchFilters, searchResponseKey, businessIdAliasForSearch, businessIdsParamForSearch } = inboxConfig()[ModuleCode];
  let { workflowFilters, searchFilters, limit, offset, sortBy, sortOrder,isUserDetailRequired } = fetchFilters(filters);

    // Query function to fetch inbox data
  const query = useQuery(
    ["INBOX", workflowFilters, searchFilters, ModuleCode, limit, offset, sortBy, sortOrder],
    () =>
      InboxGeneral.Search({
        inbox: { tenantId, processSearchCriteria: workflowFilters, moduleSearchCriteria: { ...searchFilters, sortBy, sortOrder,isUserDetailRequired }, limit, offset },
      }),
    {
      select: (data) => {
        const { statusMap, totalCount } = data;

        client.setQueryData(`INBOX_STATUS_MAP_${ModuleCode}`, statusMap);

        // Process and return data if items exists
        if (data.items.length) {
          return data.items?.map((obj) => ({
            searchData: obj.businessObject,
            workflowData: obj.ProcessInstance,
            statusMap,
            totalCount,
          }));
        } else {
          return [{ statusMap, totalCount, dataEmpty: true }];
        }
      },
      retry: false,
      ...config,
    }
  );

  return {
    ...query,
    searchResponseKey,
    businessIdsParamForSearch,
    businessIdAliasForSearch,
    searchFields: getSearchFields(true)[ModuleCode],
  };
};

export default useNewInboxGeneral;
