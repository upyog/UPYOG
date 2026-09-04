import { logoutV1 } from "./logout";
import { UICreateConfigGenerator , getMDMSContextPath } from "./workbench";
import utils from "../utils";
import useLocalisationSearch from "./useLocalisationSearch";
// Import the custom theme configuration editor hook to enable packages-wide override hooks mapping
import { useThemeConfigEditor } from "./useThemeConfigEditor";

const UserService = {
  logoutV1,
};

const workbench = {
  UICreateConfigGenerator,
  useLocalisationSearch,
  getMDMSContextPath,
  // Added useThemeConfigEditor to register the hook globally/locally in the workbench modules hook-mapping
  useThemeConfigEditor
};

const contracts = {};

const Hooks = {
  attendance: {
    update: () => {},
  },
  workbench,
  contracts,
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
