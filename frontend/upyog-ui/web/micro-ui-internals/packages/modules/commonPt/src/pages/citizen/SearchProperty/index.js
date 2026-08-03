import React, { useMemo } from "react";
import { useTranslation } from "react-i18next";
import { Route, useLocation, Routes } from "react-router-dom";
import { config } from "./config";
import SearchPropertyComponent from "./searchProperty";

const SearchProperty = ({ onSelect }) => {
  const { t } = useTranslation();
  const { path } = Digit.Hooks.useModuleBasePath();
  
  const search = useLocation().search;
  const redirectToUrl = new URLSearchParams(search).get('redirectToUrl');
  
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
          <SearchPropertyComponent config={params[0]} onSelect={onSelect} redirectToUrl={redirectToUrl} />
        }
      />
    </Routes>
  );
};

export default SearchProperty;
