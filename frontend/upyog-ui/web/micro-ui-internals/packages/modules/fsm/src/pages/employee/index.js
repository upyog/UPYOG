import {
  BreadCrumb,
  ShippingTruck,
  EmployeeModuleCard,
  PrivateRoute,
  BackButton,
  AddNewIcon,
  ViewReportIcon,
  InboxIcon,
  ULBHomeCard,
} from "@nudmcdgnpm/digit-ui-react-components";
import React, { Fragment, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useLocation, Routes, Route } from "react-router-dom";
import FstpAddVehicle from "./FstpAddVehicle";
import FstpOperations from "./FstpOperations";
import FstpServiceRequest from "./FstpServiceRequest";

export const FsmBreadCrumb = ({ location }) => {
  const { t } = useTranslation();
  const DSO = Digit.UserService.hasAccess(["FSM_DSO"]);
  const FSTPO = Digit.UserService.hasAccess(["FSM_EMP_FSTPO"]);
  const isApplicationDetails = location?.pathname?.includes("application-details");
  const isVehicleLog = location?.pathname?.includes("fstp-operator-details");
  const isInbox = location?.pathname?.includes("inbox");
  const isFsm = location?.pathname?.includes("fsm");
  const isSearch = location?.pathname?.includes("search");
  const isRegistry = location?.pathname?.includes("registry");
  const isVendorDetails = location?.pathname?.includes("vendor-details");
  const isVendorEdit = location?.pathname?.includes("modify-vendor");
  const isNewApplication = location?.pathname?.includes("new-application");
  const isVehicleDetails = location?.pathname?.includes("vehicle-details");
  const isVehicleEdit = location?.pathname?.includes("modify-vehicle");
  const isDriverDetails = location?.pathname?.includes("driver-details");
  const isDriverEdit = location?.pathname?.includes("modify-driver");
  const isModifyApplication = location?.pathname?.includes("modify-application");
  const isNewVendor = location?.pathname?.includes("new-vendor");
  const isNewVehicle = location?.pathname?.includes("new-vehicle");
  const isNewDriver = location?.pathname?.includes("new-driver");

  const [search, setSearch] = useState(false);
  const [id, setId] = useState(false);

  useEffect(() => {
    if (!search) {
      setSearch(isSearch);
    } else if (isFsm || (isInbox && search)) {
      setSearch(false);
    }
    if (location?.pathname) {
      let path = location?.pathname.split("/");
      let id = path[path.length - 1];
      setId(id);
    }
  }, [location]);

  const crumbs = [
    {
      path: DSO ? "/upyog-ui/citizen/fsm/dso-dashboard" : "/upyog-ui/employee",
      content: t("ES_COMMON_HOME"),
      show: isFsm,
    },
    {
      path: isRegistry ? "/upyog-ui/employee/fsm/registry?selectedTabs=VENDOR" : FSTPO ? "/upyog-ui/employee/fsm/fstp-inbox" : "/upyog-ui/employee",
      content: isVehicleLog ? t("ES_TITLE_INBOX") : "FSM",
      show: isFsm,
    },
    {
      path: isNewApplication ? "/upyog-ui/employee/fsm/new-application" : "",
      content: t("FSM_NEW_DESLUDGING_APPLICATION"),
      show: isFsm && isNewApplication,
    },
    {
      path: "",
      content: `${t("FSM_SUCCESS")}`,
      show: location.pathname.includes("/employee/fsm/response") ? true : false,
    },
    {
      path: isInbox || isSearch || isApplicationDetails ? "/upyog-ui/employee/fsm/inbox" : "",
      content: t("ES_TITLE_INBOX"),
      show: (isFsm && isInbox) || isSearch || isApplicationDetails,
    },
    {
      path: "/upyog-ui/employee/fsm/search",
      content: t("ES_TITILE_SEARCH_APPLICATION"),
      show: search,
    },
    { content: t("ES_TITLE_APPLICATION_DETAILS"), show: isApplicationDetails },
    { content: t("ES_TITLE_VEHICLE_LOG"), show: isVehicleLog },
    {
      path: "/upyog-ui/employee/fsm/registry/vendor-details/" + id,
      content: t("ES_TITLE_VENDOR_DETAILS"),
      show: isRegistry && (isVendorDetails || isVendorEdit),
    },
    {
      path: "/upyog-ui/employee/fsm/registry/vehicle-details/" + id,
      content: t("ES_TITLE_VEHICLE_DETAILS"),
      show: isRegistry && (isVehicleDetails || isVehicleEdit),
    },
    {
      path: "/upyog-ui/employee/fsm/registry/driver-details/" + id,
      content: t("ES_TITLE_DRIVER_DETAILS"),
      show: isRegistry && (isDriverDetails || isDriverEdit),
    },
    { content: t("ES_TITLE_VENDOR_EDIT"), show: isRegistry && (isVendorEdit || isVehicleEdit || isDriverEdit) },
    {
      path: "upyog-ui/employee/fsm/modify-application/" + id,
      content: t("ES_FSM_APPLICATION_UPDATE"),
      show: isModifyApplication,
    },
    {
      content: isNewVendor
        ? t("ES_FSM_ACTION_CREATE_VENDOR")
        : isNewVehicle
        ? t("ES_FSM_REGISTRY_DETAILS_TYPE_VEHICLE")
        : isNewDriver
        ? t("ES_FSM_REGISTRY_DETAILS_TYPE_DRIVER")
        : null,
      show: isRegistry && (isNewVendor || isNewVehicle || isNewDriver),
    },
  ];

  return <BreadCrumb crumbs={crumbs} />;
};

