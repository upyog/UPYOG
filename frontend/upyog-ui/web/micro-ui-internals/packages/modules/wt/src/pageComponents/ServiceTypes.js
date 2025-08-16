import React, { useEffect, useState, useCallback } from "react";
import { FormStep, CardLabel, Dropdown , Modal} from "@upyog/digit-ui-react-components";

/* This file is made for choosing the particular request type.  
    It provides a dropdown menu that allows users to select a service type, such as  
    "Water Tanker (WT)" or "Mobile Toilet". */

    const Close = () => (
      <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="#FFFFFF">
        <path d="M0 0h24v24H0V0z" fill="none" />
        <path d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12 19 6.41z" />
      </svg>
    );
    
    const CloseBtn = ({ onClick }) => (
      <div className="icon-bg-secondary" onClick={onClick}>
        <Close />
      </div>
    );
  const Heading = (props) => {
    return <h1 className="heading-m">{props.t("SERVICE_TYPE")}</h1>;
};

const ServiceTypes = ({ t, config, onSelect, userType, formData }) => {
const tenantId=Digit.ULBService.getStateId();
//Fetching service type data from MDMS
    const { data: serviceTypeData} = Digit.Hooks.useCustomMDMS(tenantId, "request-service", [{ name: "ServiceType" }], {
    select: (data) => {
      const formattedData = data?.["request-service"]?.["ServiceType"];
      return formattedData;
    },
  });
  
  const user = Digit.UserService.getUser().info;
  const [serviceType, setServiceType] = useState(formData?.serviceType?.serviceType || "");


  // Function to proceed to the next step, updating selected service type in form data.
  const goNext = useCallback(() => {
    let serviceTypes = formData.serviceType;
    let ServiceType = { ...serviceTypes, serviceType };
   
    onSelect(config.key, ServiceType, false);
  }, [formData.serviceType, serviceType, onSelect, config.key]);

 
  useEffect(() => {
    if (userType === "citizen") {
      console.log('calling meeeeeee');
      goNext();
    }
  }, [serviceType, userType, goNext]);

  return (
    <Modal
      headerBarMain={<Heading t={t}/>}
      headerBarEnd={<CloseBtn onClick={() => window.history.back()} />}
      popupStyles={{ backgroundColor: "#fff", position: 'relative', maxHeight: '50vh', width: '50%', overflowY: 'visible' }}
      popupModuleMianStyles={{ padding: "10px" }}
      hideSubmit={true}
      headerBarMainStyle={{ position: "sticky", top: 0, backgroundColor: "#f5f5f5" }}
      formId="modal-action"
    >
    <React.Fragment>
      <FormStep
        config={config}
        onSelect={goNext}
        t={t}
        isDisabled={!serviceType}
      >
        <div>
          <CardLabel>{`${t("REQUEST_TYPE")}`} <span className="astericColor">*</span></CardLabel>
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
    </Modal>
  );
};

export default ServiceTypes;