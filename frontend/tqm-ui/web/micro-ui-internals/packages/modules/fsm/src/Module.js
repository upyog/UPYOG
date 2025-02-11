import { CitizenHomeCard, CitizenTruck, Loader } from "@egovernments/digit-ui-react-components";
import React, { useEffect } from "react";
import { useTranslation } from "react-i18next";
import { Link, useRouteMatch } from "react-router-dom";
import FSMCard from "./components/FsmCard";
import CheckSlum from "./pageComponents/CheckSlum";
import SelectAddress from "./pageComponents/SelectAddress";
import SelectChannel from "./pageComponents/SelectChannel";
import SelectGender from "./pageComponents/SelectGender";
import SelectPaymentType from "./pageComponents/SelectPaymentType";
import SelectGeolocation from "./pageComponents/SelectGeolocation";
import SelectLandmark from "./pageComponents/SelectLandmark";
import SelectName from "./pageComponents/SelectName";
import SelectPincode from "./pageComponents/SelectPincode";
import SelectPitType from "./pageComponents/SelectPitType";
import SelectTripNo from "./pageComponents/SelectTripNo";
import SelectPropertySubtype from "./pageComponents/SelectPropertySubtype";
import SelectPropertyType from "./pageComponents/SelectPropertyType";
import SelectSlumName from "./pageComponents/SelectSlumName";
import SelectStreet from "./pageComponents/SelectStreet";
import SelectTankSize from "./pageComponents/SelectTankSize";
import SelectTripData from "./pageComponents/SelectTripData";
// import SelectTripNo from "./pageComponents/SelectTripNo";
import SelectPaymentPreference from "./pageComponents/SelectPaymentPreference";
import SelectVehicle from "./pageComponents/SelectVehicleType";
import CitizenApp from "./pages/citizen";
import ApplicationDetails from "./pages/citizen/ApplicationDetails";
import { MyApplications } from "./pages/citizen/MyApplications";
import NewApplicationCitizen from "./pages/citizen/NewApplication/index";
import RateView from "./pages/citizen/Rating/RateView";
import SelectRating from "./pages/citizen/Rating/SelectRating";
import EmployeeApp from "./pages/employee";
import ApplicationAudit from "./pages/employee/ApplicationAudit";
import EmployeeApplicationDetails from "./pages/employee/ApplicationDetails";
import DsoDashboard from "./pages/employee/DsoDashboard";
import EditApplication from "./pages/employee/EditApplication";
import FstpInbox from "./pages/employee/FstpInbox";
import FstpOperatorDetails from "./pages/employee/FstpOperatorDetails";
import Inbox from "./pages/employee/Inbox";
import { NewApplication } from "./pages/employee/NewApplication";
import Response from "./pages/Response";
import FSMRegistry from "./pages/employee/FSMRegistry";
import VendorDetails from "./pages/employee/FSMRegistry/Vendor/VendorDetails";
import AddVendor from "./pages/employee/FSMRegistry/Vendor/AddVendor";
import EditVendor from "./pages/employee/FSMRegistry/Vendor/EditVendor";
import VehicleDetails from "./pages/employee/FSMRegistry/Vehicle/VehicleDetails";
import AddVehicle from "./pages/employee/FSMRegistry/Vehicle/AddVehicle";
import EditVehicle from "./pages/employee/FSMRegistry/Vehicle/EditVehicle";
import DriverDetails from "./pages/employee/FSMRegistry/Driver/DriverDetails";
import AddDriver from "./pages/employee/FSMRegistry/Driver/AddDriver";
import EditDriver from "./pages/employee/FSMRegistry/Driver/EditDriver";
import { FsmBreadCrumb } from "./pages/employee";
import AdvanceCollection from "./pageComponents/AdvanceCollection";
import SelectTrips from "./pageComponents/SelectTrips";
import PlusMinusInput from "./pageComponents/PlusMinusInput";
import ConfirmationBox from "./components/Confirmation";
import WorkflowFilter from "./components/WorkflowFilter";
import Search from "./pages/employee/Search";
import AddWorker from "./pages/employee/FSMRegistry/Worker/AddWorker";
import AddWorkerRoles from "./pageComponents/addWorkerRole";
import SelectSWEmployeePhoneNumber from "./pageComponents/SelectSWEmployeePhoneNumber";
import EditWorker from "./pages/employee/FSMRegistry/Worker/EditWorker";
import WorkerDetails from "./pages/employee/FSMRegistry/Worker/WorkerDetails";
import SelectSWEmploymentDetails from "./pageComponents/SelectSWEmploymentDetails";
import VehicleTrackingCard from "./components/VehicleTrackingCard";
import Alerts from "./pages/employee/Alerts";
import IllegalDumpingSites from "./pages/employee/IllegalDumpingSites";

