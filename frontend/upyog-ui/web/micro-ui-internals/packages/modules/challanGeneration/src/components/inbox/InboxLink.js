import { Card, PTIcon } from "@nudmcdgnpm/digit-ui-react-components";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { Link } from "react-router-dom";

/**
 * InboxLinks component:
 * - Displays navigation links inside a card
 * - Supports internal routing and external hyperlinks
 * - Uses translation for text labels
 */

const InboxLinks = ({ parentRoute, businessService }) => {
  const { t } = useTranslation();
  const [links, setLinks] = useState([]);
  const allLinks = [
    {
      text: "UC_GENERATE_NEW_CHALLAN",
      link: "/upyog-ui/employee/challangeneration/generate-challan",
      icon: (
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
          <circle cx="12" cy="12" r="10"></circle>
          <line x1="12" y1="8" x2="12" y2="16"></line>
          <line x1="8" y1="12" x2="16" y2="12"></line>
        </svg>
      ),
    }
  ];

  useEffect(() => {
    let linksToShow = allLinks;
    setLinks(linksToShow);
  }, []);

  const GetLogo = () => (
    <div className="header cg-justify-start">
      <span className="logo">
        <PTIcon />
      </span>{" "}
      <span className="text">
        {t("ACTION_TEST_CHALLANGENERATION")}
      </span>
    </div>
  );

  return (
    <Card className="employeeCard filter inboxLinks challan-inbox-links-card">
      <div className="complaint-links-container">
        {GetLogo()}
        <div className="body">
          {links.map(({ link, text, icon, hyperlink = false }, index) => {
            return (
              <span className="link" key={index}>
                {hyperlink ? (
                  <a href={link} target="_blank" rel="noopener noreferrer">
                    {icon && <span className="link-icon">{icon}</span>}
                    <span>{t(text)}</span>
                  </a>
                ) : (
                  <Link to={link}>
                    {icon && <span className="link-icon">{icon}</span>}
                    <span>{t(text)}</span>
                  </Link>
                )}
              </span>
            );
          })}
        </div>
      </div>
    </Card>
  );
};

export default InboxLinks;
