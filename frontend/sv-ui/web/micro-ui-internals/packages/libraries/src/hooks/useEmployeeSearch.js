import { useQuery } from "@tanstack/react-query";

const useEmployeeSearch = (tenantId, filters, config = {}) => {
  if (filters.roles) {
    filters.roles = filters.roles.map((role) => role.code).join(",");
  }
  return useQuery({
    queryKey: ["EMPLOYEE_SEARCH", filters],
    queryFn: () => Digit.UserService.employeeSearch(tenantId, filters),
    ...config
  });
};

export default useEmployeeSearch;
