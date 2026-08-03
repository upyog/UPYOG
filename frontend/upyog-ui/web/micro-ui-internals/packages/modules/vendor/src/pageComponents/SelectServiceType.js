import React, { useState, useEffect } from "react";
import { Dropdown, Loader } from "@nudmcdgnpm/digit-ui-react-components";

const SelectServiceType = ({ config, onSelect, t, userType, formData }) => {
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const stateId = Digit.ULBService.getStateId();
  const [serviceTypes, setserviceTypes] = useState(formData?.serviceType);
  const [formattedServiceTypes, setFormattedServiceTypes] = useState([]);

  const { data: ServiceType, isLoading } = Digit.Hooks.useCustomMDMS(tenantId, "tenant", [{ name: "citymodule" }], {
    select: (data) => data?.tenant?.citymodule,
  });

  // Transform ServiceType data when it changes
  useEffect(() => {
    if (ServiceType) {
      const transformedData = ServiceType.map((services) => ({
        i18nKey: `${services.code}`,
        code: `${services.code}`,
        value: `${services.name}`,
      }));
      setFormattedServiceTypes(transformedData);
    }
  }, [ServiceType]);

  const selectServiceType = (value) => {
    setserviceTypes(value);
    if (userType === "employee") {
      onSelect(config.key, value);
    }
  };

  const onSkip = () => {
    onSelect();
  };

  const onSubmit = () => {
    onSelect(config.key, serviceTypes);
  };

  if (isLoading) {
    return <Loader />;
  }

  if (userType === "employee") {
    return (
      <div>
        <Dropdown
          className="payment-form-text-input-correction"
          isMandatory={config.isMandatory}
          selected={serviceTypes}
          option={formattedServiceTypes}
          select={selectServiceType}
          optionKey="i18nKey"
          disable={config.disable}
          t={t}
        />
      </div>
    );
  }
};

export default SelectServiceType;
