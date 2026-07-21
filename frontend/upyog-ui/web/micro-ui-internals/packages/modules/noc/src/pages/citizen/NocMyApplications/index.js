import { Header, Loader } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";
import NocApplication from "./NocApplication";

export const NocMyApplications = () => {
  const { t } = useTranslation();
  const tenantId = Digit.ULBService.getCitizenCurrentTenant(true) || Digit.ULBService.getCurrentTenantId();
  const user = Digit.UserService.getUser()?.info;

  let filter = window.location.href.split("/").pop();
  let t1;
  let off;
  if (!isNaN(parseInt(filter))) {
    off = filter;
    t1 = parseInt(filter) + 10;
  } else {
    t1 = 10;
  }

  let filter1 = !isNaN(parseInt(filter))
    ? { limit: "10", offset: off, tenantId, mobileNumber: user?.mobileNumber }
    : { limit: "10", offset: "0", mobileNumber: user?.mobileNumber, tenantId };

  const { isLoading, isError, error, data } = Digit.Hooks.noc.useFireNOCSearch(tenantId, filter1);
  const { FireNOCs: applicationsList } = data || {};

  if (isLoading) {
    return <Loader />;
  }

  return (
    <React.Fragment>
      <Header>{`${t("NOC_MY_APPLICATIONS_HEADER")} ${applicationsList ? `(${applicationsList.length})` : ""}`}</Header>
      <div>
        {applicationsList?.length > 0 &&
          applicationsList.map((application, index) => (
            <div key={index}>
              <NocApplication application={application} tenantId={user?.permanentCity} />
            </div>
          ))}
        {!applicationsList?.length > 0 && <p style={{ marginLeft: "16px", marginTop: "16px" }}>{t("NOC_NO_APPLICATION_FOUND_MSG", "No applications found.")}</p>}

        {applicationsList?.length !== 0 && applicationsList?.length >= 10 && (
          <div>
            <p style={{ marginLeft: "16px", marginTop: "16px" }}>
              <span className="link">
                <Link to={`/upyog-ui/citizen/firenoc/my-applications/${t1}`}>{t("NOC_LOAD_MORE_MSG", "Load More")}</Link>
              </span>
            </p>
          </div>
        )}
      </div>
    </React.Fragment>
  );
};
