export const searchTestResultData = async ({ t, id, type, tenantId }) => {
  const userInfo = Digit.UserService.getUser();
  const userRoles = userInfo.info.roles.map((roleData) => roleData.code);

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
  const testResponse = response?.tests?.[0];
  const testcriteraData = testResponse?.testCriteria;

  const mdmsCriteriaData = await Digit.CustomService.getResponse({
    url: "/mdms-v2/v2/_search",
    body: {
      tenantId,
      MdmsCriteria: {
        tenantId: tenantId,
        schemaCode: "PQM.QualityCriteria",
        isActive: true,
        limit: 100,
      },
    },
  });

  const combinedData = [];

  testcriteraData?.forEach((testItem) => {
    const matchingMdmsItem = mdmsCriteriaData?.mdms?.find((mdmsItem) => mdmsItem.uniqueIdentifier === testItem.criteriaCode);

    if (matchingMdmsItem) {
      const combineResults = `${t(Digit.Utils.locale.getTransformedLocale(`ES_TQM_${matchingMdmsItem.data.benchmarkRule}`))} ${matchingMdmsItem.data.benchmarkValues.join(' - ')}`;
      const mergedData = {
        criteriaCode: testItem.criteriaCode,
        qparameter: matchingMdmsItem.data.parameter,
        uom: matchingMdmsItem.data.unit,
        benchmarkValues: combineResults,
        results: testItem.resultValue,
        status: testItem.resultStatus,
      };
      combinedData.push(mergedData);
    }
  });

  const workflowData = await Digit.WorkflowService.getDetailsByIdWorks({ tenantId, id, moduleCode: "PQM" });
  let sla = 0;

  if (type !== "adhoc") {
    try {
      const currentDate = new Date();
      const targetTimestamp = testResponse?.scheduledDate;
      const targetDate = new Date(targetTimestamp);
      const remainingSLA = targetDate - currentDate;
      sla = targetTimestamp ? Math.ceil(remainingSLA / (24 * 60 * 60 * 1000)) : 0;
    } catch (err) {
      console.error("error fetching workflow data");
    }
  }

  const workflowActionMap = Digit?.Customizations?.commonUiConfig?.workflowActionMap;
  const sampleProcess = workflowData?.processInstances?.find((i) => i.action === workflowActionMap.submit);
  const testProcess = workflowData?.processInstances?.find((i) => i.action === workflowActionMap.update);

  return {
    details:
      userRoles.includes("PQM_ADMIN") && type !== "adhoc"
        ? [
            {
              key: t("ES_TQM_LABEL_TEST_ID"),
              value: testResponse?.testId || t("ES_TQM_NA"),
            },
            {
              key: t("ES_TQM_LABEL_PLANT_NAME"),
              value: testResponse?.plantCode ? t(Digit.Utils.locale.getTransformedLocale(`PQM.Plant_${testResponse?.plantCode}`)) : t("ES_TQM_TBD"),
            },
            {
              key: t("ES_TQM_LABEL_TREATMENT_PROCESS"),
              value: testResponse?.processCode ? t(Digit.Utils.locale.getTransformedLocale(`PQM.Process_${testResponse?.processCode}`)) : t("ES_TQM_TBD"),
            },
            {
              key: t("ES_TQM_LABEL_STAGE"),
              value: testResponse?.stageCode ? t(Digit.Utils.locale.getTransformedLocale(`PQM.Stage_${testResponse?.stageCode}`)) : t("ES_TQM_TBD"),
            },
            {
              key: t("ES_TQM_LABEL_TEST_TYPE"),
              value: testResponse?.testType ? t(Digit.Utils.locale.getTransformedLocale(`PQM.TestType_${testResponse?.testType}`)) : t("ES_TQM_TBD"),
            },
            {
              key: t("ES_TQM_LABEL_OUTPUT_TYPE"),
              value: testResponse?.materialCode ? t(Digit.Utils.locale.getTransformedLocale(`PQM.Material_${testResponse?.materialCode}`)) : t("ES_TQM_TBD"),
            },
            {
              key: t("ES_TQM_LABEL_LAB_NAME"),
              value: testResponse?.labAssignedTo ? t(Digit.Utils.locale.getTransformedLocale(`PQM.QualityTestLab_${testResponse?.labAssignedTo}`)) : t("ES_TQM_TBD"),
            },
            {
              key: t("ES_TQM_LABEL_TEST_SCHEDULED_DATE"),
              value:
                (testResponse?.scheduledDate &&
                  `${new Date(testResponse?.scheduledDate).getDate()}/${new Date(testResponse?.scheduledDate).getMonth() + 1}/${new Date(
                    testResponse?.scheduledDate
                  ).getFullYear()}`) ||
                t("ES_TQM_TBD"),
            },
            {
              key: t("ES_TQM_LABEL_STATUS"),
              value: t(`TQM_TEST_STATUS_${testResponse?.wfStatus}`) || t("ES_TQM_TBD"),
            },
            {
              key: t("ES_TQM_LABEL_SAMPLE_SUBMITTED_ON"),
              value:
                sampleProcess && sampleProcess?.auditDetails?.lastModifiedTime
                  ? `${new Date(sampleProcess && sampleProcess?.auditDetails?.lastModifiedTime).getDate()}/${
                      new Date(sampleProcess && sampleProcess?.auditDetails?.lastModifiedTime).getMonth() + 1
                    }/${new Date(sampleProcess && sampleProcess?.auditDetails?.lastModifiedTime).getFullYear()}`
                  : t("ES_TQM_TBD"),
            },
            {
              key: t("ES_TQM_LABEL_TEST_SUBMITTED_BY"),
              value: testProcess?.assigner?.name || t("ES_TQM_TBD"),
            },
            {
              key: t("ES_TQM_LABEL_TEST_SUBMITTED_DATE"),
              value:
                workflowData?.processInstances?.[0]?.state?.isTerminateState === true && workflowData?.processInstances?.[0]?.auditDetails?.lastModifiedTime
                  ? `${new Date(workflowData?.processInstances?.[0]?.auditDetails?.lastModifiedTime).getDate()}/${
                      new Date(workflowData?.processInstances?.[0]?.auditDetails?.lastModifiedTime).getMonth() + 1
                    }/${new Date(workflowData?.processInstances?.[0]?.auditDetails?.lastModifiedTime).getFullYear()}`
                  : t("ES_TQM_TBD"),
            },
            workflowData?.processInstances?.[0]?.state?.isTerminateState !== true
              ? {
                  key: t("ES_TQM_LABEL_SLA"),
                  isSla: true,
                  isSuccess: Math.sign(sla) === -1 ? false : true,
                  value: Math.sign(sla) === -1 ? `${Math.ceil(sla)} ${t("COMMON_DAYS_OVERDUE")}` : `${sla} ${t("COMMON_DAYS")}`,
                }
              : {},
          ]
        : userRoles.includes("PQM_ADMIN") && type === "adhoc"
        ? [
            {
              key: t("ES_TQM_LABEL_TEST_ID"),
              value: testResponse?.testId || t("ES_TQM_NA"),
            },
            {
              key: t("ES_TQM_LABEL_PLANT_NAME"),
              value: testResponse?.plantCode ? t(Digit.Utils.locale.getTransformedLocale(`PQM.Plant_${testResponse?.plantCode}`)) : t("ES_TQM_TBD"),
            },
            {
              key: t("ES_TQM_LABEL_TREATMENT_PROCESS"),
              value: testResponse?.processCode ? t(Digit.Utils.locale.getTransformedLocale(`PQM.Process_${testResponse?.processCode}`)) : t("ES_TQM_TBD"),
            },
            {
              key: t("ES_TQM_LABEL_STAGE"),
              value: testResponse?.stageCode ? t(Digit.Utils.locale.getTransformedLocale(`PQM.Stage_${testResponse?.stageCode}`)) : t("ES_TQM_TBD"),
            },
            {
              key: t("ES_TQM_LABEL_TEST_TYPE"),
              value: testResponse?.testType ? t(Digit.Utils.locale.getTransformedLocale(`PQM.TestType_${testResponse?.testType}`)) : t("ES_TQM_TBD"),
            },
            {
              key: t("ES_TQM_LABEL_OUTPUT_TYPE"),
              value: testResponse?.materialCode ? t(Digit.Utils.locale.getTransformedLocale(`PQM.Material_${testResponse?.materialCode}`)) : t("ES_TQM_TBD"),
            },
            {
              key: t("ES_TQM_LABEL_TEST_RESULT_DATE"),
              value:
                (testResponse?.scheduledDate &&
                  `${new Date(testResponse?.scheduledDate).getDate()}/${new Date(testResponse?.scheduledDate).getMonth() + 1}/${new Date(
                    testResponse?.scheduledDate
                  ).getFullYear()}`) ||
                t("ES_TQM_TBD"),
            },
            // {
            //   key: t("ES_TQM_LABEL_STATUS"),
            //   value: t(`TQM_TEST_STATUS_${testResponse?.wfStatus}`) || t("ES_TQM_TBD"),
            // },
          ]
        : [
            {
              key: t("ES_TQM_LABEL_TEST_ID"),
              value: testResponse?.testId || t("ES_TQM_NA"),
            },
            {
              key: t("ES_TQM_LABEL_PLANT_NAME"),
              value: testResponse?.plantCode ? t(Digit.Utils.locale.getTransformedLocale(`PQM.Plant_${testResponse?.plantCode}`)) : t("ES_TQM_NA"),
            },
            {
              key: t("ES_TQM_LABEL_TREATMENT_PROCESS"),
              value: testResponse?.processCode ? t(Digit.Utils.locale.getTransformedLocale(`PQM.Process_${testResponse?.processCode}`)) : t("ES_TQM_NA"),
            },
            {
              key: t("ES_TQM_LABEL_STAGE"),
              value: testResponse?.stageCode ? t(Digit.Utils.locale.getTransformedLocale(`PQM.Stage_${testResponse?.stageCode}`)) : t("ES_TQM_NA"),
            },
            {
              key: t("ES_TQM_LABEL_TEST_TYPE"),
              value: testResponse?.testType ? t(Digit.Utils.locale.getTransformedLocale(`PQM.TestType_${testResponse?.testType}`)) : t("ES_TQM_NA"),
            },
            {
              key: t("ES_TQM_LABEL_OUTPUT_TYPE"),
              value: testResponse?.materialCode ? t(Digit.Utils.locale.getTransformedLocale(`PQM.Material_${testResponse?.materialCode}`)) : t("ES_TQM_NA"),
            },
            {
              key: t("ES_TQM_LABEL_LAB_NAME"),
              value: testResponse?.labAssignedTo ? t(Digit.Utils.locale.getTransformedLocale(`PQM.QualityTestLab_${testResponse?.labAssignedTo}`)) : t("ES_TQM_NA"),
            },
            {
              key: t("ES_TQM_LABEL_TEST_SCHEDULED_ON"),
              value:
                (testResponse?.scheduledDate &&
                  `${new Date(testResponse?.scheduledDate).getDate()}/${new Date(testResponse?.scheduledDate).getMonth() + 1}/${new Date(
                    testResponse?.scheduledDate
                  ).getFullYear()}`) ||
                t("ES_TQM_NA"),
            },
            {
              key: t("ES_TQM_LABEL_STATUS"),
              value: t(`TQM_TEST_STATUS_${testResponse?.wfStatus}`) || t("ES_TQM_NA"),
            },
            {
              key: t("ES_TQM_LABEL_SLA"),
              isSla: true,
              isSuccess: Math.sign(sla) === -1 ? false : true,
              value: Math.sign(sla) === -1 ? `${Math.ceil(sla)} ${t("COMMON_DAYS_OVERDUE")}` : `${sla} ${t("COMMON_DAYS")}`,
            },
          ],
    documents: testResponse?.documents?.map((i) => {
      return { title: i?.documentUid, value: i?.fileStoreId };
    }),
    tableData: combinedData.length !== 0 ? combinedData : null,
    testSummary:
      combinedData.length !== 0
        ? [
            "",
            "",
            "",
            // "",
            t("ES_TQM_LABEL_RESULT_SUMMARY"),
            testResponse.status === "PASS" ? t("ES_TQM_LABEL_RESULT_PASS") : testResponse.status === "FAIL" ? t("ES_TQM_LABEL_RESULT_FAIL") : t("TQM_TEST_STATUS_PENDING"),
          ]
        : null,
    wfStatus: testResponse?.wfStatus,
    testResponse,
    workflowStatus: workflowData
  };
};
