import React, { Fragment } from "react";
import { ViewComposer, Header, Loader, MultiLink } from "@egovernments/digit-ui-react-components";
import { useTranslation } from "react-i18next";
import { useLocation } from "react-router-dom";

function ViewTestResults() {
  const { t } = useTranslation();
  const location = useLocation();
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const searchParams = new URLSearchParams(location.search);
  const id = searchParams.get("id");
  const type = searchParams.get("type");
  const businessService = "PQM";

  const { isLoading, data: testData, revalidate, isFetching } = Digit.Hooks.tqm.useViewTestResults({
    t,
    tenantId: tenantId,
    id: id,
    type: type,
    config: {
      select: (data) => ({
        cards: [
          {
            sections: [
              {
                type: "DATA",
                cardHeader: { value: "Test Details", inlineStyles: { marginTop: 0 } },
                values: data?.details,
              },
              data?.tableData
                ? {
                    type: "COMPONENT",
                    component: "TqmDetailsTable",
                    props: {
                      cardHeader: { value: "Test Result", inlineStyles: { marginTop: "1rem" } },
                      rowsData: data?.tableData.map((i, index) => {
                        return {
                          slno: index + 1,
                          qp: i.qparameter,
                          uom: i.uom,
                          bench: i.benchmarkValues,
                          results: i.results,
                          status: i.status,
                        };
                      }),
                      columnsData: [
                        {
                          Header: t("ES_TQM_SNO"),
                          accessor: "slno",
                        },
                        {
                          Header: t("ES_TQM_QUALITY_PARAMETER"),
                          accessor: "qp",
                        },
                        {
                          Header: t("ES_TQM_UOM"),
                          accessor: "uom",
                        },
                        {
                          Header: t("ES_TQM_BENCHMARK"),
                          accessor: "bench",
                        },
                        {
                          Header: t("ES_TQM_RESULTS"),
                          accessor: "results",
                          Cell: ({ row }) => {
                            return <span className={row?.original?.status === "PASS" ? "sla-cell-success" : "sla-cell-error"}>{row?.original?.results}</span>;
                          },
                        },
                      ],
                      summaryRows: data?.testSummary,
                    },
                  }
                : {},
              data?.documents?.[0]?.value
                ? {
                    cardHeader: { value: "Documents", inlineStyles: {} },
                    type: "COMPONENT",
                    component: "TqmDocumentsPreview",
                    props: {
                      documents: data?.documents,
                    },
                  }
                : {},
              {
                cardHeader: { value: "Application Timeline", inlineStyles: {} },
                type: "WFHISTORY",
                businessService: businessService,
                tenantId: tenantId,
                applicationNo: id,
                timelineStatusPrefix: "TQM_TIMELINE_",
                statusAttribute: "status",
              },
            ],
          },
        ],
        isWorkflowComplete: data?.workflowStatus?.processInstances?.[0]?.state?.isTerminateState,
      }),
      staleTime: 0,
      cacheTime: 0,
    },
  });


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

  if (isLoading) {
    return <Loader />;
  }

  return (
    <>
      <div style={{display: "flex", justifyContent: "space-between"}}>
        <Header> {t("ES_TQM_TEST_RESULTS_DETAILS_HEADER")} </Header>
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
    </>
  );
}

export default ViewTestResults;
