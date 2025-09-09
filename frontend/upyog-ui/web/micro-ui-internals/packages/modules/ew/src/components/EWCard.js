import React from "react";
import { useTranslation } from "react-i18next";
import { EmployeeModuleCard, PropertyHouse } from "@upyog/digit-ui-react-components";

/**
 * Renders the E-Waste module card for employee dashboard.
 * Displays navigation links and module information based on user permissions.
 * The card is only rendered if the user has access to the E-Waste module.
 * 
 * @returns {JSX.Element|null} Module card component or null if user lacks access
 */
const EWCard = () => {
  const { t } = useTranslation();

  if (!Digit.Utils.ewAccess()) {
    return null;
  }

  /**
   * Available navigation links for the module card.
   * Each link may have role-based access restrictions.
   * @type {Array<{label: string, link: string, role?: string}>}
   */
  const links = [
    {
      label: t("INBOX"),
      link: `/upyog-ui/employee/ew/inbox`,
      role: "EW_CEMP",
    },
    {
      label: t("ES_COMMON_APPLICATION_SEARCH"),
      link: `/upyog-ui/employee/ew/my-applications`,
    },
  ];

  const EW_CEMP = Digit.UserService.hasAccess(["EW_VENDOR"]) || false;

  const propsForModuleCard = {
    Icon: <PropertyHouse />,
    moduleName: <div style={{ width: "200px", wordWrap: "break-word" }}>{t("TITLE_E_WASTE")}</div>,
    kpis: [],
    links: links.filter((link) => !link?.role || EW_CEMP),
  };

  return <EmployeeModuleCard {...propsForModuleCard} />;
};

export default EWCard;
