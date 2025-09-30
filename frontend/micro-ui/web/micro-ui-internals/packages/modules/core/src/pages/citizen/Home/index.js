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

  OBPSIcon,
  WSICon,
} from "@demodigit/digit-ui-react-components";
import WhatsNewCard from "../../../../../../react-components/src/atoms/WhatsNewCard"
import ChatBot from "./ChatBot";
// import ChatBot from "frontend\micro-ui\web\micro-ui-internals\packages\react-components\src\atoms\ChatBot.js"
// import ChatBot from "@upyog/digit-ui-react-components/src/atoms/Chatbot";

import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";
import { CitizenSideBar } from "../../../components/TopBarSideBar/SideBar/CitizenSideBar";
import StaticCitizenSideBar from "../../../components/TopBarSideBar/SideBar/StaticCitizenSideBar";
import BannerCarousel from "./BannerCarsousel";

const Home = () => {
  const NotificationsOrWhatsNew = Digit.ComponentRegistryService.getComponent("NotificationsAndWhatsNew");
  const { t } = useTranslation();
  const history = useHistory();
  const tenantId = Digit.ULBService.getCitizenCurrentTenant(true);
  const User = Digit.UserService.getUser();
  const [resolved,setResolved]= useState(0)
  const [pending,setPending]= useState(0)
  const [submitted,setSubmitted]= useState(0)
  const [expandedRows, setExpandedRows] = useState({});

  const mobileNumber = User?.mobileNumber || User?.info?.mobileNumber || User?.info?.userInfo?.mobileNumber;
  const { data: { stateInfo, uiHomePage } = {}, isLoading } = Digit.Hooks.useStore.getInitData();
  const { data: { unreadCount: unreadNotificationCount } = {}, isSuccess: notificationCountLoaded } = Digit.Hooks.useNotificationCount({
    tenantId: tenantId,
    config: {
      enabled: Digit.UserService?.getUser()?.access_token ? true : false,
    },
  });
  const [currentPage, setCurrentPage] = useState(1);
  const rowsPerPage = 5; // Number of rows per page


  let isMobile = window.Digit.Utils.browser.isMobile();
  if (window.Digit.SessionStorage.get("TL_CREATE_TRADE")) window.Digit.SessionStorage.set("TL_CREATE_TRADE", {})

  const conditionsToDisableNotificationCountTrigger = () => {
    if (Digit.UserService?.getUser()?.info?.type === "EMPLOYEE") return false;
    if (!Digit.UserService?.getUser()?.access_token) return false;
    return true;
  };
  const toggleRow = (id) => {
    setExpandedRows(prev => ({
      ...prev,
      [id]: !prev[id],
    }));
  }
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
  const complainData= complaint?.data?.ServiceWrappers || [];
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
console.log("EventsDataEventsData",EventsData)
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
  console.log("opencomplaints", complaint)

  


  useEffect( () =>
  {
    const fetchData = async () => {
      const resolved = await Digit.PGRService.search(tenantId, { mobileNumber, applicationStatus: "RESOLVED" });
      const pendingAtLme = await Digit.PGRService.search(tenantId, { mobileNumber, applicationStatus: "PENDINGATLME" });
      const pendingAtGro = await Digit.PGRService.search(tenantId, { mobileNumber, applicationStatus: "PENDINGATSUPERVISOR" });
      const submitted = await Digit.PGRService.search(tenantId, { mobileNumber, applicationStatus: "PENDINGFORASSIGNMENT" });
  
      setResolved(resolved?.ServiceWrappers.length || 0);
      setSubmitted(submitted?.ServiceWrappers.length || 0);
      setPending((pendingAtLme?.ServiceWrappers.length || 0) + (pendingAtGro?.ServiceWrappers.length || 0));
    };
  
    fetchData();

  },[])

  return isLoading ? (
    <Loader />
  ) : (



    <div className="HomePageContainer" style={{ width: "100%",display:"flex",flexDirection:"column",marginTop:"70px",marginRight:"20px",marginLeft:"20px" }}>
    <style>
      {`
      .h1, .h2, .h3, .h4, .h5, .h6, h1, h2, h3, h4, h5, h6 {
        margin-top: 0;
        margin-bottom: 0.5rem;
        font-weight: 500;
        line-height: 1.2;
        color: var(--bs-heading-color);
    }
      .notify-bar {
        position: fixed;
        top: calc(var(--ribbon-height) + var(--header-height));
        left: var(--sidebar-width);
        width: calc(100% - var(--sidebar-width));
        height: var(--notify-height);
        line-height: var(--notify-height);
        background: #f8f9fa;
        border-top: 1px solid #ddd;
        border-bottom: 1px solid #ddd;
        font-size: 0.9rem;
        font-weight: 500;
        color: #333;
        z-index: 1080;
        overflow: hidden;
        transition: left 0.3s, width 0.3s;
        margin-left: -20px;
    }
    .notify-bar marquee {
      color: #d32f2f;
      font-weight: 600;
  }
        .row {
          display: flex;
          flex-wrap: wrap;
          margin: -10px;
        }
        .col {
          flex: 1;
          padding: 10px;
          min-width: 200px;
        }
        .service-card {
          display: flex;
          flex-direction: column;
          justify-content: center;
          align-items: center;
          padding: 20px;
          border-radius: 12px;
          text-align: center;
          background: #fff;
          box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
          transition: transform 0.2s ease, box-shadow 0.2s ease;
          cursor: pointer;
          height: 140px;
        }
        .service-card:hover {
          transform: translateY(-8px);
          box-shadow: 0 12px 24px rgba(0,0,0,0.15);
          background: linear-gradient(135deg, #004080, #0066cc, #00599f, #1a75c4);
          color: white;
        }
        .service-card-body {
          display: flex;
          flex-direction: column;
          align-items: center;
        }
        .icon-wrapper {
          display: flex;
          justify-content: center;
          align-items: center;
          width: 56px;
          height: 56px;
          border-radius: 50%;
          margin-bottom: 12px;
          font-size: 1.5rem;
          background: linear-gradient(135deg, #00599f, #00aaff);
          color: #fff;
        }
        .label-text {
          font-weight: 600;
          font-size: 16px;
          text-align: center;
        }

        /* Table styling */
        .custom-table {
          width: 100%;
          border-collapse: collapse;
          margin-top: 15px;
          background: #fff;
          box-shadow: 0 2px 6px rgba(0,0,0,0.1);
          border-radius: 8px;
          overflow: hidden;
        }
        .custom-table th, .custom-table td {
          padding: 12px 16px;
          border-bottom: 1px solid #eee;
          text-align: left;
        }
        .custom-table th {
          background: #00599f;
          color: #fff;
          font-weight: 600;
        }
        .custom-table tr:hover {
          background: #f1f7ff;
        }
        .banner {
          display: -webkit-box;
          display: -ms-flexbox;
          display: flex;
          -webkit-box-pack: center;
          -ms-flex-pack: center;
          justify-content: center;
          -webkit-box-align: center;
          -ms-flex-align: center;
          align-items: center;
          
          position: relative; }
          .banner .bannerCard {
            min-width: 400px; }
          .banner .bannerLogo {
            width: 80px;
            height: 40px;
            -o-object-fit: contain;
            object-fit: contain;
            padding-right: 10px;
            margin-right: 10px;
            border-right: 1px solid #0b0c0c; }
          .banner .bannerHeader {
            display: -webkit-box;
            display: -ms-flexbox;
            display: flex;
            -webkit-box-pack: center;
            -ms-flex-pack: center;
            justify-content: center;
            -webkit-box-align: center;
            -ms-flex-align: center;
            align-items: center;
            margin-bottom: 24px; }
            .banner {
              height: auto !important; }
          .mb-3-new{
            font-size: calc(1.275rem + .3vw);
    margin-top: 2rem !important;
    margin-bottom: 0rem !important;
          }

              
      `}
    </style>
    <div className="notify-bar">
          <marquee behavior="scroll" direction="left" scrollAmount="6">
            ⚡ Application deadline for Trade License renewal is 30th September |{" "}
            🌐 New Online Property Tax module launched |{" "}
            📢 Water supply maintenance on 25th August 10AM–4PM
          </marquee>
        </div>
    <BannerCarousel />
    {/* Top 3 Cards */}
    <div className="row">
    <div className="col">
        <div className="service-card" onClick={() => history.push("/digit-ui/citizen/pgr/create-complaint/complaint-type")}>
          <div className="service-card-body">
            <span className="icon-wrapper">
            <i class="fas fa-exclamation-triangle"></i>
            </span>
            <span className="label-text">{t("New Grievances")}</span>
          </div>
        </div>
      </div>
      <div className="col">
  <div
    className="service-card"
    onClick={() => history.push("/digit-ui/citizen/pgr/create-complaint/complaint-type")}
  >
    <div className="service-card-body">
      <span
        className="icon-wrapper"
        style={{fontWeight: 600}}
      >
        {/* Main Icon */}


        {/* Red Badge */}
          {submitted}

      </span>

      {/* Label Text */}
      <span className="label-text">{t("Submitted Grievances")}</span>
    </div>
  </div>
</div>



      <div className="col">
        <div className="service-card" onClick={() => history.push("#")}>
          <div className="service-card-body">
            <span className="icon-wrapper" style={{fontWeight: 600}}>{resolved}</span>
            <span className="label-text">{t("Disposed Grievances")}</span>
          </div>
        </div>
      </div>
      <div className="col">
        <div className="service-card" onClick={() => history.push("#")}>
          <div className="service-card-body">
            <span className="icon-wrapper" style={{fontWeight: 600}}>{pending}</span>
            <span className="label-text">{t("Pending Grievances")}</span>
          </div>
        </div>
      </div>
    </div>
    <h4 className="mb-3-new">Grievance Details</h4>
    {/* Table with 5 Columns */}

    <table className="custom-table" style={{ tableLayout: "fixed", width: "100%" }}>
  <thead>
    <tr>
      <th style={{ width: "20%" }}>Request ID</th>
      <th style={{ width: "20%" }}>Service Code</th>
      <th style={{ width: "20%" }}>Status</th>
      <th style={{ width: "20%" }}>Priority</th>
      <th style={{ width: "20%" }}>Description</th>
    </tr>
  </thead>
  <tbody>
    {complaints && complaints.map((item, index) => {
      const service = item.service;
      const description = service.description || "-";
      const isExpanded = expandedRows[service.serviceRequestId];
      const truncated = description.length > 50 && !isExpanded
        ? description.slice(0, 20) + "..."
        : description;

      return (
        <tr
          key={service.serviceRequestId}
          onClick={() => history.push(`/digit-ui/citizen/pgr/complaints/${service.serviceRequestId}`)}
          style={{ cursor: "pointer" }}
        >
          <td style={{ width: "20%", wordWrap: "break-word" }}>{service.serviceRequestId}</td>
          <td style={{ width: "20%", wordWrap: "break-word" }}>
            {t(`SERVICEDEFS.${service.serviceCode.toUpperCase()}`)}
          </td>
          <td style={{ width: "20%", wordWrap: "break-word" }}>
            {t(`CS_COMMON_${service.applicationStatus}`)}
          </td>
          <td style={{ width: "20%", wordWrap: "break-word" }}>{service.priority}</td>
          <td style={{ width: "20%", wordWrap: "break-word" }}>
            {truncated}
            {description.length > 50 && (
              <span
                style={{ color: "#00599f", cursor: "pointer", marginLeft: "5px" }}
                onClick={(e) => { e.stopPropagation(); toggleRow(service.serviceRequestId); }}
              >
                {isExpanded ? "Show less" : "Show more"}
              </span>
            )}
          </td>
        </tr>
      );
    })}
  </tbody>
</table>


    {conditionsToDisableNotificationCountTrigger() ? (
          EventsDataLoading ? (
            <Loader />
          ) : (
            <div className="WhatsNewSection" style={{marginTop:"35px"}}>
              <div className="headSection" style={{padding:"0px",display:"flex",justifyContent:"space-between"}}>
                <h4 className="mb-3" style={{fontSize:"1.5rem"}} >{t(whatsNewSectionObj?.headerLabel)}</h4>
                <h2 className="mb-3" onClick={() => history.push(whatsNewSectionObj?.sideOption?.navigationUrl)}>{t(whatsNewSectionObj?.sideOption?.name)}</h2>

              </div>
              <WhatsNewCard {...EventsData?.[0]} />
            </div>
          )
        ) : null}
            <ChatBot/>
  </div>


  );
};

export default Home;
