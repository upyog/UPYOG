import React from "react";
import {
  Loader,
  CardBasedOptions,
  ComplaintIcon,
  PTIcon,
  CaseIcon,
  DropIcon,
  HomeIcon,
  Calender,
  DocumentIcon,
  HelpIcon,
  WhatsNewCard,
  OBPSIcon,
  WSICon,
  CHBIcon
} from "@nudmcdgnpm/upyog-ui-react-components-lts";
import { useTranslation } from "react-i18next";
import ChatBot from "./ChatBot";
const Home = () => {
  const { t } = useTranslation();
  const navigate = Digit.Hooks.useCustomNavigate();
  const tenantId = Digit.ULBService.getCitizenCurrentTenant(true);
  const { data: { stateInfo, uiHomePage } = {}, isLoading } = Digit.Hooks.useStore.getInitData();
  let isMobile = window.Digit.Utils.browser.isMobile();
  if(window.Digit.SessionStorage.get("TL_CREATE_TRADE")) window.Digit.SessionStorage.set("TL_CREATE_TRADE",{})
   
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
    Digit.SessionStorage.get("locale") === null
      ? navigate(`/sv-ui/citizen/select-language`)
      : navigate(`/sv-ui/citizen/select-location`);
  }


  //TODO: Need to Add this in MDMS
  const citizenServicesCard = {
    code: "HOME_CITIZEN_SERVICES_CARD",
    name: "Home Citizen Services Card",
    enabled: true,
    headerLabel: "DASHBOARD_CITIZEN_SERVICES_LABEL",
    sideOption: {
        name: "DASHBOARD_VIEW_ALL_LABEL",
        enabled: true,
        navigationUrl: "/sv-ui/citizen/all-services"
    },
    props: [
        {
            code: "CITIZEN_SERVICE_SV",
            name: "SV",
            label: "ES_PGR_HEADER_COMPLAINT",
            enabled: true,
            navigationUrl: "/sv-ui/citizen/sv-home"
        }
    ]
};

  const citizenServicesObj = uiHomePage?.citizenServicesCard;
  const infoAndUpdatesObj = uiHomePage?.informationAndUpdatesCard;
  const whatsAppBannerWebObj = uiHomePage?.whatsAppBannerDesktop;
  const whatsAppBannerMobObj = uiHomePage?.whatsAppBannerMobile;
  const whatsNewSectionObj = uiHomePage?.whatsNewSection;

  const handleClickOnWhatsAppBanner = (obj) => {
    window.open(obj?.navigationUrl);
  };

  const allCitizenServicesProps = {
    header: t(citizenServicesObj?.headerLabel),
    sideOption: {
      name: t(citizenServicesObj?.sideOption?.name),
      onClick: () => navigate(citizenServicesObj?.sideOption?.navigationUrl),
    },
    options: [
      {
        name: t(citizenServicesObj?.props?.[0]?.label),
        Icon: <ComplaintIcon />,
        onClick: () => navigate(citizenServicesObj?.props?.[0]?.navigationUrl),
      },
      {
        name: t(citizenServicesObj?.props?.[1]?.label),
        Icon: <PTIcon className="fill-path-primary-main" />,
        onClick: () => navigate(citizenServicesObj?.props?.[1]?.navigationUrl.replace("/digit-ui/","/upyog-ui/")),
      },
      {
        name: t(citizenServicesObj?.props?.[2]?.label),
        Icon: <CaseIcon className="fill-path-primary-main" />,
        onClick: () => navigate(citizenServicesObj?.props?.[2]?.navigationUrl.replace("/digit-ui/","/upyog-ui/")),
      },
    ],
    styles: { display: "flex", flexWrap: "wrap", justifyContent: "flex-start", width: "100%" },
  };
  const allInfoAndUpdatesProps = {
    header: t(infoAndUpdatesObj?.headerLabel),
    sideOption: {
      name: t(infoAndUpdatesObj?.sideOption?.name),
      onClick: () => navigate(infoAndUpdatesObj?.sideOption?.navigationUrl),
    },
    options: [
      {
        name: t(infoAndUpdatesObj?.props?.[0]?.label),
        Icon: <HomeIcon />,
        onClick: () => navigate(infoAndUpdatesObj?.props?.[0]?.navigationUrl),
      },
      {
        name: t(infoAndUpdatesObj?.props?.[1]?.label),
        Icon: <Calender />,
        onClick: () => navigate(infoAndUpdatesObj?.props?.[1]?.navigationUrl),
      },
      {
        name: t(infoAndUpdatesObj?.props?.[2]?.label),
        Icon: <DocumentIcon />,
        onClick: () => navigate(infoAndUpdatesObj?.props?.[2]?.navigationUrl),
      },
      {
        name: t(infoAndUpdatesObj?.props?.[3]?.label),
        Icon: <DocumentIcon />,
        onClick: () => navigate(infoAndUpdatesObj?.props?.[3]?.navigationUrl),
      }
    ],
    styles: { display: "flex", flexWrap: "wrap", justifyContent: "flex-start", width: "100%" },
  };
  sessionStorage.removeItem("type" );
  sessionStorage.removeItem("pincode");
  sessionStorage.removeItem("tenantId");
  sessionStorage.removeItem("localityCode");
  sessionStorage.removeItem("landmark"); 
  sessionStorage.removeItem("propertyid");

  return isLoading ? (
    <Loader />
  ) : (
    <div className="HomePageContainer" style={{width:"100%"}}>
      {/* <div className="SideBarStatic">
        <StaticCitizenSideBar />
      </div> */}
      <div className="HomePageWrapper">
        {<div className="BannerWithSearch">
          {isMobile ? <img src={"https://nugp-assets.s3.ap-south-1.amazonaws.com/nugp+asset/Banner+UPYOG+%281920x500%29B+%282%29.jpg"} /> : <img src={"https://nugp-assets.s3.ap-south-1.amazonaws.com/nugp+asset/Banner+UPYOG+%281920x500%29A.jpg"} />}
          {/* <div className="Search">
            <StandaloneSearchBar placeholder={t("CS_COMMON_SEARCH_PLACEHOLDER")} />
          </div> */}
          <div className="ServicesSection">
          <CardBasedOptions style={{marginTop:"-30px"}} {...allCitizenServicesProps} />
          <CardBasedOptions style={isMobile ? {marginTop:"-30px"} : {marginTop:"-30px"}} {...allInfoAndUpdatesProps} />
        </div>
        </div>}


        {(whatsAppBannerMobObj || whatsAppBannerWebObj) && (
          <div className="WhatsAppBanner">
            {isMobile ? (
              <img src={"https://nugp-assets.s3.ap-south-1.amazonaws.com/nugp+asset/Banner+UPYOG+%281920x500%29B+%282%29.jpg"} onClick={() => handleClickOnWhatsAppBanner(whatsAppBannerMobObj)} style={{"width":"100%"}}/>
            ) : (
              <img src={"https://nugp-assets.s3.ap-south-1.amazonaws.com/nugp+asset/Banner+UPYOG+%281920x500%29B+%282%29.jpg"} onClick={() => handleClickOnWhatsAppBanner(whatsAppBannerWebObj)} style={{"width":"100%"}}/>
            )}
          </div>
        )}

        {conditionsToDisableNotificationCountTrigger() ? (
          EventsDataLoading ? (
            <Loader />
          ) : (
            <div className="WhatsNewSection">
              <div className="headSection">
                <h2>{t(whatsNewSectionObj?.headerLabel)}</h2>
                <p onClick={() => navigate(whatsNewSectionObj?.sideOption?.navigationUrl)}>{t(whatsNewSectionObj?.sideOption?.name)}</p>
              </div>
              <WhatsNewCard {...EventsData?.[0]} />
            </div>
          )
        ) : null}
        <ChatBot/>
      </div>
    </div>
  );
};

export default Home;
