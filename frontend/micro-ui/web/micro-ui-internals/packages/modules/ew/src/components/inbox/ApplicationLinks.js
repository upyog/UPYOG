// Importing necessary components and hooks from external libraries
import { Card, ShippingTruck } from "@nudmcdgnpm/digit-ui-react-components"; // UI components for cards and icons
import React, { useEffect, useState } from "react"; // React library and hooks for state and lifecycle management
import { useTranslation } from "react-i18next"; // Hook for translations
import { Link } from "react-router-dom"; // Component for navigation links

// Component to render application links for the E-Waste inbox
const ApplicationLinks = ({ linkPrefix, classNameForMobileView = "" }) => {
  const { t } = useTranslation(); // Translation hook

  // Array of all available links
  const allLinks = [
    {
      text: t("ES_TITILE_SEARCH_APPLICATION"), // Translated text for the link
      link: `${linkPrefix}/search`, // Link URL
    },
  ];

  const [links, setLinks] = useState([]); // State to store filtered links

  const { roles } = Digit.UserService.getUser().info; // Fetching the roles of the logged-in user

  // Function to check if the user has access to a specific link
  const hasAccess = (accessTo) => {
    return roles.filter((role) => accessTo.includes(role.code)).length; // Check if the user's roles match the required roles
  };

  // Effect to filter and set the links based on the user's roles
  useEffect(() => {
    let linksToShow = []; // Array to store links to be displayed
    allLinks.forEach((link) => {
      if (link.accessTo) {
        // If the link has access restrictions
        if (hasAccess(link.accessTo)) {
          linksToShow.push(link); // Add the link if the user has access
        }
      } else {
        linksToShow.push(link); // Add the link if there are no access restrictions
      }
    });
    setLinks(linksToShow); // Update the state with the filtered links
  }, []);

  // Function to render the logo and header text
  const GetLogo = () => (
    <div className="header">
      <span className="logo">
        <ShippingTruck /> {/* Icon for the E-Waste service */}
      </span>{" "}
      <span className="text">{t("EWASTE_TITLE_INBOX")}</span> {/* Translated text for the inbox title */}
    </div>
  );

  return (
    <Card className="employeeCard filter">
      <div className={`complaint-links-container ${classNameForMobileView}`}>
        {GetLogo()} {/* Render the logo and header */}
        <div className="body">
          {/* Render the filtered links */}
          {links.map(({ link, text }, index) => (
            <span className="link" key={index}>
              <Link to={link}>{text}</Link> {/* Render the link */}
            </span>
          ))}
        </div>
      </div>
    </Card>
  );
};

export default ApplicationLinks; // Exporting the component for use in other parts of the application