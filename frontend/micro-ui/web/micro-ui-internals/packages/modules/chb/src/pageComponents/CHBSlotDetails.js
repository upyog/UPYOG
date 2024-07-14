import React, { useEffect, useState } from "react";
import { FormStep, CardLabel, TextInput,Dropdown, TextArea,Card,CardSubHeader} from "@nudmcdgnpm/digit-ui-react-components";
import { useLocation} from "react-router-dom";
import Timeline from "../components/CHBTimeline";
import { Controller, useForm } from "react-hook-form";
import ChbCancellationPolicy from "../components/ChbCancellationPolicy";



const CHBSlotDetails
  = ({ t, config, onSelect, userType, formData,value=formData.slotlist}) => {
    const { pathname: url } = useLocation();
    let index = window.location.href.charAt(window.location.href.length - 1);
    const [specialCategory, setspecialcategory] = useState((formData.slots && formData.slots[index] && formData.slots[index].specialCategory) || formData?.slots?.specialCategory || "");
    const [purpose, setpurpose] = useState((formData.slots && formData.slots[index] && formData.slots[index].purpose) || formData?.slots?.purpose || "");

    const [purposeDescription, setPurposeDescription] = useState((formData.slots && formData.slots[index] && formData.slots[index].purposeDescription) || formData?.slots?.purposeDescription || "");

    let validation = {};
    const tenantId = Digit.ULBService.getCurrentTenantId();
    const stateId = Digit.ULBService.getStateId();
    const { data: Category } = Digit.Hooks.chb.useSpecialCategory(stateId, "CHB", "ChbSpecialCategory");
    const { data: Purposes } = Digit.Hooks.chb.usePurpose(stateId, "CHB", "ChbPurpose");
    
    let category=[];
    let purposes=[];
  

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
      const input = e.target.value.replace(/[^a-zA-Z\s]/g, '');
      setPurposeDescription(input);
    }
    const goNext = () => {

      let owner = formData.slots && formData.slots[index];
      let ownerStep;
      if (userType === "citizen") {
        ownerStep = { ...owner,specialCategory,purpose,purposeDescription};
        onSelect(config.key, { ...formData[config.key], ...ownerStep }, false, index);
      } else {

        ownerStep = { ...owner,specialCategory,purpose,purposeDescription};
        onSelect(config.key, ownerStep, false, index);
      }
      console.log(ownerStep);
    };

    const onSkip = () => onSelect();


    useEffect(() => {
      if (userType === "citizen") {
        goNext();
      }
    }, [specialCategory,purpose,purposeDescription]);

    const formatSlotDetails = (slots) => {
      const sortedSlots = slots.sort((a, b) => new Date(a.bookingDate) - new Date(b.bookingDate));
      const firstDate = sortedSlots[0]?.bookingDate;
      const lastDate = sortedSlots[sortedSlots.length - 1]?.bookingDate;
      if(firstDate===lastDate){
        return `${sortedSlots[0]?.name} (${firstDate})`;
      }
      else{
      return `${sortedSlots[0]?.name} (${firstDate} - ${lastDate})`;
      }
    };


    return (
      <React.Fragment>
        {
          window.location.href.includes("/citizen") ?
            <Timeline currentStep={2} />
            : null
        }
        <Card>
        <CardSubHeader>
          {value?.bookingSlotDetails && value.bookingSlotDetails.length > 0
            ? formatSlotDetails(value.bookingSlotDetails)
            : null}
        </CardSubHeader>
        <ChbCancellationPolicy count={value?.bookingSlotDetails.length}/>
      </Card>
        <FormStep
          config={config}
          onSelect={goNext}
          onSkip={onSkip}
          t={t}
          isDisabled={!specialCategory || !purpose || !purposeDescription}
        >
          <div>

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
                  placeholder={"Select Special Category"}
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
                  placeholder={"Select Purpose"}
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
            placeholder={"Enter Purpose Description"}
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