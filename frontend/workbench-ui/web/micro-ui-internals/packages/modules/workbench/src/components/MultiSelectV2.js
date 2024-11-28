import React, { useEffect, useState } from "react";
import Select from "react-select";
import { useTranslation } from "react-i18next";
import { Loader } from "@egovernments/digit-ui-react-components";
import _ from "lodash";

const customStyles = {
  control: (provided, state) => ({
    ...provided,
    borderColor: state.isFocused ? "#f47738" : "#505a5f",
    borderRadius: "unset",
    "&:hover": {
      borderColor: "#f47738",
    },
  }),
};

/* Multiple support not added TODO jagan to fix this issue */
const CustomSelectWidget = (props) => {
  const { t } = useTranslation();
  const { moduleName, masterName } = Digit.Hooks.useQueryParams();
  const {
    options,
    value,
    disabled,
    readonly,
    onChange,
    onBlur,
    onFocus,
    placeholder,
    multiple = false,
    schema = { schemaCode: "", fieldPath: "" },
  } = props;
  const { schemaCode = `${moduleName}.${masterName}`, tenantId, fieldPath } = schema;
  const [prefix, setPrefix] = useState(schemaCode);
  const { configs, updateConfigs, updateSchema, schema: formSchema, formData } = Digit.Hooks.workbench.useWorkbenchFormContext();
useEffect(()=>{
  const customConfig = configs?.customUiConfigs?.custom?.filter((data) => data?.fieldPath == fieldPath)?.[0] || {};
  const newPrefix = customConfig?.prefix || prefix;
  const suffix = customConfig?.suffix;

  newPrefix != prefix && setPrefix(newPrefix);
},[configs]);
  const handleChange = (selectedValue) => onChange(multiple ? selectedValue?.value : selectedValue?.value);
  /*
  logic added to fetch data of schemas in each component itself
  */
  const { isLoading, data } = Digit.Hooks.useCustomAPIHook(Digit.Utils.workbench.getCriteriaForSelectData(props) );
  const optionsList = data || options?.enumOptions || options || [];
  const formattedOptions = React.useMemo(
    () => optionsList.map((e) => ({ label: t(Digit.Utils.locale.getTransformedLocale(prefix?.trim?.()!=""?`${prefix}_${e?.label}`:e?.label)), value: e?.value })),
    [optionsList, prefix, data]
  );
  const selectedOption = formattedOptions?.filter((obj) => (multiple ? value?.includes(obj.value) : obj.value == value));
  if (isLoading) {
    return <Loader />;
  }

  return (
    <Select
      className="form-control form-select"
      classNamePrefix="digit"
      options={formattedOptions}
      isDisabled={disabled || readonly}
      placeholder={placeholder}
      onBlur={onBlur}
      onFocus={onFocus}
      closeMenuOnScroll={true}
      value={selectedOption}
      onChange={handleChange}
      isSearchable={true}
      isMulti={multiple}
      styles={customStyles}
    />
  );
};
export default CustomSelectWidget;