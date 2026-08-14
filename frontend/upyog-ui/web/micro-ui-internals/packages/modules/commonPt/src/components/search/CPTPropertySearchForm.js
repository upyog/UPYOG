import { CardLabelError, SearchField, SearchForm, SubmitBar, TextInput, Localities } from "@nudmcdgnpm/digit-ui-react-components";
import React, { useState, useEffect } from "react";
import { Controller, useForm } from "react-hook-form";


/**
 * Component responsible for switching between different search modes.
 *
 * Each search mode can have different fields. When switching the mode,
 * the previous form values are reset to avoid submitting values from a
 * different search type.
 */
const SwitchComponent = (props) => {
  return (
    <div className="w-fullwidth PropertySearchFormSwitcher">
      {props.keys.map((key) => (
        <span key={key} className={props.searchBy === key ? "selected" : "non-selected"} onClick={() => { key === "searchDetail" && !(sessionStorage.getItem("searchDetailValue")) ? sessionStorage.setItem("searchDetailValue", 1) : ""; key === "searchId" && sessionStorage.getItem("searchDetailValue") == 1 ? sessionStorage.setItem("searchDetailValue", 2) : ""; props.onSwitch(key); props.onReset(); }}>
          {props.t(`PT_SEARCH_BY_${key?.toUpperCase()}`)}
        </span>
      ))}
    </div>
  );
};
const SearchPTID = ({ tenantId, t, onSubmit, onReset, searchBy, PTSearchFields, setSearchBy, payload }) => {
  const navigate = Digit.Hooks.useCustomNavigate();
  const { register, control, handleSubmit, setValue, watch, getValues, reset, formState } = useForm({
    defaultValues: {
      ...payload,
    },
    // Removes values of fields that are no longer mounted.
    // This is important because different search types render different fields.
    // Without unregistering, hidden fields from a previous search mode can
    // remain in the submitted payload.
    shouldUnregister: true
  });
  const formValue = watch();
  const fields = PTSearchFields?.[searchBy] || {};

  useEffect(() => {
    // Redirects employee back to employee home page after create flow.
    if (sessionStorage.getItem("isCreateEnabledEmployee") === "true") {
      sessionStorage.removeItem("isCreateEnabledEmployee");
      navigate("/upyog-ui/employee", { replace: true });
    }
    else
      sessionStorage.removeItem("isCreateEnabledEmployee");

  })

  return (
    <div className="PropertySearchForm">
      <SearchForm onSubmit={onSubmit} handleSubmit={handleSubmit}>
        <SwitchComponent keys={Object.keys(PTSearchFields || {})} searchBy={searchBy} onReset={onReset} t={t} onSwitch={setSearchBy} />
        {fields &&
          Object.keys(fields).map((key) => {
            let field = fields[key];
            let validation = field?.validation || {};
            return (
              <SearchField key={key}>
                <label>{t(field?.label)}{`${field?.validation?.required ? "*" : ""}`}</label>
                {field?.type === "custom" ?
                  // Controller is used for custom components because they
                  // do not directly expose a native input ref required by register.
                  <Controller
                    name={key}
                    defaultValue={formValue?.[key]}
                    rules={field.validation}
                    control={control}
                    render={({ field: controlField }) => {
                      const CustomComponent = field.customComponent;
                      return (
                        <CustomComponent
                          selectLocality={(d) => {
                            controlField.onChange(d);
                          }}
                          tenantId={tenantId}
                          selected={controlField.value}
                          {...field.customCompProps}
                        />
                      );
                    }}
                  />
                  :
                  <div className="field-container">
                    {field?.componentInFront ? (
                      <span className="employee-card-input employee-card-input--front" style={{ flex: "none" }}>
                        {field?.componentInFront}
                      </span>
                    ) : null}
                    {(() => {
                      const { ref, ...rest } = register(key, { ...validation });
                      return (
                        <TextInput
                          name={key}
                          type={field?.type}
                          inputRef={ref}
                          {...rest}
                        />
                      );
                    })()}
                  </div>}
                <CardLabelError style={{ marginTop: "-10px", marginBottom: "-10px" }}>{t(formState?.errors?.[key]?.message)}</CardLabelError>
              </SearchField>
            );
          })}

        <div className="pt-search-action" >
          <SearchField className="pt-search-action-reset">
            <p
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
