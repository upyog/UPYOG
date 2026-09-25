import { PTService } from "../../services/elements/PT";
import { mutationTemplate } from "../../common/mutationTemplate";

const usePropertyAPI = (tenantId, type = true, config = {}) => {
  // Wrapper function around PTService create/update APIs.
  // This keeps the mutation logic in one place and allows mutationTemplate
  // to handle common mutation behavior like loading state, error handling,
  // cache invalidation, and success callbacks.
  const mutationFn = async (data) => {
    // type = true means create property, false means update property.
    // Both operations use the same mutation hook, but call different APIs
    // based on the requested operation.
    let response;

    if (type) {
      response = await PTService.create(data, tenantId);
    } else {
      response = await PTService.update(data, tenantId);
    }
    // Always return the API response so React Query can update mutation state
    // and execute configured callbacks with the response data.
    return response;
  };
  return mutationTemplate({
    mutationFn,
    config,
  });
};

export default usePropertyAPI;