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
        <TraderNameField control={control} setData={setData} data={data} />
        <BrokerNameField control={control} setData={setData} data={data} />
        <GawalNameField control={control} setData={setData} data={data} />
        <CitizenNameField control={control} setData={setData} data={data} />
          <NumberOfAnimalsField control={control} setData={setData} data={data} disabled={false} />
          <AnimalTokenNumberField control={control} setData={setData} data={data} />
              {
                (stage === "COLLECTION_POINT") ? 
                  <React.Fragment>
                    <RemovalFeeAmountField control={control} setData={setData} data={data} />
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
                    <RemovalDateField control={control} data={data} setData={setData} />
                    <RemovalTimeField control={control} data={data} setData={setData} />
                    <SubmitButtonField />
                  </React.Fragment>
                :
                  <React.Fragment></React.Fragment>
              }
              
      </div>
    </React.Fragment>
  );
};

export default ReligiousPersonal;
