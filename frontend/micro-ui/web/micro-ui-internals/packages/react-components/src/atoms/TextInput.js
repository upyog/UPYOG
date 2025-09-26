import React, { useEffect, useState } from "react";
import PropTypes from "prop-types";

const TextInput = (props) => {
  const user_type = Digit.SessionStorage.get("userType");
  const [date, setDate] = useState();
  const data = props?.watch
    ? {
        fromDate: props?.watch("fromDate"),
        toDate: props?.watch("toDate"),
      }
    : {};

  const handleDate = (event) => {
    const { value } = event.target;
    setDate(getDDMMYYYY(value));
  };

  return (
    <React.Fragment>
<style>
  {`
  /* Wrapper */
  .text-input {
    display: flex;
    flex-direction: column;
    margin-bottom: 1rem;
    width: 100%;
    position: relative;
  }

  /* Input fields */
  .text-input input {
    width: 100%;
    padding: 10px 14px; /* same as dropdown */
    height: 42px;       /* 👈 fixed height, matches dropdown input */
    border: 1px solid #d0d5dd;
    border-radius: 8px;
    font-size: 0.95rem;
    line-height: 1.4;   /* text vertical centering */
    color: #333;
    background-color: #fff;
    outline: none;
    transition: all 0.3s ease;
    box-shadow: inset 0 1px 2px rgba(0,0,0,0.05);
    position: relative;
    display: block;
    width: 100%;
    height: 2.5rem !important;
    --border-opacity: 1;
    border: 1px solid #464646;
    border-color: rgba(70, 70, 70, var(--border-opacity));
  }

  /* Hover effect */
  .text-input input:hover {
    border-color: #4096ff;
    box-shadow: 0 0 4px rgba(64,150,255,0.3);
  }

  /* Focus effect */
  .text-input input:focus {
    border-color: #1677ff;
    box-shadow: 0 0 6px rgba(22,119,255,0.4);
    background-color: #f9fcff;
  }

  /* Disabled state */
  .text-input input.disabled,
  .text-input input:disabled {
    background-color: #f5f5f5;
    border-color: #e0e0e0;
    color: #999;
    cursor: not-allowed;
  }

  /* Error style */
  .employee-card-input-error,
  .citizen-card-input-error {
    border: 1px solid #ff4d4f !important;
    background-color: #fff1f0 !important;
    color: #a8071a;
  }

  /* Date input style */
  .card-date-input {
    background: #fafafa;
    border-radius: 6px;
    padding: 8px 12px;
    height: 42px;  /* 👈 consistent with text input */
    font-size: 0.9rem;
    text-align: center;
    border: 1px solid #d9d9d9;
    cursor: default;
  }

  /* Signature image */
  .text-input img {
    margin-top: 0.5rem;
    max-width: 120px;
    height: auto;
    border-radius: 6px;
    border: 1px solid #eee;
    box-shadow: 0 2px 6px rgba(0,0,0,0.1);
  }
  `}
</style>

      <div className={`text-input ${user_type === "employee" ? "" :"text-input-width"} ${props.className}`} style={props?.textInputStyle ? { ...props.textInputStyle} : {}}>
        {props.isMandatory ? (
          <input
            type={props?.validation && props.ValidationRequired ? props?.validation?.type : (props.type || "text")}
            name={props.name}
            id={props.id}
            className={`${user_type ? "employee-card-input-error" : "card-input-error"} ${props.disable && "disabled"}`}
            placeholder={props.placeholder}
            onChange={(event) => {
              if (props?.onChange) {
                props?.onChange(event);
              }
              if (props.type === "date") {
                handleDate(event);
              }
            }}
            ref={props.inputRef}
            value={props.value}
            style={{ ...props.style }}
            defaultValue={props.defaultValue}
            minLength={props.minlength}
            maxLength={props.maxlength}
            max={props.max}
            pattern={props?.validation && props.ValidationRequired ? props?.validation?.pattern : props.pattern}
            min={props.min}
            readOnly={props.disable}
            title={props?.validation && props.ValidationRequired ? props?.validation?.title :props.title}
            step={props.step}
            autoFocus={props.autoFocus}
            onBlur={props.onBlur}
            autoComplete="off"
            disabled={props.disabled}
          />
        ) : (
          <input
            type={props?.validation && props.ValidationRequired ? props?.validation?.type : (props.type || "text")}
            name={props.name}
            id={props.id}
            className={`${user_type ? "employee-card-input" : "citizen-card-input"} ${props.disable && "disabled"} focus-visible ${props.errorStyle && "employee-card-input-error"}`}
            placeholder={props.placeholder}
            onChange={(event) => {
              if (props?.onChange) {
                props?.onChange(event);
              }
              if (props.type === "date") {
                handleDate(event);
              }
            }}
            ref={props.inputRef}
            value={props.value}
            style={{ ...props.style }}
            defaultValue={props.defaultValue}
            minLength={props.minlength}
            maxLength={props.maxlength}
            max={props.max}
            required={props?.validation && props.ValidationRequired ? props?.validation?.isRequired :props.isRequired || (props.type === "date" && (props.name === "fromDate" ? data.toDate : data.fromDate))}
            pattern={props?.validation && props.ValidationRequired ? props?.validation?.pattern : props.pattern}
            min={props.min}
            readOnly={props.disable}
            title={props?.validation && props.ValidationRequired ? props?.validation?.title :props.title}
            step={props.step}
            autoFocus={props.autoFocus}
            onBlur={props.onBlur}
            onKeyPress={props.onKeyPress}
            autoComplete="off"
            disabled={props.disabled}
          />
        )}
        {/* {props.type === "date" && <DatePicker {...props} date={date} setDate={setDate} data={data} />} */}
        {props.signature ? props.signatureImg : null}

      </div>
    </React.Fragment>
  );
};

TextInput.propTypes = {
  userType: PropTypes.string,
  isMandatory: PropTypes.bool,
  name: PropTypes.string,
  placeholder: PropTypes.string,
  onChange: PropTypes.func,
  ref: PropTypes.func,
  value: PropTypes.any,
};

TextInput.defaultProps = {
  isMandatory: false,
};

function DatePicker(props) {
  useEffect(() => {
    if (props?.shouldUpdate) {
      props?.setDate(getDDMMYYYY(props?.data[props.name], "yyyymmdd"));
    }
  }, [props?.data]);

  useEffect(() => {
    props.setDate(getDDMMYYYY(props?.defaultValue));
  }, []);

  return (
    <input
      type="text"
      className={`${props.disable && "disabled"} card-date-input`}
      name={props.name}
      id={props.id}
      placeholder={props.placeholder}
      defaultValue={props.date}
      readOnly={true}
    />
  );
}

function getDDMMYYYY(date) {
  if (!date) return "";

  return new Date(date).toLocaleString("en-In").split(",")[0];
}

export default TextInput;
