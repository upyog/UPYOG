import { CardLabel, FormStep, TextInput, DatePicker, UploadFile } from "@nudmcdgnpm/digit-ui-react-components";
import React, { useState, useEffect } from "react";
import MultiSelectDropdown from "./MultiSelectDropdown";
import { CND_VARIABLES,LoadingSpinner } from "../utils";
import { useApplicationDetails } from "../pages/employee/Edit/ApplicationContext";
import { convertToObject } from "../utils";
import WasteTypeTable from "./WasteTypeTable";
import { calculateTotalWasteInTons, formatWasteQuantity } from "../utils";

/**
* WasteType component that collects information about waste collection requests including
* waste material types, quantity, pickup scheduling, and supporting documentation.
* Handles file uploads with loading states, form validation, and persistence of
* previously uploaded files between form navigations.
*/

const WasteType = ({ t, config, onSelect, formData }) => {
  let validation = {};
  const isEmployee = window.location.href.includes("/employee") ?true:false;
  const applicationDetails = isEmployee ? useApplicationDetails():null;
  const userType = Digit.UserService.getUser().info.type;
  const inputStyles = { width: userType === "EMPLOYEE" ? "50%" : "100%" };
  // Process wasteTypeDetails from applicationDetails to get unique waste types
  const processWasteTypeDetails = (details) => {
    if (!details || !Array.isArray(details)) return [];
    // Extract unique waste types
    const uniqueWasteTypes = [...new Set(details.map(item => item.wasteType))];
    // Convert each waste type to required format using the existing utility function
    return uniqueWasteTypes.map(type => convertToObject(type));
  };
  const [wasteMaterialType, setwasteMaterialType] = useState(formData?.wasteType?.wasteMaterialType ||  processWasteTypeDetails(applicationDetails?.wasteTypeDetails)|| []);
  const [wasteQuantity, setwasteQuantity] = useState(formData?.wasteType?.wasteQuantity || applicationDetails?.totalWasteQuantity|| "");
  const [pickupDate, setpickupDate] = useState(formData?.wasteType?.pickupDate ||applicationDetails?.requestedPickupDate|| "");
  // Separate loading states for each file type
  const [isUploadingMedia, setIsUploadingMedia] = useState(false);
  const [isUploadingStack, setIsUploadingStack] = useState(false);
  const [error, setError] = useState(null);
  const [fileUploads, setFileUploads] = useState({
    siteMediaPhoto: formData?.wasteType?.siteMediaPhoto || null,
    siteStack: formData?.wasteType?.siteStack || null,
  });

  /* TODO: add these CSS inside Classname
  isInPickupProgress, containerStyle, labelStyle  
  these CSS added for Waste quantity as well as Pickup date because they are not aliging well in Facility centre Screen
  */
  const isInPickupProgress = applicationDetails?.applicationStatus === "WASTE_PICKUP_INPROGRESS";

  const containerStyle = {
    display: isInPickupProgress ? 'flex' : 'block',
    alignItems: isInPickupProgress ? 'center' : 'initial',
    marginBottom: '10px'
  };

  const labelStyle = {
    minWidth: isInPickupProgress ? '180px' : 'auto',
    flexShrink: isInPickupProgress ? 0 : 'initial'
  };

  // Initially the files state should just be empty, as we don't have the actual File objects
  const [files, setFiles] = useState({ 
    siteMediaPhoto: null, 
    siteStack: null 
  });

   // Initialize state for quantity and units for each waste type
   const [wasteDetails, setWasteDetails] = useState(() => {
    // If we have existing form data, use it
    if (formData?.wasteType?.wasteDetails) {
      return formData.wasteType.wasteDetails;
    }
    
    // If we have application details, convert to the required format
    if (applicationDetails?.wasteTypeDetails && Array.isArray(applicationDetails.wasteTypeDetails)) {
      const detailsByType = {};
      
      // Group details by waste type
      applicationDetails.wasteTypeDetails.forEach(detail => {
        if (!detailsByType[detail.wasteType]) {
          detailsByType[detail.wasteType] = {
            quantity: detail.quantity || 0,
            unit: detail.unit || "Kilogram"
          };
        }
      });
      
      return detailsByType;
    }
    
    return {};
  });

  


  // this useEffect to set the proper UI state when component loads
  useEffect(() => {
    // If we have fileStoreIds in formData but no actual files,
    // we still want to show the "1 File Uploaded" message
    if (formData?.wasteType?.siteMediaPhoto && !files.siteMediaPhoto) {
      setFileUploads((prev) => ({ ...prev, siteMediaPhoto: formData.wasteType.siteMediaPhoto }));
    }
    if (formData?.wasteType?.siteStack && !files.siteStack) {
      setFileUploads((prev) => ({ ...prev, siteStack: formData.wasteType.siteStack }));
    }
  }, [formData]);

  // Calculate total waste in tons whenever wasteDetails changes
  useEffect(() => {
    if(isEmployee){
    const totalWasteInTons = calculateTotalWasteInTons(wasteDetails);
    // Format the total with ton unit and set it as the waste quantity
    setwasteQuantity(formatWasteQuantity(totalWasteInTons));
    }
  }, [wasteDetails, isEmployee]);

  const handleFileUpload = async (e, fieldName) => {
    const file = e.target.files[0];
    
    if (!file) return;
    
    if (file.size >= 5242880) {
      setError(t("CS_MAXIMUM_UPLOAD_SIZE_EXCEEDED"));
      return;
    }
    
    // Set the appropriate loading state based on field name
    if (fieldName === "siteMediaPhoto") {
      setIsUploadingMedia(true);
    } else if (fieldName === "siteStack") {
      setIsUploadingStack(true);
    }
    
    setFileUploads((prev) => ({ ...prev, [fieldName]: null }));
    
    try {
      const response = await Digit.UploadServices.Filestorage("CND", file, Digit.ULBService.getStateId());
      if (response?.data?.files?.length > 0) {
        setFileUploads((prev) => ({ ...prev, [fieldName]: response.data.files[0].fileStoreId }));
      } else {
        setError(t("CS_FILE_UPLOAD_ERROR"));
      }
    } catch {
      setError(t("CS_FILE_UPLOAD_ERROR"));
    } finally {
      // Reset the appropriate loading state
      if (fieldName === "siteMediaPhoto") {
        setIsUploadingMedia(false);
      } else if (fieldName === "siteStack") {
        setIsUploadingStack(false);
      }
    }
  };

  const { data: waste_Material_Type } = Digit.Hooks.useEnabledMDMS(Digit.ULBService.getStateId(), CND_VARIABLES.MDMS_MASTER, [{ name: "WasteType" }], {
    select: (data) => {
      const formattedData = data?.[CND_VARIABLES.MDMS_MASTER]?.["WasteType"];
      return formattedData?.filter((item) => item.active === true);
    },
  });
  
  let common = [];
  waste_Material_Type &&
    waste_Material_Type.map((waste_material) => {
      common.push({ i18nKey: `${waste_material.name}`, code: `${waste_material.code}`, value: `${waste_material.name}` });
    });

  function setWasteQuantity(e) {
    setwasteQuantity(e.target.value);
  }

  const handleWasteTypeChange = (selectedTypes) => {
    setwasteMaterialType(selectedTypes);
     // Initialize details for newly selected waste types
     const updatedDetails = { ...wasteDetails };
     selectedTypes.forEach(type => {
       if (!updatedDetails[type.code]) {
         updatedDetails[type.code] = {
           quantity: 0,
           unit: "Kilogram"
         };
       }
     });
     
     // Remove details for deselected waste types
     Object.keys(updatedDetails).forEach(key => {
       if (!selectedTypes.some(type => type.code === key)) {
         delete updatedDetails[key];
       }
     });
     
     setWasteDetails(updatedDetails);
   };
 
   // Handle quantity change for a specific waste type
   const handleQuantityChange = (wasteType, value) => {
     setWasteDetails(prev => ({
       ...prev,
       [wasteType]: {
         ...prev[wasteType],
         quantity: value
       }
     }));
   };
 
   // Handle unit change for a specific waste type
   const handleUnitChange = (wasteType, unit) => {
     setWasteDetails(prev => ({
       ...prev,
       [wasteType]: {
         ...prev[wasteType],
         unit: unit
       }
     }));
   };

   // Available unit options
  const unitOptions = ["Kilogram", "Ton", "Metric Ton"];

  // Handle adding a new waste type from the WasteTypeTable component
  const handleAddWasteType = (wasteType, quantity, unit) => {
    // Add to wasteMaterialType if not already present
    if (!wasteMaterialType.some(type => type.code === wasteType.code)) {
      setwasteMaterialType(prev => [...prev, wasteType]);
    }
    
    // Add to wasteDetails
    setWasteDetails(prev => ({
      ...prev,
      [wasteType.code]: {
        quantity: quantity || "",
        unit: unit || "Kilogram"
      }
    }));
  };


  useEffect(() => {
    if(isEmployee){
    onSelect(config?.key, {
      wasteMaterialType,
      wasteQuantity,
      pickupDate,
      wasteDetails
    })};
  }, [wasteMaterialType, wasteQuantity, pickupDate, wasteDetails]);

  const goNext = () => {
    // Get the total waste in tons
    const totalWasteInTons = calculateTotalWasteInTons(wasteDetails);
    
    let wasteTypeStep = {
      ...formData.wasteType,
      wasteMaterialType,
      wasteQuantity: isEmployee? formatWasteQuantity(totalWasteInTons):wasteQuantity, // Use the converted total
      pickupDate,
      siteMediaPhoto: fileUploads.siteMediaPhoto,
      siteStack: fileUploads.siteStack,
      wasteDetails,
    };
    onSelect(config.key, { ...formData[config.key], ...wasteTypeStep }, false);
  };

  
  return (
    <React.Fragment>
      <FormStep config={config} onSelect={goNext} t={t} isDisabled={!wasteMaterialType || !pickupDate}>
        <div>
          {error && <div className="error-message">{error}</div>}
          
          <CardLabel>
            {`${t("CND_WASTE_TYPE")}`} <span className="astericColor">*</span>
          </CardLabel>
          { isEmployee ?
          (
            <WasteTypeTable
              selectedWasteTypes={wasteMaterialType}
              wasteDetails={wasteDetails}
              onQuantityChange={handleQuantityChange}
              onUnitChange={handleUnitChange}
              t={t}
              unitOptions={unitOptions}
              availableWasteTypes={common}
              onAddWasteType={handleAddWasteType}
            />)
          :
         (<MultiSelectDropdown 
          options={common} 
          selectedValues={wasteMaterialType} 
          onChange={setwasteMaterialType} 
          optionKey="i18nKey" 
          t={t} 
          style={{inputStyles}}
          />
          )}
         
          <div style={containerStyle}>
          <CardLabel style={labelStyle}>
            {`${t("CND_WASTE_QUANTITY")}`}<span className="astericColor">*</span>
          </CardLabel>
         { isEmployee? 
         <span style={{fontWeight:"bold"}}>{wasteQuantity}</span>
         :
          <TextInput
            t={t}
            type={"text"}
            isMandatory={true}
            optionKey="i18nKey"
            name="wasteQuantity"
            value={wasteQuantity}
            onChange={setWasteQuantity}
            style={{width:isInPickupProgress?"72%":userType === "EMPLOYEE" ? "50%" : "100%"}}
            ValidationRequired={true}
            {...(validation = {
              isRequired: true,
              pattern: "^[0-9]+(\\.[0-9]+)?$",
              type: "number",
              title: "",
            })}
          />}
        </div>
        <div style={containerStyle}>
          <CardLabel style={labelStyle}>{t("CND_SCHEDULE_PICKUP")}</CardLabel>
          <DatePicker
            date={pickupDate}
            name="pickupDate"
            onChange={setpickupDate}
            placeholder={"From (mm/yy)"}
            inputFormat="MM/yy"
            style={inputStyles}
            min={new Date().toISOString().split("T")[0]} // Prevents selecting past dates
            rules={{
              required: t("CORE_COMMON_REQUIRED_ERRMSG"),
              validDate: (val) => (/^\d{4}-\d{2}$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")), // Validates MM/YY format
            }}
          />
          </div>
         { !isEmployee && (
          <React.Fragment>
          <CardLabel>{`${t("CND_SITE_MEDIA")}`}</CardLabel>
          <div style={{ marginBottom: "15px", width:"100%" }}>
            <UploadFile
              onUpload={(e) => handleFileUpload(e, "siteMediaPhoto")}
              onDelete={() => setFileUploads((prev) => ({ ...prev, siteMediaPhoto: null }))}
              id={"CND"}
              message={
                isUploadingMedia ? (
                  <div style={{ display: "flex", alignItems: "center", gap: "8px" }}>
                    <LoadingSpinner />
                    <span>Uploading...</span>
                  </div>
                ) : fileUploads.siteMediaPhoto ? (
                  "1 File Uploaded"
                ) : (
                  "No File Uploaded"
                )
              }
              accept=".pdf, .jpeg, .jpg, .png"
              style={{inputStyles}}
            />
          </div>
          
          <CardLabel>{`${t("CND_SITE_STACK")}`}</CardLabel>
          <div style={{ marginBottom: "20px", width:"100%" }}>
            <UploadFile
              onUpload={(e) => handleFileUpload(e, "siteStack")}
              onDelete={() => setFileUploads((prev) => ({ ...prev, siteStack: null }))}
              id={"CND"}
              message={
                isUploadingStack ? (
                  <div style={{ display: "flex", alignItems: "center", gap: "8px" }}>
                    <LoadingSpinner />
                    <span>Uploading...</span>
                  </div>
                ) : fileUploads.siteStack ? (
                  "1 File Uploaded"
                ) : (
                  "No File Uploaded"
                )
              }
              accept=".pdf, .jpeg, .jpg, .png"
              style={{inputStyles}}
            />
          </div>
          </React.Fragment>)}
        </div>
      </FormStep>
    </React.Fragment>
  );
};

export default WasteType;