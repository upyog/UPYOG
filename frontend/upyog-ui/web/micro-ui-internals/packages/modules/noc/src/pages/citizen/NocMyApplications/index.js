/**
 * Displays the list of NOC applications created by the logged-in citizen with search, status overview,
 * and pagination support.
 */
import { Header, Loader,Card, CardLabel, TextInput, SubmitBar } from "@nudmcdgnpm/digit-ui-react-components";
import React, {useState, useEffect} from "react";
import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";
import NocApplication from "./NocApplication";

export const NocMyApplications = () => {
  const { t } = useTranslation();
  const [searchTerm, setSearchTerm] = useState("");
  const tenantId = Digit.ULBService.getCitizenCurrentTenant(true) || Digit.ULBService.getCurrentTenantId();
  const user = Digit.UserService.getUser()?.info;
  const [filters, setFilters] = useState(null);

  let filter = window.location.href.split("/").pop();
  let t1;
  let off;
  if (!isNaN(parseInt(filter))) {
    off = filter;
    t1 = parseInt(filter) + 4;
  } else {
    t1 = 4;
  }

  let filter1 = !isNaN(parseInt(filter))
    ? { limit: "10", offset: off, tenantId, mobileNumber: user?.mobileNumber }
    : { limit: "10", offset: "0", mobileNumber: user?.mobileNumber, tenantId };

  useEffect(() => {
      setFilters(filter1);
    }, [filter]);

  const { isLoading, isError, error, data } = Digit.Hooks.noc.useFireNOCSearch(tenantId, filters);
  const { FireNOCs: applicationsList=[] } = data || {};

  const sortedApplications = [...applicationsList].sort((a, b) => (b?.fireNOCDetails?.applicationDate || 0) - (a?.fireNOCDetails?.applicationDate || 0));

  const handleSearch = () => {
    const trimmedSearchTerm = searchTerm.trim();
    const searchFilters = {
      ...filter1,
      applicationNumber: trimmedSearchTerm || null,
    };
    
    // Update the filters state to trigger refetch
    setFilters(searchFilters);
  };

  if (isLoading) {
    return <Loader />;
  }

  return (
    <React.Fragment>
     <Header>{`${t("NOC_MY_APPLICATIONS_HEADER")} ${sortedApplications.length ? `(${sortedApplications.length})` : ""}`}</Header>
      <Card>
        <div className="fn-myapps-container">
          <div className="fn-myapps-search-row">
            <div className="fn-myapps-field-col">
              <div className="fn-myapps-field-inner">
                <CardLabel>{t("FN_APPLICATION_NUMBER_LABEL")}</CardLabel>
                <TextInput
                  placeholder={t("NOC_APPLICATION_NO")}
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                  className="fn-myapps-text-input"
                />
              </div>
            </div>
            <div>
              <div className="fn-myapps-search-btn-wrap">
              <SubmitBar label={t("ES_COMMON_SEARCH")} onSubmit={handleSearch} />
              <p className="link fn-myapps-clear-link"
                      onClick={() => {setSearchTerm("") }}>{t(`ES_COMMON_CLEAR_ALL`)}
                </p>
              </div>
            </div>
          </div>
        </div>
      </Card>

    <div>
      {sortedApplications.length > 0 &&
        sortedApplications.map((application, index) => (
          <div key={application?.applicationNumber || index}>
            <NocApplication
              application={application}
              tenantId={user?.permanentCity}
            />
          </div>
        ))}

      {sortedApplications.length === 0 && (
        <p style={{ marginLeft: "16px", marginTop: "16px" }}>
          {t(
            "NOC_NO_APPLICATION_FOUND_MSG"
          )}
        </p>
      )}

      {applicationsList.length !==0 && (
        <div>
          <p style={{ marginLeft: "16px", marginTop: "16px" }}>
            <span className="link">
              <Link
                to={`/upyog-ui/citizen/firenoc/my-applications/${t1}`}
              >
                {t("NOC_LOAD_MORE_MSG")}
              </Link>
            </span>
          </p>
        </div>
      )}
    </div>
    </React.Fragment>
  );
};
