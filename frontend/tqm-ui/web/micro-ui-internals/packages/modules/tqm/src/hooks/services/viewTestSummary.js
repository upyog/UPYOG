export const viewTestSummary = async ({ tenantId, t, id }) => {
  const response = await Digit.CustomService.getResponse({
    url: "/pqm-service/v1/_search",
    body: {
      pagination: {
        // sortBy: "testId",
        // sortOrder: "ASC",
      },
      testSearchCriteria: {
        testIds: [id],
      },
    },
  });

  const workflowData = await Digit.WorkflowService.getDetailsByIdWorks({ tenantId, id, moduleCode: "PQM" });
  const workflowActionMap = Digit?.Customizations?.commonUiConfig?.workflowActionMap;
  const testProcess = workflowData?.processInstances?.find((i) => i.action === workflowActionMap.update);
  const testResponse = response?.tests?.[0];
  const updatedTime = testResponse?.auditDetails?.lastModifiedTime;

  return {
    details: [
      {
        key: "ES_TQM_LABEL_TEST_ID",
        value: testResponse?.testId || t("TQM_SUMMARY_NA"),
      },
      {
        key: "ES_TQM_LABEL_PLANT_NAME",
        value: testResponse?.plantCode ? t(Digit.Utils.locale.getTransformedLocale(`PQM.Plant_${testResponse?.plantCode}`)) : t("TQM_SUMMARY_NA"),
      },
      {
        key: "ES_TQM_LABEL_TREATMENT_PROCESS",
        value: testResponse?.processCode ? t(Digit.Utils.locale.getTransformedLocale(`PQM.Process_${testResponse?.processCode}`)) : t("TQM_SUMMARY_NA"),
      },
      {
        key: "ES_TQM_LABEL_STAGE",
        value: testResponse?.stageCode ? t(Digit.Utils.locale.getTransformedLocale(`PQM.Stage_${testResponse?.stageCode}`)) : t("TQM_SUMMARY_NA"),
      },
      {
        key: "ES_TQM_LABEL_TEST_TYPE",
        value: testResponse?.testType ? t(Digit.Utils.locale.getTransformedLocale(`PQM.TestType_${testResponse?.testType}`)) : t("TQM_SUMMARY_NA"),
      },
      {
        key: t("ES_TQM_LABEL_OUTPUT_TYPE"),
        value: testResponse?.materialCode ? t(Digit.Utils.locale.getTransformedLocale(`PQM.Material_${testResponse?.materialCode}`)) : t("TQM_SUMMARY_NA"),
      },
      {
        key: t("ES_TQM_LABEL_LAB_NAME"),
        value: testResponse?.labAssignedTo ? t(Digit.Utils.locale.getTransformedLocale(`PQM.QualityTestLab_${testResponse?.labAssignedTo}`)) : t("TQM_SUMMARY_NA"),
      },
      {
        key: "ES_TQM_LABEL_TEST_SUBMITTED_ON",
        value:
          (updatedTime &&
            `${new Date(updatedTime).getDate()}/${new Date(updatedTime).getMonth() + 1}/${new Date(updatedTime).getFullYear()}`) ||
          t("TQM_SUMMARY_NA"),
      },
      {
        key: "ES_TQM_LABEL_TEST_RESULTS",
        isSla: true,
        isSuccess: testResponse?.status === "PASS" ? true : false,
        value: t(`TQM_TEST_STATUS_${testResponse?.status}`) || t("TQM_SUMMARY_NA"),
      },
    ],
    documents: testResponse?.documents?.map((i) => {
      return { title: i?.title, value: i?.fileStoreId };
    }),
    reading: testResponse?.testCriteria
      ? {
          title: "ES_TQM_TEST_PARAMS_HEADING",
          date:
            testResponse?.testType === "LAB_ADHOC"
              ? `${new Date(updatedTime).getDate()}/${new Date(updatedTime).getMonth() + 1}/${new Date(updatedTime).getFullYear()}`
              : testProcess?.auditDetails?.lastModifiedTime
              ? `${new Date(testProcess?.auditDetails?.lastModifiedTime).getDate()}/${new Date(testProcess?.auditDetails?.lastModifiedTime).getMonth() + 1}/${new Date(
                  testProcess?.auditDetails?.lastModifiedTime
                ).getFullYear()}`
              : `N/A`,
          readings: testResponse?.testCriteria,
        }
      : null,
    testResponse: testResponse,
    workflowStatus: workflowData
  };
};
