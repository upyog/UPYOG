import { Loader, Modal, FormComposer, Dropdown } from "@upyog/digit-ui-react-components";
import React, { useState, useEffect } from "react";

import { configPTRejectApplication, configPTVerifyApplication, configPTApproverApplication, configPTAssessProperty } from "../config";
import * as predefinedConfig from "../config";

const Heading = (props) => {
  return <h1 className="heading-m">{props.label}</h1>;
};

const Close = () => (
  <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="red">
    <path d="M0 0h24v24H0V0z" fill="none" />
    <path d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12 19 6.41z" />
  </svg>
);

const CloseBtn = (props) => {
  return (
    <div className="icon-bg-secondary" onClick={props.onClick}>
      <Close />
    </div>
  );
};

const ActionModal = ({ t, action, tenantId, state, id, closeModal, submitAction, actionData, applicationData, businessService, moduleCode,assmentSearchData,userRole }) => {

  const { data: approverData, isLoading: PTALoading } = Digit.Hooks.useEmployeeSearch(
    tenantId,
    {
      roles: action?.assigneeRoles?.map?.((e) => ({ code: e })),
      isActive: true,
    },
    { enabled: !action?.isTerminateState }
  );
  const { isLoading: financialYearsLoading, data: financialYearsData } = Digit.Hooks.pt.useMDMS(
    tenantId,
    businessService,
    "FINANCIAL_YEARLS",
    {},
    {
      details: {
        tenantId: Digit.ULBService.getStateId(),
        moduleDetails: [{ moduleName: "egf-master", masterDetails: [{ name: "FinancialYear", filter: "[?(@.module == 'PT')]" }] }],
      },
    }
  );

  const [config, setConfig] = useState({});
  const [defaultValues, setDefaultValues] = useState({});
  const [approvers, setApprovers] = useState([]);
  const [selectedApprover, setSelectedApprover] = useState(null);
  const [file, setFile] = useState(null);
  const [uploadedFile, setUploadedFile] = useState(null);
  const [error, setError] = useState(null);
  const [financialYears, setFinancialYears] = useState([]);
  const [selectedFinancialYear, setSelectedFinancialYear] = useState(null);
  
  const [selectedModeofPayment, setSelectedModeofPayment] = useState(null);
  const [disableActionSubmit, setDisableActionSubmit] = useState(false);
  let modeOfPayments = [{code: 'YEARLY', name: 'YEARLY'},{code: 'HALFYEARLY', name: 'HALFYEARLY'},{code: 'QUARTERLY', name: 'QUARTERLY'}]
  useEffect(() => {
    if (financialYearsData && financialYearsData["egf-master"]) {
      console.log("=====",financialYearsData["egf-master"]?.["FinancialYear"]);
      setFinancialYears(financialYearsData["egf-master"]?.["FinancialYear"]);
    }
  }, [financialYearsData]);

  useEffect(() => {
    setApprovers(approverData?.Employees?.map((employee) => ({ uuid: employee?.uuid, name: employee?.user?.name })));
  }, [approverData]);

  function selectFile(e) {
    setFile(e.target.files[0]);
  }

  useEffect(() => {
    (async () => {
      setError(null);
      if (file) {
        if (file.size >= 5242880) {
          setError(t("CS_MAXIMUM_UPLOAD_SIZE_EXCEEDED"));
        } else {
          try {
            const response = await Digit.UploadServices.Filestorage("PT", file, Digit.ULBService.getStateId());
            if (response?.data?.files?.length > 0) {
              setUploadedFile(response?.data?.files[0]?.fileStoreId);
            } else {
              setError(t("CS_FILE_UPLOAD_ERROR"));
            }
          } catch (err) {
            setError(t("CS_FILE_UPLOAD_ERROR"));
          }
        }
      }
    })();
  }, [file]);

  function submit(data) {
    if (!action?.showFinancialYearsModal) {
      let workflow = { action: action?.action, comment: data?.comments, businessService, moduleName: moduleCode };
      workflow["assignes"] = action?.isTerminateState || !selectedApprover ? [] : [selectedApprover];
      if (uploadedFile)
        workflow["documents"] = [
          {
            documentType: action?.action + " DOC",
            fileName: file?.name,
            fileStoreId: uploadedFile,
          },
        ];
        if(userRole && userRole=='ASSIGNING_OFFICER') {
          submitAction({
            Assessment: {
              ...assmentSearchData[0],
              workflow,
            },
          });
        } else if(userRole && userRole=='EXECUTING_OFFICER') {
          submitAction({
            Appeal: {
              ...applicationData,
              workflow,
            },
          });
        } else {
          submitAction({
            Property: {
              ...applicationData,
              workflow,
            },
          });
        }
      
    } else {
      submitAction({
        customFunctionToExecute: action?.customFunctionToExecute,
        Assessment: {
          financialYear: selectedFinancialYear?.name,
          modeOfPayment: selectedModeofPayment?.name,
          propertyId: applicationData?.propertyId,
          tenantId,
          source: applicationData?.source,
          channel: applicationData?.channel,
          assessmentDate: Date.now(),
        },
      });
    }
  }
  function getCurrentFinancialYear() {
    var fiscalyear = "";
    var today = new Date();
    if ((today.getMonth() + 1) <= 3) {
      fiscalyear = (today.getFullYear() - 1) + "-" + today.getFullYear()
    } else {
      fiscalyear = today.getFullYear() + "-" + (today.getFullYear() + 1).toString().substr(-2)
    }
    return fiscalyear
  }

  useEffect(() => {
    if (action) {
      if (action?.showFinancialYearsModal) {
   
        let currentFinYr = getCurrentFinancialYear();
        // setSelectedModeofPayment(null)
        if(selectedFinancialYear && selectedFinancialYear.name == currentFinYr) {
          modeOfPayments = [{code: 'YEARLY', name: 'YEARLY'},{code: 'HALFYEARLY', name: 'HALFYEARLY'},{code: 'QUARTERLY', name: 'QUARTERLY'}]
        } else {
          modeOfPayments = [{code: 'YEARLY', name: 'YEARLY'}]

        }
        setConfig(
          configPTAssessProperty({
            t,
            action,
            financialYears,
            selectedFinancialYear,
            setSelectedFinancialYear,
            modeOfPayments,
            selectedModeofPayment,
            setSelectedModeofPayment
          })
        );
      } else {
        setConfig(
          configPTApproverApplication({
            t,
            action,
            approvers,
            selectedApprover,
            setSelectedApprover,
            selectFile,
            uploadedFile,
            setUploadedFile,
            businessService,
          })
        );
      }
    }
  }, [action, approvers, financialYears, selectedFinancialYear,modeOfPayments,selectedModeofPayment, uploadedFile]);

  return action && config.form ? (
    <Modal
      headerBarMain={<Heading label={t(config.label.heading)} />}
      headerBarEnd={<CloseBtn onClick={closeModal} />}
      actionCancelLabel={t(config.label.cancel)}
      actionCancelOnSubmit={closeModal}
      actionSaveLabel={t(config.label.submit)}
      actionSaveOnSubmit={() => {}}
      isDisabled={!action.showFinancialYearsModal ? PTALoading : !selectedFinancialYear || !selectedModeofPayment}
      formId="modal-action"
    >
      {financialYearsLoading ? (
        <Loader />
      ) : (
        <FormComposer
          config={config.form}
          noBoxShadow
          inline
          childrenAtTheBottom
          onSubmit={submit}
          defaultValues={defaultValues}
          formId="modal-action"
          isDisabled={!action.showFinancialYearsModal ? PTALoading || (!action?.isTerminateState && !selectedApprover?.uuid) : !selectedFinancialYear}
        />
      )}
    </Modal>
  ) : (
    <Loader />
  );
};

export default ActionModal;
