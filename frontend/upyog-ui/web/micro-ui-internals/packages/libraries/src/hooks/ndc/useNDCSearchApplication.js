import { queryTemplate } from "../../common/queryTemplate";

export const useNDCSearchApplication = (params, tenantId, config = {}, t) => {
  return queryTemplate({
    queryKey: ["NDC_APPLICATIONS_LIST", params],

    queryFn: async () => {
      const data = await Digit.NDCService.NDCsearch({
        filters: params,
        tenantId,
        config,
      });

      return data;
    },

    select: (data) => {
      const applications = data?.Applications || [];
      const count = data?.totalCount || 0;

      const mappedData = applications.map((owner) => ({
        BPA_APPLICATION_NUMBER_LABEL: owner?.applicationNo,
        TL_LOCALIZATION_OWNER_NAME: owner?.owners?.[0]?.name,
        TL_HOME_SEARCH_RESULTS_APP_STATUS_LABEL:
          owner?.applicationStatus,
        Applications: owner,
      }));

      return {
        data: mappedData,
        count,
      };
    },

    config: {
      staleTime: Infinity,
      ...config,
    },
  });
};



export const useNDCSearchApplicationEmployee = (
  params,
  tenantId,
  config = {},
  t
) => {
  return queryTemplate({
    queryKey: ["NDC_APPLICATIONS_LIST", params],

    queryFn: async () => {
      const data = await Digit.NDCService.NDCsearch({
        filters: params,
        tenantId,
        config,
      });

      return data;
    },

    select: (data) => data,

    config: {
      staleTime: Infinity,
      ...config,
    },
  });
};