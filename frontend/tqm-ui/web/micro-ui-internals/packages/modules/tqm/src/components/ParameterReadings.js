import React, { Fragment } from "react";
import { ActionBar, CardCaption, CardSubHeader, SubmitBar } from "@egovernments/digit-ui-react-components";
import CardReading from "./CardReadings";
import CardMessage from "./CardMessage";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";

function ParameterReadings({ reading, responseData }) {
  const { t } = useTranslation();
  const history = useHistory();
  const isTestPassed = responseData?.status.includes("PASS") ? true : false;
  const searchParams = new URLSearchParams(location.search);
  const type = searchParams.get("type");
  const isMobile = window.Digit.Utils.browser.isMobile();

  return reading ? (
    <>
      {reading?.title ? <CardSubHeader style={isMobile ? {} : { marginTop: 0, marginBottom: 0 }}>{t(reading?.title)}</CardSubHeader> : null}
      {reading?.date ? (
        <CardCaption style={{ display: "flex" }}>
          <p style={{ marginRight: "0.5rem" }}>{t("ES_TQM_TEST_RESULTS_DATE_LABEL")}: </p>
          <p> {reading?.date}</p>
        </CardCaption>
      ) : null}
      {reading?.readings && reading?.readings?.length > 0
        ? reading?.readings?.map(({ criteriaCode, resultValue, resultStatus }) => (
            <CardReading
              showInfo={true}
              success={resultStatus === "PASS"}
              title={t(Digit.Utils.locale.getTransformedLocale(`PQM.QualityCriteria_${criteriaCode}`))}
              tip={Digit.Utils.locale.getTransformedLocale(`PQM.QualityCriteria_${criteriaCode}`)}
              value={resultValue}
            />
          ))
        : null}
      {reading?.readings?.length > 0 ? (
        <CardMessage
          success={isTestPassed}
          title={isTestPassed ? t("ES_TQM_TEST_PARAMS_SUCCESS_TITLE") : t("ES_TQM_TEST_PARAMS_FAIL_TITLE")}
          message={isTestPassed ? <>{t("ES_TQM_TEST_PARAMS_SUCCESS_MESSAGE")}</> : <>{t("ES_TQM_TEST_PARAMS_FAIL_MESSAGE")}</>}
        />
      ) : null}
      {isMobile ? (
        <SubmitBar
          label={type === "past" ? t("ES_TQM_TEST_BACK_TO_PAST_TEST") : t("ES_TQM_TEST_BACK_TO_INBOX")}
          onSubmit={() => (type === "past" ? history.goBack() : history.go(-3))}
          style={{ marginBottom: "12px" }}
        />
      ) : (
        <ActionBar>
          <SubmitBar
            label={type === "past" ? t("ES_TQM_TEST_BACK_TO_PAST_TEST") : t("ES_TQM_TEST_BACK_TO_INBOX")}
            onSubmit={() => (type === "past" ? history.goBack() : history.go(-3))}
          />
        </ActionBar>
      )}
    </>
  ) : null;
}

export default ParameterReadings;
