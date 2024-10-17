// Description: Common hook for all the modules' search data.	
// Author: Khalid Rashid	
// Status: In development	

import { useQuery, useQueryClient } from "react-query";	

const useCMSearch = ({ tenantId, type, filters, auth, searchedFrom = "" }, config = {}) => {	
  const client = useQueryClient();	
  const args = tenantId ? { tenantId, filters, auth } : { filters, auth };	

  let searchFn, searchlist, MSelect;	

  const fetchdata = (searchFn, searchlist, MSelect) => {	
    const { isLoading, error, data, isSuccess } = useQuery([searchlist, tenantId, filters, auth, config], () => searchFn(args), {	
      select: MSelect,	
      ...config,	
    });	

    return { isLoading, error, data, isSuccess, revalidate: () => client.invalidateQueries([searchlist, tenantId, filters, auth]) };	
  };	

  switch (type) {	
    case "ewaste":	
      searchlist = "ewSearchList";	
      searchFn = Digit.EwService.search;	
      MSelect = (data) => {	
        if (data.EwasteApplication.length > 0) data.EwasteApplication[0] = data.EwasteApplication[0] || [];	
        return data;	
      };	

      return fetchdata(searchFn, searchlist, MSelect);	

    case "chb":	
      searchFn = Digit.CHBServices.search;	
      searchlist = "chbSearchList";	
      MSelect = (data) => {	
        if (data.hallsBookingApplication.length > 0)	
          data.hallsBookingApplication[0].applicationNo = data.hallsBookingApplication[0].applicationNo || [];	
        return data;	
      };	

      return fetchdata(searchFn, searchlist, MSelect);	

    default:	
      return false;	
      break;	
  }	
};	

export default useCMSearch;