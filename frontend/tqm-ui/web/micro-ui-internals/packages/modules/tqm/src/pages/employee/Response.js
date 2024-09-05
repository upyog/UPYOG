import React, { useState } from "react";
import { Link, useHistory, useLocation } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { Banner, Card, LinkLabel, ArrowLeftWhite, ActionBar, SubmitBar } from "@egovernments/digit-ui-react-components";

const Response = () => {
  const { t } = useTranslation();
  const history = useHistory();
  const queryStrings = Digit.Hooks.useQueryParams();
  const [testId, setTestId] = useState(queryStrings?.testId);
  const [isResponseSuccess, setIsResponseSuccess] = useState(queryStrings?.isSuccess === "true" ? true : queryStrings?.isSuccess === "false" ? false : true);
  const { state } = useLocation();
  const isMobile = window.Digit.Utils.browser.isMobile();

  const navigate = (page) => {
    switch (page) {
      case "contracts-inbox": {
        history.push(`/${window.contextPath}/employee/tqm/summary`);
      }
    }
  };

  return (
    <Card style={{ padding: 0 }}>
      <Banner successful={isResponseSuccess} message={t(state?.message)} multipleResponseIDs={testId} whichSvg={`${isResponseSuccess ? "tick" : null}`} />
      <Card style={{ border: "none", boxShadow: "none", padding: "0", paddingBottom: "1rem" }}>
        <div style={{ display: "flex", marginBottom: "0.75rem" }}> {t(state?.text, { TEST_ID: testId })} </div>
        {isMobile ? (
          <Link to={`/${window.contextPath}/employee/tqm/summary?id=${testId}`}>
            <SubmitBar label={t("ES_TQM_SEE_SUMMARY_TITLE")} />
          </Link>
        ) : (
          <ActionBar>
            <Link to={`/${window.contextPath}/employee/tqm/summary?id=${testId}`}>
              <SubmitBar label={t("ES_TQM_SEE_SUMMARY_TITLE")} />
            </Link>
          </ActionBar>
        )}
      </Card>
    </Card>
  );
};

export default Response;
