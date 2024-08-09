import { CardLabel, CardLabelError, FormStep, LabelFieldPair, TextInput } from "@nudmcdgnpm/digit-ui-react-components";
import React, { useEffect, useState } from "react";
import { useLocation } from "react-router-dom";
import Timeline from "../components/ASTTimeline";

const AssetPincode = ({ t, config, onSelect, formData = {}, userType}) => {
  
  const tenants = Digit.Hooks.asset.useTenants();
  const { pathname } = useLocation();
  const presentInModifyApplication = pathname.includes("modify");


  const [pincode, setPincode] = useState(() => {
    if (presentInModifyApplication && userType === "employee") return formData?.originalData?.address?.pincode || "";
    return formData?.address?.pincode || "";
  });

  
  
  const inputs = [
    {
      label: "AST_PINCODE",
      type: "text",
      name: "pincode",
      validation: {
        minlength: 6,
        maxlength: 7,
        pattern: "[0-9]+",
        max: "9999999",
        title: t("PTR_ADDRESS_PINCODE_INVALID"),
      },
      style: {
        width: "50%"
      }
    },
  ];
  const [pincodeServicability, setPincodeServicability] = useState(null);
  const [error, setLocalError] = useState("");

  useEffect(() => {
    if (formData?.address?.pincode) {
      setPincode(formData.address.pincode);
    }
  }, [formData?.address?.pincode]);

  function onChange(e) {
    setPincode(e.target.value);
    setPincodeServicability(null);
    setLocalError("");
    let validPincode = Digit.Utils.getPattern("Pincode").test(e.target.value);

    if (userType === "employee") {
      if (e.target.value && !validPincode) setLocalError(t("ERR_DEFAULT_INPUT_FIELD_MSG"));
      if (validPincode) {
        const foundValue = tenants?.find((obj) => obj.pincode?.find((item) => item.toString() === e.target.value));
        if (!foundValue) setLocalError(t("PTR_COMMON_PINCODE_NOT_SERVICABLE"));
      }
      onSelect(config.key, { ...formData.address, pincode: e.target.value });
    }
  }

  const goNext = async (data) => {
    const foundValue = tenants?.find((obj) => obj.pincode?.find((item) => item == data?.pincode));
    if (foundValue) {
      onSelect(config.key, { pincode });
    } else {
      setPincodeServicability("COMMON_PINCODE_NOT_SERVICABLE");
    }
  };

  if (userType === "employee") {
    return inputs?.map((input, index) => {
      return (
        <React.Fragment>
          <LabelFieldPair key={index}>
            <CardLabel className="card-label-smaller">{t(input.label)}</CardLabel>
            <div className="field">
              <TextInput key={input.name} style={input.style} value={pincode} onChange={onChange} {...input.validation} disable={presentInModifyApplication} autoFocus={presentInModifyApplication} />
            </div>
          </LabelFieldPair>
          {error ? <CardLabelError style={{ width: "70%", marginLeft: "30%", fontSize: "12px", marginTop: "-21px" }}>{error}</CardLabelError> : null}
        </React.Fragment>
      );
    });
  }
  const onSkip = () => onSelect();
  return (
    <React.Fragment>
    {window.location.href.includes("/employee") ? <Timeline currentStep={3}/> : null}
    <FormStep
      t={t}
      config={{ ...config, inputs }}
      onSelect={goNext}
      _defaultValues={{ pincode }}
      onChange={onChange}
      onSkip={onSkip}
      forcedError={t(pincodeServicability)}
    ></FormStep>
    </React.Fragment>
  );
};

export default AssetPincode;
