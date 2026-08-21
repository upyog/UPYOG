import "../../css/ndc.css";
import React, { useEffect, useState } from "react";
import { CardLabel, UploadFile, Toast } from "@nudmcdgnpm/digit-ui-react-components";
import { useSelector } from "react-redux";
import { Loader } from "../components/Loader";
import Timeline from "../components/NDCTimeline";

const SelectNDCDocuments = ({ t, config, onSelect, userType, formData, setError: setFormError, clearErrors: clearFormErrors, formState }) => {
  const checkFormData = useSelector((state) => state.ndc.NDCForm.formData || {});

  const stateId = Digit.ULBService.getStateId();

  const [documents, setDocuments] = useState(formData?.documents?.documents || []);

  const [error, setError] = useState(null);

  useEffect(() => {
    if (checkFormData?.responseData?.[0]?.Documents?.length && documents.length === 0) {
      const apiDocs = checkFormData?.responseData?.[0]?.Documents?.map((doc) => ({
        documentType: doc?.documentType,
        fileStoreId: doc?.documentAttachment,
        documentUid: doc?.documentAttachment,
      }));

      setDocuments(apiDocs);
    }
  }, [checkFormData]);

  const { action = "create" } = Digit.Hooks.useQueryParams();

  const { isLoading, data } = Digit.Hooks.ndc.useNDCDoc(stateId, "NDC", "Documents");

  const ndcDocuments = data?.NDC?.Documents;

  const goNext = () => {
    onSelect(config.key, {
      documents,
      ndcDocumentsLength: ndcDocuments?.length,
    });
  };

  useEffect(() => {
    goNext();
  }, [documents]);

  if (isLoading) {
    return <Loader />;
  }

  return (
    <div className="ndc-css-805e0033"
      
    >
      {/* Timeline */}
      {window.location.href.includes("/citizen") ? <Timeline currentStep={2} /> : null}

      {/* Main Documents Card */}
      <div className="ndc-css-40b8fae8"
        
      >
        {/* Header */}
        <div className="ndc-css-9acc18f3"
          
        >
          <h1 className="ndc-css-2ac5b85c"
            
          >
            {t("NDC_PROOF_OF_DOCUMENTS") || "Proof of Documents"}
          </h1>

          <div className="ndc-css-67ce6330"
            
          >
            {t("NDC_SUPPORTED_FORMATS") || "Supported formats: JPG, PNG for photo and JPG, PNG, PDF for other documents"}
          </div>

          <div className="ndc-css-323d1bd8"
            
          >
            {t("NDC_MAXIMUM_UPLOAD_SIZE") || "Maximum upload size is 5 MB"}
          </div>
        </div>

        {/* Documents */}
        <div className="ndc-css-8b064ad7"
          
        >
          {ndcDocuments?.map((document, index) => {
            return (
              <SelectDocument
                key={`${document?.code || "document"}-${index}`}
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
        </div>

        {/* Error */}
        {error && <Toast isDleteBtn={true} label={error} onClose={() => setError(null)} error />}
      </div>
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

          if (!formState.errors[config.key]) {
            setFormError(config.key, {
              type: doc?.code,
            });
          }
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
    <div className="ndc-css-6bf112df"
      
    >
      {/* Document Label */}
      <div className="ndc-css-a82d9187"
        
      >
        <CardLabel className="ndc-css-c851dfe0"
          
        >
          {t(doc?.code)}

          {doc?.required && (
            <span className="ndc-css-326975fd"
              
            >
              *
            </span>
          )}
        </CardLabel>
      </div>

      {/* Upload */}
      <div className="ndc-css-805e0033"
        
      >
        <UploadFile
          id={`ndc-doc-${doc?.code}`}
          onUpload={selectfile}
          onDelete={() => {
            setUploadedFile(null);
          }}
          message={uploadedFile ? `1 ${t("CS_ACTION_FILEUPLOADED")}` : t("CS_ACTION_NO_FILEUPLOADED")}
          textStyles={{
            width: "100%",
          }}
          accept="image/*,.pdf"
        />
      </div>

      {/* Loader */}
      {getLoader && <Loader page={true} />}
    </div>
  );
}

export default SelectNDCDocuments;
