import React, { useState } from "react";
import { useTranslation } from "react-i18next";
import { Controller, useForm } from "react-hook-form";
import SearchButtonField from "../commonFormFields/searchBtn";
import MainFormHeader from "../commonFormFields/formMainHeading";
import VeterinaryOfficerField from "../commonFormFields/veterinaryOfficer";
import NumberOfAliveAnimalsField from "../commonFormFields/numberOfAliveAnimals";
import AnimalTokenNumberField from "../commonFormFields/animalTokenNumber";
import PregnancyField from "../commonFormFields/pregnancyField";
import ApproximateAgeField from "../commonFormFields/approxAge";
import HealthStatDropdownField from "../commonFormFields/healthStatDropdown";
import OtherField from "../commonFormFields/other";
import RemarkField from "../commonFormFields/remark";
import SubmitAddButtonFields from "../commonFormFields/submitAddBtn";
import ShopkeeperNameField from "../commonFormFields/shopkeeperName";
import HelkariNameField from "../commonFormFields/helkariName";
import SlaughterReceiptNumberField from "../commonFormFields/slaughterReceiptNumber";
import InspectionDate from "../commonFormFields/inspectionDate";
import InspectionDayField from "../commonFormFields/inspectionDay";

const AnteMortemPreSlaughterInspectionPage = () => {
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
          <MainFormHeader title={"DEONAR_BEFORE_SLAUGHTER_ANTE_MORTEM_INSPECTION"} />
          <div className="bmc-row-card-header">
            <div className="bmc-card-row">
                <ShopkeeperNameField />
                <HelkariNameField />
                <SearchButtonField />
            </div>
          </div>
          <div className="bmc-row-card-header">
            <div className="bmc-card-row">
              <VeterinaryOfficerField />
              <InspectionDate label="DEONAR_ANTE_MORTEM_INSPECTION_DATE" name="anteMortemInspectionDate" />
              <InspectionDayField label="DEONAR_ANTE_MORTEM_INSPECTION_DAY" name="anteMortemInspectionDay" />
              <SlaughterReceiptNumberField />
              <ShopkeeperNameField />
              <HelkariNameField />
              <NumberOfAliveAnimalsField control={control} setData={setData} data={data} />
              <AnimalTokenNumberField />
              <HealthStatDropdownField name="species" label="DEONAR_SPECIES" />
              <HealthStatDropdownField name="breed" label="DEONAR_BREED" />
              <HealthStatDropdownField name="sex" label="DEONAR_SEX" />
              <HealthStatDropdownField name="bodyColor" label="DEONAR_BODY_COLOR" />
              <PregnancyField />
              <ApproximateAgeField />
              <HealthStatDropdownField name="gait" label="DEONAR_GAIT" />
              <HealthStatDropdownField name="posture" label="DEONAR_POSTURE" />
              <HealthStatDropdownField name="bodyTemperature" label="DEONAR_BODY_TEMPERATURE" />
              <HealthStatDropdownField name="pulseRate" label="DEONAR_PULSE_RATE" />
              <HealthStatDropdownField name="appetite" label="DEONAR_APPETITE" />
              <HealthStatDropdownField name="eyes" label="DEONAR_EYES" />
              <HealthStatDropdownField name="nostrils" label="DEONAR_NOSTRILS" />
              <HealthStatDropdownField name="muzzle" label="DEONAR_MUZZLE" />
              <HealthStatDropdownField name="opinion" label="DEONAR_OPINION" required={true} />
              <HealthStatDropdownField name="animalStabling" label="DEONAR_ANIMAL_STABLING" required={true} />
              <OtherField />
              <RemarkField />
              <SubmitAddButtonFields />
            </div>
          </div>
        </form>
      </div>
      <div className="bmc-row-card-header">
        <div className="bmc-card-row">
          <div className="bmc-table-container" style={{ padding: "1rem" }}>
            <form onSubmit={handleSubmit()}>
              <table className="bmc-hover-table">
                <thead>
                  <tr>
                    <th scope="col">Col1</th>
                    <th scope="col">Col2</th>
                    <th scope="col">Col3</th>
                  </tr>
                </thead>
                <tbody>
                  <tr>
                  </tr>
                </tbody>
              </table>
            </form>
          </div>
        </div>
      </div>
    </React.Fragment>
  );
};

export default AnteMortemPreSlaughterInspectionPage;
