import { Header, Loader } from "@upyog/digit-ui-react-components";
import React from "react";
import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";
import ChbApplication from "./chb-application";

export const CHBMyApplications = () => {
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

  const { isLoading, isError, error, data } = Digit.Hooks.chb.useChbSearch({ filters: filter1 }, { filters: filter1 });
  
  const {hallsBookingApplication: applicationsList } = data || {};
  let combinedApplicationNumber = applicationsList?.length > 0 ? applicationsList?.map((ob) => ob?.bookingNo) : [];
  let serviceSearchArgs = {
    tenantId : tenantId,
    referenceIds : combinedApplicationNumber,
  }

  const { isLoading:serviceloading, data : servicedata} = Digit.Hooks.useFeedBackSearch({ filters: { serviceSearchArgs } },{ filters: { serviceSearchArgs }, enabled : combinedApplicationNumber?.length > 0 ?true : false, cacheTime : 0 });

  function getLabelValue(curservice){
    let foundValue = servicedata?.Service?.find((ob) => ob?.referenceId?.includes(curservice?.bookingNo));
    if(foundValue)
    return t("CS_CF_VIEW")
    else
    return t("CS_CF_TRACK")
  }

  if (isLoading || serviceloading) {
    return <Loader />;
  }


  return (
    <React.Fragment>
      <Header>{`${t("CS_TITLE_MY_BOOKINGS")} ${applicationsList ? `(${applicationsList.length})` : ""}`}</Header>
      <div>
        {applicationsList?.length > 0 &&
          applicationsList.map((application, index) => (
            <div key={index}>
              <ChbApplication application={application} tenantId={user?.permanentCity} buttonLabel={getLabelValue(application)}/>
            </div>
          ))}
        {!applicationsList?.length > 0 && <p style={{ marginLeft: "16px", marginTop: "16px" }}>{t("CHB_NO_APPLICATION_FOUND_MSG")}</p>}

        {applicationsList?.length !== 0 && (
          <div>
            <p style={{ marginLeft: "16px", marginTop: "16px" }}>
              <span className="link">{<Link to={`/digit-ui/citizen/chb/myBookings/${t1}`}>{t("CHB_LOAD_MORE_MSG")}</Link>}</span>
            </p>
          </div>
        )}
      </div>

      <p style={{ marginLeft: "16px", marginTop: "16px" }}>
        {t("chb_TEXT_NOT_ABLE_TO_FIND_THE_BOOKING")}{" "}
        <span className="link" style={{ display: "block" }}>
          <Link to="/digit-ui/citizen/chb/bookHall/searchHall">{t("CHB_COMMON_CLICK_HERE_TO_REGISTER_NEW_BOOKING")}</Link>
        </span>
      </p>
    </React.Fragment>
  );
};
