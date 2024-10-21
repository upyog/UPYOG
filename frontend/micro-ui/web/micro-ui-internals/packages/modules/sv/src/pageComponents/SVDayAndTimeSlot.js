import React from "react";
import { CardLabel, TextInput, CheckBox, Dropdown } from "@nudmcdgnpm/digit-ui-react-components";

const DayAndTimeSlot = ({ t, day, onDayToggle, onTimeChange }) => {
  let validation={};
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
        <div style={{ display: "flex", gap:"142%", width: "150px", marginTop: "8px", alignItems: "center" }}>
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
          
          <TextInput
            style={{  width: "100%", marginLeft: "15px" }}
            type="time"
            name="endTime"
            value={day?.endTime || ""}
            onChange={(e) => onTimeChange("endTime", e.target.value)}
            placeholder={"End Time"}
            {...(validation = {
              isRequired: day.isSelecte?true:false,
          })}
          />
        </div>
        </div>
      )}
    </div>
  );
};

export default DayAndTimeSlot;


