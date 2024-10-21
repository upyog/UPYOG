import React, { useEffect, useState, useContext } from "react";
// Importing required components and hooks from React and digit-ui
import { CardLabel, Dropdown, UploadFile, Toast, Loader, FormStep, LabelFieldPair, ImageUploadHandler } from "@nudmcdgnpm/digit-ui-react-components";
// Importing Timeline component
import Timeline from "../components/PTRTimeline";

const PTRSelectProofIdentity = ({ t, config, onSelect, formData, renewApplication }) => {
  // Initialize state for documents, error, enabling submit, and tracking required fields
  const [documents, setDocuments] = useState( formData?.documents?.documents || []);
  const [error, setError] = useState(null);
  const [enableSubmit, setEnableSubmit] = useState(true);
  const [checkRequiredFields, setCheckRequiredFields] = useState(false);

  // Get the state ID (tenant) from Digit service
  const stateId = Digit.ULBService.getStateId();

  // Fetch data from MDMS service related to documents for PetService
  const { isLoading, data } = Digit.Hooks.ptr.usePetMDMS(stateId, "PetService", "Documents");

  // Handle form submission
  const handleSubmit = () => {
    let document = formData.documents;
    let documentStep = { ...document, documents: documents };
    onSelect(config.key, documentStep);
  };

  // Handle skipping the current form step
  const onSkip = () => onSelect();

  // Handle Image Upload for Pet Photos
  const handleImageUpload = (ids) => {
    if (ids.length > 0) {
      // Save the image in the same structure as other documents
      const imageDoc = {
        documentType: "PET.PETPHOTO",  
        filestoreId: ids[0],  // filestoreId of the uploaded image
        documentUid: ids[0],
      };

      // Update the documents array with the new image
      setDocuments((prevDocuments) => {
        // Remove any previous PET.PETPHOTO document if it exists
        const filteredDocs = prevDocuments.filter((doc) => doc.documentType !== "PET.PETPHOTO");
        return [...filteredDocs, imageDoc];  // Add the new image doc
      });
    }
  };

  // Effect to check for required documents and enable/disable the submit button
  useEffect(() => {
    let count = 0;
    data?.PetService?.Documents?.forEach((doc) => {
      let isRequired = false;
      documents.forEach((data) => {
        if (doc.required && data?.documentType.includes(doc.code)) isRequired = true;
      });
      if (!isRequired && doc.required) count = count + 1;
    });

    // Enable or disable submit button based on whether required documents and image are selected
    if (count === 0 && documents.length > 0 && documents.some((doc) => doc.documentType === "PET.PETPHOTO")) {
      setEnableSubmit(true);  // Enable submit when all required docs and image are present
    } else {
      setEnableSubmit(false);  // Disable submit otherwise
    }
  }, [documents, checkRequiredFields]);

  return (
    <div>
      {/* Render the timeline showing current step */}
      <Timeline currentStep={4} />

      {/* Conditionally render the form or loader based on isLoading */}
      {!isLoading ? (
        <FormStep t={t} config={config} onSelect={handleSubmit} onSkip={onSkip} isDisabled={enableSubmit}>
          {/* Map through documents and render PTRSelectDocument component for each document */}
          {data?.PetService?.Documents?.map((document, index) => {
            // Render ImageUploadHandler for the pet photo field
            if (document?.code === "PET.PETPHOTO") {
              return (
                <div key={index}>
                  <CardLabel className="card-label-smaller">{t("PET_PETPHOTO")}</CardLabel>
                  <ImageUploadHandler
                    tenantId={stateId}
                    uploadedImages={documents.filter((doc) => doc.documentType === "PET.PETPHOTO").map((doc) => doc.filestoreId)}
                    onPhotoChange={handleImageUpload}
                    t={t}
                    error={error}
                    setError={setError}
                    maxImages={1}  // Restrict to one image
                  />
                </div>
              );
            } else {
              return (
                <PTRSelectDocument
                  key={index}
                  document={document}
                  t={t}
                  error={error}
                  setError={setError}
                  setDocuments={setDocuments}
                  documents={documents}
                  setCheckRequiredFields={setCheckRequiredFields}
                  renewApplication={renewApplication}
                />
              );
            }
          })}

          {/* Display any errors if present */}
          {error && <Toast label={error} onClose={() => setError(null)} error />}
        </FormStep>
      ) : (
        // Show loader while fetching the data
        <Loader />
      )}
    </div>
  );
};




