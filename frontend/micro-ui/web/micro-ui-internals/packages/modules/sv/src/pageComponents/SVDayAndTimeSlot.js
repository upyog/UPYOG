import React from "react";
import { CardLabel, TextInput, CheckBox, Dropdown } from "@upyog/digit-ui-react-components";

const DayAndTimeSlot = ({ t, day, onDayToggle, onSameForAllChange, isSameForAll, onTimeChange }) => {
  let validation = {};
  return (
    <div style={{ marginBottom: "10px", display: "flex" }}>
      <div style={{ display: "flex", width: "65px" }}>
        <CheckBox
          checked={day.isSelected}
          onChange={onDayToggle}
          label={day.name}
        />
      </div>
      {day.isSelected && (
        <div >
          {/* <CardLabel style={{ marginBottom: "2px", marginTop: "15px" }}>{t("SV_SELECT_TIME")}</CardLabel> */}
          <div style={{ display: "flex", gap: "120%", width: "111px", marginLeft: "30px", alignItems: "center" }}>
            <TextInput
              style={{ width: "100%", marginLeft: "80px" }}
              type="time"
              name="startTime"
              value={day?.startTime || ""}
              onChange={(e) => onTimeChange("startTime", e.target.value)}
              placeholder={"Start Time"}
              {...(validation = {
                isRequired: day.isSelecte ? true : false,
              })}
            />

            <TextInput
              style={{ width: "100%", marginLeft: "15px" }}
              type="time"
              name="endTime"
              value={day?.endTime || ""}
              onChange={(e) => onTimeChange("endTime", e.target.value)}
              placeholder={"End Time"}
              {...(validation = {
                isRequired: day.isSelecte ? true : false,
              })}
            />

            {day?.startTime && day?.endTime && <CheckBox
              style={{ width: "100%"}}
              checked={!isSameForAll}
              onChange={() => onSameForAllChange(day?.startTime, day?.endTime)}
              label="Same for All Fields"
            />}
          </div>
        </div>
      )}
    </div>
  );
};

export default DayAndTimeSlot;


