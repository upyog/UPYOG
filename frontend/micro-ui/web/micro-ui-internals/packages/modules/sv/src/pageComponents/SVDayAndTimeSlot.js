import React from "react";
import { CardLabel, TextInput, CheckBox, Dropdown } from "@nudmcdgnpm/digit-ui-react-components";

const DayAndTimeSlot = ({ t, day, onDayToggle, onTimeChange,onAmPmChange }) => {
  let validation={};
  const amPmOptions = ["AM", "PM"];
  return (
    <div style={{ marginBottom: "16px" }}>
      <div style={{ display: "flex", alignItems: "center" }}>
        <CheckBox
          checked={day.isSelected}
          onChange={onDayToggle}
          label={day.name}
        />
      </div>
      {day.isSelected && (
        <div>
        <CardLabel style={{marginBottom:"2px", marginTop:"15px"}}>{t("SV_SELECT_TIME")}</CardLabel>
        <div style={{ display: "flex", gap:"195%", width: "150px", marginTop: "8px", alignItems: "center" }}>
          <TextInput
            style={{ width: "100%" }}
            type="time"
            name="startTime"
            value={day?.startTime || ""}
            onChange={(e) => onTimeChange("startTime", e.target.value)}
            placeholder={"Start Time"}
            {...(validation = {
              isRequired: day.isSelecte?true:false,
          })}
          />
          <select
              style={{ width: "50px", height: "33px", cursor: "pointer", marginLeft:"-267px", border:"ridge", marginTop:"-16px", borderColor:"black", borderWidth:"1px"}}
              value={day?.startTimeAmPm || ""}
              onChange={(e) => onAmPmChange("startTimeAmPm", e.target.value)}
            >
              <option value="" disabled>
                AM
              </option>
              {amPmOptions.map((option, index) => (
                <option key={index} value={option}>
                  {option}
                </option>
              ))}
            </select>
          
          <TextInput
            style={{  width: "86px", marginLeft: "15px" }}
            type="time"
            name="endTime"
            value={day?.endTime || ""}
            onChange={(e) => onTimeChange("endTime", e.target.value)}
            placeholder={"End Time"}
            {...(validation = {
              isRequired: day.isSelecte?true:false,
          })}
          />
          <select
              style={{ width: "50px", height: "33px", cursor: "pointer", marginLeft:"-267px", border:"ridge", marginTop:"-16px", borderColor:"black", borderWidth:"1px" }}
              value={day?.endTimeAmPm || ""}
              onChange={(e) => onAmPmChange("endTimeAmPm", e.target.value)}
            >
              <option value="" disabled>
                PM
              </option>
              {amPmOptions.map((option, index) => (
                <option key={index} value={option}>
                  {option}
                </option>
              ))}
            </select>
        </div>
        </div>
      )}
    </div>
  );
};

export default DayAndTimeSlot;
