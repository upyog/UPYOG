import React, { useState } from "react";
import { useTranslation } from "react-i18next";
import { CardLabel, Dropdown, LabelFieldPair, TextInput, DatePicker } from "@egovernments/digit-ui-react-components";
import { Controller, useForm } from "react-hook-form";
import TraderNameField from "../../commonFormFields/traderName";
import BrokerNameField from "../../commonFormFields/brokerName";
import GawalNameField from "../../commonFormFields/gawalName";
import AssignDateField from "../../commonFormFields/assignDate";
import CitizenNameField from "../../commonFormFields/citizenName";
import NumberOfAnimalsField from "../../commonFormFields/numberOfAnimals";
import AnimalTokenNumberField from "../../commonFormFields/animalTokenNumber";
import RemovalFeeAmountField from "../../commonFormFields/removalFeeAmt";
import PaymentModeField from "../../commonFormFields/paymentMode";
import PaymentReferenceNumberField from "../../commonFormFields/paymentReferenceNumber";
import SubmitPrintButtonFields from "../../commonFormFields/submitPrintBtn";

const ReligiousPersonalRemovalSubform = ({control, data, setData, setValues}) => {
  const { t } = useTranslation();

  return (
    <React.Fragment>
      <div className="bmc-card-full">
            <div className="bmc-card-row">
            <TraderNameField control={control} setData={setData} data={data} />
            <BrokerNameField control={control} setData={setData} data={data} />
            <GawalNameField control={control} setData={setData} data={data} />
            <AssignDateField control={control} setData={setData} data={data} />
              <CitizenNameField control={control} setData={setData} data={data} />
              <NumberOfAnimalsField control={control} setData={setData} data={data} setValues={setValues} source="removal" />
              <AnimalTokenNumberField control={control} setData={setData} data={data} />
              <RemovalFeeAmountField control={control} setData={setData} data={data} disabled={true} />
              <PaymentModeField control={control} setData={setData} data={data} />
              <PaymentReferenceNumberField control={control} setData={setData} data={data} />
            </div>
            <SubmitPrintButtonFields />
      </div>
    </React.Fragment>
  );
};

export default ReligiousPersonalRemovalSubform;
