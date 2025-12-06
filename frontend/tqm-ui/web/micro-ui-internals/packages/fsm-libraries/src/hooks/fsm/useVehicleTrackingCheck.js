const useVehicleTrackingCheck = (tenantId, config = {}) => {
  const { data: vehicleTrackingResponse } = Digit.Hooks.fsm.useMDMS(tenantId, "FSM", "VehicleTracking", config);
  return vehicleTrackingResponse?.FSM?.VehicleTracking?.[0];
};

export default useVehicleTrackingCheck;
