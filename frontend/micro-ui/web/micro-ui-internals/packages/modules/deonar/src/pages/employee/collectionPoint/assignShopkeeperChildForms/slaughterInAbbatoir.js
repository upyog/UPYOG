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

  return (
    <React.Fragment>
      <div className="bmc-card-full">
            <div className="bmc-card-row">
            <TraderNameField control={control} setData={setData} data={data} />
            <BrokerNameField control={control} setData={setData} data={data} />
            <GawalNameField control={control} setData={setData} data={data} />
                <AssignDateField control={control} setData={setData} data={data} />
                <ShopkeeperNameField control={control} setData={setData} data={data} />
                <DawanwalaNameField control={control} setData={setData} data={data} />
                <ShadeNumberField control={control} setData={setData} data={data} />
                <NumberOfAliveAnimalsField control={control} setData={setData} data={data} />
                <AnimalTokenNumberField control={control} setData={setData} data={data} />
                <StablingDaysField control={control} setData={setData} data={data} />
                <StablingFeeAmountField control={control} setData={setData} data={data} />
                <PaymentModeField control={control} setData={setData} data={data} />
                <PaymentReferenceNumberField control={control} setData={setData} data={data} />
            </div>
            <SubmitPrintButtonFields />
      </div>
    </React.Fragment>
  );
};

export default SlaughterInAbbatoirSubform;
