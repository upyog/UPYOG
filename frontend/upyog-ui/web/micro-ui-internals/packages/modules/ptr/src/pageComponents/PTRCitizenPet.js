import React, { useEffect, useState } from "react";
import { FormStep, TextInput, CardLabel, RadioButtons, Dropdown } from "@upyog/digit-ui-react-components";
import Timeline from "../components/PTRTimeline";
import { Controller, useForm } from "react-hook-form";
import { convertEpochToDate } from "../utils";


const PTRCitizenPet = ({ t, config, onSelect, userType, formData, renewApplication }) => {
  const user = Digit.UserService.getUser().info;
    const convertToObject = (String) => String ? { i18nKey: String, code: String, value: String } : null; // function to convert an a single string value into an object for dropdowns
    let validation = {};

    // custom hook for getting petcolor data from mdms and format it according to the dropdown need
    let { data: pet_color } = Digit.Hooks.useEnabledMDMS(Digit.ULBService.getStateId(), "PetService", [{ name: "PetColor" }],
    {
      select: (data) => {
        const formattedData = data?.["PetService"]?.["PetColor"].map((petone) => {
          return { i18nKey: `${petone.colourName}`, colourCode: `${petone.colourCode}`, code: `${petone.colourName}`, active: `${petone.active}` };
        })
        return formattedData;
      },
    });

    const inputStyles = { width: user.type === "EMPLOYEE" ? "50%" : "86%" };

  // added data from renewapplication, renders data if there is data in renewapplication
    const [petType, setPetType] = useState(convertToObject(renewApplication?.petDetails?.petType) || formData?.pets?.petType || "");
    const [breedType, setBreedType] = useState(convertToObject(renewApplication?.petDetails?.breedType) || formData?.pets?.breedType || "");
    const [petGender, setPetGender] = useState(convertToObject(renewApplication?.petDetails?.petGender) || formData?.pets?.petGender || "");
    const [petName, setPetName] = useState(renewApplication?.petDetails?.petName || formData?.pets?.petName || "");
    const [petAge, setPetAge] = useState(renewApplication?.petDetails?.petAge || formData?.pets?.petAge || "");
    const [doctorName, setDoctorName] = useState(renewApplication?.petDetails?.petName || formData?.pets?.doctorName || "");
    const [clinicName, setClinicName] = useState(renewApplication?.petDetails?.clinicName || formData?.pets?.clinicName || "");
    const [vaccinationNumber, setVaccinationNumber] = useState(renewApplication?.petDetails?.vaccinationNumber || formData?.pets?.vaccinationNumber || "");
    const [lastVaccineDate, setVaccinationDate] = useState(renewApplication?.petDetails?.lastVaccineDate || formData?.pets?.lastVaccineDate || "");
    const [petColor, setpetColor] = useState( formData?.pets?.petColor || "");
    const [identificationMark, setidentificationmark] = useState(renewApplication?.petDetails?.identificationMark || formData?.pets?.identificationMark || "");
    const [selectBirthAdoption, setSelectBirthAdoption] = useState(convertToObject(renewApplication?.petDetails?.birthDate || renewApplication?.petDetails?.adoptionDate ? (renewApplication?.petDetails?.birthDate ? "Birth" : "Adoption") : null) || convertToObject(formData?.pets?.birthDate || formData?.pets?.adoptionDate ? (formData?.pets?.birthDate ? "Birth" : "Adoption") : null) || [{ i18nKey: "", code: "" }]);
    const [birthDate, setBirthDate] = useState(convertEpochToDate(renewApplication?.petDetails?.birthDate) || formData?.pets?.birthDate || "");
    const [adoptionDate, setAdoptionDate] = useState(convertEpochToDate(renewApplication?.petDetails?.adoptionDate) || formData?.pets?.adoptionDate || "");
    const stateId = Digit.ULBService.getStateId();

    const { data: Menu } = Digit.Hooks.useEnabledMDMS(stateId, "PetService", [{ name: "PetType" }],
      {
        select: (data) => {
          const formattedData = data?.["PetService"]?.["PetType"]
          return formattedData;
        },
      });

    const { data: Breed_Type } = Digit.Hooks.useEnabledMDMS(stateId, "PetService", [{ name: "BreedType" }],
      {
        select: (data) => {
          const formattedData = data?.["PetService"]?.["BreedType"]
          return formattedData;
        },
      });

    let menu = [];   // array to store pettype data
    let breed_type = [];    // array to store  breedtype data

    // setting petType data into a structure for pettype input field
    Menu &&
      Menu.map((petone) => {
        menu.push({ i18nKey: `PTR_PET_${petone.code}`, code: `${petone.code}`, value: `${petone.name}` });
      });

    const { control } = useForm();

    // setting breedType data into a structure for breedtype input field
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


      useEffect(() => {
        // To set the petColor
        if(renewApplication?.petDetails){
        setpetColor(pet_color?.filter((color) => color?.colourCode === renewApplication?.petDetails?.petColor)?.[0])
        }
      }, [pet_color])

    const { data: Pet_Sex } = Digit.Hooks.ptr.usePTRGenderMDMS(stateId, "common-masters", "GenderType");       // this hook is for Pet gender type { male, female}

    let pet_sex = [];    // array to store data of pet sex

    Pet_Sex &&
      Pet_Sex.map((ptrgenders) => {
        if (ptrgenders.code !== "TRANSGENDER")
          pet_sex.push({ i18nKey: `PTR_GENDER_${ptrgenders.code}`, code: `${ptrgenders.code}`, name: `${ptrgenders.code}` });
      });


      // If birthDate is selected, the petAge is setted in months
      useEffect(() => {
        if (birthDate) {
          const [year, month, day] = birthDate.split('-').map(Number);
          const newbirthDate = new Date(year, month - 1, day);  
          const today = new Date();
      
          // Calculate the year and month difference
          let yearsDiff = today.getFullYear() - newbirthDate.getFullYear();
          let monthsDiff = today.getMonth() - newbirthDate.getMonth();
      
          // Adjust if the birth month hasn't occurred yet this year
          if (monthsDiff < 0) {
              yearsDiff -= 1;
              monthsDiff += 12;
          }
      
          // Total months
          const totalMonths = yearsDiff * 12 + monthsDiff;
          setPetAge(totalMonths)
        }
      }, [birthDate]);


    function setbirthDate(e) {
      setAdoptionDate(null); // set adoption field null if data filled in the birthdata field
      setBirthDate(e.target.value);
    }

    function setadoptionDate(e) {
      setBirthDate(null); // set birth field null if data filled in the adoption field
      setAdoptionDate(e.target.value);
    }

    function setIdentificationmark(e) {
      setidentificationmark(e.target.value);
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

    /* 
    @function: goNext()
    @description: To save data in the sessionStorage of each update of the provided fields
    */
    const goNext = () => {
      let owner = formData.pets;
      let ownerStep;
      ownerStep = { ...owner, petType, breedType, petGender, petName, petAge, doctorName, clinicName, lastVaccineDate, vaccinationNumber, birthDate, adoptionDate, identificationMark, petColor };
      onSelect(config.key, ownerStep, false);
    };

    const onSkip = () => onSelect();

    // run goNext function on the updation of the provided fields
    useEffect(() => {
      if (userType === "citizen") {
        goNext();
      }
    }, [petType, breedType, petGender, petName, petAge, doctorName, lastVaccineDate, birthDate, adoptionDate, identificationMark, petColor]);


    return (
      <React.Fragment>
        {<Timeline currentStep={2} />}

        <FormStep
          config={config}
          onSelect={goNext}
          onSkip={onSkip}
          t={t}
          isDisabled={!petType || !breedType || !petGender || !petName || !petAge || !doctorName || !clinicName || !lastVaccineDate || !vaccinationNumber || !((!birthDate && adoptionDate) || (!adoptionDate && birthDate))} // Description: make either one of the fields mandatory from birthdate and adoption date. @author: khalid rashid
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

            <CardLabel>{`${t("PTR_PET_COLOR")}`} <span className="astericColor">*</span></CardLabel>
            <Controller
              control={control}
              name={"petColor"}
              defaultValue={petColor}
              rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
              render={(props) => (
                <Dropdown
                  className="form-field"
                  selected={petColor}
                  select={setpetColor}
                  optionCardStyles={{ overflowY: "auto", maxHeight: "300px" }}
                  option={pet_color}
                  optionKey="i18nKey"
                  t={t}
                  placeholder={"Select"}
                />
              )}
            />

            {/* Radio field to select one of the two options: birth or adoption */}
            <CardLabel>{`${t("PTR_SELECT_BIRTH_ADOPTION")}`} <span className="astericColor">*</span></CardLabel>
            <RadioButtons
              t={t}
              options={[{ i18nKey: "Birth", code: "Birth" }, { i18nKey: "Adoption", code: "Adoption" }]}
              optionsKey="code"
              name="selectBirthAdoption"
              value={selectBirthAdoption}
              selectedOption={selectBirthAdoption}
              innerStyles={{ display: "inline-block", marginLeft: "20px", paddingBottom: "2px", marginBottom: "2px" }}
              onSelect={setSelectBirthAdoption}
              isDependent={true}
            />

            {/* On selecting birth or renewapplication having data the following field will render */}
            {(selectBirthAdoption?.code === "Birth" || renewApplication?.petDetails?.birth) && (
              <div>
                <CardLabel>{`${t("PTR_BIRTH")}`} <span className="astericColor">*</span></CardLabel>
                <TextInput
                  t={t}
                  type={"date"}
                  isMandatory={false}
                  optionKey="i18nKey"
                  name="birthDate"
                  value={birthDate}
                  onChange={setbirthDate}
                  style={inputStyles}
                  max={new Date().toISOString().split('T')[0]}
                  rules={{
                    required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                    validDate: (val) => (/^\d{4}-\d{2}-\d{2}$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")),
                  }}

                />
              </div>
            )}

            {/* On selecting adoption or renewapplication having data the following field will render */}
            {(selectBirthAdoption?.code === "Adoption" || renewApplication?.petDetails?.adoption) && (
              <div>
                <CardLabel>{`${t("PTR_ADOPTION")}`} <span className="astericColor">*</span></CardLabel>
                <TextInput
                  t={t}
                  type={"date"}
                  isMandatory={false}
                  optionKey="i18nKey"
                  name="adoptionDate"
                  value={adoptionDate}
                  onChange={setadoptionDate}
                  style={inputStyles}
                  max={new Date().toISOString().split('T')[0]}
                  rules={{
                    required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                    validDate: (val) => (/^\d{4}-\d{2}-\d{2}$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")),
                  }}

                />
              </div>
            )}


            <CardLabel>{`${t("PTR_PET_NAME")}`} <span className="astericColor">*</span></CardLabel>
            <TextInput
              t={t}
              type={"text"}
              isMandatory={false}
              optionKey="i18nKey"
              name="petName"
              value={petName}
              onChange={setpetname}
              style={inputStyles}
              ValidationRequired={false}
              {...(validation = {
                isRequired: true,
                pattern: "^[a-zA-Z ]+$",
                type: "text",
                title: t("PT_NAME_ERROR_MESSAGE"),
              })}
            />

            <CardLabel>{`${t("PTR_IDENTIFICATION_MARK")}`}</CardLabel>
            <TextInput
              t={t}
              type={"text"}
              isMandatory={false}
              optionKey="i18nKey"
              name="identificationMark"
              value={identificationMark}
              onChange={setIdentificationmark}
              style={inputStyles}
              ValidationRequired={false}
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
              style={inputStyles}
              placeholder="in months"
              ValidationRequired={true}
              {...(validation = {
                isRequired: true,
                pattern: "^(?:[1-9][0-9]?|[1-3][0-9]{2}|400)$",
                type: "tel",
                title: t("PT_NAME_ERROR_MESSAGE"),
              })}
            />

              {petAge !== undefined && petAge !== null && petAge >= 0 && (
                <div>
                  {Math.floor(petAge / 12)}&nbsp;&nbsp;
                  {Math.floor(petAge / 12) === 1 ? "YEAR" : "YEARS"}
                  &nbsp;&nbsp;
                  {petAge % 12}&nbsp;&nbsp;
                  {petAge % 12 === 1 ? "MONTH" : "MONTHS"}
                </div>
              )}
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
              style={inputStyles}
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
              style={inputStyles}
              ValidationRequired={false}
              {...(validation = {
                isRequired: true,
                pattern: "^[a-zA-Z0-9 .,?!'\"-]+$", // pattern to accept anything except special characters
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
              style={inputStyles}
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
              style={inputStyles}

            />

          </div>
        </FormStep>
      </React.Fragment>
    );
  };

export default PTRCitizenPet;