import { FormStep, TextInput, CardLabel, LabelFieldPair, CardLabelError } from "@upyog/digit-ui-react-components";
import React, { useState, useEffect, Fragment } from "react";
import { useLocation } from "react-router-dom";
import Timeline from "../components/TLTimelineInFSM";

const FSMSelectPincode = ({ t, config, onSelect, formData = {}, userType, register, errors, props }) => {
  const tenants = Digit.Hooks.fsm.useTenants();
  const [pincode, setPincode] = useState(formData?.cpt?.details?.address?.pincode);
  const [pincodeServicability, setPincodeServicability] = useState(null);

  const { pathname } = useLocation();
  const presentInModifyApplication = pathname.includes("modify");

  let property = sessionStorage?.getItem("Digit_FSM_PT")
  if (property !== "undefined")
  {
    property = JSON.parse(sessionStorage?.getItem("Digit_FSM_PT"))
  }
  console.log("dddd11111",formData)
  const inputs = [
    {
      label: "CORE_COMMON_PINCODE",
      type: "text",
      name: "pincode",
      validation: {
        minlength: 6,
        maxlength: 6,
        pattern: "^[1-9][0-9]*",
        max: "9999999",
        title: t("CORE_COMMON_PINCODE_INVALID"),
      },
    },
  ];
  // useEffect(()=>{
  //   if(property?.propertyDetails?.address?.pincode){ 
  //       setPincode(property?.propertyDetails?.address?.pincode);   
  //   }
  // },[ property?.propertyDetails?.address?.pincode])

  // useEffect(() => {
  //   if (formData?.address?.pincode) {
  //     setPincode(formData.address.pincode);
  //   }
  //   else if(formData?.cpt?.details?.address?.pincode){
  //     setPincode(formData?.cpt?.details?.address?.pincode)
  //   }
  // }, [formData?.address?.pincode, formData?.cpt?.details?.address?.pincode]);

  // useEffect(() => {
  //   if (formData?.address?.locality?.pincode !== pincode && userType === "employee") {
  //     setPincode(formData?.address?.locality?.pincode || "");
  //     setPincodeServicability(null);
  //   }
  // }, [formData?.address?.locality]);
  useEffect(() => {
    if (formData?.address?.locality?.pincode !== pincode && userType === "employee") {
      setPincode(formData?.address?.locality?.pincode ||formData?.cpt?.details?.address?.pincode||"" );
      setPincodeServicability(null);
    }
  }, [formData?.address?.locality]);

  useEffect(() => {
    if (userType === "employee" && pincode) {
      onSelect(config.key, { ...formData.address, pincode: pincode });
    }
  }, [pincode]);

  function onChange(e) {
    e.preventDefault();
    const newInput = e.target.value; // Get the new input value
    const updatedPincode = newInput; // Update directly based on the current input value
  
    setPincode(updatedPincode); // Update the state with the new value
    setPincodeServicability(null); // Reset serviceability message
  
    if (userType === "employee") {
      console.log("setPincodeServicability");
      const foundValue = tenants?.find((obj) =>
        obj.pincode?.find((item) => item.toString() === updatedPincode)
      );
      if (foundValue) {
        const city = tenants.find((obj) =>
          obj.pincode?.find((item) => item === updatedPincode)
        );
        onSelect(config.key, {
          ...formData.address,
          city,
          pincode: updatedPincode,
          slum: null,
        });
      } else {
        onSelect(config.key, {
          ...formData.address,
          pincode: updatedPincode,
        });
        setPincodeServicability("CS_COMMON_PINCODE_NOT_SERVICABLE");
      }
    }
  }
  

  const goNext = async (data) => {
    const foundValue = tenants?.find((obj) => obj.pincode?.find((item) => item == data?.pincode));
    if (foundValue) {
      onSelect(config.key, { pincode });
    } else {
      setPincodeServicability("CS_COMMON_PINCODE_NOT_SERVICABLE");
    }
  };
  const onSkip = () => onSelect();

  if (userType === "employee") {
    return inputs?.map((input, index) => {
      return (
        <Fragment key={index}>
          <LabelFieldPair>
            <CardLabel className="card-label-smaller">
              {t(input.label)}
              {config.isMandatory ? " * " : null}
            </CardLabel>
            <div className="field">
              <TextInput key={input.name} value={pincode} onChange={onChange} {...input.validation} />
            </div>
          </LabelFieldPair>
          {pincodeServicability && (
            <CardLabelError style={{ width: "70%", marginLeft: "30%", fontSize: "12px", marginTop: "-21px" }}>
              {t(pincodeServicability)}
            </CardLabelError>
          )}
        </Fragment>
      );
    });
  }

  return (
    <React.Fragment>
      <Timeline currentStep={1} flow="APPLY" />
      <FormStep
        t={t}
        config={{ ...config, inputs }}
        onSelect={goNext}
        value={pincode}
        onChange={onChange}
        onSkip={onSkip}
        forcedError={t(pincodeServicability)}
        isDisabled={!pincode}
      ></FormStep>
    </React.Fragment>
  );
};

export default FSMSelectPincode;
