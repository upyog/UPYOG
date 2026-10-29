import React, { useEffect, useMemo, useState } from "react";
import { useTranslation } from "react-i18next";
import { Route, Routes } from "react-router-dom";
import { loginConfig as defaultLoginConfig } from "./config";
import LoginComponent from "./login";

const EmployeeLogin = () => {
  const { t } = useTranslation();
  // useRouteMatch removed — path not needed
  const [loginConfig, setloginConfig] = useState(defaultLoginConfig);

  const { data: mdmsData, isLoading } = Digit.Hooks.useCommonMDMS(Digit.ULBService.getStateId(), "commonUiConfig", ["LoginConfig"], {
    select: (data) => {
      return {
        config: data?.commonUiConfig?.LoginConfig
      };
    },
    retry: false,
  });

  //let loginConfig = mdmsData?.config ? mdmsData?.config : defaultLoginConfig;
  useEffect(() => {
    if(isLoading == false && mdmsData?.config)
    {  
      setloginConfig(mdmsData?.config)
    }
  },[mdmsData, isLoading])


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
    <Routes>                                        // Switch → Routes 
      <Route
        index                                       // exact → index 
        element={<LoginComponent config={loginParams[0]} t={t} />}  //children → element 
      />
    </Routes>

  );
};

export default EmployeeLogin;
