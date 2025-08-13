import React, { useEffect, useState } from "react";
import { FormStep, CardLabel, TextInput, UploadFile, Dropdown, LocationIcon } from "@nudmcdgnpm/digit-ui-react-components";

const TreePruningRequestDetails = ({ t, config, onSelect, userType, formData }) => {
  const user = Digit.UserService.getUser().info;
  const [reasonOfPruning, setReasonOfPruning] = useState(formData?.treePruningRequestDetails?.reasonOfPruning || "");
  const [latitude, setLatitude] = useState("");
  const [longitude, setLongitude] = useState("");
  const [geoTagLocation, setGeoTagLocation] = useState(formData?.treePruningRequestDetails?.geoTagLocation || "");
  const [supportingDocumentFile, setSupportingDocumentFile] = useState(formData?.treePruningRequestDetails?.supportingDocumentFile || "");
  const [isUploading, setIsUploading] = useState(false);
  const [uploadError, setUploadError] = useState("");
  const tenantId = Digit.ULBService.getStateId();
  const inputStyles = { width: user.type === "EMPLOYEE" ? "50%" : "100%" };

  const { data: ReasonOfPruningType} = Digit.Hooks.useCustomMDMS(tenantId, "request-service", [{ name: "ReasonPruningType" }], {
    select: (data) => {
      const formattedData = data?.["request-service"]?.["ReasonPruningType"];
      return formattedData;
    },
  });
 
  const goNext = () => {
    let treePruningRequestDetails = formData.treePruningRequestDetails;
    let Service = { ...treePruningRequestDetails, reasonOfPruning, geoTagLocation, supportingDocumentFile,latitude, longitude };
    onSelect(config.key, Service, false);
  };

  useEffect(() => {
    if (userType === "citizen") {
      goNext();
    }
  }, [reasonOfPruning, geoTagLocation, supportingDocumentFile, isUploading]);

  const handleInputChange = (e) => {
    const { name, value } = e.target ? e.target : { name: e.name, value: e };
    if (name === "reasonOfPruning") {
      setReasonOfPruning(value);
    } else if (name === "geoTagLocation" || name === "location") {
      setGeoTagLocation(value);
    } else if (name === "supportingDocumentFile") {
      setSupportingDocumentFile(value);
    }
  };

  const handleFileUpload = (e, setFileStoreId) => {
    const file = e.target.files[0];
    if (file.size >= 5242880) {
      setError("supportingDocument", { message: t("CS_MAXIMUM_UPLOAD_SIZE_EXCEEDED") }); // Set error for supportingDocument
      setFileStoreId(null); // Clear previous successful upload
    } else {
      setIsUploading(true);
      //setError("supportingDocument", { message: "" }); // Clear any previous errors
      Digit.UploadServices.Filestorage("TP", file, Digit.ULBService.getStateId())
        .then((response) => {
          if (response?.data?.files?.length > 0) {

            setFileStoreId(response.data.files[0].fileStoreId);
          } else {
            setError("supportingDocument", { message: t("CS_FILE_UPLOAD_ERROR") });
          }
        })
        .catch(() => setError("supportingDocument", { message: t("CS_FILE_UPLOAD_ERROR") }))
        .finally(() => setIsUploading(false));
    }
  };
  const fetchCurrentLocation = () => {
  if ("geolocation" in navigator) {
    navigator.geolocation.getCurrentPosition(
      (position) => {
        const { latitude, longitude } = position.coords;
        setGeoTagLocation(`${latitude}, ${longitude}`); 
        setLatitude(latitude);
        setLongitude(longitude);
      },
      (error) => {
        console.error("Error getting location:", error);
        alert("Unable to retrieve your location. Please check your browser settings.");
      }
    );
  } else {
    alert("Geolocation is not supported by your browser.");
  }
};

  const LoadingSpinner = () => (
    <div className="loading-spinner"
    />
  );
  return (
    <FormStep config={config} onSelect={goNext} t={t} isDisabled={!reasonOfPruning || !supportingDocumentFile}>
      <div>
        {/* Reason Dropdown */}
        <CardLabel>
          {t("REASON_FOR_PRUNING")} <span className="check-page-link-button">*</span>
        </CardLabel>
        <Dropdown
          className="form-field"
          selected={reasonOfPruning}
          placeholder={"Select Reason"}
          select={setReasonOfPruning}
          option={ReasonOfPruningType}
          style={inputStyles}
          optionKey="code"
          t={t}
        />
        <CardLabel>
          {t("LOCATION_GEOTAG")}
        </CardLabel>
        <div style={{ display: "flex", alignItems: "stretch", gap: "8px", width: user.type === "EMPLOYEE" ? "53%" : "100%" }}>
          <TextInput
            t={t}
            type={"text"}
            isMandatory={false}
            optionKey="i18nKey"
            name={"geoTagLocation"}
            value={geoTagLocation} 
            placeholder={"Select Location"}
            onChange={handleInputChange}
            max={new Date().toISOString().split("T")[0]}
            style={{ flex: 1 }}
            rules={{
              required: t("dddddddd"),
              validDate: (val) => (/^\d{4}-\d{2}-\d{2}$/.test(val) ? true : t("hhhhhhh")),
            }}
            className="location-input"
          />
          <div className="butt-icon"
                    onClick={() => {
                      fetchCurrentLocation("geoTagLocation");
                    }} style={{ 
            cursor: "pointer",
            width: "25px",
            marginBottom: "8px"
          }}>
            <LocationIcon className="fill-path-primary-main"/>
          </div>
        </div>

        {/* Upload Site Photograph */}
        <CardLabel>
          {t("UPLOAD_THE_SITE_PHOTOGRAPH")} <span className="check-page-link-button">*</span>
        </CardLabel>
        <div style={{ marginBottom: "16px", ...inputStyles }}>
          <UploadFile
            id="supportingDocument"
            onUpload={(e) => handleFileUpload(e, setSupportingDocumentFile)}
            onDelete={() => {
              setSupportingDocumentFile(null);
             
            }}
            message={isUploading ? (
              <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                <LoadingSpinner />
                <span>Uploading...</span>
              </div>
            ) : supportingDocumentFile ? "1 File Uploaded" : "No File Uploaded"}           
            textStyles={{ width: "100%" }}
            accept="image/*, .pdf, .png, .jpeg, .jpg"
            buttonType="button"
            error={uploadError || !supportingDocumentFile}
          />
        </div>
        {/* GeoTag Location with Icon */}
      </div>
    </FormStep>
  );
};

export default TreePruningRequestDetails;
