import {
  FormComposer, Toast
} from "@nudmcdgnpm/digit-ui-react-components";
import PropTypes from "prop-types";
import React, { useEffect, useState } from "react";
import SelectOtp from "../../citizen/Login/SelectOtp";

const ChangePasswordComponent = ({ config: propsConfig, t }) => {
  const [user, setUser] = useState(null);
  const { mobile_number: mobileNumber, tenantId } = Digit.Hooks.useQueryParams();
  const navigate = Digit.Hooks.useCustomNavigate();
  const [otp, setOtp] = useState("");
  const [isOtpValid, setIsOtpValid] = useState(true);
  const [showToast, setShowToast] = useState(null);
  const getUserType = () => Digit.UserService.getType();
  
  useEffect(() => {
    if (!user) {
      Digit.UserService.setType("employee");
      return;
    }
    Digit.UserService.setUser(user);
    const redirectPath = location.state?.from || "/upyog-ui/employee";
    navigate(redirectPath, { replace: true });
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

      await Digit.UserService.changePassword(requestData, tenantId);
      navigateToLogin();
    } catch (err) {
      setShowToast(err?.response?.data?.error?.fields?.[0]?.message || t("ES_SOMETHING_WRONG"));
      setTimeout(closeToast, 5000);
    }
  };

  const navigateToLogin = () => {
    navigate("/upyog-ui/employee/user/login", { replace: true });
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
          label: "Enter OTP",
          type: "custom",
          populators: {
            name: "Enter OTP",
            component: () => (
              <SelectOtp t={t} userType="employee" otp={otp} onOtpChange={setOtp} error={isOtpValid} onResend={onResendOTP} />
            ),
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

  const footer = showToast && <Toast error={true} label={t(showToast)} onClose={closeToast} />;

  return (
    <>
      {footer}
      <FormComposer
        onSubmit={onChangePassword}
        noBoxShadow
        inline
        submitInForm
        config={config}
        label={propsConfig.texts.submitButtonLabel}
        cardStyle={{ margin: "0 auto", width: "100%", maxWidth: "408px" }}
        cardClassName="loginFormStyleEmployee"
        buttonStyle={{ maxWidth: "100%", width: "100%", backgroundColor: "#5a1166" }}
        className="employeeChangePassword"
      >
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
    </>
  );
};

ChangePasswordComponent.propTypes = {
  loginParams: PropTypes.any,
};

ChangePasswordComponent.defaultProps = {
  loginParams: null,
};

export default ChangePasswordComponent;