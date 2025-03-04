import React, { useEffect, useState, useCallback } from "react";
import { FormStep, CardLabel, Dropdown } from "@nudmcdgnpm/digit-ui-react-components";

/* This file is made for choosing the particular request type.  
    It provides a dropdown menu that allows users to select a service type, such as  
    "Water Tanker (WT)" or "Mobile Toilet". */

const ServiceTypes = ({ t, config, onSelect, userType, formData }) => {
  const user = Digit.UserService.getUser().info;
  const [serviceType, setServiceType] = useState(formData?.serviceType?.serviceType || "");

  // Function to proceed to the next step, updating selected service type in form data.
  const goNext = useCallback(() => {
    let serviceTypes = formData.serviceType;
    let ServiceType = { ...serviceTypes, serviceType };
    onSelect(config.key, ServiceType, false);
  }, [formData.serviceType, serviceType, onSelect, config.key]);

  const serviceTypeData = [
    {
      code: "WT",
      i18nKey: "WT",
      value: "WT"
    },
    {
      code: "MobileToilet",
      i18nKey: "MobileToilet",
      value: "MobileToilet"
    }
  ];

  useEffect(() => {
    if (userType === "citizen") {
      goNext();
    }
  }, [serviceType, userType, goNext]);

  return (
    <React.Fragment>
      <FormStep
        config={config}
        onSelect={goNext}
        t={t}
        isDisabled={!serviceType}
      >
        <div>
          <CardLabel>{`${t("WT_REQUEST_TYPE")}`} <span className="astericColor">*</span></CardLabel>
          <Dropdown
            className="form-field"
            selected={serviceType}
            placeholder={"Select Service Type"}
            select={setServiceType}
            option={serviceTypeData}
            optionKey="i18nKey"
            t={t}
          />
        </div>
      </FormStep>
    </React.Fragment>
  );
};

export default ServiceTypes;