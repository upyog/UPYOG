import { Card, PropertyHouse } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { useTranslation } from "react-i18next";

const InboxLinks = () => {
  const { t } = useTranslation();

  return (
    <Card className="employeeCard filter inboxLinks">
      <div className="complaint-links-container">
        <div className="header">
          <span className="logo">
            <PropertyHouse />
          </span>{" "}
          <span className="text">{t("ESTATE_SERVICE")}</span>
        </div>
      </div>
    </Card>
  );
};

export default InboxLinks;
