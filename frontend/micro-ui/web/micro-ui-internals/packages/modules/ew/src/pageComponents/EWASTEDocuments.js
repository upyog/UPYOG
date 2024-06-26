import React, { useEffect, useState } from "react";
import { CardLabel, Dropdown, UploadFile, Toast, Loader, FormStep, LabelFieldPair } from "@upyog/digit-ui-react-components";
import Timeline from "../components/EWASTETimeline";

const EWASTEDocuments = ({ t, config, onSelect, userType, formData, setError: setFormError, clearErrors: clearFormErrors, formState }) => {
  const tenantId = Digit.ULBService.getStateId();
  const [documents, setDocuments] = useState(formData?.documents?.documents || []);
  const [error, setError] = useState(null);
  const [enableSubmit, setEnableSubmit] = useState(true);
  const [checkRequiredFields, setCheckRequiredFields] = useState(false);

  // const tenantId = Digit.ULBService.getCurrentTenantId();
    const stateId = Digit.ULBService.getStateId();
  console.log("documents ::", documents)

  // const { isLoading, data } = Digit.Hooks.ptr.usePetMDMS(stateId, "PetService", "Documents");
  
  // console.log("data in documents ::", data)

  const handleSubmit = () => {
    let document = formData.documents;
    let documentStep;
    documentStep = { ...document, documents: documents };
    onSelect(config.key, documentStep);
  };
  const onSkip = () => onSelect();
  function onAdd() {}

  // useEffect(() => {
  //   let count = 0;
  //   data?.PetService?.Documents.map((doc) => {
  //     doc.hasDropdown = true;
      
  //     let isRequired = false;
  //     documents.map((data) => {
  //       if (doc.required && data?.documentType.includes(doc.code)) isRequired = true;
  //     });
  //     if (!isRequired && doc.required) count = count + 1;
  //   });
  //   if ((count == "0" || count == 0) && documents.length > 0) setEnableSubmit(false);
  //   else setEnableSubmit(true);
  // }, [documents, checkRequiredFields]);

 

  return (
    <div>
      <Timeline currentStep={2} />
        <FormStep t={t} config={config} onSelect={handleSubmit} onSkip={onSkip} isDisabled={false} onAdd={onAdd}>
          {/* {data?.PetService?.Documents?.map((document, index) => {
            return ( */}
              <EWASTESelectDocument
                // key={index}
                // document={document}
                t={t}
                error={error}
                setError={setError}
                setDocuments={setDocuments}
                documents={documents}
                setCheckRequiredFields={setCheckRequiredFields}
              />
            {/* ); */}
          {/* })} */}
          {error && <Toast label={error} onClose={() => setError(null)} error />}
        </FormStep>
    </div>
  );
};


function EWASTESelectDocument({
  t,
  document: doc,
  setDocuments,
  setError,
  documents,
  action,
  formData,
  id,
  
}) {
  // const filteredDocument = documents?.filter((item) => item?.documentType?.includes(doc?.code))[0];

  // const tenantId = Digit.ULBService.getCurrentTenantId();
  // // const [selectedDocument, setSelectedDocument] = useState(
  // //   filteredDocument
  // //     ? { ...filteredDocument, active: doc?.active === true, code: filteredDocument?.documentType }
  // //     : doc?.dropdownData?.length === 1
  // //     ? doc?.dropdownData[0]
  // //     : {}
  // // );

  const [file, setFile] = useState(null);
  // const [uploadedFile, setUploadedFile] = useState(() => filteredDocument?.filestoreId || null);
  const [uploadedFile, setUploadedFile] = useState(null);

  // const handleEWASTESelectDocument = (value) => setSelectedDocument(value);

  function selectfile(e) {
    setFile(e.target.files[0]);
  }
  // const { dropdownData } = doc;
  
  // var dropDownData = dropdownData;
   
  // const [isHidden, setHidden] = useState(false);

  

  // useEffect(() => {
  //   if (selectedDocument?.code) {
  //     setDocuments((prev) => {
  //       const filteredDocumentsByDocumentType = prev?.filter((item) => item?.documentType !== selectedDocument?.code);

  //       if (uploadedFile?.length === 0 || uploadedFile === null) {
  //         return filteredDocumentsByDocumentType;
  //       }

  //       const filteredDocumentsByFileStoreId = filteredDocumentsByDocumentType?.filter((item) => item?.fileStoreId !== uploadedFile);
  //       return [
  //         ...filteredDocumentsByFileStoreId,
  //         {
  //           documentType: selectedDocument?.code,
  //           filestoreId: uploadedFile,
  //           documentUid: uploadedFile,
  //         },
  //       ];
  //     });
  //   }
    
  // }, [uploadedFile, selectedDocument]);

  // useEffect(() => {
  //   if (action === "update") {
  //     const originalDoc = formData?.originalData?.documents?.filter((e) => e.documentType.includes(doc?.code))[0];
  //     const docType = dropDownData
  //       .filter((e) => e.code === originalDoc?.documentType)
  //       .map((e) => ({ ...e, i18nKey: e?.code?.replaceAll(".", "_") }))[0];
  //     if (!docType) setHidden(true);
  //     else {
  //       setSelectedDocument(docType);
  //       setUploadedFile(originalDoc?.fileStoreId);
  //     }
  //   } else if (action === "create") {
  //   }
  // }, []);

  useEffect(() => {
    (async () => {
      setError(null);
      if (file) {
        if (file.size >= 5242880) {
          setError(t("CS_MAXIMUM_UPLOAD_SIZE_EXCEEDED"));
          // if (!formState.errors[config.key]) setFormError(config.key, { type: doc?.code });
        } else {
          try {
            setUploadedFile(null);
            const response = await Digit.UploadServices.Filestorage("EWASTE", file, Digit.ULBService.getStateId());
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

  // useEffect(() => {
  //   if (isHidden) setUploadedFile(null);
  // }, [isHidden]);

  return (
    <div style={{ marginBottom: "24px" }}>
      <LabelFieldPair>
        <CardLabel className="card-label-smaller">{t("EWASTE_PR_DOCUMENTS")}</CardLabel>
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
            accept=".pdf, .jpeg, .jpg, .png"   //  to accept document of all kind
            buttonType="button"
            error={!uploadedFile}
          />
        </div>
      </LabelFieldPair>
    </div>
  );
}

export default EWASTEDocuments;
