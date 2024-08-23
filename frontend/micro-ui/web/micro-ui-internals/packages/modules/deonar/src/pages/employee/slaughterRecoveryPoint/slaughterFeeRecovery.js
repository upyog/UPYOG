import React, { useState } from "react";
import { useTranslation } from "react-i18next";
import { Controller, useForm } from "react-hook-form";
import MainFormHeader from "../commonFormFields/formMainHeading";
import NumberOfAliveAnimalsField from "../commonFormFields/numberOfAliveAnimals";
import AnimalTokenNumberField from "../commonFormFields/animalTokenNumber";
import HealthStatDropdownField from "../commonFormFields/healthStatDropdown";
import ShopkeeperNameField from "../commonFormFields/shopkeeperName";
import HelkariNameField from "../commonFormFields/helkariName";
import DawanwalaNameField from "../commonFormFields/dawanwalaName";
import SlaughterFeeAmountField from "../commonFormFields/slaughterFeeAmount";
import PaymentModeField from "../commonFormFields/paymentMode";
import ReferenceNumberField from "../commonFormFields/referenceNumber";
import SubmitPrintButtonFields from "../commonFormFields/submitPrintBtn";

const SlaughterFeeRecoveryPage = () => {
  const { t } = useTranslation();
  const [data, setData] = useState({});
  const [subFormType, setSubFormType] = useState(null);

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
          <MainFormHeader title={"DEONAR_SLAUGHTER_FEE_RECOVERY"} />
          <div className="bmc-row-card-header">
            <div className="bmc-card-row">
                <HealthStatDropdownField name="slaughterType" label="DEONAR_SLAUGHTER_TYPE" />
                <HealthStatDropdownField name="slaughterUnit" label="DEONAR_SLAUGHTER_UNIT" />
                <HealthStatDropdownField name="slaughterSession" label="DEONAR_SLAUGHTER_SESSION" />
                <ShopkeeperNameField />
                <DawanwalaNameField />
                <HelkariNameField />
                <NumberOfAliveAnimalsField control={control} setData={setData} data={data} />
                <AnimalTokenNumberField />
                <SlaughterFeeAmountField />
                <PaymentModeField />
                <ReferenceNumberField />
                <SubmitPrintButtonFields />
            </div>
          </div>
        </form>
      </div>
    </React.Fragment>
  );
};

export default SlaughterFeeRecoveryPage;
