import { Card, CHBIcon } from "@upyog/digit-ui-react-components";
import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";


 /*
    Renders a card displaying dynamic links related to the CHB service.
    Links are filtered based on the provided businessService and the user's roles.
    Displays links such as "New Community Hall Booking" and "My Bookings".
  */
const InboxLinks = ({ parentRoute, businessService }) => {
  const { t } = useTranslation();

  const allLinks = [
    {
      text: "ES_NEW_COMMUNITY_HALL_BOOKING",
      link: "/digit-ui/employee/chb/bookHall/searchhall",
      businessService: "booking-refund",
      roles: ["CHB_CEMP"],
    },
    {
      text: "ES_COMMON_APPLICATION_SEARCH",
      link: `/digit-ui/employee/chb/my-applications`,
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
        <CHBIcon />
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
