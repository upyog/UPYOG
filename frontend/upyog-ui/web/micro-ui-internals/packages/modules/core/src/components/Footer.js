import React from "react";
import { useTranslation } from "react-i18next";

const Footer = ({ isEmployee = false, className = "", style = {} }) => {
  const { t } = useTranslation();
  const currentYear = new Date().getFullYear();
  const licenseUrl = "https://pg-egov-assets.s3.ap-south-1.amazonaws.com/Upyog+Code+and+Copyright+License_v1.pdf";

  return (
    <footer
      className={`upyog-app-footer ${isEmployee ? "upyog-app-footer--employee" : "upyog-app-footer--citizen"} ${className}`}
      style={style}
    >
      <div className="upyog-footer-content">
        <div className="upyog-footer-links">
          <a
            href={licenseUrl}
            target="_blank"
            rel="noopener noreferrer"
            className="upyog-footer-link"
          >
            {t("CORE_UPYOG_LICENSE") !== "CORE_UPYOG_LICENSE" ? t("CORE_UPYOG_LICENSE") : "UPYOG License"}
          </a>
          <span className="upyog-footer-divider">|</span>
          <a
            href="https://niua.in/"
            target="_blank"
            rel="noopener noreferrer"
            className="upyog-footer-link"
          >
            {t("CORE_COPYRIGHT_TEXT") !== "CORE_COPYRIGHT_TEXT"
              ? t("CORE_COPYRIGHT_TEXT")
              : `Copyright © ${currentYear} National Institute of Urban Affairs`}
          </a>
        </div>
      </div>
    </footer>
  );
};

export default Footer;
