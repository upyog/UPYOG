import React from "react";
import { Link } from "react-router-dom";
import { Card, TextField, Image, UsernameFieldWithIcon } from "components";
import { Button } from "egov-ui-framework/ui-atoms";
import { CityPicker, CityPickerNew } from "modules/common";
import Label from "egov-ui-kit/utils/translationNode";
import logo from "egov-ui-kit/assets/images/logo_black.png";
import "./index.css";

const LoginForm = ({ handleFieldChange, form, onForgotPasswdCLick, logoUrl }) => {
  const fields = form.fields || {};
  const submit = form.submit;
  return (
    <Card
      className="user-screens-card col-lg-offset-4 col-lg-4 col-md-offset-4 col-md-4 col-sm-offset-4 col-sm-4"
      style={{padding: "25px 45px"}}
      textChildren={
        <div>
          <div  style={{ marginBottom: "20px", borderBottom: "1px solid #192771", display: "flex", justifyContent: "center", marginTop: "8px" }}>
            <img src="https://upload.wikimedia.org/wikipedia/commons/5/55/Emblem_of_India.svg" style={{ width: "auto", height: "74px", padding: "0px 11px" }} />
            <h3 style={{ fontSize: "20px", marginLeft: "12px", marginTop: "0px" }}>
              <strong style={{ color: "#0C3A60", paddingRight: "15px", lineHeight: "1.2" }}>
                Housing and Urban <br /> Development Deparment
              </strong>
              <br />
              <p style={{ color: "#000000", fontSize: "14px", marginTop: "5px" }}>
                Government of Jammu & Kashmir
              </p>
            </h3>
          </div>
          <Label className="text-center" bold={true} dark={true} fontSize={16} label="CORE_COMMON_LOGIN" />
          <UsernameFieldWithIcon onChange={(e, value) => handleFieldChange("username", value)} {...fields.username} />
          <UsernameFieldWithIcon onChange={(e, value) => handleFieldChange("password", value)} {...fields.password} />
          <CityPickerNew onChange={handleFieldChange} fieldKey="city" field={fields.city} />
          <Button
            {...submit}
            style={{
              height: "48px",
              width: "100%",
              marginTop: "18px"
            }}
            variant={"contained"}
            color={"primary"}
          >
            <Label buttonLabel={true} labelStyle={{ fontWeight: 500 }} label="CORE_COMMON_CONTINUE" />
          </Button>
          <Link to="/forgot-password">
            <div style={{ float: "left" }}>
              <Label
                containerStyle={{ cursor: "pointer", position: "relative", zIndex: 10 }}
                labelStyle={{ marginBottom: "12px" }}
                className="forgot-passwd"
                fontSize={14}
                label="CORE_COMMON_FORGOT_PASSWORD"
              />
            </div>
          </Link>
          {/* <Button {...submit} fullWidth={true} primary={true} /> */}
        </div>
      }
    />
  );
};

export default LoginForm;
