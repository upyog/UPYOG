import { logoutV1 } from "./logout";
import { UICreateConfigGenerator, getMDMSContextPath, isSchemaCodeInMDMSAction } from "./workbench";
import utils from "../utils";
import useLocalisationSearch from "./useLocalisationSearch";
import useMDMSPopupSearch from "./inbox";

const UserService = {
  logoutV1,
};

const workbench = {
  UICreateConfigGenerator,
  getMDMSContextPath,
  isSchemaCodeInMDMSAction,
  useLocalisationSearch,
};

const contracts = {};

const Hooks = {
  attendance: {
    update: () => {},
  },
  workbench,
  contracts,
  inbox: {
    useMDMSPopupSearch,
  },
};

const Utils = {
  browser: {
    sample: () => {},
  },
  workbench: {
    ...utils,
  },
};

export const CustomisedHooks = {
  Hooks,
  UserService,
  Utils,
};
