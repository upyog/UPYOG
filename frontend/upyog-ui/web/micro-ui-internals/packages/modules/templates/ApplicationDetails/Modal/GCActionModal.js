import { Loader, Modal, FormComposer } from "@nudmcdgnpm/digit-ui-react-components";
import React, { useState, useEffect } from "react";
import { configGCApproverApplication } from "../config";

const Heading = (props) => <h1 className="heading-m">{props.label}</h1>;

const Close = () => (
  <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="#FFFFFF">
    <path d="M0 0h24v24H0V0z" fill="none" />
    <path d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12 19 6.41z" />
  </svg>
);

const CloseBtn = (props) => (
  <div className="icon-bg-secondary" onClick={props.onClick}>
    <Close />
  </div>
);

const GCActionModal = ({ t, action, tenantId, closeModal, submitAction, applicationData, businessService, moduleCode }) => {
  const [config, setConfig] = useState({});
  const [file, setFile] = useState(null);
  const [uploadedFile, setUploadedFile] = useState(null);
  const [error, setError] = useState(null);

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
            const response = await Digit.UploadServices.Filestorage("GC", file, tenantId);
            if (response?.data?.files?.length > 0) {
              setUploadedFile(response.data.files[0].fileStoreId);
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

  useEffect(() => {
    if (action) {
      setConfig(
        configGCApproverApplication({ t, action, selectFile, uploadedFile, setUploadedFile })
      );
    }
  }, [action, uploadedFile]);

  function submit(data) {
    const workflowAction=action?.action;
    const isOnlyWorkflowCall=true;
    const workflow = {
      action: action?.action,
      comments: data?.comments,
      assignes: [],
      businessService,
      moduleName: moduleCode,
    };
    if (uploadedFile) {
      workflow.documents = [
        {
          documentType: action?.action + " DOC",
          fileName: file?.name,
          fileStoreId: uploadedFile,
        },
      ];
    }
    const payload = {
      garbageAccounts: [
        {
          ...applicationData,
          workflowAction,
          isOnlyWorkflowCall,
          workflow,
        },
      ],
    };
    submitAction(payload);
  }

  return action && config.form ? (
    <Modal
      headerBarMain={<Heading label={t(config.label.heading)} />}
      headerBarEnd={<CloseBtn onClick={closeModal} />}
      actionCancelLabel={t(config.label.cancel)}
      actionCancelOnSubmit={closeModal}
      actionSaveLabel={t(config.label.submit)}
      actionSaveOnSubmit={() => {}}
      formId="modal-action"
    >
      {error && <p style={{ color: "red" }}>{error}</p>}
      <FormComposer
        config={config.form}
        noBoxShadow
        inline
        childrenAtTheBottom
        onSubmit={submit}
        defaultValues={{}}
        formId="modal-action"
      />
    </Modal>
  ) : (
    <Loader />
  );
};

export default GCActionModal;
