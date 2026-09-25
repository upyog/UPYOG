import React from "react";
import { LogoutIcon } from "@upyog/workbench-ui-react-components";
import ChangeLanguage from "../components/ChangeLanguage";

const SideBarMenu = (t, closeSidebar, redirectToLoginPage) => [
  {
    type: "link",
    element: "HOME",
    text: t("COMMON_BOTTOM_NAVIGATION_HOME"),
    link: `/${window?.contextPath}/employee`,
    icon: "HomeIcon",
    populators: {
      onClick: closeSidebar,
    },
  },
  {
    type: "component",
    element: "LANGUAGE",
    action: <ChangeLanguage />,
    icon: "LanguageIcon",
  },
  {
    id: "login-btn",
    element: "LOGIN",
    text: t("CORE_COMMON_LOGIN"),
    icon: <LogoutIcon className="icon" />,
    populators: {
      onClick: redirectToLoginPage,
    },
  },
];

export default SideBarMenu;
