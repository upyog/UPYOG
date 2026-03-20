import React, { useEffect, useState } from "react";
import {
  StandaloneSearchBar,
  Loader,
  // CardBasedOptions,
  ComplaintIcon,
  PTIcon,
  CaseIcon,
  DropIcon,
  HomeIcon,
  // Calender,
  // DocumentIcon,
  HelpIcon,
  WhatsNewCard,
  OBPSIcon,
  WSICon,
} from "@upyog/digit-ui-react-components";
import { CardBasedOptions } from "../../../../../../react-components/src/index";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";
import { CitizenSideBar } from "../../../components/TopBarSideBar/SideBar/CitizenSideBar";
import StaticCitizenSideBar from "../../../components/TopBarSideBar/SideBar/StaticCitizenSideBar";
import ChatBot from "./ChatBot";
const Home = () => {
  const { t } = useTranslation();
  const history = useHistory();
  const tenantId = Digit.ULBService.getCitizenCurrentTenant(true);
  const [user, setUser] = useState(null);
  const DEFAULT_REDIRECT_URL = "/mycity-ui/citizen";
  const { data: { stateInfo, uiHomePage } = {}, isLoading } = Digit.Hooks.useStore.getInitData();
  let isMobile = window.Digit.Utils.browser.isMobile();
  if (window.Digit.SessionStorage.get("TL_CREATE_TRADE")) window.Digit.SessionStorage.set("TL_CREATE_TRADE", {})

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
      ? history.push(`/mycity-ui/citizen/select-language`)
      : history.push(`/mycity-ui/citizen/select-location`);
  }

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

  const DocumentIcon = () => (
    <svg width="100" height="100" viewBox="0 0 18 18" fill="none" xmlns="http://www.w3.org/2000/svg" style={{ width: "35px", height: "35px" }}>
      <path
        d="M16 0H2C0.9 0 0 0.9 0 2V16C0 17.1 0.9 18 2 18H16C17.1 18 18 17.1 18 16V2C18 0.9 17.1 0 16 0ZM11 14H4V12H11V14ZM14 10H4V8H14V10ZM14 6H4V4H14V6Z"
        fill="#D40000"
      />
    </svg>
  );

  const Calender = ({ className, onClick }) => (
    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="black" className={className} style={{ width: "35px", height: "35px" }} onClick={onClick}>
      <path d="M0 0h24v24H0z" fill="none" />
      <path d="M20 3h-1V1h-2v2H7V1H5v2H4c-1.1 0-2 .9-2 2v16c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm0 18H4V8h16v13z"
        fill="#D40000" />
    </svg>
  );

  useEffect(async () => {
    //sessionStorage.setItem("DigiLocker.token1","cf87055822e4aa49b0ba74778518dc400a0277e5")
    if (window.location.href.includes("code")) {
      let code = window.location.href.split("=")[1].split("&")[0]
      let TokenReq = {
        dlReqRef: localStorage.getItem('code_verfier_register'),
        code: code, module: "SSO"

      }
      const { ResponseInfo, UserRequest: info, ...tokens } = await Digit.DigiLockerService.token({ TokenReq })
      setUser({ info, ...tokens });
      setCitizenDetail(info, tokens?.access_token, info?.tenantId)
    }
  }, [])
  useEffect(() => {
    if (!user) {
      return;
    }
    Digit.SessionStorage.set("citizen.userRequestObject", user);
    Digit.UserService.setUser(user);
    setCitizenDetail(user?.info, user?.access_token, "pg");
    const redirectPath = location.state?.from || DEFAULT_REDIRECT_URL;
    if (!Digit.ULBService.getCitizenCurrentTenant(true)) {
      history.replace("/mycity-ui/citizen/select-location", {
        redirectBackTo: redirectPath,
      });
    } else {
      history.replace(redirectPath);
    }
  }, [user]);
  console.log("citizenServicesObjcitizenServicesObj", citizenServicesObj);
  const cardsList = [
    {
      "code": "CITIZEN_SERVICE_PGR",
      "name": "Building Plan Approval",
      "label": "ES_PGR_HEADER_COMPLAINT",
      "enabled": true,
      "navigationUrl": "/digit-ui/citizen/obps-home"
    },
    {
      "code": "CITIZEN_SERVICE_PT",
      "name": "Advertisement",
      "label": "MODULE_PT",
      "enabled": true,
      "navigationUrl": "/digit-ui/citizen/ads-home"
    },
    {
      "code": "CITIZEN_SERVICE_TL",
      "name": "Bills Accounting",
      "label": "MODULE_TL",
      "enabled": true,
      "navigationUrl": "/digit-ui/citizen/bills-home"
    },
    {
      "code": "CITIZEN_SERVICE_CHB",
      "name": "Miscellaneous Collection",
      "label": "ACTION_TEST_CHB",
      "enabled": true,
      "navigationUrl": "/digit-ui/citizen/mcollect-home"
    },
    {
      "code": "CITIZEN_SERVICE_WS",
      "name": "Pet Registration",
      "label": "ACTION_TEST_WATER_AND_SEWERAGE",
      "enabled": true,
      "navigationUrl": "/mycity-ui/citizen/ptr-home"
    },
    {
      "code": "CITIZEN_SERVICE_WS",
      "name": "Verification Search",
      "label": "ACTION_TEST_WATER_AND_SEWERAGE",
      "enabled": true,
      "navigationUrl": "/mycity-ui/citizen/verificationsearch-home"
    },
    {
      "code": "CITIZEN_SERVICE_WS",
      "name": "E-Waste",
      "label": "ACTION_TEST_WATER_AND_SEWERAGE",
      "enabled": true,
      "navigationUrl": "/mycity-ui/citizen/ew-home"
    },
    {
      "code": "CITIZEN_SERVICE_WS",
      "name": "Construction & Demolition",
      "label": "ACTION_TEST_WATER_AND_SEWERAGE",
      "enabled": true,
      "navigationUrl": "/cnd-ui/citizen/cnd-home"
    },
    {
      "code": "CITIZEN_SERVICE_WS",
      "name": "Citizen Request Service",
      "label": "ACTION_TEST_WATER_AND_SEWERAGE",
      "enabled": true,
      "navigationUrl": "/mycity-ui/citizen/wt-home"
    },
    {
      "code": "CITIZEN_SERVICE_WS",
      "name": "Birth",
      "label": "ACTION_TEST_WATER_AND_SEWERAGE",
      "enabled": true,
      "navigationUrl": "/citizen/birth-citizen/home"
    },
    {
      "code": "CITIZEN_SERVICE_WS",
      "name": "Death",
      "label": "ACTION_TEST_WATER_AND_SEWERAGE",
      "enabled": true,
      "navigationUrl": "/citizen/death-citizen/home"
    },
    {
      "code": "CITIZEN_SERVICE_WS",
      "name": "Desludging Service",
      "label": "ACTION_TEST_WATER_AND_SEWERAGE",
      "enabled": true,
      "navigationUrl": "/mycity-ui/citizen/fsm-home"
    },
    {
      "code": "CITIZEN_SERVICE_WS",
      "name": "Fire NOC",
      "label": "ACTION_TEST_WATER_AND_SEWERAGE",
      "enabled": true,
      "navigationUrl": "/citizen/fire-noc/home"
    },
    {
      "code": "CITIZEN_SERVICE_WS",
      "name": "Street Vending",
      "label": "ACTION_TEST_WATER_AND_SEWERAGE",
      "enabled": true,
      "navigationUrl": "/sv-ui/citizen/sv-home"
    }
  ]

  const OBPSIcon = ({ className, styles }) => (
    <svg className={className} style={{ ...styles }} width="34" height="30" viewBox="0 0 34 30" fill="#D40000" xmlns="http://www.w3.org/2000/svg">
      <path
        fillRule="evenodd"
        clipRule="evenodd"
        d="M30.3333 0H3.66659C1.83325 0 0.333252 1.5 0.333252 3.33333V26.6667C0.333252 28.5 1.83325 30 3.66659 30H30.3333C32.1666 30 33.6666 28.5 33.6666 26.6667V3.33333C33.6666 1.5 32.1666 0 30.3333 0ZM13.6666 23.3333H5.33325V20H13.6666V23.3333ZM13.6666 16.6667H5.33325V13.3333H13.6666V16.6667ZM13.6666 10H5.33325V6.66667H13.6666V10ZM21.6999 20L16.9999 15.2667L19.3499 12.9167L21.6999 15.2833L26.9833 10L29.3499 12.3667L21.6999 20Z"

      />
    </svg>
  );

  const BirthIcon = ({ styles, className }) => (
    <svg width="35" height="39" className={className} style={{ ...styles }} viewBox="0 0 35 39" fill="#D40000" xmlns="http://www.w3.org/2000/svg">
      <path d="M20.7502 0.916016H3.25016C1.646 0.916016 0.333496 2.22852 0.333496 3.83268V27.166C0.333496 28.7702 1.646 30.0827 3.25016 30.0827H20.7502C22.3543 30.0827 23.6668 28.7702 23.6668 27.166V3.83268C23.6668 2.22852 22.3543 0.916016 20.7502 0.916016ZM3.25016 3.83268H10.5418V15.4994L6.896 13.3119L3.25016 15.4994V3.83268Z" />
    </svg>
  );

  const CHBIcon = ({ styles, className }) => (
    <svg width="24" height="24" className={className} style={{ ...styles }} viewBox="0 0 24 24" fill="#D40000" xmlns="http://www.w3.org/2000/svg">
      <path d="M7 10H4V17H7V10Z" />
      <path d="M13.5 10H10.5V17H13.5V10Z" />
      <path d="M22 19H2V22H22V19Z" />
      <path d="M20 10H17V17H20V10Z" />
      <path d="M12 1L2 6V8H22V6L12 1Z" />
    </svg>
  );

  const BillsIcon = ({ styles, className }) => (
    <svg width="24" height="27" className={className} style={{ ...styles }} viewBox="0 0 24 27" fill="#D40000" xmlns="http://www.w3.org/2000/svg">
      <path d="M21.3333 2.99967H15.76C15.2 1.45301 13.7333 0.333008 12 0.333008C10.2667 0.333008 8.8 1.45301 8.24 2.99967H2.66667C1.2 2.99967 0 4.19967 0 5.66634V24.333C0 25.7997 1.2 26.9997 2.66667 26.9997H21.3333C22.8 26.9997 24 25.7997 24 24.333V5.66634C24 4.19967 22.8 2.99967 21.3333 2.99967ZM12 2.99967C12.7333 2.99967 13.3333 3.59967 13.3333 4.33301C13.3333 5.06634 12.7333 5.66634 12 5.66634C11.2667 5.66634 10.6667 5.06634 10.6667 4.33301C10.6667 3.59967 11.2667 2.99967 12 2.99967ZM14.6667 21.6663H5.33333V18.9997H14.6667V21.6663ZM18.6667 16.333H5.33333V13.6663H18.6667V16.333ZM18.6667 10.9997H5.33333V8.33301H18.6667V10.9997Z" />
    </svg>
  );

  const MCollectIcon = ({ styles, className }) => (
    <svg width="37" height="35" className={className} style={{ ...styles }} viewBox="0 0 37 35" fill="#D40000" xmlns="http://www.w3.org/2000/svg">
      <path d="M34.375 28.75V30.625C34.375 32.6875 32.6875 34.375 30.625 34.375H4.375C2.29375 34.375 0.625 32.6875 0.625 30.625V4.375C0.625 2.3125 2.29375 0.625 4.375 0.625H30.625C32.6875 0.625 34.375 2.3125 34.375 4.375V6.25H17.5C15.4187 6.25 13.75 7.9375 13.75 10V25C13.75 27.0625 15.4187 28.75 17.5 28.75H34.375ZM17.5 25H36.25V10H17.5V25ZM25 20.3125C23.4438 20.3125 22.1875 19.0562 22.1875 17.5C22.1875 15.9438 23.4438 14.6875 25 14.6875C26.5562 14.6875 27.8125 15.9438 27.8125 17.5C27.8125 19.0562 26.5562 20.3125 25 20.3125Z" />
    </svg>
  );

  const FirenocIcon = ({ styles, className }) => (
    <svg width="35" height="39" className={className} style={{ ...styles, width: "35px", height: "35px" }} viewBox="0 0 35 39" fill="#D40000" xmlns="http://www.w3.org/2000/svg">
      <path
        d="M21.5142857,14.0571429 C21.12,13.5428571 20.6571429,13.0971429 20.2114286,12.6514286 C19.0971429,11.6228571 17.8114286,10.8857143 16.7314286,9.80571429 C14.2285714,7.30285714 13.7142857,3.17142857 15.2742857,0 C13.7142857,0.394285714 12.2742857,1.28571429 11.0742857,2.26285714 C6.72,5.82857143 5.00571429,12.12 7.06285714,17.52 C7.13142857,17.6914286 7.2,17.8628571 7.2,18.0857143 C7.2,18.4628571 6.94285714,18.8057143 6.6,18.9428571 C6.22285714,19.1142857 5.81142857,19.0114286 5.50285714,18.7371429 C5.4,18.6514286 5.33142857,18.5657143 5.24571429,18.4457143 C3.36,15.9942857 3.05142857,12.48 4.33714286,9.66857143 C1.52571429,12 8.60422844e-16,15.9428571 0.24,19.6628571 C0.308571429,20.52 0.411428571,21.3771429 0.702857143,22.2342857 C0.942857143,23.2628571 1.38857143,24.2914286 1.93714286,25.2 C3.72,28.1657143 6.85714286,30.2914286 10.2342857,30.72 C13.8342857,31.1828571 17.6914286,30.5142857 20.4514286,27.9771429 C23.5371429,25.1314286 24.6514286,20.5714286 23.0228571,16.6628571 L22.8,16.2171429 C22.4571429,15.4285714 21.9942857,14.7257143 21.4285714,14.0742857 L21.5142857,14.0571429 L21.5142857,14.0571429 Z M16.2,24.8571429 C15.72,25.2685714 14.9485714,25.7142857 14.3485714,25.8857143 C12.4628571,26.5714286 10.5771429,25.6114286 9.42857143,24.48 C11.4685714,24 12.6685714,22.4914286 13.0114286,20.9657143 C13.3028571,19.5942857 12.7714286,18.4628571 12.5485714,17.1428571 C12.3428571,15.8742857 12.3771429,14.7942857 12.8571429,13.6114286 C13.1485714,14.2628571 13.4914286,14.9142857 13.8857143,15.4285714 C15.1885714,17.1428571 17.2285714,17.8971429 17.6571429,20.2285714 C17.7257143,20.4685714 17.76,20.7085714 17.76,20.9657143 C17.8114286,22.3714286 17.2114286,23.9142857 16.1828571,24.8571429 L16.2,24.8571429 Z"
        style={{ fill: "#D40000" }}></path>
    </svg>
  );

  const FSMIcon = ({ className, styles }) => (
    <svg width="40" height="40" viewBox="0 0 23 19" className={className} style={{ ...styles }} fill="#D40000" xmlns="http://www.w3.org/2000/svg">
      <path d="M20 8h-3V4H3c-1.1 0-2 .9-2 2v11h2c0 1.66 1.34 3 3 3s3-1.34 3-3h6c0 1.66 1.34 3 3 3s3-1.34 3-3h2v-5l-3-4zM6 18.5c-.83 0-1.5-.67-1.5-1.5s.67-1.5 1.5-1.5 1.5.67 1.5 1.5-.67 1.5-1.5 1.5zm13.5-9l1.96 2.5H17V9.5h2.5zm-1.5 9c-.83 0-1.5-.67-1.5-1.5s.67-1.5 1.5-1.5 1.5.67 1.5 1.5-.67 1.5-1.5 1.5z" />
    </svg>
  );

  const allCitizenServicesProps = {
    header: t(citizenServicesObj?.headerLabel),
    sideOption: {
      name: t(citizenServicesObj?.sideOption?.name),
      onClick: () => history.push(citizenServicesObj?.sideOption?.navigationUrl.replace("/digit-ui/", "/mycity-ui/")),
    },
    type: "CitizenServices",
    options: [
      {
        name: t(citizenServicesObj?.props?.[0]?.label),
        Icon: <ComplaintIcon styles={{ fill: "#D40000", width: "35px", height: "35px" }} />,
        onClick: () => history.push(citizenServicesObj?.props?.[0]?.navigationUrl.replace("/digit-ui/", "/mycity-ui/")),
      },
      {
        name: t(citizenServicesObj?.props?.[1]?.label),
        Icon: <PTIcon styles={{ fill: "#D40000", width: "35px", height: "35px" }} />,
        onClick: () => history.push(citizenServicesObj?.props?.[1]?.navigationUrl.replace("/digit-ui/", "/mycity-ui/")),
      },
      {
        name: t(citizenServicesObj?.props?.[2]?.label),
        Icon: <CaseIcon styles={{ fill: "#D40000", width: "35px", height: "35px" }} />,
        onClick: () => history.push(citizenServicesObj?.props?.[2]?.navigationUrl.replace("/digit-ui/", "/mycity-ui/")),
      },
      // {
      //     name: t("ACTION_TEST_WATER_AND_SEWERAGE"),
      //     Icon: <DropIcon/>,
      //     onClick: () => history.push("/mycity-ui/citizen")
      // },
      {
        name: t(citizenServicesObj?.props?.[3]?.label),
        Icon: <WSICon styles={{ fill: "#D40000", width: "35px", height: "35px" }} />,
        onClick: () => history.push(citizenServicesObj?.props?.[3]?.navigationUrl.replace("/digit-ui/", "/mycity-ui/")),
      },
      {
        name: t(citizenServicesObj?.props?.[4]?.label),
        Icon: <WSICon styles={{ fill: "#D40000", width: "35px", height: "35px" }} />,
        onClick: () => history.push(citizenServicesObj?.props?.[4]?.navigationUrl.replace("/digit-ui/", "/mycity-ui/")),
      },
      {
        name: t(cardsList[0].name),
        Icon: <OBPSIcon styles={{ fill: "#D40000", width: "35px", height: "35px" }} />,
        onClick: () => history.push(cardsList[0].navigationUrl.replace("/digit-ui/", "/mycity-ui/")),
      },
      {
        name: t(cardsList[1].name),
        Icon: <CHBIcon styles={{ fill: "#D40000", width: "35px", height: "35px" }} />,
        onClick: () => history.push(cardsList[1].navigationUrl.replace("/digit-ui/", "/mycity-ui/")),
      },
      {
        name: t(cardsList[2].name),
        Icon: <BillsIcon styles={{ fill: "#D40000", width: "35px", height: "35px" }} />,
        onClick: () => history.push(cardsList[2].navigationUrl.replace("/digit-ui/", "/mycity-ui/")),
      },
      {
        name: t(cardsList[3].name),
        Icon: <MCollectIcon styles={{ fill: "#D40000", width: "35px", height: "35px" }} />,
        onClick: () => history.push(cardsList[3].navigationUrl.replace("/digit-ui/", "/mycity-ui/")),
      },
      {
        name: t(cardsList[4].name),
        Icon: <CaseIcon styles={{ fill: "#D40000", width: "35px", height: "35px" }} />,
        onClick: () => history.push(cardsList[4].navigationUrl.replace("/digit-ui/", "/mycity-ui/")),
      },
      {
        name: t(cardsList[5].name),
        Icon: <CaseIcon styles={{ fill: "#D40000", width: "35px", height: "35px" }} />,
        onClick: () => history.push(cardsList[5].navigationUrl.replace("/digit-ui/", "/mycity-ui/")),
      },
      {
        name: t(cardsList[6].name),
        Icon: <CaseIcon styles={{ fill: "#D40000", width: "35px", height: "35px" }} />,
        onClick: () => history.push(cardsList[6].navigationUrl.replace("/digit-ui/", "/mycity-ui/")),
      },
      {
        name: t(cardsList[7].name),
        Icon: <BillsIcon styles={{ fill: "#D40000", width: "35px", height: "35px" }} />,
        onClick: () => history.push(cardsList[7].navigationUrl.replace("/digit-ui/", "/mycity-ui/")),
      },
      {
        name: t(cardsList[8].name),
        Icon: <CHBIcon styles={{ fill: "#D40000", width: "35px", height: "35px" }} />,
        onClick: () => history.push(cardsList[8].navigationUrl.replace("/digit-ui/", "/mycity-ui/")),
      },
      {
        name: t(cardsList[9].name),
        Icon: <BirthIcon styles={{ fill: "#D40000", width: "35px", height: "35px" }} />,
        onClick: () => history.push(cardsList[9].navigationUrl.replace("/digit-ui/", "/mycity-ui/")),
      },
      {
        name: t(cardsList[10].name),
        Icon: <OBPSIcon styles={{ fill: "#D40000", width: "35px", height: "35px" }} />,
        onClick: () => history.push(cardsList[10].navigationUrl.replace("/digit-ui/", "/mycity-ui/")),
      },
      {
        name: t(cardsList[11].name),
        Icon: <FSMIcon styles={{ fill: "#D40000", width: "35px", height: "35px" }} />,
        onClick: () => history.push(cardsList[11].navigationUrl.replace("/digit-ui/", "/mycity-ui/")),
      },
      {
        name: t(cardsList[12].name),
        Icon: <FirenocIcon stylses={{ fill: "#D40000", width: "35px", height: "35px" }} />,
        onClick: () => history.push(cardsList[12].navigationUrl.replace("/digit-ui/", "/mycity-ui/")),
      },
      {
        name: t(cardsList[13].name),
        Icon: <BillsIcon styles={{ fill: "#D40000", width: "35px", height: "35px" }} />,
        onClick: () => history.push(cardsList[13].navigationUrl.replace("/digit-ui/", "/mycity-ui/")),
      },
    ],
    styles: { display: "flex", flexWrap: "wrap", justifyContent: "flex-start", width: "100%" },
  };
  const allInfoAndUpdatesProps = {
    header: t(infoAndUpdatesObj?.headerLabel),
    sideOption: {
      name: t(infoAndUpdatesObj?.sideOption?.name),
      onClick: () => history.push(infoAndUpdatesObj?.sideOption?.navigationUrl.replace("/digit-ui/", "/mycity-ui/")),
    },
    type: "Information&Updates",
    options: [
      {
        name: t(infoAndUpdatesObj?.props?.[0]?.label),
        Icon: <HomeIcon styles={{ fill: "#D40000", width: "35px", height: "35px" }} />,
        onClick: () => history.push(infoAndUpdatesObj?.props?.[0]?.navigationUrl.replace("/digit-ui/", "/mycity-ui/")),
      },
      {
        name: t(infoAndUpdatesObj?.props?.[1]?.label),
        Icon: <Calender styles={{ fill: "#D40000", width: "35px", height: "35px" }} />,
        onClick: () => history.push(infoAndUpdatesObj?.props?.[1]?.navigationUrl.replace("/digit-ui/", "/mycity-ui/")),
      },
      {
        name: t(infoAndUpdatesObj?.props?.[2]?.label),
        Icon: <DocumentIcon styles={{ fill: "#D40000", width: "35px", height: "35px" }} />,
        onClick: () => history.push(infoAndUpdatesObj?.props?.[2]?.navigationUrl.replace("/digit-ui/", "/mycity-ui/")),
      },
      {
        name: t(infoAndUpdatesObj?.props?.[3]?.label),
        Icon: <DocumentIcon styles={{ fill: "#D40000", width: "35px", height: "35px" }} />,
        onClick: () => history.push(infoAndUpdatesObj?.props?.[3]?.navigationUrl.replace("/digit-ui/", "/mycity-ui/")),
      },
      // {
      //     name: t("CS_COMMON_HELP"),
      //     Icon: <HelpIcon/>
      // }
    ],
    styles: { display: "flex", flexWrap: "wrap", justifyContent: "flex-start", width: "100%" },
  };
  sessionStorage.removeItem("type");
  sessionStorage.removeItem("pincode");
  sessionStorage.removeItem("tenantId");
  sessionStorage.removeItem("localityCode");
  sessionStorage.removeItem("landmark");
  sessionStorage.removeItem("propertyid");

  return isLoading ? (
    <Loader />
  ) : (
    <React.Fragment>
      <style>
        {`
      .infoHome {
        height: 100px !important;
      }

      .citizenAllServiceGrid {
        display: grid;
        grid-template-columns: repeat(auto-fill, minmax(22%, 1fr)); /* up to 4 per row */
        gap: 16px; /* spacing between cards */
      }
      
      @media (max-width: 768px) {
        .citizenAllServiceGrid {
          grid-template-columns: repeat(2, 1fr); /* 2 per row on smaller screens */
        }
      }
      
      @media (max-width: 480px) {
        .citizenAllServiceGrid {
          grid-template-columns: repeat(1, 1fr); /* 1 per row on mobile */
        }
      }
      
      .CardBasedOptions .mainContent .CardBasedOptionsMainChildOption {
        width: 100% !important;
        text-align: center;
        margin: auto;
        padding: 7px
    }

    .CardBasedOptions .mainContent .CardBasedOptionsMainChildOption .ChildOptionImageWrapper {
        height: 4rem !important;
        width: 4rem !important;
    } 
      
    `}
      </style>

      <div className="HomePageContainer" style={{ width: "100%" }}>
        {/* <div className="SideBarStatic">
        <StaticCitizenSideBar />
      </div> */}
        <div className="HomePageWrapper" style={{ marginTop: "-8px" }}>
          {<div className="BannerWithSearch" style={{ marginBottom: "32px" }}>
            {/* {isMobile ? <img src={"https://niuatt-filestore.s3.ap-south-1.amazonaws.com/pg/logo/Banner+UPYOG.jpg"} /> : <img src={"https://niuatt-filestore.s3.ap-south-1.amazonaws.com/pg/logo/Banner+UPYOG.jpg"} />} */}
            {/* <div className="Search">
            <StandaloneSearchBar placeholder={t("CS_COMMON_SEARCH_PLACEHOLDER")} />
          </div> */}
            <div className="ServicesSection" style={{ paddingTop: "12px" }}>
              <strong><h3 style={{ color: "#000000", fontSize: "20px", paddingBottom: "12px", marginLeft: "7px", marginBottom: "4px" }}>Citizen Services</h3></strong>
              <CardBasedOptions style={{ marginTop: "0px", width: "calc(100% - 16px", minHeight: "650px", boxShadow: "0 0 8px rgba(0, 0, 0, 0.15)", borderRadius: "10px" }} {...allCitizenServicesProps} />
              <strong><h3 style={{ color: "#000000", fontSize: "20px", paddingBottom: "12px", marginLeft: "7px" }}>Information and Updates</h3></strong>
              <CardBasedOptions style={isMobile ? { marginTop: "0px", width: "calc(100% - 16px" } : { marginTop: "0px", width: "calc(100% - 16px", maxHeight: "150px", boxShadow: "0 0 8px rgba(0, 0, 0, 0.15)", borderRadius: "10px" }} {...allInfoAndUpdatesProps} />
            </div>
          </div>}


          {/* {(whatsAppBannerMobObj || whatsAppBannerWebObj) && (
            <div className="WhatsAppBanner">
              {isMobile ? (
                <img src={"https://nugp-assets.s3.ap-south-1.amazonaws.com/nugp+asset/Banner+UPYOG+%281920x500%29B+%282%29.jpg"} onClick={() => handleClickOnWhatsAppBanner(whatsAppBannerMobObj)} style={{ "width": "100%" }} />
              ) : (
                <img src={"https://nugp-assets.s3.ap-south-1.amazonaws.com/nugp+asset/Banner+UPYOG+%281920x500%29B+%282%29.jpg"} onClick={() => handleClickOnWhatsAppBanner(whatsAppBannerWebObj)} style={{ "width": "100%" }} />
              )}
            </div>
          )} */}

          {conditionsToDisableNotificationCountTrigger() ? (
            EventsDataLoading ? (
              <Loader />
            ) : (
              <div className="WhatsNewSection">
                <div className="headSection">
                  <h2>{t(whatsNewSectionObj?.headerLabel)}</h2>
                  <p onClick={() => history.push(whatsNewSectionObj?.sideOption?.navigationUrl)}>{t(whatsNewSectionObj?.sideOption?.name)}</p>
                </div>
                <WhatsNewCard {...EventsData?.[0]} />
              </div>
            )
          ) : null}
          <ChatBot />
        </div>
      </div>
    </React.Fragment>
  );
};

export default Home;
