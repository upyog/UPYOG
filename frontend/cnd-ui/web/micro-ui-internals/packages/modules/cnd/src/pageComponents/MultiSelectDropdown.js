import PropTypes from "prop-types";
import React, { useEffect, useRef, useState } from "react";
import { ArrowDown,CheckSvg } from "@nudmcdgnpm/digit-ui-react-components";

/**
 * @author - Shivank - NUDM
 * 
 * Why We Developed This?
 * This component provides a multi-select dropdown that allows users to select multiple options from a list. 
 * It maintains selected values as an array of objects and ensures smooth user experience with a toggleable dropdown,
 *  checkboxes, and keyboard accessibility.

 * How It Works?

 * User clicks the dropdown → It opens the list of options.
 * User selects/deselects checkboxes → The selected values update dynamically.
 * Dropdown closes on click outside → Keeps interactions intuitive.
 * Maintains selected values in state → The parent component can access them via onChange.
 * Now, here's the commented code:
 */


const MultiSelectDropdown = ({ options, selectedValues, onChange, optionKey, placeholder, disable, style, t }) => {
  const [dropdownStatus, setDropdownStatus] = useState(false);
  const [selectedOptions, setSelectedOptions] = useState(selectedValues || []);
  const optionRef = useRef(null);

  useEffect(() => {
    let isMounted = true;
    if (typeof onChange === "function" && isMounted) {
      onChange(selectedOptions);
    }
    return () => {
      isMounted = false;
    };
  }, [selectedOptions, onChange]);

  // Toggles the dropdown visibility
  const toggleDropdown = () => {
    if (!disable) {
      setDropdownStatus((prev) => !prev);
    }
  };

  // Handles selection/deselection of an option
  const handleSelection = (option) => {
    const isSelected = selectedOptions.some((item) => item[optionKey] === option[optionKey]);
    if (isSelected) {
      setSelectedOptions(selectedOptions.filter((item) => item[optionKey] !== option[optionKey]));
    } else {
      setSelectedOptions([...selectedOptions, option]);
    }
  };

  const isSelected = (option) => selectedOptions.some((item) => item[optionKey] === option[optionKey]);

  return (
    <div style={{marginBottom:"25px", width:"58%"}}>
    <div className="multi-select-dropdown-wrap" ref={optionRef}>
      <div className={`master${dropdownStatus ? `-active` : ``}`} onClick={toggleDropdown}>
        <div className="label">
            {/* Display selected options or placeholder text */}
          <p>{selectedOptions.length > 0 ? selectedOptions.map((opt) => t ? t(opt[optionKey]) : opt[optionKey]).join(", ") : placeholder}</p>

          <ArrowDown />
        </div>
      </div>
      
      {dropdownStatus && (
        <div className="server" id="jk-dropdown-unique">
          {options.map((option, index) => (
            <div key={index} className="option-item" style={index % 2 !== 0 ? { background: "#EEEEEE" } : {}}>
              <input
                type="checkbox"
                value={option[optionKey]}
                checked={isSelected(option)}
                onChange={() => handleSelection(option)}
                style={{ minWidth: "24px", width: "100%" }}
              />
              <div className="custom-checkbox">
                <CheckSvg style={{ innerWidth: "24px", width: "24px" }} />
              </div>
              <p className="label">{t(option[optionKey])}</p>
            </div>
          ))}
        </div>
      )}
    </div>
    </div>
  );
};

MultiSelectDropdown.propTypes = {
  options: PropTypes.array.isRequired,
  selectedValues: PropTypes.array,
  onChange: PropTypes.func.isRequired,
  optionKey: PropTypes.string.isRequired,
  placeholder: PropTypes.string,
  disable: PropTypes.bool,
  style: PropTypes.object,
  t: PropTypes.func,
};

MultiSelectDropdown.defaultProps = {
  selectedValues: [],
  placeholder: "Select",
  disable: false,
};

export default MultiSelectDropdown;