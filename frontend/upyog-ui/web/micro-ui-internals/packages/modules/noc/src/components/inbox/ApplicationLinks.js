import { Card, ShippingTruck } from "@nudmcdgnpm/digit-ui-react-components";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { Link } from "react-router-dom";

// Component to render module name card in Mobile Screeb
const ApplicationLinks = ({ linkPrefix, classNameForMobileView="" }) => {
  const { t } = useTranslation();
  const { roles } = Digit.UserService.getUser().info;

  const hasAccess = (accessTo) => {
    return roles.filter((role) => accessTo.includes(role.code)).length;
  };

  const GetLogo = () => (
    <div className="header">
      <span className="logo">
        <ShippingTruck />
      </span>{" "}
      <span className="text">{t("NOC_FIRE_NOC_CARD")}</span>
    </div>
  );

  return (
    <Card className="employeeCard filter">
      <div className={`complaint-links-container ${classNameForMobileView}`}>
        {GetLogo()}
      </div>
    </Card>
  );
};

export default ApplicationLinks;
