import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { Controller, useForm } from "react-hook-form";
import ReligiousPersonal from "../removalChildForms/religious_personal";
import NotSold from "../removalChildForms/notSold";
import RejectedBefore from "../removalChildForms/rejectedBefore";
import DeathBefore from "../removalChildForms/deathBefore";
import DeathAfter from "../removalChildForms/deathAfter";
import RejectedAfter from "../removalChildForms/rejectedAfter";
import RemovalTypeOptionsField from "../commonFormFields/removalTypesDropdown";
import RejectedRemovalTypesDropdownField from "../commonFormFields/rejectedRemovalTypesDropdown";
import DeathRemovalTypesDropdownField from "../commonFormFields/deathRemovalTypesDropdown";
import { deathRemovalTypes, rejectedRemovalTypes, removalTypes } from "../../../constants/removalTypes";
import Salsette from "../removalChildForms/salsette";
import MainFormHeader from "../commonFormFields/formMainHeading";
import useSubmitForm from "../../../hooks/useSubmitForm";
import { COLLECTION_POINT_ENDPOINT } from "../../../constants/apiEndpoints";

const RemovalFeePage = () => {
  const { t } = useTranslation();
  const [data, setData] = useState({});
  const [subFormType, setSubFormType] = useState({});
  const [rejectedType, setRejectedType] = useState({});
  const [deathType, setDeathType] = useState({});
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
    if (
        (subFormType.name === "REJECTED_REMOVAL" && rejectedType.name === "REJECTION_BEFORE_TRADING") || 
        (subFormType.name === "DEATH_REMOVAL" && rejectedType.name === "DEATH_BEFORE_TRADING")
      ) {
      obj = {
        traderName: {},
        brokerName: {},
        gawalName: {},
        numberOfAnimals: 0,
        animalTokenNumber: 0,
        removalFeeAmount: 0,
        paymentMode: {},
        paymentReferenceNumber: ""
      };
    }
    else if (
      (subFormType.name === "REJECTED_REMOVAL" && rejectedType.name === "REJECTION_AFTER_TRADING") ||
      (subFormType.name === "DEATH_REMOVAL" && rejectedType.name === "DEATH_AFTER_TRADING")
    ) {
      obj = {
        shopkeeperName: {},
        dawanwalaName: {},
        numberOfAnimals: 0,
        animalTokenNumber: 0,
        removalFeeAmount: 0,
        paymentMode: {},
        paymentReferenceNumber: ""
      };
    }
    else if (subFormType.name === "SALSETTE") {
      obj = {
        traderName: {},
        brokerName: {},
        gawalName: {},
        dairywalaName: {},
        numberOfAnimals: 0,
        animalTokenNumber: 0,
        removalFeeAmount: 0,
        paymentMode: {},
        paymentReferenceNumber: ""
      };
    }
    else if (subFormType.name === "RELIGIOUS_PERSONAL_PURPOSE") {
      obj = {
        traderName: {},
        brokerName: {},
        gawalName: {},
        citizenName: '',
        numberOfAnimals: 0,
        animalTokenNumber: 0,
        removalFeeAmount: 0,
        paymentMode: {},
        paymentReferenceNumber: ""
      };
    }
    else if (subFormType.name === "REMOVAL_OF_NOT_SOLD_ANIMALS") {
      obj = {
        traderName: {},
        brokerName: {},
        gawalName: {},
        numberOfAnimals: 0,
        animalTokenNumber: 0,
        removalFeeAmount: 0,
        paymentMode: {},
        paymentReferenceNumber: ""
      };
    }
    setDefaults(obj);
  }, [subFormType, rejectedType, deathType]);

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
          <MainFormHeader title={"DEONAR_REMOVAL_FEE"} />
          <div className="bmc-row-card-header">
            <div className="bmc-card-row">
              <RemovalTypeOptionsField setSubFormType={setSubFormType} options={removalTypes} control={control} data={data} setData={setData} />
              
              {
                (subFormType.name === "REJECTED_REMOVAL") ?
                  <RejectedRemovalTypesDropdownField setRejectedType={setRejectedType} options={rejectedRemovalTypes} control={control} data={data} setData={setData} />
              :
                <React.Fragment></React.Fragment>
              }
              {
                (subFormType.name === "DEATH_REMOVAL") ?
                  <DeathRemovalTypesDropdownField setDeathType={setDeathType} options={deathRemovalTypes} control={control} data={data} setData={setData} />
              :
                <React.Fragment></React.Fragment>
              }
            </div>
          </div>
          { subFormType ? (
            <React.Fragment>
              {
                (subFormType.name === 'SALSETTE') ? 
                  <div className="bmc-row-card-header">
                    <div className="bmc-card-row">
                      <Salsette stage="COLLECTION_POINT" control={control} setData={setData} data={data} setValues={setValue} disabled={false} />
                    </div>
                  </div> 
                :
                (subFormType.name === 'RELIGIOUS_PERSONAL_PURPOSE') ?
                  <div className="bmc-row-card-header">
                    <div className="bmc-card-row">
                      <ReligiousPersonal stage="COLLECTION_POINT" control={control} setData={setData} data={data} setValues={setValue} /> 
                    </div>
                  </div>
                :
                (subFormType.name === 'REMOVAL_OF_NOT_SOLD_ANIMALS') ?
                  <div className="bmc-row-card-header">
                    <div className="bmc-card-row">
                      <NotSold stage="COLLECTION_POINT" control={control} setData={setData} data={data} setValues={setValue} /> 
                    </div>
                  </div>
                :
                (subFormType.name === 'REJECTED_REMOVAL' && rejectedType.name === 'REJECTION_BEFORE_TRADING') ?
                  <div className="bmc-row-card-header">
                    <div className="bmc-card-row">
                      <RejectedBefore stage="COLLECTION_POINT" control={control} setData={setData} data={data} setValues={setValue} /> 
                    </div>
                  </div>
                :
                (subFormType.name === 'REJECTED_REMOVAL' && rejectedType.name === 'REJECTION_AFTER_TRADING') ?
                  <div className="bmc-row-card-header">
                    <div className="bmc-card-row">
                      <RejectedAfter stage="COLLECTION_POINT" control={control} setData={setData} data={data} setValues={setValue} /> 
                    </div>
                  </div>
                :
                (subFormType.name === 'DEATH_REMOVAL' && deathType.name === 'DEATH_BEFORE_TRADING') ?
                  <div className="bmc-row-card-header">
                    <div className="bmc-card-row">
                      <DeathBefore stage="COLLECTION_POINT" control={control} setData={setData} data={data} setValues={setValue} /> 
                    </div>
                  </div>
                :
                (subFormType.name === 'DEATH_REMOVAL' && deathType.name === 'DEATH_AFTER_TRADING') ?
                  <div className="bmc-row-card-header">
                    <div className="bmc-card-row">
                      <DeathAfter stage="COLLECTION_POINT" control={control} setData={setData} data={data} setValues={setValue} /> 
                    </div>
                  </div>
                :
                  <React.Fragment></React.Fragment>
                }
            </React.Fragment>
          ):
            <React.Fragment></React.Fragment>
          }
        </form>
      </div>
    </React.Fragment>
  );
};

export default RemovalFeePage;
