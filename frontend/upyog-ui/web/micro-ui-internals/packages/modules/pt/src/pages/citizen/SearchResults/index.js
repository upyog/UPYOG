import React, { useMemo } from "react";
import { useTranslation } from "react-i18next";
import { Route, Routes } from "react-router-dom";
import { config as defaultConfig } from "./config";
import SearchResultsComponent from "./searchResults";

const CitizenSearchResults = (props) => {
  const { config: propConfig, onSelect, clearParams } = props;
  const { t } = useTranslation();
  const { path } = Digit.Hooks.useModuleBasePath();
  let config = propConfig ? [propConfig] : defaultConfig;

  const params = useMemo(() => {
    return config?.map?.((step) => {
      const texts = {};
      for (const key in step.texts) {
        texts[key] = t(step.texts[key]);
      }
      return { ...step, texts };
    });
  }, [config]);

  return (
<Routes>
  <Route
    path="*"
    element={
      <SearchResultsComponent
        template={params[0].labels}
        header={params[0].texts.header}
        actionButtonLabel={params[0].texts.actionButtonLabel}
        t={t}
        isMutation={propConfig?.action === "MUTATION"}
        onSelect={onSelect}
        config={propConfig}
        clearParams={clearParams}
      />
    }
  />
</Routes>

  );
};

export default CitizenSearchResults;
