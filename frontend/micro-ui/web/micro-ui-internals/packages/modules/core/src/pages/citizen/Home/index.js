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
// import ChatBot from "@demodigit/digit-ui-react-components/src/atoms/Chatbot";

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
.HomePageContainer {
  background-color: #ebf1fb;
  background-image: url(https://jaljeevanmission.gov.in/themes/edutheme/images/header-bg.png);
  background-size: cover;
  background-position: center;
}

.HomePageWrapper {
  padding: 40px 20px;
  background: linear-gradient(135deg, #f4f7fb 0%, #e9f1f9 100%);
  min-height: 100vh;
}

.HomePageWrapper h1 {
  text-align: center;
  font-size: 2.3rem;
  font-weight: 700;
  color: #004080;
  margin-bottom: 10px;
}

.HomePageWrapper p {
  text-align: center;
  font-size: 1.05rem;
  color: #444;
  margin-bottom: 30px;
  max-width: 700px;
  margin-left: auto;
  margin-right: auto;
}

.cardContainer {
  display: flex;
  flex-wrap: wrap;
  gap: 30px;
  justify-content: center;
  max-width: 1200px;
  margin: 0 auto;
}

.incidentBlock {
  background: white;
  border-radius: 16px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 250px;
  height: 160px;
  padding: 16px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  cursor: pointer;
}

.incidentBlock:hover {
  transform: scale(1.09);
  box-shadow: 0px 20px 20px rgba(0, 0, 0, 0.15);
  filter: brightness(1.05);
}

.incidentBlock img {
  width: 64px;
  height: 64px;
  object-fit: contain;
  margin-bottom: 12px;
}

.incidentBlock span {
  font-size: 1.1rem;
  font-weight: 600;
  color: #002b60;
  text-align: center;
  line-height: 1.4;
}

/* Responsive Fixes */
@media (max-width: 768px) {
  .incidentBlock {
    width: 100%;
    height: auto;
    padding: 20px;
  }

  .incidentBlock img {
    width: 56px;
    height: 56px;
  }

  .incidentBlock span {
    font-size: 0.95rem;
  }

  .HomePageWrapper h1 {
    font-size: 1.8rem;
  }

  .HomePageWrapper p {
    font-size: 0.95rem;
  }
}
`}
</style>


      <div className="HomePageWrapper">
      <h1>Welcome to Nashik Municipal Corporation</h1>
<p>Access essential municipal services with ease and transparency.</p>

      <div className="cardContainer">
      <div className="incidentBlock" onClick={() => history.push("/digit-ui/citizen/pgr-home")}>
  <img src="https://i.postimg.cc/Hkyd4wHn/icons8-complaints-96-1.png" alt="Public Grievance Redressal" />
  <span>{t("ACTION_TEST_PGR")}</span>
</div>

<div className="incidentBlock" onClick={() => history.push("/digit-ui/citizen/ws-home")}>
  <img src="https://i.postimg.cc/rFZg3yYG/1739395503649-Picture1.png" alt="Water Department" />
  <span>{t("ACTION_TEST_WATER_AND_SEWERAGE")}</span>
</div>

<div className="incidentBlock" onClick={() => history.push("/digit-ui/citizen/pt-home")}>
  <img src="https://i.postimg.cc/NFsRRGHG/icons8-property-100-1.png" alt="Property Tax Assessment" />
  <span>{t("ACTION_TEST_PT")}</span>
</div>

<div className="incidentBlock" onClick={() => history.push("/digit-ui/citizen/obps-home")}>
  <img src="https://i.postimg.cc/2664rXqp/BPA-removebg-preview.png" alt="Building Plan Approval" />
  <span>{t("ACTION_TEST_OBPS")}</span>
</div>

<div className="incidentBlock" onClick={() => history.push("/digit-ui/citizen/mcollect-home")}>
  <img src="https://i.postimg.cc/wvVmxKbC/icons8-mail-open-100.png" alt="Miscellaneous Collection" />
  <span>{t("ACTION_TEST_MCOLLECT")}</span>
</div>

<div className="incidentBlock" onClick={() => history.push("/digit-ui/citizen/tl-home")}>
  <img src="https://i.postimg.cc/gk562HN2/icons8-license-100.pngg" alt="Trade License" />
  <span>{t("ACTION_TEST_TL")}</span>
</div>

<div className="incidentBlock" onClick={() => history.push("")}>
  <img src="https://i.postimg.cc/59wr3Hdp/icons8-recycle-bin-100.png" alt="Solid Waste Management" />
  <span>{t("ACTION_TEST_SWM")}</span>
</div>

<div className="incidentBlock" onClick={() => history.push("/digit-ui/citizen/fsm-home")}>
  <img src="https://i.postimg.cc/vZ5560YX/icons8-truck-100.png" alt="Solid Waste Management" />
  <span>{t("ACTION_TEST_FSM")}</span>
</div>
<div className="incidentBlock" onClick={() => history.push("")}>
  <img src="https://i.postimg.cc/k5FQGY1d/icons8-fire-100.png" alt="Solid Waste Management" />
  <span>{t("ACTION_TEST_FIRE_NOC")}</span>
</div>
<div className="incidentBlock" onClick={() => history.push("")}>
  <img src="https://i.postimg.cc/Pr125wyY/icons8-vending-machine-100.png" alt="Solid Waste Management" />
  <span>{t("Street Vending")}</span>
</div>
<div className="incidentBlock" onClick={() => history.push("")}>
  <img src="https://i.postimg.cc/RFrzz9hR/icons8-baby-100.png" alt="Solid Waste Management" />
  <span>{t("ACTION_TEST_BIRTH")}</span>
</div>
<div className="incidentBlock" onClick={() => history.push("")}>
  <img src="https://i.postimg.cc/CKhqsTSc/icons8-death-100-1.png" alt="Solid Waste Management" />
  <span>{t("ACTION_TEST_DEATH")}</span>
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
        <div style={{ display: isMobile ? "" : "flex",  width: isMobile ? "100%" : "" }}>


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
