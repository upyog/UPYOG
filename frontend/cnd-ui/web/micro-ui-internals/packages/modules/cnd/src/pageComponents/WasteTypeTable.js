import React, { useState } from "react";
import { TextInput } from "@nudmcdgnpm/digit-ui-react-components";

/**
 * @author Shivank NUDM
 * WasteTypeTable component for displaying and collecting waste type quantities and units
 * with the ability to add new waste types dynamically
 * 
 * @param {Object} props
 * @param {Array} props.selectedWasteTypes - Array of selected waste types with format {i18nKey, code, value}
 * @param {Object} props.wasteDetails - Object containing quantity and unit for each waste type
 * @param {Function} props.onQuantityChange - Handler for quantity changes
 * @param {Function} props.onUnitChange - Handler for unit changes
 * @param {Function} props.t - Translation function
 * @param {Array} props.unitOptions - Available unit options
 * @param {Array} props.availableWasteTypes - All available waste types to select from
 * @param {Function} props.onAddWasteType - Handler when a new waste type is added
 */
const WasteTypeTable = ({
  selectedWasteTypes = [],
  wasteDetails = {},
  onQuantityChange,
  onUnitChange,
  t,
  unitOptions = ["Kilogram", "Ton", "Metric Ton"], //TODO: Add this in MAster
  availableWasteTypes = [], // All waste types from the MDMS
  onAddWasteType = () => {} // Callback to inform parent component of new waste type
}) => {
  // Validation rules for quantity input
  const validation = {
    isRequired: false,
    pattern: "^[0-9.]{1,10}$",
    type: "tel",
    title: "",
  };

  // Local state for managing additional rows
  const [additionalRows, setAdditionalRows] = useState([]);
  const [nextId, setNextId] = useState(1);

  // Get waste types that haven't been selected yet
  const getAvailableOptions = () => {
    const allSelectedCodes = [
      ...selectedWasteTypes.map(type => type.code),
      ...additionalRows
        .filter(row => row.selectedType)
        .map(row => row.selectedType.code)
    ];
    
    return availableWasteTypes.filter(type => !allSelectedCodes.includes(type.code));
  };

  // Handle adding a new row
  const handleAddRow = () => {
    const newRow = {
      id: `additional-${nextId}`,
      selectedType: null,
      quantity: "",
      unit: "Kilogram"
    };
    
    setAdditionalRows(prev => [...prev, newRow]);
    setNextId(prev => prev + 1);
  };

  // Handle waste type selection in additional rows
  const handleTypeSelection = (id, selectedCode) => {
    const selectedType = availableWasteTypes.find(type => type.code === selectedCode);
    
    setAdditionalRows(prev => 
      prev.map(row => 
        row.id === id ? { ...row, selectedType } : row
      )
    );
    
    // Notify parent component if a type is selected
    if (selectedType) {
      onAddWasteType(selectedType, "", "Kilogram");
    }
  };

  // Handle quantity change in additional rows
  const handleAdditionalQuantityChange = (id, value) => {
    setAdditionalRows(prev => 
      prev.map(row => 
        row.id === id ? { ...row, quantity: value } : row
      )
    );
    
    // Find the row to get its waste type code
    const row = additionalRows.find(row => row.id === id);
    if (row?.selectedType?.code) {
      onQuantityChange(row.selectedType.code, value);
    }
  };

  // Handle unit change in additional rows
  const handleAdditionalUnitChange = (id, unit) => {
    setAdditionalRows(prev => 
      prev.map(row => 
        row.id === id ? { ...row, unit } : row
      )
    );
    
    // Find the row to get its waste type code
    const row = additionalRows.find(row => row.id === id);
    if (row?.selectedType?.code) {
      onUnitChange(row.selectedType.code, unit);
    }
  };

  // Handle removing an additional row
  const handleRemoveRow = (id) => {
    // Find the row to get its waste type code before removing
    const rowToRemove = additionalRows.find(row => row.id === id);
    
    setAdditionalRows(prev => prev.filter(row => row.id !== id));
    
    // TODO: Implement callback to parent component to remove this waste type from wasteDetails
    if (rowToRemove?.selectedType?.code) {
      // This would require adding a onRemoveWasteType prop to this component
      // onRemoveWasteType(rowToRemove.selectedType.code);
    }
  };

  return (
    <div className="waste-type-table cnd-waste-table">
      {/* Table Headers */}
      <div className="waste-type-header cnd-waste-table-header">
        <div className="cnd-waste-table-heading-type">{t("CND_SELECTED_WASTE")}</div>
        <div className="cnd-waste-table-heading-quantity">{t("CND_QUANTITY")}</div>
        <div className="cnd-waste-table-heading-units">{t("CND_UNIT")}</div>
      </div>
      
      {/* Existing Selected Waste Types */}
      {selectedWasteTypes.length > 0 ? (
        selectedWasteTypes.map((type) => (
          <div 
            key={type.code} 
            className="waste-type-row cnd-waste-table-row" 
          >
            {/* Waste Type Name */}
            <div className="cnd-waste-table-heading-type">{t ? t(type.i18nKey) : type.value || type.i18nKey}</div>
            
            {/* Quantity Input */}
            <div className="cnd-waste-table-quantity-input-wrapper">
              <TextInput
                t={t}
                type="text"
                name={`quantity-${type.code}`}
                value={wasteDetails[type.code]?.quantity || ""}
                onChange={(e) => onQuantityChange(type.code, e.target.value)}
                className="cnd-waste-table-quantity-text-input"
                ValidationRequired={false}
                {...validation}
              />
            </div>
            
            {/* Unit Dropdown */}
            <div className="cnd-waste-table-unit-dropdown-wrapper">
              <select 
                value={wasteDetails[type.code]?.unit || "Kilogram"}
                onChange={(e) => onUnitChange(type.code, e.target.value)}
                className="cnd-waste-table-unit-dropdown-select"
              >
                {unitOptions.map(unit => (
                  <option key={unit} value={unit}>{unit}</option>
                ))}
              </select>
            </div>
          </div>
        ))
      ) : (
        <div className="cnd-waste-table-no-waste-selected">
          {t("CND_NO_WASTE_TYPES_SELECTED")}
        </div>
      )}
      
      {/* Additional Waste Type Rows */}
      {additionalRows.map((row) => (
        <div 
          key={row.id} 
          className="waste-type-row additional cnd-waste-table-additional-row" 
        >
          {/* Waste Type Dropdown */}
          <div className="cnd-waste-table-heading-type">
            <select
              value={row.selectedType?.code || ""}
              onChange={(e) => handleTypeSelection(row.id, e.target.value)}
              className="cnd-waste-table-additional-select"
            >
              <option value="">{t("CND_SELECT_WASTE_TYPE")}</option>
              {getAvailableOptions().map(type => (
                <option key={type.code} value={type.code}>
                  {t ? t(type.i18nKey) : type.value || type.i18nKey}
                </option>
              ))}
            </select>
          </div>
          
          {/* Quantity Input */}
          <div className="cnd-waste-table-quantity-input-wrapper">
            <TextInput
              t={t}
              type="text"
              name={`additional-quantity-${row.id}`}
              value={row.quantity}
              onChange={(e) => handleAdditionalQuantityChange(row.id, e.target.value)}
              className="cnd-waste-table-quantity-text-input"
              ValidationRequired={false}
              {...validation}
            />
          </div>
          
          {/* Unit Dropdown */}
          <div className="cnd-waste-table-additional-unit-dropdown">
            <select 
              value={row.unit}
              onChange={(e) => handleAdditionalUnitChange(row.id, e.target.value)}
              className="cnd-waste-table-quantity-text-input"
            >
              {unitOptions.map(unit => (
                <option key={unit} value={unit}>{unit}</option>
              ))}
            </select>
            
            {/* Remove Button */}
            <button
              type="button"
              onClick={() => handleRemoveRow(row.id)}
              className="cnd-waste-table-remove-btn"
            >
              ✕
            </button>
          </div>
        </div>
      ))}
      
      {/* Add Button */}
      <div>
        <button
          type="button"
          onClick={handleAddRow}
          className="cnd-waste-table-add-btn"
        >
          <span className="cnd-waste-table-add-btn-icon">+</span> {t("CND_ADD_WASTE_TYPE")}
        </button>
      </div>
    </div>
  );
};

export default WasteTypeTable;