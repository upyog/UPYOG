import React, { useMemo } from "react";
import { useTranslation } from "react-i18next";
import { Route, Switch, useRouteMatch } from "react-router-dom";
import { config } from "./config";
import SearchAssessmentComponent from "./searchAssessment";

const SearchAssessment = () => {
  const { t } = useTranslation();
  const { path } = useRouteMatch();

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
    <Switch>
      <Route path={`${path}`} exact>
        <SearchAssessmentComponent config={params[0]} />
      </Route>
    </Switch>
  );
};

export default SearchAssessment;
