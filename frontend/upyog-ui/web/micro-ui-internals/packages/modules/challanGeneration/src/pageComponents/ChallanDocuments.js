import React, { use, useEffect, useState } from "react";
import { CardLabel, Dropdown, UploadFile, Toast, FormStep, LabelFieldPair, ActionBar } from "@nudmcdgnpm/digit-ui-react-components";
import { Loader } from "../components/Loader";
import exifr from "exifr";

/**
 * ChallanDocuments component:
 * - Handles document upload for challan
 * - Supports validation, file upload, and EXIF extraction
 */



const ChallanDocuments = ({
  t,
  config,
  onSelect,
  userType,
  formData,
  setError: setFormError,
  clearErrors: clearFormErrors,
  formState,
  data,
  isLoading,
  error,
  setError,
}) => {
  const [documents, setDocuments] = useState(formData?.documents?.documents || []);
  const [enableSubmit, setEnableSubmit] = useState(true);
  const [checkRequiredFields, setCheckRequiredFields] = useState(false);
  const tenantId = window.location.href.includes("employee") ? Digit.ULBService.getCurrentTenantId() : localStorage.getItem("CITIZEN.CITY");

  const handleSubmit = () => {
    let document = formData.documents;
    let documentStep;
    documentStep = { ...document, documents: documents };
    onSelect(config.key, documentStep);
  };

  const onSkip = () => onSelect();
  function onAdd() { }

  useEffect(() => {
    let count = 0;
    data?.Challan?.Documents?.map((doc) => {
      doc.hasDropdown = true;

      let isRequired = false;
      documents?.map((data) => {
        if (doc.required && data?.documentType.includes(doc.code)) isRequired = true;
      });
      if (!isRequired && doc.required) count = count + 1;
    });
    if ((count == "0" || count == 0) && documents?.length > 0) setEnableSubmit(false);
    else setEnableSubmit(true);
  }, [documents, checkRequiredFields]);

  return (
    <div>
      {/* <Timeline currentStep={4} /> */}
      {!isLoading ? (
        <div>
          {data?.Challan?.Documents?.map((document, index) => {
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
                handleSubmit={handleSubmit}
              />
            );
          })}
          {error && <Toast isDleteBtn={true} label={error} onClose={() => setError(null)} error />}
          <ActionBar>
              <button onClick={onSkip} className="btn-secondary cg-btn-margin-right">
                {t("COMMON_SKIP")}
              </button>
            <button
              onClick={handleSubmit}
              className="btn-primary"
              disabled={enableSubmit}
            >
              {t("COMMON_NEXT")}
            </button>
          </ActionBar>
        </div>
      ) : (
        <Loader />
      )}
    </div>
  );
};

