import React, { useState, useEffect } from "react";
import { Header, Loader, TextInput, Dropdown, SubmitBar, CardLabel, Card } from "@nudmcdgnpm/digit-ui-react-components";
import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";
import EstateApplication from "./est-application";

export const ESTMyApplications = () => {
  const { t } = useTranslation();
  const { path: modulePath } = Digit.Hooks.useModuleBasePath();
  const tenantId = Digit.ULBService.getCitizenCurrentTenant(true) || Digit.ULBService.getCurrentTenantId();
  const user = Digit.UserService.getUser().info;

  const [searchTerm, setSearchTerm] = useState("");
  const [status, setStatus] = useState(null);
  const [isLoading, setIsLoading] = useState(false);
  const [data, setData] = useState({ Assets: [] });

  let filter = window.location.href.split("/").pop();
  let t1;
  let off;
  if (!isNaN(parseInt(filter))) {
    off = filter;
    t1 = parseInt(filter) + 50;
  } else {
    t1 = 4;
  }

  const fetchAllotments = async (searchFilters = {}) => {
    setIsLoading(true);
    try {
      const response = await Digit.ESTService.assetSearch({
        tenantId,
        filters: {
          AssetSearchCriteria: {
            tenantId,
            mobileNumber: user?.mobileNumber,
            ...(searchFilters.estateNo && { estateNo: searchFilters.estateNo }),
            ...(searchFilters.assetStatus && { assetStatus: searchFilters.assetStatus })
          }
        }
      });
      setData(response || { Assets: [] });
    } catch (error) {
      console.error("Error fetching assets:", error);
      setData({ Assets: [] });
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchAllotments();
  }, []);

  const handleSearch = () => {
    const trimmedSearchTerm = searchTerm.trim();
    const searchFilters = {
      estateNo: trimmedSearchTerm || undefined,
      assetStatus: status?.code || undefined,
    };

    fetchAllotments(searchFilters);
  };

  if (isLoading) {
    return <Loader />;
  }

  const statusOptions = [
    { code: "ACTIVE", value: t("EST_ACTIVE") },
    { code: "PENDING", value: t("EST_PENDING") },
    { code: "EXPIRED", value: t("EST_EXPIRED") },
  ];

  const filteredApplications = data?.Assets || [];

  return (
    <React.Fragment>
      <Header>{`${t("EST_MY_APPLICATIONS")} (${filteredApplications.length})`}</Header>
      <Card>
        <div style={{ marginLeft: "16px" }}>
          <div style={{ display: "flex", flexDirection: "row", alignItems: "center", gap: "16px" }}>
            <div style={{ flex: 1 }}>
              <div style={{ display: "flex", flexDirection: "column" }}>
                <CardLabel>{t("EST_ASSET_NUMBER")}</CardLabel>
                <TextInput
                  placeholder={t("Enter Asset Number")}
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                  style={{ width: "100%", padding: "8px", height: "150%" }}
                />
              </div>
            </div>
            <div style={{ flex: 1 }}>
              <div style={{ display: "flex", flexDirection: "column" }}>
                <CardLabel>{t("PT_COMMON_TABLE_COL_STATUS_LABEL")}</CardLabel>
                <Dropdown
                  className="form-field"
                  selected={status}
                  select={setStatus}
                  option={statusOptions}
                  placeholder={t("Select Status")}
                  optionKey="value"
                  style={{ width: "100%" }}
                  t={t}
                />
              </div>
            </div>
            <div>
              <div style={{ marginTop: "17%" }}>
                <SubmitBar label={t("ES_COMMON_SEARCH")} onSubmit={handleSearch} />
                <p
                  className="link"
                  style={{ marginLeft: "30%", marginTop: "10px", display: "block" }}
                  onClick={() => {
                    setSearchTerm(""); 
                    setStatus(null);
                    fetchAllotments();
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
        {filteredApplications.length > 0 &&
          filteredApplications.map((application, index) => (
            <div key={application.assetId || index}>
              <EstateApplication 
                application={application} 
                tenantId={tenantId} 
                buttonLabel={t("EST_SUMMARY")}
              />
            </div>
          ))}
        {filteredApplications.length === 0 && !isLoading && (
          <p style={{ marginLeft: "16px", marginTop: "16px" }}>{t("EST_NO_APPLICATION_FOUND_MSG")}</p>
        )}

        {filteredApplications.length !== 0 && data?.count > t1 && (
          <div>
            <p style={{ marginLeft: "16px", marginTop: "16px" }}>
              <span className="link">
                <Link to={`${modulePath}/my-applications/${t1}`}>{t("EST_LOAD_MORE_MSG")}</Link>
              </span>
            </p>
          </div>
        )}
      </div>
    </React.Fragment>
  );
};

export default ESTMyApplications;