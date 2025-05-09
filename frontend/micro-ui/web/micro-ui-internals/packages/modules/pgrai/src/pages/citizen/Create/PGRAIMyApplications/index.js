import { Header, Loader, Card, CardLabel, TextInput, Dropdown, SubmitBar } from "@nudmcdgnpm/digit-ui-react-components";
import React, {Fragment} from "react";
import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";
import PGRApplication from "./PGRAI-application";


export const PGRAIMyApplications = () => {
    const { t } = useTranslation();
    const tenantId = Digit.ULBService.getCitizenCurrentTenant(true) || Digit.ULBService.getCurrentTenantId();
    const user = Digit.UserService.getUser().info;
  
    // const [searchTerm, setSearchTerm] = useState("");
    // const [status, setStatus] = useState(null);
    // const [filters, setFilters] = useState(null);
  
    let filter = window.location.href.split("/").pop();
    let t1;
    let off;
    if (!isNaN(parseInt(filter))) {
      off = filter;
      t1 = parseInt(filter) + 50;
    } else {
      t1 = 4;
    }
  
    let initialFilters = !isNaN(parseInt(filter))
      ? { limit: "50", sortOrder: "ASC", sortBy: "createdTime", offset: off, tenantId }
      : { limit: "4", sortOrder: "ASC", sortBy: "createdTime", offset: "0", tenantId, mobileNumber:user?.mobileNumber };
  
    // useEffect(() => {
    //   setFilters(initialFilters);
    // }, [filter]);
  
    // Use the search hook with dynamic filters
  const { isLoading, isError, error, data } = Digit.Hooks.pgrAi.useSearchPGRAI({tenantId});
  console.log("data", data);
  
    // const handleSearch = () => {
    //   const trimmedSearchTerm = searchTerm.trim();
    //   const searchFilters = {
    //     ...initialFilters,
    //     bookingNo: trimmedSearchTerm || undefined,
    //     status: status?.code || undefined,
    //   };
  
    //   // Update the filters state to trigger refetch
    //   setFilters(searchFilters);
    // };
  
    if (isLoading) {
      return <Loader />;
    }
  
   
  
    const filteredApplications = data?.ServiceWrappers || [];  
    console.log("filteredApplications", filteredApplications);
    return (
      <React.Fragment>
        <Header>{`${t("PGR_AI_MY_BOOKINGS_HEADER")} (${filteredApplications.length})`}</Header>
        <Card>
          <div style={{ marginLeft: "16px" }}>
            <div style={{ display: "flex", flexDirection: "row", alignItems: "center", gap: "16px" }}>
              <div style={{ flex: 1 }}>
                <div style={{ display: "flex", flexDirection: "column" }}>
                  <CardLabel>{t("PGR_AIR_BOOKING_NO")}</CardLabel>
                  <TextInput
                    placeholder={t("Enter Booking No.")}
                    // value={searchTerm}
                    // onChange={(e) => setSearchTerm(e.target.value)}
                    style={{ width: "100%", padding: "8px", height: "150%" }}
                  />
                </div>
              </div>
              <div style={{ flex: 1 }}>
                <div style={{ display: "flex", flexDirection: "column" }}>
                  <CardLabel>{t("PT_COMMON_TABLE_COL_STATUS_LABEL")}</CardLabel>
                  <Dropdown
                   
                  />
                </div>
              </div>
              <div>
                <div style={{ marginTop: "17%" }}>
                  <SubmitBar label={t("ES_COMMON_SEARCH")} />
                  <p
                    className="link"
                    style={{ marginLeft: "30%", marginTop: "10px", display: "block" }}
                    onClick={() => {
                      setSearchTerm(""), setStatus("");
                    }}
                  >
                    {t(`ES_COMMON_CLEAR_ALL`)}
                  </p>
                </div>
              </div>
            </div>
            <Link to="/digit-ui/citizen/ads/bookad/searchads">
                <SubmitBar style={{borderRadius:"30px",width:"20%" }} label={t("PGR_AI_NEW_BOOKING")+" +"} />
              </Link>
          </div>
        </Card>
        <div>
          {filteredApplications.length > 0 &&
            filteredApplications.map((application, index) => (
              <div key={index}>
                <PGRApplication application={application} tenantId={tenantId} buttonLabel={t("PGR_AI_SUMMARY")} />
              </div>
            ))}
          {filteredApplications.length === 0 && !isLoading && (
            <p style={{ marginLeft: "16px", marginTop: "16px" }}>{t("PGR_AI_NO_APPLICATION_FOUND_MSG")}</p>
          )}
  
          {filteredApplications.length !== 0 && data?.count > t1 && (
            <div>
              <p style={{ marginLeft: "16px", marginTop: "16px" }}>
                <span className="link">
                  <Link to={`/digit-ui/citizen/ads/myBookings/${t1}`}>{t("PGR_AI_LOAD_MORE_MSG")}</Link>
                </span>
              </p>
            </div>
          )}
        </div>
        </React.Fragment>
    );
};
