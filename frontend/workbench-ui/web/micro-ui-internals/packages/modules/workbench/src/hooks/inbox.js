import { useQuery, useQueryClient } from "react-query";
import axios from "axios";

const useMDMSPopupSearch = (criteria) => {
  const updatedCriteria = { ...criteria };
  // Update filters
  if (updatedCriteria.state?.searchForm) {
    const { field, value } = updatedCriteria.state.searchForm;
    if (field && value !== undefined) {
      updatedCriteria.body.MdmsCriteria = {
        ...updatedCriteria.body.MdmsCriteria,
        tenantId: updatedCriteria.body.MdmsCriteria.tenantId,
        filters: {
          ...(updatedCriteria.body.MdmsCriteria.filters || {}),
          [field.code]: value,
        },
      };
    }
  }
  // Update limit and offset
  if (updatedCriteria.state?.tableForm) {
    const { limit, offset } = updatedCriteria.state.tableForm;
    updatedCriteria.body.MdmsCriteria = {
      ...updatedCriteria.body.MdmsCriteria,
      limit,
      offset,
    };
  }

  const { url, params, body, config = {}, changeQueryName = "Random" } = updatedCriteria;
  const client = useQueryClient();

  const fetchData = async () => {
    try {
      const response = await axios.post(url, body, { params }, config);
      return response.data;
    } catch (error) {
      throw new Error("Error fetching data");
    }
  };

  const { isLoading, data, isFetching, refetch, error } = useQuery(
    [url, params, body, "popup"].filter((e) => e),
    fetchData,
    {
      cacheTime: 0,
      ...config,
    }
  );

  return {
    isLoading,
    data,
    isFetching,
    revalidate: () => {
      data && client.invalidateQueries([url].filter((e) => e));
    },
    refetch,
    error,
  };
};

export default useMDMSPopupSearch;
