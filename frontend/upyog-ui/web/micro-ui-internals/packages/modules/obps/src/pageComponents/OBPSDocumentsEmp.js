import React, { useEffect, useState } from "react";
import { CardLabel, LabelFieldPair, Toast, Loader, MultiUploadWrapper } from "@nudmcdgnpm/digit-ui-react-components";
import { useLocation } from "react-router-dom";

const OBPSDocumentsEmp = ({ t, config, onSelect, userType, formData, setError: setFormError, clearErrors: clearFormErrors, formState, index: indexx, unit, setFieldReports, documentList }) => {
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const stateId = Digit.ULBService.getStateId();

  const [documents, setDocuments] = useState(() => {
    if (unit?.Documents && Array.isArray(unit.Documents) && unit.Documents.length > 0) {
      return unit.Documents;
    }
    if (formData?.FieldReports?.[indexx]?.Documents && Array.isArray(formData.FieldReports[indexx].Documents)) {
      return formData.FieldReports[indexx].Documents;
    }
    if (formData?.FeildReports?.[indexx]?.Documents && Array.isArray(formData.FeildReports[indexx].Documents)) {
      return formData.FeildReports[indexx].Documents;
    }
    try {
      const stored = JSON.parse(sessionStorage.getItem("INSPECTION_DATA") || "[]");
      if (Array.isArray(stored) && stored?.[indexx]?.Documents?.length) {
        return stored[indexx].Documents;
      }
    } catch (e) {}
    return [];
  });

  const [error, setError] = useState(null);

  let action = "create";
  const { pathname } = useLocation();
  const isEditScreen = pathname.includes("/modify-application/");
  if (isEditScreen) action = "update";

  const { isLoading, data: documentsData } = Digit.Hooks.pt.usePropertyMDMS(stateId, "TradeLicense", ["documentObj"]);

  let finalTlDocumentsList = [];
  documentList && documentList.map((doc) => {
    finalTlDocumentsList.push({ documentType: doc.code, code: doc.code });
  });

  useEffect(() => {
    if (setFieldReports) {
      setFieldReports((prev) => {
        const nextReports = Array.isArray(prev) ? [...prev] : [];
        if (nextReports[indexx]) {
          nextReports[indexx] = {
            ...nextReports[indexx],
            Documents: documents,
          };
        } else {
          nextReports[indexx] = {
            ...(unit || {}),
            Documents: documents,
          };
        }

        // Always sync latest state to sessionStorage INSPECTION_DATA immediately
        try {
          const stored = JSON.parse(sessionStorage.getItem("INSPECTION_DATA") || "[]");
          const nextStored = Array.isArray(stored) && stored.length > 0 ? [...stored] : [...nextReports];
          if (nextStored[indexx]) {
            nextStored[indexx] = {
              ...nextStored[indexx],
              Documents: documents,
            };
          } else {
            nextStored[indexx] = {
              ...(unit || {}),
              Documents: documents,
            };
          }
          sessionStorage.setItem("INSPECTION_DATA", JSON.stringify(nextStored));
        } catch (e) {
          console.error("Error updating INSPECTION_DATA in sessionStorage", e);
        }

        return nextReports;
      });
    }
  }, [documents]);

  if (isLoading) {
    return <Loader />;
  }

  return (
    <div>
      {finalTlDocumentsList?.map((document, index) => {
        return (
          <div key={index}>
            <SelectDocument
              document={document}
              action={action}
              t={t}
              id={`obps-doc-${index}`}
              error={error}
              setError={setError}
              setDocuments={setDocuments}
              documents={documents}
              formData={formData}
              setFormError={setFormError}
              clearFormErrors={clearFormErrors}
              config={config}
              formState={formState}
              stateId={stateId}
            />
          </div>
        );
      })}
      {error && <Toast label={error} onClose={() => setError(null)} error />}
    </div>
  );
};