const FSMModule = ({ stateCode, userType, tenants }) => {
  const moduleCode = "FSM";
  const { path, url } = useRouteMatch();
  const language = Digit.StoreData.getCurrentLanguage();
  const { isLoading, data: store } = Digit.Services.useStore({ stateCode, moduleCode, language });

  if (isLoading) {
    return <Loader />;
  }
  Digit.SessionStorage.set("FSM_TENANTS", tenants);

  if (userType === "citizen") {
    return <CitizenApp path={path} />;
  } else {
    return <EmployeeApp path={path} url={url} userType={userType} />;
  }
};

const FSMLinks = ({ matchPath, userType }) => {
  const { t } = useTranslation();
  const [params, setParams, clearParams] = Digit.Hooks.useSessionStorage("FSM_CITIZEN_FILE_PROPERTY", {});

  useEffect(() => {
    clearParams();
  }, []);

  const roleBasedLoginRoutes = [
    {
      role: "FSM_DSO",
      from: `/${window?.contextPath}/citizen/fsm/dso-dashboard`,
      dashoardLink: "CS_LINK_DSO_DASHBOARD",
      loginLink: "CS_LINK_LOGIN_DSO",
    },
  ];

  if (userType === "citizen") {
    const links = [
      {
        link: `${matchPath}/new-application`,
        i18nKey: t("CS_HOME_APPLY_FOR_DESLUDGING"),
      },
      {
        link: `${matchPath}/my-applications`,
        i18nKey: t("CS_HOME_MY_APPLICATIONS"),
      },
    ];

    roleBasedLoginRoutes.map(({ role, from, loginLink, dashoardLink }) => {
      if (Digit.UserService.hasAccess(role))
        links.push({
          link: from,
          i18nKey: t(dashoardLink),
        });
      else
        links.push({
          link: `/${window?.contextPath}/citizen/login`,
          state: { role: "FSM_DSO", from },
          i18nKey: t(loginLink),
        });
    });

    return <CitizenHomeCard header={t("CS_HOME_FSM_SERVICES")} links={links} Icon={CitizenTruck} />;
  }
};

const componentsToRegister = {
  AddWorker,
  SelectPropertySubtype,
  SelectPropertyType,
  SelectAddress,
  SelectStreet,
  SelectLandmark,
  SelectPincode,
  SelectTankSize,
  SelectPitType,
  SelectTripNo,
  SelectGeolocation,
  SelectSlumName,
  CheckSlum,
  FSMCard,
  FSMModule,
  FSMLinks,
  SelectChannel,
  SelectName,
  SelectTripData,
  SelectGender,
  SelectPaymentType,
  SelectPaymentPreference,
  FSMEmpInbox: Inbox,
  FSMFstpInbox: FstpInbox,
  FSMNewApplicationEmp: NewApplication,
  FSMEditApplication: EditApplication,
  FSMEmployeeApplicationDetails: EmployeeApplicationDetails,
  FSMFstpOperatorDetails: FstpOperatorDetails,
  FSMResponse: Response,
  FSMApplicationAudit: ApplicationAudit,
  FSMRateView: RateView,
  FSMNewApplicationCitizen: NewApplicationCitizen,
  FSMMyApplications: MyApplications,
  FSMCitizenApplicationDetails: ApplicationDetails,
  FSMSelectRating: SelectRating,
  FSMDsoDashboard: DsoDashboard,
  FSMRegistry,
  VendorDetails,
  AddVendor,
  EditVendor,
  VehicleDetails,
  AddVehicle,
  EditVehicle,
  SelectVehicle,
  AddDriver,
  DriverDetails,
  EditDriver,
  FsmBreadCrumb,
  AdvanceCollection,
  SelectTrips,
  PlusMinusInput,
  ConfirmationBox,
  DSSCard: null, // TO HIDE THE DSS CARD IN HOME SCREEN as per MUKTA
  WorkflowFilter,
  FSMSearch: Search,
  AddWorkerRoles,
  SelectSWEmployeePhoneNumber,
  EditWorker,
  WorkerDetails,
  SelectSWEmploymentDetails,
  VehicleTrackingCard,
  Alerts,
  IllegalDumpingSites,
};

export const initFSMComponents = () => {
  Object.entries(componentsToRegister).forEach(([key, value]) => {
    Digit.ComponentRegistryService.setComponent(key, value);
  });
};
