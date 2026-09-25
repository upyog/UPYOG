import i18next from "i18next";
import Enums from "./enums/index";
import mergeConfig from "./config/mergeConfig";
import { useStore } from "./services/index";
import { initI18n } from "./translations/index";

import { Storage, PersistantStorage } from "./services/atoms/Utils/Storage";
import { UploadServices } from "./services/atoms/UploadServices";
import JsDictionary from "./services/atoms/JsDictionary";

import { LocationService } from "./services/elements/Location";
import { LocalityService } from "./services/elements/Localities";
import { CustomService } from "./services/elements/CustomService";
import { LocalizationService } from "./services/elements/Localization/service";
import { LoginService } from "./services/elements/Login";
import * as dateUtils from "./services/atoms/Utils/Date";
import Download from "./services/atoms/Download";
import { WorkflowService } from "./services/elements/WorkFlow";
import { MdmsService } from "./services/elements/MDMS";
import { UserService } from "./services/elements/User";
import { ULBService } from "./services/molecules/Ulb";
import { ComponentRegistryService } from "./services/elements/ComponentRegistry";
import StoreData from "./services/molecules/StoreData";

import Contexts from "./contexts";
import Hooks from "./hooks";
import Utils from "./utils";
import { subFormRegistry } from "./subFormRegistry";
import AccessControlService from "./services/elements/Access";

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
  setupLibraries("DateUtils", dateUtils);
  setupLibraries("WorkflowService", WorkflowService);
  setupLibraries("MDMSService", MdmsService);
  setupLibraries("UploadServices", UploadServices);
  setupLibraries("JsDictionary", JsDictionary);
  setupLibraries("ComponentRegistryService", ComponentRegistryService);
  setupLibraries("StoreData", StoreData);
  setupLibraries("Contexts", Contexts);
  setupLibraries("Hooks", Hooks);
  setupLibraries("Customizations", {});
  setupLibraries("Utils", Utils);
  setupLibraries("Download", Download);

  setupLibraries("AccessControlService", AccessControlService);

  return new Promise((resolve) => {
    initI18n(resolve);
  });
};

export { initLibraries, Enums, Hooks, subFormRegistry };
