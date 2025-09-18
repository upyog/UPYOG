import React, { useEffect, useState } from "react";
import { CardLabel,Toast, FormStep, LabelFieldPair, SubmitBar, DeleteIcon } from "@upyog/digit-ui-react-components";
import UploadFile from "./UploadFile";
import { styles } from "../utils/styles";

// Document component for handling file uploads in the application 
// It allows users to upload multiple files, validate them, and manage the upload state

const Document = ({ t, config, onSelect, formData, setDocumentUploaded }) => {
  const [files, setFiles] = useState([null]);
  const [uploadedFiles, setUploadedFiles] = useState([null]);
  const [error, setError] = useState(null);
  const [ind, setInd] = useState(1);
  const user = Digit.UserService.getUser().info; // Get the user info
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



  useEffect(() => {
    setDocumentUploaded(uploadedFiles.filter((file) => file !== null));
  }, [uploadedFiles]);

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
        const response = await Digit.UploadServices.Filestorage("PGR", file, Digit.ULBService.getStateId());
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
       {files.map((file, index) => (
  <div key={index} style={styles.documentContainer}>
    <CardLabel className="card-label-smaller" style={styles.cardLabelStyle} >
      {t("PHOTO_UPLOAD") + (index !== 0 ? " " + (index + 1) : " 1")}
    </CardLabel>

    <div className="field" style={styles.documentField}>
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
        textStyles={{ width: "86%" }}
        inputStyles={{ width: "86%" }}
        accept=".png"
        buttonType="button"
        error={!uploadedFiles[index]}
      />

      <div style={{ display: "flex", alignItems: "center" }}>
        {uploadedFiles[index] && index === files.length - 1 && (
          <button 
            style={styles.addButton} 
            onClick={addFileField}
            type="button"
            disabled={ind > 4}
          >
            {t("CS_COMMON_ADD")}
          </button>
        )}

        {(!uploadedFiles[index] || index !== files.length - 1) && index>0 && (
          <button 
            onClick={() => removeFileField(index)} 
            style={styles.deleteButton}
            type="button"
          >
            <DeleteIcon className="delete" fill="#902434" style={styles.deleteIcon} />
          </button>
        )}
      </div>
    </div>
  </div>
))}
{error && <Toast label={error} onClose={() => setError(null)} error />}

    </div>
  );
};

export default Document;