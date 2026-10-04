import React, { useEffect, useState } from "react";
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
import { useLocation } from "react-router-dom";
import { CitizenSideBar } from "../../../components/TopBarSideBar/SideBar/CitizenSideBar";
import StaticCitizenSideBar from "../../../components/TopBarSideBar/SideBar/StaticCitizenSideBar";
// import ChatBot from "./ChatBot";
import UpyogBot from "./UpyogBot";
import HeaderBoxSection from "../../../components/HeaderBoxSection";
import UserActionLayout from "../../../components/UserActionLayout";

const Home = ({ layout }) => {
  const { t } = useTranslation();
  const location = useLocation();
  const navigate = Digit.Hooks.useCustomNavigate();
  const tenantId = Digit.ULBService.getCitizenCurrentTenant(true);
  const [user, setUser] = useState(Digit.UserService.getUser() || null);
  const [UIType, setUIType] = useState({})
  const DEFAULT_REDIRECT_URL = "/upyog-ui/citizen";
  const { data: { stateInfo, uiHomePage } = {}, isLoading } = Digit.Hooks.useStore.getInitData();
  let isMobile = window.Digit.Utils.browser.isMobile();
  if (window.Digit.SessionStorage.get("TL_CREATE_TRADE")) window.Digit.SessionStorage.set("TL_CREATE_TRADE", {});

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
    if (
      location.pathname.includes("/citizen/login") ||
      location.pathname.includes("/citizen/register") ||
      location.pathname.includes("/citizen/select-language") ||
      location.pathname.includes("/citizen/select-location")
    ) {
      return;
    }

    const currentUser = Digit.UserService.getUser();
    const currentLanguage = Digit.StoreData.getCurrentLanguage();

    if (!tenantId || !currentUser?.access_token) {
      navigate("/upyog-ui/citizen/login", { replace: true, state: { from: location.pathname + location.search } });
      return;
    }

    if (!currentLanguage) {
      navigate("/upyog-ui/citizen/select-language", { replace: true, state: { from: location.pathname + location.search } });
    }
  }, [tenantId, location.pathname, location.search, navigate]);

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

        const { ResponseInfo, UserRequest: info, ...tokens } = await Digit.DigiLockerService.token({ TokenReq });

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
      navigate("/upyog-ui/citizen/login", {
        replace: true,
        state: {
          redirectBackTo: redirectPath,
        },
      });
    } else {
      navigate(redirectPath, { replace: true });
    }
  }, [user]);

  sessionStorage.removeItem("type");
  sessionStorage.removeItem("pincode");
  sessionStorage.removeItem("tenantId");
  sessionStorage.removeItem("localityCode");
  sessionStorage.removeItem("landmark");
  sessionStorage.removeItem("propertyid");

  useEffect(() => {

    const DashboardLayout = {
      case1: {
        bannerView: true,
        welcomeView: false,
        quickServices: false,
        userActions: true,
        summary: false,
      },
      case2: {
        bannerView: false,
        welcomeView: true,
        quickServices: true,
        userActions: true,
        summary: true,
        isNew: true,
      },
      case3: {
        bannerView: false,
        welcomeView: true,
        quickServices: true,
        userActions: true,
        summary: true,
        isNew: false,
      }
    }
    setUIType(DashboardLayout.case2)
  }, [UIType])

  return isLoading ? (
    <Loader />
  ) : (
    <div className="HomePageContainer" style={{ width: "100%" }}>
      {/* <div className="SideBarStatic">
        <StaticCitizenSideBar />
      </div> */}
      <div className="HomePageWrapper">
        {UIType?.bannerView && <div className="BannerWithSearch">
          <div className="BannerBoard">
            <h1>UPYOG</h1>
            <p className="BannerSubheader">
              <span>U</span>rban <span>P</span>latform for deliver<span>Y</span> <br />
              of <span>O</span>nline <span>G</span>overnance
            </p>
            <div class="hero-stats">
              <article class="hero-stats__item">
                <span class="hero-stats__icon">
                  <img src="/images/bannerIcon1.svg" alt="banner Icon" />
                </span>

                <div class="hero-stats__content">
                  <h3 class="hero-stats__count">12</h3>
                  <p class="hero-stats__label">Total Applications</p>
                </div>
              </article>
              <br />
              <article class="hero-stats__item pending-requests">
                <span class="hero-stats__icon">
                  <img src="/images/bannerIcon2.svg" alt="banner Icon" />
                </span>

                <div class="hero-stats__content">
                  <h3 class="hero-stats__count">05</h3>
                  <p class="hero-stats__label">Pending Requests</p>
                </div>
              </article>
              <br />
              <article class="hero-stats__item services">
                <span class="hero-stats__icon hero-stats__icon--red">
                  <img src="/images/bannerIcon3.svg" alt="banner Icon" />
                </span>

                <div class="hero-stats__content">
                  <h3 class="hero-stats__count">28</h3>
                  <p class="hero-stats__label">Our Services</p>
                </div>
              </article>
            </div>
          </div>
          <div className="ServicesSection">
            <CardBasedOptions {...returnConstants(t, "allCitizenServicesProps", citizenServicesObj)} />

            <CardBasedOptions {...returnConstants(t, "allInfoAndUpdatesProps", infoAndUpdatesObj)} />
          </div>
        </div>}

        {UIType?.welcomeView && <div className="welcomeBanner">
          <p>Welcome, ABC</p>
          <h1>What are you working on today?</h1>
          <div>
            <img src="/images/search.svg" alt="searc icon" />
            <input type="text" placeholder="Search by applicant number, ID, name or module..." />
            <button>Search</button>
          </div>
        </div>}

        <div style={{ width: "100%", marginTop: "28px" }}>
          {UIType?.quickServices && <HeaderBoxSection title="Quick Services" cards={returnConstants(t, "quickServiceCards", navigate)} variant="service" columns={4} />}
          {UIType?.userActions && <UserActionLayout cards={returnConstants(t, "userActionCards", navigate)} showEmpty={UIType?.isNew || false} />}
          {UIType?.summary && <HeaderBoxSection cards={returnConstants(t, "taskSummaryCards", navigate)} columns={4} />}
        </div>



        {false && (whatsAppBannerMobObj || whatsAppBannerWebObj) && (
          <div className="WhatsAppBanner">
            {isMobile ? (
              <img
                src={"https://nugp-assets.s3.ap-south-1.amazonaws.com/nugp+asset/Banner+UPYOG+%281920x500%29B+%282%29.jpg"}
                onClick={() => handleClickOnWhatsAppBanner(whatsAppBannerMobObj)}
                style={{ width: "100%" }}
              />
            ) : (
              <img
                src={"https://nugp-assets.s3.ap-south-1.amazonaws.com/nugp+asset/Banner+UPYOG+%281920x500%29B+%282%29.jpg"}
                onClick={() => handleClickOnWhatsAppBanner(whatsAppBannerWebObj)}
                style={{ width: "100%" }}
              />
            )}
          </div>
        )}

        {false && conditionsToDisableNotificationCountTrigger() ? (
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

function returnConstants(t, type, fn) {
  switch (type) {
    case "allCitizenServicesProps":
      return {
        header: t(fn?.headerLabel),
        sideOption: {
          name: t(fn?.sideOption?.name),
          onClick: () => navigate(fn?.sideOption?.navigationUrl.replace("/digit-ui/", "/upyog-ui/")),
        },
        options: [
          {
            name: t(fn?.props?.[0]?.label),
            Icon: <ComplaintIcon />,
            onClick: () => navigate(fn?.props?.[0]?.navigationUrl.replace("/digit-ui/", "/upyog-ui/")),
          },
          {
            name: t(fn?.props?.[1]?.label),
            Icon: <PTIcon className="fill-path-primary-main" />,
            onClick: () => navigate(fn?.props?.[1]?.navigationUrl.replace("/digit-ui/", "/upyog-ui/")),
          },
          {
            name: t(fn?.props?.[2]?.label),
            Icon: <CaseIcon className="fill-path-primary-main" />,
            onClick: () => navigate(fn?.props?.[2]?.navigationUrl.replace("/digit-ui/", "/upyog-ui/")),
          },
          // {
          //     name: t("ACTION_TEST_WATER_AND_SEWERAGE"),
          //     Icon: <DropIcon/>,
          //     onClick: () => navigate("/upyog-ui/citizen")
          // },
          {
            name: t(fn?.props?.[3]?.label),
            Icon: <WSICon />,
            onClick: () => navigate(fn?.props?.[3]?.navigationUrl.replace("/digit-ui/", "/upyog-ui/")),
          },
        ],
        styles: { display: "flex", flexWrap: "wrap", justifyContent: "flex-start", width: "100%", marginRight: "16px" },
      }
    case "allInfoAndUpdatesProps":
      return {
        header: t(fn?.headerLabel),
        sideOption: {
          name: t(fn?.sideOption?.name),
          onClick: () => navigate(fn?.sideOption?.navigationUrl.replace("/digit-ui/", "/upyog-ui/")),
        },
        options: [
          {
            name: t(fn?.props?.[0]?.label),
            Icon: <HomeIcon />,
            onClick: () => navigate(fn?.props?.[0]?.navigationUrl.replace("/digit-ui/", "/upyog-ui/")),
          },
          {
            name: t(fn?.props?.[1]?.label),
            Icon: <Calender />,
            onClick: () => navigate(fn?.props?.[1]?.navigationUrl.replace("/digit-ui/", "/upyog-ui/")),
          },
          {
            name: t(fn?.props?.[2]?.label),
            Icon: <DocumentIcon />,
            onClick: () => navigate(fn?.props?.[2]?.navigationUrl.replace("/digit-ui/", "/upyog-ui/")),
          },
          {
            name: t(fn?.props?.[3]?.label),
            Icon: <DocumentIcon />,
            onClick: () => navigate(fn?.props?.[3]?.navigationUrl.replace("/digit-ui/", "/upyog-ui/")),
          },
          // {
          //     name: t("CS_COMMON_HELP"),
          //     Icon: <HelpIcon/>
          // }
        ],
        styles: { display: "flex", flexWrap: "wrap", justifyContent: "flex-start", width: "100%" },
      }
    case "taskSummaryCards":
      return [
        { title: "Task Assigned", value: 0, actionLabel: "View all tasks", onClick: () => fn("/upyog-ui/citizen") },
        { title: "Pending Payment", value: 0, actionLabel: "Pay now", onClick: () => fn("/upyog-ui/citizen") },
        { title: "Pending Documents", value: 0, actionLabel: "Take action", onClick: () => fn("/upyog-ui/citizen") },
        { title: "Under Review", value: 0, actionLabel: "Track status", onClick: () => fn("/upyog-ui/citizen") },
      ]
    case "quickServiceCards":
      return [
        { title: "PT", "text": "View & manage property tax", subtitle: "Property Tax", icon: "PT", onClick: () => fn("/upyog-ui/citizen/pt/property-tax") },
        { title: "TL", "text": "Apply for trade licence", subtitle: "Trade License", icon: "TL", onClick: () => fn("/upyog-ui/citizen/tl/trade-license") },
        { title: "BPA", "text": "Apply & track building plans", subtitle: "Building Plan Approval", icon: "BPA", onClick: () => fn("/upyog-ui/citizen/obps") },
        { title: "+", "text": "Expand all services", subtitle: "View All", icon: "+", onClick: () => fn("/upyog-ui/citizen") },
      ]
    case "userActionCards":
      return [
        {
          title: "Recent Applications",
          // viewAllLabel: "View All",
          onViewAll: () => fn("/upyog-ui/citizen/pt/property-tax"),
          footerLabel: "Go to My Applications",
          onFooterClick: () => fn("/upyog-ui/citizen/pt/property-tax"),
          items: [
            { title: "Property Tax Management", subTitle: "APR-2024-001234", status: "Paid", statusVariant: "success", icon: "🏠" },
            { title: "Community Hall Booking", subTitle: "APR-2024-001234", status: "Approved", statusVariant: "success", icon: "🏛️" },
            { title: "Advertisement Renewal", subTitle: "APR-2024-001234", status: "In Review", statusVariant: "info", icon: "📣" },
            { title: "Birth Registration", subTitle: "APR-2024-001234", status: "Rejected", statusVariant: "warning", icon: "👶" },
          ],
        },
        {
          title: "Notifications",
          // viewAllLabel: "View All",
          onViewAll: () => fn("/upyog-ui/citizen/engagement/notifications"),
          footerLabel: "View All Notifications",
          onFooterClick: () => fn("/upyog-ui/citizen/engagement/notifications"),
          items: [
            { title: "Property Tax Management", subTitle: "Receipt No: PT-2024-55677", icon: "🏠" },
            { title: "Community Hall booking request has...", subTitle: "Booking ID: CHB-2024-7788", icon: "🏛️" },
            { title: "Advertisement Renewed Successfully", subTitle: "Renewal ID: ADV-2024-7788", icon: "📣" },
            { title: "Birth Registration Completed", subTitle: "Registration ID: BR-2024-7788", icon: "👶" },
          ],
        },
        {
          title: "Upcoming Events",
          // viewAllLabel: "View All",
          onViewAll: () => fn("/upyog-ui/citizen/engagement/events"),
          footerLabel: "View All Upcoming Events",
          onFooterClick: () => fn("/upyog-ui/citizen/engagement/events"),
          items: [
            {
              title: "Clean City Drive",
              subTitle: "Connaught Place, Delhi",
              date: { day: "26", month: "JUN" },
              cta: "Register",
            },
            {
              title: "Clean City Drive",
              subTitle: "Connaught Place, Delhi",
              date: { day: "26", month: "JUN" },
              cta: "Register",
            },
            {
              title: "Clean City Drive",
              subTitle: "Connaught Place, Delhi",
              date: { day: "26", month: "JUN" },
              cta: "Register",
            },
          ],
        },
      ]
    default:
      return;
  }
}

export default Home;
