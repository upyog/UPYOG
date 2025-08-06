import React, { useState } from "react";
import { useQuery } from "react-query";
import { WSService } from "../../services/elements/WS";
/*
 * Feature :: Privacy
 * Task 6502 to show only locality info without door no and street names
 */

const useBulkSearchWS = ({ tenantId,  filters, config = {}}) => {
  let responseWS = "";
  responseWS=useQuery(
      ["WS_WATER_SEARCH",tenantId, ...Object.keys(filters)?.map((e) => filters?.[e])],
      async () => await WSService.WSMeterSearch({tenantId, filters }),
      {
        ...config,
      }
    )
    return responseWS?.data
};


export default useBulkSearchWS;
