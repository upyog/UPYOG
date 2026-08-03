import React, { useEffect, useState } from "react";
import { CardLabel, LabelFieldPair, Dropdown, UploadFile, Toast } from "@nudmcdgnpm/digit-ui-react-components";
import { useSelector } from "react-redux";
import { Loader } from "../components/Loader";
import Timeline from "../components/NDCTimeline";

// This component allows users to select and upload documents required for an NDC application. It fetches the list of required documents from MDMS and displays them as a list of upload fields. The component also handles file uploads and validation.

const SelectNDCDocuments = ({ t, config, onSelect, userType, formData, setError: setFormError, clearErrors: clearFormErrors, formState }) => {
  {window.location.href.includes("/citizen") ? <Timeline currentStep={2} /> : null}
  const checkFormData = useSelector((state) => state.ndc.NDCForm.formData || {});
  const stateId = Digit.ULBService.getStateId();
  const [documents, setDocuments] = useState(formData?.documents?.documents || []);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (checkFormData?.responseData?.[0]?.Documents?.length && documents.length === 0) {
      // Map API response into the structure your UploadFile expects
      const apiDocs = checkFormData?.responseData?.[0]?.Documents?.map((doc) => ({
        documentType: doc?.documentType,
        fileStoreId: doc?.documentAttachment, 
        documentUid: doc?.documentAttachment,
      }));

      setDocuments(apiDocs);
    }
  }, [checkFormData]);

  const { action = "create" } = Digit.Hooks.useQueryParams();

  // const { isLoading, data } = Digit.Hooks..usePropertyMDMS(stateId, "NDC", ["Documents"]);
 // useNDCDoc for fetching the documents from mdms as per the state and tenant id
  const {isLoading ,data } = Digit.Hooks.ndc.useNDCDoc(stateId, "NDC", "Documents");


  const ndcDocuments = data?.NDC?.Documents;

  const goNext = () => {
    onSelect(config.key, { documents, ndcDocumentsLength: ndcDocuments?.length });
  };

  useEffect(() => {
    goNext();
  }, [documents]);

  if (isLoading) {
    return <Loader />;
  }

  return (
    <div>
      {window.location.href.includes("/citizen") ? <Timeline currentStep={2} /> : null}
      {ndcDocuments?.map((document, index) => {
       
        return (
          <SelectDocument
            key={index}
            document={document}
            action={action}
            t={t}
            error={error}
            setError={setError}
            setDocuments={setDocuments}
            documents={documents}
            formData={formData}
            setFormError={setFormError}
            clearFormErrors={clearFormErrors}
            config={config}
            formState={formState}
          />
        );
      })}
      {error && <Toast isDleteBtn={true} label={error} onClose={() => setError(null)} error />}
    </div>
  );
};

function SelectDocument({ t, document: doc, setDocuments, setError, documents, setFormError, config, formState }) {
  const filteredDocument = documents?.filter((item) => item?.documentType?.includes(doc?.code))[0];
  const [getLoader, setLoader] = useState(false);

  const [file, setFile] = useState(null);
  const [uploadedFile, setUploadedFile] = useState(() => filteredDocument?.fileStoreId || null);

  function selectfile(e) {
    setFile(e.target.files[0]);
  }

  useEffect(() => {
    if (filteredDocument?.fileStoreId && !file) {
      setUploadedFile(filteredDocument.fileStoreId);
    }
  }, [filteredDocument]);

  useEffect(() => {
    if (uploadedFile) {
      setDocuments((prev) => {
        const filteredDocumentsByDocumentType = prev?.filter((item) => item?.documentType !== doc?.code);

        if (uploadedFile?.length === 0 || uploadedFile === null) {
          return filteredDocumentsByDocumentType;
        }

        const filteredDocumentsByFileStoreId = filteredDocumentsByDocumentType?.filter((item) => item?.fileStoreId !== uploadedFile);
        return [
          ...filteredDocumentsByFileStoreId,
          {
            documentType: doc?.code,
            fileStoreId: uploadedFile,
            documentUid: uploadedFile,
          },
        ];
      });
    } else if (uploadedFile === null) {
      setDocuments((prev) => prev.filter((item) => item?.documentType !== doc?.code));
    }
  }, [uploadedFile]);

  useEffect(() => {
    (async () => {
      setError(null);
      if (file) {
        setLoader(true);
        if (file.size >= 5242880) {
          setError(t("CS_MAXIMUM_UPLOAD_SIZE_EXCEEDED"));
          setLoader(false);
          if (!formState.errors[config.key]) setFormError(config.key, { type: doc?.code });
        } else {
          try {
            setUploadedFile(null);
            const response = await Digit.UploadServices.Filestorage("NDC", file, Digit.ULBService.getStateId());
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

  return (
    <div className="ndc-label-field-pair">
      <LabelFieldPair>
        <CardLabel className="card-label-smaller">
          {t(doc?.code)} {doc?.required && " *"}
        </CardLabel>
        <div className="form-field">
          <UploadFile
            id={"tl-doc"}
            onUpload={selectfile}
            onDelete={() => {
              setUploadedFile(null);
            }}
            message={uploadedFile ? `1 ${t(`CS_ACTION_FILEUPLOADED`)}` : t(`CS_ACTION_NO_FILEUPLOADED`)}
            textStyles={{ width: "100%" }}
            accept="image/*,.pdf"
            // disabled={enabledActions?.[action].disableUpload || !selectedDocument?.code}
          />
        </div>
      </LabelFieldPair>
      {getLoader && <Loader page={true} />}
    </div>
  );
}

export default SelectNDCDocuments;
