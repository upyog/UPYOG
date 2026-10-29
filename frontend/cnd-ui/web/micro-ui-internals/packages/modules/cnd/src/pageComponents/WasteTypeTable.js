import React, { useState } from "react";
import { TextInput } from "@nudmcdgnpm/digit-ui-react-components";
import { cndStyles } from "../utils/cndStyles";

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
  lockedWasteCodes = [],
  onAddWasteType = () => {},
  onRemoveWasteType = () => {},
}) => {
  const validation = {
    isRequired: false,
    pattern: "^[0-9.]{1,10}$",
    type: "tel",
    title: "",
  };

  const [showAddRow, setShowAddRow] = useState(false);

  // Options not yet selected
  const availableOptions = availableWasteTypes.filter(
    (type) => !selectedWasteTypes.some((s) => s.code === type.code)
  );

  const handleSelectNew = (code) => {
    const selectedType = availableWasteTypes.find((type) => type.code === code);
    if (!selectedType) return;
    onAddWasteType(selectedType, "", "Kilogram");
    setShowAddRow(false); // row becomes a normal row in the list above
  };

  const label = (type) => t(type.i18nKey);

  return (
    <div className="waste-type-table" style={cndStyles.wasteTypeTable}>
      {/* Headers */}
      <div className="waste-type-header" style={cndStyles.wasteTypeHeader}>
        <div style={cndStyles.wasteTypeHeadingType}>{t("CND_SELECTED_WASTE")}</div>
        <div style={cndStyles.wasteTypeHeadingQuantity}>{t("CND_QUANTITY")}</div>
        <div style={cndStyles.wasteTypeHeadingUnits}>{t("CND_UNIT")}</div>
      </div>

      {/* Selected waste types (API + employee added) */}
      {selectedWasteTypes.length > 0 ? (
        selectedWasteTypes.map((type) => {
          const isRemovable = !lockedWasteCodes.includes(type.code);
          return (
            <div key={type.code} className="waste-type-row" style={cndStyles.wasteTypeRow}>
              <div style={cndStyles.wasteTypeHeadingType}>{label(type)}</div>

            <div style={cndStyles.wasteQuantityInput}>
              <TextInput
                t={t}
                type="text"
                name={`quantity-${type.code}`}
                value={wasteDetails[type.code]?.quantity || ""}
                onChange={(e) => onQuantityChange(type.code, e.target.value)}
                style={cndStyles.quantityTextInput}
                ValidationRequired={false}
                {...validation}
              />
            </div>

            <div style={{ ...cndStyles.unitDropdown, display: "flex", alignItems: "center", gap: "8px" }}>
              <select
                value={wasteDetails[type.code]?.unit || "Kilogram"}
                onChange={(e) => onUnitChange(type.code, e.target.value)}
                style={cndStyles.unitDropDOwnSelect}
              >
                {unitOptions.map((unit) => (
                  <option key={unit} value={unit}>{unit}</option>
                ))}
              </select>

              {isRemovable && (
                <button
                  type="button"
                  onClick={() => onRemoveWasteType(type.code)}
                  style={cndStyles.removeButton}
                >
                  ✕
                </button>
              )}
            </div>
            </div>
          );
        })
      ) : (
        <div style={cndStyles.noWasteTypeSelect}>{t("CND_NO_WASTE_TYPES_SELECTED")}</div>
      )}

      {/* Temporary "pick a type" row */}
      {showAddRow && availableOptions.length > 0 && (
        <div className="waste-type-row additional" style={cndStyles.additionalWasteTypeRow}>
          <div style={cndStyles.wasteTypeHeadingType}>
            <select
              value=""
              onChange={(e) => handleSelectNew(e.target.value)}
              style={cndStyles.additionalDropDown}
            >
              <option value="">{t("CND_SELECT_WASTE_TYPE")}</option>
              {availableOptions.map((type) => (
                <option key={type.code} value={type.code}>{label(type)}</option>
              ))}
            </select>
          </div>
          <div style={cndStyles.wasteQuantityInput} />
          <div style={cndStyles.additionalUnitDropdown}>
            <button
              type="button"
              onClick={() => setShowAddRow(false)}
              style={cndStyles.removeButton}
            >
              ✕
            </button>
          </div>
        </div>
      )}

      {/* Add button – hidden when no options left or the pick-row is already open */}
    {availableOptions.length > 0 && !showAddRow && (
      <div>
        <button type="button" onClick={() => setShowAddRow(true)} style={cndStyles.addButton}>
            <span style={cndStyles.addButtonFont}>+</span> {t("CND_ADD_WASTE_TYPE")}
          </button>
        </div>
      )}
    </div>
  );
};

export default WasteTypeTable;