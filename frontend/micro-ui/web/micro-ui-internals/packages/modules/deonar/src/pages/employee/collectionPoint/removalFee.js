import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { CardLabel, Dropdown, LabelFieldPair, TextInput } from "@egovernments/digit-ui-react-components";
import { Controller, useForm } from "react-hook-form";
import ReligiousPersonal from "../removalChildForms/religious_personal";
import NotSold from "../removalChildForms/notSold";
import RejectedBefore from "../removalChildForms/rejectedBefore";
import DeathBefore from "../removalChildForms/deathBefore";
import DeathAfter from "../removalChildForms/deathAfter";
import RejectedAfter from "../removalChildForms/rejectedAfter";
import RemovalTypeOptionsField from "../commonFormFields/removalTypesDropdown";
import RemovalFeeReceiptNumberField from "../commonFormFields/removalFeeReceiptNumber";
import RejectedRemovalTypesDropdownField from "../commonFormFields/rejectedRemovalTypesDropdown";
import DeathRemovalTypesDropdownField from "../commonFormFields/deathRemovalTypesDropdown";
import SearchButtonField from "../commonFormFields/searchBtn";
import { deathRemovalTypes, rejectedRemovalTypes, removalTypes } from "../../../constants/removalTypes";
import Salsette from "../removalChildForms/salsette";
import MainFormHeader from "../commonFormFields/formMainHeading";

const RemovalFeePage = () => {
  const { t } = useTranslation();
  const [data, setData] = useState({});
  const [subFormType, setSubFormType] = useState(null);
  const [rejectedType, setRejectedType] = useState(null);
  const [deathType, setDeathType] = useState(null);

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
    const referenceNumber = getValues("removalFeeReceiptNumber");
    if (referenceNumber) {
      try {
        const result = await fetchDataByReferenceNumber(referenceNumber);
        setData(result);
        Object.keys(result).forEach((key) => {
          setValue(key, result[key]);
        });
        setSubFormType(getValues("removalType"));
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
          <MainFormHeader title={"DEONAR_REMOVAL_FEE"} />
          <div className="bmc-row-card-header">
            <div className="bmc-card-row">
              <RemovalTypeOptionsField setSubFormType={setSubFormType} options={removalTypes} />
              
              {
                (subFormType === "REJECTED_REMOVAL") ?
                  <RejectedRemovalTypesDropdownField setRejectedType={setRejectedType} options={rejectedRemovalTypes} />
              :
                <React.Fragment></React.Fragment>
              }
              {
                (subFormType === "DEATH_REMOVAL") ?
                  <DeathRemovalTypesDropdownField setDeathType={setDeathType} options={deathRemovalTypes} />
              :
                <React.Fragment></React.Fragment>
              }
              <SearchButtonField />
            </div>
          </div>
          </form>
          { subFormType ? (
            <React.Fragment>
              {
                (subFormType === 'SALSETTE') ? 
                  <div className="bmc-row-card-header">
                    <div className="bmc-card-row">
                      <Salsette stage="COLLECTION_POINT" control={control} setData={setData} data={data} />
                    </div>
                  </div> 
                :
                (subFormType === 'RELIGIOUS_PERSONAL_PURPOSE') ?
                  <div className="bmc-row-card-header">
                    <div className="bmc-card-row">
                      <ReligiousPersonal stage="COLLECTION_POINT" control={control} setData={setData} data={data} /> 
                    </div>
                  </div>
                :
                (subFormType === 'REMOVAL_OF_NOT_SOLD_ANIMALS') ?
                  <div className="bmc-row-card-header">
                    <div className="bmc-card-row">
                      <NotSold stage="COLLECTION_POINT" control={control} setData={setData} data={data} /> 
                    </div>
                  </div>
                :
                (subFormType === 'REJECTED_REMOVAL' && rejectedType === 'REJECTION_BEFORE_TRADING') ?
                  <div className="bmc-row-card-header">
                    <div className="bmc-card-row">
                      <RejectedBefore stage="COLLECTION_POINT" control={control} setData={setData} data={data} /> 
                    </div>
                  </div>
                :
                (subFormType === 'REJECTED_REMOVAL' && rejectedType === 'REJECTION_AFTER_TRADING') ?
                  <div className="bmc-row-card-header">
                    <div className="bmc-card-row">
                      <RejectedAfter stage="COLLECTION_POINT" control={control} setData={setData} data={data} /> 
                    </div>
                  </div>
                :
                (subFormType === 'DEATH_REMOVAL' && deathType === 'DEATH_BEFORE_TRADING') ?
                  <div className="bmc-row-card-header">
                    <div className="bmc-card-row">
                      <DeathBefore stage="COLLECTION_POINT" control={control} setData={setData} data={data} /> 
                    </div>
                  </div>
                :
                (subFormType === 'DEATH_REMOVAL' && deathType === 'DEATH_AFTER_TRADING') ?
                  <div className="bmc-row-card-header">
                    <div className="bmc-card-row">
                      <DeathAfter stage="COLLECTION_POINT" control={control} setData={setData} data={data} /> 
                    </div>
                  </div>
                :
                  <React.Fragment></React.Fragment>
                }
            </React.Fragment>
          ):
            <React.Fragment></React.Fragment>
          }
        
      </div>
    </React.Fragment>
  );
};

export default RemovalFeePage;
