import React, { useEffect, useState } from "react";
import { FormStep, TextInput, CardLabel, MobileNumber, Dropdown, RadioOrSelect } from "@nudmcdgnpm/digit-ui-react-components";
import { cardBodyStyle } from "../utils";
import { useLocation, useRouteMatch } from "react-router-dom";
import Timeline from "../components/PTRTimeline";
import { Controller, useForm } from "react-hook-form";


const PTRCitizenPet
  = ({ t, config, onSelect, userType, formData, ownerIndex }) => {
    const { pathname: url } = useLocation();
    let index = window.location.href.charAt(window.location.href.length - 1);
    let validation = {};
    const [petType, setPetType] = useState((formData.pets && formData.pets[index] && formData.pets[index].petType) || formData?.pets?.petType || "");
    const [breedType, setBreedType] = useState((formData.pets && formData.pets[index] && formData.pets[index].breedType) || formData?.pets?.breedType || "");
    const [petGender, setPetGender] = useState((formData.pets && formData.pets[index] && formData.pets[index].petGender) || formData?.pets?.petGender || "");



    const [petName, setPetName] = useState(
      (formData.pets && formData.pets[index] && formData.pets[index].petName) || formData?.pets?.petName || ""
    );

    const [petAge, setPetAge] = useState(
      (formData.pets && formData.pets[index] && formData.pets[index].petAge) || formData?.pets?.petAge || ""
    );

    const [doctorName, setDoctorName] = useState(
      (formData.pets && formData.pets[index] && formData.pets[index].doctorName) || formData?.pets?.doctorName || ""
    );

    const [clinicName, setClinicName] = useState(
      (formData.pets && formData.pets[index] && formData.pets[index].clinicName) || formData?.pets?.clinicName || ""
    );

    const [vaccinationNumber, setVaccinationNumber] = useState(
      (formData.pets && formData.pets[index] && formData.pets[index].vaccinationNumber) || formData?.pets?.vaccinationNumber || ""
    );

    const [lastVaccineDate, setVaccinationDate] = useState(
      (formData.pets && formData.pets[index] && formData.pets[index].lastVaccineDate) || formData?.pets?.lastVaccineDate || ""
    );





    const tenantId = Digit.ULBService.getCurrentTenantId();
    const stateId = Digit.ULBService.getStateId();

    const { data: Menu } = Digit.Hooks.ptr.usePTRPetMDMS(stateId, "PetService", "PetType");

    const { data: Breed_Type } = Digit.Hooks.ptr.useBreedTypeMDMS(stateId, "PetService", "BreedType");  // hooks for breed type

    let menu = [];   //variable name for pettype
    let breed_type = [];
    // variable name for breedtype

    Menu &&
      Menu.map((petone) => {
        menu.push({ i18nKey: `PTR_PET_${petone.code}`, code: `${petone.code}`, value: `${petone.name}` });
      });

    const { control } = useForm();




    Breed_Type &&
      Breed_Type.map((breedss) => {
        if (breedss.PetType == petType?.code) {
          breed_type.push({
            i18nKey: `PTR_BREED_TYPE_${breedss.code}`,
            code: `${breedss.code}`,
            value: `${breedss.name}`
          });
        }

      });


    const { data: Pet_Sex } = Digit.Hooks.ptr.usePTRGenderMDMS(stateId, "common-masters", "GenderType");       // this hook is for Pet gender type { male, female}

    let pet_sex = [];    //for pet gender 

    Pet_Sex &&
      Pet_Sex.map((ptrgenders) => {
        if (ptrgenders.code !== "TRANSGENDER")
          pet_sex.push({ i18nKey: `PTR_GENDER_${ptrgenders.code}`, code: `${ptrgenders.code}`, name: `${ptrgenders.code}` });
      });



    function setpettype(e) {
      setPetType(e.target.value);
    }

    function setbreedtype(e) {
      setBreedType(e.target.value);
    }

    function setpetgender(e) {
      setPetGender(e.target.value);
    }





    function setpetage(e) {
      setPetAge(e.target.value);
    }

    function setdoctorname(e) {
      setDoctorName(e.target.value);
    }
    function setclinicname(e) {
      setClinicName(e.target.value);
    }

    function setvaccinationdate(e) {
      setVaccinationDate(e.target.value);
    }

    function setvaccinationnumber(e) {
      setVaccinationNumber(e.target.value);
    }

    function setpetname(e) {
      setPetName(e.target.value);
    }






    const goNext = () => {
      let owner = formData.pets && formData.pets[index];
      let ownerStep;
      if (userType === "citizen") {
        ownerStep = { ...owner, petType, breedType, petGender, petName, petAge, doctorName, clinicName, lastVaccineDate, vaccinationNumber };
        onSelect(config.key, { ...formData[config.key], ...ownerStep }, false, index);
      } else {

        ownerStep = { ...owner, petType, breedType, petGender, petName, petAge, doctorName, clinicName, lastVaccineDate, vaccinationNumber };
        onSelect(config.key, ownerStep, false, index);
      }
    };

    const onSkip = () => onSelect();


    useEffect(() => {
      if (userType === "citizen") {
        goNext();
      }
    }, [petType, breedType, petGender, petName, petAge, doctorName, lastVaccineDate]);







    return (
      <React.Fragment>
        {
          window.location.href.includes("/citizen") ?
            <Timeline currentStep={2} />
            : null
        }

        <FormStep
          config={config}
          onSelect={goNext}
          onSkip={onSkip}
          t={t}
          isDisabled={!petType || !breedType || !petGender || !petName || !petAge || !doctorName || !clinicName || !lastVaccineDate || !vaccinationNumber}
        >
          <div>
            <CardLabel>{`${t("PTR_SEARCH_PET_TYPE")}`} <span className="astericColor">*</span></CardLabel>
            <Controller
              control={control}
              name={"petType"}
              defaultValue={petType}
              rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
              render={(props) => (
                <Dropdown
                  className="form-field"
                  selected={petType}
                  select={setPetType}
                  option={menu}
                  optionKey="i18nKey"
                  t={t}
                  placeholder={"Select"}
                />
              )}
            />
            <CardLabel>{`${t("PTR_SEARCH_BREED_TYPE")}`} <span className="astericColor">*</span></CardLabel>
            <Controller
              control={control}
              name={"breedType"}
              defaultValue={breedType}
              rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
              render={(props) => (
                <Dropdown
                  className="form-field"
                  selected={breedType}
                  select={setBreedType}
                  option={breed_type}
                  optionKey="i18nKey"
                  t={t}
                  placeholder={"Select"}
                />
              )}
            />


            <CardLabel>{`${t("PTR_PET_NAME")}`} <span className="astericColor">*</span></CardLabel>
            <TextInput
              t={t}
              type={"text"}
              isMandatory={false}
              optionKey="i18nKey"
              name="petName"
              value={petName}
              onChange={setpetname}
              style={{ width: "86%" }}
              ValidationRequired={false}
              {...(validation = {
                isRequired: true,
                pattern: "^[a-zA-Z ]+$",
                type: "text",
                title: t("PT_NAME_ERROR_MESSAGE"),
              })}
            />

            <CardLabel>{`${t("PTR_PET_SEX")}`} <span className="astericColor">*</span></CardLabel>
            <Controller
              control={control}
              name={"petGender"}
              defaultValue={petGender}
              rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
              render={(props) => (
                <Dropdown
                  className="form-field"
                  selected={petGender}
                  select={setPetGender}
                  option={pet_sex}
                  optionKey="i18nKey"
                  t={t}
                  placeholder={"Select"}
                />
              )}
            />

            <CardLabel>{`${t("PTR_PET_AGE") + " (in months)"}`} <span className="astericColor">*</span></CardLabel>
            <TextInput
              t={t}
              type={"text"}
              isMandatory={false}
              optionKey="i18nKey"
              name="petAge"
              value={petAge}
              onChange={setpetage}
              style={{ width: "86%" }}
              placeholder="in months"
              ValidationRequired = {true}
              {...(validation = {
                isRequired: true,
                pattern: "[0-9]{1}[0-9]{2}",
                type: "tel",
                title: t("PT_NAME_ERROR_MESSAGE"),
              })}
            />
            
            <div>
            {Math.floor(petAge / 12)}&nbsp;&nbsp;
            {Math.floor(petAge / 12) === 1 ? "YEAR" : "YEARS"}
            &nbsp;&nbsp;
            {petAge % 12}&nbsp;&nbsp;
            {petAge % 12 === 1 ? "MONTH" : "MONTHS"}
          </div>

            <br></br>

            <CardLabel>{`${t("PTR_DOCTOR_NAME")}`} <span className="astericColor">*</span></CardLabel>
            <TextInput
              t={t}
              type={"text"}
              isMandatory={false}
              optionKey="i18nKey"
              name="doctorName"
              value={doctorName}
              onChange={setdoctorname}
              style={{ width: "86%" }}
              ValidationRequired={false}
              {...(validation = {
                isRequired: true,
                pattern: "^[a-zA-Z ]+$",
                type: "text",
                title: t("PT_NAME_ERROR_MESSAGE"),
              })}
            />

            <CardLabel>{`${t("PTR_CLINIC_NAME")}`} <span className="astericColor">*</span></CardLabel>
            <TextInput
              t={t}
              type={"text"}
              isMandatory={false}
              optionKey="i18nKey"
              name="clinicName"
              value={clinicName}
              onChange={setclinicname}
              style={{ width: "86%" }}
              ValidationRequired={false}
              {...(validation = {
                isRequired: true,
                pattern: "^[a-zA-Z ]+$",
                type: "text",
                title: t("PT_NAME_ERROR_MESSAGE"),
              })}
            />

            <CardLabel>{`${t("PTR_VACCINATED_DATE")}`} <span className="astericColor">*</span></CardLabel>
            <TextInput
              t={t}
              type={"date"}
              isMandatory={false}
              optionKey="i18nKey"
              name="lastVaccineDate"
              value={lastVaccineDate}
              onChange={setvaccinationdate}
              style={{ width: "86%" }}
              max={new Date().toISOString().split('T')[0]}
              rules={{
                required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                validDate: (val) => (/^\d{4}-\d{2}-\d{2}$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")),
              }}

            />

            <CardLabel>{`${t("PTR_VACCINATION_NUMBER")}`} <span className="astericColor">*</span></CardLabel>
            <TextInput
              t={t}
              type={"text"}
              isMandatory={false}
              optionKey="i18nKey"
              name="vaccinationNumber"
              value={vaccinationNumber}
              onChange={setvaccinationnumber}
              style={{ width: "86%" }}

            />

          </div>
        </FormStep>
      </React.Fragment>
    );
  };

export default PTRCitizenPet;