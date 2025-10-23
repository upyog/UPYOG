import { CardLabelError, SearchField, SearchForm, SubmitBar, TextInput,Localities,MobileNumber } from "@upyog/digit-ui-react-components";
import React, { useState } from "react";
import { Controller, useForm } from "react-hook-form";


const SwitchComponent = (props) => {
  let searchBy = props.searchBy;
  return (
    <div className="w-fullwidth PropertySearchFormSwitcher">
      {props.keys.map((key) => (
        <span key={key} className={searchBy === key ? "selected" : "non-selected"} onClick={() => {key === "searchDetail" && !(sessionStorage.getItem("searchDetailValue"))?sessionStorage.setItem("searchDetailValue",1):""; key==="searchId" && sessionStorage.getItem("searchDetailValue") == 1?sessionStorage.setItem("searchDetailValue",2):"";   props.onSwitch(key);props.onReset(); }}>
          {props.t(`PT_SEARCH_BY_${key?.toUpperCase()}`)}
        </span>
      ))}
    </div>
  );
};
const SearchPTID = ({ tenantId, t, onSubmit, onReset, searchBy, PTSearchFields, setSearchBy ,payload}) => {
  const { register, control, handleSubmit, setValue, watch,getValues, reset, formState } = useForm({
    defaultValues: {
      ...payload,
        }
  });
  const formValue = watch();
  const fields = PTSearchFields?.[searchBy] || {};
  sessionStorage.removeItem("revalidateddone");
  return (
    <div className="PropertySearchForm">
      <SearchForm onSubmit={onSubmit} className={"pt-property-search"} handleSubmit={handleSubmit}>
        <SwitchComponent keys={Object.keys(PTSearchFields || {})} searchBy={searchBy} onReset={onReset} t={t} onSwitch={setSearchBy} />
        {fields &&
          Object.keys(fields).map((key) => {
            let field = fields[key];
            let validation = field?.validation || {};
            return (
              <SearchField key={key} className={"pt-form-field"}>
                <label>{t(field?.label)}{`${field?.validation?.required?"*":""}`}</label>
                {field?.type==="custom"? 
                <Controller
                 name= {key}
                defaultValue={formValue?.[key]}
                rules= {field.validation}
                control={control}
                render={(props, customProps) => (
                  <field.customComponent
                    selectLocality={(d) => {
                      props.onChange(d);
                    }}
                    tenantId={tenantId}
                    selected={formValue?.[key]}
                    {...field.customCompProps}
                  />
                )}
                />
            :field?.type === "number"?
            <div>
            <MobileNumber
              name="mobileNumber"
              inputRef={register({
                value: getValues(key),
                shouldUnregister: true,
                ...validation,
              })}
              type="number"
              componentInFront={<div className="employee-card-input employee-card-input--front">+91</div>}
              //maxlength={10}
        />
        </div>
        :
            <TextInput
                  name={key}
                  type={field?.type}
                  inputRef={register({
                    value: getValues(key),
                    shouldUnregister: true,
                    ...validation,
                  })}
                />}
                <CardLabelError style={{ marginTop: "-10px", marginBottom: "-10px" }}>{t(formState?.errors?.[key]?.message)}</CardLabelError>
              </SearchField>
            );
          })}

       <div className="pt-search-action" >
         <SearchField  className="pt-search-action-reset">
         <p style={{color:"#0f4f9e"}}
            onClick={() => {
              onReset({});
            }}
          >
            {t(`ES_COMMON_CLEAR_ALL`)}
          </p>
           </SearchField>
       <SearchField className="pt-search-action-submit">
          <SubmitBar label={t("ES_COMMON_SEARCH")} submit />
        </SearchField>
       </div>
      </SearchForm>
    </div>
  );
};

export default SearchPTID;
