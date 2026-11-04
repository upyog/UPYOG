package org.upyog.Automation.config;
import java.util.Map;

public class ModuleData {

    private String moduleName;

    private CitizenData citizen;

    private EmployeeData employee;

    private VendorData vendor;

    private Map<String, EmployeeData> employees;

    public ModuleData() {
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public CitizenData getCitizen() {
        return citizen;
    }

    public void setCitizen(CitizenData citizen) {
        this.citizen = citizen;
    }

    public EmployeeData getEmployeeByRole(String role) {

        if (employees != null &&
                employees.containsKey(role)) {

            return employees.get(role);
        }

        if (employee != null) {

            return employee;
        }

        return null;
    }

    public void setEmployee(EmployeeData employee) {
        this.employee = employee;
    }

    public VendorData getVendor() {
        return vendor;
    }

    public void setVendor(VendorData vendor) {
        this.vendor = vendor;
    }

    public Map<String, EmployeeData> getEmployees() {
        return employees;
    }

    public void setEmployees(Map<String, EmployeeData> employees) {
        this.employees = employees;
    }
}