import { CardLabel, FormStep, LabelFieldPair, TextInput, CardLabelError } from "@egovernments/digit-ui-react-components";
import React, { useEffect, useState } from "react";
import { useLocation } from "react-router-dom";
import Timeline from "../components/TLTimeline";

const Electricity = ({ t, config, onSelect, value, userType, formData, setError: setFormError, clearErrors: clearFormErrors, formState, onBlur }) => {
  //let index = window.location.href.charAt(window.location.href.length - 1);
  let index = window.location.href.split("/").pop();
  let validation = {};
  const onSkip = () => onSelect();
 
  let electricity;
  let setElectricity;
  const [hidden, setHidden] = useState(true);
  if (!isNaN(index)) {
   
    [electricity, setElectricity] = useState(formData.units && formData.units[index] && formData.units[index].electricity);

  } else {
   
     [electricity, setElectricity] = useState(formData?.electricity);
  }
  const [error, setError] = useState(null);
  const [unitareaerror, setunitareaerror] = useState(null);
  const [areanotzeroerror, setareanotzeroerror] = useState(null);

  const { pathname } = useLocation();
  const presentInModifyApplication = pathname.includes("modify");
  function setElectricityNo(e) {
    //electricity=true;
    setElectricity(e.target.value);
    if(electricity?.length==9 ){
      
      setHidden(false);
    //setElectricity(e.target.value);
  }

}

//   function setPropertyfloorarea(e) {
//     setfloorarea(e.target.value);
//     setunitareaerror(null);
//     setareanotzeroerror(null);
//     if (formData?.PropertyType?.code === "BUILTUP.INDEPENDENTPROPERTY" && parseInt(formData?.units[index]?.builtUpArea) < e.target.value) {
//       setunitareaerror("PT_TOTUNITAREA_LESS_THAN_BUILTUP_ERR_MSG");
//     }
//     if (formData?.PropertyType?.code === "BUILTUP.SHAREDPROPERTY" && parseInt(formData?.floordetails?.builtUpArea) < e.target.value) {
//       setunitareaerror("PT_SELFOCCUPIED_AREA_LESS_THAN_BUILTUP");
//     }
//     if (parseInt(e.target.value) == 0) {
//       setareanotzeroerror("PT_AREA_NOT_0_MSG");
//     }
//   }


//   const goNext = () => {
//     if (!isNaN(index)) {
//       let unit = formData.units && formData.units[index];
//       //units["RentalArea"] = RentArea;
//       //units["AnnualRent"] = AnnualRent;
//       if (
//         (formData?.isResdential?.i18nKey === "PT_COMMON_YES" || formData?.usageCategoryMajor?.i18nKey == "PROPERTYTAX_BILLING_SLAB_NONRESIDENTIAL") &&
//         formData?.PropertyType?.i18nKey !== "COMMON_PROPTYPE_VACANT"
//       ) {
//         sessionStorage.setItem("area", "yes");
//       } else {
//         sessionStorage.setItem("area", "no");
//       }

//       let floordet = { ...unit, floorarea };
//       onSelect(config.key, floordet, false, index);
//     } else {
//       if (
//         (formData?.isResdential?.i18nKey === "PT_COMMON_YES" || formData?.usageCategoryMajor?.i18nKey == "PROPERTYTAX_BILLING_SLAB_NONRESIDENTIAL") &&
//         formData?.PropertyType?.i18nKey !== "COMMON_PROPTYPE_VACANT"
//       ) {
//         sessionStorage.setItem("area", "yes");
//       } else if (formData?.PropertyType?.code === "VACANT") {
//         sessionStorage.setItem("area", "vacant");
//       } else {
//         sessionStorage.setItem("area", "no");
//       }

//       onSelect("landarea", { floorarea });
//     }
//   };
  //const onSkip = () => onSelect();

  function onChange(e) {
    //electricity=true;
    setElectricity(e.target.value);
    if(electricity?.length== 9){
      
      setHidden(false);

    }
      //setElectricity(e.target.value);
   
 
  }
  function goNext()  {
    
      onSelect(config.key, electricity);
   
  };

//   useEffect(() => {
//     if (userType === "employee") {
//       if (!Number(floorarea)) setFormError(config.key, { type: "required", message: t("CORE_COMMON_REQUIRED_ERRMSG") });
//       else if (isNaN(floorarea)) setFormError(config.key, { type: "invalid", message: t("ERR_DEFAULT_INPUT_FIELD_MSG") });
//       else clearFormErrors(config.key);
     
//       onSelect(config.key, floorarea);
//     }
//   }, [floorarea]);
  useEffect(() => {
    if (userType === "employee") {
      console.log("config",config,formData)
    //   if (!Number(floorarea)) setFormError(config.key, { type: "required", message: t("CORE_COMMON_REQUIRED_ERRMSG") });
    //   else if (isNaN(floorarea)) setFormError(config.key, { type: "invalid", message: t("ERR_DEFAULT_INPUT_FIELD_MSG") });
    //   else clearFormErrors(config.key);
      onSelect(config.key, electricity);
      //onSelect("electricity", electricity);
    }
  }, [electricity]);

  useEffect(() => {
    if (presentInModifyApplication && userType === "employee") {
     
      setElectricity(formData?.originalData?.electricity)
    }
  }, []);

  const inputs = [
    {
      label: "PT_ELECTRICITY_LABEL",
      type: "text",
      name: "elec",
      error: "ERR_HRMS_INVALID_ELECTRICITY_NO",
      validation: {
        required: true,
        minLength: 10,
        maxLength: 10
      }
    },
    {
      label: "PT_ELECTRICITY_UID_LABEL",
      type: "text",
      name: "elec",
      error: "ERR_HRMS_INVALID_ELECTRICITY_NO",
      validation: {
        required: true,
        minLength: 10,
        maxLength: 10
      }
    },
    
  ];

  if (userType === "employee") {
    return inputs?.map((input, index) => {
      return (
        <React.Fragment>
          <LabelFieldPair key={index}>
            <CardLabel className="card-label-smaller">{t(input.label) + " *"}</CardLabel>
            <div className="field">
              <TextInput
                key={input.name}
                id={input.name}
                isMandatory={config.isMandatory}
                value={electricity}
                onChange={onChange}
                onSelect={goNext}
                {...input.validation}
                onBlur={onBlur}
                
                // autoFocus={presentInModifyApplication}
              />
            </div>
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
     {window.location.href.includes("/citizen") ? <Timeline currentStep={1}/> : null}
    <FormStep
      config={config}
      onChange={onChange}
      
      onSelect={goNext}
      onSkip={onSkip}
      t={t}
      isDisabled={hidden}
      showErrorBelowChildren={true}
    >
      <CardLabel>{`${t("PT_ELECTRICITY")}`}</CardLabel>
      <TextInput
        t={t}
        type={"number"}
        isMandatory={false}
        optionKey="i18nKey"
        name="electricity"
        value={electricity}
        onChange={setElectricityNo}
        {...(validation = { 
          required: true,
          minLength: 10,
          maxLength: 10,
         })}
      />
    </FormStep>
    </React.Fragment>
  );



};

export default Electricity;