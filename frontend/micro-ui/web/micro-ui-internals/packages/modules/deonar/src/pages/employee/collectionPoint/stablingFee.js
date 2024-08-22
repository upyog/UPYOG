import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { Controller, useForm } from "react-hook-form";
import MainFormHeader from "../commonFormFields/formMainHeading";
import StablingTypeOptionsField from "../commonFormFields/stablingTypesDropdown";
import ImportPermissionNumberField from "../commonFormFields/importPermissionNumber";
import ArrivalUuidField from "../commonFormFields/arrivalUuid";
import TraderNameField from "../commonFormFields/traderName";
import GawalNameField from "../commonFormFields/gawalName";
import SearchButtonField from "../commonFormFields/searchBtn";
import BrokerNameField from "../commonFormFields/brokerName";
import ShadeNumberField from "../commonFormFields/shadeNumber";
import NumberOfAnimalsField from "../commonFormFields/numberOfAnimals";
import AnimalTokenNumberField from "../commonFormFields/animalTokenNumber";
import StablingDaysField from "../commonFormFields/stablingDays";
import StablingFeeAmountField from "../commonFormFields/stablingFeeAmt";
import PaymentModeField from "../commonFormFields/paymentMode";
import ReferenceNumberField from "../commonFormFields/referenceNumber";
import SubmitPrintButtonFields from "../commonFormFields/submitPrintBtn";
import DawanwalaNameField from "../commonFormFields/dawanwalaName";
import ShopkeeperNameField from "../commonFormFields/shopkeeperName";
import useSubmitForm from "../../../hooks/useSubmitForm";
import { COLLECTION_POINT_ENDPOINT } from "../../../constants/apiEndpoints";
import { stablingAfterMockdata, stablingBeforeTradingMockdata } from "../../../constants/dummyData";

const StablingFeePage = () => {
  const { t } = useTranslation();
  const [data, setData] = useState({});
  const [stablingFormType, setStablingFormType] = useState({});
  const [defaults, setDefaults] = useState({});

  const {
    control,
    setValue,
    handleSubmit,
    getValues,
    formState: { errors, isValid },
  } = useForm({ defaultValues: defaults, mode: "onChange" });

  const { submitForm, isSubmitting, response, error } = useSubmitForm(COLLECTION_POINT_ENDPOINT);

  useEffect(() => {
    let obj = {};
    if (stablingFormType.name === "BEFORE_TRADING") {
      obj = {
        importPermissionNumber: 0,
        arrivalUuid: 0,
        traderName: {},
        gawalName: {},
        brokerName: {},
        shadeNumber: {},
        numberOfAnimals: 0,
        animalTokenNumber: 0,
        stablingDays: 2,
        stablingFeeAmount: 0,
        paymentMode: {},
        referenceNumber: 0
      };
    }
    else {
      obj = {
        dawanwalaName: {},
        shopkeeperName: {},
        shadeNumber: {},
        numberOfAnimals: 0,
        animalTokenNumber: 0,
        stablingDays: 2,
        stablingFeeAmount: 100,
        paymentMode: {},
        referenceNumber: 0
      };
    }
    setDefaults(obj);
  }, [stablingFormType]);

  const fetchDataByReferenceNumber = async () => {
    if (stablingFormType.name === "BEFORE_TRADING") {
      return stablingBeforeTradingMockdata;
    }
    else {
      return stablingAfterMockdata;
    }
  };

  const handleSearch = async () => {
    console.log("handle search");
    const referenceNumber = getValues("importPermissionNumber");
    if (referenceNumber) {
      try {
        const result = await fetchDataByReferenceNumber();
        setData(result);
        Object.keys(result).forEach((key) => {
          setValue(key, result[key]);
        });
        setValue("importPermissionNumber", referenceNumber);
        setValue("traderName", getValues("traderName"));
        setValue("gawalName", getValues("gawalName"));
        setValue("brokerName", getValues("brokerName"));
        console.log(result);
      } catch (error) {
        console.error("Failed to fetch data", error);
      }
    }
  };

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

  return (
    <React.Fragment>
      <div className="bmc-card-full">
        <form onSubmit={handleSubmit(onSubmit)}>
          <MainFormHeader title={"DEONAR_STABLING_FEE"} />
          <div className="bmc-row-card-header">
            <div className="bmc-card-row">
                <StablingTypeOptionsField setStablingFormType={setStablingFormType} control={control} data={data} setData={setData} />
                {
                    (stablingFormType.name === 'BEFORE_TRADING') ? 
                        <React.Fragment>
                            <ImportPermissionNumberField control={control} setData={setData} data={data} />
                            <ArrivalUuidField control={control} setData={setData} data={data} disabled={false} />
                            <TraderNameField control={control} setData={setData} data={data} />
                            <GawalNameField control={control} setData={setData} data={data} />
                            <SearchButtonField onSearch={handleSearch} />
                        </React.Fragment>
                    :
                        <React.Fragment></React.Fragment>
                }
              </div>
              </div>
              {
                (stablingFormType.name === 'BEFORE_TRADING') ?
                    <div className="bmc-row-card-header">
                        <div className="bmc-card-row">
                          <BrokerNameField control={control} setData={setData} data={data} />
                          <ShadeNumberField control={control} setData={setData} data={data} />
                          <NumberOfAnimalsField control={control} setData={setData} data={data} setValues={setValue} source="stabling" getValues={getValues} />
                          <AnimalTokenNumberField control={control} setData={setData} data={data} />
                          <StablingDaysField control={control} setData={setData} data={data} setValues={setValue} getValues={getValues} />
                          <StablingFeeAmountField control={control} setData={setData} data={data} />
                          <PaymentModeField control={control} setData={setData} data={data} />
                          <ReferenceNumberField control={control} setData={setData} data={data} />
                        </div>
                        <SubmitPrintButtonFields />
                    </div>
                :
                    <React.Fragment></React.Fragment>
              }
              {
                (stablingFormType.name === 'AFTER_TRADING') ?
                <div className="bmc-row-card-header">
                    <div className="bmc-card-row">
                        <DawanwalaNameField control={control} setData={setData} data={data} />
                        <ShopkeeperNameField control={control} setData={setData} data={data} />
                        <ShadeNumberField control={control} setData={setData} data={data} />
                        <NumberOfAnimalsField control={control} setData={setData} data={data} setValues={setValue} source="stabling" getValues={getValues} />
                        <AnimalTokenNumberField control={control} setData={setData} data={data} />
                        <StablingDaysField control={control} setData={setData} data={data} setValues={setValue} getValues={getValues} />
                        <StablingFeeAmountField control={control} setData={setData} data={data} />
                        <PaymentModeField control={control} setData={setData} data={data} />
                        <ReferenceNumberField control={control} setData={setData} data={data} />
                    </div>
                <SubmitPrintButtonFields />
            </div>
                :
                    <React.Fragment></React.Fragment>
              }
        </form>
      </div>
    </React.Fragment>
  );
};

export default StablingFeePage;
