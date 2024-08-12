import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { CardLabel, Dropdown, LabelFieldPair, TextInput } from "@egovernments/digit-ui-react-components";
import { Controller, useForm } from "react-hook-form";
import Salsette from "../removalChildForms/salsette";
import ReligiousPersonal from "../removalChildForms/religious_personal";
import NotSold from "../removalChildForms/notSold";
import { deathRemovalTypes, rejectedRemovalTypes, removalTypes } from "../../../constants/removalTypes";
import RejectedBefore from "../removalChildForms/rejectedBefore";
import DeathBefore from "../removalChildForms/deathBefore";
import DeathAfter from "../removalChildForms/deathAfter";
import RejectedAfter from "../removalChildForms/rejectedAfter";
import MainFormHeader from "../commonFormFields/formMainHeading";
import RemovalTypeOptionsField from "../commonFormFields/removalTypesDropdown";
import RemovalFeeReceiptNumberField from "../commonFormFields/removalFeeReceiptNumber";
import RejectedRemovalTypesDropdownField from "../commonFormFields/rejectedRemovalTypesDropdown";
import DeathRemovalTypesDropdownField from "../commonFormFields/deathRemovalTypesDropdown";
import SearchButtonField from "../commonFormFields/searchBtn";

const RemovalPage = () => {
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

  const fetchDataByReferenceNumber = (referenceNumber) => {
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
              <RemovalFeeReceiptNumberField />
              
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
                      <Salsette stage="SECURITY_CHECKPOINT" control={control} setData={setData} data={data} />
                    </div>
                  </div> 
                :
                (subFormType === 'RELIGIOUS_PERSONAL_PURPOSE') ?
                  <div className="bmc-row-card-header">
                    <div className="bmc-card-row">
                      <ReligiousPersonal stage="SECURITY_CHECKPOINT" control={control} setData={setData} data={data} /> 
                    </div>
                  </div>
                :
                (subFormType === 'REMOVAL_OF_NOT_SOLD_ANIMALS') ?
                  <div className="bmc-row-card-header">
                    <div className="bmc-card-row">
                      <NotSold stage="SECURITY_CHECKPOINT" control={control} setData={setData} data={data} /> 
                    </div>
                  </div>
                :
                (subFormType === 'REJECTED_REMOVAL' && rejectedType === 'REJECTION_BEFORE_TRADING') ?
                  <div className="bmc-row-card-header">
                    <div className="bmc-card-row">
                      <RejectedBefore stage="SECURITY_CHECKPOINT" control={control} setData={setData} data={data} /> 
                    </div>
                  </div>
                :
                (subFormType === 'REJECTED_REMOVAL' && rejectedType === 'REJECTION_AFTER_TRADING') ?
                  <div className="bmc-row-card-header">
                    <div className="bmc-card-row">
                      <RejectedAfter stage="SECURITY_CHECKPOINT" control={control} setData={setData} data={data} /> 
                    </div>
                  </div>
                :
                (subFormType === 'DEATH_REMOVAL' && deathType === 'DEATH_BEFORE_TRADING') ?
                  <div className="bmc-row-card-header">
                    <div className="bmc-card-row">
                      <DeathBefore stage="SECURITY_CHECKPOINT" control={control} setData={setData} data={data} /> 
                    </div>
                  </div>
                :
                (subFormType === 'DEATH_REMOVAL' && deathType === 'DEATH_AFTER_TRADING') ?
                  <div className="bmc-row-card-header">
                    <div className="bmc-card-row">
                      <DeathAfter stage="SECURITY_CHECKPOINT" control={control} setData={setData} data={data} /> 
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

export default RemovalPage;
