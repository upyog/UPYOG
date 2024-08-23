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
import InspectionDayField from "../commonFormFields/inspectionDay";
import InspectionDate from "../commonFormFields/inspectionDate";

const PostMortemInspectionPage = () => {
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
          <MainFormHeader title={"BEFORE_SLAUGHTER_ANTE_MORTEM_INSPECTION"} />
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
              <InspectionDate label="DEONAR_POST_MORTEM_INSPECTION_DATE" name="postMortemInspectionDate" />
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
              <HealthStatDropdownField name="visibleMucusMembrane" label="DEONAR_VISIBLE_MUCUS_MEMBRANE" />
              <HealthStatDropdownField name="thoracicCavity" label="DEONAR_THORACIC_CAVITY" />
              <HealthStatDropdownField name="abdominalCavity" label="DEONAR_ABDOMINAL_CAVITY" />
              <HealthStatDropdownField name="pelvicCavity" label="DEONAR_PELVIC_CAVITY" />
              <HealthStatDropdownField name="specimenCollection" label="DEONAR_SPECIMEN_COLLECTION" />
              <HealthStatDropdownField name="specialObservation" label="DEONAR_SPECIAL_OBSERVATION" />
              <HealthStatDropdownField name="opinion" label="DEONAR_OPINION" required={true} />
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

export default PostMortemInspectionPage;
