import React, { useMemo } from "react";
import { useTranslation } from "react-i18next";
import { Route, Routes } from "react-router-dom";
import { loginConfig } from "./config";
import ForgotPasswordComponent from "./forgotPassword";

const EmployeeForgotPassword = () => {
  const { t } = useTranslation();
  // useRouteMatch removed — path not needed (relative routes)

  const params = useMemo(() =>
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
    <Routes>                                        // Switch → Routes 
      <Route
        index                                       // exact → index 
        element={<ForgotPasswordComponent config={params[0]} t={t} />}  //children → element 
      />
    </Routes>

  );
};

export default EmployeeForgotPassword;
