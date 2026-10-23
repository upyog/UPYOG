import React, { useEffect, useState } from "react";
import { Route, Link, Routes, useLocation } from "react-router-dom";
import { CollectPayment } from "./payment-collect";
import { SuccessfulPayment, FailedPayment } from "./response";
import { testForm } from "../../hoc/testForm-config";
import { subFormRegistry } from "@nudmcdgnpm/digit-ui-libraries";
import { useTranslation } from "react-i18next";

subFormRegistry?.addSubForm("testForm", testForm);

const EmployeePayment = ({ stateCode, cityCode, moduleCode }) => {
  const userType = "employee";
  const { path: currentPath } = Digit.Hooks.useModuleBasePath();
  const { pathname } = useLocation();
  const parts = pathname.split("/").filter(Boolean);
  const employeeHomePath = parts.length >= 2 ? `/${parts[0]}/${parts[1]}` : "/cnd-ui/employee";

  const { t } = useTranslation();

  const [link, setLink] = useState(null);

  const commonProps = { stateCode, cityCode, moduleCode, setLink };

  return (
    <React.Fragment>
      <p className="breadcrumb" style={{ marginLeft: "15px" }}>
        <Link to={employeeHomePath}>{t("ES_COMMON_HOME")}</Link>
      </p>
      <Routes>
        <Route path="collect/:businessService/:consumerCode" element={<CollectPayment {...commonProps} basePath={currentPath} />} />
        <Route path="success/:businessService/:receiptNumber/:consumerCode" element={<SuccessfulPayment {...commonProps} />} />
        <Route path="failure" element={<FailedPayment {...commonProps} />} />
      </Routes>
    </React.Fragment>
  );
};

export default EmployeePayment;
