import { useQuery } from "react-query";
import { FSMService } from "../../services/elements/FSM";

const useTripTrack = async (args) => {
  const requestCriteria = {
    url: "/trackingservice/api/v3/trip/_searchfsm",
    method: "POST",
    params: {
      tenantId: tenantId,
      ...filters,
    },
  };
  const { isLoading, data: response } = Digit.Hooks.useCustomAPIHook(requestCriteria);

  return response;
};

export default useTripTrack;
