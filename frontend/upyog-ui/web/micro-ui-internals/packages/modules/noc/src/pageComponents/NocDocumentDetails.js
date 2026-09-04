/** 
 * NOC Document Details Component handles uploading required verification documents (Identity, Address Proof, and Building Plans).
 * Performs validation checks to ensure all mandatory files are uploaded before proceeding.
 */
import React, { useEffect, useState } from "react";
import {
  FormStep,
  CardLabel,
  Dropdown,
  UploadFile,
  Loader,
  CardLabelError,
  CardHeader,
  Toast,
} from "@nudmcdgnpm/digit-ui-react-components";

const DocumentRow = ({ t, config, initialDoc, onChange }) => {
  const stateId = Digit.ULBService.getStateId();

  const [selectedDropdown, setSelectedDropdown] = useState(initialDoc?.selectedDropdown || null);
  const [fileStoreId, setFileStoreId] = useState(initialDoc?.fileStoreId || null);
  const [fileName, setFileName] = useState(initialDoc?.fileName || null);
  const [uploadError, setUploadError] = useState(null);
  const [isUploading, setIsUploading] = useState(false);

  useEffect(() => {
    if (config.hasDropdown && config.dropdownData?.length === 1 && !selectedDropdown) {
      setSelectedDropdown(config.dropdownData[0]);
    }
  }, [config]);

  /** Handles uploading selected document file to filestorage after size validation. */
  const selectFile = async (e) => {
    const file = e.target.files[0];
    if (!file) return;

    if (file.size >= 5242880) {
      setUploadError(t("CS_MAXIMUM_UPLOAD_SIZE_EXCEEDED"));
      return;
    }

    setUploadError(null);
    setIsUploading(true);
    try {
      const response = await Digit.UploadServices.Filestorage("NOC", file, stateId);
      if (response?.data?.files?.length > 0) {
        const id = response?.data?.files[0]?.fileStoreId;
        const name = file.name;
        setFileStoreId(id);
        setFileName(name);
        onChange(id, name, selectedDropdown);
      } else {
        setUploadError(t("CS_FILE_UPLOAD_ERROR"));
      }
    } catch (err) {
      console.error(err);
      setUploadError(t("CS_FILE_UPLOAD_ERROR"));
    } finally {
      setIsUploading(false);
    }
  };

  /** Clears uploaded file reference and resets upload state. */
  const deleteFile = () => {
    setFileStoreId(null);
    setFileName(null);
    setUploadError(null);
    onChange(null, null, selectedDropdown);
  };

  /** Updates document subtype dropdown selection. */
  const handleDropdownChange = (val) => {
    setSelectedDropdown(val);
    onChange(fileStoreId, fileName, val);
  };

  return (
    <div style={{ marginBottom: "15px" }}>
      <CardLabel style={{ fontWeight: "bold" }}>
        {t(config.code.replaceAll(".", "_"), config.name)} {config.required && <span className="astericColor">*</span>}
      </CardLabel>

      {config.hasDropdown && (
        <Dropdown
          style={{ marginBottom: "10px" }}
          selected={selectedDropdown}
          option={config.dropdownData?.map(opt => ({
            ...opt,
            i18nKey: opt.code?.replaceAll(".", "_")
          }))}
          select={handleDropdownChange}
          optionKey="i18nKey"
          t={t}
        />
      )}

      <UploadFile
        onUpload={selectFile}
        onDelete={deleteFile}
        id={`noc-doc-${config.code.replaceAll(".", "-")}`}
        message={
          isUploading
            ? t("CS_UPLOADING_FILE")
            : fileStoreId
              ? `1 ${t("CS_ACTION_FILEUPLOADED")}`
              : t("CS_ACTION_NO_FILEUPLOADED")
        }
        accept="image/*, .pdf, .png, .jpeg"
        error={uploadError}
      />
      {fileName && <div style={{ fontSize: "0.9rem", color: "#555", marginTop: "5px" }}>{fileName}</div>}
      {uploadError && <CardLabelError>{uploadError}</CardLabelError>}
    </div>
  );
};

