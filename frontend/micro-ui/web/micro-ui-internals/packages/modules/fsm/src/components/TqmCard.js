import { ArrowRightInbox, ShippingTruck, EmployeeModuleCard, Loader } from "@egovernments/digit-ui-react-components";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";
import { checkForEmployee } from "../utils";

const ROLES = {
  plant: ["PQM_TP_OPERATOR"],
  ulb: ["PQM_ADMIN"],
};

const TqmCard = ({ reRoute = true }) => {
  const history = useHistory();
  const isMobile = Digit.Utils.browser.isMobile();
  const isPlantOperatorLoggedIn = Digit.Utils.isPlantOperatorLoggedIn();
  const { t } = useTranslation();
  const tenantId = Digit.ULBService.getCurrentTenantId();

  if (!Digit.Utils.tqmAccess()) {
    return null;
  }

  //searching for plants linked to this user

  const userInfo = Digit.UserService.getUser();
  const userRoles = userInfo.info.roles.map((roleData) => roleData.code);

  const requestCriteriaPlantUsers = {
    params: {},
    url: "/pqm-service/plant/user/v1/_search",
    body: {
      plantUserSearchCriteria: {
        tenantId,
        // "plantCodes": [],
        plantUserUuids: userInfo?.info?.uuid ? [userInfo?.info?.uuid] : [],
        additionalDetails: {},
      },
      pagination: {},
    },
    config: {
      select: (data) => {
        let userPlants = data?.plantUsers
          ?.map((row) => {
            row.i18nKey = `PQM_PLANT_${row?.plantCode}`;
            return row;
          })
          ?.filter((row) => row.isActive);
        // userPlants.push({i18nKey:"PQM_PLANT_DEFAULT_ALL"})
        Digit.SessionStorage.set("user_plants", userPlants);
        return userPlants;
      },
    },
  };

  const { isLoading: isLoadingPlantUsers, data: dataPlantUsers } = Digit.Hooks.useCustomAPIHook(requestCriteriaPlantUsers);
  const requestCriteria = {
    url: "/inbox/v2/_search",
    body: {
      inbox: {
        tenantId,
        processSearchCriteria: {
          businessService: ["PQM"],
          moduleName: "pqm",
          tenantId,
        },
        moduleSearchCriteria: {
          tenantId,
        },
        limit: 100,
        offset: 0,
      },
    },
    config: {
      enabled:
        dataPlantUsers?.length > 0
          ? Digit.Utils.didEmployeeHasAtleastOneRole(ROLES.plant) || Digit.Utils.didEmployeeHasAtleastOneRole(ROLES.ulb)
          : false,
    },
  };

  const activePlantCode = Digit.SessionStorage.get("active_plant")?.plantCode
    ? [Digit.SessionStorage.get("active_plant")?.plantCode]
    : Digit.SessionStorage.get("user_plants")
        ?.filter((row) => row.plantCode)
        ?.map((row) => row.plantCode);
  if (activePlantCode?.length > 0) {
    requestCriteria.body.inbox.moduleSearchCriteria.plantCodes = [...activePlantCode];
  }

  const { isLoading, data: tqmInboxData } = Digit.Hooks.useCustomAPIHook(requestCriteria);

  let links = [
    {
      label: t("TQM_MONITOR"),
      link: userRoles?.includes("PQM_ADMIN") ? `/tqm-ui/employee` : `/tqm-ui/employee/tqm/landing`,
    },
  ];

  const propsForModuleCard = {
    Icon: <ShippingTruck />,
    moduleName: t("ACTION_TEST_TQM"),
    links: links,
  };

  if (isPlantOperatorLoggedIn) {
    delete propsForModuleCard.kpis;
    delete propsForModuleCard.links[2];
  }

  if (reRoute) {
    if (userRoles.length === 1) {
      const role = userRoles[0];
      let redirectUrl;
      switch (role) {
        case "PQM_TP_OPERATOR":
          redirectUrl = "/tqm-ui/employee/tqm/landing";
          break;
        case "PQM_ADMIN":
          redirectUrl = "/tqm-ui/employee";
          break;
      }
      window.location.href = redirectUrl;
    }
  }

  if (isLoading) {
    return <Loader />;
  }
  return <EmployeeModuleCard {...propsForModuleCard} TqmEnableUrl={true} />;
};

export default TqmCard;
