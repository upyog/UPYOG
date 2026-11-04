import React, { useMemo } from "react";
import { useTranslation } from "react-i18next";
import { Route, Routes } from "react-router-dom";
import ChangePasswordComponent from "./changePassword";
import { config } from "./config";

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
    <Routes>                                        // Switch → Routes 
      <Route
        index                                       // exact → index 
        element={<ChangePasswordComponent config={params[0]} t={t} />}  //children → element 
      />
    </Routes>

  );
};

export default EmployeeChangePassword;
