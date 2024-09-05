import React from "react";
import { Header } from "@egovernments/digit-ui-react-components";

const IllegalDumpingSites = () => {
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const userInfo = Digit.UserService.getUser();
  const userid = userInfo?.info?.id;
  return (
    <React.Fragment>
      <Header>{"Illegal Dumping Sites"}</Header>
      <div className="app-iframe-wrapper">
        <iframe src={`${document.location.origin}/route_map/#/vehicledumpingsites?userid=${userid}&tenantid=${tenantId}`} title={"title"} className="app-iframe" />
      </div>
    </React.Fragment>
  );
};

export default IllegalDumpingSites;
