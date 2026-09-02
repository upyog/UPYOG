import { Card, PropertyHouse } from "@nudmcdgnpm/digit-ui-react-components";
import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";

// Component for Module name card renderer inside Inbox
const InboxLinks = ({ parentRoute, businessService }) => {
  const { t } = useTranslation();

  const GetLogo = () => (
    <div className="header">
      <span className="logo">
        <PropertyHouse />
      </span>{" "}
      <span className="text">{t("NOC_FIRE_NOC_CARD")}</span>
    </div>
  );

  return (
    <Card className="employeeCard filter inboxLinks">
      <div className="complaint-links-container">
        {GetLogo()}
      </div>
    </Card>
  );
};

export default InboxLinks;
