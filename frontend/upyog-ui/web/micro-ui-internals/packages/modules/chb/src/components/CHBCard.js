import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { EmployeeModuleCard,CHBIcon } from "@upyog/digit-ui-react-components";

/*
    CHBCard Component
    
    This component renders a module card displaying relevant information and links related to 
    Community Hall Booking (CHB) in the application.

    It fetches data related to inbox counts and other relevant statistics using the `useNewInboxGeneral` hook.
    The data includes:
      - `totalCount`: Total number of items in the inbox

    Key Features:
      - Displays the total count of items in the inbox, or "-" while loading.
      - Dynamically generates links for:
        - Inbox
        - Community Hall Booking Search
        - My Bookings
      - Customizes links and data based on user roles (e.g., CHB_CEMP).

    The component also ensures:
      - It only renders if the user has the necessary `chbAccess`.
      - Access control is applied for specific links, depending on roles (like `CHB_CEMP`).
  */
const CHBCard = () => {
  const { t } = useTranslation();

  const [total, setTotal] = useState("-");
  const { data, isLoading, isFetching, isSuccess } = Digit.Hooks.useNewInboxGeneral({
    tenantId: Digit.ULBService.getCurrentTenantId(),
    ModuleCode: "CHB",
    filters: { limit: 10, offset: 0, services: ["booking-refund"] },

    config: {
      select: (data) => {
        return {totalCount:data?.totalCount,nearingSlaCount:data?.nearingSlaCount} || "-";
      },
      enabled: Digit.Utils.chbAccess(),
    },
  });

  useEffect(() => {
    if (!isFetching && isSuccess) setTotal(data);
  }, [isFetching]);

  if (!Digit.Utils.chbAccess()) {
    return null;
  }
  const links=[
    {
      count: isLoading ? "-" : total?.totalCount,
      label: t("ES_COMMON_INBOX"),
      link: `/upyog-ui/employee/chb/inbox`,
    },
    {
      label: t("ES_NEW_COMMUNITY_HALL_BOOKING"),
      link: `/upyog-ui/employee/chb/bookHall/searchhall`,
    },
    {
      label: t("ES_COMMON_APPLICATION_SEARCH"),
      link: `/upyog-ui/employee/chb/my-applications`,
    },
  ]
  const CHB_CEMP = Digit.UserService.hasAccess(["CHB_CEMP"]) || false;
  const propsForModuleCard = {
    Icon: <CHBIcon/>,
    moduleName: t("ACTION_TEST_CHB"),
    kpis: [
      {
        count: total?.totalCount,
        label: t("ES_TITLE_INBOX"),
        link: `/upyog-ui/employee/chb/searchHall/inbox`,
      },
    ],
    links:links.filter(link=>!link?.role||CHB_CEMP),
  };

  return <EmployeeModuleCard {...propsForModuleCard} />;
};

export default CHBCard;
