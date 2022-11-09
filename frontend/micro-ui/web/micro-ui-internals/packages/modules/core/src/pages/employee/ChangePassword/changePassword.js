import {
  BackButton, CardSubHeader, CardText, FormComposer, Toast
} from "@egovernments/digit-ui-react-components";
import PropTypes from "prop-types";
import React, { useEffect, useState } from "react";
import { useHistory } from "react-router-dom";
import Background from "../../../components/Background";
import Header from "../../../components/Header";
import SelectOtp from "../../citizen/Login/SelectOtp";

const ChangePasswordComponent = ({ config: propsConfig, t }) => {
  const [user, setUser] = useState(null);
  const { mobile_number: mobileNumber, tenantId } = Digit.Hooks.useQueryParams();
  const history = useHistory();
  const [otp, setOtp] = useState("");
  const [isOtpValid, setIsOtpValid] = useState(true);
  const [showToast, setShowToast] = useState(null);
  const getUserType = () => Digit.UserService.getType();
  let sourceUrl = "https://s3.ap-south-1.amazonaws.com/egov-qa-assets";
  const pdfUrl = "https://pg-egov-assets.s3.ap-south-1.amazonaws.com/Upyog+Code+and+Copyright+License_v1.pdf";
  
  useEffect(() => {
    if (!user) {
      Digit.UserService.setType("employee");
      return;
    }
    Digit.UserService.setUser(user);
    const redirectPath = location.state?.from || "/digit-ui/employee";
    history.replace(redirectPath);
  }, [user]);

  const closeToast = () => {
    setShowToast(null);
  };

  const onResendOTP = async () => {
    const requestData = {
      otp: {
        mobileNumber,
        userType: getUserType().toUpperCase(),
        type: "passwordreset",
        tenantId,
      },
    };

    try {
      await Digit.UserService.sendOtp(requestData, tenantId);
      setShowToast(t("ES_OTP_RESEND"));
    } catch (err) {
      setShowToast(err?.response?.data?.error_description || t("ES_INVALID_LOGIN_CREDENTIALS"));
    }
    setTimeout(closeToast, 5000);
  };

  const onChangePassword = async (data) => {
    try {
      if (data.newPassword !== data.confirmPassword) {
        return setShowToast(t("ERR_PASSWORD_DO_NOT_MATCH"));
      }
      const requestData = {
        ...data,
        otpReference: otp,
        tenantId,
        type: getUserType().toUpperCase(),
      };

      const response = await Digit.UserService.changePassword(requestData, tenantId);
      navigateToLogin();
    } catch (err) {
      setShowToast(err?.response?.data?.error?.fields?.[0]?.message || t("ES_SOMETHING_WRONG"));
      setTimeout(closeToast, 5000);
    }
  };

  const navigateToLogin = () => {
    history.replace("/digit-ui/employee/user/login");
  };

  const [username, password, confirmPassword] = propsConfig.inputs;
  const config = [
    {
      body: [
        {
          label: t(username.label),
          type: username.type,
          populators: {
            name: username.name,
          },
          isMandatory: true,
        },
        {
          label: t(password.label),
          type: password.type,
          populators: {
            name: password.name,
          },
          isMandatory: true,
        },
        {
          label: t(confirmPassword.label),
          type: confirmPassword.type,
          populators: {
            name: confirmPassword.name,
          },
          isMandatory: true,
        },
      ],
    },
  ];

  return (
    <Background>
      <div className="employeeBackbuttonAlign">
        <BackButton variant="white" style={{ borderBottom: "none" }} />
      </div>
      <FormComposer
        onSubmit={onChangePassword}
        noBoxShadow
        inline
        submitInForm
        config={config}
        label={propsConfig.texts.submitButtonLabel}
        cardStyle={{ maxWidth: "408px", margin: "auto" }}
        className="employeeChangePassword"
      >
        <Header />
        <CardSubHeader style={{ textAlign: "center" }}> {propsConfig.texts.header} </CardSubHeader>
        <CardText>
          {`${t(`CS_LOGIN_OTP_TEXT`)} `}
          <b>
            {" "}
            {`${t(`+ 91 - `)}`} {mobileNumber}
          </b>
        </CardText>
        <SelectOtp t={t} userType="employee" otp={otp} onOtpChange={setOtp} error={isOtpValid} onResend={onResendOTP} />
        {/* <div>
          <CardLabel style={{ marginBottom: "8px" }}>{t("CORE_OTP_SENT_MESSAGE")}</CardLabel>
          <CardLabelDesc style={{ marginBottom: "0px" }}> {mobileNumber} </CardLabelDesc>
          <CardLabelDesc style={{ marginBottom: "8px" }}> {t("CORE_EMPLOYEE_OTP_CHECK_MESSAGE")}</CardLabelDesc>
        </div>
        <CardLabel style={{ marginBottom: "8px" }}>{t("CORE_OTP_OTP")} *</CardLabel>
        <TextInput className="field" name={otpReference} isRequired={true} onChange={updateOtp} type={"text"} style={{ marginBottom: "10px" }} />
        <div className="flex-right">
          <div className="primary-label-btn" onClick={onResendOTP}>
            {t("CORE_OTP_RESEND")}
          </div>
        </div> */}
      </FormComposer>
      {showToast && <Toast error={true} label={t(showToast)} onClose={closeToast} />}
      {/* <div style={{ width: '100%',position: "absolute", bottom: 0 }}>
        <div style={{ display: 'flex', justifyContent: 'center', color:"white" }}>
          <img style={{ cursor: "pointer", display: "inline-flex", height: '1.4em' }} alt={"Powered by DIGIT"} src={`${sourceUrl}/digit-footer-bw.png`} onError={"this.src='./../digit-footer.png'"} onClick={() => {
            window.open('https://www.digit.org/', '_blank').focus();
          }}></img>
          <span style={{ margin: "0 10px" }}>|</span>
          <span style={{ cursor: "pointer", fontSize: "16px", fontWeight: "400"}} onClick={() => { window.open('https://niua.in/', '_blank').focus();}} >Copyright © 2022 National Institute of Urban Affairs</span>
          <span style={{ margin: "0 10px" }}>|</span>
          <a style={{ cursor: "pointer", fontSize: "16px", fontWeight: "400"}} href={pdfUrl} target='_blank'>UPYOG License</a>
        </div>
      </div> */}
    </Background>
  );
};

ChangePasswordComponent.propTypes = {
  loginParams: PropTypes.any,
};

ChangePasswordComponent.defaultProps = {
  loginParams: null,
};

export default ChangePasswordComponent;