function PTRSelectDocument({ t, document: doc, setDocuments, setError, documents, action, formData, handleSubmit, id }) {
  const filteredDocument = documents?.filter((item) => item?.documentType?.includes(doc?.code))[0];

  const tenantId = Digit.ULBService.getCurrentTenantId();
  const [selectedDocument, setSelectedDocument] = useState(() => {
    if (filteredDocument) {
      const match = doc?.dropdownData?.find((e) => e.code === filteredDocument.documentType);
      return match ? { ...match, i18nKey: match.code?.replaceAll(".", "_") } : {};
    }
    if (doc?.dropdownData?.length === 1) {
      const onlyOption = doc.dropdownData[0];
      return { ...onlyOption, i18nKey: onlyOption.code?.replaceAll(".", "_") };
    }
    return {};
  });

  const [file, setFile] = useState(null);
  const [uploadedFile, setUploadedFile] = useState(() => filteredDocument?.filestoreId || null);

  const handlePTRSelectDocument = (value) => setSelectedDocument(value);

  async function selectfile(e) {
  const file = e.target.files[0];
  if (!file) return;

  const fileType = file.type.toLowerCase();

  // ✅ Case 1: Image files (JPEG/JPG/PNG)
  if (
    fileType.includes("image/jpeg") ||
    fileType.includes("image/jpg") ||
    fileType.includes("image/png")
  ) {
    try {
      let latitude = null;
      let longitude = null;

      // ✅ Extract GPS safely using exifr
      const gpsData = await exifr.gps(file);

      if (gpsData) {
        latitude = gpsData.latitude || null;
        longitude = gpsData.longitude || null;
      } else {
        console.warn("⚠️ No GPS data found in image");
      }

      // ✅ Save file + coordinates
      setFile(file);
      updateDocument(selectedDocument, { latitude, longitude });

    } catch (err) {
      console.warn("EXIF extraction failed:", err);

      // fallback (no crash)
      setFile(file);
      updateDocument(selectedDocument, {});
    }
  }

  // ✅ Case 2: PDFs or other file types
  else {
    setFile(file);
    updateDocument(selectedDocument, {});
  }
}

  // helper function to avoid repeating code
  function updateDocument(selectedDocument, extraFields = {}) {
    setDocuments((prev = []) => {
      const updated = prev.map((item) => (item?.documentType === selectedDocument?.code ? { ...item, ...extraFields } : item));

      if (!updated.some((i) => i?.documentType === selectedDocument?.code)) {
        updated.push({
          documentType: selectedDocument?.code,
          filestoreId: null,
          documentUid: null,
          ...extraFields,
        });
      }

      return updated;
    });
  }

  function convertDMSToDD(dms, ref) {
    const [degrees, minutes, seconds] = dms;
    let dd = degrees + minutes / 60 + seconds / 3600;
    if (ref === "S" || ref === "W") dd *= -1;
    return dd;
  }

  const { dropdownData } = doc;

  var dropDownData = dropdownData;

  const [isHidden, setHidden] = useState(false);
  const [getLoading, setLoading] = useState(false);
  useEffect(() => {
    if (selectedDocument?.code) {
      setDocuments((prev) => {
        return prev.map((item) => {
          if (item?.documentType === selectedDocument?.code) {
            // ✅ Preserve existing fields (like latitude, longitude)
            return {
              ...item,
              filestoreId: uploadedFile,
              documentUid: uploadedFile,
            };
          }
          return item;
        });
      });
    }
  }, [uploadedFile, selectedDocument]);

  useEffect(() => {
    if (documents?.length > 0) {
      handleSubmit();
    }
  }, [documents]);

  useEffect(() => {
    if (action === "update") {
      const originalDoc = formData?.originalData?.documents?.filter((e) => e.documentType.includes(doc?.code))[0];
      const docType = dropDownData
        .filter((e) => e.code === originalDoc?.documentType)
        .map((e) => ({ ...e, i18nKey: e?.code?.replaceAll(".", "_") }))[0];
      if (!docType) setHidden(true);
      else {
        setSelectedDocument(docType);
        setUploadedFile(originalDoc?.fileStoreId);
      }
    } else if (action === "create") {
    }
  }, []);

  useEffect(() => {
    if (!doc?.hasDropdown) {
      setSelectedDocument({ code: doc?.code, i18nKey: doc?.code?.replaceAll(".", "_") });
      // setHidden(true);
    }
  }, []);

  useEffect(() => {
    (async () => {
      setError(null);
      if (file) {
        setLoading(true);
        if (file.size >= 5242880) {
          setError(t("CS_MAXIMUM_UPLOAD_SIZE_EXCEEDED"));
        } else {
          try {
            setUploadedFile(null);
            const response = await Digit.UploadServices.Filestorage("PTR", file, Digit.ULBService.getStateId());
            setLoading(false);
            if (response?.data?.files?.length > 0) {
              setUploadedFile(response?.data?.files[0]?.fileStoreId);
            } else {
              setError(t("CS_FILE_UPLOAD_ERROR"));
            }
          } catch (err) {
            setLoading(false);
            setError(t("CS_FILE_UPLOAD_ERROR"));
          }
        }
      }
    })();
  }, [file]);

  useEffect(() => {
    if (isHidden) setUploadedFile(null);
  }, [isHidden]);
  return (
    <div className="challan-documents">
      <LabelFieldPair className="challan-label-field" >
        <CardLabel className="challan-card-label" >
          {t(doc?.code)} <span className="requiredField"> {doc?.required && " *"}</span>
        </CardLabel>
        <div className="field cg-field-width-100">
          <UploadFile
            className="cg-upload-file"
            onUpload={selectfile}
            onDelete={() => {
              setUploadedFile(null);
            }}
            id={id}
            message={uploadedFile ? `1 ${t(`CS_ACTION_FILEUPLOADED`)}` : t(`CS_ACTION_NO_FILEUPLOADED`)}
            accept=".pdf, .jpeg, .jpg, .png"
            buttonType="button"
            error={!uploadedFile}
          />
          {doc?.code === "CHALLAN.EVIDENCE_IMAGE" && (
            <span className="challan-note-green">
              <span className="challan-note-red">
                {t("CHALLAN_NOTE_LABEL")}
              </span>
              {" "}
              {t("CHALLAN_UPLOAD_LOCATION_IMAGE")}
            </span>
          )}
          {doc?.code == "CHALLAN.EVIDENCE_IMAGE" &&
            documents
              ?.filter((item) => item.documentType == "CHALLAN.EVIDENCE_IMAGE")
              ?.map((item, index) => (
                <div key={index}>
                  {/* Latitude & Longitude */}
                  <div className="challan-lat-long" >
                    <div>
                      <b>Latitude:</b> {item.latitude}
                    </div>
                    <div>
                      <b>Longitude:</b> {item.longitude}
                    </div>
                  </div>
                </div>
              ))}
        </div>
      </LabelFieldPair>
      {getLoading && <Loader page={true} />}
    </div>
  );
}

export default ChallanDocuments;
