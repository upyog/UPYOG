import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useForm } from "react-hook-form";
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
import { deathAfterTradingMockData, deathBeforeTradingMockData, notSoldMockData, rejectionAfterTradingMockData, rejectionBeforeTradingMockData, religiousPersonalMockData, salsetteRemovalMockData } from "../../../constants/dummyData";

const RemovalPage = () => {
  const { t } = useTranslation();
  const [data, setData] = useState({});
  const [subFormType, setSubFormType] = useState({});
  const [rejectedType, setRejectedType] = useState(null);
  const [deathType, setDeathType] = useState(null);
  const [defaults, setDefaults] = useState({});
  const [disabledFlag, setDisabledFlag] = useState(false);
  const [options, setOptions] = useState([]);

  const {
    control,
    setValue,
    handleSubmit,
    getValues,
    formState: { errors, isValid },
  } = useForm({ defaultValues: defaults, mode: "onChange" });

  useEffect(() => {
    let obj = {};
    if (
        (subFormType.name === "REJECTED_REMOVAL" && rejectedType === "REJECTION_BEFORE_TRADING") || 
        (subFormType.name === "DEATH_REMOVAL" && rejectedType === "DEATH_BEFORE_TRADING")
      ) {
      obj = {
        traderName: {},
        brokerName: {},
        gawalName: {},
        numberOfAnimals: 0,
        animalTokenNumber: 0,
        removalDate: new Date().toISOString().split('T')[0],
        removalTime: new Date().toTimeString().split(' ')[0]
      };
    }
    else if (
      (subFormType.name === "REJECTED_REMOVAL" && rejectedType === "REJECTION_AFTER_TRADING") ||
      (subFormType.name === "DEATH_REMOVAL" && rejectedType === "DEATH_AFTER_TRADING")
    ) {
      obj = {
        shopkeeperName: {},
        dawanwalaName: {},
        numberOfAnimals: 0,
        animalTokenNumber: 0,
        removalDate: new Date().toISOString().split('T')[0],
        removalTime: new Date().toTimeString().split(' ')[0]
      };
    }
    else if (subFormType.name === "SALSETTE") {
      obj = {
        removalType: subFormType,
        removalFeeReceiptNumber: '',
        traderName: {},
        brokerName: {},
        gawalName: {},
        dairywalaName: {},
        numberOfAnimals: 0,
        animalTokenNumber: 0,
        removalDate: new Date().toISOString().split('T')[0],
        removalTime: new Date().toTimeString().split(' ')[0]
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
        removalDate: new Date().toISOString().split('T')[0],
        removalTime: new Date().toTimeString().split(' ')[0]
      };
    }
    else if (subFormType.name === "REMOVAL_OF_NOT_SOLD_ANIMALS") {
      obj = {
        traderName: {},
        brokerName: {},
        gawalName: {},
        numberOfAnimals: 0,
        animalTokenNumber: 0,
        removalDate: new Date().toISOString().split('T')[0],
        removalTime: new Date().toTimeString().split(' ')[0]
      };
    }
    setDefaults(obj);
  }, [subFormType, rejectedType, deathType]);

  const fetchDataByReferenceNumber = async () => {
    if (subFormType.name === "REJECTED_REMOVAL" && rejectedType === "REJECTION_BEFORE_TRADING") {
      return rejectionBeforeTradingMockData;
    }
    else if (subFormType.name === "REJECTED_REMOVAL" && rejectedType === "REJECTION_AFTER_TRADING")
    {
      return rejectionAfterTradingMockData;
    }
    else if (subFormType.name === "DEATH_REMOVAL" && deathType === "DEATH_BEFORE_TRADING") {
      return deathBeforeTradingMockData;
    }
    else if (subFormType.name === "DEATH_REMOVAL" && deathType === "DEATH_AFTER_TRADING") {
      return deathAfterTradingMockData;
    }
    else if (subFormType.name === "SALSETTE") {
      return salsetteRemovalMockData;
    }
    else if (subFormType.name === "RELIGIOUS_PERSONAL_PURPOSE") {
      return religiousPersonalMockData;
    }
    else if (subFormType.name === "REMOVAL_OF_NOT_SOLD_ANIMALS") {
      return notSoldMockData;
    }
    else {
      return {};
    }
  };

  useEffect(() => {
    setOptions(removalTypes);
  }, []);

  const handleSearch = async () => {
    console.log("handle search");
    console.log(data);
    const referenceNumber = getValues("removalFeeReceiptNumber");
    if (referenceNumber) {
      try {
        const result = await fetchDataByReferenceNumber();
        setData(result);
        Object.keys(result).forEach((key) => {
          setValue(key, result[key]);
        });
        setValue("removalFeeReceiptNumber", referenceNumber);
        setValue("removalType", subFormType);
        console.log(subFormType);
        setSubFormType(getValues("removalType"));
        setDisabledFlag(true);
        console.log(result);
      } catch (error) {
        console.error("Failed to fetch data", error);
        setDisabledFlag(false);
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
              <RemovalTypeOptionsField control={control} setData={setData} data={data} setSubFormType={setSubFormType} options={removalTypes} />
              <RemovalFeeReceiptNumberField control={control} setData={setData} data={data} />
              
              {
                (subFormType.name === "REJECTED_REMOVAL") ?
                  <RejectedRemovalTypesDropdownField control={control} setData={setData} data={data} setRejectedType={setRejectedType} options={rejectedRemovalTypes} disabled={disabledFlag} />
              :
                <React.Fragment></React.Fragment>
              }
              {
                (subFormType.name === "DEATH_REMOVAL") ?
                  <DeathRemovalTypesDropdownField control={control} setData={setData} data={data} setDeathType={setDeathType} options={deathRemovalTypes} disabled={disabledFlag} />
              :
                <React.Fragment></React.Fragment>
              }
              <SearchButtonField onSearch={handleSearch} />
            </div>
          </div>
          { subFormType ? (
            <React.Fragment>
              {
                (subFormType.name === 'SALSETTE') ? 
                  <div className="bmc-row-card-header">
                    <div className="bmc-card-row">
                      <Salsette stage="SECURITY_CHECKPOINT" control={control} setData={setData} data={data} disabled={disabledFlag} />
                    </div>
                  </div> 
                :
                (subFormType.name === 'RELIGIOUS_PERSONAL_PURPOSE') ?
                  <div className="bmc-row-card-header">
                    <div className="bmc-card-row">
                      <ReligiousPersonal stage="SECURITY_CHECKPOINT" control={control} setData={setData} data={data} disabled={disabledFlag} /> 
                    </div>
                  </div>
                :
                (subFormType.name === 'REMOVAL_OF_NOT_SOLD_ANIMALS') ?
                  <div className="bmc-row-card-header">
                    <div className="bmc-card-row">
                      <NotSold stage="SECURITY_CHECKPOINT" control={control} setData={setData} data={data} disabled={disabledFlag} /> 
                    </div>
                  </div>
                :
                (subFormType.name === 'REJECTED_REMOVAL' && rejectedType === 'REJECTION_BEFORE_TRADING') ?
                  <div className="bmc-row-card-header">
                    <div className="bmc-card-row">
                      <RejectedBefore stage="SECURITY_CHECKPOINT" control={control} setData={setData} data={data} disabled={disabledFlag} /> 
                    </div>
                  </div>
                :
                (subFormType.name === 'REJECTED_REMOVAL' && rejectedType === 'REJECTION_AFTER_TRADING') ?
                  <div className="bmc-row-card-header">
                    <div className="bmc-card-row">
                      <RejectedAfter stage="SECURITY_CHECKPOINT" control={control} setData={setData} data={data} disabled={disabledFlag} /> 
                    </div>
                  </div>
                :
                (subFormType.name === 'DEATH_REMOVAL' && deathType === 'DEATH_BEFORE_TRADING') ?
                  <div className="bmc-row-card-header">
                    <div className="bmc-card-row">
                      <DeathBefore stage="SECURITY_CHECKPOINT" control={control} setData={setData} data={data} disabled={disabledFlag} /> 
                    </div>
                  </div>
                :
                (subFormType.name === 'DEATH_REMOVAL' && deathType === 'DEATH_AFTER_TRADING') ?
                  <div className="bmc-row-card-header">
                    <div className="bmc-card-row">
                      <DeathAfter stage="SECURITY_CHECKPOINT" control={control} setData={setData} data={data} disabled={disabledFlag} /> 
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

export default RemovalPage;
