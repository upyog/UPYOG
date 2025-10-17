import { BackButton, WhatsappIcon, Card, CitizenHomeCard, CitizenInfoLabel, PrivateRoute, Loader } from "@upyog/digit-ui-react-components";
import React from "react";
import { useTranslation } from "react-i18next";
import { Route, Switch, useRouteMatch, useHistory, Link } from "react-router-dom";
import ErrorBoundary from "../../components/ErrorBoundaries";
import { AppHome, processLinkData } from "../../components/Home";
import TopBarSideBar from "../../components/TopBarSideBar";
import StaticCitizenSideBar from "../../components/TopBarSideBar/SideBar/StaticCitizenSideBar";
import CitizenHome from "./Home";
import LanguageSelection from "./Home/LanguageSelection";
import LocationSelection from "./Home/LocationSelection";
import Login from "./Login";
import UserProfile from "./Home/UserProfile";
import ErrorComponent from "../../components/ErrorComponent";
import FAQsSection from "./FAQs/FAQs";
import HowItWorks from "./HowItWorks/howItWorks";
import StaticDynamicCard from "./StaticDynamicComponent/StaticDynamicCard";
import AcknowledgementCF from "../../components/AcknowledgementCF";
import CitizenFeedback from "../../components/CitizenFeedback";
import Search from "./SearchApp";
import './cityzenCustomStyle.css';
import { useRef } from "react";
import { useEffect } from "react";
const sidebarHiddenFor = [
  "digit-ui/citizen/register/name",
  "/digit-ui/citizen/select-language",
  "/digit-ui/citizen/select-location",
  "/digit-ui/citizen/login",
  "/digit-ui/citizen/register/otp",
];

const getTenants = (codes, tenants) => {
  return tenants.filter((tenant) => codes.map((item) => item.code).includes(tenant.code));
};

