import { CardLabel,LabelFieldPair, TextInput, CardLabelError, RadioButtons, Dropdown, UploadFile  } from "@egovernments/digit-ui-react-components";

import  FormStep  from "../../../../react-components/src/molecules/FormStep";
import React, { useEffect, useState } from "react";
import { useLocation } from "react-router-dom";
import Timeline from "../components/TLTimeline";

const ExemptionDetails = ({ t, config, onSelect, value, userType, formData, setError: setFormError, clearErrors: clearFormErrors, formState, onBlur }) => {
  //let index = window.location.href.charAt(window.location.href.length - 1);
  console.log("formData11----",formData)
  let index = window.location.href.split("/").pop();
  let validation = {};
  const onSkip = () => onSelect();
  const { pathname } = useLocation();
  const presentInModifyApplication = pathname.includes("modify");
  const isMutation = pathname.includes("property-mutation");


  const [uploadedFile, setUploadedFile] = useState(
    !isMutation ? formData?.exemption?.documents?.exemptionProof?.fileStoreId || null : formData?.[config.key]?.fileStoreId
  );

  const [exemptionRequired, setExemptionRequired] = useState(formData?.exemption ? {code: 'PT_COMMON_YES', i18nKey: "PT_COMMON_YES"}: {code: 'PT_COMMON_NO', i18nKey: "PT_COMMON_NO"});
  const [exemptionType, setExemptionType] = useState(formData?.exemption);
  const [file, setFile] = useState(formData?.exemption?.documents?.exemptionProof);
  
  const [hidden, setHidden] = useState(true);
 
  const [error, setError] = useState(null);
 
  
  const goNext=()=> {
    // sessionStorage.setItem("exemption", electricity.i18nKey);
    let fileStoreId = uploadedFile;
    let fileDetails = file;
    if (fileDetails) fileDetails.documentType = 'PROOF_OF_EXEMPTION';
    if (fileDetails) fileDetails.fileStoreId = fileStoreId ? fileStoreId : null;
    console.log("formData----",formData,fileDetails)
    let exemption =  { exemptionRequired: exemptionRequired,  exemptionType: exemptionType};
    console.log("formData22----",formData,exemption)
    if (exemption && exemption.documents) {
      exemption.documents["exemptionProof"] = fileDetails;
    } else {
      exemption["documents"] = [];
      exemption.documents["exemptionProof"] = fileDetails;
    }
    console.log("exemption===",exemption)
    // if (!isMutation) onSelect(config.key, exemption, "", index);
    // else onSelect(config.key, { documentType: dropdownValue, fileStoreId }, "", index);
    onSelect("exemption", exemption, "", index);
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
  
  function selectExemptionRequired(value) {
    setExemptionRequired(value);
    if(value?.code == 'PT_COMMON_NO') {
      selectExemptionType('')
    }
  }
  function selectExemptionType(value) {
    setExemptionType(value)
  }
  function isAllowedNext() {
    if(!exemptionRequired){
      return true
    }else if(exemptionRequired && exemptionRequired?.code == 'PT_COMMON_YES' && !exemptionType) {
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
      {window.location.href.includes("/citizen") ? <Timeline currentStep={4} /> : null}
      
        <FormStep
          config={config}
          onSelect={goNext}
          onSkip={onSkip}
          t={t}
          isDisabled={isAllowedNext()}
        >
          <CardLabel>{`${t("PT_ELIGIBLE_FOR_EXEMPTION")}*`}</CardLabel>
          <RadioButtons
            t={t}
            optionsKey="i18nKey"
            isMandatory={config.isMandatory}
            //options={menu}
            options={[{code: 'PT_COMMON_YES', i18nKey: "PT_COMMON_YES"},{code: 'PT_COMMON_NO', i18nKey: "PT_COMMON_NO"}]}
            selectedOption={exemptionRequired}
            onSelect={selectExemptionRequired}
          />
          {exemptionRequired?.code =='PT_COMMON_YES' && (
            <div>
              <CardLabel>{`${t("PT_EXEMPTION_TYPE")}*`}</CardLabel>
              <div className={"form-pt-dropdown-only"}>
                <Dropdown
                  t={t}
                  optionKey="i18nKey"
                  isMandatory={config.isMandatory}
                  option={mdmsData?.ExemptionList}
                  selected={formData?.exemption?.exemptionType}
                  select={(e) => selectExemptionType(e)}
                />
              </div>
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
              />
            </div>
            
          )}
          
        
      </FormStep>
    </React.Fragment>
  );



};

export default ExemptionDetails;