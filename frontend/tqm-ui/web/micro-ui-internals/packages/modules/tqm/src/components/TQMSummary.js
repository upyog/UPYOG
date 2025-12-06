import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { Link, useHistory } from "react-router-dom";
import { Header, Loader, MultiLink, Toast, ViewComposer } from "@egovernments/digit-ui-react-components";

const TQMSummary = () => {
  const { t } = useTranslation();
  const history = useHistory();
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const searchParams = new URLSearchParams(location.search);
  const id = searchParams.get("id");
  const isMobile = window.Digit.Utils.browser.isMobile();

  const config = {
    select: (data) => ({
      cards: [
        {
          sections: [
            {
              type: "DATA",
              cardHeader: { value: t("ES_TQM_TEST_DETAILS_HEADING"), inlineStyles: { marginTop: 0 } },
              values: data.details,
            },
          ],
        },
        data?.documents?.[0]?.value !== null
          ? {
              sections: [
                {
                  cardHeader: { value: t("ES_TQM_DOCUMENTS_HEADING"), inlineStyles: isMobile ? {} : { marginTop: 0 } },
                  type: "COMPONENT",
                  component: "TqmDocumentsPreview",
                  props: {
                    documents: data?.documents,
                  },
                },
              ],
            }
          : {},
        data?.reading
          ? {
              sections: [
                {
                  type: "COMPONENT",
                  component: "TqmParameterReadings",
                  props: {
                    reading: data?.reading,
                    responseData: data?.testResponse,
                  },
                },
              ],
            }
          : {},
      ],
      isWorkflowComplete: data?.workflowStatus?.processInstances?.[0]?.state?.isTerminateState,
    }),
  };

  const { isLoading, data: testData, revalidate, isFetching } = Digit.Hooks.tqm.useViewTestSummary({ tenantId, t, id: id, config });

  if (isLoading || !testData) {
    return <Loader />;
  }

  if (testData?.length === 0) {
    history.goBack();
  }

  const handleDownloadPdf = async () => {
    try {
      const respo = await Digit.CustomService.getResponse({
        url: "/pqm-service/v1/_downloadPdf",
        params: {
          testId: id,
        },
      });
      if (respo?.filestoreIds?.[0]) {
        const pdfDownload = await Digit.UploadServices.Filefetch(respo?.filestoreIds, tenantId);
        window.open(pdfDownload?.data?.fileStoreIds?.[0]?.url, "_blank");
      }
    } catch (err) {
      console.error(err);
    }
  };

  return (
    <React.Fragment>
      <div className="cardHeaderWithOptions">
        <Header>{t("ES_TQM_SUMMARY_HEADING")}</Header>
        {testData?.isWorkflowComplete ? (
          <MultiLink
            className="multilinkWrapper employee-mulitlink-main-div"
            onHeadClick={handleDownloadPdf}
            style={{ marginTop: "10px" }}
            downloadBtnClassName={"employee-download-btn-className"}
          />
        ) : null}
      </div>

      {!isLoading && <ViewComposer data={testData} isLoading={isLoading} />}
    </React.Fragment>
  );
};

export default TQMSummary;