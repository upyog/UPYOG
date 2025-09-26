import PropTypes from "prop-types";
import React, { useEffect, useRef, useState } from "react";
import { ArrowDown } from "./svgindex";

const TextField = (props) => {
  const [value, setValue] = useState(props.selectedVal ? props.selectedVal : "");

  useEffect(() => {
    if (!props.keepNull)
      if( props.selectedVal)
        setValue(props.selectedVal)
      else
      { setValue(""); props.setFilter("") } 
    else setValue("");
  }, [props.selectedVal, props.forceSet]);

  function inputChange(e) {
    if (props.freeze) return;

    setValue(e.target.value);
    props.setFilter(e.target.value);
  }

  function broadcastToOpen() {
    if (!props.disable) {
      props.dropdownDisplay(true);
    }
  }

  function broadcastToClose() {
    props.dropdownDisplay(false);
  }

  /* Custom function to scroll and select in the dropdowns while using key up and down */
  const keyChange = (e) => {
    if (e.key == "ArrowDown") {
      props.setOptionIndex((state) => (state + 1 == props.addProps.length ? 0 : state + 1));
      if (props.addProps.currentIndex + 1 == props.addProps.length) {
        e?.target?.parentElement?.parentElement?.children?.namedItem("jk-dropdown-unique")?.scrollTo?.(0, 0);
      } else {
        props?.addProps?.currentIndex > 2 && e?.target?.parentElement?.parentElement?.children?.namedItem("jk-dropdown-unique")?.scrollBy?.(0, 45);
      }
      e.preventDefault();
    } else if (e.key == "ArrowUp") {
      props.setOptionIndex((state) => (state !== 0 ? state - 1 : props.addProps.length - 1));
      if (props.addProps.currentIndex == 0) {
        e?.target?.parentElement?.parentElement?.children?.namedItem("jk-dropdown-unique")?.scrollTo?.(100000, 100000);
      } else {
        props?.addProps?.currentIndex > 2 && e?.target?.parentElement?.parentElement?.children?.namedItem("jk-dropdown-unique")?.scrollBy?.(0, -45);
      }
      e.preventDefault();
    } else if (e.key == "Enter") {
      props.addProps.selectOption(props.addProps.currentIndex);
    }
  };

  return (
    <input
      ref={props.inputRef}
      className={`employee-select-wrap--elipses ${props.disable && "disabled"}`}
      type="text"
      value={value}
      onChange={inputChange}
      onClick={props.onClick}
      onFocus={broadcastToOpen}
      onBlur={(e) => {
        broadcastToClose();
        props?.onBlur?.(e);
        if (props.selectedVal !== props.filterVal) {
          setTimeout(() => {
            props.setforceSet((val) => val + 1);
          }, 1000);
        }
      }}
      onKeyDown={keyChange}
      readOnly={props.disable}
      autoFocus={props.autoFocus}
      placeholder={props.placeholder}
      autoComplete={"off"}
      style={{...props.style, zIndex: "auto"}}
    />
  );
};

const translateDummy = (text) => {
  return text;
};

