import React, { useMemo } from "react";
import { useTranslation } from "react-i18next";
import { Route, Routes } from "react-router-dom";
import { config } from "./config";
import SearchTradeComponent from "./searchTrade";

const SearchTrade = () => {
  const { t } = useTranslation();
  const { path } = Digit.Hooks.useModuleBasePath();

  const params = useMemo(() =>
    config?.map(
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
      <Route path={`*`}>
        <SearchTradeComponent config={params[0]} />
      </Route>
    </Routes>
  );
};

export default SearchTrade;