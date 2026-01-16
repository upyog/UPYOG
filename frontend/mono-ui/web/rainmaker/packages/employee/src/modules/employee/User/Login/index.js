import React from "react";
import formHoc from "egov-ui-kit/hocs/form";
import { Banner } from "modules/common";
import LoginForm from "./components/LoginForm";
import { connect } from "react-redux";
import get from "lodash/get";
import { banner1, banner2, banner3, banner4 } from "egov-ui-kit/common/common/Header/components/AppBar/bannerImages";

const LoginFormHOC = formHoc({ formKey: "employeeLogin" })(LoginForm);

const Login = ({ bannerUrl, logoUrl }) => {
  let allImages = [banner1, banner2, banner3, banner4];
  return (
    <Banner hideBackButton={false} bannerUrl={allImages} logoUrl={logoUrl}>
      <LoginFormHOC logoUrl={logoUrl} />
    </Banner>
  );
};

const mapStateToProps = ({ common }) => {
  const { stateInfoById } = common;
  let bannerUrl = get(stateInfoById, "0.bannerUrl");
  let logoUrl = get(stateInfoById, "0.logoUrl");
  return { bannerUrl, logoUrl };
};

export default connect(
  mapStateToProps,
  null
)(Login);
