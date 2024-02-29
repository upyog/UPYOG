import {
  CardLabel,
  CardLabelError,
  Dropdown,
  LabelFieldPair,
  LinkButton,
  //MobileNumber,
  TextInput,
  Toast,
} from "@upyog/digit-ui-react-components";
import _ from "lodash";
import React, { useEffect, useMemo, useState } from "react";
import { Controller, useForm } from "react-hook-form";
import { useTranslation } from "react-i18next";
import { useLocation } from "react-router-dom";
import { stringReplaceAll, CompareTwoObjects } from "../utils";

const createPtrDetails = () => ({

  doctorName: "",
  vaccinationNumber: "",
  lastVaccineDate: "",
  petAge: "",
  petType: "",
  breedType: "",
  clinicName: "",
  petName: "",
  petGender: "",

  key: Date.now(),
});

const PTRPetdetails = ({ config, onSelect, userType, formData, setError, formState, clearErrors }) => {
  const { t } = useTranslation();

  const { pathname } = useLocation();
  const [pets, setPets] = useState(formData?.pets || [createPtrDetails()]);
  const [focusIndex, setFocusIndex] = useState({ index: -1, type: "" });

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

  



  Breed_Type &&
    Breed_Type.map((breedss) => {
      if (breedss.PetType == pets[0]?.petType?.code) {
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


  useEffect(() => {
    onSelect(config?.key, pets);
  }, [pets]);




  const commonProps = {
    focusIndex,
    allOwners: pets,
    setFocusIndex,
    formData,
    formState,
    setPets,
    t,
    setError,
    clearErrors,
    config,
    menu,
    breed_type,
    pet_sex
  };

  return (
    <React.Fragment>
      {pets.map((pets, index) => (
        <OwnerForm key={pets.key} index={index} pets={pets} {...commonProps} />
      ))}

    </React.Fragment>
  )
};

const OwnerForm = (_props) => {
  const {
    pets,
    index,
    focusIndex,
    allOwners,
    setFocusIndex,
    setPets,
    t,
    formData,
    config,
    setError,
    clearErrors,
    formState,
    menu,
    breed_type,
    pet_sex

  } = _props;

  const [showToast, setShowToast] = useState(null);
  const {
    control, formState: localFormState, watch, setError: setLocalError, clearErrors: clearLocalErrors, setValue, trigger, } = useForm();
  const formValue = watch();
  const { errors } = localFormState;
  const tenantId = Digit.ULBService.getCurrentTenantId();

  const isIndividualTypeOwner = useMemo(
    () => formData?.ownershipCategory?.code.includes("INDIVIDUAL"),
    [formData?.ownershipCategory?.code],
  );

  const [part, setPart] = React.useState({});

  useEffect(() => {
    let _ownerType = isIndividualTypeOwner

    if (!_.isEqual(part, formValue)) {
      setPart({ ...formValue });
      setPets((prev) => prev.map((o) => (o.key && o.key === pets.key ? { ...o, ...formValue, ..._ownerType } : { ...o })));
      trigger();
    }
  }, [formValue]);

  useEffect(() => {
    if (Object.keys(errors).length && !_.isEqual(formState.errors[config.key]?.type || {}, errors))
      setError(config.key, { type: errors });
    else if (!Object.keys(errors).length && formState.errors[config.key]) clearErrors(config.key);
  }, [errors]);

  const errorStyle = { width: "70%", marginLeft: "30%", fontSize: "12px", marginTop: "-21px" };

  return (
    <React.Fragment>
      <div style={{ marginBottom: "16px" }}>
        <div style={{ border: "1px solid #E3E3E3", padding: "16px", marginTop: "8px" }}>
          {allOwners?.length > 2 ? (
            <div style={{ marginBottom: "16px", padding: "5px", cursor: "pointer", textAlign: "right" }}>
              X
            </div>
          ) : null}

          <LabelFieldPair>
            <CardLabel className="card-label-smaller">{t("PTR_SEARCH_PET_TYPE") + " *"}</CardLabel>
            <Controller
              control={control}
              name={"petType"}
              defaultValue={pets?.petType}
              rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
              render={(props) => (
                <Dropdown

                  className="form-field"
                  selected={props.value}
                  select={props.onChange}
                  onBlur={props.onBlur}
                  option={menu}
                  optionKey="i18nKey"
                  t={t}
                />

              )}

            />

          </LabelFieldPair>
          <CardLabelError style={errorStyle}>{localFormState.touched.petType ? errors?.petType?.message : ""}</CardLabelError>
          <LabelFieldPair>
            <CardLabel className="card-label-smaller">{t("PTR_SEARCH_BREED_TYPE") + " *"}</CardLabel>
            <Controller
              control={control}
              name={"breedType"}
              defaultValue={pets?.breedType}
              rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
              render={(props) => (
                <Dropdown
                  className="form-field"
                  selected={props.value}
                  select={props.onChange}
                  onBlur={props.onBlur}
                  option={breed_type}
                  optionKey="i18nKey"
                  t={t}
                />
              )}
            />
          </LabelFieldPair>
          <CardLabelError style={errorStyle}>{localFormState.touched.breedType ? errors?.breedType?.message : ""}</CardLabelError>

          <LabelFieldPair>
            <CardLabel className="card-label-smaller">{t("PTR_PET_NAME") + " *"}</CardLabel>
            <div className="field">
              <Controller
                control={control}
                name={"petName"}
                defaultValue={pets?.petName}
                rules={{
                  required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                  validate: { pattern: (val) => (/^[a-zA-Z\s]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
                }}
                render={(props) => (
                  <TextInput
                    value={props.value}
                    // disable={isEditScreen}
                    autoFocus={focusIndex.index === pets?.key && focusIndex.type === "petName"}
                    onChange={(e) => {
                      props.onChange(e.target.value);
                      setFocusIndex({ index: pets.key, type: "petName" });
                    }}
                    onBlur={(e) => {
                      setFocusIndex({ index: -1 });
                      props.onBlur(e);
                    }}
                  />
                )}
              />
            </div>
          </LabelFieldPair>
          <CardLabelError style={errorStyle}>{localFormState.touched.petName ? errors?.petName?.message : ""}</CardLabelError>

          <LabelFieldPair>
            <CardLabel className="card-label-smaller">{t("PTR_PET_AGE") + " *"}</CardLabel>
            <div className="field">
              <Controller
                control={control}
                name={"petAge"}
                defaultValue={pets?.petAge}
                rules={{
                  required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                  validate: (v) => (/^\d{1,4}$/.test(v) && parseInt(v, 10) >= 0 && parseInt(v, 10) <= 1440 ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")),

                }}
                render={(props) => (
                  <TextInput
                    value={props.value}
                    // disable={isEditScreen}
                    autoFocus={focusIndex.index === pets?.key && focusIndex.type === "petAge"}
                    onChange={(e) => {
                      props.onChange(e);
                      setFocusIndex({ index: pets.key, type: "petAge" });
                    }}
                    labelStyle={{ marginTop: "unset" }}
                    onBlur={props.onBlur}
                    placeholder="in months"
                  />

                )}
              />

            </div>

          </LabelFieldPair>
          <div style={{textAlign: 'center'}}>
            {Math.floor(watch('petAge') / 12)}
            {Math.floor(watch('petAge') / 12) === 1 ? 'PTR_YEAR' : 'PTR_YEARS'}
            &nbsp;
            {watch('petAge') % 12}
            {watch('petAge') % 12 === 1 ? 'PTR_MONTH' : 'PTR_MONTHS'}
          </div>
          <br></br>
          <CardLabelError style={errorStyle}>{localFormState.touched.petAge ? errors?.petAge?.message : ""}</CardLabelError>


          <LabelFieldPair>
            <CardLabel className="card-label-smaller">{t("PTR_PET_SEX") + " *"}</CardLabel>
            <Controller
              control={control}
              name={"petGender"}
              defaultValue={pets?.petGender}
              // rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
              render={(props) => (
                <Dropdown
                  className="form-field"
                  selected={props.value}
                  select={props.onChange}
                  onBlur={props.onBlur}
                  // disable={isEditScreen}
                  option={pet_sex}
                  optionKey="i18nKey"
                  t={t}
                />
              )}
            />
          </LabelFieldPair>
          <CardLabelError style={errorStyle}>{localFormState.touched.petGender ? errors?.petGender?.message : ""}</CardLabelError>

          <LabelFieldPair>
            <CardLabel className="card-label-smaller">{t("PTR_DOCTOR_NAME") + " *"}</CardLabel>
            <div className="field">
              <Controller
                control={control}
                name={"doctorName"}
                defaultValue={pets?.doctorName}
                rules={{
                  required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                  validate: { pattern: (val) => (/^\w+( +\w+)*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
                }}
                render={(props) => (
                  <TextInput
                    value={props.value}
                    // disable={isEditScreen}
                    autoFocus={focusIndex.index === pets?.key && focusIndex.type === "doctorName"}
                    onChange={(e) => {
                      props.onChange(e.target.value);
                      setFocusIndex({ index: pets.key, type: "doctorName" });
                    }}
                    onBlur={props.onBlur}
                  />
                )}
              />
            </div>
          </LabelFieldPair>
          <CardLabelError style={errorStyle}>
            {localFormState.touched.doctorName ? errors?.doctorName?.message : ""}
          </CardLabelError>
          <LabelFieldPair>
            <CardLabel className="card-label-smaller">{t("PTR_CLINIC_NAME") + " *"}</CardLabel>
            <div className="field">
              <Controller
                control={control}
                name={"clinicName"}
                defaultValue={pets?.clinicName}
                rules={{
                  required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                  validate: { pattern: (val) => (/^\w+( +\w+)*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
                }}
                render={(props) => (
                  <TextInput
                    value={props.value}
                    // disable={isEditScreen}
                    autoFocus={focusIndex.index === pets?.key && focusIndex.type === "clinicName"}
                    onChange={(e) => {
                      props.onChange(e.target.value);
                      setFocusIndex({ index: pets.key, type: "clinicName" });
                    }}
                    onBlur={props.onBlur}
                  />
                )}
              />
            </div>
          </LabelFieldPair>
          <CardLabelError style={errorStyle}>
            {localFormState.touched.clinicName ? errors?.clinicName?.message : ""}
          </CardLabelError>

          <LabelFieldPair>
            <CardLabel className="card-label-smaller">{t("PTR_VACCINATED_DATE") + " *"}</CardLabel>
            <div className="field">
              <Controller
                control={control}
                name={"lastVaccineDate"}
                defaultValue={pets?.lastVaccineDate}
                rules={{
                  required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                  validDate: (val) => (/^\d{4}-\d{2}-\d{2}$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")),
                }}
                render={(props) => (
                  <TextInput
                    type="date"
                    value={props.value}
                    onChange={(e) => {
                      props.onChange(e.target.value);
                    }}
                    max={new Date().toISOString().split('T')[0]}
                  />
                )}
              />
            </div>
          </LabelFieldPair>
          <CardLabelError style={errorStyle}>{localFormState.touched.lastVaccineDate ? errors?.lastVaccineDate?.message : ""}</CardLabelError>

          <LabelFieldPair>
            <CardLabel className="card-label-smaller">{t("PTR_VACCINATION_NUMBER") + " *"}</CardLabel>
            <div className="field">
              <Controller
                control={control}
                name={"vaccinationNumber"}
                defaultValue={pets?.vaccinationNumber}
                // rules={{
                //  // required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                //   //validate: { pattern: (val) => (/^\w+( +\w+)*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
                // }}
                render={(props) => (
                  <TextInput
                    value={props.value}
                    // disable={isEditScreen}
                    autoFocus={focusIndex.index === pets?.key && focusIndex.type === "vaccinationNumber"}
                    onChange={(e) => {
                      props.onChange(e.target.value);
                      setFocusIndex({ index: pets.key, type: "vaccinationNumber" });
                    }}
                    onBlur={props.onBlur}
                  />
                )}
              />
            </div>
          </LabelFieldPair>
          <CardLabelError style={errorStyle}>
            {localFormState.touched.vaccinationNumber ? errors?.vaccinationNumber?.message : ""}
          </CardLabelError>



        </div>
      </div>
      {showToast?.label && (
        <Toast
          label={showToast?.label}
          onClose={(w) => {
            setShowToast((x) => null);
          }}
        />
      )}
    </React.Fragment>
  );
};

export default PTRPetdetails;