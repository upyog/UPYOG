import { CardLabel, CardLabelDesc, Dropdown, FormStep, UploadFile } from "@upyog/digit-ui-react-components";
import React, { useEffect, useState } from "react";
import { useLocation } from "react-router-dom";
import { stringReplaceAll } from "../utils";
import Timeline from "../components/TLTimeline";

const PropertyPhoto = ({ t, config, onSelect, userType, formData }) => {
  //let index = window.location.href.charAt(window.location.href.length - 1);
  const { pathname: url } = useLocation();
  const isMutation = url.includes("property-mutation");
  console.log("propertyPhoto=",formData)

  let index = window.location.href.split("/").pop();
  const [uploadedFile, setUploadedFile] = useState(
    !isMutation ? formData?.propertyPhoto?.documents?.propertyPhoto?.fileStoreId || null : formData?.[config.key]?.fileStoreId
  );
  const [file, setFile] = useState(formData?.propertyPhoto?.documents?.propertyPhoto);
  const [error, setError] = useState(null);
  const cityDetails = Digit.ULBService.getCurrentUlb();
  const isUpdateProperty = formData?.isUpdateProperty || false;
  const isEditProperty = formData?.isEditProperty || false;
  
  useEffect(()=>{
    if(formData?.documents && formData?.documents.length>0) {
      let obj = formData?.documents.find(o => o.documentType === "PROPERTY_PHOTO") || null;
        if(obj) {
          setFile(obj);
          setUploadedFile(!isMutation ? obj?.fileStoreId || null : formData?.[config.key]?.fileStoreId)
        }
    }
   },[])

  const handleSubmit = () => {
    let fileStoreId = uploadedFile;
    let fileDetails = {};
    if (fileDetails) fileDetails.documentType = "PROPERTY_PHOTO";
    if (fileDetails) fileDetails.fileStoreId = fileStoreId ? fileStoreId : null;
    let propertyPhoto = formData && formData.propertyPhoto ? formData.propertyPhoto : {};
    if (propertyPhoto && propertyPhoto.documents) {
        propertyPhoto.documents["propertyPhoto"] = fileDetails;
    } else {
        propertyPhoto["documents"] = {};
        propertyPhoto.documents["propertyPhoto"] = fileDetails;
    }
    console.log("propertyPhoto=",propertyPhoto)
    onSelect("propertyPhoto", propertyPhoto);
  };
  const onSkip = () => onSelect();

  function selectfile(e) {
    setFile(e.target.files[0]);
  }

  useEffect(() => {
    (async () => {
      setError(null);
      if (file) {
        if (file.size >= 2000000) {
          setError(t("PT_MAXIMUM_UPLOAD_SIZE_EXCEEDED"));
        } else {
          try {
            const response = await Digit.UploadServices.Filestorage("property-upload", file, Digit.ULBService.getStateId());
            if (response?.data?.files?.length > 0) {
              setUploadedFile(response?.data?.files[0]?.fileStoreId);
            } else {
              setError(t("PT_FILE_UPLOAD_ERROR"));
            }
          } catch (err) {}
        }
      }
    })();
  }, [file]);
  const checkMutatePT = window.location.href.includes("citizen/pt/property/property-mutation/") ? (
    <Timeline currentStep={5} flow="PT_MUTATE" />
  ) : (
    <Timeline currentStep={5} />
  );

  return (
    <React.Fragment>
      {window.location.href.includes("/citizen") ? <Timeline currentStep={5} /> : null}
      <FormStep
        config={config}
        onSelect={handleSubmit}
        onSkip={onSkip}
        t={t}
        isDisabled={isUpdateProperty || isEditProperty ? false : !uploadedFile  || error}
      >
        {/* <CardLabelDesc>{t(`PT_UPLOAD_RESTRICTIONS_TYPES`)}</CardLabelDesc> */}
        <CardLabelDesc>{t(`PT_UPLOAD_RESTRICTIONS_SIZE`)}</CardLabelDesc>
        <CardLabel>{`${t("PT_PROPERTY_PHOTO_DESC")}`}</CardLabel>
       
        <UploadFile
          id={"pt-doc"}
          extraStyleName={"propertyCreate"}
          accept=".jpg,.png,.pdf"
          onUpload={selectfile}
          onDelete={() => {
            setUploadedFile(null);
          }}
          message={uploadedFile ? `1 ${t(`PT_ACTION_FILEUPLOADED`)}` : t(`PT_ACTION_NO_FILEUPLOADED`)}
          error={error}
          hasFile={uploadedFile ? true : false}
        />
        {error ? <div style={{ height: "20px", width: "100%", fontSize: "20px", color: "red", marginTop: "5px" }}>{error}</div> : ""}
        <div style={{ disabled: "true", height: "20px", width: "100%" }}></div>
      </FormStep>
    </React.Fragment>
  );
};

export default PropertyPhoto;
