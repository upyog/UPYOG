import React, { useState, useEffect } from "react";
import { useTranslation } from "react-i18next";

/**
 * Custom Hook: useFilterEmployee
 *
 * Filters and returns employees based on their department assignment
 * and the service linked to a specific complaint.
 *
 * Steps:
 * 1. Fetches employee list based on tenant, role, and active status.
 * 2. Fetches service definitions from MDMS.
 * 3. Extracts department linked to the complaint's service.
 * 4. Filters employees assigned to that department.
 * 5. Formats and returns filtered employee data with department name and employee name + UUID.
 *
 * @param {string} tenantId - Tenant identifier (e.g., state.tenant).
 * @param {Array<string>} roles - Roles to filter employees by (e.g., ["CSR", "GRO"]).
 * @param {Object} complaintDetails - Complaint object with a serviceCode.
 * @param {boolean} isActive - If true, filters only active employees.
 * @returns {Array<Object>|null} employeeDetails - Array of department-wise employee data or null while loading.
 */


const useFilterEmployee = (tenantId, roles, complaintDetails,isActive) => {
  const [employeeDetails, setEmployeeDetails] = useState(null);
  const { t } = useTranslation();
  useEffect(() => {
    (async () => {
      const searchResponse = await Digit.PGRAIService.employeeSearch(tenantId, roles,isActive );
      const serviceDefs = await Digit.MDMSService.getServiceDefs(tenantId, "PGR");
      const serviceCode = complaintDetails.service.serviceCode;
      const service = serviceDefs?.find((def) => def.serviceCode === serviceCode);
      const department = service?.department;
      const employees = searchResponse.Employees.filter((employee) =>
        employee.assignments.map((assignment) => assignment.department).includes(department)
      );

      //employees data sholld only conatin name uuid dept
      setEmployeeDetails([
        {
          department: t(`COMMON_MASTERS_DEPARTMENT_${department}`),
          employees: employees.map((employee) => {
          return { uuid: employee.user.uuid, name: employee.user.name}})
        }
      ])
    })();
  }, [tenantId, roles, t, complaintDetails]);

  return employeeDetails;
};

export default useFilterEmployee;
