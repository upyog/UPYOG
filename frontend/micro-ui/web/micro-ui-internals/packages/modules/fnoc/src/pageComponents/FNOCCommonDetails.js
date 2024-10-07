import React, { useState, useEffect } from "react";
import { FormStep, CardLabel, RadioOrSelect} from "@nudmcdgnpm/digit-ui-react-components";
import { TextInput, Toast } from "@nudmcdgnpm/digit-ui-react-components";
import { useTranslation } from "react-i18next";

const FNOCCommonDetails = ({ config, formData,onSelect }) => {
  const { t } = useTranslation();
  const [showToast, setShowToast] = useState(null);
  const [nocType, setnocType] = useState(formData?.commonDetails?.nocType || "");
  let index=0;
const [nocNumber, setnocNumber] = useState(formData?.commonDetails?.nocNumber || ""
);
// const handleSearchClick = () => {
//   if (nocNumber) {
//     setShouldFetchDetails(true);
//   } else {
//     console.log("Property ID is required");
//   }
// };

  let SearchTypes = [ 
      {
        code : "NEW",
        i18nKey:"WS_NOC_TYPE_NEW_RADIOBUTTON_SEARCH",
        value:"NOC_TYPE_NEW_RADIOBUTTON"
      },
      {
        code : "PROVISIONAL",
        i18nKey:"WS_NOC_TYPE_PROVISIONAL_RADIOBUTTON_SEARCH",
        value:"NOC_TYPE_PROVISIONAL_RADIOBUTTON"
      }
    ];


  function selectNocType(value) {
    setnocType(value);
  }
  function setNocNumber(e) {
    const input = e.target.value.replace(/[^a-zA-Z\s]/g, ""); 
    setnocNumber(input);
  }

  const goNext = () => {
    let owner = formData.commonDetails && formData.commonDetails[index];
    let ownerStep;
    
        ownerStep = {
            ...owner,
            nocType,
            nocNumber
        };
        onSelect(config.key, { ...formData[config.key], ...ownerStep }, false, index);
};

  const onSkip = () => onSelect();

  return (
    <div style={{ marginTop: "16px" }}>
      <FormStep
       config={config}
       onSelect={goNext}
       onSkip={onSkip}
       t={t}
      >
        <RadioOrSelect
             className="form-field"
             isMandatory={true}
              t={t}
              optionKey="code"
              name="SearchType"
              options={SearchTypes}
              value={nocType}
              selectedOption={nocType}
              onSelect={selectNocType}
              // {...(validation = {
              //   isRequired: true,
              //   title: t("WS_SEARCH_TYPE_MANDATORY"),
              // })}
          />
  
        {nocType && nocType?.code == "NEW"&& <div style={{border:"solid",borderRadius:"5px",padding:"10px",paddingTop:"20px",marginTop:"10px",borderColor:"#f3f3f3",background:"#FAFAFA",marginBottom:"20px"}} >
        <CardLabel>{`${t("NOC_PROVISIONAL_FIRE_NOC_NO_LABEL")}`}
          
        </CardLabel>
            <TextInput
              t={t}
              type="text"
              isMandatory={false}
              optionKey="i18nKey"
              name="nocNumber"
              placeholder="NOC_PROVISIONAL_FIRE_NOC_NO_PLACEHOLDER"
              value={nocNumber}
              style={{ width: "87%" }}
              onChange={setNocNumber}
              validation={{
                // isRequired: true,
                pattern: "^[a-zA-Z ]+$",
                type: "text",
                title: t("CHB_NAME_ERROR_MESSAGE"),
              }}
            />
            
             
        </div>}
      </FormStep>
      {showToast && (
        <Toast
          isDleteBtn={true}
          error={showToast.key}
          label={t(showToast.label)}
          onClose={() => {
            setShowToast(null);
          }}
        />
      )}
    </div>
  );
};

export default FNOCCommonDetails;






