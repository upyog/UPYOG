import React, { useEffect, useState } from "react";
import { useRouteMatch, Switch, Route, Link } from "react-router-dom";
import { CollectPayment } from "./payment-collect";
import { SuccessfulPayment, FailedPayment } from "./response";
import { testForm } from "../../hoc/testForm-config";
import { subFormRegistry } from "@nudmcdgnpm/digit-ui-libraries";
import { useTranslation } from "react-i18next";

subFormRegistry?.addSubForm("testForm", testForm);

const EmployeePayment = ({ stateCode, cityCode, moduleCode }) => {
  const userType = "employee";
  const { path: currentPath } = useRouteMatch();

  const { t } = useTranslation();

  const [link, setLink] = useState(null);

  const commonProps = { stateCode, cityCode, moduleCode, setLink };

  return (
    <React.Fragment>
      <p className="breadcrumb" style={{ marginLeft: "15px" }}>
        <Link to={`/sv-ui/employee`}>{t("ES_COMMON_HOME")}</Link>
      </p>
      <Switch>
        <Route path={`${currentPath}/collect/:businessService/:consumerCode`}>
          <CollectPayment {...commonProps} basePath={currentPath} />
        </Route>
        <Route path={`${currentPath}/success/:businessService/:receiptNumber/:consumerCode`}>
          <SuccessfulPayment {...commonProps} />
        </Route>
        <Route path={`${currentPath}/failure`}>
          <FailedPayment {...commonProps} />
        </Route>
      </Switch>
    </React.Fragment>
  );
};

export default EmployeePayment;
