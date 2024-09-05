import { CardText, FormComposerV2, Loader, Modal, WarningIcon } from "@egovernments/digit-ui-react-components";
import React, { Fragment, useEffect, useState } from "react";
import { updateConfig } from "./config/updateTestConfig";
import { testResultsConfig } from "./config/testResultsConfig";
import _ from "lodash";

function TestWFActions({ id, t, WFData, actionData, actionState, submitAction, testDetailsData = null, isDataLoading }) {
  const [showPopUp, setshowPopUp] = useState(null);
  const [config, setConfig] = useState(null);
  const [tempFormData, setTempFormData] = useState({});
  const [convertedData, setConvertedData] = useState({});
  const [drafted, setDrafted] = useState({});
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const { isLoading: istestLabLoading, data: testLabs } = Digit.Hooks.tqm.useCustomMDMSV2({
    tenantId: tenantId,
    schemaCode: "PQM.QualityTestLab",
  });

  useEffect(() => {
    // if (convertedData === null) {
    let __ = convertedData;
    testDetailsData?.testCriteria
      ?.map((i) => {
        return { [i?.criteriaCode]: i.resultValue };
      })
      ?.forEach((entry) => {
        const [criteriaCode, resultValue] = Object.entries(entry)[0];
        __[criteriaCode] = resultValue;
      });
    __.documents = testDetailsData?.documents?.[0]?.fileStoreId
      ? [
          [
            "Photo",
            {
              file: { name: "photo" },
              fileStoreId: {
                fileStoreId: testDetailsData?.documents?.filter((i) => i.isActive === true)?.[0]?.fileStoreId,
                tenantId: tenantId,
              },
            },
          ],
        ]
      : [];
    setConvertedData({ ...__ });
    // }
  }, [testDetailsData?.testCriteria]);

  const UpdateTestSessionScheduled = Digit.Hooks.useSessionStorage("UPDATE_TEST_SESSION_SCHEDULED", {});
  const [sessionFormDataScheduled,setSessionFormDataScheduled, clearSessionFormDataScheduled] = UpdateTestSessionScheduled

  const UpdateTestSessionPendingResults = Digit.Hooks.useSessionStorage("UPDATE_TEST_SESSION_PENDINGRESULTS", {});
  const [sessionFormDataPendingResults,setSessionFormDataPendingResults, clearSessionFormDataPendingResults] = UpdateTestSessionPendingResults
  const empty = Object.values(sessionFormDataPendingResults).every((i) => i === undefined || (Array.isArray(i) && i.length === 0));
  const emptyD = Object.values(drafted).every((i) => i === undefined || (Array.isArray(i) && i.length === 0));

  useEffect(() => {
    const emptyCheck = tempFormData ? Object.values(tempFormData).every((i) => i === undefined || (Array.isArray(i) && i.length === 0)) : null;
    const fillCheck = convertedData ? Object.values(convertedData).every((i) => i === undefined || (Array.isArray(i) && i.length === 0)) : true;
    if (actionState === "DRAFTED" && emptyCheck && !fillCheck && !_.isEmpty(convertedData) && _.isEmpty(sessionFormDataPendingResults)) {
      setDrafted({ ...convertedData });
    }
  }, [convertedData, tempFormData]);

  const onFormValueChange = (setValue, formData, errors, formState, reset, setError, clearErrors, trigger, getValues) => {
    if (actionState === "PENDINGRESULTS" || actionState === "DRAFTED") {
      if (!_.isEqual(sessionFormDataPendingResults, formData)) {
        const duplicateFormData = _.clone(formData);
        delete duplicateFormData.status;
        setSessionFormDataPendingResults({ ...sessionFormDataPendingResults, ...duplicateFormData });
        setTempFormData({ ...sessionFormDataPendingResults, ...duplicateFormData });
      }
    } else {
      if (!_.isEqual(sessionFormDataScheduled, formData)) {
        setSessionFormDataScheduled({ ...sessionFormDataScheduled, ...formData });
      }
    }
    
  }

  const isMobile = window.Digit.Utils.browser.isMobile();

  // const { isLoading: isDataLoading, data: testDetailsData } = Digit.Hooks.tqm.useSearchTest({ id: id, tenantId: tenantId });

  const { isLoading: istestCriteriaLoading, data: testCriteriaData } = Digit.Hooks.tqm.useCustomMDMSV2({
    tenantId: tenantId,
    schemaCode: "PQM.QualityCriteria",
    changeQueryName: "QualityCriteria",
  });

  function checkNonEmptyAndDefinedValues(data) {
    for (const key in data) {
      if (key !== "documents" && (data[key] === null || data[key] === undefined || data[key] === "")) {
        return false;
      }
    }
    return true;
  }

  const onSubmit = (data) => {
    if (actionState === "PENDINGRESULTS" || actionState === "DRAFTED") {
      clearSessionFormDataPendingResults();
    } else {
      clearSessionFormDataScheduled();
    }

    if ((actionState === "PENDINGRESULTS" || actionState === "DRAFTED") && !showPopUp) {
      if (checkNonEmptyAndDefinedValues(data) === false) {
        submitAction({ updateError: true });
        return;
      }
      setshowPopUp(data);
      return null;
    }
    testDetailsData.workflow = { action: "SUBMIT_SAMPLE" };
    //here add lab as well
    testDetailsData.labAssignedTo = data?.status?.code;
    submitAction(testDetailsData);
  };

  useEffect(() => {
    if (testLabs || (testDetailsData && testCriteriaData)) {
      switch (actionState) {
        case "SCHEDULED":
          return setConfig(updateConfig({ t, testLabs }));
        case "PENDINGRESULTS":
          if (testDetailsData && testCriteriaData) return setConfig(testResultsConfig({ t, testDetailsData, testCriteriaData }));
          return setConfig(null);
        case "DRAFTED":
          if (testDetailsData && testCriteriaData) return setConfig(testResultsConfig({ t, testDetailsData, testCriteriaData }));
          return setConfig(null);
        default:
          return setConfig(null);
      }
    }
  }, [actionState, testLabs, istestLabLoading, istestCriteriaLoading, testCriteriaData, testDetailsData, isDataLoading, WFData]);

  const onConfirm = () => {
    const keyf = Object.keys(showPopUp);
    const tempCriteria = testDetailsData?.testCriteria;
    keyf?.forEach((i) => {
      const _ = tempCriteria?.find((h) => h?.criteriaCode === i);
      if (_) {
        _.resultValue = showPopUp[i];
      }
    });
    if (showPopUp?.documents?.length > 0) {
      const fileStoreIds = showPopUp.documents.map(([, obj]) => obj.fileStoreId.fileStoreId);
      testDetailsData.documents.push({ fileStoreId: fileStoreIds[0] });
    }
    testDetailsData.workflow = { action: "UPDATE_RESULT" };
    submitAction(testDetailsData);
    setshowPopUp(null);
  };

  if (istestCriteriaLoading || isDataLoading || istestLabLoading) {
    return <Loader />;
  }
  if (!config) {
    return <Loader />;
  }

  if (actionState === "DRAFTED" && emptyD) {
    if (empty) {
      <Loader />;
    }
  }

  const checkNonEmptyValue = (data) => {
    for (const key in data) {
      if (key !== "documents" && data[key]) {
        return true;
      }
    }
    return false;
  };

  const handleSaveDraft = () => {
    if (checkNonEmptyValue(sessionFormDataPendingResults) === false) {
      submitAction({ draftError: true });
      return;
    }
    const keyf = Object.keys(sessionFormDataPendingResults);
    const tempCriteria = testDetailsData?.testCriteria;
    keyf?.forEach((i) => {
      const _ = tempCriteria?.find((h) => h?.criteriaCode === i);
      if (_) {
        _.resultValue = tempFormData[i];
      }
    });
    if (tempFormData?.documents?.length === 0 && testDetailsData.documents?.length > 0) {
      testDetailsData.documents = [];
    }
    if (tempFormData?.documents?.length > 0) {
      const fileStoreIds = tempFormData.documents.map(([, obj]) => obj.fileStoreId.fileStoreId);
      // testDetailsData.documents.filter((i) => i.fileStoreId !== fileStoreIds[0]).forEach((i) => (i.isActive = false));
      testDetailsData.documents = fileStoreIds[0] ? [{ fileStoreId: fileStoreIds[0], isActive: true }] : [];
    }
    testDetailsData.workflow = { action: "SAVE_AS_DRAFT" };
    submitAction(testDetailsData);
    clearSessionFormDataPendingResults();
  };

  return (
    <>
      <FormComposerV2
        config={config}
        onSubmit={onSubmit}
        label={t(actionState === "SCHEDULED" ? "ES_TQM_UPDATE_STATUS_BUTTON" : "ES_TQM_SUBMIT_TEST_RESULTS_BUTTON")}
        secondaryActionLabel={t(actionData?.find((i) => i.action === "SAVE_AS_DRAFT") ? "ES_TQM_SAVE_AS_DRAFT" : null)}
        secondaryActionStyle={{ border: "1px solid", justifyContent: "center", padding: "10px 0px", width: "100%" }}
        onSecondayActionClick={handleSaveDraft}
        checkSecondaryValidation={true}
        submitInForm={isMobile ? true : false}
        cardClassName={isMobile ? "testwf" : "employeeCard-override"}
        onFormValueChange={onFormValueChange}
        defaultValues={
          actionState === "DRAFTED" && empty && !emptyD
            ? drafted
            : actionState === "PENDINGRESULTS" || actionState === "DRAFTED"
            ? sessionFormDataPendingResults
            : sessionFormDataScheduled
        }
      />
      {showPopUp && (
        <Modal
          popUpContainerClassName="tqm-pop-wrap"
          popmoduleClassName="tqm-pop-module"
          popupModuleActionBarClass="tqm-pop-action"
          style={{ flex: 1 }}
          popupMainModuleClass="tqm-pop-main"
          headerBarMain={
            <h1 className="tqm-modal-heading">
              <WarningIcon /> {t("ES_TQM_UPDATE_MODAL_HEADER")}
            </h1>
          }
          actionCancelLabel={t("ES_TQM_UPDATE_MODAL_BACK")}
          actionCancelOnSubmit={() => {
            setshowPopUp(false);
          }}
          actionSaveLabel={t("ES_TQM_UPDATE_MODAL_SUBMIT")}
          actionSaveOnSubmit={onConfirm}
          customTheme="v-tqm"
          formId="modal-action"
        >
          <div>
            <CardText style={{ margin: 0 }}>{t("ES_TQM_UPDATE_MODAL_TEXT") + " "}</CardText>
          </div>
        </Modal>
      )}
    </>
  );
}

export default TestWFActions;