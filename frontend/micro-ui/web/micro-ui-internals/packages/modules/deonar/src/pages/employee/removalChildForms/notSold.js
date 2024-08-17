import React, { useState } from "react";
import { useTranslation } from "react-i18next";
import { Controller, useForm } from "react-hook-form";
import TraderNameField from "../commonFormFields/traderName";
import BrokerNameField from "../commonFormFields/brokerName";
import GawalNameField from "../commonFormFields/gawalName";
import NumberOfAnimalsField from "../commonFormFields/numberOfAnimals";
import AnimalTokenNumberField from "../commonFormFields/animalTokenNumber";
import RemovalDateField from "../commonFormFields/removalDate";
import RemovalTimeField from "../commonFormFields/removalTime";
import SubmitButtonField from "../commonFormFields/submitBtn";
import RemovalFeeAmountField from "../commonFormFields/removalFeeAmt";
import PaymentModeField from "../commonFormFields/paymentMode";
import PaymentReferenceNumberField from "../commonFormFields/paymentReferenceNumber";
import SubmitPrintButtonFields from "../commonFormFields/submitPrintBtn";

const NotSold = ({stage, control, data, setData}) => {
  const { t } = useTranslation();

  return (
    <React.Fragment>
      <div className="bmc-card-full">
        <TraderNameField control={control} setData={setData} data={data} />
        <BrokerNameField control={control} setData={setData} data={data} />
        <GawalNameField control={control} setData={setData} data={data} />
          <NumberOfAnimalsField control={control} setData={setData} data={data} />
          <AnimalTokenNumberField control={control} setData={setData} data={data} />
              {
                (stage === "SECURITY_CHECKPOINT") ?
                  <React.Fragment>
                    <RemovalDateField control={control} setData={setData} data={data} />
                    <RemovalTimeField control={control} setData={setData} data={data} />
                    <SubmitButtonField />
                  </React.Fragment>
                :
                  <React.Fragment></React.Fragment>
              }
              {
                (stage === "COLLECTION_POINT") ? 
                  <React.Fragment>
                    <RemovalFeeAmountField control={control} setData={setData} data={data} />
                    <PaymentModeField control={control} setData={setData} data={data} />
                    <PaymentReferenceNumberField control={control} setData={setData} data={data} />
                    <SubmitPrintButtonFields />
                  </React.Fragment>
                :
                  <React.Fragment></React.Fragment>
              }
      </div>
    </React.Fragment>
  );
};

export default NotSold;
