import React, { useEffect, useState, useRef } from "react";
import PropTypes from "prop-types";
import isEqual from "lodash/isEqual";
import { useTranslation } from "react-i18next";

let radioGroupCounter = 0;

const RadioButtons = (props) => {
  const { t } = useTranslation();
  const defaultGroupNameRef = useRef(null);
  if (!defaultGroupNameRef.current) {
    radioGroupCounter += 1;
    defaultGroupNameRef.current = `radio-buttons-group-${radioGroupCounter}`;
  }
  var selected = props.selectedOption;
  function selectOption(value) {
    //selected = value;
    props.onSelect(value);
  }

  const groupName = props.name || defaultGroupNameRef.current;

  const isOptionChecked = (option) => {
    if (selected === undefined || selected === null) return false;
    if (props?.optionsKey) {
      if (selected?.code !== undefined && option?.code !== undefined) {
        if (String(selected.code) === String(option.code)) return true;
      }
      if (selected?.i18nKey !== undefined && option?.i18nKey !== undefined) {
        if (selected.i18nKey === option.i18nKey) return true;
      }
      if (props.optionsKey && selected?.[props.optionsKey] !== undefined && option?.[props.optionsKey] !== undefined) {
        if (selected[props.optionsKey] === option[props.optionsKey]) return true;
      }
      if (typeof selected === "string" || typeof selected === "number") {
        if (String(selected) === String(option.code) || String(selected) === String(option[props.optionsKey])) return true;
      }
      return isEqual(selected, option);
    }
    return selected === option || isEqual(selected, option);
  };

  return (
    <div style={props.style} className={`radio-wrap ${props?.additionalWrapperClass}`}>
      {props?.options?.map((option, ind) => {
        if (props?.optionsKey && !props?.isDependent) {
          const checked = isOptionChecked(option);
          return (
            <div style={props.innerStyles} key={ind}>
              <span className="radio-btn-wrap">
                <input
                  className="radio-btn"
                  type="radio"
                  value={option}
                  checked={Boolean(checked)}
                  onChange={() => selectOption(option)}
                  disabled={props?.disabled}
                  name={groupName}
                  ref={props.inputRef}
                />
                <span className="radio-btn-checkmark"></span>
              </span>
              <label style={props.inputStyle}>{t(option[props.optionsKey])}</label>
            </div>
          );
        } else if (props?.optionsKey && props?.isDependent) {
          const isDependentChecked = isOptionChecked(option);
          return (
            <div style={props.innerStyles} key={ind}>
              <span className="radio-btn-wrap">
                <input
                  className="radio-btn"
                  type="radio"
                  value={option}
                  checked={Boolean(isDependentChecked)}
                  onChange={() => selectOption(option)}
                  disabled={props?.disabled}
                  name={groupName}
                  ref={props.inputRef}
                />
                <span className="radio-btn-checkmark"></span>
              </span>
              <label style={props.inputStyle}>{t(props.labelKey ? `${props.labelKey}_${option.code}` : option.code)}</label>
            </div>
          );
        } else {
          const isSimpleChecked = selected === option || isEqual(selected, option);
          return (
            <div style={props.innerStyles} key={ind}>
              <span className="radio-btn-wrap">
                <input
                  className="radio-btn"
                  type="radio"
                  value={option}
                  checked={Boolean(isSimpleChecked)}
                  onChange={() => selectOption(option)}
                  disabled={props?.disabled}
                  name={groupName}
                  ref={props.inputRef}
                />
                <span className="radio-btn-checkmark"></span>
              </span>
              <label style={props.inputStyle}>{t(option)}</label>
            </div>
          );
        }
      })}
    </div>
  );
};

RadioButtons.propTypes = {
  selectedOption: PropTypes.any,
  onSelect: PropTypes.func,
  options: PropTypes.any,
  optionsKey: PropTypes.string,
  innerStyles: PropTypes.any,
  style: PropTypes.any,
};

RadioButtons.defaultProps = {};

export default RadioButtons;
