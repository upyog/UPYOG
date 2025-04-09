// Importing necessary components and hooks from external libraries
import React from "react";
import { useTranslation } from "react-i18next"; // Hook for translations
import { EmployeeModuleCard, PropertyHouse } from "@nudmcdgnpm/digit-ui-react-components"; // Components for rendering module cards and icons

// Component to render the E-Waste module card for employees
const EWCard = () => {
  const { t } = useTranslation(); // Translation hook

  // Check if the user has access to the E-Waste module
  if (!Digit.Utils.ewAccess()) {
    return null; // Return null if the user does not have access
  }

  // Links to be displayed in the module card
  const links = [
    {
      label: t("INBOX"), // Label for the "Inbox" link
      link: `/digit-ui/employee/ew/inbox`, // URL for the "Inbox" page
      role: "EW_CEMP", // Role required to access this link
    },
    {
      label: t("ES_COMMON_APPLICATION_SEARCH"), // Label for the "Application Search" link
      link: `/digit-ui/employee/ew/my-applications`, // URL for the "Application Search" page
    },
  ];

  // Check if the user has the "EW_VENDOR" role
  const EW_CEMP = Digit.UserService.hasAccess(["EW_VENDOR"]) || false;

  // Properties for the EmployeeModuleCard component
  const propsForModuleCard = {
    Icon: <PropertyHouse />, // Icon to display in the module card
    moduleName: <div style={{ width: "200px", wordWrap: "break-word" }}>{t("TITLE_E_WASTE")}</div>, // Module name with styling
    kpis: [], // Key Performance Indicators (KPIs) for the module (empty in this case)
    links: links.filter((link) => !link?.role || EW_CEMP), // Filter links based on the user's role
  };

  // Render the EmployeeModuleCard component with the specified properties
  return <EmployeeModuleCard {...propsForModuleCard} />;
};

export default EWCard; // Exporting the component
