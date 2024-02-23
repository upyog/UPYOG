import React, { Component } from "react";
import { FeesEstimateOverviewCard } from "../../ui-molecules-local";
import { connect } from "react-redux";
import get from "lodash/get";

class EstimateOverviewCardContainer extends Component {
  constructor(props) {
    super(props);
  }
  render() {
    return <FeesEstimateOverviewCard estimate={this.props.estimate} isMigrated = {this.props.isMigrated} />;
  }
}

const mapStateToProps = (state, ownProps) => {
  const { screenConfiguration } = state;
  const fees = get(
    screenConfiguration.preparedFinalObject,
    ownProps.sourceJsonPath,
    []
  );
  const isMigrated = get(screenConfiguration.preparedFinalObject, "WaterConnection[0].additionalDetails.isMigrated");
  const estimate = {
    header: { labelName: "Fee Estimate", labelKey: "WS_SUMMARY_FEE_EST" },
    fees,
    extra: [
      //   { textLeft: "Last Date for Rebate (20% of TL)" },
      //   {
      //     textLeft: "Penalty (10% of TL) applicable from"
      //   },
      //   { textLeft: "Additional Penalty (20% of TL) applicable from" }
    ]
  };
  return { estimate, isMigrated };
};

export default connect(
  mapStateToProps,
  null
)(EstimateOverviewCardContainer);
