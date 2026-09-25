import React, { useMemo,useState } from "react";
import { useTranslation } from "react-i18next";
import { Route, useLocation, Routes } from "react-router-dom";
import { config as defaultConfig } from "./config";
import PropertySearchResults from "./searchResults";
import { loginSteps } from "../Otp/config";

const SearchResultsComponent = (props) => {
  const { config: propConfig, onSelect, clearParams, formData, stateCode } = props;
  const { t } = useTranslation();
  const { path } = Digit.Hooks.useModuleBasePath();

  const search = useLocation().search;
  const redirectToUrl = new URLSearchParams(search).get('redirectToUrl');

  // let config = propConfig ? [propConfig] : defaultConfig;
  let config = defaultConfig;

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
          <PropertySearchResults
            template={params[0].labels}
            header={params[0].texts.header}
            actionButtonLabel={params[0].texts.actionButtonLabel}
            t={t}
            isMutation={propConfig?.action === "MUTATION"}
            onSelect={onSelect}
            config={propConfig}
            clearParams={clearParams}
            stateCode={stateCode}
            redirectToUrl={redirectToUrl}
            searchQuery={formData?.cptSearchQuery}
          />
        }
      />
    </Routes>
  );
};
export default SearchResultsComponent;