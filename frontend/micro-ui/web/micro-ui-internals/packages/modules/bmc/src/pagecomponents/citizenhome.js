import React from "react";
import { useTranslation } from "react-i18next";
import { useQueryClient } from "react-query";
import { useHistory, useLocation, useRouteMatch } from "react-router-dom";

import {
  BackButton,
  Calender, CardBasedOptions,
  DocumentIcon,
  Loader, OBPSIcon,
  StandaloneSearchBar, WhatsNewCard
} from "@egovernments/digit-ui-react-components";

const BMCCitizenHome = ({ parentRoute }) => {
  const queryClient = useQueryClient();
  const match = useRouteMatch();
  const { t } = useTranslation();
  const { pathname } = useLocation();
  const history = useHistory();
  const stateId = Digit.ULBService.getStateId();
  const tenantId = Digit.ULBService.getCitizenCurrentTenant(true);
  const { data: { stateInfo } = {}, isLoading } = Digit.Hooks.useStore.getInitData();
  let config = [];
  const Aadhar = Digit?.ComponentRegistryService?.getComponent("AadhaarVerification");
  const conditionsToDisableNotificationCountTrigger = () => {
    if (Digit.UserService?.getUser()?.info?.type === "EMPLOYEE") return false;
    if (!Digit.UserService?.getUser()?.access_token) return false;
    return true;
  };

  const { data: EventsData, isLoading: EventsDataLoading } = Digit.Hooks.useEvents({
    tenantId,
    variant: "whats-new",
    config: {
      enabled: conditionsToDisableNotificationCountTrigger(),
    },
  });

  if (!tenantId) {
    history.push(`/digit-ui/citizen/select-language`);
  }

  const [params, setParams, clearParams] = Digit.Hooks.useSessionStorage("BMC_Application", {});

  const allCitizenServicesProps = {
    header: t("DASHBOARD_CITIZEN_SERVICES_LABEL"),
    sideOption: {
      name: t("DASHBOARD_VIEW_ALL_LABEL"),
      onClick: () => history.push("/digit-ui/citizen/all-services"),
    },
    options: [
      {
        name: t("New Application"),
        Icon: <OBPSIcon />,
        onClick: () => history.push(`${parentRoute}/application/aadhaarLogin`),
      },
      {
        name: t("Past Applications"),
        Icon: <DocumentIcon />,
        onClick: () => history.push("/digit-ui/citizen/bmc/aadhaarLogin"),
      },
      {
        name: t("Upcoming Schemes"),
        Icon: <Calender />,
        onClick: () => {history.push("/digit-ui/citizen/bmc/aadhaarLogin")},
      },

    ],
    styles: { display: "flex", flexWrap: "wrap", justifyContent: "flex-start", width: "100%" },
  };
  return (
    <div className="HomePageWrapper">
      <div className="BannerWithSearch">
      {!location.pathname.includes("/response") && <BackButton>{t("CS_COMMON_BACK")}</BackButton>}
        <img src={stateInfo?.bannerUrl} />
        <div className="Search">
          <StandaloneSearchBar placeholder={t("CS_COMMON_SEARCH_PLACEHOLDER")} />
        </div>
      </div>

      <div className="ServicesSection">
        <CardBasedOptions {...allCitizenServicesProps} />
      </div>

      {conditionsToDisableNotificationCountTrigger() ? (
        EventsDataLoading ? (
          <Loader />
        ) : (
          <div className="WhatsNewSection">
            <div className="headSection">
              <h2>{t("DASHBOARD_WHATS_NEW_LABEL")}</h2>
              <p onClick={() => history.push("/digit-ui/citizen/engagement/whats-new")}>{t("DASHBOARD_VIEW_ALL_LABEL")}</p>
            </div>
            <WhatsNewCard {...EventsData?.[0]} />
          </div>
        )
      ) : null}
    </div>
  );
};

export default BMCCitizenHome;
