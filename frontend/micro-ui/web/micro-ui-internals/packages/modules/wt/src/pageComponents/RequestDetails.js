import React, { useEffect, useState } from "react";
import { FormStep, TextInput, CardLabel, RadioButtons,CheckBox } from "@nudmcdgnpm/digit-ui-react-components";

/**
 * Major Page which is developed for Request/Booking detail page
 * 
 */


const RequestDetails = ({ t, config, onSelect, userType, formData }) => {
  const user = Digit.UserService.getUser().info;
  const inputStyles = { width: user.type === "EMPLOYEE" ? "50%" : "86%" };
  let validation = {};

  const [tankerType, settankerType] = useState(formData?.requestDetails?.tankerType || "");
  const [tankerQuantity, settankerQuantity] = useState(formData?.requestDetails?.tankerQuantity || 1);
  const [waterQuantity, setwaterQuantity] = useState(formData?.requestDetails?.waterQuantity || "");
  const [deliveryDate, setdeliveryDate] = useState(formData?.requestDetails?.deliveryDate || "");
  const [description, setdescription] = useState(formData?.requestDetails?.description || "");
  const [deliveryTime, setdeliveryTime] = useState(formData?.requestDetails?.deliveryTime || "");
  const [extraCharge, setextraCharge] = useState(formData?.requestDetails?.extraCharge || false)
 


  
  // Custom time input component
  const TimeInput = () => {
    return (
      <div className="flex items-center">
        <TextInput
          type="time"
          value={deliveryTime}
          onChange={(e) => setdeliveryTime(e.target.value)}
          style={inputStyles}
          min="06:00"
          max="23:59"
        />
      </div>
    );
  };

  // Custom TextInput wrapper for quantity controls
  const CustomQuantityInput = ({ value, onChange, ...props }) => {
    const handleQuantityChange = (action) => {
      if (action === 'increment' && value < 10) {
        onChange({ target: { value: value + 1 } });
      } else if (action === 'decrement' && value > 1) {
        onChange({ target: { value: value - 1 } });
      }
    };

    return (
      <div style={{ position: 'relative', width: inputStyles.width }}>
        <TextInput
          {...props}
          type="number"
          value={value}
          onChange={(e) => {
            const newValue = parseInt(e.target.value);
            if (!isNaN(newValue) && newValue >= 1 && newValue <= 10) {
              onChange(e);
            }
          }}
          style={{ ...inputStyles, paddingRight: '30px' }}
        />
        <div style={{
          position: 'absolute',
          right: '340px',
          top: '50%',
          transform: 'translateY(-50%)',
          display: 'flex',
          flexDirection: 'column',
          gap: '5px'
        }}>
          <button
            type="button"
            onClick={() => handleQuantityChange('increment')}
            disabled={value >= 10}
            style={{
              padding: '2px 6px',
              fontSize: '8px',
              border: '1px solid #ccc',
              borderRadius: '2px',
              cursor: value >= 10 ? 'not-allowed' : 'pointer',
              backgroundColor: '#a82227',
              color:'white'
            }}
          >
            ▲
          </button>
          <button
            type="button"
            onClick={() => handleQuantityChange('decrement')}
            disabled={value <= 1}
            style={{
              padding: '2px 6px',
              fontSize: '8px',
              border: '1px solid #ccc',
              borderRadius: '2px',
              cursor: value <= 1 ? 'not-allowed' : 'pointer',
              backgroundColor: '#a82227',
              color:'white'
            }}
          >
            ▼
          </button>
        </div>
      </div>
    );
  };


  const setextrachargeHandler = () => {
    setextraCharge(!extraCharge);
  }

  function setDescription(e) {
    setdescription(e.target.value);
  }

  function setWaterQuantity(e) {
    setwaterQuantity(e.target.value);
  }

  function setDeliveryDate(e) {
    setdeliveryDate(e.target.value);
  }

  const goNext = () => {
    let requestDetails = formData.requestDetails;
    let request = { ...requestDetails, tankerType, deliveryDate, tankerQuantity, waterQuantity, deliveryTime, description, extraCharge };
    onSelect(config.key, request, false);
  };

  const common = [
    {
      code: "TANKER",
      i18nKey: "TANKER",
      value: "Tanker"
    },
    {
      code: "TROLLEY",
      i18nKey: "TROLLEY",
      value: "Trolley"
    }
  ];

  useEffect(() => {
    if (userType === "citizen") {
      goNext();
    }
  }, [tankerType, deliveryDate, tankerQuantity, waterQuantity, deliveryTime, description,extraCharge]);

  return (
    <React.Fragment>
      <FormStep
        config={config}
        onSelect={goNext}
        t={t}
        isDisabled={!tankerType || !deliveryDate || !tankerQuantity || !waterQuantity || !deliveryTime || !description || !extraCharge}
      >
        <div>
          <CardLabel>{`${t("WT_TANKER_TYPE")}`} <span className="astericColor">*</span></CardLabel>
          <RadioButtons
            t={t}
            options={common}
            style={{ display: "flex", flexWrap: "wrap", maxHeight: "30px" }}
            innerStyles={{ minWidth: "24%" }}
            optionsKey="i18nKey"
            name={`tankerType`}
            value={tankerType}
            selectedOption={tankerType}
            onSelect={settankerType}
            labelKey="i18nKey"
            isPTFlow={true}
          />
          
          <CardLabel>{`${t("WT_TANKER_QUANTITY")}`} <span className="astericColor">*</span></CardLabel>
          <CustomQuantityInput
            t={t}
            name="tankerQuantity"
            value={tankerQuantity}
            onChange={(e) => settankerQuantity(parseInt(e.target.value))}
            ValidationRequired={true}
            {...(validation = {
              isRequired: true,
              pattern: "^[0-9]+$",
              type: "tel",
              title: t("PT_NAME_ERROR_MESSAGE"),
            })}
          />

          <CardLabel>{`${t("WT_WATER_QUANTITY")}`} <span className="astericColor">*</span></CardLabel>
          <TextInput
            t={t}
            type={"text"}
            isMandatory={false}
            optionKey="i18nKey"
            name="waterQuantity"
            style={inputStyles}
            value={waterQuantity}
            onChange={setWaterQuantity}
            ValidationRequired={true}
            {...(validation = {
              isRequired: true,
              pattern: "^[0-9 ]+$",
              type: "tel",
              title: t("PT_NAME_ERROR_MESSAGE"),
            })}
          />

          <CardLabel>{`${t("WT_DELIVERY_DATE")}`} <span className="astericColor">*</span></CardLabel>
          <TextInput
            t={t}
            type={"date"}
            isMandatory={false}
            optionKey="i18nKey"
            name="deliveryDate"
            value={deliveryDate}
            onChange={setDeliveryDate}
            style={inputStyles}
            min={new Date().toISOString().split('T')[0]}
            rules={{
              required: t("CORE_COMMON_REQUIRED_ERRMSG"),
              validDate: (val) => (/^\d{4}-\d{2}-\d{2}$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")),
            }}
          />

          <CardLabel>{`${t("WT_DELIVERY_TIME")}`} <span className="astericColor">*</span></CardLabel>
          <TimeInput />

          <CardLabel>{`${t("WT_DESCRIPTION")}`} <span className="astericColor">*</span></CardLabel>
          <TextInput
            t={t}
            type={"text"}
            isMandatory={false}
            optionKey="i18nKey"
            name="description"
            style={inputStyles}
            value={description}
            onChange={setDescription}
            ValidationRequired={true}
            {...(validation = {
              isRequired: true,
              pattern: "^[a-zA-Z ]+$",
              type: "tel",
              title: t("PT_NAME_ERROR_MESSAGE"),
            })}
          />
          <div style={{ display: "flex", gap: "22px" }}>
            <CardLabel>{`${t("WT_IMMEDIATE")}`}<span className="astericColor">*</span></CardLabel>
            <CheckBox
                label={t("WT_IMMEDIATE")}
                onChange={setextrachargeHandler}
                checked={extraCharge}
            />
            </div>
        </div>
      </FormStep>
    </React.Fragment>
  );
};

export default RequestDetails;