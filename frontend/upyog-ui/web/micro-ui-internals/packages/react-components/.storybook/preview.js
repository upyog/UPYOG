import "@upyog-niua/upyog-css/example/index.css";
import { initLibraries } from "@egovernments/digit-ui-libraries";

// TODO: It should be removed bcz we should not use any library in components
initLibraries();

export const parameters = {
  actions: { argTypesRegex: "^on[A-Z].*" },
};
