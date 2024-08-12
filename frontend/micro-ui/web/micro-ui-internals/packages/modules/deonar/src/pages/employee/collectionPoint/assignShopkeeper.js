import React, { useState } from "react";
import { useTranslation } from "react-i18next";
import { CardLabel, Dropdown, LabelFieldPair, TextInput, DatePicker } from "@egovernments/digit-ui-react-components";
import { Controller, useForm } from "react-hook-form";
import { shopkeeperBuyingPurposeTypes } from "./constants/buyingPurposeTypes";
import SlaughterInAbbatoirSubform from "./assignShopkeeperChildForms/slaughterInAbbatoir";
import ReligiousPersonalRemovalSubform from "./assignShopkeeperChildForms/religiousPersonalRemoval";
import SalsetteRemovalSubform from "./assignShopkeeperChildForms/salsetteRemoval";
import ImportPermissionNumberField from "../commonFormFields/importPermissionNumber";
import LicenseNumberField from "../commonFormFields/licenseNumber";
import RemovalTypeOptionsField from "../commonFormFields/removalTypesDropdown";
import SearchButtonField from "../commonFormFields/searchBtn";
import MainFormHeader from "../commonFormFields/formMainHeading";

const AssignShopkeeperAfterTrading = () => {
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
          <MainFormHeader title={"DEONAR_ASSIGN_SHOPKEEPER_AFTER_TRADING"} />
          <div className="bmc-row-card-header">
            <div className="bmc-card-row">
            <ImportPermissionNumberField control={control} setData={setData} data={data} />
            <LicenseNumberField control={control} setData={setData} data={data} />
            <RemovalTypeOptionsField setSubFormType={setSubFormType} options={shopkeeperBuyingPurposeTypes} />
              <SearchButtonField />
            </div>
          </div>
            <div className="bmc-card-row">
            { subFormType ? (
                <React.Fragment>
                {
                    (subFormType === 'SLAUGHTER_IN_ABBATOIR') ? 
                        <div className="bmc-row-card-header">
                            <div className="bmc-card-row">
                                <SlaughterInAbbatoirSubform control={control} setData={setData} data={data}  />
                            </div>
                        </div> 
                    :
                    (subFormType === 'RELIGIOUS_PERSONAL_PURPOSE') ?
                        <div className="bmc-row-card-header">
                            <div className="bmc-card-row">
                                <ReligiousPersonalRemovalSubform control={control} setData={setData} data={data} /> 
                            </div>
                        </div>
                    :
                    (subFormType === 'SALSETTE_REMOVAL') ?
                        <div className="bmc-row-card-header">
                            <div className="bmc-card-row">
                                <SalsetteRemovalSubform isShopkeeperAssignment={true} control={control} setData={setData} data={data} />
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
