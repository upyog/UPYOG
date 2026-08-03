import React, { useEffect,useState } from "react";
import {
  StandaloneSearchBar,
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
} from "@nudmcdgnpm/digit-ui-react-components";
import { useTranslation } from "react-i18next";
import {  useLocation } from "react-router-dom";
import { CitizenSideBar } from "../../../components/TopBarSideBar/SideBar/CitizenSideBar";
import StaticCitizenSideBar from "../../../components/TopBarSideBar/SideBar/StaticCitizenSideBar";
// import ChatBot from "./ChatBot";
import UpyogBot from "./UpyogBot";
const Home = () => {
  const { t } = useTranslation();
  const location = useLocation();
  const navigate = Digit.Hooks.useCustomNavigate();
  const tenantId = Digit.ULBService.getCitizenCurrentTenant(true);
  const [user, setUser] = useState(null);
  const DEFAULT_REDIRECT_URL = "/upyog-ui/citizen";
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

   /* Added below condition inside useEffect because
      When the Home component renders, if !tenantId is true,
      it immediately calls navigate(), which tries to update the BrowserRouter's state 
      while the Home component is still in the middle of rendering.
   */
    useEffect(() => {
      if (!tenantId) {
        Digit.SessionStorage.get("locale") === null
          ? navigate(`/upyog-ui/citizen/select-language`)
          : navigate(`/upyog-ui/citizen/select-location`);
      }
  }, [tenantId, navigate]);

  const appBannerWebObj = uiHomePage?.appBannerDesktop;
  const appBannerMobObj = uiHomePage?.appBannerMobile;
  const citizenServicesObj = uiHomePage?.citizenServicesCard;
  const infoAndUpdatesObj = uiHomePage?.informationAndUpdatesCard;
  const whatsAppBannerWebObj = uiHomePage?.whatsAppBannerDesktop;
  const whatsAppBannerMobObj = uiHomePage?.whatsAppBannerMobile;
  const whatsNewSectionObj = uiHomePage?.whatsNewSection;

  const handleClickOnWhatsAppBanner = (obj) => {
    window.open(obj?.navigationUrl);
  };
  /* set citizen details to enable backward compatiable */
const setCitizenDetail = (userObject, token, tenantId) => {
  let locale = JSON.parse(sessionStorage.getItem("Digit.initData"))?.value?.selectedLanguage;
  localStorage.setItem("Citizen.tenant-id", tenantId);
  localStorage.setItem("tenant-id", tenantId);
  localStorage.setItem("citizen.userRequestObject", JSON.stringify(userObject));
  localStorage.setItem("locale", locale);
  localStorage.setItem("Citizen.locale", locale);
  localStorage.setItem("token", token);
  localStorage.setItem("Citizen.token", token);
  localStorage.setItem("user-info", JSON.stringify(userObject));
  localStorage.setItem("Citizen.user-info", JSON.stringify(userObject));
};

  // useEffect(async () => {
  //   //sessionStorage.setItem("DigiLocker.token1","cf87055822e4aa49b0ba74778518dc400a0277e5")
  //   if (window.location.href.includes("code")) {
  //     let code = window.location.href.split("=")[1].split("&")[0]
  //     let TokenReq = {
  //       dlReqRef: localStorage.getItem('code_verfier_register'),
  //       code: code, module: "SSO"

  //     }
  //     const { ResponseInfo, UserRequest: info, ...tokens } = await Digit.DigiLockerService.token({ TokenReq })
  //     setUser({ info, ...tokens });
  //     setCitizenDetail(info, tokens?.access_token, info?.tenantId)
  //   }
  // }, [])

  useEffect(() => {
  const init = async () => {
    if (window.location.href.includes("code")) {
      let code = window.location.href.split("=")[1].split("&")[0];

      let TokenReq = {
        dlReqRef: localStorage.getItem("code_verfier_register"),
        code: code,
        module: "SSO",
      };

      const { ResponseInfo, UserRequest: info, ...tokens } =
        await Digit.DigiLockerService.token({ TokenReq });

      setUser({ info, ...tokens });
      setCitizenDetail(info, tokens?.access_token, info?.tenantId);
    }
  };

  init();
}, []);
useEffect(() => {
  if (!user) {
    return;
  }
  Digit.SessionStorage.set("citizen.userRequestObject", user);
  Digit.UserService.setUser(user);
  setCitizenDetail(user?.info, user?.access_token, "pg");
  const redirectPath = location.state?.from || DEFAULT_REDIRECT_URL;
  if (!Digit.ULBService.getCitizenCurrentTenant(true)) {
    navigate("/upyog-ui/citizen/select-location", { replace: true, state: {
      redirectBackTo: redirectPath,
    } });
  } else {
    navigate(redirectPath, { replace: true });
  }
}, [user]);

  const allCitizenServicesProps = {
    header: t(citizenServicesObj?.headerLabel),
    sideOption: {
      name: t(citizenServicesObj?.sideOption?.name),
      onClick: () => navigate(citizenServicesObj?.sideOption?.navigationUrl.replace("/digit-ui/","/upyog-ui/")),
    },
    options: [
      {
        name: t(citizenServicesObj?.props?.[0]?.label),
        Icon: <ComplaintIcon />,
        onClick: () => navigate(citizenServicesObj?.props?.[0]?.navigationUrl.replace("/digit-ui/","/upyog-ui/")),
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
      // {
      //     name: t("ACTION_TEST_WATER_AND_SEWERAGE"),
      //     Icon: <DropIcon/>,
      //     onClick: () => navigate("/upyog-ui/citizen")
      // },
      {
        name: t(citizenServicesObj?.props?.[3]?.label),
        Icon: <WSICon />,
        onClick: () => navigate(citizenServicesObj?.props?.[3]?.navigationUrl.replace("/digit-ui/","/upyog-ui/")),
      },
    ],
    styles: { display: "flex", flexWrap: "wrap", justifyContent: "flex-start", width: "100%" },
  };
  const allInfoAndUpdatesProps = {
    header: t(infoAndUpdatesObj?.headerLabel),
    sideOption: {
      name: t(infoAndUpdatesObj?.sideOption?.name),
      onClick: () => navigate(infoAndUpdatesObj?.sideOption?.navigationUrl.replace("/digit-ui/","/upyog-ui/")),
    },
    options: [
      {
        name: t(infoAndUpdatesObj?.props?.[0]?.label),
        Icon: <HomeIcon />,
        onClick: () => navigate(infoAndUpdatesObj?.props?.[0]?.navigationUrl.replace("/digit-ui/","/upyog-ui/")),
      },
      {
        name: t(infoAndUpdatesObj?.props?.[1]?.label),
        Icon: <Calender />,
        onClick: () => navigate(infoAndUpdatesObj?.props?.[1]?.navigationUrl.replace("/digit-ui/","/upyog-ui/")),
      },
      {
        name: t(infoAndUpdatesObj?.props?.[2]?.label),
        Icon: <DocumentIcon />,
        onClick: () => navigate(infoAndUpdatesObj?.props?.[2]?.navigationUrl.replace("/digit-ui/","/upyog-ui/")),
      },
      {
        name: t(infoAndUpdatesObj?.props?.[3]?.label),
        Icon: <DocumentIcon />,
        onClick: () => navigate(infoAndUpdatesObj?.props?.[3]?.navigationUrl.replace("/digit-ui/","/upyog-ui/")),
      },
      // {
      //     name: t("CS_COMMON_HELP"),
      //     Icon: <HelpIcon/>
      // }
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
          {isMobile ? <img src={"https://niuatt-filestore.s3.ap-south-1.amazonaws.com/pg/logo/Banner+UPYOG.jpg"} /> : <img src={"https://niuatt-filestore.s3.ap-south-1.amazonaws.com/pg/logo/Banner+UPYOG.jpg"} />}
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
          ) : EventsData?.length > 0 ? (
            <div className="WhatsNewSection">
              <div className="headSection">
                <h2>{t(whatsNewSectionObj?.headerLabel)}</h2>
                <p onClick={() => navigate(whatsNewSectionObj?.sideOption?.navigationUrl)}>{t(whatsNewSectionObj?.sideOption?.name)}</p>
              </div>
              <WhatsNewCard {...EventsData?.[0]} />
            </div>
          ) : null
        ) : null}
       {/* <ChatBot /> text bot commented as we are using new speech bot*/} 
       <UpyogBot />
      </div>
    </div>
  );
};

export default Home;
