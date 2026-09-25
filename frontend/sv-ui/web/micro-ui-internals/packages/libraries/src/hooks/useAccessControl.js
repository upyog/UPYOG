import { useQuery } from "@tanstack/react-query";
import AccessControlService from "../services/elements/Access";
const useAccessControl = (tenantId) => {
  const getUserRoles = Digit.SessionStorage.get("User")?.info?.roles;

  const roles = getUserRoles?.map((role) => {
    return role.code;
  });
// Updated: TanStack Query v5 requires useQuery to accept a single object instead of positional arguments.
// Updated: queryKey and queryFn are now explicit keys inside the object — positional args removed.
  const response = useQuery({
    queryKey: ["ACCESS_CONTROL", tenantId],
    queryFn: async () => await AccessControlService.getAccessControl(roles),
    enabled: roles ? true : false
  });
  return response;
};
export default useAccessControl;
