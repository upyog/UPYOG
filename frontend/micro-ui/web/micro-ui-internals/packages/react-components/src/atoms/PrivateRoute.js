import React from "react";
import { Route, Redirect } from "react-router-dom";

export const PrivateRoute = ({ component: Component, roles, ...rest }) => {
  console.log("component",Component,roles)
  return (
    <Route
      {...rest}
      render={(props) => {
        const user = Digit.UserService.getUser();
        const userType = Digit.UserService.getType();
        function getLoginRedirectionLink (){
          if(userType === "employee"){
            return "/digit-ui/employee/user/login"
          }
          else{
            return "/digit-ui/citizen/login"
          }
        }
        console.log("props.location.pathname",props.location.pathname,getLoginRedirectionLink())
        if (!user || !user.access_token) {
         
          // not logged in so redirect to login page with the return url
          return <Redirect to={{ pathname: getLoginRedirectionLink(), state: { from: props.location.pathname + props.location.search } }} />;
        }

        // logged in so return component
        return <Component {...props} />;
      }}
    />
  );
};
