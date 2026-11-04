/** 
* Hook to structure the data of applicationDetails page for a single application
* Uses the CNDSearch service where the data is structured and returned here
*
* @param {function} t - Translation function for internationalization
* @param {string} tenantId - The tenant identifier for the application
* @param {string} applicationNumber - The unique identifier for the application
* @param {object} config - Optional configuration object for react-query
* @param {string} userType - The type of user accessing the application details
* @param {object} args - Additional arguments to pass to the search function
* @returns {object} - React query result object containing application data
*/

import { queryTemplate } from "../../common/queryTemplate";
import { CNDSearch } from "../../services/molecules/CND/Search";

const useCndApplicationDetails = (t, tenantId, applicationNumber, isUserDetailRequired, config = {}, userType, args) => {
  const queryKey = [
    "APPLICATION_SEARCH",
    "CND_SEARCH",
    applicationNumber,
    isUserDetailRequired,
    userType,
    JSON.stringify(args)
  ];

  const queryFn = () =>
    CNDSearch.applicationDetails(t, tenantId, applicationNumber, isUserDetailRequired, userType, args);

  const select = (data) => {
    let applicationDetails = data.applicationDetails.map((obj) => {
      return obj;
    });

    return {
      applicationData: data,
      applicationDetails
    };
  };

  return queryTemplate({
    queryKey,
    queryFn,
    select,
    config
  });
};

export default useCndApplicationDetails;