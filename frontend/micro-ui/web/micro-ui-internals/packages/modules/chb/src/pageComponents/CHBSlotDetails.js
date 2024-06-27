import React, { useEffect, useState } from "react";
import { FormStep, CardLabel, TextInput,Dropdown, TextArea,Card,CardSubHeader} from "@upyog/digit-ui-react-components";
import { useLocation} from "react-router-dom";
import Timeline from "../components/CHBTimeline";
import { Controller, useForm } from "react-hook-form";
import ChbCancellationPolicy from "../components/ChbCancellationPolicy";



const CHBSlotDetails
  = ({ t, config, onSelect, userType, formData,value=formData.slotlist}) => {
    const { pathname: url } = useLocation();
    let index = window.location.href.charAt(window.location.href.length - 1);
    const [selectslot, setslot] =useState((formData.slots && formData.slots[index] && formData.slots[index].selectslot) || formData?.slots?.selectslot || "");
    const [residentType, setresidenttype] = useState((formData.slots && formData.slots[index] && formData.slots[index].residentType) || formData?.slots?.residentType || "");
    const [specialCategory, setspecialcategory] = useState((formData.slots && formData.slots[index] && formData.slots[index].specialCategory) || formData?.slots?.specialCategory || "");
    const [purpose, setpurpose] = useState((formData.slots && formData.slots[index] && formData.slots[index].purpose) || formData?.slots?.purpose || "");

    const [purposeDescription, setPurposeDescription] = useState((formData.slots && formData.slots[index] && formData.slots[index].purposeDescription) || formData?.slots?.purposeDescription || "");

    let slot = [{
      "i18nKey": '01-may-2024 to 02-may-2024'
    }]; 
    let validation = {};
    const tenantId = Digit.ULBService.getCurrentTenantId();
    const stateId = Digit.ULBService.getStateId();
    const { data: Resident} = Digit.Hooks.chb.useResidentType(stateId, "CHB", "ChbResidentType");
    const { data: Category } = Digit.Hooks.chb.useSpecialCategory(stateId, "CHB", "ChbSpecialCategory");
    const { data: Purposes } = Digit.Hooks.chb.usePurpose(stateId, "CHB", "ChbPurpose");
    const { data: hallList } = Digit.Hooks.chb.useChbCommunityHalls(stateId, "CHB", "ChbCommunityHalls");

    

    let resident=[];
    let category=[];
    let purposes=[];
  
    Resident &&
    Resident.map((chbDetails) => {
      resident.push({ i18nKey: `CHB_${chbDetails.code}`, code: `${chbDetails.code}`, value: `${chbDetails.name}` });
      console.log("resident is-->",Resident);
      console.log("hallList is-->",hallList);
      });

      Category &&
      Category.map((chbDetails) => {
        category.push({ i18nKey: `CHB_${chbDetails.code}`, code: `${chbDetails.code}`, value: `${chbDetails.name}` });
      });

      Purposes &&
      Purposes.map((chbDetails) => {
        purposes.push({ i18nKey: `CHB_${chbDetails.code}`, code: `${chbDetails.code}`, value: `${chbDetails.name}` });
      });

    const { control } = useForm();
    function setpurposeDescription(e) {
      setPurposeDescription(e.target.value);
    }
    const goNext = () => {

      let owner = formData.slots && formData.slots[index];
      let ownerStep;
      if (userType === "citizen") {
        ownerStep = { ...owner, selectslot, residentType,specialCategory,purpose,purposeDescription};
        onSelect(config.key, { ...formData[config.key], ...ownerStep }, false, index);
      } else {

        ownerStep = { ...owner, selectslot, residentType,specialCategory,purpose,purposeDescription};
        onSelect(config.key, ownerStep, false, index);
      }
      console.log(ownerStep);
    };

    const onSkip = () => onSelect();


    useEffect(() => {
      if (userType === "citizen") {
        goNext();
      }
    }, [selectslot, residentType,specialCategory,purpose,purposeDescription]);







    return (
      <React.Fragment>
        {
          window.location.href.includes("/citizen") ?
            <Timeline currentStep={2} />
            : null
        }
        <Card>
      <CardSubHeader>{value?.bookingSlotDetails.map((slot) =>(
        <div>
        {slot.name}
     </div>
    // <div key={index}>
    //   {slot.name}
    //   {/* ({slot.date1}) */}
    // </div>
  ))}</CardSubHeader>
  <ChbCancellationPolicy/>
      </Card>
        <FormStep
          config={config}
          onSelect={goNext}
          onSkip={onSkip}
          t={t}
          isDisabled={ !selectslot || !residentType || !specialCategory || !purpose || !purposeDescription}
        >
          <div>
            <CardLabel>{`${t("CHB_SELECT_SLOT")}`} <span style={{ color: 'red' }}>*</span></CardLabel>

            <Controller
              control={control}
              name={"selectslot"}
              defaultValue={selectslot}
              rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
              render={(props) => (
                <Dropdown

                  className="form-field"
                  selected={selectslot}
                  select={setslot}
                  option={slot}
                  optionKey="i18nKey"
                  t={t}
                />

              )}

            />


            <CardLabel>{`${t("CHB_RESIDENT_TYPE")}`} <span style={{ color: 'red' }}>*</span></CardLabel>


            <Controller
              control={control}
              name={"residentType"}
              defaultValue={residentType}
              rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
              render={(props) => (
                <Dropdown

                  className="form-field"
                  selected={residentType}
                  select={setresidenttype}
                  option={resident}
                  optionKey="i18nKey"
                  t={t}
                />

              )}

            />


            <CardLabel>{`${t("CHB_SPECIAL_CATEGORY")}`} <span style={{ color: 'red' }}>*</span></CardLabel>


            <Controller
              control={control}
              name={"specialCategory"}
              defaultValue={specialCategory}
              rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
              render={(props) => (
                <Dropdown

                  className="form-field"
                  selected={specialCategory}
                  select={setspecialcategory}
                  option={category}
                  optionKey="i18nKey"
                  t={t}
                />

              )}

            />

            <CardLabel>{`${t("CHB_PURPOSE")}`} <span style={{ color: 'red' }}>*</span></CardLabel>


            <Controller
              control={control}
              name={"purpose"}
              defaultValue={purpose}
              rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
              render={(props) => (
                <Dropdown

                  className="form-field"
                  selected={purpose}
                  select={setpurpose}
                  option={purposes}
                  optionKey="i18nKey"
                  t={t}
                />

              )}
                />

          <CardLabel>{`${t("CHB_PURPOSE_DESCRIPTION")}`} <span style={{ color: 'red' }}>*</span></CardLabel>
          <TextArea
            t={t}
            type={"textarea"}
            isMandatory={false}
            optionKey="i18nKey"
            name="purposeDescription"
            value={purposeDescription}
            onChange={setpurposeDescription}
            style={{ width: "50%" }}
            ValidationRequired={false}
            {...(validation = {
              isRequired: true,
              pattern: "^[a-zA-Z ]+$",
              type: "textarea",
              title: t("PURPOSE_DESCRIPTION_ERROR_MESSAGE"),
            })}
          />
          </div>
        </FormStep>
      </React.Fragment>
    );
  };

export default CHBSlotDetails;