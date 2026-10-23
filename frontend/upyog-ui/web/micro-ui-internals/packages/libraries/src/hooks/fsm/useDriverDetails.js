import { queryTemplate } from "../../common/queryTemplate";
import DriverDetails from "../../services/molecules/FSM/DriverDetails";

const useDriverDetails = (tenantId, filters, config = {}) => {
  const queryKey = [
    "FSM_DRIVER_DETAILS",
    tenantId,
    JSON.stringify(filters),
  ];

  const queryFn = () => DriverDetails(tenantId, filters);

  return queryTemplate({
    queryKey,
    queryFn,
    config,
  });
};

export default useDriverDetails;