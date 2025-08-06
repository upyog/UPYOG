import { Header, Loader, TextInput, Dropdown, SubmitBar, CardLabel, Card, MobileNumber } from "@upyog/digit-ui-react-components";
import React, { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";
import StreetVendingApplication from "./StreetVendingApplication";

/**
 * SVMyApplications Component
This component is responsible for displaying the street vending applications submitted by the user.
It fetches the applications using a custom hook and send it as a props in StreetVendingApplication component so that component show the data in list format .
If there are no applications found, a message is displayed informing the user.
The component also provides a link to load more applications if available, 
and a link for the user to apply for a new street vending application.*/

export const SVMyApplications = () => {
  const { t } = useTranslation();
  const tenantId = Digit.ULBService.getCitizenCurrentTenant(true) || Digit.ULBService.getCurrentTenantId();
  const user = Digit.UserService.getUser().info;

  const [searchTerm, setSearchTerm] = useState("");
  const [mobileTerm, setMobileTerm] = useState("")
  const [filters, setFilters] = useState(null);
  const [applicationsList, setApplicationsList] = useState([]);
  


  let filter = window.location.href.split("/").pop();
  let t1;
  let off;
  if (!isNaN(parseInt(filter))) {
    off = filter;
    t1 = parseInt(filter) + 50;
  } else {
    t1 = 4;
  }

  let initialFilters = { limit: "4", sortOrder: "ASC", sortBy: "createdTime", offset: "0", tenantId, isDraftApplication:false,mobileNumber:user?.mobileNumber };

  useEffect(() => {
    setFilters(initialFilters);
  }, [filter]);

  const { isLoading: isNonDraftLoading, data: nonDraftData } = Digit.Hooks.sv.useSvSearchApplication({
  filters: { ...filters, isDraftApplication: false },
});

const { isLoading: isDraftLoading, data: draftData } = Digit.Hooks.sv.useSvSearchApplication({
  filters: { ...filters, isDraftApplication: true, sortOrder: "DESC"  },
});

const previousDraftId = draftData?.SVDetail?.[0]?.draftId;


  useEffect(() => {
    if (draftData?.SVDetail && nonDraftData?.SVDetail) {
      setApplicationsList([
        ...(draftData.SVDetail || []),
        ...(nonDraftData.SVDetail || [])
      ]);
    }
  }, [draftData, nonDraftData]);

  // Handle discarding a draft application
  const handleDiscardApplication = (applicationToRemove) => {
    // Update the applications list by filtering out the discarded application
    setApplicationsList(prevApplications => 
      prevApplications.filter(app => app !== applicationToRemove)
    );
  };

if (isNonDraftLoading || isDraftLoading) {
  return <Loader />;
}

  const handleSearch = () => {
    const trimmedSearchTerm = searchTerm.trim();
    const searchFilters = {
      ...initialFilters,
      applicationNumber: trimmedSearchTerm || null,
      mobileNumber: mobileTerm || null,
    };

    // Update the filters state to trigger refetch
    setFilters(searchFilters);
  };

 
  return (
    <React.Fragment>
      <Header>{`${t("SV_MY_APPLICATIONS")} ${applicationsList ? `(${applicationsList.length})` : ""}`}</Header>

      <Card>
        <div style={{ marginLeft: "16px" }}>
          <div className="svsearchbox" >
            <div style={{ flex: 1 }}>
              <div className="svsearchfield" >
                <CardLabel>{t("SV_APPLICATION_NUMBER")}</CardLabel>
                <TextInput
                  placeholder={t("Enter Application No.")}
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                  style={{ width: "100%", padding: "8px", height: "150%" }}
                />
              </div>
            </div>
            <div style={{ flex: 1 }}>
              <div className="svsearchfield" >
                <CardLabel>{t("SV_REGISTERED_MOB_NUMBER")}</CardLabel>
                <TextInput
                  placeholder={t("Enter Mobile No.")}
                  value={mobileTerm}
                  onChange={(e) => setMobileTerm(e.target.value)}
                  style={{ width: "100%", padding: "8px", height: "150%" }}
                />
              </div>
            </div>
            <div>
              <div style={{ marginTop: "17%" }}>
                <SubmitBar label={t("ES_COMMON_SEARCH")} onSubmit={handleSearch} />
                <p
                  className="link svclearall"
                  onClick={() => {
                    setSearchTerm(""), setMobileTerm("");
                  }}
                >
                  {t(`ES_COMMON_CLEAR_ALL`)}
                </p>
              </div>
            </div>
          </div>
        </div>
      </Card>

      <div>
        {applicationsList?.length > 0 &&
          applicationsList.map((application, index) => (
            <div key={index}>
              <StreetVendingApplication application={application} tenantId={user?.permanentCity} buttonLabel={t("SV_TRACK")} previousDraftId={previousDraftId} onDiscard={handleDiscardApplication} />
            </div>
          ))}
        {!applicationsList?.length > 0 && <p style={{ marginLeft: "16px", marginTop: "16px" }}>{t("SV_NO_APPLICATION_FOUND_MSG")}</p>}

        {applicationsList?.length !== 0 && (
          <div>
            <p style={{ marginLeft: "16px", marginTop: "16px" }}>
              <span className="link">{<Link to={`/upyog-ui/citizen/sv/my-applications/${t1}`}>{t("SV_LOAD_MORE_MSG")}</Link>}</span>
            </p>
          </div>
        )}
      </div>

      <p style={{ marginLeft: "16px", marginTop: "16px" }}>
        {t("SV_NO_APPLICATION_FOUND")}{" "}
        <span className="link" style={{ display: "block" }}>
          <Link to="/upyog-ui/citizen/sv/apply/info">{t("SV_CLICK_TO_APPLY_NEW_APPLICATION")}</Link>
        </span>
      </p>
    </React.Fragment>
  );
};
