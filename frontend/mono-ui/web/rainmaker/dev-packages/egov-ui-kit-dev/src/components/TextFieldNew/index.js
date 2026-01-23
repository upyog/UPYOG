import React from "react";
import PropTypes from "prop-types";
import TextField from "../TextField"; // your normal TextField
import { getLocaleLabels } from "egov-ui-framework/ui-utils/commons";
import PersonIcon from "material-ui/svg-icons/social/person"; // MUI v1

const containerStyle = {
  position: "relative",
  width: "100%",
  marginBottom: "1rem",
};

const inputStyleBase = {
  width: "100%",
  height: "44px",
  padding: "12px 15px",
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
  right: "13px",
  top: "55%",
  transform: "translateY(-50%)",
  color: "#757575",
  pointerEvents: "none",
  fontSize: "20px",
};

class TextFieldWithIcon extends React.Component {
  state = { isFocused: false };

  handleFocus = () => this.setState({ isFocused: true });
  handleBlur = () => this.setState({ isFocused: false });

  render() {
    const { className, textFieldStyle = {}, placeholder, suffixIcon, ...textFieldProps } = this.props;
    const { isFocused } = this.state;

    return (
      <div style={containerStyle}>
        <TextField
          className={className}
          placeholder={getLocaleLabels("CORE_COMMON_NAME_PLACEHOLDER", "CORE_COMMON_NAME_PLACEHOLDER")}
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
        <span style={suffixIconStyle}>
          {suffixIcon || <PersonIcon />}
        </span>
      </div>
    );
  }
}

TextFieldWithIcon.propTypes = {
  className: PropTypes.string,
  placeholder: PropTypes.string,
  textFieldStyle: PropTypes.object,
  suffixIcon: PropTypes.node,
};

export default TextFieldWithIcon;
