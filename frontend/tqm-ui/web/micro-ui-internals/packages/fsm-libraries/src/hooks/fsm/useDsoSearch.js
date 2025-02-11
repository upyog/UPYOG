import React from "react";
import { useQuery } from "react-query";
import DsoDetails from "../../services/molecules/FSM/DsoDetails";

const useDsoSearch = (tenantId, filters, config = {},t) => {
  return useQuery(["DSO_SEARCH", filters], () => DsoDetails(tenantId, filters,t), config);
};

export default useDsoSearch;
