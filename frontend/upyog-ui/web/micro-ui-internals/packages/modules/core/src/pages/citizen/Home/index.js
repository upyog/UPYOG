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
import DueRenewalsModal from "../../../components/DueRenewalsModal";

const Home = ({ layout }) => {
  const { t } = useTranslation();
  const location = useLocation();
  const navigate = Digit.Hooks.useCustomNavigate();
  const tenantId = Digit.ULBService.getCitizenCurrentTenant(true);
  const [user, setUser] = useState(Digit.UserService.getUser() || null);
  const [UIType, setUIType] = useState({});
  const [searchTerm, setSearchTerm] = useState("");
  const [isDueRenewalsModalOpen, setIsDueRenewalsModalOpen] = useState(false);
  const DEFAULT_REDIRECT_URL = "/upyog-ui/citizen";
  const { data: { stateInfo, uiHomePage } = {}, isLoading } = Digit.Hooks.useStore.getInitData();
  let isMobile = window.Digit.Utils.browser.isMobile();
  if (window.Digit.SessionStorage.get("TL_CREATE_TRADE")) window.Digit.SessionStorage.set("TL_CREATE_TRADE", {});

  const conditionsToDisableNotificationCountTrigger = () => {
    if (Digit.UserService?.getUser()?.info?.type === "EMPLOYEE") return false;
    if (!Digit.UserService?.getUser()?.access_token) return false;
    return true;
  };

  const { data: aggregateData, isLoading: isAggregateLoading } = Digit.Hooks.useCitizenAggregate({
    tenantId,
    config: {
      enabled: conditionsToDisableNotificationCountTrigger(),
    },
  });

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
      },
      case3: {
        bannerView: false,
        welcomeView: true,
        quickServices: true,
        userActions: true,
        summary: true,
      },
    };
    setUIType(DashboardLayout.case2);
  }, []);

  const responses = aggregateData?.responses || {};
  const dueRenewalsRaw = responses?.["due-renewals"]?.data?.bills || responses?.["due-renewals"]?.data || [];
  const dueRenewals = Array.isArray(dueRenewalsRaw) ? dueRenewalsRaw : [];

  const actionHandlers = {
    onOpenDueRenewals: () => setIsDueRenewalsModalOpen(true),
  };

  const userActionCards = buildUserActionCards(aggregateData, t, navigate, tenantId, actionHandlers, EventsData);
  const taskSummaryCards = buildTaskSummaryCards(aggregateData, t, navigate, actionHandlers, EventsData);
  const hasAnyData = userActionCards.length > 0;
  const userName = user?.info?.name || user?.name || "Citizen";

  return isLoading ? (
    <Loader />
  ) : (
    <div className="HomePageContainer">
      {/* <div className="SideBarStatic">
        <StaticCitizenSideBar />
      </div> */}
      <div className="HomePageWrapper">
        {UIType?.bannerView && (
          <div className="BannerWithSearch">
            <div className="BannerBoard">
              <h1>UPYOG</h1>
              <p className="BannerSubheader">
                <span>U</span>rban <span>P</span>latform for deliver<span>Y</span> <br />
                of <span>O</span>nline <span>G</span>overnance
              </p>
              <div className="hero-stats">
                <article className="hero-stats__item">
                  <span className="hero-stats__icon">
                    <img src="/upyog-ui/images/bannerIcon1.svg" alt="banner Icon" />
                  </span>

                  <div className="hero-stats__content">
                    <h3 className="hero-stats__count">12</h3>
                    <p className="hero-stats__label">Total Applications</p>
                  </div>
                </article>
                <br />
                <article className="hero-stats__item pending-requests">
                  <span className="hero-stats__icon">
                    <img src="/upyog-ui/images/bannerIcon2.svg" alt="banner Icon" />
                  </span>

                  <div className="hero-stats__content">
                    <h3 className="hero-stats__count">05</h3>
                    <p className="hero-stats__label">Pending Requests</p>
                  </div>
                </article>
                <br />
                <article className="hero-stats__item services">
                  <span className="hero-stats__icon hero-stats__icon--red">
                    <img src="/upyog-ui/images/bannerIcon3.svg" alt="banner Icon" />
                  </span>

                  <div className="hero-stats__content">
                    <h3 className="hero-stats__count">28</h3>
                    <p className="hero-stats__label">Our Services</p>
                  </div>
                </article>
              </div>
            </div>
            <div className="ServicesSection">
              <CardBasedOptions {...returnConstants(t, "allCitizenServicesProps", citizenServicesObj, navigate)} />

              <CardBasedOptions {...returnConstants(t, "allInfoAndUpdatesProps", infoAndUpdatesObj, navigate)} />
            </div>
          </div>
        )}

        {UIType?.summary && (
          <div className="home-summary-section">
            <HeaderBoxSection cards={taskSummaryCards} columns={4} />
          </div>
        )}

        {UIType?.welcomeView && (
          <div className="welcomeBanner">
            <p>Welcome, {userName}</p>
            <h1>What are you working on today?</h1>
            <div>
              <img src="/upyog-ui/images/search.svg" alt="search icon" />
              <input
                type="text"
                placeholder="Search by applicant number, ID, name or module..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                onKeyDown={(e) => {
                  if (e.key === "Enter" && searchTerm) {
                    navigate(`/upyog-ui/citizen/search-application?id=${encodeURIComponent(searchTerm)}`);
                  }
                }}
              />
              <button
                onClick={() => {
                  if (searchTerm) {
                    navigate(`/upyog-ui/citizen/search-application?id=${encodeURIComponent(searchTerm)}`);
                  }
                }}
              >
                Search
              </button>
            </div>
          </div>
        )}

        <div className="home-services-actions-section">
          {UIType?.quickServices && (
            <HeaderBoxSection title={getLabel(t, "CITIZEN_QUICK_SERVICES", "Quick Services")} cards={returnConstants(t, "quickServiceCards", navigate)} variant="service" columns={4} />
          )}
          {UIType?.userActions && (
            isAggregateLoading ? (
              <div className="home-loader-container"><Loader /></div>
            ) : (
              <UserActionLayout cards={userActionCards} showEmpty={!hasAnyData} />
            )
          )}
        </div>



        {false && (whatsAppBannerMobObj || whatsAppBannerWebObj) && (
          <div className="WhatsAppBanner">
            {isMobile ? (
              <img
                src={"https://nugp-assets.s3.ap-south-1.amazonaws.com/nugp+asset/Banner+UPYOG+%281920x500%29B+%282%29.jpg"}
                onClick={() => handleClickOnWhatsAppBanner(whatsAppBannerMobObj)}
                className="w-full"
              />
            ) : (
              <img
                src={"https://nugp-assets.s3.ap-south-1.amazonaws.com/nugp+asset/Banner+UPYOG+%281920x500%29B+%282%29.jpg"}
                onClick={() => handleClickOnWhatsAppBanner(whatsAppBannerWebObj)}
                className="w-full"
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

        <DueRenewalsModal
          isOpen={isDueRenewalsModalOpen}
          onClose={() => setIsDueRenewalsModalOpen(false)}
          bills={dueRenewals}
          tenantId={tenantId}
          navigate={navigate}
        />
      </div>
    </div>
  );
};

const getLabel = (t, key, fallback) => {
  if (!key) return fallback;
  const translated = t(key);
  if (!translated || translated === key) {
    return fallback;
  }
  return translated;
};

const getModuleIcon = (service = "") => {
  const s = (service || "").toLowerCase();
  if (s.includes("chb")) return "🏛️";
  if (s.includes("pt") || s.includes("property")) return "🏠";
  if (s.includes("adv")) return "📣";
  if (s.includes("pet") || s.includes("ptr")) return "🐾";
  if (s.includes("bpa") || s.includes("obps")) return "🏗️";
  if (s.includes("ws") || s.includes("water") || s.includes("sw")) return "💧";
  if (s.includes("fsm")) return "🚛";
  if (s.includes("tl") || s.includes("trade")) return "📜";
  if (s.includes("fire")) return "🚒";
  if (s.includes("gc") || s.includes("garbage")) return "🗑️";
  if (s.includes("challan")) return "🧾";
  if (s.includes("est")) return "🏢";
  if (s.includes("ndc")) return "📄";
  return "📋";
};

const formatNotifOneLiner = (text = "", fallback = "") => {
  const cleaned = String(text || fallback || "")
    .replace(/\s+/g, " ")
    .trim();
  if (!cleaned) return "";
  return cleaned.length > 78 ? `${cleaned.slice(0, 75)}...` : cleaned;
};

const getNotificationTypeMeta = (notif = {}, t) => {
  const rawType = (
    notif?.type ||
    notif?.category ||
    notif?.eventType ||
    notif?.businessService ||
    notif?.source ||
    ""
  )
    .toString()
    .toUpperCase();

  if (rawType.includes("EVENT")) {
    return {
      status: getLabel(t, "CITIZEN_NOTIF_TYPE_EVENT", "Event"),
      statusVariant: "primary",
      icon: "📅",
      defaultLine: getLabel(t, "CITIZEN_NOTIF_EVENT_LINE", "Upcoming municipal event • Tap to view schedule & details"),
    };
  }
  if (rawType.includes("BILL") || rawType.includes("PAY") || rawType.includes("DUE") || rawType.includes("REMINDER")) {
    return {
      status: getLabel(t, "CITIZEN_NOTIF_TYPE_REMINDER", "Reminder"),
      statusVariant: "warning",
      icon: "💳",
      defaultLine: getLabel(t, "CITIZEN_NOTIF_REMINDER_LINE", "Payment due reminder • Complete payment before due date"),
    };
  }
  if (rawType.includes("BROADCAST") || rawType.includes("NOTICE") || rawType.includes("ANNOUNCEMENT")) {
    return {
      status: getLabel(t, "CITIZEN_NOTIF_TYPE_NOTICE", "Notice"),
      statusVariant: "neutral",
      icon: "📢",
      defaultLine: getLabel(t, "CITIZEN_NOTIF_NOTICE_LINE", "Public municipal advisory and citizen notice"),
    };
  }
  if (rawType.includes("APPROV") || rawType.includes("SUCCESS") || rawType.includes("PAID")) {
    return {
      status: getLabel(t, "CITIZEN_NOTIF_TYPE_APPROVED", "Approved"),
      statusVariant: "success",
      icon: "✅",
      defaultLine: getLabel(t, "CITIZEN_NOTIF_APPROVED_LINE", "Your application request has been processed"),
    };
  }
  if (rawType.includes("SYSTEM") || rawType.includes("UPDATE") || rawType.includes("STATUS")) {
    return {
      status: getLabel(t, "CITIZEN_NOTIF_TYPE_UPDATE", "Update"),
      statusVariant: "info",
      icon: "🔔",
      defaultLine: getLabel(t, "CITIZEN_NOTIF_UPDATE_LINE", "Application status & service update available"),
    };
  }
  return {
    status: getLabel(t, "CITIZEN_NOTIF_TYPE_ALERT", "Alert"),
    statusVariant: "info",
    icon: "🔔",
    defaultLine: getLabel(t, "CITIZEN_NOTIF_DEFAULT_LINE", "Recent update on your citizen services account"),
  };
};

const buildNotificationItems = (aggregateData, eventsData = [], t, navigate, tenantId, handlers = {}) => {
  const responses = aggregateData?.responses || {};
  const notifsRaw = responses?.["notifications"]?.data?.notifications || responses?.["notifications"]?.data || [];
  const apiNotifs = Array.isArray(notifsRaw) ? notifsRaw : [];
  const liveEvents = Array.isArray(eventsData) ? eventsData : [];

  const dueRenewalsRaw = responses?.["due-renewals"]?.data?.bills || responses?.["due-renewals"]?.data || [];
  const dueRenewals = Array.isArray(dueRenewalsRaw) ? dueRenewalsRaw : [];

  const recentAppsRaw = responses?.["recent-applications"]?.data?.applications || responses?.["recent-applications"]?.data || [];
  const recentApps = Array.isArray(recentAppsRaw) ? recentAppsRaw : [];

  const sourceList = apiNotifs.length > 0 ? apiNotifs : liveEvents;

  if (sourceList.length > 0) {
    return sourceList.slice(0, 4).map((notif) => {
      const typeMeta = getNotificationTypeMeta(notif, t);
      const rawTitle = notif?.title || notif?.header || notif?.name || notif?.businessService || "";
      const title = rawTitle
        ? getLabel(t, rawTitle, rawTitle)
        : getLabel(t, "NOTIFICATION", "Notification");

      const rawDesc =
        notif?.description ||
        notif?.eventNotificationText ||
        notif?.subTitle ||
        notif?.message ||
        notif?.summary ||
        notif?.text ||
        notif?.eventDetails?.address ||
        "";
      const translatedDesc = rawDesc ? getLabel(t, rawDesc, rawDesc) : "";
      const ts = notif?.auditDetails?.lastModifiedTime || notif?.auditDetails?.createdTime || notif?.createdTime || notif?.date;
      const dateStr = ts ? new Date(ts).toLocaleDateString("en-IN", { day: "2-digit", month: "short" }) : "";

      const baseOneLiner = formatNotifOneLiner(translatedDesc, typeMeta.defaultLine);
      const subTitle = dateStr && !baseOneLiner.includes(dateStr) ? `${baseOneLiner} • ${dateStr}` : baseOneLiner;

      return {
        title,
        subTitle,
        status: typeMeta.status,
        statusVariant: typeMeta.statusVariant,
        icon: notif?.businessService ? getModuleIcon(notif.businessService) : typeMeta.icon,
        onClick: () => {
          if (notif?.redirectUrl) navigate(notif.redirectUrl.replace("/digit-ui/", "/upyog-ui/"));
          else if (notif?.actions?.[0]?.actionUrl) navigate(notif.actions[0].actionUrl.replace("/digit-ui/", "/upyog-ui/"));
          else navigate("/upyog-ui/citizen/engagement/notifications");
        },
      };
    });
  }

  const fallbackItems = [];

  if (dueRenewals.length > 0) {
    const topBill = dueRenewals[0];
    const svcLabel = topBill?.businessService
      ? getLabel(t, topBill.businessService.toUpperCase(), topBill.businessService)
      : getLabel(t, "BILL_PAYMENT", "Bill Payment");
    const amt = topBill?.totalAmount !== undefined ? `₹${topBill.totalAmount}` : "";
    fallbackItems.push({
      title: `${svcLabel} ${getLabel(t, "PAYMENT_REMINDER", "Payment Reminder")}`,
      subTitle: formatNotifOneLiner(
        `${amt ? `Pending bill of ${amt}` : "Pending utility bill"} for ${topBill?.consumerCode || svcLabel} • Tap to pay online`
      ),
      status: getLabel(t, "CITIZEN_NOTIF_TYPE_REMINDER", "Reminder"),
      statusVariant: "warning",
      icon: getModuleIcon(topBill?.businessService || "pt"),
      onClick: () => {
        if (handlers?.onOpenDueRenewals) handlers.onOpenDueRenewals();
        else navigate("/upyog-ui/citizen/engagement/notifications");
      },
    });
  }

  if (recentApps.length > 0) {
    const topApp = recentApps[0];
    const appSvc = topApp?.serviceName
      ? getLabel(t, topApp.serviceName, topApp.serviceName)
      : topApp?.businessService
      ? getLabel(t, topApp.businessService, topApp.businessService)
      : getLabel(t, "APPLICATION", "Application");
    const appNo = topApp?.applicationNumber || topApp?.consumerCode || topApp?.id || "";
    const appStatus = topApp?.status ? getLabel(t, topApp.status, topApp.status) : getLabel(t, "SUBMITTED", "Submitted");
    fallbackItems.push({
      title: `${appSvc} ${getLabel(t, "STATUS_UPDATE", "Status Update")}`,
      subTitle: formatNotifOneLiner(`Application ${appNo} is currently ${appStatus} • Track progress`),
      status: getLabel(t, "CITIZEN_NOTIF_TYPE_UPDATE", "Update"),
      statusVariant: "info",
      icon: getModuleIcon(topApp?.businessService || topApp?.serviceName || "pt"),
      onClick: () => {
        if (topApp?.redirectUrl) navigate(topApp.redirectUrl.replace("/digit-ui/", "/upyog-ui/"));
        else navigate("/upyog-ui/citizen/engagement/notifications");
      },
    });
  }

  fallbackItems.push(
    {
      title: getLabel(t, "CITIZEN_NOTIF_PT_REBATE_TITLE", "Property Tax Rebate Window"),
      subTitle: getLabel(
        t,
        "CITIZEN_NOTIF_PT_REBATE_DESC",
        "Pay your annual property tax early online to avail rebate benefits"
      ),
      status: getLabel(t, "CITIZEN_NOTIF_TYPE_NOTICE", "Notice"),
      statusVariant: "primary",
      icon: "🏠",
      onClick: () => navigate("/upyog-ui/citizen/engagement/notifications"),
    },
    {
      title: getLabel(t, "CITIZEN_NOTIF_SERVICE_PORTAL_TITLE", "Citizen Digital Services Update"),
      subTitle: getLabel(
        t,
        "CITIZEN_NOTIF_SERVICE_PORTAL_DESC",
        "Track applications, download receipts & receive instant SMS alerts"
      ),
      status: getLabel(t, "CITIZEN_NOTIF_TYPE_ALERT", "Alert"),
      statusVariant: "neutral",
      icon: "🔔",
      onClick: () => navigate("/upyog-ui/citizen/engagement/notifications"),
    }
  );

  return fallbackItems.slice(0, 3);
};

const buildBillRedirectUrl = (bill, fallbackTenantId) => {
  const resolvedTenant = bill?.tenantId || fallbackTenantId || Digit?.ULBService?.getCitizenCurrentTenant(true);
  if (bill?.redirectUrl) {
    const baseUrl = bill.redirectUrl.replace("/digit-ui/", "/upyog-ui/");
    if (!resolvedTenant || /[?&]tenantId=/.test(baseUrl)) {
      return baseUrl;
    }
    const separator = baseUrl.includes("?") ? "&" : "?";
    return `${baseUrl}${separator}tenantId=${encodeURIComponent(resolvedTenant)}`;
  }
  if (bill?.consumerCode && bill?.businessService) {
    return `/upyog-ui/citizen/payment/pay?consumerCode=${bill.consumerCode}&tenantId=${resolvedTenant}&businessService=${bill.businessService}`;
  }
  return "/upyog-ui/citizen";
};

const buildUserActionCards = (aggregateData, t, navigate, tenantId, handlers = {}, eventsData = []) => {
  const responses = aggregateData?.responses || {};

  const dueRenewalsRaw = responses?.["due-renewals"]?.data?.bills || responses?.["due-renewals"]?.data || [];
  const dueRenewals = Array.isArray(dueRenewalsRaw) ? dueRenewalsRaw : [];

  const recentAppsRaw = responses?.["recent-applications"]?.data?.applications || responses?.["recent-applications"]?.data || [];
  const recentApps = Array.isArray(recentAppsRaw) ? recentAppsRaw : [];

  const notifs = buildNotificationItems(aggregateData, eventsData, t, navigate, tenantId, handlers);

  const draftsRaw = responses?.["draft-applications"]?.data?.drafts || responses?.["draft-applications"]?.data || [];
  const drafts = Array.isArray(draftsRaw) ? draftsRaw : [];

  const eventsRaw = responses?.["upcoming-events"]?.data?.events || responses?.["upcoming-events"]?.data || [];
  const events = Array.isArray(eventsRaw) ? eventsRaw : [];

  const cards = [];

  if (dueRenewals.length > 0) {
    cards.push({
      title: getLabel(t, "CITIZEN_DUE_RENEWALS", "Due Renewals"),
      viewAllLabel: getLabel(t, "COMMON_VIEW_ALL", "View All"),
      onViewAll: () => {
        if (handlers?.onOpenDueRenewals) {
          handlers.onOpenDueRenewals();
        } else if (dueRenewals[0]) {
          navigate(buildBillRedirectUrl(dueRenewals[0], tenantId));
        } else {
          navigate("/upyog-ui/citizen");
        }
      },
      footerLabel: getLabel(t, "CITIZEN_VIEW_ALL_BILLS", "View All Due Bills"),
      onFooterClick: () => {
        if (handlers?.onOpenDueRenewals) {
          handlers.onOpenDueRenewals();
        } else if (dueRenewals[0]) {
          navigate(buildBillRedirectUrl(dueRenewals[0], tenantId));
        } else {
          navigate("/upyog-ui/citizen");
        }
      },
      items: dueRenewals.slice(0, 4).map((bill) => {
        const dateStr = bill?.dueDate ? new Date(bill.dueDate).toLocaleDateString("en-IN", { day: "2-digit", month: "short", year: "numeric" }) : "";
        const svcLabel = bill?.businessService ? getLabel(t, bill.businessService.toUpperCase(), bill.businessService) : getLabel(t, "BILL_PAYMENT", "Bill Payment");
        return {
          title: svcLabel,
          subTitle: `${bill?.consumerCode || bill?.billNumber || ""}${dateStr ? ` • Due: ${dateStr}` : ""}`,
          status: bill?.totalAmount !== undefined ? `₹ ${bill.totalAmount}` : getLabel(t, "DUE", "Due"),
          statusVariant: (bill?.totalAmount || 0) > 0 ? "warning" : "success",
          icon: getModuleIcon(bill?.businessService),
          onClick: () => {
            navigate(buildBillRedirectUrl(bill, tenantId));
          },
        };
      }),
    });
  }

  if (recentApps.length > 0) {
    cards.push({
      title: getLabel(t, "CITIZEN_RECENT_APPLICATIONS", "Recent Applications"),
      viewAllLabel: recentApps.length > 3 ? getLabel(t, "COMMON_VIEW_ALL", "View All") : undefined,
      onViewAll: () => navigate("/upyog-ui/citizen"),
      footerLabel: getLabel(t, "CITIZEN_MY_APPLICATIONS", "Go to My Applications"),
      onFooterClick: () => navigate("/upyog-ui/citizen"),
      items: recentApps.slice(0, 4).map((app) => ({
        title: app?.serviceName ? getLabel(t, app.serviceName, app.serviceName) : app?.businessService ? getLabel(t, app.businessService, app.businessService) : app?.title || getLabel(t, "APPLICATION", "Application"),
        subTitle: app?.applicationNumber || app?.consumerCode || app?.id || "",
        status: app?.status ? getLabel(t, app.status, app.status) : app?.state ? getLabel(t, app.state, app.state) : getLabel(t, "SUBMITTED", "Submitted"),
        statusVariant: (app?.status || "").toLowerCase().includes("paid") || (app?.status || "").toLowerCase().includes("approved") ? "success" : (app?.status || "").toLowerCase().includes("reject") ? "warning" : "info",
        icon: getModuleIcon(app?.businessService || app?.serviceName || app?.module),
        onClick: () => {
          if (app?.redirectUrl) navigate(app.redirectUrl.replace("/digit-ui/", "/upyog-ui/"));
        },
      })),
    });
  }

  if (notifs.length > 0) {
    cards.push({
      title: getLabel(t, "CITIZEN_NOTIFICATIONS", "Notifications"),
      viewAllLabel: getLabel(t, "COMMON_VIEW_ALL", "View All"),
      onViewAll: () => navigate("/upyog-ui/citizen/engagement/notifications"),
      footerLabel: getLabel(t, "CITIZEN_ALL_NOTIFICATIONS", "View All Notifications"),
      onFooterClick: () => navigate("/upyog-ui/citizen/engagement/notifications"),
      items: notifs.slice(0, 4),
    });
  }

  if (drafts.length > 0 && cards.length < 3) {
    cards.push({
      title: getLabel(t, "CITIZEN_DRAFT_APPLICATIONS", "Draft Applications"),
      viewAllLabel: drafts.length > 3 ? getLabel(t, "COMMON_VIEW_ALL", "View All") : undefined,
      onViewAll: () => navigate("/upyog-ui/citizen"),
      footerLabel: getLabel(t, "CITIZEN_CONTINUE_DRAFT", "Continue Draft Applications"),
      onFooterClick: () => navigate("/upyog-ui/citizen"),
      items: drafts.slice(0, 4).map((draft) => ({
        title: draft?.serviceName ? getLabel(t, draft.serviceName, draft.serviceName) : draft?.title || getLabel(t, "DRAFT_APPLICATION", "Draft Application"),
        subTitle: draft?.draftId || draft?.consumerCode || draft?.id || "",
        status: getLabel(t, "DRAFT", "Draft"),
        statusVariant: "neutral",
        icon: getModuleIcon(draft?.businessService || draft?.serviceName || draft?.module),
        onClick: () => {
          if (draft?.redirectUrl) navigate(draft.redirectUrl.replace("/digit-ui/", "/upyog-ui/"));
        },
      })),
    });
  }

  if (events.length > 0 && cards.length < 3) {
    cards.push({
      title: getLabel(t, "CITIZEN_UPCOMING_EVENTS", "Upcoming Events"),
      viewAllLabel: events.length > 3 ? getLabel(t, "COMMON_VIEW_ALL", "View All") : undefined,
      onViewAll: () => navigate("/upyog-ui/citizen/engagement/events"),
      footerLabel: getLabel(t, "CITIZEN_VIEW_EVENTS", "View All Upcoming Events"),
      onFooterClick: () => navigate("/upyog-ui/citizen/engagement/events"),
      items: events.slice(0, 3).map((ev) => {
        const evDate = ev?.eventDetails?.fromDate ? new Date(ev.eventDetails.fromDate) : ev?.date ? new Date(ev.date) : null;
        return {
          title: ev?.name || ev?.title || getLabel(t, "EVENT", "Event"),
          subTitle: ev?.eventDetails?.address || ev?.subTitle || ev?.location || "",
          date: evDate ? { day: `${evDate.getDate()}`, month: evDate.toLocaleString("default", { month: "short" }).toUpperCase() } : undefined,
          cta: getLabel(t, "REGISTER", "Register"),
          onClick: () => {
            if (ev?.redirectUrl) navigate(ev.redirectUrl.replace("/digit-ui/", "/upyog-ui/"));
            else navigate("/upyog-ui/citizen/engagement/events");
          },
        };
      }),
    });
  }

  return cards;
};

const buildTaskSummaryCards = (aggregateData, t, navigate, handlers = {}, eventsData = []) => {
  const responses = aggregateData?.responses || {};
  const quickSummary = responses?.["quick-summary"]?.data || {};

  const dueRenewalsRaw = responses?.["due-renewals"]?.data?.bills || responses?.["due-renewals"]?.data || [];
  const dueRenewals = Array.isArray(dueRenewalsRaw) ? dueRenewalsRaw : [];

  const recentAppsRaw = responses?.["recent-applications"]?.data?.applications || responses?.["recent-applications"]?.data || [];
  const recentApps = Array.isArray(recentAppsRaw) ? recentAppsRaw : [];

  const draftsRaw = responses?.["draft-applications"]?.data?.drafts || responses?.["draft-applications"]?.data || [];
  const drafts = Array.isArray(draftsRaw) ? draftsRaw : [];

  const notifsRaw = responses?.["notifications"]?.data?.notifications || responses?.["notifications"]?.data || [];
  const apiNotifs = Array.isArray(notifsRaw) ? notifsRaw : [];
  const liveEvents = Array.isArray(eventsData) ? eventsData : [];
  const resolvedNotifs = buildNotificationItems(aggregateData, eventsData, t, navigate, undefined, handlers);
  const notifsCount = apiNotifs.length || liveEvents.length || quickSummary?.notifications || resolvedNotifs.length || 0;

  const pendingBillsCount = dueRenewals.filter((b) => (b?.totalAmount || 0) > 0).length;

  return [
    {
      title: getLabel(t, "CITIZEN_TASK_ASSIGNED", "Task Assigned"),
      value: quickSummary?.taskAssigned || 0,
      actionLabel: getLabel(t, "VIEW_ALL_TASKS", "View all tasks"),
      onClick: () => navigate("/upyog-ui/citizen"),
    },
    {
      title: getLabel(t, "CITIZEN_PENDING_PAYMENT", "Pending Payment"),
      value: pendingBillsCount || quickSummary?.pendingPayment || 0,
      actionLabel: getLabel(t, "PAY_NOW", "Pay now"),
      onClick: () => {
        if (handlers?.onOpenDueRenewals) {
          handlers.onOpenDueRenewals();
        } else if (dueRenewals[0]) {
          navigate(buildBillRedirectUrl(dueRenewals[0]));
        } else {
          navigate("/upyog-ui/citizen");
        }
      },
    },
    {
      title: getLabel(t, "CITIZEN_RECENT_APPLICATIONS", "Recent Applications"),
      value: recentApps.length || quickSummary?.totalApplications || 0,
      actionLabel: getLabel(t, "VIEW_APPLICATIONS", "View applications"),
      onClick: () => navigate("/upyog-ui/citizen"),
    },
    {
      title: getLabel(t, "CITIZEN_NOTIFICATIONS", "Notifications"),
      value: notifsCount,
      actionLabel: getLabel(t, "VIEW_NOTIFICATIONS", "View notifications"),
      onClick: () => navigate("/upyog-ui/citizen/engagement/notifications"),
    },
  ];
};

function returnConstants(t, type, fn, navigate) {
  const nav = navigate || fn;
  switch (type) {
    case "allCitizenServicesProps":
      return {
        header: t(fn?.headerLabel),
        sideOption: {
          name: t(fn?.sideOption?.name),
          onClick: () => nav(fn?.sideOption?.navigationUrl.replace("/digit-ui/", "/upyog-ui/")),
        },
        options: [
          {
            name: t(fn?.props?.[0]?.label),
            Icon: <ComplaintIcon />,
            onClick: () => nav(fn?.props?.[0]?.navigationUrl.replace("/digit-ui/", "/upyog-ui/")),
          },
          {
            name: t(fn?.props?.[1]?.label),
            Icon: <PTIcon className="fill-path-primary-main" />,
            onClick: () => nav(fn?.props?.[1]?.navigationUrl.replace("/digit-ui/", "/upyog-ui/")),
          },
          {
            name: t(fn?.props?.[2]?.label),
            Icon: <CaseIcon className="fill-path-primary-main" />,
            onClick: () => nav(fn?.props?.[2]?.navigationUrl.replace("/digit-ui/", "/upyog-ui/")),
          },
          {
            name: t(fn?.props?.[3]?.label),
            Icon: <WSICon />,
            onClick: () => nav(fn?.props?.[3]?.navigationUrl.replace("/digit-ui/", "/upyog-ui/")),
          },
        ],
        styles: { display: "flex", flexWrap: "wrap", justifyContent: "flex-start", width: "100%", marginRight: "16px" },
      };
    case "allInfoAndUpdatesProps":
      return {
        header: t(fn?.headerLabel),
        sideOption: {
          name: t(fn?.sideOption?.name),
          onClick: () => nav(fn?.sideOption?.navigationUrl.replace("/digit-ui/", "/upyog-ui/")),
        },
        options: [
          {
            name: t(fn?.props?.[0]?.label),
            Icon: <HomeIcon />,
            onClick: () => nav(fn?.props?.[0]?.navigationUrl.replace("/digit-ui/", "/upyog-ui/")),
          },
          {
            name: t(fn?.props?.[1]?.label),
            Icon: <Calender />,
            onClick: () => nav(fn?.props?.[1]?.navigationUrl.replace("/digit-ui/", "/upyog-ui/")),
          },
          {
            name: t(fn?.props?.[2]?.label),
            Icon: <DocumentIcon />,
            onClick: () => nav(fn?.props?.[2]?.navigationUrl.replace("/digit-ui/", "/upyog-ui/")),
          },
          {
            name: t(fn?.props?.[3]?.label),
            Icon: <DocumentIcon />,
            onClick: () => nav(fn?.props?.[3]?.navigationUrl.replace("/digit-ui/", "/upyog-ui/")),
          },
        ],
        styles: { display: "flex", flexWrap: "wrap", justifyContent: "flex-start", width: "100%" },
      };
    case "quickServiceCards":
      return [
        { title: "PT", text: "View & manage property tax", subtitle: "Property Tax", icon: "PT", onClick: () => nav("/upyog-ui/citizen/pt-home") },
        { title: "TL", text: "Apply for trade licence", subtitle: "Trade License", icon: "TL", onClick: () => nav("/upyog-ui/citizen/tl-home") },
        { title: "WS", text: "Water & sewerage connections & bills", subtitle: "Water & Sewerage", icon: "WS", onClick: () => nav("/upyog-ui/citizen/ws-home") },
        { title: "+", text: "Expand all services", subtitle: "View All", icon: "+", onClick: () => nav("/upyog-ui/citizen/all-services") },
      ];
    default:
      return;
  }
}

export default Home;

