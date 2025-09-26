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
  ActionBar,
  SubmitBar,
  LabelFieldPair,
} from "@demodigit/digit-ui-react-components";
import  TextInput from "../../../../react-components/src/atoms/TextInput"
import { useTranslation } from "react-i18next";

export const FormComposer = (props) => {
  const { register, handleSubmit, errors, control,
    setValue,
    getValues,
    reset,
    watch,
 
    setError,
    clearErrors,
 
    formState,
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
    console.log("texttexttext",type,)
    switch (type) {
      case "text":
        return (
          <div className="field-container">
            {populators.componentInFront ? populators.componentInFront : null}
            <TextInput className="field desktop-w-full" {...populators} inputRef={register(populators.validation)} value={value} style={{ marginRight:populators.componentInFront ? "32px" : "0px"}}/>
          </div>
        );
      case "textarea":
        return <TextArea className="field desktop-w-full" value={value} name={populators.name || ""} {...populators} inputRef={register(populators.validation)} />;
      case "component":
        {
          
          if(props?.config?.[1]?.body?.[2]?.isProperty)
          {
            return (
              <Controller
                render={(props) => (
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
                    props={props}
                    setError={setError}
                    clearErrors={clearErrors}
                    formState={formState}
                    onBlur={props.onBlur}
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
<div className="section-header-wrapper">
  <CardSectionHeader>{section.head}</CardSectionHeader>
  <div className="section-header-line"></div>
</div>

            
            <style>
              {
                `
                .section-header-wrapper {
                  margin-bottom: 20px;
                }
                
                .section-header-line {
                  width: 100%;
                  height: 2px;
                  background: #e0e0e0; /* subtle grey line */
                  border-radius: 2px;
                  margin-top: 6px;
                }
                
                .section-header-wrapper .card-section-header {
                  font-size: 24px;
                  color: #0255ac !important;
                  font-weight: 600;
                  color: #333;
                }
                
                .form-section-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr); /* 2 columns */
  gap: 16px; /* spacing between fields */
}

.form-field-wrapper {
  width: 100%;
}

@media (max-width: 768px) {
  .form-section-grid {
    grid-template-columns: 1fr; /* 1 column on mobile */
  }
}

                
                `
              }
            </style>
            <div className="form-section-grid">
              
            {section.body.map((field, index) => {
  return (
    <div className="form-field-wrapper" key={index}>
      {errors[field?.populators?.name] && (
        <CardLabelError>{field.populators.error}</CardLabelError>
      )}
      <LabelFieldPair>
        <CardLabel>
          {field.label}
          {field.isMandatory ? " * " : null}
        </CardLabel>
        <div className="field">
          {fieldSelector(field.type, field.populators, field?.component, field.value, field)}
        </div>
      </LabelFieldPair>
    </div>
  );
})}

            </div>
            {array.length - 1 === index ? null : <BreakLine />}
          </React.Fragment>
        );
      }),
    [props.config, errors]
  );

  const isDisabled = props.isDisabled || false;
  return (
<form onSubmit={handleSubmit(onSubmit)} onChange={(e)=> changeValue(e.target.name,e.target.value)}>
  <Card className="styled-form">
    {/* <CardSubHeader >
      {props.heading}
    </CardSubHeader> */}

    <div className="form-sections">
      {formFields}
    </div>

    {props.children}

    <ActionBar >
      <SubmitBar disabled={isDisabled} label={t(props.label)} submit="submit" />
    </ActionBar>
  </Card>

  <style>
    {`
    .break-line {
      --border-opacity: 1;
      border-color: #ffffff;
      margin-top: 0px !important;
      margin-bottom: 0px !important;
  }
      .styled-form {
        background: #fff;
        border-radius: 16px;
        padding: 24px;
        box-shadow: 0 4px 12px rgba(0,0,0,0.05);
      }
      .card .card-label, .card-emp .card-label {
        font-size: 16px;
        line-height: 23px;
        --text-opacity: 1;
        color: #0056ac !important;
        font-weight: 500;
        margin-bottom: 8px;
    }

      .form-header {
        font-size: 20px;
        font-weight: 600;
        color: #333;
        border-bottom: 1px solid #eee;
        padding-bottom: 12px;
        margin-bottom: 24px;
        display: flex;
        align-items: center;
        gap: 8px;
      }

      .form-header::before {
        content: "📝";
        font-size: 20px;
      }

      .form-section-grid {
        display: grid;
        grid-template-columns: repeat(2, 1fr);
        gap: 20px;
        margin-bottom: 32px;
      }

      .form-field-wrapper {
        display: flex;
        flex-direction: column;
      }

      .form-field-wrapper label {
        font-weight: 500;
        margin-bottom: 6px;
        color: #444;
      }




      .error-text {
        color: #d9534f;
        font-size: 12px;
        margin-top: 4px;
      }

      @media (max-width: 768px) {
        .form-section-grid {
          grid-template-columns: 1fr;
        }
      }

      .sticky-action-bar {
        position: sticky;
        bottom: 0;
        background: #fff;
        border-top: 1px solid #eee;
        padding: 16px;
        display: flex;
        justify-content: flex-end;
        z-index: 10;
      }
    `}
  </style>
</form>

  );
};