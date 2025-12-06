import _ from "lodash";
import React, { useEffect, useState } from "react";
import { Loader } from "../atoms/Loader";
import RadioButtons from "../atoms/RadioButtons";
import Dropdown from "../atoms/Dropdown";
import MultiSelectDropdown from "../atoms/MultiSelectDropdown";

/**
 * Custom Dropdown / Radio Button component can be used mostly via formcomposer
 *
 * @author jagankumar-egov
 *
 * @example
 * 
 * 
 * {
 * component: "CustomDropdown",
        isMandatory: false,
        key: "gender",
        type: "radio",
        label: "Enter Gender",
        disable: false,
        populators: {
          name: "gender",
          optionsKey: "name",
          error: "sample required message",
          required: false,
          options: [
            {
              code: "MALE",
              name: "MALE",
            },
            {
              code: "FEMALE",
              name: "FEMALE",
            },
            {
              code: "TRANSGENDER",
              name: "TRANSGENDER",
            },
          ],
        },
      }
or 
      {
        component: "CustomDropdown",
        isMandatory: true,
        key: "genders",
        type: "radioordropdown",
        label: "Enter Gender",
        disable: false,
        populators: {
          name: "genders",
          optionsKey: "name",
          error: "sample required message",
          required: true,
          mdmsConfig: {
            masterName: "GenderType",
            moduleName: "common-masters",
            localePrefix: "COMMON_GENDER",
          },
        },
      },
 *
 */
const CustomDropdown = ({ t, config, inputRef, label, onChange, value, errorStyle, disable, type, additionalWrapperClass = "",mdmsv2=false,props}) => {
  const master = { name: config?.mdmsConfig?.masterName };
  if (config?.mdmsConfig?.filter) {
    master["filter"] = config?.mdmsConfig?.filter;
  }
  const { isLoading, data } = Digit.Hooks.useCustomMDMS(Digit.ULBService.getStateId(), config?.mdmsConfig?.moduleName, [master], {
      select: config?.mdmsConfig?.select
        ? Digit.Utils.createFunction(config?.mdmsConfig?.select)
        : (data) => {
            const optionsData = _.get(data, `${config?.mdmsConfig?.moduleName}.${config?.mdmsConfig?.masterName}`, []);
            return optionsData
              .filter((opt) => (opt?.hasOwnProperty("active") ? opt.active : true))
              .map((opt) => ({ ...opt, name: `${config?.mdmsConfig?.localePrefix}_${Digit.Utils.locale.getTransformedLocale(opt.code)}` }));
          },
      enabled: (config?.mdmsConfig || config?.mdmsv2) ? true : false,
    },mdmsv2);
  if (isLoading) {
    return <Loader />;
  }

  // const getValue = () => {
  //   let selectedValue = ""
  //   if(data?.length === 1 || config?.options?.length === 1) {
  //     selectedValue = data?.[0] || config?.options?.[0]
  //   } else {
  //     selectedValue = value
  //   }
  //   return selectedValue
  // }

  if(config?.optionsDisable && config?.options && config?.defaultOptions){
    config?.options?.forEach(obj1 => obj1.isDisabled = config?.defaultOptions.some(obj2 => obj2.type === obj1.code));
  }

  return (
    <React.Fragment key={config.name}>
      {/* <LabelFieldPair>
        <CardLabel className="card-label-smaller">
          {t(label)}
          {config.required ? " * " : null}
        </CardLabel> */}

      { (config.allowMultiSelect && type==="dropdown") ?
        <div style={{ display: "grid", gridAutoFlow: "row" }}>
          <MultiSelectDropdown
            options={data || config?.options || []}
            optionsKey={config?.optionsKey}
            props={props} //these are props from Controller
            isPropsNeeded={true}
            onSelect={(e) => {
              props?.onChange
                ? props.onChange(
                    e
                      ?.map((row) => {
                        return row?.[1] ? row[1] : null;
                      })
                      .filter((e) => e)
                  )
                : onChange(
                    e
                      ?.map((row) => {
                        return row?.[1] ? row[1] : null;
                      })
                      .filter((e) => e)
                  );
            }}
            selected={props?.value || value}
            defaultLabel={t(config?.defaultText) }
            defaultUnit={t(config?.selectedText) || t("COMMON_SELECTED")}
            config={config}
            disable={disable}
            optionsDisable={config?.optionsDisable}
          />
        </div> : type === "radio" ? (
        <RadioButtons
          inputRef={inputRef}
          style={{ display: "flex", justifyContent: "flex-start", gap: "3rem", ...config.styles }}
          options={data || config?.options || []}
          key={config.name}
          optionsKey={config?.optionsKey}
          value={value}
          onSelect={(e) => {
            onChange(e, config.name);
          }}
          disable={disable}
          selectedOption={value}
          defaultValue={value}
          t={t}
          errorStyle={errorStyle}
          additionalWrapperClass={additionalWrapperClass}
          innerStyles={config?.innerStyles}
        />
      ) : (
        <Dropdown
          inputRef={inputRef}
          style={{ display: "flex", justifyContent: "space-between", ...config.styles }}
          option={data || config?.options || []}
          key={config.name}
          optionKey={config?.optionsKey}
          value={value}
          select={(e) => {
            onChange(e, config.name);
          }}
          disable={disable}
          selected={value || config.defaultValue}
          defaultValue={value || config.defaultValue}
          t={t}
          errorStyle={errorStyle}
          optionCardStyles={config?.optionsCustomStyle}
        />
      )
      }
    </React.Fragment>
  );
};

export default CustomDropdown;
