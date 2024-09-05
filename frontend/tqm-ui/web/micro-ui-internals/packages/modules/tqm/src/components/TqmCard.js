import { EmployeeModuleCard, ArrowRightInbox, TqmHomePageCardIcon, Loader } from "@egovernments/digit-ui-react-components";
import React from "react";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";
import { checkForEmployee } from "../utils";

const ROLES = {
  plant:["PQM_TP_OPERATOR"],
  ulb:["PQM_ADMIN"]
};

const TqmCard = ({reRoute=true}) => {
  const history = useHistory()
  const isMobile = Digit.Utils.browser.isMobile();
  const isPlantOperatorLoggedIn = Digit.Utils.tqm.isPlantOperatorLoggedIn()
  const { t } = useTranslation();
  const tenantId = Digit.ULBService.getCurrentTenantId();

  if (!Digit.Utils.tqm.tqmAccess()) {
    return null;
  }

  //searching for plants linked to this user
  const userInfo = Digit.UserService.getUser();
  const requestCriteriaPlantUsers = {
    params:{},
    url:'/pqm-service/plant/user/v1/_search',
    body:{
      "plantUserSearchCriteria": {
        tenantId,
        // "plantCodes": [],
        "plantUserUuids": userInfo?.info?.uuid ?  [userInfo?.info?.uuid]: [],
        "additionalDetails": {}
      },
      "pagination": {}
    },
    config: {
      select:(data)=> {
        let userPlants =  data?.plantUsers?.map(row => {
          row.i18nKey = `PQM_PLANT_${row?.plantCode}`
          return row
        })?.filter(row=>row.isActive)
        // userPlants.push({i18nKey:"PQM_PLANT_DEFAULT_ALL"})
        Digit.SessionStorage.set("user_plants",userPlants );
        return userPlants
      }
    }
  }
  const { isLoading:isLoadingPlantUsers, data:dataPlantUsers} = Digit.Hooks.useCustomAPIHook(requestCriteriaPlantUsers);

  const requestCriteria = {
    url: "/inbox/v2/_search",
    body: {
      inbox: {
        tenantId,
        processSearchCriteria: {
          "businessService": [
              "PQM"
          ],
          "moduleName": "pqm",
          tenantId
      },
        moduleSearchCriteria: {
          tenantId,
        },
        limit: 100,
        offset: 0,
      },
    },
    config: {
      enabled: dataPlantUsers?.length>0 ? Digit.Utils.didEmployeeHasAtleastOneRole(ROLES.plant) || Digit.Utils.didEmployeeHasAtleastOneRole(ROLES.ulb):false,
    },
  };

  const activePlantCode = Digit.SessionStorage.get("active_plant")?.plantCode ? [Digit.SessionStorage.get("active_plant")?.plantCode]:Digit.SessionStorage.get("user_plants")?.filter(row => row.plantCode)?.map(row => row.plantCode)
  if(activePlantCode?.length>0){
    requestCriteria.body.inbox.moduleSearchCriteria.plantCodes = [...activePlantCode]
  }

  const { isLoading, data:tqmInboxData } = Digit.Hooks.useCustomAPIHook(requestCriteria);

  let links = [
    {
      label: t("TQM_INBOX"),
      link: `/${window?.contextPath}/employee/tqm/inbox`,
      roles: [...ROLES.plant,ROLES.ulb],
      count:  isLoading ? '-' : tqmInboxData?.totalCount ? String(tqmInboxData?.totalCount) : "0"
    },
    {
      label: t("TQM_VIEW_PAST_RESULTS"),
      link: `/${window?.contextPath}/employee/tqm/search-test-results`,
      roles: [...ROLES.plant,ROLES.ulb],
    },
    // {
    //   label: t("TQM_VIEW_IOT_READING"),
    //   link: `/${window?.contextPath}/employee/tqm/search-test-results`,
    //   roles: [...ROLES.plant,ROLES.ulb],
    // },
    // {
    //   label: t("TQM_SENSOR_MON"),
    //   link: `/${window?.contextPath}/employee/tqm/search-devices`,
    //   roles: [...ROLES.plant,ROLES.ulb],
    // },
    {
      label: t("TQM_ADD_TEST_RESULT"),
      link: `/${window?.contextPath}/employee/tqm/add-test-result`,
      roles: [...ROLES.ulb],
    },
    // {
    //   label: t("TQM_DASHBOARD"),
    //   link: `/${window?.contextPath}/employee/dss/dashboard/pqm`,
    //   roles: [...ROLES.plant,ROLES.ulb],
    // }
  ];
  links = links.filter((link) =>
    link.roles ? checkForEmployee(link.roles) : true
  );
  // links = links.filter((link) => (link?.roles && link?.roles?.length > 0 ? Digit.Utils.didEmployeeHasAtleastOneRole(link?.roles) : true));
  

  const propsForModuleCard = {
    Icon: <TqmHomePageCardIcon />,
    moduleName: t("ACTION_TEST_TQM"),
    kpis: [
      {
        count:  isLoading ? '-' : tqmInboxData?.totalCount ,
        label: t('TQM_KPI_PENDING_TESTS'),
        link: `/${window?.contextPath}/employee/tqm/inbox`,
      },
    ],
    links: links,
  };

  if(isPlantOperatorLoggedIn) {
    delete propsForModuleCard.kpis
    delete propsForModuleCard.links[2]
  }
  if(reRoute && isPlantOperatorLoggedIn){
    history.push( `/${window?.contextPath}/employee/tqm/landing`)
  }

  if(isLoading){
    return <Loader />
  }
  return <EmployeeModuleCard {...propsForModuleCard} />;
};

export default TqmCard;
