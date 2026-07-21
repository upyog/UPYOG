/* 
 * NOC Document Details Component (Step 4 of the Citizen wizard flow)
 * Handles uploading required verification documents (Identity, Address Proof, and Building Plans).
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
import Timeline from "../components/NocTimeline";

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

  const deleteFile = () => {
    setFileStoreId(null);
    setFileName(null);
    setUploadError(null);
    onChange(null, null, selectedDropdown);
  };

  const handleDropdownChange = (val) => {
    setSelectedDropdown(val);
    onChange(fileStoreId, fileName, val);
  };

  return (
    <div style={{ marginBottom: "15px" }}>
      <CardLabel style={{ fontWeight: "bold" }}>
        {t(config.code.replaceAll(".", "_"), config.name)} {config.required && <span style={{ color: "red" }}>*</span>}
      </CardLabel>

      {config.hasDropdown && (
        <Dropdown
          style={{ marginBottom: "10px" }}
          selected={selectedDropdown}
          option={config.dropdownData}
          select={handleDropdownChange}
          optionKey="label"
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

  const ownerDocConfigs = [
    {
      code: "OWNER.IDENTITYPROOF",
      name: "Identity Proof",
      required: true
    },
    {
      code: "OWNER.ADDRESSPROOF",
      name: "Address Proof",
      required: true
    }
  ];

  const buildingDocConfigs = [
    { code: "BUILDING.BUILDING_PLAN.SITE_PLAN", name: t("NOC_SITE_PLAN", "Site Plan"), required: false },
    { code: "BUILDING.BUILDING_PLAN.GROUND_FLOOR_PLAN", name: t("NOC_GROUND_FLOOR_PLAN", "Ground Floor Plan"), required: false },
    { code: "BUILDING.BUILDING_PLAN.SECTION_PLAN", name: t("NOC_SECTION_PLAN", "Section Plan"), required: false },
    { code: "BUILDING.BUILDING_PLAN.ELEVATION_PLAN", name: t("NOC_ELEVATION_PLAN", "Elevation Plan"), required: false },
    { code: "BUILDING.BUILDING_PLAN.BUILTUP_AREA_STATEMENT", name: t("NOC_BUILT_UP_AREA_STATEMENT", "Built-up Area Statement"), required: false },
    { code: "BUILDING.FIRE_FIGHTING_PLAN", name: t("NOC_FIRE_FIGHTING_PLAN", "Fire-Fighting Plan"), required: false },
    { code: "BUILDING.BUILDING_PLAN.OWNERS_CHECKLIST", name: t("NOC_NBC_CHECKLIST", "NBC Checklist"), required: false }
  ];

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

  const goNext = () => {
    if (validateForm()) {
      onSelect(config.key, { documents });
    }
  };

  const onSkip = () => onSelect();

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
      <Timeline currentStep={4} />
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
