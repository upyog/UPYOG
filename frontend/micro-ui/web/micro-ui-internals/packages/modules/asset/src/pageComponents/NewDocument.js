import React, { useEffect, useState } from "react";
import { CardLabel, Dropdown, UploadFile, Toast, Loader, FormStep, LabelFieldPair } from "@upyog/digit-ui-react-components";
import Timeline from "../components/ASTTimeline";
import EXIF from 'exif-js';

const NewDocument = ({ t, config, onSelect, userType, formData, setError: setFormError, clearErrors: clearFormErrors, formState }) => {
  const tenantId = Digit.ULBService.getStateId();
  const [documents, setDocuments] = useState(formData?.documents?.documents || []);
  const [error, setError] = useState(null);
  const [enableSubmit, setEnableSubmit] = useState(true);
  const [checkRequiredFields, setCheckRequiredFields] = useState(true);

  // const tenantId = Digit.ULBService.getCurrentTenantId();
    const stateId = Digit.ULBService.getStateId();
  

  const { isLoading, data } = Digit.Hooks.asset.useAssetDocumentsMDMS(stateId, "ASSET", "Documents");
  

  const handleSubmit = () => {
    let document = formData.documents;
    let documentStep;
    documentStep = { ...document, documents: documents };
    onSelect(config.key, documentStep);
  };
  const onSkip = () => onSelect();
  function onAdd() {}

  useEffect(() => {
    let count = 0;
    data?.ASSET?.Documents.map((doc) => {

        console.log("docccccc",doc);
      doc.hasDropdown = true;
      
      let isRequired = false;
      documents.map((data) => {
        if (doc.required && data?.documentType.includes(doc.code)) isRequired = true;
      });
      if (!isRequired && doc.required) count = count + 1;
    });
    if ((count == "0" || count == 0) && documents.length > 0) setEnableSubmit(false);
    else setEnableSubmit(true);
  }, [documents, checkRequiredFields]);

 

  return (
    <div>
      <Timeline currentStep={4} />
      {!isLoading ? (
        <FormStep t={t} config={config} onSelect={handleSubmit} onSkip={onSkip} isDisabled={enableSubmit} onAdd={onAdd}>
          {data?.ASSET?.Documents?.map((document, index) => {
            console.log("djuhewdiugwefigwehjdbywd",document);
            return (
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
            );
          })}
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
  action,
  formData,
  
  id,
  
}) {
  const filteredDocument = documents?.filter((item) => item?.documentType?.includes(doc?.code))[0];
  

  const tenantId = Digit.ULBService.getCurrentTenantId();
  const [selectedDocument, setSelectedDocument] = useState(
    filteredDocument
      ? { ...filteredDocument, active: doc?.active === true, code: filteredDocument?.documentType }
      : doc?.dropdownData?.length === 1
      ? doc?.dropdownData[0]
      : {}
  );

  const [file, setFile] = useState(null);
  const [uploadedFile, setUploadedFile] = useState(() => filteredDocument?.fileStoreId || null);
  const [latitude, setLatitude] = useState(formData?.latitude || null);
  const [longitude, setLongitude] = useState(formData?.longitude || null);

  const handleASSETSelectDocument = (value) => setSelectedDocument(value);

  function selectfile(e) {
    setFile(e.target.files[0]);
  }
  const { dropdownData } = doc;
  
  var dropDownData = dropdownData;
   
  const [isHidden, setHidden] = useState(false);

  

  useEffect(() => {
    if (selectedDocument?.code) {
      setDocuments((prev) => {
        const filteredDocumentsByDocumentType = prev?.filter((item) => item?.documentType !== selectedDocument?.code);

        if (uploadedFile?.length === 0 || uploadedFile === null) {
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
    
  }, [uploadedFile, selectedDocument, latitude, longitude]);

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
    (async () => {
      setError(null);
      if (file) {
        if (file.size >= 5242880) {
          setError(t("CS_MAXIMUM_UPLOAD_SIZE_EXCEEDED"));
          // if (!formState.errors[config.key]) setFormError(config.key, { type: doc?.code });
        } else {
          try {
            setUploadedFile(null);
            const response = await Digit.UploadServices.Filestorage("ASSET", file, Digit.ULBService.getStateId());
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

  useEffect(() => {
    if (isHidden) setUploadedFile(null);
  }, [isHidden]);

  return (
    <div style={{ marginBottom: "24px" }}>
      {doc?.hasDropdown ? (
        <LabelFieldPair>
          <CardLabel className="card-label-smaller">{t(doc?.code.replaceAll(".", "_")) + "  *"}</CardLabel>
          <Dropdown
            className="form-field"
            selected={selectedDocument}
            style={{ width: "50%" }}
            option={dropDownData.map((e) => ({ ...e, i18nKey: e.code?.replaceAll(".", "_") }))}
            select={handleASSETSelectDocument}
            optionKey="i18nKey"
            t={t}
          />
        </LabelFieldPair>
      ) : null}
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
            accept=".pdf, .jpeg, .jpg, .png"   //  to accept document of all kind
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
      {/* {error && <CardLabel style={{ color: "red" }}>{error}</CardLabel>} */}
    </div>
  );
}

export default NewDocument;
