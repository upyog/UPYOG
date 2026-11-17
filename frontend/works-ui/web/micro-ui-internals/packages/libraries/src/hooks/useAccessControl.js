import { queryTemplate } from "../common/queryTemplate";
import AccessControlService from "../services/elements/Access";
const useAccessControl = (tenantId) => {
  const getUserRoles = Digit.SessionStorage.get("User")?.info?.roles;

  const roles = getUserRoles?.map((role) => {
    return role.code;
  });

  const response = queryTemplate({ queryKey: ["ACCESS_CONTROL", tenantId], queryFn: async () => await AccessControlService.getAccessControl(roles), config: {enabled:roles?true:false} });
  return response;
};
export default useAccessControl;
