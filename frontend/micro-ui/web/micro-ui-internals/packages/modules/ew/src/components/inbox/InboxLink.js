// Importing necessary components and hooks from external libraries
import { Card, PropertyHouse } from "@upyog/digit-ui-react-components"; // UI components for cards and icons
import React, { useEffect, useState } from "react"; // React library and hooks for state and lifecycle management
import { Link } from "react-router-dom"; // Component for navigation links
import { useTranslation } from "react-i18next"; // Hook for translations

/**
 * Renders inbox links for the E-Waste module based on user roles and business service.
 * Filters and displays links according to user permissions and service context.
 *
 * @param {Object} props The component properties
 * @param {string} props.parentRoute The parent route path for navigation
 * @param {string} props.businessService The business service identifier
 * @returns {JSX.Element} A Card component containing filtered inbox links
 */
const InboxLinks = ({ parentRoute, businessService }) => {
  const { t } = useTranslation(); // Translation hook

  const allLinks = []; // Placeholder for all available links

  const [links, setLinks] = useState([]); // State to store filtered links

  const { roles: userRoles } = Digit.UserService.getUser().info; // Fetching the roles of the logged-in user

  /**
   * Filters links based on business service and user roles.
   * Updates the links state with filtered results.
   * Links are filtered by:
   * 1. Matching business service
   * 2. User having required role permissions or no role restrictions
   */
  useEffect(() => {
    let linksToShow = allLinks
      .filter((e) => e.businessService === businessService) // Filter links by business service
      .filter(({ roles }) => roles.some((e) => userRoles.map(({ code }) => code).includes(e)) || !roles?.length); // Filter links by user roles
    setLinks(linksToShow); // Update the state with the filtered links
  }, []);

  /**
   * Renders the E-Waste service header with logo.
   * Includes the PropertyHouse icon and translated service name.
   *
   * @returns {JSX.Element} Header section with logo and service title
   */
  const GetLogo = () => (
    <div className="header">
      <span className="logo">
        <PropertyHouse /> {/* Icon for the E-Waste service */}
      </span>{" "}
      <span className="text">{t("EW_SERVICE")}</span> {/* Translated text for the service name */}
    </div>
  );

  return (
    <Card className="employeeCard filter inboxLinks">
      <div className="complaint-links-container">
        {GetLogo()} {/* Render the logo and header */}
        <div className="body">
          {/* Render the filtered links */}
          {links.map(({ link, text, hyperlink = false, roles = [] }, index) => {
            return (
              <span className="link" key={index}>
                {/* Render as a hyperlink or a router link based on the `hyperlink` flag */}
                {hyperlink ? <a href={link}>{text}</a> : <Link to={link}>{t(text)}</Link>}
              </span>
            );
          })}
        </div>
      </div>
    </Card>
  );
};

export default InboxLinks; // Exporting the component for use in other parts of the application
