import useTenantsFSM from "./fsm/useTenants";
import useDesludging from "./fsm/useDesludging";
import useApplicationStatus from "./fsm/useApplicationStatus";
import useMDMS from "./fsm/useMDMS";
import useSearch from "./fsm/useSearch";
import useSearchAll from "./fsm/useSearchAll";
import useVehicleSearch from "./fsm/useVehicleSearch";
import useVehicleUpdate from "./fsm/useVehicleUpdate";
import useVehicleTripCreate from "./fsm/useVehicleTripCreate";
import useFSMInbox from "./fsm/useInbox";
import useApplicationUpdate from "./fsm/useApplicationUpdate";
import useWorkflowData from "./fsm/useWorkflowData";
import useDsoSearch from "./fsm/useDsoSearch";
import useApplicationDetail from "./fsm/useApplicationDetail";
import useApplicationActions from "./fsm/useApplicationActions";
import useApplicationAudit from "./fsm/useApplicationAudit";
import useSearchForAuditData from "./fsm/useSearchForAudit";
import useVehiclesSearch from "./fsm/useVehiclesSearch";
import useConfig from "./fsm/useConfig";
import useVendorDetail from "./fsm/useVendorDetail";
import useSlum from "./fsm/useSlum";
import usePaymentHistory from "./fsm/usePaymentHistory";
import useVendorCreate from "./fsm/useVendorCreate";
import useVendorUpdate from "./fsm/useVendorUpdate";
import useVehicleDetails from "./fsm/useVehicleDetails";
import useVehicleCreate from "./fsm/useVehicleCreate";
import useUpdateVehicle from "./fsm/useUpdateVehicle";
import useDriverSearch from "./fsm/useDriverSearch";
import useDriverCreate from "./fsm/useDriverCreate";
import useDriverUpdate from "./fsm/useDriverUpdate";
import useDriverDetails from "./fsm/useDriverDetails";
import useVendorSearch from "./fsm/useVendorSearch";
import useAdvanceBalanceCalulation from "./fsm/useAdvanceBalanceCalculation";
import useRouteSubscription from "./useRouteSubscription";
import useWorkerSearch from "./fsm/useWorkerSearch";
import useWorkerCreate from "./fsm/useWorkerCreate";
import useWorkerUpdate from "./fsm/useWorkerUpdate";
import useWorkerDetails from "./fsm/useWorkerDetails";
import useWorkerDelete from "./fsm/useWorkerDelete";
import usePlantUserCreate from "./fsm/usePlantUserCreate";
import usePlantUserUpdate from "./fsm/usePlantUserUpdate";
import useVehicleTrackingCheck from "./fsm/useVehicleTrackingCheck";
import useTripTrack from "./vehicleTracking/useTripTrack";

const fsm = {
  useTenants: useTenantsFSM,
  useDesludging: useDesludging,
  useMDMS: useMDMS,
  useSearch,
  useSearchAll,
  useInbox: useFSMInbox,
  useApplicationUpdate,
  useApplicationStatus,
  useWorkflowData,
  useDsoSearch,
  useApplicationDetail,
  useApplicationActions,
  useApplicationAudit,
  useSearchForAuditData,
  useVehicleSearch,
  useVehicleUpdate,
  useVendorDetail,
  useVehiclesSearch,
  useConfig,
  useSlum,
  usePaymentHistory,
  useVendorCreate,
  useVendorUpdate,
  useVehicleDetails,
  useVehicleCreate,
  useVendorCreate,
  useVendorUpdate,
  useVehicleDetails,
  useVehicleCreate,
  useUpdateVehicle,
  useDriverSearch,
  useDriverCreate,
  useDriverUpdate,
  useDriverDetails,
  useVehicleTripCreate,
  useVendorSearch,
  useAdvanceBalanceCalulation,
  useRouteSubscription,
  useWorkerSearch,
  useWorkerCreate,
  useWorkerUpdate,
  useWorkerDetails,
  useWorkerDelete,
  usePlantUserCreate,
  usePlantUserUpdate,
  useVehicleTrackingCheck,
  useTripTrack,
};

const Hooks = {
  fsm,
};

export default Hooks;
