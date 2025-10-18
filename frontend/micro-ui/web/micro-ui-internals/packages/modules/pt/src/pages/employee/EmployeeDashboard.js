import React, { useEffect, useState } from "react";
import { Chart as ChartJS, ArcElement, Tooltip, Legend } from "chart.js";
import { Doughnut } from "react-chartjs-2";
import { useTranslation } from "react-i18next";
import { CardLabel, Dropdown, Header } from "@upyog/digit-ui-react-components";
import { LocationService } from "../../../../../libraries/src/services/elements/Location";
import { ServiceBasedDashboard } from "./ServiceBasedDashboard";
import { RevenueBasedDashboard } from "./RevenueBasedDashboard";

ChartJS.register(ArcElement, Tooltip, Legend);

const EmployeeDashboard = (props) => {
  console.log("EmployeeDashboard==", props);
  let userRole = '';
  if(props?.userDetails && props?.userDetails.info && props?.userDetails.info?.roles) {
    props?.userDetails.info.roles.map((role)=>{
      if (role?.code == "EXECUTING_OFFICER") {
        userRole = role;
      }
    })
  }
  let data = {
    labels: ["Red", "Blue", "Yellow", "Green", "Purple", "Orange"],
    datasets: [
      {
        label: "# of Votes",
        data: [12, 19, 3, 5, 2, 3],
        backgroundColor: [
          "rgba(255, 99, 132, 0.2)",
          "rgba(54, 162, 235, 0.2)",
          "rgba(255, 206, 86, 0.2)",
          "rgba(75, 192, 192, 0.2)",
          "rgba(153, 102, 255, 0.2)",
          "rgba(255, 159, 64, 0.2)",
        ],
        borderColor: [
          "rgba(255, 99, 132, 1)",
          "rgba(54, 162, 235, 1)",
          "rgba(255, 206, 86, 1)",
          "rgba(75, 192, 192, 1)",
          "rgba(153, 102, 255, 1)",
          "rgba(255, 159, 64, 1)",
        ],
        borderWidth: 1,
      },
    ],
  };
  const { data: cities, isLoading } = Digit.Hooks.useTenants();
  console.log("cities==", cities);
  const [wardList, setWardList] = useState([]);
  const { t } = useTranslation();
  const [city, setCity] = useState(null);
  const [ward, setWard] = useState(null);
  const [fromDate, setFromDate] = useState(null);
  const [toDate, setToDate] = useState(null);

   const [activeTab, setActiveTab] = useState("service");

  const [resetTriggered, setResetTriggered] = useState(false);
  const [dashboardData, setDashboardData] = useState(null);
  const [cityDisable, setCityDisable] = useState(false);

  

  const formatDate = (data) => {
    const date = new Date(data);
    const formatted = date.toLocaleDateString("en-GB"); // dd/MM/yyyy
    const finalFormat = formatted.replaceAll("/", "-");
    console.log(finalFormat); // "16-07-2025"
    return finalFormat;
  };
  const onSearch = (e) => {
    e.preventDefault();
    console.log("onSearch");
    loadDashboardData();
  };
  const onReset = (e) => {
    e.preventDefault();
    if(!cityDisable) {
      setCity(null);
    }
    
    setWard(null);
    setFromDate(null);
    setToDate(null);
    setResetTriggered(true);
  };
  useEffect(() => {
    if (resetTriggered) {
      loadDashboardData();
      setResetTriggered(false); // reset flag
    }
  }, [resetTriggered]);
  useEffect(()=>{
    if(userRole && userRole?.tenantId && cities && cities.length>0) {
      cities.filter((city)=>{
        if(userRole?.tenantId===city?.code) {
          setCity(city);
          setResetTriggered(true);
          setCityDisable(true)
        }
      })
    }
  },[])
  const loadDashboardData = async () => {
    try {
      let DashboardFilters = {
        tenantid: city?.code || null,
        ward: ward?.name || null,
        fromDate: fromDate ? formatDate(fromDate) : null,
        toDate: toDate ? formatDate(toDate) : null,
      };
      let dashboardData = await Digit.PTService.dashboardSearch(DashboardFilters);
      console.log("dashboardData==", dashboardData);
      setDashboardData(dashboardData?.Data)
      // if(wards?.TenantBoundary?.length>0)
      // setWardList(wards?.TenantBoundary[0]?.boundary || [])
    } catch (err) {
      alert("Something went wrong while fetching Dashboard Data!!");
    }
  };
  useEffect(() => {
    if(!userRole && !userRole?.tenantId)
    loadDashboardData();
  }, []);
  useEffect(async () => {
    if (city && city?.code) {
      setWard(null);
      try {
        let wards = await Digit.LocationService.getBlocks(city?.code);
        if (wards?.TenantBoundary?.length > 0) setWardList(wards?.TenantBoundary[0]?.boundary || []);
      } catch (err) {
        alert("Something went wrong while fetching wards!!");
      }
    }
  }, [city]);

  return (
    <div className="employee-app-container">
      <div className="dashboard-filter">
        <div className="">
          <form>
            <div id="form-print">
              {<Header>{t("Dashboard")}</Header>}
              <div >
                <div className="card" style={{maxWidth: "100%"}}>
                  <div className="row">
                    <div className="col-sm-3" style={{ display: "inline-block" }}>
                      <CardLabel>{`${t("City")}`}</CardLabel>
                      <Dropdown
                        isMandatory
                        optionCardStyles={{ zIndex: 111111 }}
                        selected={city}
                        optionKey="name"
                        option={cities}
                        select={setCity}
                        disable={cityDisable}
                        t={t}
                      />
                    </div>
                    <div className="col-sm-3" style={{ display: "inline-block" }}>
                      <CardLabel>{`${t("Ward")}`}</CardLabel>
                      <Dropdown
                        isMandatory
                        optionCardStyles={{ zIndex: 111111 }}
                        selected={ward}
                        optionKey="name"
                        option={wardList}
                        select={setWard}
                        t={t}
                      />
                    </div>
                    <div className="col-sm-3" style={{ display: "inline-block" }}>
                      <CardLabel>{`${t("From Date")}`}</CardLabel>
                      {/* <DatePicker
                            isRequired={true}
                            date={submissionDate}
                            onChange={(d) => {
                            setSubmissionDate(d);
                            }}
                        /> */}
                      <input
                        className={`employee-card-input`}
                        // className={`${props.disabled ? "disabled" : ""}`}
                        // style={{ width: "calc(100%-62px)" }}
                        // style={{ right: "6px", zIndex: "100", top: 6, position: "absolute", opacity: 0, width: "100%" }}
                        value={fromDate ? fromDate : ""}
                        type="date"
                        onChange={(d) => {
                          setFromDate(d.target.value);
                        }}
                        max={new Date().toISOString().split("T")[0]}
                        required={false}
                      />
                    </div>
                    <div className="col-sm-3" style={{ display: "inline-block" }}>
                      <CardLabel>{`${t("To Date")}`}</CardLabel>
                      {/* <DatePicker
                            isRequired={true}
                            date={submissionDate}
                            onChange={(d) => {
                            setSubmissionDate(d);
                            }}
                        /> */}
                      <input
                        className={`employee-card-input`}
                        // className={`${props.disabled ? "disabled" : ""}`}
                        // style={{ width: "calc(100%-62px)" }}
                        // style={{ right: "6px", zIndex: "100", top: 6, position: "absolute", opacity: 0, width: "100%" }}
                        value={toDate ? toDate : ""}
                        type="date"
                        max={new Date().toISOString().split("T")[0]}
                        onChange={(d) => {
                          setToDate(d.target.value);
                        }}
                        required={false}
                      />
                    </div>
                  </div>
                  <hr />
                  <div style={{ display: "inline-flex", justifyContent: "end", marginTop: "10px" }}>
                    <div style={{ display: "inline" }}>
                      <button
                        onClick={onSearch}
                        className="submit-bar"
                        style={{
                          color: "white",
                          float: "right",
                          marginLeft: "10px",
                        }}
                      >
                        {t("Search")}
                      </button>
                      <button
                        onClick={onReset}
                        className="submit-bar"
                        style={{
                          color: "white",
                          float: "right",
                          marginLeft: "10px",
                        }}
                      >
                        {t("Reset")}
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            {/* <div className="card">
                    
                </div> */}
          </form>
        </div>
      </div>
      <div className="">
        {dashboardData && dashboardData[0] ?  (
            <div>
                <div className="panel panel-default">
            <div className="panel-heading panel-heading-nav">
                <ul className="nav nav-tabs">
                <li className={activeTab === "service" ? "active" : ""}>
                    <a href="#" onClick={(e) => { e.preventDefault(); setActiveTab("service"); }}>Service</a>
                </li>
                <li className={activeTab === "revenue" ? "active" : ""}>
                    <a href="#" onClick={(e) => { e.preventDefault(); setActiveTab("revenue"); }}>Revenue</a>
                </li>
                </ul>
            </div>
            <div className="panel-body">
                <div className="tab-content">
                {activeTab === "service" && (
                    <div className="tab-pane active" id="service">
                        <ServiceBasedDashboard dashboardData={dashboardData} />
                    </div>
                )}
                {activeTab === "revenue" && (
                    <div className="tab-pane active" id="revenue">
                        <RevenueBasedDashboard dashboardData={dashboardData} />
                    </div>
                )}
                </div>
            </div>
        </div>
            </div>
        ) :  <div>No data found</div>}
        
        
      </div>
    </div>
  );
};
export default EmployeeDashboard;