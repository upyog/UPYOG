import { CardLabel,LabelFieldPair, TextInput, CardLabelError, RadioButtons, Dropdown, UploadFile  } from "@upyog/digit-ui-react-components";

import  FormStep  from "../../../../react-components/src/molecules/FormStep";
import React, { useEffect, useState } from "react";
import { useLocation } from "react-router-dom";
import Timeline from "../components/TLTimeline";

const PTBuildingPermission = ({ t, config, onSelect, value, userType, formData, setError: setFormError, clearErrors: clearFormErrors, formState, onBlur }) => {
  //let index = window.location.href.charAt(window.location.href.length - 1);
  console.log("formData11-PTBuildingPermission----",formData)
  let index = window.location.href.split("/").pop();
  let validation = {};
  const onSkip = () => onSelect();
  const { pathname } = useLocation();
  const presentInModifyApplication = pathname.includes("modify");
  const isMutation = pathname.includes("property-mutation");


  const [uploadedFile, setUploadedFile] = useState(
    !isMutation ? formData?.buildingPermission?.documents?.buildingPermissionProof?.fileStoreId || null : formData?.[config.key]?.fileStoreId
  );
//  {code: 'PT_COMMON_YES', i18nKey: "PT_COMMON_YES"}: {code: 'PT_COMMON_NO', i18nKey: "PT_COMMON_NO"}

  const [buildingPermissionRequired, setBuildingPermissionRequired] = useState(formData?.buildingPermission?.buildingPermissionRequired || {code: 'PT_COMMON_NO', i18nKey: "PT_COMMON_NO"});
  const [file, setFile] = useState(formData?.buildingPermission?.documents?.buildingPermissionProof);
  
  const [hidden, setHidden] = useState(true);
 
  const [error, setError] = useState(null);

  
  // useEffect(()=>{
  //   if(formData?.documents){
  //     let obj = formData?.documents.find(o => o.documentType === "PROOF_OF_BUILDINGPERMISSION") || null;
  //     if(obj) {
  //       setBuildingPermissionRequired({code: 'PT_COMMON_YES', i18nKey: "PT_COMMON_YES"});
  //       setFile(obj);
  //       setUploadedFile(obj?.fileStoreId)
  //     }
  //   }
  // },[])
 
  
  const goNext=()=> {
    // sessionStorage.setItem("buildingPermission", electricity.i18nKey);
    let fileStoreId = uploadedFile;
    let fileDetails = file;
    if (fileDetails) fileDetails.documentType = 'PROOF_OF_BUILDINGPERMISSION';
    if (fileDetails) fileDetails.fileStoreId = fileStoreId ? fileStoreId : null;
    let buildingPermission =  { buildingPermissionRequired: buildingPermissionRequired};

    if (buildingPermission && buildingPermission.documents) {
        buildingPermission.documents['buildingPermissionProof'] = fileDetails;
    } else {
      buildingPermission["documents"] = {};
      buildingPermission.documents['buildingPermissionProof'] = fileDetails;
    }
    // if (!isMutation) onSelect(config.key, buildingPermission, "", index);
    // else onSelect(config.key, { documentType: dropdownValue, fileStoreId }, "", index);
    onSelect("buildingPermission", buildingPermission, "", index);
  };

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


  const { data: mdmsData, isLoading } = Digit.Hooks.useCommonMDMS(
    Digit.ULBService.getStateId(),
    "PropertyTax",
    ["ExemptionList"],
    {
      select: (data) => {
        
        return {
          ExemptionList: data?.PropertyTax?.ExemptionList?.filter((exempt) => exempt.active)?.map((exempt) => ({
            ...exempt,
            i18nKey: `PT_EXEMPTION_TYPE_${exempt.code}`,
            code: exempt.code,
          })),
        };
      },
      retry: false,
      enable: false,
    }
  );
  
  function selectBuildingPermissionRequired(value) {
    setBuildingPermissionRequired(value);
    if(value?.code == 'PT_COMMON_NO') {
        setFile('')
    }
  }
//   function selectBuildingPermissionType(value) {
//     setBuildingPermissionType(value)
//   }
  function isAllowedNext() {
    if(!buildingPermissionRequired){
      return true
    }else if(buildingPermissionRequired && buildingPermissionRequired?.code == 'PT_COMMON_YES' && !file) {
      return true
    } else {
      return false;
    }
    
  }

  function selectfile(e) {
    setFile(e.target.files[0]);
  }

  if (userType === "employee") {
    return inputs?.map((input, index) => {
      return (
        <React.Fragment>
          <LabelFieldPair key={index}>
            {/* <CardLabel className="card-label-smaller">{t(input.label) + " *"}</CardLabel>
            <div className="field">

              <TextInput
                key={input.name}
                id={input.name}
                //isMandatory={config.isMandatory}
                value={electricity}
                onChange={handleElectricityChange}
                //onChange={setElectricityNo}
                onSelect={goNext}
                placeholder={"Enter a valid 10-digit electricity number"}
                {...input.validation}
                onBlur={onBlur}

              // autoFocus={presentInModifyApplication}
              />

            </div> */}
          </LabelFieldPair>
          {formState.touched[config.key] ? (
            <CardLabelError style={{ width: "70%", marginLeft: "30%", fontSize: "12px", marginTop: "-21px" }}>
              {formState.errors?.[config.key]?.message}
            </CardLabelError>
          ) : null}
        </React.Fragment>
      );
    });
  }
  return (
    <React.Fragment>
      {window.location.href.includes("/citizen") ? <Timeline currentStep={1} /> : null}
      
        <FormStep
          config={config}
          onSelect={goNext}
          onSkip={onSkip}
          t={t}
          isDisabled={isAllowedNext()}
        >
          <CardLabel>{`${t("Obtained Building Permission")}*`}</CardLabel>
          <RadioButtons
            t={t}
            optionsKey="i18nKey"
            isMandatory={config.isMandatory}
            //options={menu}
            options={[{code: 'PT_COMMON_YES', i18nKey: "PT_COMMON_YES"},{code: 'PT_COMMON_NO', i18nKey: "PT_COMMON_NO"}]}
            selectedOption={buildingPermissionRequired}
            onSelect={selectBuildingPermissionRequired}
          />
          {buildingPermissionRequired?.code =='PT_COMMON_YES' && (
            <div>
              {/* <CardLabel>{`${t("PT_EXEMPTION_TYPE")}*`}</CardLabel>
              <div className={"form-pt-dropdown-only"}>
                <Dropdown
                  t={t}
                  optionKey="i18nKey"
                  isMandatory={config.isMandatory}
                  option={mdmsData?.ExemptionList}
                  selected={formData?.buildingPermission?.exemptionType}
                  select={(e) => selectExemptionType(e)}
                />
              </div> */}
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
            </div>
            
          )}
          
        
      </FormStep>
    </React.Fragment>
  );



};

export default PTBuildingPermission;