import React, { useEffect, useState } from "react";
import { CardLabel, Dropdown, UploadFile, Toast, Loader, FormStep, LabelFieldPair } from "@nudmcdgnpm/digit-ui-react-components";
import Timeline from "../components/ASTTimeline";
import EXIF from 'exif-js';

const NewDocument = ({ t, config, onSelect, formData }) => {
  const [documents, setDocuments] = useState(formData?.documents?.documents || []);
  const [error, setError] = useState(null);
  const [enableSubmit, setEnableSubmit] = useState(true);
  const [checkRequiredFields, setCheckRequiredFields] = useState(true);

  const stateId = Digit.ULBService.getStateId();
  const { isLoading, data } = Digit.Hooks.asset.useAssetDocumentsMDMS(stateId, "ASSET", "Documents");

  const handleSubmit = () => {
    let documentStep = { ...formData.documents, documents };
    onSelect(config.key, documentStep);
  };

  const onSkip = () => onSelect();

  useEffect(() => {
    let count = 0;
    data?.ASSET?.Documents.forEach((doc) => {
      doc.hasDropdown = true;
      let isRequired = documents.some((data) => doc.required && data?.documentType.includes(doc.code));
      if (!isRequired && doc.required) count += 1;
    });
    setEnableSubmit(!(count === 0 && documents.length > 0));
  }, [documents, checkRequiredFields, data]);

  return (
    <div>
      <Timeline currentStep={4} />
      {!isLoading ? (
        <FormStep t={t} config={config} onSelect={handleSubmit} onSkip={onSkip} isDisabled={enableSubmit}>
          {data?.ASSET?.Documents?.map((document, index) => (
            <ASSETSelectDocument
              key={index}
              document={document}
              t={t}
              error={error}
              setError={setError}
              setDocuments={setDocuments}
              documents={documents}
              setCheckRequiredFields={setCheckRequiredFields}
              formData={formData}
            />
          ))}
          {error && <Toast label={error} onClose={() => setError(null)} error />}
        </FormStep>
      ) : (
        <Loader />
      )}
    </div>
  );
};

function ASSETSelectDocument({
  t,
  document: doc,
  setDocuments,
  setError,
  documents,
  formData,
  id,
}) {
  const filteredDocument = documents?.find((item) => item?.documentType?.includes(doc?.code));

  const [selectedDocument, setSelectedDocument] = useState(
    filteredDocument
      ? { ...filteredDocument, active: doc?.active === true, code: filteredDocument?.documentType }
      : doc?.dropdownData?.length === 1
      ? doc?.dropdownData[0]
      : {}
  );

  const [file, setFile] = useState(null);
  const [uploadedFile, setUploadedFile] = useState(filteredDocument?.fileStoreId || null);
  const [latitude, setLatitude] = useState(formData?.latitude || null);
  const [longitude, setLongitude] = useState(formData?.longitude || null);

  const handleASSETSelectDocument = (value) => setSelectedDocument(value);

  const extractGeoLocation = (file) => {
    return new Promise((resolve) => {
      EXIF.getData(file, function () {
        const lat = EXIF.getTag(this, 'GPSLatitude');
        const lon = EXIF.getTag(this, 'GPSLongitude');
        if (lat && lon) {
          const latDecimal = convertToDecimal(lat);
          const lonDecimal = convertToDecimal(lon);
          resolve({ latitude: latDecimal, longitude: lonDecimal });
        } else {
          resolve({ latitude: null, longitude: null });
          if (doc?.code === "OWNER.ASSETPHOTO") {
            alert("Please upload a photo with location details");
          }
        }
      });
    });
  };

  const convertToDecimal = (coordinate) => {
    const degrees = coordinate[0];
    const minutes = coordinate[1];
    const seconds = coordinate[2];
    return degrees + minutes / 60 + seconds / 3600;
  };

  const handleFileUpload = (e) => {
    const file = e.target.files[0];
    setFile(file);
    extractGeoLocation(file).then(({ latitude, longitude }) => {
      setLatitude(latitude);
      setLongitude(longitude);
      if (doc?.code === "OWNER.ASSETPHOTO" && (!latitude || !longitude)) {
        setError("Please upload a photo with location details");
      }
    });
  };

  useEffect(() => {
    if (file) {
      if (file.size >= 5242880) {
        setError(t("CS_MAXIMUM_UPLOAD_SIZE_EXCEEDED"));
      } else {
        setUploadedFile(null);
        Digit.UploadServices.Filestorage("ASSET", file, Digit.ULBService.getStateId())
          .then(response => {
            if (response?.data?.files?.length > 0) {
              setUploadedFile(response.data.files[0].fileStoreId);
            } else {
              setError(t("CS_FILE_UPLOAD_ERROR"));
            }
          })
          .catch(() => setError(t("CS_FILE_UPLOAD_ERROR")));
      }
    }
  }, [file, t]);

  useEffect(() => {
    if (selectedDocument?.code) {
      setDocuments((prev) => {
        const filteredDocumentsByDocumentType = prev?.filter((item) => item?.documentType !== selectedDocument?.code);
        if (!uploadedFile) {
          return filteredDocumentsByDocumentType;
        }
        const filteredDocumentsByFileStoreId = filteredDocumentsByDocumentType?.filter((item) => item?.fileStoreId !== uploadedFile);
        return [
          ...filteredDocumentsByFileStoreId,
          {
            documentType: selectedDocument?.code,
            fileStoreId: uploadedFile,
            documentUid: uploadedFile,
            latitude,
            longitude,
          },
        ];
      });
    }
  }, [uploadedFile, selectedDocument, latitude, longitude, setDocuments]);

  return (
    <div style={{ marginBottom: "24px" }}>
      {doc?.hasDropdown && (
        <LabelFieldPair>
          <CardLabel className="card-label-smaller">{t(doc?.code.replaceAll(".", "_")) + "  *"}</CardLabel>
          <Dropdown
            className="form-field"
            selected={selectedDocument}
            style={{ width: "50%" }}
            option={doc?.dropdownData.map((e) => ({ ...e, i18nKey: e.code?.replaceAll(".", "_") }))}
            select={handleASSETSelectDocument}
            optionKey="i18nKey"
            t={t}
            placeholder={"Select"}
          />
        </LabelFieldPair>
      )}
      <LabelFieldPair>
        <CardLabel className="card-label-smaller"></CardLabel>
        <div className="field">
          <UploadFile
            onUpload={handleFileUpload}
            onDelete={() => {
              setUploadedFile(null);
              setLatitude(null);
              setLongitude(null);
            }}
            id={id}
            message={uploadedFile ? `1 ${t(`CS_ACTION_FILEUPLOADED`)}` : t(`CS_ACTION_NO_FILEUPLOADED`)}
            textStyles={{ width: "100%" }}
            inputStyles={{ width: "280px" }}
            accept=".pdf, .jpeg, .jpg, .png"
            buttonType="button"
            error={!uploadedFile}
          />
        </div>
      </LabelFieldPair>
      {doc?.code === "OWNER.ASSETPHOTO" && latitude && longitude && (
        <div style={{ marginTop: '10px', textAlign: 'center' }}>
          <p><strong>{t("Location Details")}:</strong></p>
          <p>{t("Latitude")}: {latitude}</p>
          <p>{t("Longitude")}: {longitude}</p>
        </div>
      )}
    </div>
  );
}

export default NewDocument;