function SelectDocument({
  t,
  document: doc,
  setDocuments,
  error,
  setError,
  documents,
  action,
  formData,
  setFormError,
  clearFormErrors,
  config,
  formState,
  fromRawData,
  key,
  id,
  stateId = Digit.ULBService.getStateId(),
}) {
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const docKey = doc?.documentType?.replaceAll(".", "_");
  const filteredDocument = documents?.filter((item) => item?.documentType === docKey || item?.documentType === doc?.documentType);

  const [selectedDocument, setSelectedDocument] = useState(() => (filteredDocument?.length > 0 ? { documentType: docKey } : ""));
  const [file, setFile] = useState(null);
  const [uploadedFile, setUploadedFile] = useState(() => filteredDocument?.[0]?.fileStoreId || null);
  const [isHidden, setHidden] = useState(false);
  const allowedFileTypes = /(.*?)(jpg|jpeg|png|image|pdf)$/i;

  const initialUploadedState = (filteredDocument || []).map((d) => [
    d.fileName || d.fileStoreId,
    {
      file: { name: d.fileName || d.fileStoreId },
      fileStoreId: { fileStoreId: d.fileStoreId, tenantId: d.tenantId || tenantId },
    },
  ]);

  const addError = () => {
    let type = formState?.errors?.[config?.key]?.type;
    if (!Array.isArray(type)) type = [];
    if (!type.includes(doc?.documentType)) {
      type.push(doc?.documentType);
      setFormError && setFormError(config?.key, { type });
    }
  };

  const removeError = () => {
    let type = formState?.errors?.[config?.key]?.type;
    if (!Array.isArray(type)) type = [];
    if (type.includes(doc?.documentType)) {
      type = type.filter((e) => e !== doc?.documentType);
      if (!type.length) {
        clearFormErrors && clearFormErrors(config?.key);
      } else {
        setFormError && setFormError(config?.key, { type });
      }
    }
  };

  function getData(e, key) {
    let data = Object.fromEntries(e || []);
    let newArr = Object.values(data);

    const validNewFiles = newArr.filter((ob) => ob?.fileStoreId?.fileStoreId);

    if (validNewFiles.length > 0) {
      const newDocsForThisKey = validNewFiles.map((ob) => ({
        documentType: key,
        fileStoreId: ob?.fileStoreId?.fileStoreId,
        tenantId: ob?.fileStoreId?.tenantId || tenantId,
        fileName: ob?.file?.name || "",
      }));

      setSelectedDocument({ documentType: key });
      setUploadedFile(validNewFiles[0]?.fileStoreId?.fileStoreId);
      setFile(validNewFiles[0]?.file || null);

      setDocuments((prev) => {
        const otherDocs = (prev || []).filter(
          (item) => item?.documentType !== key && item?.documentType !== doc?.documentType
        );
        return [...otherDocs, ...newDocsForThisKey];
      });

      removeError();
    } else {
      setSelectedDocument("");
      setUploadedFile(null);
      setFile(null);

      setDocuments((prev) => {
        return (prev || []).filter(
          (item) => item?.documentType !== key && item?.documentType !== doc?.documentType
        );
      });

      addError();
    }
  }

  useEffect(() => {
    if (!isHidden) {
      const isRenewal = window.location.href.includes("renew-application-details");
      if (!isRenewal) {
        const hasDoc = documents?.some(
          (item) => item?.documentType === docKey || item?.documentType === doc?.documentType
        );
        if (hasDoc) {
          removeError();
        } else {
          addError();
        }
      }
    } else {
      removeError();
    }
  }, [isHidden, doc?.documentType]);

  useEffect(() => {
    if (doc && formData?.documents?.documents?.length > 0) {
      for (let i = 0; i < formData?.documents?.documents?.length; i++) {
        if (doc?.documentType === formData?.documents?.documents?.[i]?.documentType) {
          setSelectedDocument({ documentType: formData?.documents?.documents?.[i]?.documentType, id: formData?.documents?.documents?.[i]?.id });
          setUploadedFile(formData?.documents?.documents?.[i]?.fileStoreId);
        }
      }
    }
  }, [doc]);

  return (
    <div className="obps-obpsdocuments-emp-bottom-spacing">
      <LabelFieldPair className="obps-inspection-report-fullwidth">
        <CardLabel className="card-label-smaller obps-inspection-report-fullwidth">
          {doc?.documentType !== "OLDLICENCENO" ?
            `${t(`${doc?.documentType.replaceAll(".", "_")}`)}*` :
            `${t(`${doc?.documentType.replaceAll(".", "_")}`)}`}
        </CardLabel>
        <div className="field obps-inspection-report-fullwidth">
          <MultiUploadWrapper
            module="BPA"
            tenantId={stateId}
            getFormState={(e) => getData(e, docKey)}
            t={t}
            setuploadedstate={initialUploadedState}
            allowedFileTypesRegex={allowedFileTypes}
            allowedMaxSizeInMB={5}
            acceptFiles="image/*, .pdf, .png, .jpeg, .jpg"
          />
        </div>
      </LabelFieldPair>
    </div>
  );
}

export default OBPSDocumentsEmp;
