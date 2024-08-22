import React, { useState } from "react";
import { useTranslation } from "react-i18next";
import { Controller, useForm } from "react-hook-form";
import TraderNameField from "../../commonFormFields/traderName";
import BrokerNameField from "../../commonFormFields/brokerName";
import GawalNameField from "../../commonFormFields/gawalName";
import AssignDateField from "../../commonFormFields/assignDate";
import DairywalaNameField from "../../commonFormFields/dairywalaName";
import NumberOfAnimalsField from "../../commonFormFields/numberOfAnimals";
import AnimalTokenNumberField from "../../commonFormFields/animalTokenNumber";
import SalsetteFeeAmountField from "../../commonFormFields/salsetteFeeAmt";
import RemovalFeeAmountField from "../../commonFormFields/removalFeeAmt";
import PaymentModeField from "../../commonFormFields/paymentMode";
import PaymentReferenceNumberField from "../../commonFormFields/paymentReferenceNumber";
import SubmitPrintButtonFields from "../../commonFormFields/submitPrintBtn";

const SalsetteRemovalSubform = ({isShopkeeperAssignment, control, data, setData, setValues}) => {
  const { t } = useTranslation();

  return (
    <React.Fragment>
      <div className="bmc-card-full">
            <div className="bmc-card-row">
            <TraderNameField control={control} setData={setData} data={data} />
            <BrokerNameField control={control} setData={setData} data={data} />
            <GawalNameField control={control} setData={setData} data={data} />
                {
                    isShopkeeperAssignment ? 
                        <AssignDateField control={control} setData={setData} data={data} />
                    :
                        <React.Fragment></React.Fragment>
                }
                <DairywalaNameField control={control} setData={setData} data={data} />
                <NumberOfAnimalsField control={control} setData={setData} data={data} setValues={setValues} source="removal" />
                <AnimalTokenNumberField control={control} setData={setData} data={data} />
            {
                isShopkeeperAssignment ? 
                    <SalsetteFeeAmountField control={control} setData={setData} data={data} />
                :
                    <RemovalFeeAmountField control={control} setData={setData} data={data} disabled={true} />
            }
            <PaymentModeField control={control} setData={setData} data={data} />
            <PaymentReferenceNumberField control={control} setData={setData} data={data} />
            </div>
            <SubmitPrintButtonFields />
      </div>
    </React.Fragment>
  );
};

export default SalsetteRemovalSubform;
