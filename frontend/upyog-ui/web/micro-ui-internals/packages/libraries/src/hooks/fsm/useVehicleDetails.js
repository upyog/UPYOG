import { queryTemplate } from "../../common/queryTemplate";
import VehicleDetails from "../../services/molecules/FSM/VehicleDetails";

const useVehicleDetails = (tenantId, filters, config = {}) => {
  const queryKey = [
    "FSM_VEHICLE_DETAILS",
    tenantId,
    JSON.stringify(filters),
  ];

  return queryTemplate({
    queryKey,
    queryFn: () => VehicleDetails(tenantId, filters),
    config,
  });
};

export default useVehicleDetails;