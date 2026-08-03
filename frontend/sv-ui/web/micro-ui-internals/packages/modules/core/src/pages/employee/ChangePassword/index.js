import React, { useMemo } from "react";
import { useTranslation } from "react-i18next";
import { AppContainer } from "@nudmcdgnpm/upyog-ui-react-components-lts";
import { Route, Routes } from "react-router-dom";
import { config } from "./config";
import ChangePasswordComponent from "./changePassword";

const EmployeeChangePassword = () => {
  const { t } = useTranslation();

  const params = useMemo(() =>
    config.map(
      (step) => {
        const texts = {};
        for (const key in step.texts) {
          texts[key] = t(step.texts[key]);
        }
        return { ...step, texts };
      },
      [config]
    )
  );

  return (
    <Routes>
      <Route index element={<ChangePasswordComponent config={params[0]} t={t} />} />
    </Routes>
  );
};

export default EmployeeChangePassword;
