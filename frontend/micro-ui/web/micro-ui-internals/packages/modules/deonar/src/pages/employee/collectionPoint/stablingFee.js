import React, { useState } from "react";
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

const StablingFeePage = () => {
  const { t } = useTranslation();
  const [data, setData] = useState({});
  const [stablingFormType, setStablingFormType] = useState(null);

  const {
    control,
    setValue,
    handleSubmit,
    getValues,
    formState: { errors, isValid },
  } = useForm({ defaultValues: {}, mode: "onChange" });

  const fetchDataByReferenceNumber = async (referenceNumber) => {
    const mockData = {
      arrivalUuid: referenceNumber,
      importType: "Type A",
      importPermissionNumber: "123456",
      importPermissionDate: new Date(),
      traderName: "John Doe",
      licenseNumber: "LIC123",
      vehicleNumber: "ABC123",
      numberOfAliveAnimals: 5,
      numberOfDeadAnimals: 2,
      arrivalDate: new Date(),
      arrivalTime: "12:00",
    };
    return mockData;
  };

  const handleSearch = async () => {
    const referenceNumber = getValues("arrivalUuid");
    if (referenceNumber) {
      try {
        const result = await fetchDataByReferenceNumber(referenceNumber);
        setData(result);
        Object.keys(result).forEach((key) => {
          setValue(key, result[key]);
        });
      } catch (error) {
        console.error("Failed to fetch data", error);
      }
    }
  };

  const onSubmit = (formData) => {
    console.log("Form data submitted:", formData);
    const jsonData = JSON.stringify(formData);
    console.log("Generated JSON:", jsonData);
  };

  return (
    <React.Fragment>
      <div className="bmc-card-full">
        <form onSubmit={handleSubmit(onSubmit)}>
          <MainFormHeader title={"DEONAR_STABLING_FEE"} />
          <div className="bmc-row-card-header">
            <div className="bmc-card-row">
                <StablingTypeOptionsField setStablingFormType={setStablingFormType} />
                {
                    (stablingFormType === 'BEFORE_TRADING') ? 
                        <React.Fragment>
              <ImportPermissionNumberField control={control} setData={setData} data={data} />
              <ArrivalUuidField control={control} setData={setData} data={data} />
              <TraderNameField control={control} setData={setData} data={data} />
              <GawalNameField control={control} setData={setData} data={data} />
                            <SearchButtonField />
                        </React.Fragment>
                    :
                        <React.Fragment></React.Fragment>
                }
              </div>
              </div>
              {
                (stablingFormType === 'BEFORE_TRADING') ?
                    <div className="bmc-row-card-header">
                        <div className="bmc-card-row">
                        <ImportPermissionNumberField control={control} setData={setData} data={data} />
                        <ArrivalUuidField control={control} setData={setData} data={data} />
                        <TraderNameField control={control} setData={setData} data={data} />
                        <GawalNameField control={control} setData={setData} data={data} />
                        <BrokerNameField control={control} setData={setData} data={data} />
                        <ShadeNumberField />
                            <NumberOfAnimalsField />
                            <AnimalTokenNumberField />
                            <StablingDaysField />
                            <StablingFeeAmountField />
                            <PaymentModeField />
                            <ReferenceNumberField />
                        </div>
                        <SubmitPrintButtonFields />
                    </div>
                :
                    <React.Fragment></React.Fragment>
              }
              {
                (stablingFormType === 'AFTER_TRADING') ?
                <div className="bmc-row-card-header">
                    <div className="bmc-card-row">
                        <DawanwalaNameField />
                        <ShopkeeperNameField />
                        <ShadeNumberField />
                        <NumberOfAnimalsField />
                        <AnimalTokenNumberField />
                        <StablingDaysField />
                        <StablingFeeAmountField />
                        <PaymentModeField />
                        <ReferenceNumberField />
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
