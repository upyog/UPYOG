import React, { useState } from "react";
import { useTranslation } from "react-i18next";
import { Controller, useForm } from "react-hook-form";
import TraderNameField from "../../commonFormFields/traderName";
import BrokerNameField from "../../commonFormFields/brokerName";
import GawalNameField from "../../commonFormFields/gawalName";
import AssignDateField from "../../commonFormFields/assignDate";
import ShopkeeperNameField from "../../commonFormFields/shopkeeperName";
import DawanwalaNameField from "../../commonFormFields/dawanwalaName";
import ShadeNumberField from "../../commonFormFields/shadeNumber";
import NumberOfAliveAnimalsField from "../../commonFormFields/numberOfAliveAnimals";
import AnimalTokenNumberField from "../../commonFormFields/animalTokenNumber";
import StablingDaysField from "../../commonFormFields/stablingDays";
import StablingFeeAmountField from "../../commonFormFields/stablingFeeAmt";
import PaymentModeField from "../../commonFormFields/paymentMode";
import PaymentReferenceNumberField from "../../commonFormFields/paymentReferenceNumber";
import SubmitPrintButtonFields from "../../commonFormFields/submitPrintBtn";

const SlaughterInAbbatoirSubform = ({control, data, setData}) => {
  const { t } = useTranslation();

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

  const onSubmit = (formData) => {
    console.log("Form data submitted:", formData);
    const jsonData = JSON.stringify(formData);
    console.log("Generated JSON:", jsonData);
  };

  return (
    <React.Fragment>
      <div className="bmc-card-full">
        <form onSubmit={onSubmit}>
          <div className="bmc-row-card-header">
            <div className="bmc-card-row">
            <TraderNameField control={control} setData={setData} data={data} />
            <BrokerNameField />
            <GawalNameField control={control} setData={setData} data={data} />
                <AssignDateField />
                <ShopkeeperNameField />
                <DawanwalaNameField />
                <ShadeNumberField />
                <NumberOfAliveAnimalsField control={control} setData={setData} data={data} />
                <AnimalTokenNumberField />
                <StablingDaysField />
                <StablingFeeAmountField />
                <PaymentModeField />
                <PaymentReferenceNumberField />
            </div>
            <SubmitPrintButtonFields />
          </div>
        </form>
      </div>
    </React.Fragment>
  );
};

export default SlaughterInAbbatoirSubform;