const Dropdown = (props) => {
  const user_type = Digit.SessionStorage.get("userType");
  const [dropdownStatus, setDropdownStatus] = useState(false);
  const [selectedOption, setSelectedOption] = useState(props.selected ? props.selected : null);
  const [filterVal, setFilterVal] = useState("");
  const [forceSet, setforceSet] = useState(0);
  const [optionIndex, setOptionIndex] = useState(-1);
  const optionRef = useRef(null);
  const hasCustomSelector = props.customSelector ? true : false;
  const t = props.t || translateDummy;

  useEffect(() => {
    setSelectedOption(props.selected);
  }, [props.selected]);

  function dropdownSwitch() {
    if (!props.disable) {
      var current = dropdownStatus;
      if (!current) {
        document.addEventListener("mousedown", handleClick, false);
      }
      setDropdownStatus(!current);
      props?.onBlur?.();
    }
  }

  function handleClick(e) {
    if (!optionRef.current || !optionRef.current.contains(e.target)) {
      document.removeEventListener("mousedown", handleClick, false);
      setDropdownStatus(false);
    }
  }

  function dropdownOn(val) {
    const waitForOptions = () => setTimeout(() => setDropdownStatus(val), 500);
    const timerId = waitForOptions();
    return () => {
      clearTimeout(timerId);
    };
  }

  function onSelect(val) {
    if (val !== selectedOption || props.allowMultiselect) {
      props.select(val);
      setSelectedOption(val);
      setDropdownStatus(false);
    } else {
      setSelectedOption(val);
      setforceSet(forceSet + 1);
    }
  }

  function setFilter(val) {
    setFilterVal(val);
  }

  let filteredOption =
    (props.option && props.option?.filter((option) => t(option[props.optionKey])?.toUpperCase()?.indexOf(filterVal?.toUpperCase()) > -1)) || [];
  function selectOption(ind) {
    onSelect(filteredOption[ind]);
  }
  if (props?.option?.[0]?.label == "PropertyType") {
    filteredOption = props.option
  }
  if(props.isBPAREG && selectedOption)
  {
    let isSelectedSameAsOptions = props.option?.filter((ob) => ob?.code === selectedOption?.code)?.length > 0;
    if(!isSelectedSameAsOptions) setSelectedOption(null)
  }

  return (
<div
  className={`${user_type === "employee" ? "employee-select-wrap" : "select-wrap"} ${props?.className ? props?.className : ""}`}
  style={{ ...props.style }}
>
  <style>
      {

        `
        /* Wrapper */
.select,
.select-active {
  display: flex;
  align-items: center;
  justify-content: space-between;
  border: 1px solid #dcdfe6;
  border-radius: 8px;
  padding: 10px 12px;
  background: #fff;
  transition: all 0.2s ease-in-out;
  cursor: pointer;
}

.select-active {
  border-color: #007bff;
  box-shadow: 0 0 0 2px rgba(0,123,255,0.15);
}

/* Arrow icon */
.dropdown-arrow {
  margin-left: 8px;
  transition: transform 0.3s ease;
}
.select-active .dropdown-arrow {
  transform: rotate(180deg);
}

/* Input field */
.employee-select-wrap--elipses {
  flex: 1;
  border: none;
  outline: none;
  font-size: 14px;
  color: #333;
  background: transparent;
}
.employee-select-wrap--elipses::placeholder {
  color: #aaa;
}

/* Options card */
.options-card {
  margin-top: 6px;
  background: #fff;
  border: 1px solid #eee;
  border-radius: 8px;
  box-shadow: 0px 6px 18px rgba(0,0,0,0.08);
  max-height: 240px;
  overflow-y: auto;
  animation: fadeIn 0.2s ease;
  z-index: 1000;
}

/* Scrollbar styling */
.options-card::-webkit-scrollbar {
  width: 6px;
}
.options-card::-webkit-scrollbar-thumb {
  background: #ccc;
  border-radius: 3px;
}
.options-card::-webkit-scrollbar-thumb:hover {
  background: #999;
}

/* Each option */
.dropdown-option {
  padding: 10px 14px;
  font-size: 14px;
  color: #333;
  cursor: pointer;
  transition: background 0.2s ease;
  display: flex;
  align-items: center;
  gap: 8px;
}
.dropdown-option:hover {
  background: #f6f9fc;
}
.dropdown-option.active {
  background: #eaf3ff;
  color: #007bff;
  font-weight: 500;
}
.dropdown-option.no-option {
  color: #777;
  cursor: default;
  font-style: italic;
}
.select-wrap .select
{
  padding : 0px !important
}
/* Options card - smooth dropdown */
.options-card {
  margin-top: 6px;
  background: #fff;
  border: 1px solid #eee;
  border-radius: 8px;
  box-shadow: 0px 6px 18px rgba(0,0,0,0.08);
  max-height: 240px;
  overflow-y: auto;
  
  /* Smooth open/close */
  opacity: 0;
  transform: scaleY(0.95);
  transform-origin: top;
  transition: all 0.25s ease;
  z-index: 1000;
  pointer-events: none;
}

.options-card.show {
  opacity: 1;
  transform: scaleY(1);
  pointer-events: auto;
}

/* Animations */
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(-5px); }
  to { opacity: 1; transform: translateY(0); }
}

        `
      }
  </style>
  {!hasCustomSelector && (
    <div
      className={`${dropdownStatus ? "select-active" : "select"} ${props.disable && "disabled"}`}
      style={props.errorStyle ? { border: "1px solid #e63946" } : {}}
    >
      <TextField
        autoComplete={props.autoComplete}
        setFilter={setFilter}
        forceSet={forceSet}
        setforceSet={setforceSet}
        setOptionIndex={setOptionIndex}
        keepNull={props.keepNull}
        selectedVal={
          selectedOption
            ? props.t
              ? props.isMultiSelectEmp
                ? `${selectedOption} ${t("BPA_SELECTED_TEXT")}`
                : props.t(props.optionKey ? selectedOption[props.optionKey] : selectedOption)
              : props.optionKey
              ? selectedOption[props.optionKey]
              : selectedOption
            : null
        }
        filterVal={filterVal}
        addProps={{ length: filteredOption.length, currentIndex: optionIndex, selectOption }}
        dropdownDisplay={dropdownOn}
        handleClick={handleClick}
        disable={props.disable}
        freeze={props.freeze ? true : false}
        autoFocus={props.autoFocus}
        placeholder={props.placeholder}
        inputRef={props.ref}
      />
      <ArrowDown onClick={dropdownSwitch} className={`cp dropdown-arrow ${props.disable && "disabled"}`} />
    </div>
  )}

  {dropdownStatus && (
    <div
  id="jk-dropdown-unique"
  className={`options-card ${dropdownStatus ? "show" : ""} ${hasCustomSelector ? "margin-top-10" : ""}`}
  style={{ ...props.optionCardStyles }}
  ref={optionRef}
>

      {filteredOption?.map((option, index) => (
        <div
          key={index}
          className={`dropdown-option ${index === optionIndex ? "active" : ""}`}
          onClick={() => onSelect(option)}
        >
          {option.icon && <span className="icon">{option.icon}</span>}
          <span>{props.t ? props.t(option[props.optionKey]) : option[props.optionKey]}</span>
        </div>
      ))}
      {filteredOption?.length === 0 && (
        <div className="dropdown-option no-option">
          {props.t ? props.t("CMN_NOOPTION") : "No option available"}
        </div>
      )}
    </div>
  )}
</div>

  );
};

Dropdown.propTypes = {
  customSelector: PropTypes.any,
  showArrow: PropTypes.bool,
  selected: PropTypes.any,
  style: PropTypes.object,
  option: PropTypes.array,
  optionKey: PropTypes.any,
  select: PropTypes.any,
  t: PropTypes.func,
};

Dropdown.defaultProps = {
  customSelector: null,
  showArrow: true,
};

export default Dropdown;
