import React, { useEffect, useState } from "react";
import { FormStep, CardLabel, Dropdown} from "@upyog/digit-ui-react-components";
import { useLocation} from "react-router-dom";
import Timeline from "../components/CHBTimeline";
import { Controller, useForm } from "react-hook-form";


const CHBSlotDetails
  = ({ t, config, onSelect, userType, formData,}) => {
    const { pathname: url } = useLocation();
    let index = window.location.href.charAt(window.location.href.length - 1);
    const [selectslot, setslot] =useState((formData.slots && formData.slots[index] && formData.slots[index].selectslot) || formData?.slots?.selectslot || "");
    const [residenttype, setresidenttype] = useState((formData.slots && formData.slots[index] && formData.slots[index].residenttype) || formData?.slots?.residenttype || "");
    const [specialcategory, setspecialcategory] = useState((formData.slots && formData.slots[index] && formData.slots[index].specialcategory) || formData?.slots?.specialcategory || "");
    const [purpose, setpurpose] = useState((formData.slots && formData.slots[index] && formData.slots[index].purpose) || formData?.slots?.purpose || "");

    let slot = [{
      "value": "01-may-2024 to 02-may-2024",
      "code": "01-may-2024 to 02-may-2024",
      "i18nKey": '01-may-2024 to 02-may-2024'
    },
    {
      "value": "01-may-2024 to 03-may-2024",
      "code": "01-may-2024 to 03-may-2024",
      "i18nKey": '01-may-2024 to 03-may-2024'
    },  
    {
      "value": "01-may-2024 to 04-may-2024",
      "code": "01-may-2024 to 04-may-2024",
      "i18nKey": '01-may-2024 to 04-may-2024'
    }]; 
    // let resident = [
    //   {
    //     "value": "Cant Resident",
    //     "code": "Cant Resident",
    //     "i18nKey": 'Cant Resident'
    //   }
    // ];
    // let category = [
    //   {
    //     "value": "Cantonment Staff",
    //     "code": "Cantonment Staff",
    //     "i18nKey": 'Cantonment Staff'
    //   },
    //   {
    //     "value": "Retaired Cant Staff",
    //     "code": "Retaired Cant Staff",
    //     "i18nKey": 'Retaired Cant Staff'
    //   },
    //   {
    //     "value": "Elected Member",
    //     "code": "Elected Member",
    //     "i18nKey": 'Elected Member'
    //   },
    //   {
    //     "value": "Below Poverty Line",
    //     "code": "Below Poverty Line",
    //     "i18nKey": 'Below Poverty Line'
    //   },
    //   {
    //     "value": "None",
    //     "code": "None",
    //     "i18nKey": 'None'
    //   }
    // ];
    // let purposes = [
    //   {
    //     "value": "Religious",
    //     "code": "Religious",
    //     "i18nKey": 'Religious'
    //   },
    //   {
    //     "value": "Marriage",
    //     "code": "Marriage",
    //     "i18nKey": 'Marriage'
    //   },
    //   {
    //     "value": "Social",
    //     "code": "Social",
    //     "i18nKey": 'Social'
    //   },
    //   {
    //     "value": "Conference",
    //     "code": "Conference",
    //     "i18nKey": 'Conference'
    //   },
    //   {
    //     "value": "Public Gathering",
    //     "code": "Public Gathering",
    //     "i18nKey": 'Public Gathering'
    //   }
    // ];

    const tenantId = Digit.ULBService.getCurrentTenantId();
    const stateId = Digit.ULBService.getStateId();

    const { data: Resident} = Digit.Hooks.chb.useResidentType(stateId, "CHB", "ChbResidentType");
    const { data: Category } = Digit.Hooks.chb.useSpecialCategory(stateId, "CHB", "ChbSpecialCategory");
    const { data: Purposes } = Digit.Hooks.chb.usePurpose(stateId, "CHB", "ChbPurpose");


      //variable name for pettype
    let resident=[];
    let category=[];
    let purposes=[];
  
    Resident &&
    Resident.map((chbDetails) => {
      resident.push({ i18nKey: `CHB_${chbDetails.code}`, code: `${chbDetails.code}`, value: `${chbDetails.name}` });
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

    const goNext = () => {

      let owner = formData.slots && formData.slots[index];
      let ownerStep;
      if (userType === "citizen") {
        ownerStep = { ...owner, selectslot, residenttype,specialcategory,purpose};
        onSelect(config.key, { ...formData[config.key], ...ownerStep }, false, index);
      } else {

        ownerStep = { ...owner, selectslot, residenttype,specialcategory,purpose};
        onSelect(config.key, ownerStep, false, index);
      }
      console.log(ownerStep);
    };

    const onSkip = () => onSelect();


    useEffect(() => {
      if (userType === "citizen") {
        goNext();
      }
    }, [selectslot, residenttype,specialcategory,purpose]);







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
          isDisabled={ !selectslot || !residenttype || !specialcategory || !purpose}
        >
          <div>
            <CardLabel>{`${t("SELECT_SLOT")}`}</CardLabel>

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


            <CardLabel>{`${t("RESIDENT_TYPE")}`}</CardLabel>


            <Controller
              control={control}
              name={"residenttype"}
              defaultValue={residenttype}
              rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
              render={(props) => (
                <Dropdown

                  className="form-field"
                  selected={residenttype}
                  select={setresidenttype}
                  option={resident}
                  optionKey="i18nKey"
                  t={t}
                />

              )}

            />


            <CardLabel>{`${t("SPECIAL_CATEGORY")}`}</CardLabel>


            <Controller
              control={control}
              name={"specialcategory"}
              defaultValue={specialcategory}
              rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
              render={(props) => (
                <Dropdown

                  className="form-field"
                  selected={specialcategory}
                  select={setspecialcategory}
                  option={category}
                  optionKey="i18nKey"
                  t={t}
                />

              )}

            />

            <CardLabel>{`${t("PURPOSE")}`}</CardLabel>


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
          </div>
        </FormStep>
      </React.Fragment>
    );
  };

export default CHBSlotDetails;