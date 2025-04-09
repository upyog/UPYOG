// Importing necessary components and hooks from external libraries and local files
import React, { useEffect, useState } from "react";
import { CardLabel, UploadFile, Toast, FormStep, LabelFieldPair, SubmitBar, DeleteIcon } from "@nudmcdgnpm/digit-ui-react-components"; // UI components for form steps, file uploads, and notifications
import Timeline from "../components/EWASTETimeline"; // Component for displaying the timeline

// Main component for uploading documents in the E-Waste module
const EWASTEDocuments = ({ t, config, onSelect, formData }) => {
  const [files, setFiles] = useState([null]); // State to manage selected files
  const [uploadedFiles, setUploadedFiles] = useState([null]); // State to manage uploaded files
  const [error, setError] = useState(null); // State to manage error messages
  const [ind, setInd] = useState(1); // State to track the number of file input fields

  const tenantId = Digit.ULBService.getStateId(); // Fetching the tenant ID
  const stateId = Digit.ULBService.getStateId(); // Fetching the state ID
  const [documents, setDocuments] = useState(formData?.documents?.documents || []); // State to manage document data

  // Effect to initialize uploaded files and file input fields when form data changes
  useEffect(() => {
    if (formData?.documents?.documents) {
      setUploadedFiles(formData.documents.documents);
      setInd(formData.documents.documents.length);
      setFiles(new Array(formData.documents.documents.length).fill(null));
    }
  }, [formData]);

  // Function to handle form submission
  const handleSubmit = () => {
    let document = formData.documents;
    let documentStep;
    documentStep = { ...document, documents: uploadedFiles.filter((file) => file !== null) }; // Filter out null files
    onSelect(config.key, documentStep); // Pass the document data to the parent component
  };

  // Function to handle skipping the step
  const onSkip = () => onSelect();

  // Function to handle file uploads
  const handleFileUpload = async (file, index) => {
    if (file.size >= 5242880) {
      // Check if the file size exceeds the limit (5 MB)
      setError(t("CS_MAXIMUM_UPLOAD_SIZE_EXCEEDED")); // Set an error message
    } else {
      try {
        // Upload the file using the Digit Upload Service
        const response = await Digit.UploadServices.Filestorage("EWASTE", file, Digit.ULBService.getStateId());
        if (response?.data?.files?.length > 0) {
          const fileStoreId = response.data.files[0].fileStoreId; // Get the file store ID
          setUploadedFiles((prev) => {
            const updatedFiles = [...prev];
            updatedFiles[index] = { filestoreId: fileStoreId, documentuuid: fileStoreId, documentType: "Photo" + (index + 1) }; // Update the uploaded files state
            return updatedFiles;
          });
        } else {
          setError(t("CS_FILE_UPLOAD_ERROR")); // Set an error message if the upload fails
        }
      } catch (err) {
        setError(t("CS_FILE_UPLOAD_ERROR")); // Set an error message if an exception occurs
      }
    }
  };

  // Function to handle file selection
  const handleFileSelect = (e, index) => {
    const file = e.target.files[0]; // Get the selected file
    setFiles((prev) => {
      const updatedFiles = [...prev];
      updatedFiles[index] = file; // Update the files state
      return updatedFiles;
    });
    handleFileUpload(file, index); // Upload the selected file
  };

  // Function to add a new file input field
  const addFileField = () => {
    setInd(ind + 1); // Increment the file input field count
    setFiles((prev) => [...prev, null]); // Add a new file input field
    setUploadedFiles((prev) => [...prev, null]); // Add a placeholder for the new file
  };

  // Function to remove a file input field
  const removeFileField = (index) => {
    setInd(ind - 1); // Decrement the file input field count

    setFiles((prev) => {
      const updatedFiles = [...prev];
      updatedFiles.splice(index, 1); // Remove the file from the files state
      return updatedFiles;
    });

    setUploadedFiles((prev) => {
      const updatedFiles = [...prev];
      updatedFiles.splice(index, 1); // Remove the file from the uploaded files state
      return updatedFiles;
    });
  };

  return (
    <div>
      {/* Display the timeline */}
      <Timeline currentStep={2} />
      <FormStep
        t={t} // Translation function
        config={config} // Configuration for the form step
        onSelect={handleSubmit} // Function to call when the "Next" button is clicked
        onSkip={onSkip} // Function to call when the "Skip" button is clicked
        isDisabled={uploadedFiles.some((file) => file === null) && files.some((file) => file === null)} // Disable the "Next" button if any file is missing
      >
        {/* Render file input fields */}
        {files.map((file, index) => (
          <LabelFieldPair key={index} style={{ marginBottom: "24px" }}>
            <CardLabel className="card-label-smaller">{t("EWASTE_PR_DOCUMENT") + (index !== 0 ? " " + (index + 1) : " 1")}</CardLabel>
            <div className="field" style={{ display: "flex", alignItems: "center" }}>
              <UploadFile
                onUpload={(e) => handleFileSelect(e, index)} // Handle file selection
                onDelete={() =>
                  setUploadedFiles((prev) => {
                    const updatedFiles = [...prev];
                    updatedFiles[index] = null; // Remove the file from the uploaded files state
                    return updatedFiles;
                  })
                }
                id={`file-upload-${index}`} // Unique ID for the file input field
                message={uploadedFiles[index] ? `1 ${t("CS_ACTION_FILEUPLOADED")}` : t("CS_ACTION_NO_FILEUPLOADED")} // Display the upload status
                textStyles={{ width: "100%" }}
                inputStyles={{ width: "280px" }}
                accept=".png" // Accept only PNG files
                buttonType="button"
                error={!uploadedFiles[index]} // Show an error if the file is missing
              />

              {/* Render a delete button for additional file input fields */}
              {index > 0 && (
                <button style={{ marginLeft: "10px" }} onClick={() => removeFileField(index)}>
                  <DeleteIcon className="delete" fill="#a82227" style={{ cursor: "pointer", marginLeft: "20px" }} />
                </button>
              )}
            </div>
          </LabelFieldPair>
        ))}

        {/* Button to add a new file input field */}
        <SubmitBar label={t("CS_COMMON_ADD")} style={{ marginBottom: "10px" }} onSubmit={addFileField} disabled={ind > 4} />

        {/* Display an error toast if an error occurs */}
        {error && <Toast label={error} onClose={() => setError(null)} error />}
      </FormStep>
    </div>
  );
};

export default EWASTEDocuments; // Exporting the component