import React, { useEffect, useMemo, useState, Fragment } from "react";
import { useForm, Controller } from "react-hook-form";
import BreakLine from "../atoms/BreakLine";
import Card from "../atoms/Card";
import CardLabel from "../atoms/CardLabel";
import CardText from "../atoms/CardText";
// import CardLabelError from "../atoms/CardLabelError";
import CardSubHeader from "../atoms/CardSubHeader";
import CardSectionHeader from "../atoms/CardSectionHeader";
import CardLabelDesc from "../atoms/CardLabelDesc";
import CardLabelError from "../atoms/CardLabelError";
import TextArea from "../atoms/TextArea";
import TextInput from "../atoms/TextInput";
import ActionBar from "../atoms/ActionBar";
import SubmitBar from "../atoms/SubmitBar";
import LabelFieldPair from "../atoms/LabelFieldPair";
import LinkButton from "../atoms/LinkButton";

import { useTranslation } from "react-i18next";
import MobileNumber from "../atoms/MobileNumber";
import _ from "lodash";

const FormComposer = (props) => {
  const {
    register,
    handleSubmit,
    setValue,
    getValues,
    reset,
    watch,
    trigger,
    control,
    formState,
    formState: { errors },
    setError,
    clearErrors,
    unregister,
  } = useForm({
    defaultValues: props.defaultValues,
  });
  const { t } = useTranslation();
  const formData = watch();
  const navigate = Digit.Hooks.useCustomNavigate();

  useEffect(() => {
    const iseyeIconClicked = sessionStorage.getItem("eyeIconClicked");
    if (props?.appData && !(props?.appData?.ConnectionHolderDetails?.[0]?.sameAsOwnerDetails) && iseyeIconClicked && Object.keys(props?.appData)?.length > 0 && (!(_.isEqual(props?.appData?.ConnectionHolderDetails?.[0], formData?.ConnectionHolderDetails?.[0])))) {
      reset({ ...props?.appData });
    }
  }, [props?.appData, formData, props?.appData?.ConnectionHolderDetails]);

  useEffect(() => {
    props.getFormAccessors && props.getFormAccessors({ setValue, getValues });
  }, []);

  function onSubmit(data) {
    props.onSubmit(data);
  }

  function onSecondayActionClick(data) {
    props.onSecondayActionClick();
  }

  useEffect(() => {
    props.onFormValueChange && props.onFormValueChange(setValue, formData, formState);
  }, [formData]);

  const fieldSelector = (type, populators, isMandatory, disable = false, component, config) => {
    const Component = typeof component === "string" ? Digit.ComponentRegistryService.getComponent(component) : component;

    switch (type) {
      case "text":
      case "date":
      case "number":
      case "password":
      case "time":
        // if (populators.defaultValue) setTimeout(setValue(populators?.name, populators.defaultValue));
        return (
          <div className={!populators?.customProps?.classes ? "field-container" : `field-container ${populators.customProps.classes}`}>
            {populators?.componentInFront ? (
              <span className={`component-in-front ${disable && "disabled"}`}>{populators.componentInFront}</span>
            ) : null}
            {(() => {
              const { ref = {}, ...rest } = register(populators.name, populators.validation) || {};
              return (
                <TextInput
                  className="field"
                  {...populators}
                  {...rest}
                  inputRef={ref}
                  isRequired={isMandatory}
                  type={type}
                  disable={disable}
                  watch={watch}
                />
              );
            })()}
          </div>
        );
      case "textarea":
        // if (populators.defaultValue) setTimeout(setValue(populators?.name, populators.defaultValue));
        return (
          <Controller
            name={populators?.name || ""}
            control={control}
            defaultValue={populators?.defaultValue || ""}
            rules={populators?.validation}
            render={({ field }) => (
              <TextArea
                className="field"
                {...populators}
                {...field}
                inputRef={field.ref}
                disable={disable}
              />
            )}
          />);
      case "mobileNumber":
        return (
          <Controller
            render={({ field }) => (
              <MobileNumber className={populators?.className || "field"} onChange={field.onChange} value={field.value} disable={disable} />
            )}
            defaultValue={populators.defaultValue}
            name={populators?.name}
            control={control}
          />
        );
      case "custom":
        return (
          <Controller
            render={({ field }) => populators.component({ ...field, setValue }, populators.customProps)}
            defaultValue={populators.defaultValue}
            name={populators?.name}
            control={control}
          />
        );
      case "component":
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

      case "form":
        return (
          <form>
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
              setError={setError}
              clearErrors={clearErrors}
              formState={formState}
              control={control}
            />
          </form>
        );
      default:
        return populators?.dependency !== false ? populators : null;
    }
  };

  const getCombinedStyle = (placementinBox) => {
    switch (placementinBox) {
      case 0:
        return {
          border: "solid",
          borderRadius: "5px",
          padding: "10px",
          paddingTop: "20px",
          marginTop: "10px",
          borderColor: "#f3f3f3",
          background: "#FAFAFA",
          marginBottom: "20px",
        };
      case 1:
        return {
          border: "solid",
          borderRadius: "5px",
          padding: "10px",
          paddingTop: "20px",
          marginTop: "-30px",
          borderColor: "#f3f3f3",
          background: "#FAFAFA",
          borderTop: "0px",
          borderBottom: "0px",
        };
      case 2:
        return {
          border: "solid",
          borderRadius: "5px",
          padding: "10px",
          paddingTop: "20px",
          marginTop: "-30px",
          borderColor: "#f3f3f3",
          background: "#FAFAFA",
          marginBottom: "20px",
          borderTop: "0px",
        };
    }
  };

  const isEmployeeLoginCard = props?.cardClassName === "loginFormStyleEmployee";

  const pathname = typeof window !== "undefined" ? window.location.pathname || "" : "";
  const search = typeof window !== "undefined" ? window.location.search || "" : "";

  const maskEmployeeMobileNumber = (mobileNumber) => {
    const cleanedMobileNumber = (mobileNumber || "").replace(/\D/g, "");
    if (!cleanedMobileNumber) return "XXXXXX0000";
    const maskedLastFour = cleanedMobileNumber.slice(-4).padStart(4, "0");
    return `XXXXXX${maskedLastFour}`;
  };

  const getEmployeeHeaderContent = () => {
    if (pathname.includes("employee/user/forgot-password")) {
      return {
        header: "Forgot Password?",
        subHeader: "All the communications regarding the application will be sent to this mobile number.",
      };
    }

    if (pathname.includes("employee/user/change-password")) {
      const mobileNumber = new URLSearchParams(search).get("mobile_number");
      return {
        header: "Reset Password",
        subHeader: `Enter the OTP sent to ${maskEmployeeMobileNumber(mobileNumber)}`,
      };
    }

    return {
      header: "Login to UPYOG",
      subHeader: "Use your registered details to continue.",
    };
  };

  const getCombinedComponent = (section) => {
    if (isEmployeeLoginCard) {
      const { header, subHeader } = getEmployeeHeaderContent();
      return (
        <div className="login-form-header">
          <h2>{header}</h2>
          <p>{subHeader}</p>
        </div>
      );
    }
  };

  const formFields = useMemo(
    () =>
      props.config?.map((section, index, array) => {
        return (
          <React.Fragment key={index}>
            {section && getCombinedComponent(section)}
            {section.body.map((field, index) => {
              if (props.inline)
                return (
                  <React.Fragment key={index}>
                    <div style={field.isInsideBox ? getCombinedStyle(field?.placementinbox) : {}}>
                      {!field.withoutLabel && (
                        <CardLabel
                          className={`${field.isSectionText ? "card-label--section" : ""} ${props.inline ? "card-label--inline-14" : ""} ${field?.disable ? "disabled" : ""}`}
                        >
                          {t(field.label)}
                          {field.isMandatory ? " * " : null}
                          {field.labelChildren && field.labelChildren}
                        </CardLabel>
                      )}
                      {errors && errors[field.populators?.name] && Object.keys(errors[field.populators?.name]).length ? (
                        <CardLabelError>{t(field.populators.error || errors[field.populators?.name]?.message)}</CardLabelError>
                      ) : null}
                      <div className={`field ${field.withoutLabel ? "w-full" : ""}`}>
                        {fieldSelector(field.type, field.populators, field.isMandatory, field?.disable, field?.component, field)}
                        {field?.description && (
                          <CardLabel
                            className="form-composer-field-desc-label"
                          >
                            {t(field.description)}
                          </CardLabel>
                        )}
                      </div>
                    </div>
                  </React.Fragment>
                );
              return (
                <Fragment key={field?.populators?.name || field?.label || index}>
                  <LabelFieldPair>
                    {!field.withoutLabel && (
                      <CardLabel className={`${field.isSectionText ? "card-label--section" : ""} ${props.inline ? "card-label--inline-8" : ""}`}>
                        {t(field.label)}
                        {field.isMandatory ? " * " : null}
                        {field.labelChildren && field.labelChildren}
                      </CardLabel>
                    )}
                    <div className={`field ${field.withoutLabel ? "w-full" : ""}`}>
                      {fieldSelector(field.type, field.populators, field.isMandatory, field?.disable, field?.component, field)}
                      {field?.description && <CardText className="form-composer-desc-text">{t(field?.description)}</CardText>}
                    </div>
                  </LabelFieldPair>
                  {field?.populators?.name && errors && errors[field?.populators?.name] && Object.keys(errors[field?.populators?.name]).length ? (
                    <CardLabelError className="form-composer-error-offset">
                      {t(field?.populators?.error)}
                    </CardLabelError>
                  ) : null}
                </Fragment>
              );
            })}
            {!props.noBreakLine && (array.length - 1 === index ? null : <BreakLine />)}
          </React.Fragment>
        );
      }),
    [props.config, formData]
  );

  const getCardStyles = () => {
    let styles = props.cardStyle || {};
    if (props.noBoxShadow) styles = { ...styles, boxShadow: "none" };
    return styles;
  };

  const isDisabled = props.isDisabled || false;
  const checkKeyDown = (e) => {
    const keyCode = e.keyCode ? e.keyCode : e.key ? e.key : e.which;
    if (keyCode === 13) {
      e.preventDefault();
    }
  };
  return (
    <form onSubmit={handleSubmit(onSubmit)} onKeyDown={(e) => checkKeyDown(e)} id={props.formId} className={props.className}>
      <Card className={props?.cardClassName ? props.cardClassName : ""}>
        {!props.childrenAtTheBottom && props.children}
        {!isEmployeeLoginCard && props.heading && <CardSubHeader className="form-composer-heading"> {props.heading} </CardSubHeader>}
        {/* {props.description && <CardLabelDesc className={"repos"}> {props.description} </CardLabelDesc>} */}
        {props.text && <CardText>{props.text}</CardText>}
        {formFields}
        {props.childrenAtTheBottom && props.children}
        {props.submitInForm && (
          <SubmitBar label={t(props.label)} submit="submit" disabled={isDisabled} className="w-full text-white" />
        )}
        {props.secondaryActionLabel && (
          <div className="primary-label-btn primary-label-btn--form" onClick={onSecondayActionClick}>
            {props.secondaryActionLabel}
          </div>
        )}
        {!props.submitInForm && props.label && (
          <ActionBar>
            <SubmitBar label={t(props.label)} submit="submit" disabled={isDisabled} />
            {props.onSkip && props.showSkip && <LinkButton style={props?.skipStyle} label={t(`CS_SKIP_CONTINUE`)} onClick={props.onSkip} />}
          </ActionBar>
        )}
      </Card>
    </form>
  );
};
export default FormComposer;