import mergeConfig from "./config/mergeConfig";
import Enums from "./enums/index";
import { useStore } from "./services/index";
import { initI18n } from "./translations/index";

import JsDictionary from "./services/atoms/JsDictionary";
import { UploadServices } from "./services/atoms/UploadServices";
import { PersistantStorage, Storage } from "./services/atoms/Utils/Storage";

import Download from "./services/atoms/Download";
import * as dateUtils from "./services/atoms/Utils/Date";
import { Complaint } from "./services/elements/Complaint";
import { CustomService } from "./services/elements/CustomService";
import EventsServices from "./services/elements/Events";
import HrmsService from "./services/elements/HRMS";
import { InboxGeneral } from "./services/elements/InboxService";
import { LocalityService } from "./services/elements/Localities";
import { LocalizationService } from "./services/elements/Localization/service";
import { LocationService } from "./services/elements/Location";
import { LoginService } from "./services/elements/Login";
import { MdmsService } from "./services/elements/MDMS";
import { PGRService } from "./services/elements/PGR";
import { PaymentService } from "./services/elements/Payment";
import { UserService } from "./services/elements/User";
import { WorkflowService } from "./services/elements/WorkFlow";


import { ComponentRegistryService } from "./services/elements/ComponentRegistry";
import { GetServiceDefinitions } from "./services/molecules/ServiceDefinitions";
import ShareFiles from "./services/molecules/ShareFiles";
import StoreData from "./services/molecules/StoreData";
import { ULBService } from "./services/molecules/Ulb";

import Contexts from "./contexts";
import Hooks from "./hooks";
import AccessControlService from "./services/elements/Access";
import SchemeService from "./services/elements/Scheme";
import { subFormRegistry } from "./subFormRegistry";
import Utils from "./utils";

const setupLibraries = (Library, props) => {
  window.Digit = window.Digit || {};
  window.Digit[Library] = window.Digit[Library] || {};
  window.Digit[Library] = { ...window.Digit[Library], ...props };
};

const initLibraries = () => {
  setupLibraries("SessionStorage", Storage);
  setupLibraries("PersistantStorage", PersistantStorage);
  setupLibraries("UserService", UserService);
  setupLibraries("ULBService", ULBService);

  setupLibraries("Config", { mergeConfig });
  setupLibraries("Services", { useStore });
  setupLibraries("Enums", Enums);
  setupLibraries("LocationService", LocationService);
  setupLibraries("CustomService",CustomService)
  setupLibraries("LocalityService", LocalityService);
  setupLibraries("LoginService", LoginService);
  setupLibraries("LocalizationService", LocalizationService);
  setupLibraries("PGRService", PGRService);
  setupLibraries("HRMSService", HrmsService);
  setupLibraries("PaymentService", PaymentService);
  setupLibraries("DateUtils", dateUtils);
  setupLibraries("WorkflowService", WorkflowService);
  setupLibraries("MDMSService", MdmsService);
  setupLibraries("UploadServices", UploadServices);
  setupLibraries("JsDictionary", JsDictionary);
  setupLibraries("GetServiceDefinitions", GetServiceDefinitions);
  setupLibraries("Complaint", Complaint);
  setupLibraries("ComponentRegistryService", ComponentRegistryService);
  setupLibraries("StoreData", StoreData);
  setupLibraries("EventsServices", EventsServices);

  setupLibraries("InboxGeneral", InboxGeneral);
  setupLibraries("ShareFiles", ShareFiles);
  setupLibraries("Contexts", Contexts);
  setupLibraries("Hooks", Hooks);
  setupLibraries("Customizations", {});
  setupLibraries("Utils", Utils);
  setupLibraries("Download", Download);
  setupLibraries("SchemeService",SchemeService);
  setupLibraries("AccessControlService", AccessControlService);

  return new Promise((resolve) => {
    initI18n(resolve);
  });
};

export { Enums, Hooks, initLibraries, subFormRegistry };

