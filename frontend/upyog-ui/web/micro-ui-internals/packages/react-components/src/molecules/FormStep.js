import React from "react";
import { useForm } from "react-hook-form";
import PropTypes from "prop-types";
import TextArea from "../atoms/TextArea";
import CardLabel from "../atoms/CardLabel";
import CardLabelError from "../atoms/CardLabelError";
import TextInput from "../atoms/TextInput";
import InputCard from "./InputCard";

const FormStep = ({
  t,
  children,
  config,
  onSelect,
  onSkip,
  value,
  onChange,
  isDisabled,
  _defaultValues = {},
  forcedError,
  componentInFront,
  onAdd,
  cardStyle = {},
  isMultipleAllow = false,
  showErrorBelowChildren = false,
  childrenAtTheBottom = true,
  textInputStyle,
  isMandatory
}) => {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm({
    defaultValues: _defaultValues,
  });
// console.log("_defaultValues", _defaultValues);
  const goNext = (data) => {
    if (isDisable) return;
      onSelect(data); 
  };

  const isDisable =
    isDisabled ? true : config.canDisable && Object.keys(errors).length;

  const inputs = config.inputs?.map((input, index) => {
    if (input.type === "text") {
      const registeredField =
      input?.name
        ? register(input.name, input.validation)
        : {};
    
    const {
      ref,
      onChange: rhfOnChange = () => {},
      ...registerRest
    } = registeredField || {};      return (
        <React.Fragment key={index}>
          <CardLabel>{t(input.label)}</CardLabel>
          {errors[input.name] && (
            <CardLabelError>{t(input.error)}</CardLabelError>
          )}
          <div className="field-container" style={{ justifyContent: "left" }}>
            {componentInFront && (
              <span className="citizen-card-input citizen-card-input--front">
                {componentInFront}
              </span>
            )}
            <TextInput
              {...registerRest}
              inputRef={ref}
              placeholder={input.placeholder}
              value={value}
              onChange={(e) => {
                rhfOnChange(e);
                onChange && onChange(e);
              }}
              isMandatory={!!errors[input.name]}
              disable={input.disable || false}
              textInputStyle={textInputStyle}
            />
          </div>
        </React.Fragment>
      );
    }

    if (input.type === "textarea") {
      return (
        <React.Fragment key={index}>
          <CardLabel>{t(input.label)}</CardLabel>
          <TextArea
            {...register(input.name, input.validation)}
            value={value}
            onChange={onChange}
            maxLength="1024"
          />
        </React.Fragment>
      );
    }

    if (input.type === "date") {
      const registeredField =
      input?.name
        ? register(input.name, input.validation)
        : {};
    
    const {
      ref,
      onChange: rhfOnChange = () => {},
      ...registerRest
    } = registeredField || {};      return (
        <React.Fragment key={index}>
          <CardLabel>
            {t(input.label)} {input.labelChildren && input.labelChildren}
          </CardLabel>
          {errors[input.name] && (
            <CardLabelError>{t(input.error)}</CardLabelError>
          )}
          <div className="field-container" style={{ justifyContent: "left" }}>
            <TextInput
              {...registerRest}
              inputRef={ref}
              value={value}
              onChange={(e) => {
                rhfOnChange(e);
                onChange && onChange(e);
              }}
              isMandatory={!!errors[input.name]}
              disable={input.disable || false}
              textInputStyle={textInputStyle}
              type="date"
            />
          </div>
        </React.Fragment>
      );
    }

    return null;
  });

  const { key, ...inputCardProps } = config;

  return (
    <form onSubmit={handleSubmit(goNext)}>
      <InputCard
        key={key}
        {...{
          isDisable,
          isMultipleAllow,
          isMandatory,
        }}
        {...inputCardProps}
        cardStyle={cardStyle}
        submit
        {...{ onSkip, onAdd }}
        t={t}
      >
        {!childrenAtTheBottom && children}
        {inputs}
        {forcedError && !showErrorBelowChildren && (
          <CardLabelError>{t(forcedError)}</CardLabelError>
        )}
        {childrenAtTheBottom && children}
        {forcedError && showErrorBelowChildren && (
          <CardLabelError>{t(forcedError)}</CardLabelError>
        )}
      </InputCard>
    </form>
  );
};

FormStep.propTypes = {
  config: PropTypes.shape({}),
  onSelect: PropTypes.func,
  onSkip: PropTypes.func,
  onAdd: PropTypes.func,
  t: PropTypes.func,
};

FormStep.defaultProps = {
  config: {},
  onSelect: undefined,
  onSkip: undefined,
  onAdd: undefined,
  t: (value) => value,
};

export default FormStep;