import React, { Component } from "react";
import Label from "egov-ui-kit/utils/translationNode";
import Card from "egov-ui-kit/components/Card";

import BreadCrumbs from "egov-ui-kit/components/BreadCrumbs";
import Divider from "egov-ui-kit/components/Divider";
import { connect } from "react-redux";
import { addBreadCrumbs } from "egov-ui-kit/redux/app/actions";
import "./index.css";
class PropertTaxNotification extends Component {
  componentDidMount() {
    const { addBreadCrumbs, title } = this.props;
    title && addBreadCrumbs({ title: "ONLINE BUILDING PLAN", path: window.location.pathname });
  }
  render() {
    const { urls, history } = this.props;
    return (
      <div className="col-sm-12 blockBox">
        <BreadCrumbs url={urls} history={history} />
        <Card
          id="home-complaint-card"
          className="clearfix"
          textChildren={
            <div className="example-main-cont clearfix">
              <div className="col-sm-12 descriptionStyle">

                <a href="https://enaksha.lgpunjab.gov.in" target="_blank"><h2>ONLINE BUILDING PLAN</h2></a>

              </div>

            </div>
          }
        />
      </div>
    );
  }
}

const mapStateToProps = state => {
  const { app } = state;
  const { urls } = app;
  return { urls };
};

const mapDispatchToProps = dispatch => {
  return {
    addBreadCrumbs: url => dispatch(addBreadCrumbs(url))
  };
};

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(PropertTaxNotification);