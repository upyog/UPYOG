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

const ReligiousPersonalRemovalSubform = ({control, data, setData}) => {
  const { t } = useTranslation();
  const [disabledFlag, setDisabledFlag] = useState(false);

  // const {
  //   setValue,
  //   handleSubmit,
  //   getValues,
  //   formState: { errors, isValid },
  // } = useForm({ defaultValues: {
  //     traderName: {},
  //     brokerName: {},
  //     gawalName: {},
  //     assignDate: '',
  //     citizenName: '',
  //     numberOfAnimals: 0,
  //     animalTokenNumber: '',
  //     removalFeeAmount: '',
  //     paymentMode: '',
  //     paymentReferenceNumber: ''
  // }, mode: "onChange" });

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
            <GawalNameField control={control} setData={setData} data={data} disabled={disabledFlag} />
            <AssignDateField />
              <CitizenNameField />
              <NumberOfAnimalsField />
              <AnimalTokenNumberField />
              <RemovalFeeAmountField />
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

export default ReligiousPersonalRemovalSubform;
