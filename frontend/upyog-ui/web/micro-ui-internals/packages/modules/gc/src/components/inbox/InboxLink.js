import { Card, PTIcon } from "@nudmcdgnpm/digit-ui-react-components";
import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";


/**
 * InboxLinks Component
 * 
 * Renders a card displaying dynamic links related to the GC service on the inbox sidebar.
 * Links are filtered based on the provided `businessService` and the current user's roles.
 * Displays links such as "New Application" and "Search Application".
 * 
 * Props:
 * - `parentRoute`: Base route path for constructing navigation URLs
 * - `businessService`: The business service identifier used to filter applicable links
 */
const InboxLinks = ({ parentRoute, businessService }) => {
  const { t } = useTranslation();

  const allLinks = [
    {
      text: "ES_NEW_APPLICATION",
      link: "/upyog-ui/employee/gc/new-application",
      businessService: "booking-refund",
      roles: ["GC_CEMP"],
    },
    {
      text: "ES_COMMON_APPLICATION_SEARCH",
      link: `/upyog-ui/employee/gc/my-applications`,
      businessService: "booking-refund",
      roles: [],
    }
  ];

  const [links, setLinks] = useState([]);

  const { roles: userRoles } = Digit.UserService.getUser().info;

  useEffect(() => {
    let linksToShow = allLinks
      .filter((e) => e.businessService === businessService)
      .filter(({ roles }) => roles.some((e) => userRoles.map(({ code }) => code).includes(e)) || !roles?.length);
    setLinks(linksToShow);
  }, []);

  const GetLogo = () => (
    <div className="header">
      <span className="logo">
        <PTIcon />
      </span>{" "}
      <span className="text">{t("GC_MODULE_NAME")}</span>
    </div>
  );

  return (
    // <Card style={{ paddingRight: 0, marginTop: 0 }} className="employeeCard filter inboxLinks">
    <Card className="employeeCard filter inboxLinks">
      <div className="complaint-links-container">
        {GetLogo()}
        {/* <div style={{ marginLeft: "unset", paddingLeft: "0px" }} className="body"> */}
        <div className="body">
          {links.map(({ link, text, hyperlink = false, roles = [] }, index) => {
            return (
              <span className="link" key={index}>
                {hyperlink ? <a href={link}>{text}</a> : <Link to={link}>{t(text)}</Link>}
              </span>
            );
          })}
        </div>
      </div>
    </Card>
  );
};

export default InboxLinks;