const EmployeeApp = ({ path, url, userType }) => {
  const { t } = useTranslation();
  const location = useLocation();
  const DSO = Digit.UserService.hasAccess(["FSM_DSO"]);
  const COLLECTOR = Digit.UserService.hasAccess("FSM_COLLECTOR") || false;
  const FSM_ADMIN = Digit.UserService.hasAccess("FSM_ADMIN") || false;
  const FSM_EDITOR = Digit.UserService.hasAccess("FSM_EDITOR_EMP") || false;
  const FSM_CREATOR = Digit.UserService.hasAccess("FSM_CREATOR_EMP") || false;

  const moduleForSomeFSMEmployees =
    !DSO && !COLLECTOR && !FSM_EDITOR
      ? [
          {
            link: "/upyog-ui/employee/fsm/new-application",
            name: "FSM_NEW_DESLUDGING_APPLICATION",
            icon: <AddNewIcon />,
          },
        ]
      : [];

  const moduleForSomeFSMAdmin = FSM_ADMIN
    ? [
        {
          link: "/upyog-ui/employee/fsm/registry",
          name: "ES_TITLE_FSM_REGISTRY",
          icon: <AddNewIcon />,
        },
      ]
    : [];

  const module = [
    ...moduleForSomeFSMEmployees,
    {
      link: "/upyog-ui/employee/fsm/inbox",
      name: "ES_COMMON_INBOX",
      icon: <InboxIcon />,
    },
    {
      link: "/employee/report/fsm/FSMDailyDesludingReport",
      hyperlink: true,
      name: "ES_FSM_VIEW_REPORTS_BUTTON",
      icon: <ViewReportIcon />,
    },
    ...moduleForSomeFSMAdmin,
  ];

  useEffect(() => {
    if (!location?.pathname?.includes("application-details")) {
      if (!location?.pathname?.includes("inbox")) {
        Digit.SessionStorage.del("fsm/inbox/searchParams");
      } else if (!location?.pathname?.includes("search")) {
        Digit.SessionStorage.del("fsm/search/searchParams");
      }
    }
  }, [location]);

  const Inbox = Digit.ComponentRegistryService.getComponent("FSMEmpInbox");
  const FstpInbox = Digit.ComponentRegistryService.getComponent("FSMFstpInbox");
  const NewApplication = Digit.ComponentRegistryService.getComponent("FSMNewApplicationEmp");
  const EditApplication = Digit.ComponentRegistryService.getComponent("FSMEditApplication");
  const EmployeeApplicationDetails = Digit.ComponentRegistryService.getComponent("FSMEmployeeApplicationDetails");
  const FstpOperatorDetails = Digit.ComponentRegistryService.getComponent("FSMFstpOperatorDetails");
  const Response = Digit.ComponentRegistryService.getComponent("FSMResponse");
  const ApplicationAudit = Digit.ComponentRegistryService.getComponent("FSMApplicationAudit");
  const RateView = Digit.ComponentRegistryService.getComponent("FSMRateView");
  const FSMLinks = Digit.ComponentRegistryService.getComponent("FSMLinks");
  const FSTPO = Digit.UserService.hasAccess(["FSM_EMP_FSTPO"]);
  const FSMRegistry = Digit.ComponentRegistryService.getComponent("FSMRegistry");
  const VendorDetails = Digit.ComponentRegistryService.getComponent("VendorDetails");
  const AddVendor = Digit.ComponentRegistryService.getComponent("AddVendor");
  const EditVendor = Digit.ComponentRegistryService.getComponent("EditVendor");
  const VehicleDetails = Digit.ComponentRegistryService.getComponent("VehicleDetails");
  const AddVehicle = Digit.ComponentRegistryService.getComponent("AddVehicle");
  const EditVehicle = Digit.ComponentRegistryService.getComponent("EditVehicle");
  const DriverDetails = Digit.ComponentRegistryService.getComponent("DriverDetails");
  const AddDriver = Digit.ComponentRegistryService.getComponent("AddDriver");
  const EditDriver = Digit.ComponentRegistryService.getComponent("EditDriver");
  const BreadCrumbComp = Digit.ComponentRegistryService.getComponent("FsmBreadCrumb");
  const EnhancedReport = Digit?.ComponentRegistryService?.getComponent("EnhancedReport");


  const locationCheck =
    window.location.href.includes("/employee/fsm/inbox") ||
    window.location.href.includes("/employee/fsm/registry") ||
    window.location.href.includes("/employee/fsm/application-details/");

  const desludgingApplicationCheck = window.location.href.includes("/employee/fsm/new-application") || window.location.href.includes("/employee/fsm/modify-application");
  return (
    <React.Fragment>
      <div className="ground-container">
        {FSTPO ? (
          <BackButton
            isCommonPTPropertyScreen={location.pathname.includes("new") ? true : false}
            getBackPageNumber={location.pathname.includes("new") ? () => -2 : null}
          >
            {t("CS_COMMON_BACK")}
          </BackButton>
        ) : (
          <div style={locationCheck ? { marginLeft: "-4px" } : desludgingApplicationCheck ? { marginLeft: "12px" } : { marginLeft: "20px" }}>
            <BreadCrumbComp location={location} />
          </div>
        )}
        <Routes>
          <Route path={`*`} element={<PrivateRoute><FSMLinks matchPath={path} userType={userType} /></PrivateRoute>} />
          <Route path={`/inbox`} element={<PrivateRoute><Inbox parentRoute={path} isInbox={true} /></PrivateRoute>} />
          <Route path={`/fstp-inbox`} element={<PrivateRoute><FstpInbox parentRoute={path} /></PrivateRoute>} />
          <Route path={`/new-application`} element={<PrivateRoute><NewApplication parentUrl={url} /></PrivateRoute>} />
          <Route path={`/modify-application/:id`} element={<PrivateRoute><EditApplication /></PrivateRoute>} />
          <Route
            path={`/application-details/:id`}
            element={
              <PrivateRoute>
                <EmployeeApplicationDetails parentRoute={path} userType="EMPLOYEE" />
              </PrivateRoute>
            }
          />
          <Route path={`/fstp-operator-details/:id`} element={<PrivateRoute><FstpOperatorDetails /></PrivateRoute>} />
          <Route path={`/response`} element={<PrivateRoute><Response parentRoute={path} /></PrivateRoute>} />
<Route path={`/application-audit/:id`} element={<PrivateRoute><ApplicationAudit parentRoute={path} /></PrivateRoute>} />
<Route path={`/search`} element={<PrivateRoute><Inbox parentRoute={path} isSearch={true} /></PrivateRoute>} />
<Route path={`/rate-view/:id`} element={<PrivateRoute><RateView parentRoute={path} /></PrivateRoute>} />
<Route path={`/mark-for-disposal`} element={<PrivateRoute><div /></PrivateRoute>} />
<Route path={`/registry`} element={<PrivateRoute><FSMRegistry parentRoute={path} /></PrivateRoute>} />
<Route path={`/registry/vendor-details/:id`} element={<PrivateRoute><VendorDetails parentRoute={path} /></PrivateRoute>} />
<Route path={`/registry/new-vendor`} element={<PrivateRoute><AddVendor parentRoute={path} /></PrivateRoute>} />
<Route path={`/registry/modify-vendor/:id`} element={<PrivateRoute><EditVendor parentRoute={path} /></PrivateRoute>} />
<Route path={`/registry/vehicle-details/:id`} element={<PrivateRoute><VehicleDetails parentRoute={path} /></PrivateRoute>} />
<Route path={`/registry/new-vehicle`} element={<PrivateRoute><AddVehicle parentRoute={path} /></PrivateRoute>} />
<Route path={`/registry/modify-vehicle/:id`} element={<PrivateRoute><EditVehicle parentRoute={path} /></PrivateRoute>} />
<Route path={`/registry/driver-details/:id`} element={<PrivateRoute><DriverDetails parentRoute={path} /></PrivateRoute>} />
<Route path={`/registry/new-driver`} element={<PrivateRoute><AddDriver parentRoute={path} /></PrivateRoute>} />
<Route path={`/registry/modify-driver/:id`} element={<PrivateRoute><EditDriver parentRoute={path} /></PrivateRoute>} />
<Route path={`/fstp-operations`} element={<PrivateRoute><FstpOperations /></PrivateRoute>} />
<Route path={`/fstp-add-vehicle`} element={<PrivateRoute><FstpAddVehicle /></PrivateRoute>} />
<Route path={`/fstp-fsm-request/:id`} element={<PrivateRoute><FstpServiceRequest /></PrivateRoute>} />
<Route path={`/home`} element={<PrivateRoute><ULBHomeCard module={module} /></PrivateRoute>} />
<Route path={`/fstp/new-vehicle-entry`} element={<PrivateRoute><FstpOperatorDetails /></PrivateRoute>} />
          <Route path={`/fstp/new-vehicle-entry/:id`} element={<PrivateRoute><FstpOperatorDetails /></PrivateRoute>} />

<Route path="FSMDailyCollectionReport/*" element={<PrivateRoute><EnhancedReport parentRoute={path} moduleName="fsm" reportName="FSMDailyCollectionReport" /></PrivateRoute>} />
<Route path="FSMRequestReport/*" element={<PrivateRoute><EnhancedReport parentRoute={path} moduleName="fsm" reportName="FSMRequestReport" /></PrivateRoute>} />
<Route path="TQMTest/*" element={<PrivateRoute><EnhancedReport parentRoute={path} moduleName="fsm" reportName="TQMTest" /></PrivateRoute>} />


        </Routes>
      </div>
    </React.Fragment>
  );
};

export default EmployeeApp;
