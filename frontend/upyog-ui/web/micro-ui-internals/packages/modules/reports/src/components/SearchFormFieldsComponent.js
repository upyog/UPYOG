import React, { Fragment } from "react";
import { TextInput, SubmitBar, DatePicker, SearchField, Dropdown, CardLabelError, MobileNumber,MultiSelectDropdown,FilterFormField } from "@nudmcdgnpm/digit-ui-react-components";
import { useWatch } from "react-hook-form";


const getSearchField = (field, formState, Controller, register, control, t, extraHandlers = {}) => {
    const formErrors = formState?.errors;
    const { onStateChange, setValue, searchParams = [] } = extraHandlers;
    const handleFieldChange = (val, onChange) => {
        onChange(val);
        if (field.name === "state" && onStateChange) {
            const ulbField = searchParams.find((f) => f.name === "ulb");
            if (setValue && ulbField) {
                setValue("ulb", ulbField.type === "multivaluelist" ? [] : "");
            }
            onStateChange(val);
        }
    };

    switch (field.type) {
        case "string":
    return (
        <SearchField>
            <label>{t(`${field.label}${field.isMandatory ? "*" : ""}`)}</label>
            <TextInput
                name={field.name}
                type="text"
                inputRef={register(field.name, {
                    maxLength: {
                        value: 60,
                        message: t("EXCEEDS_60_CHAR_LIMIT"),
                    },
                    required: field.isMandatory
                })}
            />
        </SearchField>
    )

        case "singlevaluelist":
            var optionsArr = Object.values(field.defaultValue)?.map(el => t(el))
            optionsArr.unshift(t('ALL'))
            return (<SearchField>
                <label>{t(`${field.label}${field.isMandatory?"*":""}`)}</label>
                <Controller
                    control={control}
                    name={field.name}
                    rules={{
                        required: field.isMandatory
                    }}
                    render={({ field: controllerField }) => (
    <Dropdown
        selected={controllerField.value}
        select={(val) => handleFieldChange(val, controllerField.onChange)}
        onBlur={controllerField.onBlur}
        option={optionsArr}
        t={t}
    />
)}
                />
                {formErrors && formErrors?.[field.name] && formErrors?.[field.name]?.type === "required" && (
                    <CardLabelError>{t(`This field is required`)}</CardLabelError>
                )}
            </SearchField>
            )
        case "multivaluelist":
            const optionsObjArr = [
                { name: t("ALL"), code: "All" },
                ...Object.entries(field.defaultValue || {}).map(([key, val]) => {
                    const translatedVal = t(val);
                    return {
                        code: key,
                        name: field.islocalisationRequired && field.localisationPrefix ? t(`${field.localisationPrefix}${val}`) : translatedVal
                    };
                })
            ];
            const selectMulti = (listOfSelections, controllerField) => {
                const res = listOfSelections.map((item) => item[1]);
                controllerField.onChange(res);
            };
            return (
            <SearchField>
                <label>{t(`${field.label}${field.isMandatory ? "*" : ""}`)}</label>
                <Controller
                    control={control}
                    name={field.name}
                    rules={{
                        required: field.isMandatory
                    }}
                    render={({ field: controllerField }) => (
    <MultiSelectDropdown
        options={optionsObjArr}
        props={controllerField}
        isPropsNeeded={true}
        onSelect={selectMulti}
        selected={controllerField.value}
        optionsKey="name"
        defaultUnit={t("BPA_SELECTED_TEXT")}
    />
)}
                />
                {formErrors && formErrors?.[field.name] && formErrors?.[field.name]?.type === "required" && (
                    <CardLabelError>{t(`This field is required`)}</CardLabelError>
                )}
            </SearchField>
            )
        case "epoch":
            return (
                <>
                <SearchField>
                        <label>{`${t(field.label)}${field.isMandatory ? "*" : ""}`}</label>
                    <Controller
  name={field.name}
  control={control}
  rules={{
    required: field.isMandatory
  }}
  render={({ field: controllerField }) => (
    <DatePicker
      date={controllerField.value}
      onChange={(value) => controllerField.onChange(value)}
    />
  )}
/>
                        {formErrors && formErrors?.[field.name] && formErrors?.[field.name]?.type === "required" && (
                            <CardLabelError>{t(`This field is required`)}</CardLabelError>
                        )}
                </SearchField>
                
                </>
            )

        default:
            return (
                <div>Field not defined</div>
            )
    }
}

const SearchFormFieldsComponent = ({ formState, Controller, register, control, setValue, t, reset, data, onStateChange, onClearAll }) => {
    let resetObj={};

    data?.reportDetails?.searchParams?.map(el => el.type === "multivaluelist" ? resetObj[el?.name] = [] : resetObj[el?.name] = "")
    const extraHandlers = {
        onStateChange,
        setValue,
        searchParams: data?.reportDetails?.searchParams || [],
    };
    return (
        <>
            {data?.reportDetails?.searchParams?.map(field => (
                getSearchField(field, formState, Controller, register, control, t, extraHandlers)
            ))}
            <SearchField className="submit">
                <SubmitBar label={t("ES_COMMON_SEARCH")} submit />
                <p
                    className="rpt-search-form-fields-component-top-spacing"
                    onClick={() => {
                        reset(resetObj);
                        onClearAll?.();
                    }}
                >
                    {t(`ES_COMMON_CLEAR_ALL`)}
                </p>
            </SearchField>
        </>
    );
}

export default SearchFormFieldsComponent