import { PrivateRoute,BreadCrumb,AppContainer,BackButton, CloseSvg } from "@upyog/digit-ui-react-components";
import React from "react";
import { useTranslation } from "react-i18next";
import { Link, Switch, useLocation } from "react-router-dom";
import SearchApp from "./SearchApp";

export const VendorBreadCrumb = ({ location }) => {
  const { t } = useTranslation();
  const isVendor = location?.pathname?.includes("vendor");
  const isSearchVendor = location?.pathname?.includes("search-vendor");
  const isRegistry = location?.pathname?.includes("registry");
  const isNewVendor = location?.pathname?.includes("new-vendor");
  const isNewVehicle = location?.pathname?.includes("new-vehicle");
  const isNewDriver = location?.pathname?.includes("new-driver");

  const crumbs = [
    {
      path: "/upyog-ui/employee",
      content: t("ES_COMMON_HOME"),
      show: isVendor,
    },
    {
      path: "/upyog-ui/employee/vendor/search-vendor?selectedTabs=VENDOR",
      content: "VENDOR",
      show: isVendor,
    },
    {
      content: isNewVendor
        ? t("ES_FSM_REGISTRY_TITLE_NEW_VENDOR")
        : isNewVehicle
        ? t("ES_FSM_REGISTRY_TITLE_NEW_VEHICLE")
        : isNewDriver
        ? t("ES_FSM_REGISTRY_TITLE_NEW_DRIVER")
        : null,
      show: isRegistry && (isNewVendor || isNewVehicle || isNewDriver),
    },
  ];

  return <BreadCrumb crumbs={crumbs} />;
};

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
                 <div style={{ marginLeft: "-4px" }}>
          <VendorBreadCrumb location={location} />
        </div>         
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
