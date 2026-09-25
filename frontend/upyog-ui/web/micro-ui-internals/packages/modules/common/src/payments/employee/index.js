import React, { useEffect, useState } from "react";
// import { useTranslation } from "react-i18next";
import { Route, Link, Routes } from "react-router-dom";
import { CollectPayment } from "./payment-collect";
import { SuccessfulPayment, FailedPayment } from "./response";
// import { SubformComposer } from "../../hoc";
// import { subFormRegistry } from "../../hoc/subFormClass";
import { testForm } from "../../hoc/testForm-config";
import { subFormRegistry } from "@upyog/digit-ui-libraries";
import { useTranslation } from "react-i18next";
import IFrameInterface from "./IFrameInterface";

subFormRegistry?.addSubForm("testForm", testForm);

const EmployeePayment = ({ stateCode, cityCode, moduleCode }) => {
  const userType = "employee";
  const { path: currentPath } = Digit.Hooks.useModuleBasePath();

  const { t } = useTranslation();

  const [link, setLink] = useState(null);

  const commonProps = { stateCode, cityCode, moduleCode, setLink };

  const isFsm = location?.pathname?.includes("fsm") || location?.pathname?.includes("FSM");

  return (
    <React.Fragment>
      <p className="breadcrumb" style={{ marginLeft: "15px" }}>
        <Link to={"/upyog-ui/employee"}>{t("ES_COMMON_HOME")}</Link>
        {isFsm ? <Link to={"/upyog-ui/employee/fsm/home"}>/ {t("ES_TITLE_FSM")} </Link> : null}
        {isFsm ? <Link to={"/upyog-ui/employee/fsm/inbox"}>/ {t("ES_TITLE_INBOX")}</Link> : null}/ {link}
      </p>
      <Routes>
        <Route path="collect/:businessService/:consumerCode/*" element={<CollectPayment {...commonProps} basePath={currentPath} />} />
        <Route path="success/:businessService/:receiptNumber/:consumerCode/*" element={<SuccessfulPayment {...commonProps} />} />
        <Route path="integration/:moduleName/:pageName" element={<IFrameInterface {...commonProps} />} />
        <Route path="failure" element={<FailedPayment {...commonProps} />} />
      </Routes>
    </React.Fragment>
  );
};

export default EmployeePayment;
