import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { Controller, useForm } from "react-hook-form";
import MainFormHeader from "../commonFormFields/formMainHeading";
import VehicleTypeDropdownField from "../commonFormFields/vehicleTypeDropdown";
import VehicleNumberField from "../commonFormFields/vehicleNumber";
import ParkingDateField from "../commonFormFields/parkingDate";
import ParkingTimeField from "../commonFormFields/parkingTime";
import ParkingAmountField from "../commonFormFields/parkingAmount";
import SubmitPrintButtonFields from "../commonFormFields/submitPrintBtn";
import useDate from "../../../hooks/useCurrentDate";
import useCurrentTime from "../../../hooks/useCurrentTime";

const ParkingFeePage = () => {
  const { t } = useTranslation();
  const [data, setData] = useState({});

  const {
    control,
    handleSubmit,
    formState: { errors, isValid },
  } = useForm({ 
    defaultValues: {
      vehicleType: '',
      vehicleNumber: '',
      parkingDate: useDate(0),
      parkingTime: useCurrentTime(),
      parkingAmount: 0
    },
    mode: "onChange"
  });

  const onSubmit = (formData) => {
    console.log("Form data submitted:", formData);
    const jsonData = JSON.stringify(formData);
    console.log("Generated JSON:", jsonData);
  };

  return (
    <React.Fragment>
      <div className="bmc-card-full">
        <form onSubmit={handleSubmit(onSubmit)}>
          <MainFormHeader title={"DEONAR_PARKING_FEE"} />
          <div className="bmc-row-card-header">
            <div className="bmc-card-row">
              <VehicleTypeDropdownField control={control} setData={setData} data={data} />
              <VehicleNumberField control={control} setData={setData} data={data} />
              <ParkingDateField control={control} setData={setData} data={data} />
              <ParkingTimeField control={control} setData={setData} data={data} />
            </div>
            <div className="bmc-card-row">
              <ParkingAmountField control={control} setData={setData} data={data} />
            </div>
            <SubmitPrintButtonFields control={control} />
          </div>
        </form>
      </div>
    </React.Fragment>
  );
};

export default ParkingFeePage;
