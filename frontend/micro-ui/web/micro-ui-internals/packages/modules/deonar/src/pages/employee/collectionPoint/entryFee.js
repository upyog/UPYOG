import React, { useState } from "react";
import { useTranslation } from "react-i18next";
import { Controller, useForm } from "react-hook-form";
import MainFormHeader from "../commonFormFields/formMainHeading";
import ImportPermissionNumberField from "../commonFormFields/importPermissionNumber";
import ArrivalUuidField from "../commonFormFields/arrivalUuid";
import TraderNameField from "../commonFormFields/traderName";
import LicenseNumberField from "../commonFormFields/licenseNumber";
import VehicleNumberField from "../commonFormFields/vehicleNumber";
import NumberOfAliveAnimalsField from "../commonFormFields/numberOfAliveAnimals";
import ArrivalDateField from "../commonFormFields/arrivalDate";
import ArrivalTimeField from "../commonFormFields/arrivalTime";
import GawalNameField from "../commonFormFields/gawalName";
import BrokerNameField from "../commonFormFields/brokerName";
import EntryFeeField from "../commonFormFields/entryFeeField";
import ReceiptModeField from "../commonFormFields/receiptMode";
import PaymentReferenceNumberField from "../commonFormFields/paymentReferenceNumber";
import SubmitPrintButtonFields from "../commonFormFields/submitPrintBtn";

const EntryFeeCollection = () => {
  const { t } = useTranslation();
  const [data, setData] = useState({});
  const [disabledFlag, setDisabledFlag] = useState(false);

  const {
    control,
    setValue,
    handleSubmit,
    getValues,
    formState: { errors, isValid },
  } = useForm({ defaultValues: {
    importPermissionNumber: '',
    arrivalUuid: '',
    traderName: {},
    licenseNumber: '',
    vehicleNumber: '',
    numberOfAliveAnimals: 0,
    arrivalDate: '',
    arrivalTime: '',
    gawalName: {},
    brokerName: {},
    entryFee: '',
    receiptMode: '',
    paymentReferenceNumber: ''
  }, mode: "onChange" });


  const onSubmit = (formData) => {
    console.log("Form data submitted:", formData);
    const jsonData = JSON.stringify(formData);
    console.log("Generated JSON:", jsonData);
  };

  return (
    <React.Fragment>
      <div className="bmc-card-full">
        <form onSubmit={onSubmit}>
          <MainFormHeader title={"DEONAR_ENTRY_FEE_COLLECTION"} />
          <div className="bmc-row-card-header">
            <div className="bmc-card-row">
            <ImportPermissionNumberField control={control} setData={setData} data={data} />
            <ArrivalUuidField control={control} setData={setData} data={data} />
            <TraderNameField control={control} setData={setData} data={data} />
            <LicenseNumberField control={control} setData={setData} data={data} />
            <VehicleNumberField control={control} setData={setData} data={data} />
            <NumberOfAliveAnimalsField control={control} setData={setData} data={data} />
            <ArrivalDateField control={control} setData={setData} data={data} />
              <ArrivalTimeField control={control} setData={setData} data={data} />
              <GawalNameField control={control} setData={setData} data={data} disabled={disabledFlag} />
              <BrokerNameField />
              <EntryFeeField />
              <ReceiptModeField />
              <PaymentReferenceNumberField />
            </div>
            <SubmitPrintButtonFields />
          </div>
        </form>
      </div>
    </React.Fragment>
  );
};

export default EntryFeeCollection;
