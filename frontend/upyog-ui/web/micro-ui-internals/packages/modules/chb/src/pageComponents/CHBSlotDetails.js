import React, { useEffect, useState } from "react";
import { FormStep, CardLabel, TextInput,Dropdown, TextArea,Card,CardSubHeader} from "@upyog/digit-ui-react-components";
import { useLocation} from "react-router-dom";
import Timeline from "../components/CHBTimeline";
import { Controller, useForm } from "react-hook-form";
import ChbCancellationPolicy from "../components/ChbCancellationPolicy";
// import { TimerValues } from "../components/TimerValues";


/**
 * CHBSlotDetails Component
 * 
 * This component is responsible for rendering and managing the slot details form for the CHB module.
 * It allows users to input and manage details related to the booking slot, such as special category, purpose, and purpose description.
 * 
 * Props:
 * - `t`: Translation function for internationalization.
 * - `config`: Configuration object for the form step.
 * - `onSelect`: Callback function triggered when the form step is completed.
 * - `userType`: Type of the user (e.g., employee or citizen).
 * - `formData`: Existing form data to prefill the fields.
 * - `value`: Default value for the form fields.
 * 
 * State Variables:
 * - `specialCategory`: State variable to manage the selected special category for the booking slot.
 * - `purpose`: State variable to manage the purpose of the booking slot.
 * - `purposeDescription`: State variable to manage the description of the purpose for the booking slot.
 * 
 * Variables:
 * - `validation`: Object to store validation rules for the form fields.
 * - `tenantId`: The current tenant ID fetched using the Digit ULB Service.
 * - `stateId`: The state ID fetched using the Digit ULB Service.
 * - `Category`: Data fetched for special categories using the `useSpecialCategory` hook.
 * - `Purposes`: Data fetched for purposes using the `usePurpose` hook.
 * - `category`: Array to store formatted special category options for the dropdown.
 * - `purposes`: Array to store formatted purpose options for the dropdown.
 * 
 * Logic:
 * - Initializes state variables using the `formData` object, allowing for prefilled data if available.
 * - Fetches special categories and purposes from MDMS using custom hooks.
 * - Maps over the fetched data to format it into dropdown options for the form fields.
 * 
 * Returns:
 * - A form step component that allows users to input and manage slot details, with dropdowns for special category and purpose, and a text area for purpose description.
 */
const CHBSlotDetails
  = ({ t, config, onSelect, userType, formData,value=formData.slotlist}) => {
    const { pathname: url } = useLocation();
    let index = window.location.href.charAt(window.location.href.length - 1);
    const [specialCategory, setspecialcategory] = useState((formData.slots && formData.slots[index] && formData.slots[index].specialCategory) || formData?.slots?.specialCategory || value?.existingDataSet?.slots?.specialCategory || "");
    const [purpose, setpurpose] = useState((formData.slots && formData.slots[index] && formData.slots[index].purpose) || formData?.slots?.purpose || value?.existingDataSet?.slots?.purpose || "");

    const [purposeDescription, setPurposeDescription] = useState((formData.slots && formData.slots[index] && formData.slots[index].purposeDescription) || formData?.slots?.purposeDescription || value?.existingDataSet?.slots?.purposeDescription  || "");

    let validation = {};
    const tenantId =  Digit.ULBService.getCitizenCurrentTenant(true) || Digit.ULBService.getCurrentTenantId();
    const stateId = Digit.ULBService.getStateId();

    const { data: Category } = Digit.Hooks.useEnabledMDMS(tenantId, "CHB", [{ name: "SpecialCategory" }],
      {
        select: (data) => {
          const formattedData = data?.["CHB"]?.["SpecialCategory"]
          return formattedData;
        },
      });

    const { data: Purposes } = Digit.Hooks.useEnabledMDMS(tenantId, "CHB", [{ name: "Purpose" }],
      {
        select: (data) => {
          const formattedData = data?.["CHB"]?.["Purpose"]
          return formattedData;
        },
      });
    
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
            :<Timeline currentStep={2} />
        }
        <Card>
        <CardSubHeader>
        <div style={{display:"flex", justifyContent: "space-between", width: "100%" }}>
          {value?.bookingSlotDetails && value.bookingSlotDetails.length > 0
            ? formatSlotDetails(value.bookingSlotDetails)
            : null}
           {/* <TimerValues timerValues={value?.existingDataSet?.timervalue?.timervalue} SlotSearchData={value?.Searchdata} /> */}
          </div>
        </CardSubHeader>
        <ChbCancellationPolicy slotDetail={value?.bookingSlotDetails}/>
      </Card>
        <FormStep
          config={config}
          onSelect={goNext}
          onSkip={onSkip}
          t={t}
          isDisabled={!specialCategory || !purpose || !purposeDescription}
        >
          <div>

            <CardLabel>{`${t("CHB_SPECIAL_CATEGORY")}`} <span className="check-page-link-button">*</span></CardLabel>
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

            <CardLabel>{`${t("CHB_PURPOSE")}`} <span className="check-page-link-button">*</span></CardLabel>
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

          <CardLabel>{`${t("CHB_PURPOSE_DESCRIPTION")}`} <span className="check-page-link-button">*</span></CardLabel>
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