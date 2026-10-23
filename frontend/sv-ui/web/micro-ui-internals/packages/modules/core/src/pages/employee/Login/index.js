import React, { useMemo } from "react";
import { useTranslation } from "react-i18next";
import { AppContainer } from "@nudmcdgnpm/upyog-ui-react-components-lts";
import { Route, Routes } from "react-router-dom";
import { loginConfig } from "./config";
import LoginComponent from "./login";

const EmployeeLogin = () => {
  const { t } = useTranslation();
  

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
      <Route
        index
        element={<LoginComponent config={loginParams[0]} t={t} />}
      />

    </Routes>
  );
};

export default EmployeeLogin;
