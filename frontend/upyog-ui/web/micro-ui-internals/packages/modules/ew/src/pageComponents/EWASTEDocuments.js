import React, { useEffect, useState } from "react";
import { CardLabel, UploadFile, Toast, FormStep, LabelFieldPair, SubmitBar, DeleteIcon } from "@upyog/digit-ui-react-components";
import Timeline from "../components/EWASTETimeline";

/**
 * Manages document upload functionality for the E-Waste module.
 * Allows users to upload, preview, and manage multiple document attachments.
 *
 * @param {Object} props Component properties
 * @param {Function} props.t Translation function
 * @param {Object} props.config Form configuration object
 * @param {Function} props.onSelect Callback for form submission
 * @param {Object} props.formData Existing form data
 * @returns {JSX.Element} Document upload form interface
 */
const EWASTEDocuments = ({ t, config, onSelect, formData }) => {
  const [files, setFiles] = useState([null]);
  const [uploadedFiles, setUploadedFiles] = useState([null]);
  const [error, setError] = useState(null);
  const [ind, setInd] = useState(1);

  const tenantId = Digit.ULBService.getStateId();
  const stateId = Digit.ULBService.getStateId();
  const [documents, setDocuments] = useState(formData?.documents?.documents || []);

  /**
   * Initializes document states from existing form data
   */
  useEffect(() => {
    if (formData?.documents?.documents) {
      setUploadedFiles(formData.documents.documents);
      setInd(formData.documents.documents.length);
      setFiles(new Array(formData.documents.documents.length).fill(null));
    }
  }, [formData]);

  /**
   * Processes form submission by filtering and formatting document data
   */
  const handleSubmit = () => {
    let document = formData.documents;
    let documentStep = { ...document, documents: uploadedFiles.filter((file) => file !== null) };
    onSelect(config.key, documentStep);
  };

  const onSkip = () => onSelect();

  /**
   * Handles file upload process including validation and storage
   * 
   * @param {File} file File object to upload
   * @param {number} index Position in the upload array
   */
  const handleFileUpload = async (file, index) => {
    if (file.size >= 5242880) {
      setError(t("CS_MAXIMUM_UPLOAD_SIZE_EXCEEDED"));
    } else {
      try {
        const response = await Digit.UploadServices.Filestorage("EWASTE", file, Digit.ULBService.getStateId());
        if (response?.data?.files?.length > 0) {
          const fileStoreId = response.data.files[0].fileStoreId;
          setUploadedFiles((prev) => {
            const updatedFiles = [...prev];
            updatedFiles[index] = { filestoreId: fileStoreId, documentuuid: fileStoreId, documentType: "Photo" + (index + 1) };
            return updatedFiles;
          });
        } else {
          setError(t("CS_FILE_UPLOAD_ERROR"));
        }
      } catch (err) {
        setError(t("CS_FILE_UPLOAD_ERROR"));
      }
    }
  };

  /**
   * Processes file selection and triggers upload
   * 
   * @param {Event} e File input change event
   * @param {number} index Position in the files array
   */
  const handleFileSelect = (e, index) => {
    const file = e.target.files[0];
    setFiles((prev) => {
      const updatedFiles = [...prev];
      updatedFiles[index] = file;
      return updatedFiles;
    });
    handleFileUpload(file, index);
  };

  /**
   * Adds a new file upload field to the form
   */
  const addFileField = () => {
    setInd(ind + 1);
    setFiles((prev) => [...prev, null]);
    setUploadedFiles((prev) => [...prev, null]);
  };

  /**
   * Removes a file upload field from the form
   * 
   * @param {number} index Position of field to remove
   */
  const removeFileField = (index) => {
    setInd(ind - 1);
    setFiles((prev) => {
      const updatedFiles = [...prev];
      updatedFiles.splice(index, 1);
      return updatedFiles;
    });
    setUploadedFiles((prev) => {
      const updatedFiles = [...prev];
      updatedFiles.splice(index, 1);
      return updatedFiles;
    });
  };

  return (
    <div>
      <Timeline currentStep={2} />
      <FormStep
        t={t}
        config={config}
        onSelect={handleSubmit}
        onSkip={onSkip}
        isDisabled={uploadedFiles.some((file) => file === null) && files.some((file) => file === null)}
      >
        {files.map((file, index) => (
          <LabelFieldPair key={index} style={{ marginBottom: "24px" }}>
            <CardLabel className="card-label-smaller">{t("EWASTE_PR_DOCUMENT") + (index !== 0 ? " " + (index + 1) : " 1")}</CardLabel>
            <div className="field" style={{ display: "flex", alignItems: "center" }}>
              <UploadFile
                onUpload={(e) => handleFileSelect(e, index)}
                onDelete={() =>
                  setUploadedFiles((prev) => {
                    const updatedFiles = [...prev];
                    updatedFiles[index] = null;
                    return updatedFiles;
                  })
                }
                id={`file-upload-${index}`}
                message={uploadedFiles[index] ? `1 ${t("CS_ACTION_FILEUPLOADED")}` : t("CS_ACTION_NO_FILEUPLOADED")}
                textStyles={{ width: "100%" }}
                inputStyles={{ width: "280px" }}
                accept=".png"
                buttonType="button"
                error={!uploadedFiles[index]}
              />
              {index > 0 && (
                <button style={{ marginLeft: "10px" }} onClick={() => removeFileField(index)}>
                  <DeleteIcon className="delete" fill="#a82227" style={{ cursor: "pointer", marginLeft: "20px" }} />
                </button>
              )}
            </div>
          </LabelFieldPair>
        ))}
        <SubmitBar label={t("CS_COMMON_ADD")} style={{ marginBottom: "10px" }} onSubmit={addFileField} disabled={ind > 4} />
        {error && <Toast label={error} onClose={() => setError(null)} error />}
      </FormStep>
    </div>
  );
};

export default EWASTEDocuments;