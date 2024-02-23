import React from "react";
import PropTypes from "prop-types";
import { withStyles } from "@material-ui/core/styles";
import FormGroup from "@material-ui/core/FormGroup";
import { connect } from "react-redux";
import FormControlLabel from "@material-ui/core/FormControlLabel";
import Checkbox from "@material-ui/core/Checkbox";
import { prepareFinalObject } from "egov-ui-framework/ui-redux/screen-configuration/actions";
import { getLocaleLabels } from "egov-ui-framework/ui-utils/commons";

//import "./index.css";

const styles = {
  root: {
    color: "#FE7A51",
    "&$checked": {
      color: "#FE7A51",
    },
  },
  checked: {},
};
debugger;
class CheckboxLabels extends React.Component {
  state = {
    checkedG: false,
  };

  handleChange = (name) => (event) => {
    const { jsonPath, approveCheck } = this.props;
    this.setState({ [name]: event.target.checked }, () =>
      approveCheck(jsonPath, this.state.checkedG)
    );
  };

  render() {
    const { classes, content, label = {}, localizationLabels } = this.props;
    let translatedLabel = "";
    if (label && label.labelKey && Array.isArray(label.labelKey)) {
      label.labelKey.forEach((key) => {
        translatedLabel += getLocaleLabels(key, key, localizationLabels) + " ";
      });
    } else {
      translatedLabel = getLocaleLabels(
        label.labelName,
        label.labelKey,
        localizationLabels
      );
    }

    return (
      <FormGroup row>
        <FormControlLabel
          classes={{ label: "checkbox-label" }}
          control={
            <Checkbox
              checked={this.state.checkedG}
              onChange={this.handleChange("checkedG")}
              value={this.state.checkedG}
              classes={{
                root: classes.root,
                checked: classes.checked,
              }}
            />
          }
          label={translatedLabel}
        />
      </FormGroup>
    );
  }
}

const mapStateToProps = (state, ownprops) => {
  const { screenConfiguration, app } = state;
  const { localizationLabels } = app;
  const { jsonPath } = ownprops;
  const { preparedFinalObject } = screenConfiguration;
  return { preparedFinalObject, jsonPath, localizationLabels };
};

const mapDispatchToProps = (dispatch) => {
  return {
    approveCheck: (jsonPath, value) => {
      dispatch(prepareFinalObject(jsonPath, value));
    },
  };
};

CheckboxLabels.propTypes = {
  classes: PropTypes.object.isRequired,
};

export default withStyles(styles)(
  connect(mapStateToProps, mapDispatchToProps)(CheckboxLabels)
);
