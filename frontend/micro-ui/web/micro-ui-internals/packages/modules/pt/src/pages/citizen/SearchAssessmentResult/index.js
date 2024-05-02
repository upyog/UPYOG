import React, { useMemo } from "react";
import { useTranslation } from "react-i18next";
import { Route, Switch, useRouteMatch } from "react-router-dom";
import { config as defaultConfig } from "./config";
import AssessmentSearchResults from "./searchAssessmentResult";

const CitizenSearchAssessmentResults = (props) => {
  const { config: propConfig, onSelect, clearParams } = props;
  const { t } = useTranslation();
  const { path } = useRouteMatch();
  let config = propConfig ? [propConfig] : defaultConfig;
  console.log("config===",config)
  const params = useMemo(() => {
    return config?.map?.((step) => {
      const texts = {};
      for (const key in step.texts) {
        texts[key] = t(step.texts[key]);
      }
      return { ...step, texts };
    });
  }, [config]);

  console.log("params===",params)

  return (
    <Switch>
      <Route path={`${path}`} exact>
        <AssessmentSearchResults
          template={params[0].labels}
          header={params[0].texts.header}
          actionButtonLabel={params[0].texts.actionButtonLabel}
          t={t}
          isMutation={propConfig?.action === "MUTATION"}
          isAmalgamation={propConfig?.action === "AMALGAMATION"}
          onSelect={onSelect}
          config={propConfig}
          clearParams={clearParams}
        />
      </Route>
    </Switch>
  );
};

export default CitizenSearchAssessmentResults;
