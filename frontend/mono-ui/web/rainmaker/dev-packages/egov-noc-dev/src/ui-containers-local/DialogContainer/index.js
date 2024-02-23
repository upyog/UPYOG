import React from "react";
import { connect } from "react-redux";
import get from "lodash/get";
import { Dialog, DialogContent } from "@material-ui/core";
import CloseIcon from "@material-ui/icons/Close";
import IconButton from "@material-ui/core/IconButton";

import { handleScreenConfigurationFieldChange as handleField } from "egov-ui-framework/ui-redux/screen-configuration/actions";

class DialogContainer extends React.Component {
  handleClose = () => {
    const { screenKey } = this.props;
    this.props.handleField(
      screenKey,
      `components.adhocDialog`,
      "props.open",
      false
    );
  };
  style={
    position: "absolute",
    top: "5px",
    right: "21px"
  }
  style2={
    paddingTop: "20px",

  }

  render() {
    const { open, maxWidth, children,classes,onClose } = this.props;
    return (
      <Dialog open={open} maxWidth={maxWidth} onClose={this.handleClose}>
         <IconButton
          aria-label="Close"
          onClick={this.handleClose}
          className="dialog-close-buttonStyele"
          style={this.style}
        >
          <CloseIcon/>
        </IconButton> 
        <DialogContent children={children} style={this.style2} />
      </Dialog>
    );
  }
}

const mapStateToProps = (state, ownProps) => {
  const { screenConfiguration } = state;
  const { screenKey } = ownProps;
  const { screenConfig } = screenConfiguration;
  const open = get(
    screenConfig,
    `${screenKey}.components.adhocDialog.props.open`
  );

  return {
    open,
    screenKey,
    screenConfig
  };
};

const mapDispatchToProps = dispatch => {
  return { handleField: (a, b, c, d) => dispatch(handleField(a, b, c, d)) };
};

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(DialogContainer);
