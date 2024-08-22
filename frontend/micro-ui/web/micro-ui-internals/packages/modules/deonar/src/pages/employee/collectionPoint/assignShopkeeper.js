import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useForm } from "react-hook-form";
import { shopkeeperBuyingPurposeTypes } from "./constants/buyingPurposeTypes";
import SlaughterInAbbatoirSubform from "./assignShopkeeperChildForms/slaughterInAbbatoir";
import ReligiousPersonalRemovalSubform from "./assignShopkeeperChildForms/religiousPersonalRemoval";
import SalsetteRemovalSubform from "./assignShopkeeperChildForms/salsetteRemoval";
import ImportPermissionNumberField from "../commonFormFields/importPermissionNumber";
import LicenseNumberField from "../commonFormFields/licenseNumber";
import RemovalTypeOptionsField from "../commonFormFields/removalTypesDropdown";
import SearchButtonField from "../commonFormFields/searchBtn";
import MainFormHeader from "../commonFormFields/formMainHeading";
import { religiousPersonalRemovalMockData, salsetteRemovalShopkeeperAssignmentMockData, slaughterInAbbatoirMockData } from "../../../constants/dummyData";
import useSubmitForm from "../../../hooks/useSubmitForm";
import { COLLECTION_POINT_ENDPOINT } from "../../../constants/apiEndpoints";

const AssignShopkeeperAfterTrading = () => {
  const { t } = useTranslation();
  const [data, setData] = useState({});
  const [subFormType, setSubFormType] = useState({});
  const [defaults, setDefaults] = useState({});
  const [options, setOptions] = useState([]);

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
    if (subFormType.name === "SLAUGHTER_IN_ABBATOIR") {
      obj = {
        traderName: {},
        brokerName: {},
        gawalName: {},
        assignDate: new Date().toISOString().split('T')[0],
        shopkeeperName: {},
        dawanwalaName: {},
        shadeNumber: 1,
        numberOfAliveAnimals: 1,
        animalTokenNumber: 1,
        stablingDays: 2,
        stablingFeeAmt: 1,
        paymentMode: {},
        paymentReferenceNumber: 1
      };
    }
    else if (subFormType.name === "RELIGIOUS_PERSONAL_PURPOSE") {
      obj = {
        traderName: {},
        brokerName: {},
        gawalName: {},
        assignDate: new Date().toISOString().split('T')[0],
        citizenName: '',
        numberOfAnimals: 1,
        animalTokenNumber: 1,
        removalFeeAmount: 1,
        paymentMode: {},
        paymentReferenceNumber: 1
      };
    }
    else if (subFormType.name === "SALSETTE_REMOVAL") {
      obj = {
        traderName: {},
        brokerName: {},
        gawalName: {},
        assignDate: new Date().toISOString().split('T')[0],
        dairywalaName: {},
        numberOfAnimals: 1,
        animalTokenNumber: 1,
        salsetteFeeAmount: 1,
        paymentMode: {},
        paymentReferenceNumber: 1
      };
    }
    setDefaults(obj);
  }, [subFormType]);

  const fetchDataByReferenceNumber = async () => {
    if (subFormType.name === "SLAUGHTER_IN_ABBATOIR") {
      return slaughterInAbbatoirMockData;
    }
    else if (subFormType.name === "RELIGIOUS_PERSONAL_PURPOSE") {
      return religiousPersonalRemovalMockData;
    }
    else if (subFormType.name === "SALSETTE_REMOVAL") {
      return salsetteRemovalShopkeeperAssignmentMockData;
    }
    else {
      return {};
    }
  };

  useEffect(() => {
    setOptions(shopkeeperBuyingPurposeTypes);
  }, []);

  const handleSearch = async () => {
    const referenceNumber = getValues("importPermissionNumber");
    if (referenceNumber) {
      try {
        const result = await fetchDataByReferenceNumber(referenceNumber);
        setData(result);
        Object.keys(result).forEach((key) => {
          setValue(key, result[key]);
        });
        setValue("importPermissionNumber", referenceNumber);
        setValue("licenseNumber", getValues("licenseNumber"));
        setValue("removalType", subFormType);
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
          <MainFormHeader title={"DEONAR_ASSIGN_SHOPKEEPER_AFTER_TRADING"} />
            <div className="bmc-row-card-header">
              <div className="bmc-card-row">
                <ImportPermissionNumberField control={control} setData={setData} data={data} />
                <LicenseNumberField control={control} setData={setData} data={data} />
                <RemovalTypeOptionsField setSubFormType={setSubFormType} control={control} setData={setData} data={data} options={options} />
                <SearchButtonField onSearch={handleSearch} />
              </div>
            </div>
            <div className="bmc-card-row">
            { subFormType ? (
                <React.Fragment>
                {
                    (subFormType.name === 'SLAUGHTER_IN_ABBATOIR') ? 
                        <div className="bmc-row-card-header">
                            <div className="bmc-card-row">
                                <SlaughterInAbbatoirSubform control={control} setData={setData} data={data} setValues={setValue}  />
                            </div>
                        </div> 
                    :
                    (subFormType.name === 'RELIGIOUS_PERSONAL_PURPOSE') ?
                        <div className="bmc-row-card-header">
                            <div className="bmc-card-row">
                                <ReligiousPersonalRemovalSubform control={control} setData={setData} data={data} setValues={setValue} /> 
                            </div>
                        </div>
                    :
                    (subFormType.name === 'SALSETTE_REMOVAL') ?
                        <div className="bmc-row-card-header">
                            <div className="bmc-card-row">
                                <SalsetteRemovalSubform isShopkeeperAssignment={true} control={control} setData={setData} data={data} setValues={setValue} />
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
        </form>
      </div>
    </React.Fragment>
  );
};

export default AssignShopkeeperAfterTrading;
