import React, { useEffect } from "react";
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
} from "@demodigit/digit-ui-react-components";

import ChatBot from "./ChatBot";
// import ChatBot from "frontend\micro-ui\web\micro-ui-internals\packages\react-components\src\atoms\ChatBot.js"
// import ChatBot from "@upyog/digit-ui-react-components/src/atoms/Chatbot";

import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";
import { CitizenSideBar } from "../../../components/TopBarSideBar/SideBar/CitizenSideBar";
import StaticCitizenSideBar from "../../../components/TopBarSideBar/SideBar/StaticCitizenSideBar";


const Home = () => {
  const NotificationsOrWhatsNew = Digit.ComponentRegistryService.getComponent("NotificationsAndWhatsNew");
  const { t } = useTranslation();
  const history = useHistory();
  const tenantId = Digit.ULBService.getCitizenCurrentTenant(true);
  const User = Digit.UserService.getUser();

  const mobileNumber = User?.mobileNumber || User?.info?.mobileNumber || User?.info?.userInfo?.mobileNumber;
  const { data: { stateInfo, uiHomePage } = {}, isLoading } = Digit.Hooks.useStore.getInitData();
  const { data: { unreadCount: unreadNotificationCount } = {}, isSuccess: notificationCountLoaded } = Digit.Hooks.useNotificationCount({
    tenantId: tenantId,
    config: {
      enabled: Digit.UserService?.getUser()?.access_token ? true : false,
    },
  });
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
  const { error, data: complaint, revalidate } = Digit.Hooks.pgr.useComplaintsListByMobile(tenantId, mobileNumber);
  if (!tenantId) {
    console.log("tenantIdtenantId",tenantId,Digit.SessionStorage.get("locale"))
    Digit.SessionStorage.set("locale", "en_IN");
    const selectedCity =  {
      "i18nKey": "TENANT_TENANTS_PG_CITYA",
      "code": "pg.citya",
      "name": "Kamaladiha",
      "description": "City A",
      "pincode": [
          143001,
          143002,
          143003,
          143004,
          143005
      ],
      "logoId": "https://in-egov-assets.s3.ap-south-1.amazonaws.com/in.citya/logo.png",
      "imageId": null,
      "domainUrl": "https://www.upyog.niua.org",
      "type": "CITY",
      "twitterUrl": null,
      "facebookUrl": null,
      "emailId": "citya@gmail.com",
      "OfficeTimings": {
          "Mon - Fri": "9.00 AM - 6.00 PM"
      },
      "isPopular": true,
      "city": {
          "name": "City A",
          "localName": null,
          "districtCode": "CUTTACK",
          "districtName": "Cuttack",
          "districtTenantCode": "pg.citya",
          "blockname": "Narasinghpur",
          "regionName": null,
          "ulbGrade": "Gram Panchayat",
          "longitude": 75.5761829,
          "latitude": 31.3260152,
          "shapeFileLocation": null,
          "captcha": null,
          "code": "1013",
          "ddrName": "DDR A"
      },
      "address": "City A Gram Panchayat",
      "contactNumber": "001-2345876"
  }
    Digit.SessionStorage.set("CITIZEN.COMMON.HOME.CITY", selectedCity);
    Digit.SessionStorage.get("locale") === null
      ? history.push(`/digit-ui/citizen/select-language`)
      : history.push(`/digit-ui/citizen/login`);
  }
  useEffect(() => {
    if (!tenantId) {
      console.log("tenantId", tenantId, "User", User);
      Digit.SessionStorage.set("locale", "en_IN");
      const selectedCityNew =  {
        "i18nKey": "TENANT_TENANTS_PG_CITYA",
        "code": "pg.citya",
        "name": "Kamaladiha",
        "description": "City A",
        "pincode": [
            143001,
            143002,
            143003,
            143004,
            143005
        ],
        "logoId": "https://in-egov-assets.s3.ap-south-1.amazonaws.com/in.citya/logo.png",
        "imageId": null,
        "domainUrl": "https://www.upyog.niua.org",
        "type": "CITY",
        "twitterUrl": null,
        "facebookUrl": null,
        "emailId": "citya@gmail.com",
        "OfficeTimings": {
            "Mon - Fri": "9.00 AM - 6.00 PM"
        },
        "isPopular": true,
        "city": {
            "name": "City A",
            "localName": null,
            "districtCode": "CUTTACK",
            "districtName": "Cuttack",
            "districtTenantCode": "pg.citya",
            "blockname": "Narasinghpur",
            "regionName": null,
            "ulbGrade": "Gram Panchayat",
            "longitude": 75.5761829,
            "latitude": 31.3260152,
            "shapeFileLocation": null,
            "captcha": null,
            "code": "1013",
            "ddrName": "DDR A"
        },
        "address": "City A Gram Panchayat",
        "contactNumber": "001-2345876"
    }
      Digit.SessionStorage.set("CITIZEN.COMMON.HOME.CITY", selectedCityNew);
      Digit.SessionStorage.get("locale") === null
        ? history.push(`/digit-ui/citizen/select-language`)
        : history.push(`/digit-ui/citizen/login`);
    }
  }, []);
  
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

  const allCitizenServicesProps = {
    header: t(citizenServicesObj?.headerLabel),
    sideOption: {
      name: t(citizenServicesObj?.sideOption?.name),
      onClick: () => history.push(citizenServicesObj?.sideOption?.navigationUrl),
    },
    options: [
      {
        name: t(citizenServicesObj?.props?.[0]?.label),
        Icon: <ComplaintIcon />,
        onClick: () => history.push(citizenServicesObj?.props?.[0]?.navigationUrl),
      },
      {
        name: t(citizenServicesObj?.props?.[1]?.label),
        Icon: <PTIcon className="fill-path-primary-main" />,
        onClick: () => history.push(citizenServicesObj?.props?.[1]?.navigationUrl),
      },
      {
        name: t(citizenServicesObj?.props?.[2]?.label),
        Icon: <CaseIcon className="fill-path-primary-main" />,
        onClick: () => history.push(citizenServicesObj?.props?.[2]?.navigationUrl),
      },
      // {
      //     name: t("ACTION_TEST_WATER_AND_SEWERAGE"),
      //     Icon: <DropIcon/>,
      //     onClick: () => history.push("/digit-ui/citizen")
      // },
      {
        name: t(citizenServicesObj?.props?.[3]?.label),
        Icon: <WSICon />,
        onClick: () => history.push(citizenServicesObj?.props?.[3]?.navigationUrl),
      },
    ],
    styles: { display: "flex", flexWrap: "wrap", justifyContent: "flex-start", width: "100%" },
  };
  const allInfoAndUpdatesProps = {
    header: t(infoAndUpdatesObj?.headerLabel),
    sideOption: {
      name: t(infoAndUpdatesObj?.sideOption?.name),
      onClick: () => history.push(infoAndUpdatesObj?.sideOption?.navigationUrl),
    },
    options: [
      {
        name: t(infoAndUpdatesObj?.props?.[0]?.label),
        Icon: <HomeIcon />,
        onClick: () => history.push(infoAndUpdatesObj?.props?.[0]?.navigationUrl),
      },
      {
        name: t(infoAndUpdatesObj?.props?.[1]?.label),
        Icon: <Calender />,
        onClick: () => history.push(infoAndUpdatesObj?.props?.[1]?.navigationUrl),
      },
      {
        name: t(infoAndUpdatesObj?.props?.[2]?.label),
        Icon: <DocumentIcon />,
        onClick: () => history.push(infoAndUpdatesObj?.props?.[2]?.navigationUrl),
      },
      {
        name: t(infoAndUpdatesObj?.props?.[3]?.label),
        Icon: <DocumentIcon />,
        onClick: () => history.push(infoAndUpdatesObj?.props?.[3]?.navigationUrl),
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
  let complaints = complaint?.ServiceWrappers
  const opencomplaints = complaints?.filter((code) => code.service.applicationStatus !== "RESOLVED");
  const closecomplaints = complaints?.filter((code) => code.service.applicationStatus == "RESOLVED");
  console.log("opencomplaints", opencomplaints, closecomplaints)
  //  const data = [
  //   { label: 'Open Incident', value: opencomplaints?.length ||0 },
  //   { label: 'Close Incident', value: closecomplaints?.length || 0 }
  // ];
  const dataNew = [
    { label: 'Red', value: 5 },
    { label: 'Green', value: 4 }
  ];
  const data = [
    { label: 'Open Incident', value: 10 }, // Replace with opencomplaints?.length
    { label: 'Close Incident', value: 6 } // Replace with closecomplaints?.length
  ];
  console.log("vvvv", citizenServicesObj?.props?.[1]?.navigationUrl)
  return isLoading ? (
    <Loader />
  ) : (
    <div className="HomePageContainer" style={{ width: "100%" }}>
      {/* <div className="SideBarStatic">
        <StaticCitizenSideBar />
      </div> */}
      <style>
        {`
        .logoText {
            font-size: 26px;
            color: #0a97d5;
            font-family: 'PoppinsBold';
            line-height: 25px;
            transition: all 0.5s ease;
        }
        .newlogoWrap h1.logo a .logoText .logoTextSubline {
          font-size: 18px;
          font-family: 'PoppinsRegular';
          color: #323232;
          line-height: 20px;
          display: block;
          transition: all 0.5s ease;
      }
      .newlogoWrap h1.logo a {
        display: flex;
        text-decoration: none!important;
        align-items: center;
    }
    .incidentBlock{
      width:100%;
      height:200px;
     margin:10px;
     padding:20px;
     background-color:white;
     display:flex;
     align-items:center;
     font-size: 22px;
     font-weight: bold;
      box-shadow:rgba(0, 0, 0, 0.35) 0px 5px 15px;
      border-radius:15px;
      cursor : pointer;
      border: 4px solid #0a97d5;

    }
    .mydashboard{
      height: 100%;
      width: 100%;
      padding: 25px;
      font-size: 26px;
      font-weight: bold;
    }
    .dash{
      border-top-right-radius: 10px;
    border-top-left-radius: 10px;
    padding-left:15px;
    }
    .charts{
      height:250px
    }
    #chart-container {
      position: relative;
      width: 200px;
      height: 200px;
  }
  
  .donut-chart {
      position: absolute;
      width: 100%;
      height: 100%;
      border-radius: 50%;
      clip: rect(0, 200px, 200px, 100px); /* clip to the right half */
  }
  
  .inner-circle {
      position: absolute;
      top: 50%;
      left: 50%;
      width: 100px;
      height: 100px;
      margin-top: -50px; /* half of the height */
      margin-left: -50px; /* half of the width */
      background-color: #f0f0f0;
      border-radius: 50%;
      z-index: 1;
  }
  .incidentTable{
    display: flex;
    flex-direction: column;
    font-size: 18px;
    padding: 10px;
    box-shadow: rgba(0, 0, 0, 0.35) 0px 5px 15px;
    margin: 15px;
  }
  .donut-chart {
    position: relative;
    width: 200px;
    height: 200px;
    border-radius: 50%;
    background: conic-gradient(
      var(--color, #ddd) calc(var(--offset, 0) * 1%),
      var(--next-color, #ddd) 0 calc((var(--offset, 0) + var(--value, 0)) * 1%)
    );
  }
  
  .donut-segment {
    --next-color: var(--color);
  }
  
  .donut-hole {
    position: absolute;
    top: 50%;
    left: 50%;
    width: 100px;
    height: 100px;
    background: #fff;
    border-radius: 50%;
    transform: translate(-50%, -50%);
  }
  .donutchart{
    height: 75% !important;
    width:  75% !important;

  }
  
  .HomePageContainer {background-color: #ebf1fb;
    background-image: url(https://jaljeevanmission.gov.in//themes/edutheme/images/header-bg.png);
  }
        `
        }
      </style>
      <div className="HomePageWrapper">
        <div style={{ display: "flex", flexDirection: isMobile ? "column" : "row", padding: "25px", width: "100%", marginTop: isMobile ? "10%" : "3%" }}>
          <div className="incidentBlock" style={{ marginLeft: isMobile ? "0px" : "" }} onClick={() => history.push("/digit-ui/citizen/pgr-home")}>
            <div style={{ width: "50%", cursor: "pointer" }} >
              <span>{t("Public Grievance Redressal")}</span>
            </div>
            <div style={{ width: "50%", display: "flex", flexDirection: "row-reverse" }}>
              <div style={{ display: "flex", flexDirection: "row-reverse" }}>


                <img src="https://chstage.blob.core.windows.net/assets/tmp/incon1.png" style={{ maxWidth: "65%" }}></img>
              </div>
            </div>
          </div>
          <div className="incidentBlock" style={{ marginLeft: isMobile ? "0px" : "" }} onClick={() => history.push("/digit-ui/citizen/ws-home")} >
            <div style={{ width: "50%", cursor: "pointer" }} >
              <span>{t("Water Department")}</span>
            </div>
            <div style={{ width: "50%", display: "flex", flexDirection: "row-reverse" }}>
              <div style={{ display: "flex", flexDirection: "row-reverse" }}>


                <img src="https://i.postimg.cc/rFZg3yYG/1739395503649-Picture1.png" style={{ maxWidth: "65%" }}></img>
              </div>
            </div>
          </div>
          {/* <div className="incidentBlock" style={{marginLeft:isMobile?"0px":"", cursor:"pointer"}}>
    <div style={{width:"50%"}} onClick={()=> history.push("/digit-ui/citizen/pgr-home")}>
<span>{t("My Incident")}</span>
    </div>
    <div style={{width:"50%",display:"flex",flexDirection:"row-reverse"}}>
      <div style={{display:"flex",flexDirection:"row-reverse"}}>
        <img src="https://chstage.blob.core.windows.net/assets/tmp/incon1.png" style={{maxWidth:"65%"}}></img>
    </div>
    </div>
  </div> */}

        </div>
        <div style={{ display: isMobile ? "" : "flex", height: "66vh", width: isMobile ? "100%" : "" }}>


        </div>


      </div>

      <div style={{
        position: 'fixed',
        right: '30px',
        bottom: '30px',
        zIndex: 1
      }}>
        {/* <button style={{
        backgroundColor: 'blue', 
        borderRadius: '15px', 
        color: 'white', 
        fontSize: 'medium', 
        padding: '10px 20px', 
        border: 'none', 
        cursor: 'pointer' 
      }}>Chatbot</button>  */}
        <ChatBot />
      </div>

    </div>

  );
};

export default Home;
