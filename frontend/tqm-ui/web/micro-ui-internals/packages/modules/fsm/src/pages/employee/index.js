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
} from "@egovernments/digit-ui-react-components";
import React, { Fragment, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { Switch, useLocation } from "react-router-dom";
import FstpAddVehicle from "./FstpAddVehicle";
import FstpOperations from "./FstpOperations";
import FstpServiceRequest from "./FstpServiceRequest";
import Inbox from "./Inbox";

export const FsmBreadCrumb = ({ location, defaultPath }) => {
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
  const isAddWorker = location?.pathname?.includes("new-worker");
  const isEditWorker = location?.pathname?.includes("edit-worker");
  const isWorkerDetails = location?.pathname?.includes("worker-details");
  const pathVar = location.pathname.replace(defaultPath + "/", "").split("?")?.[0];
  const [search, setSearch] = useState(false);
  const [id, setId] = useState(false);
  const searchParams = new URLSearchParams(location.search);
  const paramId = searchParams.get("id");
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
      path: DSO ? `/${window?.contextPath}/citizen/fsm/dso-dashboard` : `/${window?.contextPath}/employee`,
      content: t("ES_COMMON_HOME"),
      show: isFsm,
    },
    {
      path:
        isVendorDetails || isVehicleDetails || isWorkerDetails || isAddWorker || isNewVehicle || isNewVendor || isVendorEdit || isEditWorker || isVehicleEdit
          ? `/${window?.contextPath}/employee/fsm/registry`
          : isRegistry
          ? null
          : FSTPO
          ? `/${window?.contextPath}/employee/fsm/fstp-inbox`
          : `/${window?.contextPath}/employee`,
      query: isVehicleDetails ? "selectedTabs=VEHICLE" : isWorkerDetails ? "selectedTabs=WORKER" : isVendorDetails ? "selectedTabs=VENDOR" : "",
      content: isVehicleLog ? t("ES_TITLE_INBOX") : "FSM",
      show: isFsm,
      isBack: false,
    },
    {
      path: isNewApplication ? "" : `/${window?.contextPath}/employee/fsm/new-application`,
      content: t("FSM_NEW_DESLUDGING_APPLICATION"),
      show: isFsm && isNewApplication,
    },
    {
      path: "",
      content: `${t("FSM_SUCCESS")}`,
      show: location.pathname.includes("/employee/fsm/response") ? true : false,
    },
    {
      path: isInbox ? "" : isSearch || isApplicationDetails ? `/${window?.contextPath}/employee/fsm/inbox` : "",
      content: t("ES_TITLE_INBOX"),
      show: (isFsm && isInbox) || isSearch || isApplicationDetails,
    },
    {
      path: pathVar === "search" ? "" : `/${window?.contextPath}/employee/fsm/search`,
      content: t("ES_TITILE_SEARCH_APPLICATION"),
      show: search,
    },
    { content: t("ES_TITLE_APPLICATION_DETAILS"), show: isApplicationDetails },
    { content: t("ES_TITLE_VEHICLE_LOG"), show: isVehicleLog },
    {
      path: isVendorDetails ? null : `/${window?.contextPath}/employee/fsm/registry/vendor-details/` + id,
      content: t("ES_TITLE_VENDOR_DETAILS"),
      show: isRegistry && (isVendorDetails || isVendorEdit),
    },
    {
      path: isVehicleDetails ? null : `/${window?.contextPath}/employee/fsm/registry/vehicle-details/` + id,
      content: t("ES_TITLE_VEHICLE_DETAILS"),
      show: isRegistry && (isVehicleDetails || isVehicleEdit),
    },
    {
      path: isDriverDetails ? null : `/${window?.contextPath}/employee/fsm/registry/driver-details/` + id,
      content: t("ES_TITLE_DRIVER_DETAILS"),
      show: isRegistry && (isDriverDetails || isDriverEdit),
    },
    {
      path: isWorkerDetails ? null : `/${window?.contextPath}/employee/fsm/registry/worker-details`,
      query: `id=${paramId}`,
      content: t("ES_TITLE_WORKER_DETAILS"),
      show: isRegistry && (isWorkerDetails || isEditWorker),
    },
    {
      content: t("ES_TITLE_VENDOR_EDIT"),
      show: isRegistry && (isVendorEdit || isVehicleEdit || isDriverEdit || isEditWorker),
    },
    {
      path: `/${window?.contextPath}/employee/fsm/modify-application/` + id,
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
        : isAddWorker
        ? t("ES_FSM_REGISTRY_DETAILS_TYPE_WORKER")
        : null,
      show: isRegistry && (isNewVendor || isNewVehicle || isNewDriver || isAddWorker),
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
            link: "/${window?.contextPath}/employee/fsm/new-application",
            name: "FSM_NEW_DESLUDGING_APPLICATION",
            icon: <AddNewIcon />,
          },
        ]
      : [];

  const moduleForSomeFSMAdmin = FSM_ADMIN
    ? [
        {
          link: `/${window?.contextPath}/employee/fsm/registry?selectedTabs=VENDOR`,
          name: "ES_TITLE_FSM_REGISTRY",
          icon: <AddNewIcon />,
        },
      ]
    : [];

  const module = [
    ...moduleForSomeFSMEmployees,
    {
      link: `/${window?.contextPath}/employee/fsm/inbox`,
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

  // const Inbox = Digit.ComponentRegistryService.getComponent('FSMEmpInbox');
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
  const FSMSearch = Digit.ComponentRegistryService.getComponent("FSMSearch");
  const AddWorker = Digit.ComponentRegistryService.getComponent("AddWorker");
  const EditWorker = Digit.ComponentRegistryService.getComponent("EditWorker");
  const WorkerDetails = Digit.ComponentRegistryService.getComponent("WorkerDetails");
  const VehicleTrackingCard = Digit.ComponentRegistryService.getComponent("VehicleTrackingCard");
  const VehicleTrackingAlerts = Digit.ComponentRegistryService.getComponent("Alerts");
  const IllegalDumpingSites = Digit.ComponentRegistryService.getComponent("IllegalDumpingSites");

  const locationCheck =
    window.location.href.includes("/employee/fsm/inbox") ||
    window.location.href.includes("/employee/fsm/registry") ||
    window.location.href.includes("/employee/fsm/application-details/");

  const desludgingApplicationCheck = window.location.href.includes("/employee/fsm/new-application") || window.location.href.includes("/employee/fsm/modify-application");

  const destroySessionHelper = (currentPath,pathList,sessionName) => {
    if(!pathList.includes(currentPath)){
      sessionStorage.removeItem(`Digit.${sessionName}`)
    }
  }

  //destroying inbox session 
  useEffect(() => {
    const pathVar = location.pathname.replace(path + "/", "").split("?")?.[0];
    destroySessionHelper(pathVar,["inbox","application-details/"],"FSM_INBOX_SESSION");
  }, [location])

  return (
    <Switch>
      <React.Fragment>
        <div className="ground-container fsm-ground-container">
          {FSTPO ? (
            <BackButton isCommonPTPropertyScreen={location.pathname.includes("new") ? true : false} getBackPageNumber={location.pathname.includes("new") ? () => -2 : null}>
              {t("CS_COMMON_BACK")}
            </BackButton>
          ) : (
            <div>
              <BreadCrumbComp location={location} defaultPath={path} />
            </div>
          )}
          <PrivateRoute exact path={`${path}/`} component={() => <FSMLinks matchPath={path} userType={userType} />} />
          <PrivateRoute path={`${path}/inbox`} component={() => <Inbox parentRoute={path} isInbox={true} />} />
          <PrivateRoute path={`${path}/fstp-inbox`} component={() => <FstpInbox parentRoute={path} />} />
          <PrivateRoute path={`${path}/new-application`} component={() => <NewApplication parentUrl={url} />} />
          <PrivateRoute path={`${path}/modify-application/:id`} component={() => <EditApplication />} />
          <PrivateRoute path={`${path}/application-details/:id`} component={() => <EmployeeApplicationDetails parentRoute={path} userType="EMPLOYEE" />} />
          <PrivateRoute path={`${path}/fstp-operator-details/:id`} component={FstpOperatorDetails} />
          <PrivateRoute path={`${path}/response`} component={(props) => <Response {...props} parentRoute={path} />} />
          <PrivateRoute path={`${path}/application-audit/:id`} component={() => <ApplicationAudit parentRoute={path} />} />
          <PrivateRoute path={`${path}/search`} component={() => <FSMSearch />} />
          <PrivateRoute path={`${path}/rate-view/:id`} component={() => <RateView parentRoute={path} />} />
          <PrivateRoute path={`${path}/mark-for-disposal`} component={() => <div />} />
          <PrivateRoute exact path={`${path}/registry`} component={() => <FSMRegistry parentRoute={path} />} />
          <PrivateRoute path={`${path}/registry/vendor-details/:id`} component={() => <VendorDetails parentRoute={path} />} />
          <PrivateRoute path={`${path}/registry/new-vendor`} component={() => <AddVendor parentRoute={path} />} />
          <PrivateRoute path={`${path}/registry/modify-vendor/:id`} component={() => <EditVendor parentRoute={path} />} />
          <PrivateRoute path={`${path}/registry/vehicle-details/:id`} component={() => <VehicleDetails parentRoute={path} />} />
          <PrivateRoute path={`${path}/registry/new-vehicle`} component={() => <AddVehicle parentRoute={path} />} />
          <PrivateRoute path={`${path}/registry/modify-vehicle/:id`} component={() => <EditVehicle parentRoute={path} />} />
          <PrivateRoute path={`${path}/registry/driver-details/:id`} component={() => <DriverDetails parentRoute={path} />} />
          <PrivateRoute path={`${path}/registry/new-driver`} component={() => <AddDriver parentRoute={path} />} />
          <PrivateRoute path={`${path}/registry/modify-driver/:id`} component={() => <EditDriver parentRoute={path} />} />
          <PrivateRoute exact path={`${path}/fstp-operations`} component={() => <FstpOperations />} />
          <PrivateRoute exact path={`${path}/fstp-add-vehicle`} component={() => <FstpAddVehicle />} />
          <PrivateRoute exact path={`${path}/fstp-fsm-request/:id`} component={() => <FstpServiceRequest />} />
          {/* <PrivateRoute exact path={`${path}/home`} component={() => <ULBHomeCard module={module} />} /> */}
          <PrivateRoute exact path={`${path}/fstp/new-vehicle-entry`} component={FstpOperatorDetails} />
          <PrivateRoute exact path={`${path}/fstp/new-vehicle-entry/:id`} component={FstpOperatorDetails} />
          <PrivateRoute path={`${path}/registry/new-worker`} component={() => <AddWorker parentRoute={path} />} />
          <PrivateRoute path={`${path}/registry/edit-worker`} component={() => <EditWorker parentRoute={path} />} />
          <PrivateRoute path={`${path}/registry/worker-details`} component={() => <WorkerDetails parentRoute={path} />} />
          <PrivateRoute exact path={`${path}/vehicle-tracking/home`} component={() => <VehicleTrackingCard matchPath={path} userType={userType} />} />
          <PrivateRoute path={`${path}/vehicle-tracking/alerts`} component={() => <VehicleTrackingAlerts parentRoute={path} isInbox={true} />} />
          <PrivateRoute path={`${path}/vehicle-tracking/illegal-dumping-sites`} component={() => <IllegalDumpingSites />} />
        </div>
      </React.Fragment>
    </Switch>
  );
};

export default EmployeeApp;
