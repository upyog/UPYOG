import { Dropdown, FormComposer, Loader, Toast } from "@nudmcdgnpm/digit-ui-react-components";
import PropTypes from "prop-types";
import React, { useEffect, useState } from "react";

const ForgotPassword = ({ layout, config: propsConfig, t, emp = {}, setEmp = () => {} }) => {
  const { data: cities, isLoading } = Digit.Hooks.useTenants();
  const [user, setUser] = useState(null);
  const navigate = Digit.Hooks.useCustomNavigate();
  const [showToast, setShowToast] = useState(null);
  const {isEmployeeV2 = false} = layout?.loginUI
  
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

  const onForgotPassword = async (data) => {
      navigate(`/upyog-ui/employee/user/change-password?mobile_number=${emp.mobileNumber}&tenantId=${emp.city.city.code}`);

    // if (!data.city) {
    //   alert("Please Select City!");
    //   return;
    // }
    // const requestData = {
    //   otp: {
    //     mobileNumber: data.mobileNumber,
    //     userType: "citizen",
    //     type: "passwordreset",
    //     tenantId: data.city.code,
    //   },
    // };
    // try {
    //   await Digit.UserService.sendOtp(requestData, data.city.code);
    //   navigate(`/upyog-ui/employee/user/change-password?mobile_number=${data.mobileNumber}&tenantId=${data.city.code}`);
    // } catch (err) {
    //   setShowToast(err?.response?.data?.error?.fields?.[0]?.message || "Invalid login credentials!");
    //   setTimeout(closeToast, 5000);
    // }
  };

  const navigateToLogin = () => {
    navigate("/upyog-ui/employee/user/login", { replace: true });
  };
  console.log("forget", emp);
  
  const [userId, city] = propsConfig.inputs;
  const config = [
    {
      body: [
        {
          label: t(userId.label),
          type: userId.type,
          populators: {
            name: userId.name,
            placeholder: "Enter Number",
            onChange: (e) => setEmp({...emp, [userId.name]: e.target.value }),
            componentInFront: "+91",
            customProps: {
              classes: "forgetPassword"
            },
          },
          isMandatory: true,
        },
        {
          label: t(city.label),
          type: city.type,
          populators: {
            name: city.name,
            customProps: {},
            component: (props, customProps) => (
              <Dropdown
                option={cities}
                optionKey="name"
                id={city.name}
                placeholder={"Select City"}
                className="login-city-dd forgetPasswordDropdown"
                select={(d) => {
                  setEmp({...emp, city: d})
                }}
                {...customProps}
              />
            ),
          },
          isMandatory: true,
        },
      ],
    },
  ];

  if (isLoading) {
    return <Loader />;
  }

  const footer = showToast && <Toast error={true} label={t(showToast)} onClose={closeToast} />;

  return (
    <>
      {footer}
      <FormComposer
        onSubmit={onForgotPassword}
        noBoxShadow
        inline
        submitInForm
        config={config}
        label={propsConfig.texts.submitButtonLabel}
        secondaryActionLabel={propsConfig.texts.secondaryButtonLabel}
        onSecondayActionClick={navigateToLogin}
        heading={propsConfig.texts.header}
        description={propsConfig.texts.description}
        headingStyle={{ textAlign: "center" }}
        cardStyle={{ margin: "0 auto", width: "100%", maxWidth: "408px", marginTop: isEmployeeV2 ? "160px" : "auto" }}
        cardClassName="loginFormStyleEmployee"
        buttonStyle={{ maxWidth: "100%", width: "100%", backgroundColor: "#5a1166" }}
        className="employeeForgotPassword"
      />
    </>
  );
};

ForgotPassword.propTypes = {
  loginParams: PropTypes.any,
};

ForgotPassword.defaultProps = {
  loginParams: null,
};

export default ForgotPassword;