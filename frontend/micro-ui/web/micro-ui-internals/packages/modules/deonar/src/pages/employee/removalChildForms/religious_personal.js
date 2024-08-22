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

const ReligiousPersonal = ({stage, control, data, setData, setValues}) => {
  const { t } = useTranslation();

  return (
    <React.Fragment>
      <div className="bmc-card-full">
        <TraderNameField control={control} setData={setData} data={data} />
        <BrokerNameField control={control} setData={setData} data={data} />
        <GawalNameField control={control} setData={setData} data={data} />
        <CitizenNameField control={control} setData={setData} data={data} />
          <NumberOfAnimalsField control={control} setData={setData} data={data} disabled={false} setValues={setValues} source="removal" />
          <AnimalTokenNumberField control={control} setData={setData} data={data} />
              {
                (stage === "COLLECTION_POINT") ? 
                  <React.Fragment>
                    <RemovalFeeAmountField control={control} setData={setData} data={data} disabled={true} />
                    <PaymentModeField control={control} data={data} setData={setData} />
                    <PaymentReferenceNumberField control={control} data={data} setData={setData} />
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
