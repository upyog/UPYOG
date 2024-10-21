import React, { useEffect, useState, useContext } from "react";
import { FormStep, TextInput, CardLabel, MobileNumber, RadioButtons, Dropdown, RadioOrSelect } from "@nudmcdgnpm/digit-ui-react-components";
import { cardBodyStyle, convertDotValues } from "../utils";
import { useLocation, useRouteMatch } from "react-router-dom";
import Timeline from "../components/PTRTimeline";
import { Controller, useForm } from "react-hook-form";
import { ApplicationContext } from "../Module";


const PTRCitizenPet
  = ({ t, config, onSelect, userType, formData, renewApplication }) => {
    const { pathname: url } = useLocation();
    const convertToObject = (String) => String ? { i18nKey: String, code: String, value: String } : null; // function to convert an a single string value into an object for dropdowns
    let validation = {};

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
    const [petColor, setpetColor] = useState(renewApplication?.petDetails?.petColor || formData?.pets?.petColor || "");
    const [identificationmark, setidentificationmark] = useState(renewApplication?.petDetails?.identificationmark || formData?.pets?.identificationmark || "");
    const [selectBirthAdoption, setSelectBirthAdoption] = useState(renewApplication?.petDetails?.selectBirthAdoption || formData?.pets?.selectBirthAdoption || [{ i18nKey: "", code: "" }]);
    const [birthDate, setBirthDate] = useState(renewApplication?.petDetails?.birthDate || formData?.pets?.birth || "");
    const [adoptionDate, setAdoptionDate] = useState(renewApplication?.petDetails?.adoptionDate || formData?.pets?.adoption || "");
    const tenantId = Digit.ULBService.getCurrentTenantId();
    const stateId = Digit.ULBService.getStateId();


    const { data: Menu } = Digit.Hooks.ptr.usePTRPetMDMS(stateId, "PetService", "PetType"); // hook for pettype data
    const { data: Breed_Type} = Digit.Hooks.ptr.useBreedTypeMDMS(stateId, "PetService", "BreedType");  // hook for breed type data

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

    const { data: Pet_Sex } = Digit.Hooks.ptr.usePTRGenderMDMS(stateId, "common-masters", "GenderType");       // this hook is for Pet gender type { male, female}

    let pet_sex = [];    // array to store data of pet sex

    Pet_Sex &&
      Pet_Sex.map((ptrgenders) => {
        if (ptrgenders.code !== "TRANSGENDER")
          pet_sex.push({ i18nKey: `PTR_GENDER_${ptrgenders.code}`, code: `${ptrgenders.code}`, name: `${ptrgenders.code}` });
      });


    // used the custom hook for getting petcolor data from mdms and format it according to the dropdown need
    let { data: pet_color, isLoading } = Digit.Hooks.useCustomMDMS(Digit.ULBService.getStateId(), "PetService", [{ name: "PetColor" }],
      {
        select: (data) => {
          const formattedData = data?.["PetService"]?.["PetColor"].map((petone) => {
            return { i18nKey: `${petone.colourName}`, colourCode: `${petone.colourCode}`, code: `${petone.colourName}`, active: `${petone.active}` };
          })
          return formattedData;
        },
      });


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
      ownerStep = { ...owner, petType, breedType, petGender, petName, petAge, doctorName, clinicName, lastVaccineDate, vaccinationNumber, birthDate, adoptionDate, identificationmark, petColor };
      onSelect(config.key, ownerStep, false);
    };

    const onSkip = () => onSelect();

    // run goNext function on the updation of the provided fields
    useEffect(() => {
      if (userType === "citizen") {
        goNext();
      }
    }, [petType, breedType, petGender, petName, petAge, doctorName, lastVaccineDate, birthDate, adoptionDate, identificationmark, petColor]);


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
                  style={{ width: "86%" }}
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
                  style={{ width: "86%" }}
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
                  option={pet_color}
                  optionKey="i18nKey"
                  t={t}
                  placeholder={"Select"}
                />
              )}
            />

            <CardLabel>{`${t("PTR_IDENTIFICATION_MARK")}`}</CardLabel>
            <TextInput
              t={t}
              type={"text"}
              isMandatory={false}
              optionKey="i18nKey"
              name="identificationmark"
              value={identificationmark}
              onChange={setIdentificationmark}
              style={{ width: "86%" }}
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
              style={{ width: "86%" }}
              placeholder="in months"
              ValidationRequired={true}
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