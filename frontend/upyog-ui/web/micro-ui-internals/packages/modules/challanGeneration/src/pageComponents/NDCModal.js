import { Modal, FormComposer, Toast } from "@nudmcdgnpm/digit-ui-react-components";
import React, { useState, useEffect } from "react";
import { Loader } from "../components/Loader";

import { ModalConfig } from "../config/ModalConfig";

/**
 * NDCModal component:
 * - Displays modal for workflow actions
 * - Handles amount input and document upload
 */

const Heading = (props) => {
  return <h1 className="heading-m">{props.label}</h1>;
};

const Close = () => (
  <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="#FFFFFF">
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

const NDCModal = ({ t, action, closeModal, submitAction, showErrorToast, errorOne, closeToastOne, getChallanData }) => {
  const [config, setConfig] = useState({});
  const [getAmount, setAmount] = useState();
  const [error, setError] = useState();
  const [uploadedFile, setUploadedFile] = useState(null);
  const [file, setFile] = useState(null);
  const [loader, setLoader] = useState(false);

  function submit(data) {
    const payload = {
      amount: getAmount,
      wfDocuments: uploadedFile
        ? [
            {
              documentType: action?.action + " DOC",
              fileName: file?.name,
              fileStoreId: uploadedFile,
            },
          ]
        : null,

      // file: file
    };
    submitAction(payload);
  }

  useEffect(() => {
    if (action) {
      setConfig(
        ModalConfig({
          t,
          action,
          setAmount,
          getChallanData,
          error,
          setError,
          selectFile,
          uploadedFile,
          setUploadedFile,
        })
      );
    }
  }, [action, uploadedFile]);

  function selectFile(e) {
    setFile(e.target.files[0]);
  }

  useEffect(() => {
    (async () => {
      if (file) {
        if (file.size >= 5242880) {
          setError(t("CS_MAXIMUM_UPLOAD_SIZE_EXCEEDED"));
        } else {
          setLoader(true);
          try {
            const response = await Digit.UploadServices.Filestorage("PT", file, Digit.ULBService.getStateId());
            setLoader(false);
            if (response?.data?.files?.length > 0) {
              setUploadedFile(response?.data?.files[0]?.fileStoreId);
            } else {
              setError(t("CS_FILE_UPLOAD_ERROR"));
            }
          } catch (err) {
            setLoader(false);
            setError(t("CS_FILE_UPLOAD_ERROR"));
          }
        }
      }
    })();
  }, [file]);

  if (!action || !config.form) return null;

  return (
    <Modal
       className="BPAemployeeCard bpa-workflow-modal-form"
      headerBarMain={<Heading label={t(config.label.heading)} />}
      headerBarEnd={<CloseBtn onClick={closeModal} />}
      actionCancelLabel={t(config.label.cancel)}
      actionCancelOnSubmit={closeModal}
      actionSaveLabel={t(config.label.submit)}
      actionSaveOnSubmit={() => {}}
      formId="modal-action"
    >
      <FormComposer config={config.form} noBoxShadow inline childrenAtTheBottom onSubmit={submit} formId="modal-action" />
      {showErrorToast && <Toast error={true} label={errorOne} isDleteBtn={true} onClose={closeToastOne} />}

      {loader && <Loader />}
    </Modal>
  );
};

export default NDCModal;
