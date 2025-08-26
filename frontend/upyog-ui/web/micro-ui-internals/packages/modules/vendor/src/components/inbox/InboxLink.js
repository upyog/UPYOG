import { Card, PropertyHouse } from "@upyog/digit-ui-react-components";
import React from "react";
import { useTranslation } from "react-i18next";

const InboxLinks = () => {
  const { t } = useTranslation();


  

 
  const GetLogo = () => (
    <div className="header">
      <span className="logo">
        <PropertyHouse />
      </span>{" "}
      <span className="text">{t("ASSET_SERVICE")}</span>
    </div>
  );

  return (
    // <Card style={{ paddingRight: 0, marginTop: 0 }} className="employeeCard filter inboxLinks">
    <Card className="employeeCard filter inboxLinks">
      <div className="complaint-links-container">
        {GetLogo()}
        {/* <div style={{ marginLeft: "unset", paddingLeft: "0px" }} className="body"> */}
        {/* <div className="body">
          {links.map(({ link, text, hyperlink = false, roles = [] }, index) => {
            return (
              <span className="link" key={index}>
                {hyperlink ? <a href={link}>{text}</a> : <Link to={link}>{t(text)}</Link>}
              </span>
            );
          })}
        </div> */}
      </div>
    </Card>
  );
};

export default InboxLinks;
