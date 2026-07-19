import React, { useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { EmployeeModuleCard, PropertyHouse } from "@nudmcdgnpm/digit-ui-react-components";
import { getCreateAssetPath, getEstModulePath } from "../utils/estRoutes";

const ESTCard = () => {
  const { t } = useTranslation();
  const navigate = Digit.Hooks.useCustomNavigate();
  const { path: modulePath } = Digit.Hooks.useModuleBasePath();
  const estModulePath = getEstModulePath(modulePath);
  const [isMobile, setIsMobile] = useState(window.innerWidth <= 768);

  useEffect(() => {
    const handleResize = () => setIsMobile(window.innerWidth <= 768);
    window.addEventListener("resize", handleResize);
    return () => window.removeEventListener("resize", handleResize);
  }, []);

  const EST_CEMP = Digit.UserService.hasAccess(["EST_CEMP"]) || false;
  if (!EST_CEMP) return null;

  const links = [
    {
      label: t("ES_COMMON_APPLICATION_SEARCH"),
      link: `${estModulePath}/search-applications`,
      onClick: () => navigate(`${estModulePath}/search-applications`),
    },
    {
      label: t("EST_CREATE_ASSET"),
      link: getCreateAssetPath(modulePath),
      onClick: () => navigate(getCreateAssetPath(modulePath)),
    }
    // {
    //   label: t("EST_MANAGE_PROPERTIES"),
    //   link: `${estModulePath}/manage-properties`,
    //   onClick: () => navigate(`${estModulePath}/manage-properties`),
    // },
    // {
    //   label: t("EST_ACTIONS"),
    //   link: `${estModulePath}/actions`,
    //   onClick: () => navigate(`${estModulePath}/actions`),
    // },
  ];

  const propsForModuleCard = {
    Icon: <PropertyHouse />,
    moduleName: (
      <div
        style={{
          width: isMobile ? "150px" : "200px",
          wordWrap: "break-word",
          fontSize: isMobile ? "14px" : "16px",
        }}
      >
        {t("ESTATE_MANAGEMENT")}
      </div>
    ),
    kpis: [],
    links: links.filter((link) => !link?.role || EST_CEMP),
  };

  return (
    <div
      style={{
        width: "100%",
        padding: isMobile ? "10px" : "20px",
      }}
    >
      <EmployeeModuleCard {...propsForModuleCard} />
    </div>
  );
};

export default ESTCard;
