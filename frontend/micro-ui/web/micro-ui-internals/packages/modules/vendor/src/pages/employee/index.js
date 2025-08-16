import { PrivateRoute,BreadCrumb,AppContainer,BackButton, CloseSvg } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { useTranslation } from "react-i18next";
import { Link, Switch, useLocation } from "react-router-dom";
import SearchApp from "./SearchApp";

const EmployeeApp = ({ path, url, userType }) => {
  console.log("tttttttttttt",path)
  const { t } = useTranslation();
  const location = useLocation();
  const mobileView = innerWidth <= 640;
  sessionStorage.removeItem("revalidateddone");
  const isMobile = window.Digit.Utils.browser.isMobile();

  const inboxInitialState = {
    // searchParams: {
    //   uuid: { code: "ASSIGNED_TO_ALL", name: "ES_INBOX_ASSIGNED_TO_ALL" },
    //   services: ["asset-create"],
    //   applicationStatus: [],
    //   locality: [],

    // },
  };

  
 

  // const AssetBreadCrumbs = ({ location }) => {
  //   const { t } = useTranslation();
  //   const search = useLocation().search;
  //   const fromScreen = new URLSearchParams(search).get("from") || null;
  //   const { from : fromScreen2 } = Digit.Hooks.useQueryParams();
  //   const crumbs = [
  //     {
  //       path: "/digit-ui/employee",
  //       content: t("ES_COMMON_HOME"),
  //       show: true,
  //     },
  //     {
  //       path: "/digit-ui/employee/asset/assetservice/inbox",
  //       content: t("ES_TITLE_INBOX"),
  //       show: location.pathname.includes("asset/assetservice/inbox") ? false : false,
  //     },
  //   ];
  //   return <BreadCrumb style={isMobile?{display:"flex"}:{margin: "0 0 4px", color:"#000000" }}  spanStyle={{maxWidth:"min-content"}} crumbs={crumbs} />;
  // }

  console.log("index page in employee")
  //const Create = Digit?.ComponentRegistryService?.getComponent("VENDOREMPCreate");
  const AddVendor = Digit.ComponentRegistryService.getComponent("AddVendor");
  const SearchVendor = Digit.ComponentRegistryService.getComponent("SearchVendor");
  //const SearchApp = Digit.ComponentRegistryService.getComponent("SearchApp");
  const AddDriver = Digit.ComponentRegistryService.getComponent("AddDriver");
  const EditVendorDetails = Digit.ComponentRegistryService.getComponent("EditVendorDetails");
  const AddVehicle = Digit.ComponentRegistryService.getComponent("AddVehicle");
  const VendorCreate =  Digit.ComponentRegistryService.getComponent("VENDORCreate");
  const DriverDetails = Digit.ComponentRegistryService.getComponent("DriverDetails");
  const VehicleDetails = Digit.ComponentRegistryService.getComponent("VehicleDetails");

  return (
    <Switch>
      <AppContainer>
      <React.Fragment>
       <div className="ground-container">
         {/* 
        {!isRes ? 
              <div style={isNewRegistration ? { marginLeft: "12px",display: "flex", alignItems: "center" } : { marginLeft: "-4px",display: "flex", alignItems: "center" }}>
                  <BackButton location={location} />
                  <span style={{ margin: "0 5px 16px", display: "inline-block" }}>|</span>
                  <AssetBreadCrumbs location={location} />
               
              </div>
          : null}
          <PrivateRoute exact path={`${path}/`} component={() => <ASSETLinks matchPath={path} userType={userType} />} />
          */}
          {/* <PrivateRoute path={`${path}/additional`} component={Create} /> */}
          <PrivateRoute path={`${path}/registry/new-vendor`} component={() => <AddVendor parentRoute={path} />} />
          <PrivateRoute path={`${path}/search-vendor`} component={() => <SearchVendor parentRoute={path} />} />
          <PrivateRoute path={`${path}/registry/new-driver`} component={() => <AddDriver parentRoute={path} />} />
          <PrivateRoute path={`${path}/registry/vendor-details/:id`} component={() => <EditVendorDetails parentRoute={path} />} />
          <PrivateRoute path={`${path}/registry/vehicle-details/:id`} component={() => <VehicleDetails parentRoute={path} />} />
          <PrivateRoute path={`${path}/registry/new-vehicle`} component={() => <AddVehicle parentRoute={path} />} />
          <PrivateRoute path={`${path}/registry/additionaldetails`} component={() => <VendorCreate parentRoute={path} />} />
          <PrivateRoute path={`${path}/registry/driver-details`} component={() => <DriverDetails parentRoute={path} />} />
          <PrivateRoute path={`${path}/common-search/:id`} component={(props) => <SearchApp {...props} parentRoute={path} />} />

          
          {/* <PrivateRoute path={`${path}/new-application`} component={(props) => <Create {...props} parentRoute={path} />} /> */}

        </div> 
      </React.Fragment>
      </AppContainer>
    </Switch>
  );
};

export default EmployeeApp;
