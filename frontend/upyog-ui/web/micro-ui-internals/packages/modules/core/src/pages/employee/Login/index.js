import React, { useMemo } from "react";
import { useTranslation } from "react-i18next";
import { AppContainer } from "@nudmcdgnpm/digit-ui-react-components";
import { Route, Routes } from "react-router-dom";
import { loginConfig } from "./config";
import LoginComponent from "./login";

const EmployeeLogin = ({layout, emp, setEmp}) => {
  const { t } = useTranslation();
  const { path } = Digit.Hooks.useModuleBasePath();

  const loginParams = useMemo(() =>
    loginConfig.map(
      (step) => {
        const texts = {};
        for (const key in step.texts) {
          texts[key] = t(step.texts[key]);
        }
        return { ...step, texts };
      },
      [loginConfig]
    )
  );

  return (
    <Routes>
      <Route index element={<LoginComponent  layout={layout} emp={emp} setEmp={setEmp} config={loginParams[0]} t={t} />} />
    </Routes>
  );
};

export default EmployeeLogin;
