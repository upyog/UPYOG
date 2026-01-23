import React from "react";
import PropTypes from "prop-types";
import TextField from "../TextField";
import {
  getLocaleLabels
} from "egov-ui-framework/ui-utils/commons";
import PhoneAndroidIcon from "@material-ui/icons/PhoneAndroid";

const containerStyle = {
  position: "relative",
  width: "100%",
  // marginBottom: "1rem",
};

const prefixStyleBase = {
  position: "absolute",
  left: "15px",
  top: "50%",
  transform: "translateY(-50%)",
  color: "#969696",
  fontSize: "1rem",
  pointerEvents: "none",
  borderRight: "1px solid #eee",
  paddingRight: "8px",
  height: "60%",
  display: "flex",
  alignItems: "center",
};

const inputStyleBase = {
  width: "100%",
  height: "44px",
  padding: "12px 15px 12px 15px", // left padding for prefix
  border: "1px solid #b3b3b3",
  borderRadius: "10px",
  fontSize: "16px",
  outline: "none",
  boxSizing: "border-box",
  transition: "all 0.2s ease",
  backgroundColor: "#fff",
  color: "#1B1B1B",
  letterSpacing: "0.7px",
};

const inputFocusStyle = {
  borderColor: "#0060BD",
  boxShadow: "0 0 0 3px rgba(0,96,189,0.15)",
};

const suffixIconStyle = {
  position: "absolute",
  right: "15px",
  top: "50%",
  transform: "translateY(-50%)",
  color: "#757575",
  pointerEvents: "none",
  fontSize: "20px",
};

class MobileNumberFieldNew extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      isFocused: false,
    };
  }

  handleFocus = () => this.setState({ isFocused: true });
  handleBlur = () => this.setState({ isFocused: false });

  render() {
    const { className, prefix = "+91", prefixStyle = {}, textFieldStyle = {}, ...textFieldProps } = this.props;
    const { isFocused } = this.state;

    return (
      <div style={containerStyle}>
        <div style={{ ...prefixStyleBase, ...prefixStyle }}>{prefix}</div>
        <TextField
          className={`mobile-number-field ${className}`}
          type="tel"
          maxLength={10}
          minLength={10}
          placeholder={getLocaleLabels("NOC_ENTER_APPLICANT_MOBILE_NO_PLACEHOLDER", "NOC_ENTER_APPLICANT_MOBILE_NO_PLACEHOLDER")}
          inputStyle={{
            ...inputStyleBase,
            ...(isFocused ? inputFocusStyle : {}),
            ...textFieldStyle,
          }}
          underlineShow={false}
          onFocus={this.handleFocus}
          onBlur={this.handleBlur}
          {...textFieldProps}
        />
        <PhoneAndroidIcon style={suffixIconStyle} />
      </div>
    );
  }
}

MobileNumberFieldNew.propTypes = {
  className: PropTypes.string,
  prefix: PropTypes.string,
  prefixStyle: PropTypes.object,
  textFieldStyle: PropTypes.object,
};

export default MobileNumberFieldNew;
