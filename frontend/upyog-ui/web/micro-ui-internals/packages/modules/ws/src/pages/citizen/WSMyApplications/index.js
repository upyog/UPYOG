import { Header, Loader } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";
import WSApplication from "./ws-application";
import WSInfoLabel from "../../../pageComponents/WSInfoLabel";

export const WSMyApplications = () => {
  const {
    t
  } = useTranslation();
  const user = Digit.UserService.getUser();
  const tenantId = Digit.SessionStorage.get("CITIZEN.COMMON.HOME.CITY")?.code || user?.info?.permanentCity || Digit.ULBService.getCurrentTenantId();
  let filter = window.location.href.split("/").pop();
  let t1;
  let off;
  if (!isNaN(parseInt(filter))) {
    off = filter;
    t1 = parseInt(filter) + 50;
  } else {
    t1 = 4;
  }
  let filter1 = !isNaN(parseInt(filter)) ? {
    tenantId: tenantId,
    mobileNumber: user?.info?.mobileNumber
  } : {
    tenantId: tenantId,
    mobileNumber: user?.info?.mobileNumber
  };
  const {
    isLoading,
    isError,
    error,
    data
  } = Digit.Hooks.ws.useMyApplicationSearch({
    filters: filter1
  }, {
    filters: filter1
  });
  const {
    isLoading: isSWLoading,
    isError: isSWError,
    error: SWerror,
    data: SWdata
  } = Digit.Hooks.ws.useMyApplicationSearch({
    filters: filter1,
    BusinessService: "SW"
  }, {
    filters: filter1
  });
  let applicationNoWS = data && data?.WaterConnection?.map(ob => ob.applicationNo).join(",") || "";
  let applicaionNoSW = SWdata && SWdata?.SewerageConnections?.map(ob => ob.applicationNo).join(",") || "";
  let applicationNos = applicationNoWS.concat(applicaionNoSW);
  let workflowDetails = Digit.Hooks.useWorkflowDetails({
    tenantId: tenantId,
    id: applicationNos,
    moduleCode: "WS,SW",
    config: {
      enabled: !!applicationNos
    }
  });
  let propertyWS = data && data?.WaterConnection?.map(ob => ob?.propertyId).join(",") || "";
  let propertySW = SWdata && SWdata?.SewerageConnections?.map(ob => ob?.propertyId).join(",") || "";
  let propertyNos = propertyWS.concat(propertySW);
  const {
    isLoading: PTisLoading,
    isError: PTisError,
    error: PTerror,
    data: PTdata
  } = Digit.Hooks.pt.usePropertySearch({
    filters: {
      propertyIds: propertyNos
    }
  }, {
    filters: {
      propertyIds: propertyNos
    },
    enabled: propertyNos ? true : false
  });
  if (isLoading || isSWLoading || PTisLoading) {
    return <Loader />;
  }
  let {
    WaterConnection: WSapplicationsList
  } = data || {};
  let {
    SewerageConnections: SWapplicationsList
  } = SWdata || {};
  WSapplicationsList = WSapplicationsList?.map(ob => {
    return {
      ...ob,
      "sla": workflowDetails?.data?.processInstances?.filter(pi => pi.businessId == ob.applicationNo)[0]?.businesssServiceSla
    };
  });
  SWapplicationsList = SWapplicationsList?.map(ob => {
    return {
      ...ob,
      "sla": workflowDetails?.data?.processInstances?.filter(pi => pi.businessId == ob.applicationNo)[0]?.businesssServiceSla
    };
  });
  WSapplicationsList = WSapplicationsList?.filter(ob => ob?.applicationType !== "MODIFY_WATER_CONNECTION");
  SWapplicationsList = SWapplicationsList?.filter(ob => ob?.applicationType !== "MODIFY_SEWERAGE_CONNECTION");
  let applicationsList = WSapplicationsList.concat(SWapplicationsList);
  applicationsList = applicationsList && applicationsList.map(ob => {
    return {
      ...ob,
      property: PTdata?.Properties?.filter(pt => pt?.propertyId === ob?.propertyId)[0]
    };
  });
  return (
    <div className="ws-citizen-container">
      <div className="ws-citizen-header-wrap">
        <div className="ws-citizen-title-block">
          <h1 className="ws-citizen-page-title">{t("CS_HOME_MY_APPLICATIONS")}</h1>
          {applicationsList && applicationsList.length > 0 && (
            <span className="ws-citizen-count-badge">
              {applicationsList.length} {applicationsList.length === 1 ? t("WS_APPLICATION") : t("WS_APPLICATIONS")}
            </span>
          )}
        </div>
      </div>
      {/* For UM-4418 changes */}
      <WSInfoLabel t={t} />
      <div>
        {applicationsList?.length > 0 ? (
          <div className="ws-citizen-grid">
            {applicationsList
              .sort((a, b) => b.auditDetails?.lastModifiedTime - a.auditDetails?.lastModifiedTime)
              .map((application, index) => (
                <WSApplication key={index} application={application} />
              ))}
          </div>
        ) : (
          <div className="ws-empty-state">
            <div className="ws-empty-icon">
              <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z" />
                <polyline points="14 2 14 8 20 8" />
                <line x1="16" y1="13" x2="8" y2="13" />
                <line x1="16" y1="17" x2="8" y2="17" />
                <polyline points="10 9 9 9 8 9" />
              </svg>
            </div>
            <div className="ws-empty-title">{t("WS_NO_APPLICATION_FOUND_MSG") || t("WS_NO_APPLICATIONS_FOUND")}</div>
          </div>
        )}
      </div>
    </div>
  );
};

export default WSMyApplications;

