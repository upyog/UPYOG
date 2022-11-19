import React from "react";
import { Switch, useLocation, Link } from "react-router-dom";
import { PrivateRoute, BreadCrumb,CommonDashboard } from "@egovernments/digit-ui-react-components";


const Dashboard = ({ path }) => {
  console.log(path);
  const cardMenuData = [
    {
      title: "Finance",
      subTitle: "Inbox",},

    {
      title: "Trade License",
      subTitle: "Inbox",
      link: `${path}/dfm/submenu`,
    },
    {
      title: "Trade License-1",
      subTitle: "Inbox",
    },
    {
      title: "Trade License-2",
      subTitle: "Inbox",
    },
    {
      title: "Trade License-3",
      subTitle: "Inbox",
    },
    {
      title: "Trade License-4",
      subTitle: "Inbox",
    },
    {
      title: "Trade License-5",
      subTitle: "Inbox",
    },
  ];
  return (
 <React.Fragment>
    <CommonDashboard title="Choose FileType" data={cardMenuData} path={path}/>
 </React.Fragment>
  );
};

export default Dashboard;
