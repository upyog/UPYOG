import React, { useEffect, useState, useCallback } from "react";
import { FormStep, CardLabel, Dropdown, Modal } from "@nudmcdgnpm/digit-ui-react-components";
import { useTranslation } from "react-i18next";


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
  return <h1 className="heading-m">{props.t("SELECT_MODULE")}</h1>;
};

const ServiceTypes = ({ config = {} }) => {
  const { t } = useTranslation();
  const navigate = Digit.Hooks.useCustomNavigate();
  //Fetching service type data from MDMS
  const serviceTypeData = [
    { i18nKey: "Property Tax", businessService: "PT" },
    { i18nKey: "Asset", businessService: "ASSET" }
  ];

  const [serviceType, setServiceType] = useState("");

  const goNext = useCallback(() => {
    if (serviceType) {
      sessionStorage.setItem('selectedServiceType', JSON.stringify(serviceType));
    }
    const nextStep = "mapview";
    const userType = Digit.UserService.getUser()?.info?.type === "EMPLOYEE" ? "employee" : "citizen";
    navigate(`/upyog-ui/${userType}/gis/${nextStep}`);
  }, [config, history, serviceType]);

  useEffect(() => {
    if (serviceType) {
      goNext();
    }
  }, [serviceType, goNext]);

  return (
    <Modal
      headerBarMain={<Heading t={t} />}
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
            <CardLabel>{`${t("SELECT_MODULE")}`} <span className="astericColor">*</span></CardLabel>
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