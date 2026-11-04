import React, { useMemo, useEffect } from "react";
import { useForm, Controller } from "react-hook-form";
import {
  BreakLine,
  Card,
  CardLabel,
  CardLabelError,
  CardSubHeader,
  CardSectionHeader,
  TextArea,
  TextInput,
  ActionBar,
  SubmitBar,
  LabelFieldPair,
} from "@nudmcdgnpm/digit-ui-react-components";
import { useTranslation } from "react-i18next";

export const FormComposer = (props) => {
  const { register, handleSubmit, control,
    setValue,
    getValues,
    reset,
    watch,
    setError,
    clearErrors,
    formState,
    formState: { errors },
   } = useForm();
  const { t } = useTranslation();
  const formData = watch();
  function onSubmit(data) {
    props.onSubmit(data);
  }
  const changeValue=(name,data)=>{
    sessionStorage.setItem(name,data)
  }
  useEffect(() => {
    props.getFormAccessors && props.getFormAccessors({ setValue, getValues });
  }, []);
  const fieldSelector = (type, populators,component,value,config) => {
  
    const Component = typeof component === "string" ? Digit.ComponentRegistryService.getComponent(component) : component;
    switch (type) {
      case "text":
        const registerPropsText = register(populators.name, populators.validation);
        return (
          <div className="field-container">
            {populators.componentInFront ? populators.componentInFront : null}
            <TextInput
              className="field desktop-w-full"
              {...populators}
              inputRef={registerPropsText.ref}
              {...registerPropsText}
              onChange={(e) => {
                registerPropsText.onChange(e);
                if (populators.onChange) {
                  populators.onChange(e);
                }
              }}
              value={value}
            />
          </div>
        );
      case "textarea":
        const registerPropsTextArea = register(populators.name || "", populators.validation);
        return (
          <TextArea
            className="field desktop-w-full"
            value={value}
            name={populators.name || ""}
            {...populators}
            inputRef={registerPropsTextArea.ref}
            {...registerPropsTextArea}
            onChange={(e) => {
              registerPropsTextArea.onChange(e);
              if (populators.onChange) {
                populators.onChange(e);
              }
            }}
          />
        );
      case "component":
        {
          
          if(props?.config?.[1]?.body?.[2]?.isProperty)
          {
            return (
              <Controller
                render={({ field }) => (
                  <Component
                    userType={"employee"}
                    t={t}
                    setValue={setValue}
                    onSelect={setValue}
                    config={config}
                    data={formData}
                    formData={formData}
                    register={register}
                    errors={errors}
                    props={field}
                    setError={setError}
                    clearErrors={clearErrors}
                    formState={formState}
                    onBlur={field.onBlur}
                  />
                )}
                name={config.key}
                control={control}
              />
            );
          }
        }
       

        default:
        return populators?.dependency !== false ? populators : null;
    }
  };

  const formFields = useMemo(
    () =>
      props.config?.map((section, index, array) => {
        return (
          <React.Fragment key={index}>
            <CardSectionHeader>{section.head}</CardSectionHeader>
            {section.body.map((field, index) => {
              return (
                <React.Fragment key={index}>
                  {errors[field?.populators?.name] && (field.populators?.validate ? errors[field.populators.validate] : true) && (
                    <CardLabelError>{field.populators.error}</CardLabelError>
                  )}
                  <LabelFieldPair>
                    <CardLabel>
                      {field.label}
                      {field.isMandatory ? <span className="check-page-link-button"> *</span> : null}
                    </CardLabel>
                    <div className="field">{fieldSelector(field.type, field.populators,field?.component,field.value, field)}</div>
                  </LabelFieldPair>
                </React.Fragment>
              );
            })}
            {array.length - 1 === index ? null : <BreakLine />}
          </React.Fragment>
        );
      }),
    [props.config, errors]
  );

  const isDisabled = props.isDisabled || false;
  return (
    <form onSubmit={handleSubmit(onSubmit)}  onChange={(e)=> changeValue(e.target.name,e.target.value)}>
      <Card>
        <CardSubHeader>{props.heading}</CardSubHeader>
        {formFields}
        {props.children}
        <ActionBar>
          <SubmitBar disabled={isDisabled} label={t(props.label)} submit="submit" />
        </ActionBar>
      </Card>
    </form>
  );
};