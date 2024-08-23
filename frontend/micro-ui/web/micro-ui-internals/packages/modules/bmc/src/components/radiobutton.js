// import React from "react";
// import PropTypes from "prop-types";
// import isEqual from "lodash/isEqual";
// import { useTranslation } from "react-i18next";

// const RadioButton = (props) => {
//   const { t } = useTranslation();
//   const selected = props.selectedOption;

//   function selectOption(value) {
//     props.onSelect(value);
//   }

//   return (
//     <div style={props.style} className={`bmc-radio-wrap ${props?.additionalWrapperClass}`}>
//       {props?.options?.map((option, ind) => {
//         const isChecked =
//           props?.optionsKey && !props?.isDependent
//             ? (props.isPTFlow && selected?.code === option.code) || isEqual(selected, option)
//             : props?.optionsKey && props?.isDependent
//             ? props?.isTLFlow
//               ? selected?.code === option.code || selected?.i18nKey === option.i18nKey
//               : selected?.code === option.code
//             : selected === option;

//         return (
//           <div style={props.innerStyles} key={ind}>
//             <span className="bmc-radio-btn-wrap">
//               <input
//                 className="bmc-radio-btn"
//                 type="radio"
//                 value={option}
//                 checked={isChecked}
//                 onChange={() => selectOption(option)}
//                 disabled={props.disabled}
//                 name={props.name}
//                 ref={props.inputRef}
//                 onClick={props.onClick}
//               />
//               <span className="bmc-radio-btn-checkmark"></span>
//             </span>
//             <label style={props.inputStyle}>
//               {props?.optionsKey && !props?.isDependent
//                 ? t(option[props.optionsKey])
//                 : props?.optionsKey && props?.isDependent
//                 ? t(props.labelKey ? `${props.labelKey}_${option.code}` : option.code)
//                 : t(option)}
//             </label>
//           </div>
//         );
//       })}
//     </div>
//   );
// };

// RadioButton.propTypes = {
//   selectedOption: PropTypes.any,
//   onSelect: PropTypes.func,
//   options: PropTypes.any,
//   optionsKey: PropTypes.string,
//   innerStyles: PropTypes.any,
//   style: PropTypes.any,
//   additionalWrapperClass: PropTypes.string,
//   isPTFlow: PropTypes.bool,
//   isDependent: PropTypes.bool,
//   isTLFlow: PropTypes.bool,
//   labelKey: PropTypes.string,
//   disabled: PropTypes.bool,
//   name: PropTypes.string,
//   inputRef: PropTypes.object,
//   inputStyle: PropTypes.object,
//   onClick: PropTypes.func
// };

// RadioButton.defaultProps = {};

// export default RadioButton;


import React from "react";
import PropTypes from "prop-types";
import isEqual from "lodash/isEqual";
import { useTranslation } from "react-i18next";

const RadioButton = (props) => {
  const { t } = useTranslation();
  var selected = props.selectedOption;

  function selectOption(value) {
    props.onSelect(value);
  }

  return (
    <div style={props.style} className={`bmc-radio-wrap ${props?.additionalWrapperClass}`}>
      {props?.options?.map((option, ind) => {
        const isChecked =
          props?.optionsKey && !props?.isDependent
            ? (props.isPTFlow && selected?.code === option.code) || isEqual(selected, option)
            : props?.optionsKey && props?.isDependent
            ? props?.isTLFlow
              ? selected?.code === option.code || selected?.i18nKey === option.i18nKey
              : selected?.code === option.code
            : selected === option;

        const isDisabled =
          Array.isArray(props.disabled) && props.disabled.includes(option)
            ? true
            : props.disabled;

        return (
          <div style={props.innerStyles} key={ind}>
            <span className="bmc-radio-btn-wrap">
              <input
                className="bmc-radio-btn"
                type="radio"
                value={option}
                checked={isChecked}
                onChange={() => selectOption(option)}
                disabled={isDisabled}
                name={props.name}
                ref={props.inputRef}
                onClick={props.onClick}
              />
              <span className="bmc-radio-btn-checkmark"></span>
            </span>
            <label style={props.inputStyle}>
              {props?.optionsKey && !props?.isDependent
                ? t(option[props.optionsKey])
                : props?.optionsKey && props?.isDependent
                ? t(props.labelKey ? `${props.labelKey}_${option.code}` : option.code)
                : t(option)}
            </label>
          </div>
        );
      })}
    </div>
  );
};

RadioButton.propTypes = {
  selectedOption: PropTypes.any,
  onSelect: PropTypes.func,
  options: PropTypes.any,
  optionsKey: PropTypes.string,
  innerStyles: PropTypes.any,
  style: PropTypes.any,
  additionalWrapperClass: PropTypes.string,
  isPTFlow: PropTypes.bool,
  isDependent: PropTypes.bool,
  isTLFlow: PropTypes.bool,
  labelKey: PropTypes.string,
  disabled: PropTypes.oneOfType([PropTypes.bool, PropTypes.array]),
  name: PropTypes.string,
  inputRef: PropTypes.object,
  inputStyle: PropTypes.object,
  onClick: PropTypes.func
};

RadioButton.defaultProps = {};

export default RadioButton;
