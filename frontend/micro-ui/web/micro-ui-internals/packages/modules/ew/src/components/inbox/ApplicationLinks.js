import { Card, ShippingTruck } from "@upyog/digit-ui-react-components";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { Link } from "react-router-dom";

/**
 * Renders application links for the E-Waste inbox based on user roles and permissions.
 * 
 * @param {Object} props The component properties
 * @param {string} props.linkPrefix The prefix to be added to each link URL
 * @param {string} [props.classNameForMobileView=""] Additional CSS class name for mobile view styling
 * @returns {JSX.Element} A Card component containing filtered application links
 */
const ApplicationLinks = ({ linkPrefix, classNameForMobileView = "" }) => {
  const { t } = useTranslation();

  const allLinks = [
    {
      text: t("ES_TITILE_SEARCH_APPLICATION"),
      link: `${linkPrefix}/search`,
    },
  ];

  const [links, setLinks] = useState([]);
  const { roles } = Digit.UserService.getUser().info;

  /**
   * Verifies if the user has access to specific functionality based on their roles.
   *
   * @param {string[]} accessTo Array of role codes required for access
   * @returns {number} Number of matching roles found
   */
  const hasAccess = (accessTo) => {
    return roles.filter((role) => accessTo.includes(role.code)).length;
  };

  /**
   * Filters available links based on user role permissions.
   * Links without access restrictions are always included.
   * Links with access restrictions are only included if the user has the required role.
   */
  useEffect(() => {
    let linksToShow = [];
    allLinks.forEach((link) => {
      if (link.accessTo) {
        if (hasAccess(link.accessTo)) {
          linksToShow.push(link);
        }
      } else {
        linksToShow.push(link);
      }
    });
    setLinks(linksToShow);
  }, []);

  /**
   * Renders the E-Waste service logo and header text.
   *
   * @returns {JSX.Element} Header section with logo and title
   */
  const GetLogo = () => (
    <div className="header">
      <span className="logo">
        <ShippingTruck />
      </span>{" "}
      <span className="text">{t("EWASTE_TITLE_INBOX")}</span>
    </div>
  );

  return (
    <Card className="employeeCard filter">
      <div className={`complaint-links-container ${classNameForMobileView}`}>
        {GetLogo()}
        <div className="body">
          {links.map(({ link, text }, index) => (
            <span className="link" key={index}>
              <Link to={link}>{text}</Link>
            </span>
          ))}
        </div>
      </div>
    </Card>
  );
};

export default ApplicationLinks;