import React, { useState } from "react";
import { useTranslation } from "react-i18next";
import { CardLabel, Dropdown, LabelFieldPair, TextInput, DatePicker } from "@egovernments/digit-ui-react-components";
import { Controller, useForm } from "react-hook-form";
import ShopkeeperNameField from "../commonFormFields/shopkeeperName";
import DawanwalaNameField from "../commonFormFields/dawanwalaName";
import NumberOfAnimalsField from "../commonFormFields/numberOfAnimals";
import AnimalTokenNumberField from "../commonFormFields/animalTokenNumber";
import RemovalDateField from "../commonFormFields/removalDate";
import RemovalTimeField from "../commonFormFields/removalTime";
import SubmitButtonField from "../commonFormFields/submitBtn";
import RemovalFeeAmountField from "../commonFormFields/removalFeeAmt";
import PaymentModeField from "../commonFormFields/paymentMode";
import PaymentReferenceNumberField from "../commonFormFields/paymentReferenceNumber";
import SubmitPrintButtonFields from "../commonFormFields/submitPrintBtn";

const RejectedAfter = ({stage}) => {
  const { t } = useTranslation();
  const [data, setData] = useState(null);

  const {
    control,
    setValue,
    handleSubmit,
    getValues,
    formState: { errors, isValid },
  } = useForm({ defaultValues: {}, mode: "onChange" });

  const fetchDataByReferenceNumber = async (referenceNumber) => {
    const mockData = {
      arrivalUuid: referenceNumber,
      importType: "Type A",
      importPermissionNumber: "123456",
      importPermissionDate: new Date(),
      traderName: "John Doe",
      licenseNumber: "LIC123",
      vehicleNumber: "ABC123",
      numberOfAliveAnimals: 5,
      numberOfDeadAnimals: 2,
      arrivalDate: new Date(),
      arrivalTime: "12:00",
    };
    return mockData;
  };

  const handleSearch = async () => {
    const referenceNumber = getValues("arrivalUuid");
    if (referenceNumber) {
      try {
        const result = await fetchDataByReferenceNumber(referenceNumber);
        setData(result);
        Object.keys(result).forEach((key) => {
          setValue(key, result[key]);
        });
      } catch (error) {
        console.error("Failed to fetch data", error);
      }
    }
  };

  const onSubmit = (formData) => {
    console.log("Form data submitted:", formData);
    const jsonData = JSON.stringify(formData);
    console.log("Generated JSON:", jsonData);
  };

  return (
    <React.Fragment>
      <div className="bmc-card-full">
        <form onSubmit={handleSubmit(onSubmit)}>
          <ShopkeeperNameField />
          <DawanwalaNameField />
              {
                (stage === "COLLECTION_POINT") ?
                  <NumberOfAnimalsField />
                :
                  <React.Fragment></React.Fragment>
              }
              <AnimalTokenNumberField />
              {
                (stage === "SECURITY_CHECKPOINT") ?
                  <React.Fragment>
                    <RemovalDateField />
                    <RemovalTimeField />
                    <SubmitButtonField />
                  </React.Fragment>
                :
                  <React.Fragment></React.Fragment>
              }
            
              {
                (stage === "COLLECTION_POINT") ?
                  <React.Fragment>
                    <RemovalFeeAmountField />
                    <PaymentModeField />
                    <PaymentReferenceNumberField />
                    <SubmitPrintButtonFields />
                  </React.Fragment>
                :
                  <React.Fragment></React.Fragment>
              }
            
        </form>
      </div>
    </React.Fragment>
  );
};

export default RejectedAfter;
