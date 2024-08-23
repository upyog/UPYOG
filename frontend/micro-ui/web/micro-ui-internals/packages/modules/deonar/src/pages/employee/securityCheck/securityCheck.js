import React, { useState, useEffect, useRef } from "react";
import { useTranslation } from "react-i18next";
import { Controller, useForm } from "react-hook-form";
import ImportTypeField from "../commonFormFields/importType";
import ImportPermissionNumberField from "../commonFormFields/importPermissionNumber";
import SearchButtonField from "../commonFormFields/searchBtn";
import TraderNameField from "../commonFormFields/traderName";
import LicenseNumberField from "../commonFormFields/licenseNumber";
import ImportPermissionDateField from "../commonFormFields/importPermissionDate";
import VehicleNumberField from "../commonFormFields/vehicleNumber";
import NumberOfAliveAnimalsField from "../commonFormFields/numberOfAliveAnimals";
import ArrivalDateField from "../commonFormFields/arrivalDate";
import ArrivalTimeField from "../commonFormFields/arrivalTime";
import SubmitButtonField from "../commonFormFields/submitBtn";
import ArrivalUuidField from "../commonFormFields/arrivalUuid";
import MainFormHeader from "../commonFormFields/formMainHeading";
import useCreateUuid from "../../../hooks/useCreateUuid";
import { arrivalMockData } from "../../../constants/dummyData";
import NumberOfDeadAnimalsField from "../commonFormFields/numberOfDeadAnimals";
import GawalNameField from "../commonFormFields/gawalName";
import useDate from "../../../hooks/useCurrentDate";
import useCurrentTime from "../../../hooks/useCurrentTime";

const SecurityCheckPage = () => {
  const { t } = useTranslation();
  const [data, setData] = useState({});
  const [disabledFlag, setDisabledFlag] = useState(false);

  const {
    control,
    setValue,
    handleSubmit,
    getValues,
    formState: { errors, isValid },
  } = useForm({
    defaultValues: {
      arrivalUuid: '',
      importType: '',
      importPermissionNumber: '',
      importPermissionDate: useDate(-2),
      traderName: '',
      licenseNumber: '',
      vehicleNumber: '',
      numberOfAliveAnimals: '',
      arrivalDate: useDate(0),
      arrivalTime: useCurrentTime(),
      gawalName: ''
    },
    mode: "onChange",
  });

  const fetchDataByReferenceNumber = async (referenceNumber) => {
    //setTimeout(() => {
      return arrivalMockData;
    //}, 1000);
  };

  // useEffect(() => {
  //   console.log(getValues("arrivalUuid"));
  //   console.log(getValues("importType"));
  //   console.log(getValues("importPermissionNumber"));
  // });

  const val = useRef(useCreateUuid(5));
  useEffect(() => {
    if (val.current.length > 0) {
      const newData = {
        ...data,
        arrivalUuid: val.current
      };
      setData(newData);
    }
  }, []);
  

  const handleSearch = async () => {
    console.log("handleSearch");
    const referenceNumber = val.current;
    if (referenceNumber) {
      try {
        const result = await fetchDataByReferenceNumber(referenceNumber);
        setData(result);
        Object.keys(result).forEach((key) => {
          setValue(key, result[key]);
        });
        setValue('arrivalUuid', val.current);
        setDisabledFlag(true);
      } catch (error) {
        console.error("Failed to fetch data", error);
        setDisabledFlag(true);
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
          <MainFormHeader title={"DEONAR_SECURITY_CHECK"} />
          <div className="bmc-row-card-header">
            <div className="bmc-card-row">
              <ArrivalUuidField control={control} setData={setData} data={data} uuid={val.current} disabled={true} />
              <ImportTypeField control={control} setData={setData} data={data} />
              <ImportPermissionNumberField control={control} setData={setData} data={data} />
              <SearchButtonField onSearch={handleSearch} />
            </div>
          </div>
          <div className="bmc-row-card-header">
            <div className="bmc-card-row">
              <TraderNameField control={control} setData={setData} data={data} disabled={disabledFlag} />
              <LicenseNumberField control={control} setData={setData} data={data} disabled={disabledFlag} />
              <ImportPermissionDateField control={control} setData={setData} data={data} />
              <VehicleNumberField control={control} setData={setData} data={data} />
            </div>
            <div className="bmc-card-row">
              <NumberOfAliveAnimalsField control={control} setData={setData} data={data} />
              <NumberOfDeadAnimalsField control={control} setData={setData} data={data} />
              <ArrivalDateField control={control} setData={setData} data={data} />
              <ArrivalTimeField control={control} setData={setData} data={data} />
              <GawalNameField control={control} setData={setData} data={data} disabled={disabledFlag} />
            </div>
            <SubmitButtonField control={control} />
          </div>
        </form>
      </div>
    </React.Fragment>
  );
};

export default SecurityCheckPage;