const NocDocumentDetails = ({ t, config, onSelect, userType, formData }) => {
  const [documents, setDocuments] = useState(formData?.documents?.documents || []);
  const [error, setError] = useState(null);

  /* Auto-dismisses warning toast messages after 3 seconds */
  useEffect(() => {
    if (error) {
      const timer = setTimeout(() => {
        setError(null);
      }, 3000);
      return () => clearTimeout(timer);
    }
  }, [error]);

  const buildings = formData?.property?.buildings || [];

  const stateId = Digit.ULBService.getStateId();
  const { isLoading, data: mdmsData } = Digit.Hooks.useCustomMDMS(stateId, "FireNoc", [{ name: "Documents" }]);

  const mdmsDocs = mdmsData?.FireNoc?.Documents || [];

  const ownerDocConfigs = mdmsDocs
    .filter((doc) => doc.documentType === "OWNER")
    .map((doc) => ({
      ...doc,
      name: doc.name || doc.code,
    }));

  const buildingDocConfigs = [];
  mdmsDocs
    .filter((doc) => doc.documentType === "BUILDING")
    .forEach((doc) => {
      if (doc.hasMultipleRows && doc.options) {
        doc.options.forEach((option) => {
          buildingDocConfigs.push({
            ...option,
            name: option.name || option.code,
            hasDropdown: option.hasDropdown || false,
            dropdownData: option.dropdownData || [],
          });
        });
      } else {
        buildingDocConfigs.push({
          ...doc,
          name: doc.name || doc.code,
        });
      }
    });

  /** Updates or removes document metadata in local state list upon upload/delete. */
  const handleDocumentChange = (docCode, fileStoreId, fileName, selectedDropdown, buildingName = null) => {
    setDocuments((prev) => {
      const filtered = prev.filter(
        (doc) =>
          !(doc.categoryCode === docCode && doc.buildingName === buildingName)
      );

      if (!fileStoreId) {
        return filtered;
      }

      const newDoc = {
        documentType: selectedDropdown?.code || docCode,
        fileStoreId,
        fileName,
        categoryCode: docCode,
        selectedDropdown,
        buildingName
      };

      return [...filtered, newDoc];
    });
  };

  /** Validates that mandatory verification documents are uploaded before submission. */
  const validateForm = () => {
    for (const configItem of ownerDocConfigs) {
      const match = documents.find((d) => d.categoryCode === configItem.code);
      if (!match || !match.fileStoreId || !match.documentType) {
        setError(t("NOC_ERROR_FILL_ALL_MANDATORY_DETAILS"));
        return false;
      }
    }
    setError(null);
    return true;
  };

  /** Validates mandatory document requirements and proceeds to next wizard step. */
  const goNext = () => {
    if (validateForm()) {
      onSelect(config.key, { documents });
    }
  };

  /** Skips document details step in application wizard. */
  const onSkip = () => onSelect();

  if (isLoading) {
    return <Loader />;
  }

  if (userType === "employee") {
    return (
      <div style={{ padding: "10px" }}>
        <div style={{ marginBottom: "20px" }}>
          <CardHeader>{t("NOC_OWNER_DOCUMENTS_HEADER")}</CardHeader>
          {ownerDocConfigs.map((docConfig) => {
            const initialDoc = documents.find((d) => d.categoryCode === docConfig.code);
            return (
              <DocumentRow
                key={docConfig.code}
                t={t}
                config={docConfig}
                initialDoc={initialDoc}
                onChange={(fileStoreId, fileName, selectedDropdown) =>
                  handleDocumentChange(docConfig.code, fileStoreId, fileName, selectedDropdown)
                }
              />
            );
          })}
        </div>

        {buildings.map((building, bIdx) => (
          <div key={bIdx} style={{ marginBottom: "20px", borderTop: "1px solid #ccc", paddingTop: "20px" }}>
            <CardHeader style={{ fontSize: "1.2rem" }}>
              {t("NOC_BUILDING_DOCUMENTS_HEADER")}
            </CardHeader>
            {buildingDocConfigs.map((docConfig) => {
              const initialDoc = documents.find(
                (d) => d.categoryCode === docConfig.code && d.buildingName === building.name
              );
              return (
                <DocumentRow
                  key={`${building.name}-${docConfig.code}`}
                  t={t}
                  config={docConfig}
                  initialDoc={initialDoc}
                  onChange={(fileStoreId, fileName, selectedDropdown) =>
                    handleDocumentChange(docConfig.code, fileStoreId, fileName, selectedDropdown, building.name)
                  }
                />
              );
            })}
          </div>
        ))}
        {error && <CardLabelError style={{ marginTop: "15px" }}>{error}</CardLabelError>}
      </div>
    );
  }

  return (
    <React.Fragment>

      <FormStep config={config} onSelect={goNext} onSkip={onSkip} t={t} forcedError={null}>
        <div style={{ marginBottom: "20px" }}>
          <CardHeader>{t("NOC_OWNER_DOCUMENTS_HEADER")}</CardHeader>
          {ownerDocConfigs.map((docConfig) => {
            const initialDoc = documents.find((d) => d.categoryCode === docConfig.code);
            return (
              <DocumentRow
                key={docConfig.code}
                t={t}
                config={docConfig}
                initialDoc={initialDoc}
                onChange={(fileStoreId, fileName, selectedDropdown) =>
                  handleDocumentChange(docConfig.code, fileStoreId, fileName, selectedDropdown)
                }
              />
            );
          })}
        </div>

        {buildings.map((building, bIdx) => (
          <div key={bIdx} style={{ marginBottom: "20px", borderTop: "1px solid #ccc", paddingTop: "20px" }}>
            <CardHeader style={{ fontSize: "1.2rem" }}>
              {t("NOC_BUILDING_DOCUMENTS_HEADER")}
            </CardHeader>
            {buildingDocConfigs.map((docConfig) => {
              const initialDoc = documents.find(
                (d) => d.categoryCode === docConfig.code && d.buildingName === building.name
              );
              return (
                <DocumentRow
                  key={`${building.name}-${docConfig.code}`}
                  t={t}
                  config={docConfig}
                  initialDoc={initialDoc}
                  onChange={(fileStoreId, fileName, selectedDropdown) =>
                    handleDocumentChange(docConfig.code, fileStoreId, fileName, selectedDropdown, building.name)
                  }
                />
              );
            })}
          </div>
        ))}
      </FormStep>
      {error && <Toast error={true} label={t(error)} onClose={() => setError(null)} />}
    </React.Fragment>
  );
};

export default NocDocumentDetails;
