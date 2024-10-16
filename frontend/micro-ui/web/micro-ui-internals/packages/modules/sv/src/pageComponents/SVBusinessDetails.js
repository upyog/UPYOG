import React, { useEffect, useState } from "react";
import { FormStep, TextInput, CardLabel,Dropdown, LinkButton} from "@nudmcdgnpm/digit-ui-react-components";
import { Controller, useForm } from "react-hook-form";
import GIS from "./GIS";
import SVDayAndTimeSlot from "./SVDayAndTimeSlot";
import Timeline from "../components/Timeline";

const SVBusinessDetails = ({ t, config, onSelect, userType, formData }) => {
    let validation = {};
    console.log("formdatataaa",formData);
    const [vendingType, setvendingType] = useState(formData?.businessDetails?.vendingType || "");
    const [vendingZones, setvendingZones] = useState(formData?.businessDetails?.vendingZones || "");
    const [location, setlocation] = useState( formData?.businessDetails?.location || "");
    const [areaRequired, setareaRequired] = useState( formData?.businessDetails?.areaRequired || "" );
    const [nameOfAuthority, setnameOfAuthority] = useState(formData?.businessDetails?.nameOfAuthority || "");
    const [vendingLiscence, setvendingLiscence] = useState(formData?.businessDetails?.vendingLiscence || "");
    const [daysOfOperation, setDaysOfOperation] = useState(
        formData?.businessDetails?.daysOfOperation || [
          { name: "Monday", isSelected: false, startTime: "", endTime: "" },
          { name: "Tuesday", isSelected: false,startTime: "", endTime: "" },
          { name: "Wednesday", isSelected: false,startTime: "", endTime: "" },
          { name: "Thursday", isSelected: false,startTime: "", endTime: "" },
          { name: "Friday",isSelected: false, startTime: "", endTime: "" },
          { name: "Saturday", isSelected: false,startTime: "", endTime: "" },
          { name: "Sunday",isSelected: false, startTime: "", endTime: "" },
        ]
      );


      /* this checks two conditions:
       1. At least one day of the week is selected.
       2. If a day is selected, both the startTime and endTime for that day are filled.*/

      const validateDaysOfOperation = () => {
        const atLeastOneDaySelected = daysOfOperation.some(day => day.isSelected);
        const allSelectedDaysHaveTimeSlots = daysOfOperation.every(
            day => !day.isSelected || (day.startTime && day.endTime)
        );
        return atLeastOneDaySelected && allSelectedDaysHaveTimeSlots;
    };

      const handleDayToggle = (index) => {
        const updatedDays = [...daysOfOperation];
        updatedDays[index].isSelected = !updatedDays[index].isSelected;
        setDaysOfOperation(updatedDays);
      };
    
      // const handleTimeChange = (index, field, value) => {
      //   const updatedDays = [...daysOfOperation];
      //   updatedDays[index] = { ...updatedDays[index], [field]: value };
      //   setDaysOfOperation(updatedDays);
      // };
      const handleTimeChange = (index, field, value) => {
        const updatedDays = [...daysOfOperation];
        
        if (field === "startTime" || field === "endTime") {
          // Only update the time without the AM/PM part
          updatedDays[index][field] = value;
        } else {
          // Update the AM/PM part and concatenate it with the time
          const timeField = field === "startAmPm" ? "startTime" : "endTime";
          const timeValue = updatedDays[index][timeField];
          updatedDays[index][timeField] = `${timeValue} ${value}`; // Concatenate time and AM/PM
        }
      
        setDaysOfOperation(updatedDays);
      };

      const onAmPmChange = (index, field, value) => {
        const updatedDays = [...daysOfOperation];
        updatedDays[index] = { ...updatedDays[index], [field]: value };
        setDaysOfOperation(updatedDays);
      };
    
    
    const { control } = useForm();
    const [isOpen, setIsOpen] = useState(false);


    
    const { data: vendingTypeData } = Digit.Hooks.useCustomMDMS(Digit.ULBService.getStateId(), "StreetVending", [{ name: "VendingActivityType" }],
      {
        select: (data) => {
            const formattedData = data?.["StreetVending"]?.["VendingActivityType"]
            return formattedData;
        },
    }); 
    let vendingTypeOptions = [];
    vendingTypeData && vendingTypeData.map((vending) => {
        vendingTypeOptions.push({i18nKey: `${vending.name}`, code: `${vending.code}`, value: `${vending.name}`})
    }) 

    const { data: vendingZone } = Digit.Hooks.useCustomMDMS(Digit.ULBService.getStateId(), "StreetVending", [{ name: "VendingZones" }],
      {
        select: (data) => {
            const formattedData = data?.["StreetVending"]?.["VendingZones"]
            return formattedData;
        },
    }); 
    let vending_Zone = [];
    vendingZone && vendingZone.map((zone) => {
      vending_Zone.push({i18nKey: `${zone.name}`, code: `${zone.code}`, value: `${zone.name}`})
    }) 

    const handleGIS = () => {
        setIsOpen(!isOpen);
      }
    
      const handleRemove = () => {
        setIsOpen(!isOpen);
      }
    
    function onSave(location) {
        setlocation(location);
        setIsOpen(false);
      }

    function selectlocation(e) {
        formData.businessDetails["location"] = (typeof e === 'object' && e !== null) ? e.target.value : e;
        // setlocation((typeof e === 'object' && e !== null) ? e.target.value : e);
        setlocation(e.target.value);
      }

    function setNameOfAuthority(e) {
      setnameOfAuthority(e.target.value);
    }
    function setVendingLiscence(e) {
      setvendingLiscence(e.target.value);
    }

    function setAreaRequired(e) {
      setareaRequired(e.target.value);
    }

    
    const goNext = () => {
      if (!validateDaysOfOperation()) {
        alert("Please select at least one day and provide both start and end times for the selected day(s).");
        return;
    }
      let business = formData.businessDetails;
      let businessStep;

        businessStep = { ...business, vendingType, vendingZones, location, areaRequired, nameOfAuthority, vendingLiscence, daysOfOperation };
        onSelect(config.key, businessStep, false);
      
    };

    const onSkip = () => onSelect();


    useEffect(() => {
      if (userType === "citizen") {
        goNext();
      }
    }, [vendingType, vendingZones, location, areaRequired, nameOfAuthority, vendingLiscence, daysOfOperation]);




    return (
      <React.Fragment>
        {
          window.location.href.includes("/citizen") ? !isOpen &&<Timeline currentStep={2} />: null
        } 
        <div>
        {isOpen && <GIS t={t} onSelect={onSelect} formData={formData} handleRemove={handleRemove} onSave={onSave} />}

        {!isOpen && <FormStep
          config={config}
          onSelect={goNext}
          onSkip={onSkip}
          t={t}
          isDisabled={!vendingType || !vendingZones || !areaRequired || !nameOfAuthority || !daysOfOperation }

        >

          <div>
            <CardLabel>{`${t("SV_VENDING_TYPE")}`} <span className="astericColor">*</span></CardLabel>
            <Controller
              control={control}
              name={"vendingType"}
              defaultValue={vendingType}
              rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
              render={(props) => (
                <Dropdown
                  className="form-field"
                  selected={vendingType}
                  select={setvendingType}
                  option={vendingTypeOptions}
                  optionKey="i18nKey"
                  t={t}
                  placeholder={"Select"}
                />
              )}
            />
            <CardLabel>{`${t("SV_VENDING_ZONES")}`} <span className="astericColor">*</span></CardLabel>
            <Controller
              control={control}
              name={"vendingZones"}
              defaultValue={vendingZones}
              rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
              render={(props) => (
                <Dropdown
                  className="form-field"
                  selected={vendingZones}
                  select={setvendingZones}
                  option={vending_Zone}
                  optionKey="i18nKey"
                  t={t}
                  placeholder={"Select"}
                />
              )}
            />
            <CardLabel>{`${t("SV_LOCATION")}`}</CardLabel>
            <div>
            <TextInput
            isMandatory={false}
            optionKey="i18nKey"
            t={t}
            style={{ width: "86%" }}
            name="location"
            value={location}
            onChange={selectlocation}
            />
            <LinkButton
            label={
                <div>
                <span>
                    <svg 
                    style={{ float: "right", position: "relative", bottom: "25px", marginTop: "-17px", marginRight: "51%" }} width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path d="M11 7C8.79 7 7 8.79 7 11C7 13.21 8.79 15 11 15C13.21 15 15 13.21 15 11C15 8.79 13.21 7 11 7ZM19.94 10C19.48 5.83 16.17 2.52 12 2.06V0H10V2.06C5.83 2.52 2.52 5.83 2.06 10H0V12H2.06C2.52 16.17 5.83 19.48 10 19.94V22H12V19.94C16.17 19.48 19.48 16.17 19.94 12H22V10H19.94ZM11 18C7.13 18 4 14.87 4 11C4 7.13 7.13 4 11 4C14.87 4 18 7.13 18 11C18 14.87 14.87 18 11 18Z" fill="#a82227" />
                    </svg>
                </span>
                </div>
            }
            onClick={(e) => handleGIS()}
            />
            </div>

            <CardLabel>{t("SV_DAY_HOUR_OPERATION")} <span className="astericColor">*</span></CardLabel>
            {daysOfOperation.map((day, index) => (
            <SVDayAndTimeSlot
                key={index}
                t={t}
                day={day}
                onDayToggle={() => handleDayToggle(index)}
                onDayChange={() => {}} // No need to change the day name since all days are shown
                onTimeChange={(field, value) => handleTimeChange(index, field, value)}
                onAmPmChange={(field, value) => onAmPmChange(index, field, value)}
                ValidationRequired={true}
                {...(validation = {
                  isRequired: true,
                })}
            />
            ))}

            
            <CardLabel>{`${t("SV_AREA_REQUIRED")}`} <span className="astericColor">*</span></CardLabel>
            <TextInput
              t={t}
              type={"text"}
              isMandatory={false}
              optionKey="i18nKey"
              name="areaRequired"
              value={areaRequired}
              onChange={setAreaRequired}
              style={{ width: "86%" }}
              ValidationRequired={false}
              {...(validation = {
                isRequired: true,
                pattern: "^[0-9. ]{1,3}$",
                type: "tel",
                title: t("SV_INVALID_AREA"),
              })}
            />

            <CardLabel>{`${t("SV_LOCAL_AUTHORITY_NAME")}`} <span className="astericColor">*</span></CardLabel>
            <TextInput
              t={t}
              type={"text"}
              isMandatory={false}
              optionKey="i18nKey"
              name="nameOfAuthority"
              value={nameOfAuthority}
              onChange={setNameOfAuthority}
              style={{ width: "86%" }}
              ValidationRequired={false}
              {...(validation = {
                isRequired: true,
                pattern: "^[a-zA-Z ]*$",
                type: "text",
                title: t("SV_INPUT_DID_NOT_MATCH"),
              })}
            />

            <CardLabel>{`${t("SV_VENDING_LISCENCE")}`}</CardLabel>
            <TextInput
              t={t}
              type={"text"}
              isMandatory={false}
              optionKey="i18nKey"
              name="vendingLiscence"
              value={vendingLiscence}
              onChange={setVendingLiscence}
              style={{ width: "86%" }}
              ValidationRequired={false}
              {...(validation = {
                isRequired: false,
                pattern: "^[a-zA-Z ]+$",
                type: "text",
                title: t("PT_NAME_ERROR_MESSAGE"),
              })}
            />

          </div>
        </FormStep>}
        </div>
      </React.Fragment>
    );
  };

export default SVBusinessDetails;