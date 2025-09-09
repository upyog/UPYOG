import { 
  Card, 
  CaseIcon,
  TimerIcon, 
  RupeeSymbol, 
  ValidityTimeIcon, 
  WhatsappIconGreen, 
  HelpLineIcon, 
  ServiceCenterIcon, 
  Loader

} from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { useTranslation } from "react-i18next";

const StaticDynamicCard = ({moduleCode}) => {
    const { t } = useTranslation();
    const tenantId = Digit.ULBService.getCitizenCurrentTenant();
    const { isLoading: isMdmsLoading, data: mdmsData } = Digit.Hooks.useStaticData(Digit.ULBService.getStateId());
    const { isLoading: isSearchLoading, error, data: dynamicData, isSuccess } = Digit.Hooks.useDynamicData({ moduleCode, tenantId: tenantId , filters: {} , t});
    const isMobile = window.Digit.Utils.browser.isMobile();
    const IconComponent = ({module, styles}) => {

      switch(module){
        default:
          return <CaseIcon className="fill-path-primary-main" styles={styles}/>;
      }
    }
    const mdmsConfigResult = mdmsData?.MdmsRes["common-masters"]?.StaticData[0]?.[`${moduleCode}`];

    if(isMdmsLoading || isSearchLoading){
      return <Loader/>
    }
    return mdmsConfigResult ? (
      <React.Fragment>
          { mdmsConfigResult && mdmsConfigResult?.helpline ?
            <Card style={{margin: "16px", padding: "16px", maxWidth: "unset"}}>
          <div className="static-home-Card">
              <div className="static-home-Card-header">{t("CALL_CENTER_HELPLINE")}</div>
              <div className="helplineIcon">
                <HelpLineIcon />
              </div>
          </div>
          <div className="call-center-card-text">
          { mdmsConfigResult?.helpline?.contactOne ? <div className="call-center-card-content">
            <a href={`tel:${mdmsConfigResult?.helpline?.contactOne}`}>{mdmsConfigResult?.helpline?.contactOne}</a>
          </div> : null}
         { mdmsConfigResult?.helpline?.contactTwo ? <div className="call-center-card-content">
            <a href={`tel:${mdmsConfigResult?.helpline?.contactTwo}`}>{mdmsConfigResult?.helpline?.contactTwo}</a>
          </div> : null}
          </div>
          </Card> : null
          }
          { mdmsConfigResult && mdmsConfigResult?.serviceCenter ?
          <Card style={{margin: "16px", padding: "16px", maxWidth: "unset"}}>
          <div className="static-home-Card">
              <div className="static-home-Card-header">{t("CITIZEN_SERVICE_CENTER")}</div>
              <div className="serviceCentrIcon">
                <ServiceCenterIcon />
              </div>
          </div>
          <div className="service-center-details-card">
          <div className="service-center-details-text">
            {mdmsConfigResult?.serviceCenter}
          </div>
          </div>
          { mdmsConfigResult?.viewMapLocation ? <div className="link">
            <a href={mdmsConfigResult?.viewMapLocation}>{t("VIEW_ON_MAP")}</a>
          </div> : null}
          </Card> : <div/> }
      <Card style={{margin: "16px", padding: "16px", maxWidth: "unset"}}>
      { error || dynamicData == null || dynamicData?.dynamicDataOne === null ? (
        <div/>
      ) : (
      <div className="dynamicDataCard" style={isMobile ? {maxHeight:"fit-content"} : {}}>
        <div className="dynamicData">
        <span style={{paddingTop: "2px"}}>
        <IconComponent module={moduleCode} styles={{width: "18px", height: "24px"}}/></span>
          <span className="dynamicData-content">
            {dynamicData?.dynamicDataOne}
          </span>
        </div>
      </div> ) }
      { error || dynamicData == null || dynamicData?.dynamicDataTwo === null ? (
        <div/>
      ) : (
      <div className="dynamicDataCard" style={isMobile ? {maxHeight:"fit-content"} : {}}>
      <div className="dynamicData">
      <span style={{paddingTop: "2px"}}>
        <IconComponent module={moduleCode} styles={{width: "18px", height: "24px"}}/></span>
          <span className="dynamicData-content">
          {dynamicData?.dynamicDataTwo}
          </span>
        </div>
      </div>) }
      { mdmsConfigResult && mdmsConfigResult?.staticDataOne 
        ? <div className="staticDataCard">
            <div className="staticData">
              {/* <StaticDataIconComponentOne module={moduleCode}/> */}
              <span className="static-data-content">
                <span className="static-data-content-first" style={
                  {
                    marginTop: staticData(moduleCode)?.staticDataOne === "" ? "8px" : "unset"
                  }
                }>
              {staticData(moduleCode)?.staticDataOneHeader}
              </span>
              <span className="static-data-content-second">
                {`${staticData(moduleCode)?.staticDataOne}`}
              </span>
              </span>
        </div>
      </div> : <div/>}
      { mdmsConfigResult && mdmsConfigResult?.staticDataTwo 
        ?
        <div className="staticDataCard">
        <div className="staticData">
        {/* <StaticDataIconComponentTwo module={moduleCode}/> */}
            <span className="static-data-content">
              <span className="static-data-content-first">
            {staticData(moduleCode)?.staticDataTwoHeader}
            </span>
            <span className="static-data-content-second">
              {staticData(moduleCode)?.staticDataTwo}
            </span>
            </span>
          </div>
      </div> : <div/> }
      { mdmsConfigResult && mdmsConfigResult?.validity 
        ? <div className="staticDataCard">
          <div className="staticData">
            <span className="validityIcon">
              <ValidityTimeIcon/>
            </span>
            <span className="static-data-content">
              <span className="static-data-content-first">
              {/* {staticContent(moduleCode)?.staticCommonContent} */}
            </span>
          <span className="static-data-content-second">
            {/* {staticContent(moduleCode)?.validity} */}
          </span>
          </span>
        </div>
      </div> : <div/> }
      { error || dynamicData == null  || !dynamicData?.staticData || dynamicData?.staticData === null ? (
        <div/>
      ) : (
         <div className="staticDataCard">
          <div className="staticData">
            { moduleCode === "PGR" 
            ? <span style={{paddingTop: "15px"}}>
            <TimerIcon module={moduleCode} styles={{width: "18px", height: "24px", marginLeft: "13px"}}/></span>
            : <span className="validityIcon">
               <ValidityTimeIcon/>
            </span>}
            <span className="static-data-content">
              <span className="static-data-content-first">
              {/* {staticContent(moduleCode)?.staticCommonContent} */}
            </span>
          <span className="static-data-content-second">
            {dynamicData?.staticData}
          </span>
          </span>
        </div>
      </div>)}
    </Card>
    </React.Fragment>) : <React.Fragment/>
}

export default StaticDynamicCard;