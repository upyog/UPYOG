import { Header, Loader } from "@egovernments/digit-ui-react-components";
import React from "react";
import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";
import PTPayments from "./PTPayments";


export const PTMyPayments = () => {
  const { t } = useTranslation();
  const tenantId = Digit.ULBService.getCurrentTenantId();

  const result = Digit.Hooks.ptr.usePTRSearch({});
  const consumerCode = result?.data?.PetRegistrationApplications?.map((a) => a.applicationNumber).join(",");

  const {data, isLoading, error} = Digit.Hooks.ptr.useMyPetPayments({tenantId : tenantId,filters: {consumerCodes:consumerCode}},{enabled:result?.data?.PetRegistrationApplications.length>0?true:false, propertyData:result?.data?.PetRegistrationApplications});
  
  if (isLoading || result?.isLoading) {
    return <Loader />;
  }
  const applicationsList = data && data?.Payments || [];

  return (
    <React.Fragment>
      <Header>{`${t("PTR_MY_PAYMENTS_HEADER")} ${applicationsList ? `(${applicationsList.length})` : ""}`}</Header>
      <div>
        {applicationsList?.length > 0 &&
          applicationsList.map((application, index) => (
            <div key={index}>
              <PTPayments application={application} />
            </div>
          ))}
        {!applicationsList?.length > 0 && <p style={{ marginLeft: "16px", marginTop: "16px" }}>{t("PTR_NO_APPLICATION_FOUND_MSG")}</p>}
      </div>
     {/*  <p style={{ marginLeft: "16px", marginTop: "16px" }}>
        {t("PT_TEXT_NOT_ABLE_TO_FIND_THE_PROPERTY")}{" "}
        <span className="link" style={{ display: "block" }}>
          <Link to="/digit-ui/citizen/pt/property/citizen-search">{t("PT_COMMON_CLICK_HERE_TO_SEARCH_THE_PROPERTY")}</Link>
        </span>
      </p> */}
    </React.Fragment>
  );
};