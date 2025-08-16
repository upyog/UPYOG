import { Header, Loader } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";
import CndApplicationData from "./CndApplicationData";
import { cndStyles } from "../../../utils/cndStyles";

export const MyRequests = () => {
  const { t } = useTranslation();
  const tenantId = Digit.ULBService.getCitizenCurrentTenant(true) || Digit.ULBService.getCurrentTenantId();
  const user = Digit.UserService.getUser().info;
  

  let filter = window.location.href.split("/").pop();
  let t1;
  let off;
  if (!isNaN(parseInt(filter))) {
    off = filter;
    t1 = parseInt(filter) + 50;
  } else {
    t1 = 4;
  }
  let filter1 = !isNaN(parseInt(filter))
    ? { limit: "50", sortOrder: "ASC", sortBy: "createdTime", offset: off, tenantId }
    : { limit: "4", sortOrder: "ASC", sortBy: "createdTime", offset: "0",mobileNumber:user?.mobileNumber, tenantId };

  const { isLoading, isError, error, data } = Digit.Hooks.cnd.useCndSearchApplication({ filters: filter1 }, { filters: filter1 });
  const {cndApplicationDetail: applicationsList } = data || {};
  
  if (isLoading) {
    return <Loader />;
  }


  return (
    <React.Fragment>
      <Header>{`${t("CS_TITLE_MY_APPLICATIONS")} ${applicationsList ? `(${applicationsList.length})` : ""}`}</Header>
      <div>
        {applicationsList?.length > 0 &&
          applicationsList.map((application, index) => (
            <div key={index}>
              <CndApplicationData application={application} tenantId={user?.permanentCity} buttonLabel={t("CS_CF_TRACK")}/>
            </div>
          ))}
        {!applicationsList?.length > 0 && <p style={cndStyles.applicationList}>{t("CND_NO_APPLICATION_FOUND")}</p>}

        {applicationsList?.length !== 0 && (
          <div>
            <p style={cndStyles.applicationList}>
              <span className="link">{<Link to={`/cnd-ui/citizen/cnd/my-request/${t1}`}>{t("CND_LOAD_MORE")}</Link>}</span>
            </p>
          </div>
        )}
      </div>

      <p style={cndStyles.applicationList}>
        {t("CND_NO_APPLICATION_FOUND")}{" "}
        <span className="link" style={cndStyles.newApplication}>
          <Link to="/cnd-ui/citizen/cnd/apply">{t("CND_CLICK_TO_REQUEST")}</Link>
        </span>
      </p>
    </React.Fragment>
  );
};
