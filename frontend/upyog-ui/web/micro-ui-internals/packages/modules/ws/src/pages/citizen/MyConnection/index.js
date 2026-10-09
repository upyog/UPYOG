import { Card, Header, KeyNote, Loader, SubmitBar } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { useTranslation } from "react-i18next";
import { Link } from "react-router-dom";
import WSConnection from "./WSConnection";
import WSInfoLabel from "../../../pageComponents/WSInfoLabel";

const MyConnections = ({ view }) => {
  console.log("MyConnections rendered with view:");
  const { t } = useTranslation();
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
    mobileNumber: user?.info?.mobileNumber,
    searchType: "CONNECTION"
  } : {
    tenantId: tenantId,
    mobileNumber: user?.info?.mobileNumber,
    searchType: "CONNECTION"
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
  let connectionList = data?.WaterConnection.concat(SWdata?.SewerageConnections);
  let applicationNoWS = data && data?.WaterConnection?.map(ob => ob?.propertyId).join(",") || "";
  let applicaionNoSW = SWdata && SWdata?.SewerageConnections?.map(ob => ob?.propertyId).join(",") || "";
  let applicationNos = applicationNoWS.concat(applicaionNoSW);
  const {
    isLoading: PTisLoading,
    isError: PTisError,
    error: PTerror,
    data: PTdata
  } = Digit.Hooks.pt.usePropertySearch({
    filters: {
      propertyIds: applicationNos
    }
  }, {
    filters: {
      propertyIds: applicationNos
    },
    enabled: applicationNos ? true : false
  });
  connectionList = connectionList && connectionList.map(ob => {
    return {
      ...ob,
      property: PTdata?.Properties?.filter(pt => pt?.propertyId === ob?.propertyId)[0]
    };
  });
  if (isLoading || PTisLoading || isSWLoading) {
    return <Loader />;
  }
  return (
    <div className="ws-citizen-container">
      <div className="ws-citizen-header-wrap">
        <div className="ws-citizen-title-block">
          <h1 className="ws-citizen-page-title">{t("WS_MYCONNECTIONS_HEADER")}</h1>
          {connectionList && connectionList.length > 0 && (
            <span className="ws-citizen-count-badge">
              {connectionList.length} {connectionList.length === 1 ? t("WS_CONNECTION") : t("WS_CONNECTIONS")}
            </span>
          )}
        </div>
      </div>
      <WSInfoLabel t={t} />
      <div>
        {connectionList?.length > 0 ? (
          <div className="ws-citizen-grid">
            {connectionList.map((application, index) => (
              <WSConnection key={index} application={application} />
            ))}
          </div>
        ) : (
          <div className="ws-empty-state">
            <div className="ws-empty-icon">
              <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                <path d="M12 2.69l5.66 5.66a8 8 0 1 1-11.31 0z" />
              </svg>
            </div>
            <div className="ws-empty-title">{t("PT_NO_APPLICATION_FOUND_MSG") || t("WS_NO_CONNECTIONS_FOUND")}</div>
            <div className="ws-empty-subtitle">{t("WS_NO_CONNECTIONS_SUBTITLE") || ""}</div>
          </div>
        )}
      </div>
    </div>
  );
};
export default MyConnections;

