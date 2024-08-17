import React, { useState } from "react";
import { useTranslation } from "react-i18next";
import { Controller, useForm } from "react-hook-form";
import TraderNameField from "../commonFormFields/traderName";
import BrokerNameField from "../commonFormFields/brokerName";
import GawalNameField from "../commonFormFields/gawalName";
import DairywalaNameField from "../commonFormFields/dairywalaName";
import NumberOfAnimalsField from "../commonFormFields/numberOfAnimals";
import AnimalTokenNumberField from "../commonFormFields/animalTokenNumber";
import RemovalDateField from "../commonFormFields/removalDate";
import RemovalTimeField from "../commonFormFields/removalTime";
import SubmitButtonField from "../commonFormFields/submitBtn";
import RemovalFeeAmountField from "../commonFormFields/removalFeeAmt";
import PaymentModeField from "../commonFormFields/paymentMode";
import PaymentReferenceNumberField from "../commonFormFields/paymentReferenceNumber";
import SubmitPrintButtonFields from "../commonFormFields/submitPrintBtn";

const Salsette = ({stage, control, data, setData, disabled}) => {
  const { t } = useTranslation();

  return (
    <React.Fragment>
      <div className="bmc-card-full">
        <TraderNameField control={control} setData={setData} data={data} disabled={disabled} />
        <BrokerNameField control={control} setData={setData} data={data} disabled={disabled} />
        <GawalNameField control={control} setData={setData} data={data} disabled={disabled} />
        <DairywalaNameField control={control} setData={setData} data={data} disabled={disabled} />
        <NumberOfAnimalsField control={control} setData={setData} data={data} disabled={disabled} />
        <AnimalTokenNumberField control={control} setData={setData} data={data} disabled={disabled} />
        {
          (stage === "SECURITY_CHECKPOINT") ? 
          <React.Fragment>
            <RemovalDateField control={control} setData={setData} data={data} disabled={disabled} />
            <RemovalTimeField control={control} setData={setData} data={data} disabled={disabled} />
            <SubmitButtonField control={control} />
          </React.Fragment>
          :
            <React.Fragment></React.Fragment>
        }
              {
                (stage === "COLLECTION_POINT") ?
                  <React.Fragment>
                    <RemovalFeeAmountField control={control} setData={setData} data={data} disabled={disabled} />
                    <PaymentModeField control={control} setData={setData} data={data} disabled={disabled} />
                    <PaymentReferenceNumberField control={control} setData={setData} data={data} disabled={disabled} />
                    <SubmitPrintButtonFields control={control} setData={setData} data={data} disabled={disabled} />
                  </React.Fragment>
                :
                  <React.Fragment></React.Fragment>
              }
      </div>
    </React.Fragment>
  );
};

export default Salsette;
