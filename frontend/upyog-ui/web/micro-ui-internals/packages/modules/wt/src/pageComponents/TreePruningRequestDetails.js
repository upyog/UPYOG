import React, { useEffect, useState } from "react";
import { FormStep, CardLabel, UploadFile, Dropdown, GeoLocationWithDigipin } from "@nudmcdgnpm/digit-ui-react-components";
import { TPService } from "../../../../libraries/src/services/elements/TP";

const TreePruningRequestDetails = ({ t, config, onSelect, userType, formData }) => {
  const user = Digit.UserService.getUser().info;
  const [reasonOfPruning, setReasonOfPruning] = useState(formData?.treePruningRequestDetails?.reasonOfPruning || "");
  const [geoTagLocation, setGeoTagLocation] = useState(formData?.treePruningRequestDetails?.geoTagLocation || "");
  const [latitude, setLatitude] = useState("");
  const [longitude, setLongitude] = useState("");
  const [digipin, setDigipin] = useState(formData?.treePruningRequestDetails?.digipin || "");
  const [supportingDocumentFile, setSupportingDocumentFile] = useState(formData?.treePruningRequestDetails?.supportingDocumentFile || "");
  const [isUploading, setIsUploading] = useState(false);
  const [uploadError, setUploadError] = useState("");
  const tenantId = Digit.ULBService.getStateId();
  const inputStyles = { width: user.type === "EMPLOYEE" ? "50%" : "100%" };

  const { data: ReasonOfPruningType} = Digit.Hooks.useCustomMDMS(tenantId, "Request-Service", [{ name: "ReasonPruningType" }], {
    select: (data) => {
      const formattedData = data?.["Request-Service"]?.["ReasonPruningType"];
      return formattedData;
    }
  });
  const goNext = () => {
    let treePruningRequestDetails = formData.treePruningRequestDetails;
    let Service = {
      ...treePruningRequestDetails,
      reasonOfPruning,
      geoTagLocation,
      supportingDocumentFile,
      latitude,
      longitude,
      digipin
    };
    onSelect(config.key, Service, false);
  };
  useEffect(() => {
    if (userType === "citizen") {
      goNext();
    }
  }, [reasonOfPruning, geoTagLocation, supportingDocumentFile, isUploading]);
  const handleInputChange = e => {
    const {
      name,
      value
    } = e.target ? e.target : {
      name: e.name,
      value: e
    };
    if (name === "reasonOfPruning") {
      setReasonOfPruning(value);
      // Removed the "geoTagLocation" / "location" branch — geo state is now managed by handleGeoChange
    } else if (name === "supportingDocumentFile") {
      setSupportingDocumentFile(value);
    }
  };
  const handleFileUpload = (e, setFileStoreId) => {
    const file = e.target.files[0];
    if (file.size >= 5242880) {
      setError("supportingDocument", {
        message: t("CS_MAXIMUM_UPLOAD_SIZE_EXCEEDED")
      }); // Set error for supportingDocument
      setFileStoreId(null); // Clear previous successful upload
    } else {
      setIsUploading(true);
      //setError("supportingDocument", { message: "" }); // Clear any previous errors
      Digit.UploadServices.Filestorage("TP", file, Digit.ULBService.getStateId()).then(response => {
        if (response?.data?.files?.length > 0) {
          setFileStoreId(response.data.files[0].fileStoreId);
        } else {
          setError("supportingDocument", {
            message: t("CS_FILE_UPLOAD_ERROR")
          });
        }
      }).catch(() => setError("supportingDocument", {
        message: t("CS_FILE_UPLOAD_ERROR")
      })).finally(() => setIsUploading(false));
    }
  };
  // Replaces the old inline fetchCurrentLocation + manual setters; receives all geo fields from GeoLocationWithDigipin
  const handleGeoChange = ({ geoTagLocation, latitude, longitude, digipin }) => {
    setGeoTagLocation(geoTagLocation);
    setLatitude(latitude);
    setLongitude(longitude);
    setDigipin(digipin);
  };

  // Passed as onFetchDigipin prop; delegates digipin generation to the backend instead of the old local utility
  const handleFetchDigipin = async (latitude, longitude) => {
    const res = await TPService.generateDigipin(latitude, longitude);
    return res?.digipin || "";
  };
  const LoadingSpinner = () => <div className="loading-spinner" />;
  return <FormStep config={config} onSelect={goNext} t={t} isDisabled={!reasonOfPruning || !supportingDocumentFile}>
      <div>
        {/* Reason Dropdown */}
        <CardLabel>
          {t("REASON_FOR_PRUNING")} <span className="check-page-link-button">*</span>
        </CardLabel>
        <Dropdown className="form-field" selected={reasonOfPruning} placeholder={"Select Reason"} select={setReasonOfPruning} option={ReasonOfPruningType} style={inputStyles} optionKey="code" t={t} />
        <CardLabel>{t("LOCATION_GEOTAG")}</CardLabel>
        {/* Replaced manual TextInput + LocationIcon + DigipinDisplay block with the reusable GeoLocationWithDigipin component */}
        <GeoLocationWithDigipin
          t={t}
          value={geoTagLocation}
          onChange={handleGeoChange}
          onFetchDigipin={handleFetchDigipin}
          // Slightly wider on employee view (60%) vs the old 53%
          inputStyle={{ width: user.type === "EMPLOYEE" ? "60%" : "100%" }}
          showDigipin
          showMapLink
        />

        {/* Upload Site Photograph */}
        <CardLabel>
          {t("UPLOAD_THE_SITE_PHOTOGRAPH")} <span className="check-page-link-button">*</span>
        </CardLabel>
        <div style={{
        marginBottom: "16px",
        ...inputStyles
      }}>
          <UploadFile id="supportingDocument" onUpload={e => handleFileUpload(e, setSupportingDocumentFile)} onDelete={() => {
          setSupportingDocumentFile(null);
        }} message={isUploading ? <div className="wt-auto-30">
                <LoadingSpinner />
                <span>Uploading...</span>
              </div> : supportingDocumentFile ? "1 File Uploaded" : "No File Uploaded"} textStyles={{
          width: "100%"
        }} accept="image/*, .pdf, .png, .jpeg, .jpg" buttonType="button" error={uploadError || !supportingDocumentFile} />
        </div>
        {/* GeoTag Location with Icon */}
      </div>
    </FormStep>;
};
export default TreePruningRequestDetails;
