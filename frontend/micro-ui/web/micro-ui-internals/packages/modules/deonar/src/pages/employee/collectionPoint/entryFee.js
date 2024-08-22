import React, { useEffect, useRef, useState } from "react";
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
import { entryFeeCollectionMockData } from "../../../constants/dummyData";
import SearchButtonField from "../commonFormFields/searchBtn";
import { COLLECTION_POINT_ENDPOINT } from "../../../constants/apiEndpoints";
import useSubmitForm from "../../../hooks/useSubmitForm";

const EntryFeeCollection = () => {
  const { t } = useTranslation();
  const [data, setData] = useState({});
  const [disabledFlag, setDisabledFlag] = useState(false);
  const [showSubform, setShowSubform] = useState(false);

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

  const { submitForm, isSubmitting, response, error } = useSubmitForm(COLLECTION_POINT_ENDPOINT);

  const onSubmit = async (formData) => {
    try {
      const result = await submitForm(formData);
      console.log("Form successfully submitted:", result);
      alert("Form submission successful!");
    } catch (error) {
      console.error("Error submitting form:", error);
      alert("Form submission failed");
    }
  };

  const fetchDataByReferenceNumber = async (referenceNumber) => {
    //setTimeout(() => {
      return entryFeeCollectionMockData;
    //}, 1000);
  };

  //once backend is enabled, assign uuid value coming from there as per import permission number
  const [uuidVal, setUuidVal] = useState(0);

  const handleSearch = async () => {
    console.log("handleSearch");
    setShowSubform(true);
    const referenceNumber = getValues("importPermissionNumber");
    if (referenceNumber) {
      try {
        const result = await fetchDataByReferenceNumber(referenceNumber);
        setData(result);
        Object.keys(result).forEach((key) => {
          if (key === "arrivalUuid") {
            setUuidVal(result[key]);
          }
          setValue(key, result[key]);
        });
        setValue('importPermissionNumber', referenceNumber);
        setDisabledFlag(true);
      } catch (error) {
        console.error("Failed to fetch data", error);
        setDisabledFlag(true);
      }
    }
  };

  return (
    <React.Fragment>
      <div className="bmc-card-full">
        <form onSubmit={handleSubmit(onSubmit)}>
          <MainFormHeader title={"DEONAR_ENTRY_FEE_COLLECTION"} />
          <div className="bmc-row-card-header">
            <div className="bmc-card-row">
              <ImportPermissionNumberField control={control} setData={setData} data={data} />
              <SearchButtonField onSearch={handleSearch} />
            </div>
          </div>
          {
            showSubform && 
            <div className="bmc-row-card-header">
              <div className="bmc-card-row">
              <ArrivalUuidField control={control} setData={setData} data={data} uuid={uuidVal} />
              <TraderNameField control={control} setData={setData} data={data} />
              <LicenseNumberField control={control} setData={setData} data={data} />
              <VehicleNumberField control={control} setData={setData} data={data} />
              <NumberOfAliveAnimalsField control={control} setData={setData} data={data} />
              <ArrivalDateField control={control} setData={setData} data={data} />
                <ArrivalTimeField control={control} setData={setData} data={data} />
                <GawalNameField control={control} setData={setData} data={data} disabled={disabledFlag} />
                <BrokerNameField control={control} setData={setData} data={data} />
                <EntryFeeField control={control} setData={setData} data={data} />
                <ReceiptModeField control={control} setData={setData} data={data} />
                <PaymentReferenceNumberField control={control} setData={setData} data={data} />
              </div>
              <SubmitPrintButtonFields />
            </div>
          }
        </form>
      </div>
    </React.Fragment>
  );
};

export default EntryFeeCollection;
