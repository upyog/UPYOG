import { Card, PropertyHouse } from "@upyog/digit-ui-react-components";
import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";

const InboxLinks = ({ parentRoute, businessService }) => {
  const { t } = useTranslation();

  const allLinks = [
    {
      text: "ES_TITLE_NEW_PET_REGISTRATION",
      link: "/digit-ui/employee/chb/new-application",
      businessService: "chb",
      roles: ["CHB_APPROVER"],
    },
    {
      text: "CHB_SEARCH",
      link: `/digit-ui/employee/chb/search`,
      businessService: "chb",
      roles: [],
    },
    {
      text: "CHB_COMMON_APPLICATION_SEARCH",
      link: `/digit-ui/employee/chb/application-search`,
      businessService: "chb",
      roles: [],
    },
    // { text: "PT_REPORTS", hyperLink: true, link: "/digit-ui/employee/integration/dss/propertytax", roles: [], businessService: "PT" },
    // { text: "PT_DASHBOARD", link: "/digit-ui/employee/", roles: [], businessService: "PT" },
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
        <PropertyHouse />
      </span>{" "}
      <span className="text">{t("CHB_SERVICE")}</span>
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
