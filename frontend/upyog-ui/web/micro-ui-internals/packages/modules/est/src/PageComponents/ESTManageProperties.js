import React from "react";
import { Link } from "react-router-dom";
import { Header, Card } from "@nudmcdgnpm/digit-ui-react-components";
import { useTranslation } from "react-i18next";
const ESTManageProperties = () => {
  const { t } = useTranslation();
  const { path: modulePath } = Digit.Hooks.useModuleBasePath();

  
//  This is for the page to navigate to different sections related to property management in the EST module.
//  It provides links to the following sections:
//  1. Manage Property: Allows users to manage properties by navigating to the "manage-properties-table" route.
//  2. All Properties: Allows users to view all properties by navigating to the "all-properties" route.
//  3. Assign Assets: Allows users to assign assets by navigating to the "assignassets/info" route.
//  4. Allotte Asset: Allows users to view allottee details by navigating to the "property-allottee-details" route.
// Each link is styled for better visibility and user experience.


  return (
    <React.Fragment>
      <div>
         <Header>{t("EST_MANAGE_PROPERTIES")}</Header>
      {/* Card Section */}
      <Card style={{ padding: "16px 24px" }}>
        <div
            style={{
            display: "flex",
            gap: "40px", // space between links
            alignItems: "center",
          }}
        >
          {/* Manage Property Link */}
          <span className="link">
            <Link
              to={`${modulePath}/manage-properties-table`}
              style={{
                textDecoration: "none",
                color: "#8B0000",
                fontWeight: "500",
                fontSize: "16px",
              }}
            >
              {t("EST_MANAGE_PROPERTY")}
            </Link>
          </span>
            
           {/* Manage Property Link */}
          <span className="link">
            <Link
              to={`${modulePath}/all-properties`}
              style={{
                textDecoration: "none",
                color: "#8B0000",
                fontWeight: "500",
                fontSize: "16px",
              }}
            >
              {t("EST_ALL_PROPERTIES")}
            </Link>
          </span>
      
          {/* Allotte Asset Link */}
          <span className="link">
            <Link
              to={`${modulePath}/property-allottee-details`}
              style={{
                textDecoration: "none",
                color: "#8B0000",
                fontWeight: "500",
                fontSize: "16px",
              }}
            >
              {t("EST_ALLOTTEE_DETAILS")}
            </Link>
          </span>
        </div>
      </Card>
        </div>
        </React.Fragment>
  );
};

export default ESTManageProperties;