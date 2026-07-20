import { Card, Header, KeyNote, Loader, SubmitBar } from "@nudmcdgnpm/digit-ui-react-components";
import React, { useState } from "react";
import { useTranslation } from "react-i18next";
import { Link } from "react-router-dom";

// This component is responsible for displaying the list of applications submitted by the user in the Citizen portal. 
// It fetches the applications using a custom hook and displays them in a paginated format. 
// Each application card shows key details and provides links to view more details or make payments if applicable.
const MyApplications = ({ view }) => {
  const { t } = useTranslation();
  const userInfo = Digit.UserService.getUser()?.info || {};
  // const tenantId = Digit.ULBService.getCurrentTenantId();
  const tenantId = Digit.ULBService.getCitizenCurrentTenant(true) || Digit.ULBService.getCurrentTenantId();

  const itemsPerPage = 5;

  const { isLoading, data } = Digit.Hooks.ndc.useSearchApplication({}, tenantId);

  const [currentPage, setCurrentPage] = useState(1);

  if (isLoading) {
    return <Loader />;
  }

  const applicationsList = data?.data || [];
  const totalPages = Math.ceil(applicationsList.length / itemsPerPage);
  const indexOfLastItem = currentPage * itemsPerPage;
  const indexOfFirstItem = indexOfLastItem - itemsPerPage;
  const currentApplications = applicationsList.slice(indexOfFirstItem, indexOfLastItem);


  return (
    <React.Fragment>
      <Header>{t("TL_MY_APPLICATIONS_HEADER")}</Header>

      {currentApplications.map((application, index) => {
        const ownerForName = application?.Applications?.owners || [];
        const ownerNames = ownerForName
          ?.map((owner) => owner?.name)
          ?.filter(Boolean)
          ?.join(", ");
        return (
          <div key={`card-${index}`}>
           
            <Card>
              <KeyNote keyValue={t("BPA_APPLICATION_NUMBER_LABEL")} note={t(application?.Applications?.applicationNo)} />
              <KeyNote keyValue={t("TL_LOCALIZATION_OWNER_NAME")} note={t(ownerNames)} />
              <KeyNote keyValue={t("TL_HOME_SEARCH_RESULTS_APP_STATUS_LABEL")} note={t(application?.TL_HOME_SEARCH_RESULTS_APP_STATUS_LABEL)} />

                <div className="action-button-myapplication">
                <div className="ndc-margin-bottom-8">
                  <Link to={`/upyog-ui/citizen/ndc/search/application-overview/${application?.Applications?.applicationNo}`}>
                    <SubmitBar label={t("CS_VIEW_DETAILS")} />
                  </Link>
                </div>

                {application?.Applications?.applicationStatus === "PENDINGPAYMENT" && (
                  <div>
                    <Link to={`/upyog-ui/citizen/payment/collect/NDC/${application?.Applications?.applicationNo}/${tenantId}?tenantId=${tenantId}`}>
                      <SubmitBar label={t("CS_APPLICATION_DETAILS_MAKE_PAYMENT")} />
                    </Link>
                  </div>
                )}
              </div>
            </Card>
          </div>
        );
      })}

      {!applicationsList.length && <p className="ndc-application-list" >{t("PTR_NO_APPLICATION_FOUND_MSG")}</p>}

      {/* Pagination Controls */}
      {applicationsList.length > itemsPerPage && (
          <div className="ndc-application-overview-custom" >
          <button
            disabled={currentPage === 1}
            onClick={() => setCurrentPage((prev) => prev - 1)}
            className={currentPage === 1 ? 'ndc-cursor-not-allowed' : 'ndc-cursor-pointer'}
          >
            &#8592; Prev
          </button>

          <span>
            Page {currentPage} of {totalPages}
          </span>

          <button
            disabled={currentPage === totalPages}
            onClick={() => setCurrentPage((prev) => prev + 1)}
            className={currentPage === totalPages ? 'ndc-cursor-not-allowed' : 'ndc-cursor-pointer'}
          >
            Next &#8594;
          </button>
        </div>
      )}

      <p className="ndc-application-list">
        {t("PTR_TEXT_NOT_ABLE_TO_FIND_THE_APPLICATION")}{" "}
        <span className="link ndc-block-display">
          <Link to="/upyog-ui/citizen/ndc/new-application">{t("NDC_COMMON_CLICK_HERE_TO_REGISTER_NEW_APPLICATION")}</Link>
        </span>
      </p>
    </React.Fragment>
  );
};

export default MyApplications;
