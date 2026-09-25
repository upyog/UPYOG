import React, { useMemo } from "react";
import { useTranslation } from "react-i18next";
import { Route, Routes } from "react-router-dom";
import { config } from "./config";
import MyChallanResultsComponent from "./myChallan";

const MyChallans = () => {
  console.log("MyChallans rendered");
  const { t } = useTranslation();
  const { path } = Digit.Hooks.useModuleBasePath();

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
      <Route
        path="*"
        element={
          <MyChallanResultsComponent
            template={params[0].labels}
            header={params[0].texts.header}
            actionButtonLabel={params[0].texts.actionButtonLabel}
            t={t}
          />
        }
      />
    </Routes>
  );
};

export default MyChallans;
