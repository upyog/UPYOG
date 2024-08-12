import React, { useRef, useState } from "react";
import { ArrowDown, CheckSvg, CloseSvg } from "@egovernments/digit-ui-react-components";
import { useTranslation } from "react-i18next";

const MultiSelect = ({
  options = [],
  optionsKey = "name",
  selected = [],
  onSelect,
  defaultLabel = "",
  defaultUnit = "",
  BlockNumber = 1,
  isOBPSMultiple = false,
  disabled = false,
}) => {
  const [active, setActive] = useState(false);
  const [searchQuery, setSearchQuery] = useState("");
  const [optionIndex, setOptionIndex] = useState(-1);
  const dropdownRef = useRef();
  const { t } = useTranslation();
  Digit.Hooks.useClickOutside(dropdownRef, () => setActive(false), active);

  const filteredOptions = searchQuery ? options.filter((option) => t(option[optionsKey]).toLowerCase().includes(searchQuery.toLowerCase())) : options;

  const onSearch = (e) => {
    setSearchQuery(e.target.value);
  };

  const keyChange = (e) => {
    if (e.key === "ArrowDown") {
      setOptionIndex((prev) => (prev + 1) % filteredOptions.length);
      e.preventDefault();
    } else if (e.key === "ArrowUp") {
      setOptionIndex((prev) => (prev === 0 ? filteredOptions.length - 1 : prev - 1));
      e.preventDefault();
    } else if (e.key === "Enter") {
      if (optionIndex >= 0 && optionIndex < filteredOptions.length) {
        handleSelect(null, filteredOptions[optionIndex]);
      }
    }
  };

  const handleSelect = (e, option) => {
    if (!disabled) {
      onSelect(e, option, isOBPSMultiple ? BlockNumber : undefined);
    }
    e?.stopPropagation();
  };

  const MenuItem = ({ option, index }) => (
    <div key={index} className={`menu-item ${index === optionIndex ? "active" : ""}`}>
      <input
        type="checkbox"
        value={option[optionsKey]}
        checked={selected.some((selectedOption) => selectedOption[optionsKey] === option[optionsKey])}
        onChange={(e) => handleSelect(e, option)}
        disabled={disabled}
      />
      <div className="custom-checkbox">
        <CheckSvg />
      </div>
      <p className="label">{t(option[optionsKey])}</p>
    </div>
  );

  const Menu = () => (
    <div className="server" id="jk-dropdown-unique">
      {filteredOptions.map((option, index) => (
        <MenuItem option={option} key={index} index={index} />
      ))}
    </div>
  );

  const clearOption = (option) => {
    if (!disabled) {
      onSelect({ target: { checked: false } }, option);
    }
  };

  return (
    <div className="multi-select-dropdown-wrap" ref={dropdownRef}>
      <div className={`master${active ? `-active` : ""}`}>
        <input
          className="cursorPointer"
          type="text"
          onKeyDown={keyChange}
          onFocus={() => setActive(true)}
          value={searchQuery}
          onChange={onSearch}
          disabled={disabled}
        />
        <div className="label">
          <p>{selected.length > 0 ? `${selected.length} ${defaultUnit}` : defaultLabel}</p>
          <ArrowDown />
        </div>
      </div>
      {active && !disabled && <Menu />}
      {selected.length > 0 && (
        <div className="bmc-selected-options">
          {selected.map((option) => (
            <span className="bmc-selected-option" key={option[optionsKey]}>
              <span>{t(option[optionsKey])}</span>
              <span className="bmc-clear-chip" onClick={() => clearOption(option)}>
                <CloseSvg />
              </span>
            </span>
          ))}
        </div>
      )}
    </div>
  );
};

export default MultiSelect;
