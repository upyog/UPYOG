import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { ArrowRightInbox, ShippingTruck, EmployeeModuleCard, AddNewIcon, ViewReportIcon, InboxIcon, ReceiptIcon } from "@egovernments/digit-ui-react-components";
import { checkForEmployee } from "../utils";
const ArrowRight = ({ to }) => (
  <Link to={to}>
    <ArrowRightInbox />
  </Link>
);

const VehicleTrackingCard = () => {
  const { t } = useTranslation();
  const FSM_ADMIN = Digit.UserService.hasAccess("FSM_ADMIN") || false;
  const tenantId = Digit.ULBService.getCurrentTenantId();

  let links = [
    {
      link: `/${window?.contextPath}/employee/fsm/vehicle-tracking/alerts`,
      label: t("ES_FSM_ALERTS"),
      roles: ["FSM_ADMIN"],
    },
    {
      label: t("ES_FSM_ILLEGAL_DUMPING_SITES"),
      link: `/${window?.contextPath}/employee/fsm/vehicle-tracking/illegal-dumping-sites`,
      roles: ["FSM_ADMIN"],
    },
  ];

  links = links.filter((link) => (link.roles ? checkForEmployee(link.roles) : true));

  const propsForModuleCard = {
    Icon: <ReceiptIcon />,
    moduleName: t("ES_FSM_VEHICLE_TRACKING"),
    kpis: [],
    links: [...links],
  };

  return <EmployeeModuleCard {...propsForModuleCard} longModuleName={true} FsmHideCount={false} />;
};
export default VehicleTrackingCard;
