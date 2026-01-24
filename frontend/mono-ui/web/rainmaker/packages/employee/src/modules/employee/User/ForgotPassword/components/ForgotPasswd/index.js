import React from "react";
import { Card, TextField, Image } from "components";
import { Button} from "egov-ui-framework/ui-atoms";
import Label from "egov-ui-kit/utils/translationNode";
import logo from "egov-ui-kit/assets/images/logo_black.png";
import "./index.css";
import { CityPicker, CityPickerNew } from "modules/common";
import FieldNew from "egov-ui-kit/utils/fieldNew";

const ForgotPasswd = ({ form, handleFieldChange,logoUrl }) => {
  const fields = form.fields || {};
  const submit = form.submit;

  return (
    <Card
      className="user-screens-card forgot-passwd-card col-lg-offset-4 col-lg-4 col-md-offset-4 col-md-4"
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
          <Label
            style={{ marginBottom: "12px" }}
            className="text-center forgotpasswd"
            bold={true}
            dark={true}
            fontSize={16}
            label="CORE_COMMON_FORGOT_PASSWORD_LABEL"
          />
          <FieldNew fieldKey="username" field={fields.username} handleFieldChange={handleFieldChange} />
          <CityPickerNew onChange={handleFieldChange} fieldKey="tenantId" field={fields.tenantId} />
          <Button
           id="login-submit-action"
                {...submit}
            style={{
              height: "48px",     
              width:"100%",
              marginTop: "20px",
              marginBottom: "15px"       
            }}
            variant={"contained"}
            color={"primary"}
          >
            <Label buttonLabel={true}   labelStyle={{fontWeight:500 }}  label="CORE_COMMON_CONTINUE" />
          </Button>
          {/* <Button id="login-submit-action" primary={true} label="CONTINUE" fullWidth={true} {...submit} /> */}
        </div>
      }
    />
  );
};

export default ForgotPasswd;