function PTRSelectDocument({
  t,
  document: doc,
  setDocuments,
  setError,
  documents,
  action,
  formData,
  id,
  renewApplication
}) {
  // Filter documents to find the one matching the current document code
  const filteredDocument = documents?.filter((item) => item?.documentType?.includes(doc?.code))[0];

  // Initialize state for selected document and file to upload
  const [selectedDocument, setSelectedDocument] = useState(
    filteredDocument
      ? { ...filteredDocument, active: doc?.active === true, code: filteredDocument?.documentType }
      : doc?.dropdownData?.length === 1
        ? doc?.dropdownData[0]
        : {}
  );

  // State for handling file and uploaded file ID
  const [file, setFile] = useState(null);
  const [uploadedFile, setUploadedFile] = useState(() => filteredDocument?.filestoreId || null);

  // Handle document selection from the dropdown
  const handlePTRSelectDocument = (value) => setSelectedDocument(value);

  // Handle file selection for upload
  function selectfile(e) {
    setFile(e.target.files[0]);
  }

  // Dropdown data for the current document
  const { dropdownData } = doc;

  var dropDownData = dropdownData;

  // State to handle hidden state of the document field
  const [isHidden, setHidden] = useState(false);

  // field to upload the pictures from the renewapplication which comes if the application is renew application
  useEffect(() => {
    renewApplication?.documents?.map((row, index) => {
      row?.documentType.includes(doc?.code) ? setUploadedFile(row) : null;
    } )
  }, [renewApplication?.documents])

  // Effect to update documents when selected document or file changes
  useEffect(() => {
    if (selectedDocument?.code) {
      setDocuments((prev) => {
        const filteredDocumentsByDocumentType = prev?.filter((item) => item?.documentType !== selectedDocument?.code);

        // If no file is uploaded, remove the document from the list
        if (uploadedFile?.length === 0 || uploadedFile === null) {
          return filteredDocumentsByDocumentType;
        }

        const filteredDocumentsByFileStoreId = filteredDocumentsByDocumentType?.filter((item) => item?.fileStoreId !== uploadedFile);
        return [
          ...filteredDocumentsByFileStoreId,
          {
            documentType: selectedDocument?.code,
            filestoreId: uploadedFile,
            documentUid: uploadedFile,
          },
        ];
      });
    }
  }, [uploadedFile, selectedDocument]);

  // Effect to handle file uploads and validation
  useEffect(() => {
    (async () => {
      setError(null);
      if (file) {
        if (file.size >= 5242880) {
          // Set error if file size exceeds 5MB
          setError(t("CS_MAXIMUM_UPLOAD_SIZE_EXCEEDED"));
        } else {
          try {
            setUploadedFile(null);
            // Upload the selected file and update the uploadedFile state
            const response = await Digit.UploadServices.Filestorage("PTR", file, Digit.ULBService.getStateId());
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

  // Effect to handle isHidden state change
  useEffect(() => {
    if (isHidden) setUploadedFile(null);
  }, [isHidden]);

  return (
    <div style={{ marginBottom: "24px" }}>
      {/* Render document dropdown if the document has dropdown */}
      {doc?.hasDropdown ? (
        <LabelFieldPair>
          <CardLabel className="card-label-smaller">{t(doc?.code.replaceAll(".", "_"))} <span className="astericColor">*</span></CardLabel>
          <Dropdown
            className="form-field"
            selected={selectedDocument}
            style={{ width: "100%" }}
            option={dropDownData.map((e) => ({ ...e, i18nKey: e.code?.replaceAll(".", "_") }))}
            select={handlePTRSelectDocument}
            optionKey="i18nKey"
            t={t}
          />
        </LabelFieldPair>
      ) : (
        <LabelFieldPair>
          <CardLabel className="card-label-smaller">{t(doc?.code.replaceAll(".", "_"))} <span className="astericColor">*</span></CardLabel>
        </LabelFieldPair>
      )
      }
      {/* Render file upload field */}
      <LabelFieldPair>
        <CardLabel className="card-label-smaller"></CardLabel>
        <div className="field">
          <UploadFile
            onUpload={selectfile}
            onDelete={() => {
              setUploadedFile(null);
            }}
            id={id}
            message={uploadedFile ? `1 ${t(`CS_ACTION_FILEUPLOADED`)}` : t(`CS_ACTION_NO_FILEUPLOADED`)}
            textStyles={{ width: "100%" }}
            inputStyles={{ width: "280px" }}
            accept=".pdf, .jpeg, .jpg, .png"   // Acceptable file formats
            buttonType="button"
            error={!uploadedFile}
          />
        </div>
      </LabelFieldPair>
    </div>
  );
}

export default PTRSelectProofIdentity;
