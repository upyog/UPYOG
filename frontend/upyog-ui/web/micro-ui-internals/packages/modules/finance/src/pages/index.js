import React from "react";
import { Route, Routes } from "react-router-dom";
import EGFFinance from "./employee/EGFFinance";

/**
 * Employee router for the Finance (EGF) module.
 *
 * All sub-paths under /upyog-ui/employee/egf/* are forwarded
 * to EGFFinance, which in turn passes the full pathname to the
 * ERP iframe as the target URL.
 *
 * e.g.  /upyog-ui/employee/egf/services/EGF/expensebill/newform
 *        → erp_url = https://<finEnv>.<domain>/services/EGF/expensebill/newform
 */
const EmployeeApp = ({ path }) => {
  return (
    <Routes>
      {/* Catch all sub-paths and render the EGFFinance iframe wrapper */}
      <Route path="*" element={<EGFFinance />} />
    </Routes>
  );
};

export default EmployeeApp;
