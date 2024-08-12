import React, { useState } from "react";
import { useTranslation } from "react-i18next";
import { Controller, useForm } from "react-hook-form";
import TraderNameField from "../commonFormFields/traderName";
import BrokerNameField from "../commonFormFields/brokerName";
import GawalNameField from "../commonFormFields/gawalName";
import CitizenNameField from "../commonFormFields/citizenName";
import NumberOfAnimalsField from "../commonFormFields/numberOfAnimals";
import AnimalTokenNumberField from "../commonFormFields/animalTokenNumber";
import RemovalFeeAmountField from "../commonFormFields/removalFeeAmt";
import PaymentModeField from "../commonFormFields/paymentMode";
import PaymentReferenceNumberField from "../commonFormFields/paymentReferenceNumber";
import SubmitPrintButtonFields from "../commonFormFields/submitPrintBtn";
import RemovalDateField from "../commonFormFields/removalDate";
import RemovalTimeField from "../commonFormFields/removalTime";
import SubmitButtonField from "../commonFormFields/submitBtn";

const ReligiousPersonal = ({stage, control, data, setData}) => {
  const { t } = useTranslation();

  const onSubmit = (formData) => {
    console.log("Form data submitted:", formData);
    const jsonData = JSON.stringify(formData);
    console.log("Generated JSON:", jsonData);
  };

  return (
    <React.Fragment>
      <div className="bmc-card-full">
        <form onSubmit={onSubmit}>
        <TraderNameField control={control} setData={setData} data={data} />
        <BrokerNameField />
        <GawalNameField control={control} setData={setData} data={data} />
        <CitizenNameField />
          <NumberOfAnimalsField />
          <AnimalTokenNumberField />
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
              
        </form>
      </div>
    </React.Fragment>
  );
};

export default ReligiousPersonal;