const Home = ({
  stateInfo,
  userDetails,
  CITIZEN,
  cityDetails,
  mobileView,
  handleUserDropdownSelection,
  logoUrl,
  DSO,
  stateCode,
  modules,
  appTenants,
  sourceUrl,
  pathname,
  initData,
}) => {
  const { isLoading: islinkDataLoading, data: linkData, isFetched: isLinkDataFetched } = Digit.Hooks.useCustomMDMS(
    Digit.ULBService.getStateId(),
    "ACCESSCONTROL-ACTIONS-TEST",
    [
      {
        name: "actions-test",
        filter: "[?(@.url == 'digit-ui-card')]",
      },
    ],
    {
      select: (data) => {
        const formattedData = data?.["ACCESSCONTROL-ACTIONS-TEST"]?.["actions-test"]
          ?.filter((el) => el.enabled === true)
          .reduce((a, b) => {
            if(b.parentModule=='PT')
            a[b.parentModule] = a[b.parentModule]?.length > 0 && b.parentModule=='PT' ? [b, ...a[b.parentModule]] : [b];
            return a;
          }, {});
        return formattedData;
      },
    }
  );

  const classname = Digit.Hooks.fsm.useRouteSubscription(pathname);
  const { t } = useTranslation();
  const { path } = useRouteMatch();
  sourceUrl = "https://s3.ap-south-1.amazonaws.com/egov-qa-assets";
  const pdfUrl = "https://pg-egov-assets.s3.ap-south-1.amazonaws.com/Upyog+Code+and+Copyright+License_v1.pdf"
  const history = useHistory();
  const handleClickOnWhatsApp = (obj) => {
    window.open(obj);
  };
  const sidebarRef = useRef();

  

  const hideSidebar = sidebarHiddenFor.some((e) => window.location.href.includes(e));
  const appRoutes = modules.map(({ code, tenants }, index) => {
    const Module = Digit.ComponentRegistryService.getComponent(`${code}Module`);
    return Module ? (
      <Route key={index} path={`${path}/${code.toLowerCase()}`}>
        <Module stateCode={stateCode} moduleCode={code} userType="citizen" tenants={getTenants(tenants, appTenants)} />
      </Route>
    ) : null;
  });

  const ModuleLevelLinkHomePages = modules.map(({ code, bannerImage }, index) => {
    let Links = Digit.ComponentRegistryService.getComponent(`${code}Links`) || (() => <React.Fragment />);
    let mdmsDataObj = isLinkDataFetched ? processLinkData(linkData, code, t) : undefined;

    //if (mdmsDataObj?.header === "ACTION_TEST_WS") {
      mdmsDataObj?.links && mdmsDataObj?.links.sort((a, b) => {
        return a.orderNumber - b.orderNumber;
      });
    // }
    // console.log("mdmsDataObj===",mdmsDataObj)
    return (
      <React.Fragment>
        <Route key={index} path={`${path}/${code.toLowerCase()}-home`}>
          <div className="moduleLinkHomePage">
            {/* <img src={ "https://mnptapp-terraform.s3.ap-south-1.amazonaws.com/images/Kangla-Sha.png"||bannerImage || stateInfo?.bannerUrl} alt="noimagefound" /> */}
            {/* <BackButton className="moduleLinkHomePageBackButton" />
            <h1>{t("MODULE_" + code.toUpperCase())}</h1> */}
            <div className="moduleLinkHomePageModuleLinks">
              {mdmsDataObj && (
                <CitizenHomeCard
                  header={t(mdmsDataObj?.header)}
                  links={mdmsDataObj?.links}
                  Icon={() => <span />}
                  Info={
                    code === "OBPS"
                      ? () => (
                          <CitizenInfoLabel
                            style={{ margin: "0px", padding: "10px" }}
                            info={t("CS_FILE_APPLICATION_INFO_LABEL")}
                            text={t(`BPA_CITIZEN_HOME_STAKEHOLDER_INCLUDES_INFO_LABEL`)}
                          />
                        )
                      : null
                  }
                  isInfo={code === "OBPS" ? true : false}
                />
              )}
              {/* <Links key={index} matchPath={`/digit-ui/citizen/${code.toLowerCase()}`} userType={"citizen"} /> */}
            </div>
            {/* <StaticDynamicCard moduleCode={code?.toUpperCase()}/> */}
          </div>
        </Route>
        {/* <Route key={"faq" + index} path={`${path}/${code.toLowerCase()}-faq`}>
          <FAQsSection module={code?.toUpperCase()} />
        </Route>
        <Route key={"hiw" + index} path={`${path}/${code.toLowerCase()}-how-it-works`}>
          <HowItWorks module={code?.toUpperCase()} />
        </Route> */}
      </React.Fragment>
    );
  });


  // useEffect(() => {
  //   if (islinkDataLoading) {
  //     return <Loader />;
  //   }
  //   console.log("sidebarRef===",sidebarRef)
  //   if(hideSidebar && sidebarRef && sidebarRef.current) {
  //     sidebarRef?.current?.style.cursor = "pointer";
  //     collapseNav();
  //   }
  // }, [islinkDataLoading]);

  // const expandNav = () => {
  //   sidebarRef.current.style.width = "260px";
  //   sidebarRef.current.style.overflow = "auto";

  //   sidebarRef.current.querySelectorAll(".dropdown-link").forEach((element) => {
  //     element.style.display = "flex";
  //   });
  //   sidebarRef.current.querySelectorAll(".label-text").forEach((element) => {
  //     element.style.display = "flex";
  //   });
  // };
  // const collapseNav = () => {
  //   sidebarRef.current.style.width = "55px";
  //   sidebarRef.current.style.overflow = "hidden";

  //   sidebarRef.current.querySelectorAll(".dropdown-link").forEach((element) => {
  //     element.style.display = "none";
  //   });
  //   sidebarRef.current.querySelectorAll(".label-text").forEach((element) => {
  //     element.style.display = "none";
  //   });
  //   sidebarRef.current.querySelectorAll(".actions").forEach((element) => {
  //     element.style.padding = "0";
  //   });
  // };

  return (
    <div className={classname}>
      <div className="citizen-main">
      <TopBarSideBar
        t={t}
        stateInfo={stateInfo}
        userDetails={userDetails}
        CITIZEN={CITIZEN}
        cityDetails={cityDetails}
        mobileView={mobileView}
        handleUserDropdownSelection={handleUserDropdownSelection}
        logoUrl={logoUrl}
        showSidebar={true}
        linkData={linkData}
        islinkDataLoading={islinkDataLoading}
      />

      <div className={`main center-container citizen-home-container mb-25`}>
        {hideSidebar ? null : (
          <div>
           {/* ref={sidebarRef} onMouseOver={expandNav} onMouseLeave={collapseNav}> */}
            {/* className="SideBarStatic" */}
            <StaticCitizenSideBar linkData={linkData} islinkDataLoading={islinkDataLoading} />
          </div>
        )}

        <Switch>
          <Route exact path={path}>
            <CitizenHome />
          </Route>

          <PrivateRoute path={`${path}/feedback`} component={CitizenFeedback}></PrivateRoute>
          <PrivateRoute path={`${path}/feedback-acknowledgement`} component={AcknowledgementCF}></PrivateRoute>

          <Route exact path={`${path}/select-language`}>
            <LanguageSelection />
          </Route>

          <Route exact path={`${path}/select-location`}>
            <LocationSelection />
          </Route>
          <Route path={`${path}/error`}>
            <ErrorComponent
              initData={initData}
              goToHome={() => {
                history.push("/digit-ui/citizen");
              }}
            />
          </Route>
          <Route path={`${path}/all-services`}>
            <AppHome
              userType="citizen"
              modules={modules}
              getCitizenMenu={linkData}
              fetchedCitizen={isLinkDataFetched}
              isLoading={islinkDataLoading}
            />
          </Route>

          <Route path={`${path}/login`}>
            <Login stateCode={stateCode} />
          </Route>

          <Route path={`${path}/register`}>
            <Login stateCode={stateCode} isUserRegistered={false} />
          </Route>

          <PrivateRoute path={`${path}/user/profile`}>
            <UserProfile stateCode={stateCode} userType={"citizen"} cityDetails={cityDetails} />
          </PrivateRoute>

          <Route path={`${path}/Audit`}>
            <Search/>
          </Route>
          <ErrorBoundary initData={initData}>
            {appRoutes}
            {ModuleLevelLinkHomePages}
          </ErrorBoundary>
        </Switch>
      </div>
      </div>

      <div style={{ width: '100%', position: 'fixed', bottom: 0,backgroundColor:"white",textAlign:"center", zIndex: "99" }}>
        <div style={{ display: 'flex', justifyContent: 'center', color:"black", boxShadow: "2px 2px 2px 2px" }}>
          {/* <span style={{ cursor: "pointer", fontSize: window.Digit.Utils.browser.isMobile()?"12px":"14px", fontWeight: "400"}} onClick={() => { window.open('https://www.digit.org/', '_blank').focus();}} >Powered by DIGIT</span>
          <span style={{ margin: "0 10px" ,fontSize: window.Digit.Utils.browser.isMobile()?"12px":"14px"}}>|</span>
          <a style={{ cursor: "pointer", fontSize: window.Digit.Utils.browser.isMobile()?"12px":"14px", fontWeight: "400"}} href="#" target='_blank'>UPYOG License</a>

          <span  className="upyog-copyright-footer" style={{ margin: "0 10px",fontSize: window.Digit.Utils.browser.isMobile()?"12px":"14px" }} >|</span> */}
          <span  className="upyog-copyright-footer" style={{ cursor: "pointer", fontSize: window.Digit.Utils.browser.isMobile()?"12px":"14px", fontWeight: "400"}} onClick={() => { window.open('https://niua.in/', '_blank').focus();}} >Copyright © 2024 Manipur Municipality Property Tax Board</span>
          
          {/* <a style={{ cursor: "pointer", fontSize: "16px", fontWeight: "400"}} href="#" target='_blank'>UPYOG License</a> */}

        </div>
        <div className="upyog-copyright-footer-web">
          <span className="" style={{ cursor: "pointer", fontSize:  window.Digit.Utils.browser.isMobile()?"12px":"14px", fontWeight: "400"}} onClick={() => { window.open('https://niua.in/', '_blank').focus();}} >Copyright © 2024 Manipur Municipality Property Tax Board</span>
          </div>
      </div>
      
    </div>
  );
};

export default Home;
