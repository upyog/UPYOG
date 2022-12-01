import React, { useState } from "react";
import { useTranslation } from "react-i18next";
import { Link } from "react-router-dom";

const mystyle = {
  display: "flex",
  alignItems: "center",
};
const navStyle = {
  width: "300px",
  display: "block",
  listStyle: "none",
  position: "relative",
  left: "70px",
  bottom: "20px",
};
const navLi = {
 marginBottom:"5px"
};
const ToolTipWrapper = ({ child, label, t }) => (
  <span className="tooltip">
    {child}
    <span className="tooltiptext" style={{ fontSize: "14px", marginLeft: "0px", bottom: "20%" }}>
      <div style={{ background: "#555", width: "100%", padding: "5px", borderRadius: "6px", whiteSpace: "pre" }}>{t(label)}</div>
    </span>
  </span>
);

const EmployeeSideBar = () => {
  const STADMIN = Digit.UserService.hasAccess("STADMIN");
  const NATADMIN = Digit.UserService.hasAccess("NATADMIN");
  const [click, setClick] = useState(false);
  const [first, setfirst] = useState("");
  console.log("setfirstsetfirstsetfirst", first);

  let key = "ACTION_TEST_HOME";

  if (STADMIN) {
    key = "ACTION_TEST_SURE_DASHBOARD";
  }
  if (NATADMIN) {
    key = "ACTION_TEST_NATDASHBOARD";
  }

  function getRedirectionUrl() {
    if (NATADMIN) return "/digit-ui/employee/payment/integration/dss/NURT_DASHBOARD";
    else if (STADMIN) return "/employee/integration/dss/home";
    else return "/employee";
  }
  // const Nav =styled.div``
  const { t } = useTranslation();
  const [sidebar, setSideBar] = useState(false);
  // const showSidebar = () => setSideBar(!sidebar);

  return (
    <div
      onMouseEnter={() => setClick(true)}
      onMouseLeave={() => {
        if (!click === false) {
          setfirst("");
        }
        setClick(false);
      }}
      className={click ? "display" : "sidebar"}
    >
      <div>
        <Link
          onClick={() => {
            setClick(click);
          }}
          to="/digit-ui/employee"
        >
          <div
            className="actions active"
            onClick={() => {
              if (first === "") {
                setfirst("Home");
              } else {
                setfirst("");
              }
            }}
          >
            <div style={mystyle}>
              <ToolTipWrapper
                child={
                  <svg id="ic_home" xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24">
                    <rect id="Rectangle_3" data-name="Rectangle 3" width="24" height="24" fill="none" />
                    <g id="Group_2" data-name="Group 2">
                      <path id="Path_3" data-name="Path 3" d="M0,0H24V24H0Z" fill="none" />
                      <path
                        id="Path_4"
                        data-name="Path 4"
                        d="M13,19h6V9.978L12,4.534,5,9.978V19h6V13h2Zm8,1a1,1,0,0,1-1,1H4a1,1,0,0,1-1-1V9.49a1,1,0,0,1,.386-.79l8-6.222a1,1,0,0,1,1.228,0l8,6.222A1,1,0,0,1,21,9.49V20Z"
                        fill="#696b74"
                      />
                    </g>
                  </svg>
                }
                t={t}
                label={"ACTION_TEST_HOME"}
              />
              {click ? <span style={{ display: "inline-block" }}>Home</span> : null}
            </div>
          </div>
          {first === "Home" ? <div style={navStyle} className="nav_drop"></div> : null}
        </Link>
        <Link
          onClick={() => {
            setClick(click);
          }}
          to="/digit-ui/employee/pgr/inbox"
        >
          <div
            className="actions active"
            onClick={() => {
              if (first === "") {
                setfirst("Home1");
              } else {
                setfirst("");
              }
            }}
          >
            <div style={mystyle}>
              <ToolTipWrapper
                child={
                  <svg id="ic_complaints" xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24">
                    <rect id="Rectangle_2" data-name="Rectangle 2" width="24" height="24" fill="none" />
                    <g id="Group_8" data-name="Group 8">
                      <path id="Path_13" data-name="Path 13" d="M0,0H24V24H0Z" fill="none" />
                      <path
                        id="Path_14"
                        data-name="Path 14"
                        d="M6.455,19,2,22.5V4A1,1,0,0,1,3,3H21a1,1,0,0,1,1,1V18a1,1,0,0,1-1,1ZM4,18.385,5.763,17H20V5H4ZM11,13h2v2H11Zm0-6h2v5H11Z"
                        fill="#696b74"
                      />
                    </g>
                  </svg>
                }
                t={t}
                label={"ACTION_TEST_HOME"}
              />
              {click ? <span style={{ display: "inline-block" }}>Complaints</span> : null}
            </div>
          </div>
          {first === "Home1" ? (
            <div style={navStyle} className="nav_drop">
              <li style={navLi}>Open Complaints</li>
              <li style={navLi}>Closed Complaints</li>
            </div>
          ) : null}
        </Link>

        <Link
          onClick={() => {
            setClick(click);
          }}
          to="/digit-ui/employee/"
        >
          <div
            className="actions active"
            onClick={() => {
              if (first === "") {
                setfirst("Home2");
              } else {
                setfirst("");
              }
            }}
          >
            <div style={mystyle}>
              <ToolTipWrapper
                child={
                  <svg id="ic_reports" xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24">
                    <rect id="Rectangle_4" data-name="Rectangle 4" width="24" height="24" fill="none" />
                    <g id="Group_9" data-name="Group 9">
                      <path id="Path_15" data-name="Path 15" d="M0,0H24V24H0Z" fill="none" />
                      <path
                        id="Path_16"
                        data-name="Path 16"
                        d="M21,8V20.993A1,1,0,0,1,20.007,22H3.993A.993.993,0,0,1,3,21.008V2.992A1,1,0,0,1,4,2H15ZM19,9H14V4H5V20H19ZM8,7h3V9H8Zm0,4h8v2H8Zm0,4h8v2H8Z"
                        fill="#696b74"
                      />
                    </g>
                  </svg>
                }
                t={t}
                label={"ACTION_TEST_HOME"}
              />
              {click ? <span style={{ display: "inline-block" }}>Reports</span> : null}
            </div>
          </div>
          {first === "Home2" ? (
            <div style={navStyle} className="nav_drop">
              <li>Collection Register</li>
            </div>
          ) : null}
        </Link>

        <Link
          onClick={() => {
            setClick(click);
          }}
          to="/digit-ui/employee/"
        >
          <div
            className="actions active"
            onClick={() => {
              if (first === "") {
                setfirst("Home3");
              } else {
                setfirst("");
              }
            }}
          >
            <div style={mystyle}>
              <ToolTipWrapper
                child={
                  <svg id="ic_fire_noc" xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24">
                    <rect id="Rectangle_5" data-name="Rectangle 5" width="24" height="24" fill="none" />
                    <g id="Group_12" data-name="Group 12" transform="translate(-356 -376)">
                      <path id="Path_21" data-name="Path 21" d="M0,0H24V24H0Z" transform="translate(356 376)" fill="none" />
                      <path
                        id="Union_2"
                        data-name="Union 2"
                        d="M1,20a1,1,0,0,1-1-1V1A1,1,0,0,1,1,0H17a1,1,0,0,1,1,1V19a1,1,0,0,1-1,1Zm1-2H16V2H2Zm3.327-4.461A3.821,3.821,0,0,1,6.2,9.212c.05-.047.216-.2.506-.453l.025-.023c.289-.259.516-.48.736-.715A6.811,6.811,0,0,0,9.142,5.111L9.172,5h.171l.042.035a4.068,4.068,0,0,1,1.74,2.786A6.65,6.65,0,0,1,9.92,11.407a1.184,1.184,0,0,0,1.028,1.77A2.819,2.819,0,0,0,12.29,12.8l.3-.164-.088.334A3.822,3.822,0,0,1,9.118,15.8c-.1.007-.2.012-.3.012A3.818,3.818,0,0,1,5.327,13.539Z"
                        transform="translate(359 378)"
                        fill="#696b74"
                      />
                    </g>
                  </svg>
                }
                t={t}
                label={"ACTION_TEST_HOME"}
              />
              {click ? <span style={{ display: "inline-block" }}>Fire Noc</span> : null}
            </div>
          </div>
          {first === "Home3" ? (
            <div style={navStyle} className="nav_drop">
              <li>Reports</li>
            </div>
          ) : null}
        </Link>

        <Link
          onClick={() => {
            setClick(click);
          }}
          to="/digit-ui/employee/"
        >
          <div
            className="actions active"
            onClick={() => {
              if (first === "") {
                setfirst("Home4");
              } else {
                setfirst("");
              }
            }}
          >
            <div style={mystyle}>
              <ToolTipWrapper
                child={
                  <svg id="ic_citizen_engagement" xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24">
                    <rect id="Rectangle_9" data-name="Rectangle 9" width="24" height="24" fill="none" />
                    <g id="Group_6" data-name="Group 6">
                      <path id="Path_9" data-name="Path 9" d="M0,0H24V24H0Z" fill="none" />
                      <path
                        id="Path_10"
                        data-name="Path 10"
                        d="M15,4H5V20H19V8H15ZM3,2.992A1,1,0,0,1,4,2H16l5,5V20.993A1,1,0,0,1,20.007,22H3.993A1,1,0,0,1,3,21.008ZM12,11.5A2.5,2.5,0,1,1,14.5,9,2.5,2.5,0,0,1,12,11.5ZM7.527,17a4.5,4.5,0,0,1,8.946,0Z"
                        fill="#696b74"
                      />
                    </g>
                  </svg>
                }
                t={t}
                label={"ACTION_TEST_HOME"}
              />
              {click ? <span style={{ display: "inline-block" }}>Citizen Engagement</span> : null}
            </div>
          </div>
          {first === "Home4" ? <div style={navStyle} className="nav_drop"></div> : null}
        </Link>

        <Link
          onClick={() => {
            setClick(click);
          }}
          to="/digit-ui/employee/"
        >
          <div
            className="actions active"
            onClick={() => {
              if (first === "") {
                setfirst("Home11");
              } else {
                setfirst("");
              }
            }}
          >
            <div style={mystyle}>
              <ToolTipWrapper
                child={
                  <svg id="ic_bpa" xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24">
                    <rect id="Rectangle_8" data-name="Rectangle 8" width="24" height="24" fill="none" />
                    <g id="Group_14" data-name="Group 14">
                      <path id="Path_25" data-name="Path 25" d="M0,0H24V24H0Z" fill="none" />
                      <path
                        id="Path_26"
                        data-name="Path 26"
                        d="M20,22H4a1,1,0,0,1-1-1V3A1,1,0,0,1,4,2H20a1,1,0,0,1,1,1V21A1,1,0,0,1,20,22Zm-1-2V4H5V20ZM7,6h4v4H7Zm0,6H17v2H7Zm0,4H17v2H7Zm6-9h4V9H13Z"
                        fill="#696b74"
                      />
                    </g>
                  </svg>
                }
                t={t}
                label={"ACTION_TEST_HOME"}
              />
              {click ? <span style={{ display: "inline-block" }}>Bpa Stakeholder</span> : null}
            </div>
          </div>
          {first === "Home11" ? (
            <div style={navStyle} className="nav_drop">
              <li>Search</li>
            </div>
          ) : null}
        </Link>

        <Link
          onClick={() => {
            setClick(click);
          }}
          to="/digit-ui/employee/"
        >
          <div
            className="actions active"
            onClick={() => {
              if (first === "") {
                setfirst("Home6");
              } else {
                setfirst("");
              }
            }}
          >
            <div style={mystyle}>
              <ToolTipWrapper
                child={
                  <svg id="ic_property_tax" xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24">
                    <rect id="Rectangle_6" data-name="Rectangle 6" width="24" height="24" fill="none" />
                    <g id="Group_1" data-name="Group 1">
                      <path id="Path_1" data-name="Path 1" d="M0,0H24V24H0Z" fill="none" />
                      <path
                        id="Path_2"
                        data-name="Path 2"
                        d="M21,21H3a1,1,0,0,1-1-1V12.487a1,1,0,0,1,.343-.754L6,8.544V4A1,1,0,0,1,7,3H21a1,1,0,0,1,1,1V20A1,1,0,0,1,21,21ZM9,19h3V12.942L8,9.454,4,12.942V19H7V15H9Zm5,0h6V5H8V7.127a.994.994,0,0,1,.657.247l5,4.359a1,1,0,0,1,.343.754Zm2-8h2v2H16Zm0,4h2v2H16Zm0-8h2V9H16ZM12,7h2V9H12Z"
                        fill="#696b74"
                      />
                    </g>
                  </svg>
                }
                t={t}
                label={"ACTION_TEST_HOME"}
              />
              {click ? <span style={{ display: "inline-block" }}>Property Tax</span> : null}
            </div>
          </div>
          {first === "Home5" ? (
            <div style={navStyle} className="nav_drop">
              <li style={navLi}>Search Property</li>
              <li style={navLi}>Pt Massters</li>
            </div>
          ) : null}
        </Link>

        <Link
          onClick={() => {
            setClick(click);
          }}
          to="/digit-ui/employee/"
        >
          <div
            className="actions active"
            onClick={() => {
              if (first === "") {
                setfirst("Home7");
              } else {
                setfirst("");
              }
            }}
          >
            <div style={mystyle}>
              <ToolTipWrapper
                child={
                  <svg id="ic_dashboard" xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24">
                    <rect id="Rectangle_7" data-name="Rectangle 7" width="24" height="24" fill="none" />
                    <g id="Group_10" data-name="Group 10">
                      <path id="Path_17" data-name="Path 17" d="M0,0H24V24H0Z" fill="none" />
                      <path
                        id="Path_18"
                        data-name="Path 18"
                        d="M13,21V11h8V21ZM3,13V3h8V13Zm6-2V5H5v6ZM3,21V15h8v6Zm2-2H9V17H5Zm10,0h4V13H15ZM13,3h8V9H13Zm2,2V7h4V5Z"
                        fill="#696b74"
                      />
                    </g>
                  </svg>
                }
                t={t}
                label={"ACTION_TEST_HOME"}
              />
              {click ? <span style={{ display: "inline-block" }}>Dashboard</span> : null}
            </div>
          </div>
          {first === "Home7" ? (
            <div style={navStyle} className="nav_drop">
              <li style={navLi}>Sure Dashboard</li>
              <li style={navLi}>Property Tax</li>
              <li style={navLi}>Trade Liscense</li>
            </div>
          ) : null}
        </Link>
        <Link
          onClick={() => {
            setClick(click);
          }}
          to="/digit-ui/employee/"
        >
          <div
            className="actions active"
            onClick={() => {
              if (first === "") {
                setfirst("Home8");
              } else {
                setfirst("");
              }
            }}
          >
            <div style={mystyle}>
              <ToolTipWrapper
                child={
                  <svg id="ic_mcollect" xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24">
                    <rect id="Rectangle_11" data-name="Rectangle 11" width="24" height="24" fill="none" />
                    <g id="Group_13" data-name="Group 13">
                      <path id="Path_23" data-name="Path 23" d="M0,0H24V24H0Z" fill="none" />
                      <path
                        id="Path_24"
                        data-name="Path 24"
                        d="M21,19h2v2H1V19H3V4A1,1,0,0,1,4,3H14a1,1,0,0,1,1,1V19h4V11H17V9h3a1,1,0,0,1,1,1ZM5,5V19h8V5Zm2,6h4v2H7ZM7,7h4V9H7Z"
                        fill="#696b74"
                      />
                    </g>
                  </svg>
                }
                t={t}
                label={"ACTION_TEST_HOME"}
              />
              {click ? <span style={{ display: "inline-block" }}>mCollect</span> : null}
            </div>
          </div>
          {first === "Home8" ? (
            <div style={navStyle} className="nav_drop">
             
            </div>
          ) : null}
        </Link>

        <Link
          onClick={() => {
            setClick(click);
          }}
          to="/digit-ui/employee/"
        >
          <div
            className="actions active"
            onClick={() => {
              if (first === "") {
                setfirst("Home9");
              } else {
                setfirst("");
              }
            }}
          >
            <div style={mystyle}>
              <ToolTipWrapper
                child={
                  <svg id="ic_trade_license" xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24">
                    <rect id="Rectangle_10" data-name="Rectangle 10" width="24" height="24" fill="none" />
                    <path id="Path_7" data-name="Path 7" d="M0,0H24V24H0Z" fill="none" />
                    <path
                      id="Path_8"
                      data-name="Path 8"
                      d="M15,3a1,1,0,0,1,1,1V6h5a1,1,0,0,1,1,1V20a1,1,0,0,1-1,1H3a1,1,0,0,1-1-1V7A1,1,0,0,1,3,6H8V4A1,1,0,0,1,9,3Zm1,5H8V19h8ZM4,8V19H6V8ZM14,5H10V6h4Zm4,3V19h2V8Z"
                      fill="#696b74"
                    />
                  </svg>
                }
                t={t}
                label={"ACTION_TEST_HOME"}
              />
              {click ? <span style={{ display: "inline-block" }}>Trade Licenceeeee</span> : null}
            </div>
          </div>
          {first === "Home9" ? (
            <div style={navStyle} className="nav_drop">
              <li style={navLi}>
                <Link to="/digit-ui/employee/tl/inbox">Trade Licenseeeee</Link>
              </li>
              <li style={navLi}><Link to="/digit-ui/employee/tl/search/application">Search</Link></li>
              <li style={navLi}><Link to="/digit-ui/employee//tl/new-application">Apply TL</Link></li>
              <li style={navLi}>Apply for TL</li>
            </div>
          ) : null}
        </Link>

        {/* <Link to="/digit-ui/employee/tl/search/application">
          <div className="actions active">
            <ToolTipWrapper
              child={
                <svg id="ic_complaints" xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24">
                  <rect id="Rectangle_2" data-name="Rectangle 2" width="24" height="24" fill="none" />
                  <g id="Group_8" data-name="Group 8">
                    <path id="Path_13" data-name="Path 13" d="M0,0H24V24H0Z" fill="none" />
                    <path
                      id="Path_14"
                      data-name="Path 14"
                      d="M6.455,19,2,22.5V4A1,1,0,0,1,3,3H21a1,1,0,0,1,1,1V18a1,1,0,0,1-1,1ZM4,18.385,5.763,17H20V5H4ZM11,13h2v2H11Zm0-6h2v5H11Z"
                      fill="#696b74"
                    />
                  </g>
                </svg>
              }
              t={t}
              label={"ACTION_TEST_HOME"}
            />
          </div>
        </Link>
        <Link to="/digit-ui/employee/tl/search/application">
          <div className="actions active">
            <ToolTipWrapper
              child={
                <svg id="ic_complaints" xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24">
                  <rect id="Rectangle_2" data-name="Rectangle 2" width="24" height="24" fill="none" />
                  <g id="Group_8" data-name="Group 8">
                    <path id="Path_13" data-name="Path 13" d="M0,0H24V24H0Z" fill="none" />
                    <path
                      id="Path_14"
                      data-name="Path 14"
                      d="M6.455,19,2,22.5V4A1,1,0,0,1,3,3H21a1,1,0,0,1,1,1V18a1,1,0,0,1-1,1ZM4,18.385,5.763,17H20V5H4ZM11,13h2v2H11Zm0-6h2v5H11Z"
                      fill="#696b74"
                    />
                  </g>
                </svg>
              }
              t={t}
              label={"ACTION_TEST_HOME"}
            />
          </div>
        </Link>
        <Link to="/digit-ui/employee/tl/search/application">
          <div className="actions active">
            <ToolTipWrapper
              child={
                <svg id="ic_complaints" xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24">
                  <rect id="Rectangle_2" data-name="Rectangle 2" width="24" height="24" fill="none" />
                  <g id="Group_8" data-name="Group 8">
                    <path id="Path_13" data-name="Path 13" d="M0,0H24V24H0Z" fill="none" />
                    <path
                      id="Path_14"
                      data-name="Path 14"
                      d="M6.455,19,2,22.5V4A1,1,0,0,1,3,3H21a1,1,0,0,1,1,1V18a1,1,0,0,1-1,1ZM4,18.385,5.763,17H20V5H4ZM11,13h2v2H11Zm0-6h2v5H11Z"
                      fill="#696b74"
                    />
                  </g>
                </svg>
              }
              t={t}
              label={"ACTION_TEST_HOME"}
            />
          </div>
        </Link>
        
        <a href={getRedirectionUrl()}>
          <div className="actions">
            <ToolTipWrapper
              child={
                <svg xmlns="http://www.w3.org/2000/svg" height="24" viewBox="0 0 24 24" width="24">
                  <path d="M0 0h24v24H0z" fill="none" />
                  <path d="M8.17 5.7L1 10.48V21h5v-8h4v8h5V10.25z" fill="white" />
                  <path d="M17 7h2v2h-2z" fill="none" />
                  <path d="M10 3v1.51l2 1.33L13.73 7H15v.85l2 1.34V11h2v2h-2v2h2v2h-2v4h6V3H10zm9 6h-2V7h2v2z" fill="white" />
                </svg>
              }
              t={t}
              label={"CORE_CHANGE_TENANT_DESCRIPTION"}
            />
          </div>
        </a> */}
      </div>
      {/*
      <a href={key.includes("DASHBOARD") ? `/employee/integration/dss/${NATADMIN ? "NURT_DASHBOARD" : "home"}` : "/employee"}>
        <div className="actions">
          <ToolTipWrapper
            child={
              key.includes("DASHBOARD") ? (
                <svg width="18" height="18" viewBox="0 0 18 18" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M0 10H8V0H0V10ZM0 18H8V12H0V18ZM10 18H18V8H10V18ZM10 0V6H18V0H10Z" fill="white" />
                </svg>
              ) : (
                <svg xmlns="http://www.w3.org/2000/svg" height="24" viewBox="0 0 24 24" width="24" title="National DSS Home">
                  <path d="M0 0h24v24H0z" fill="none" />
                  <path
                    d="M20 6h-4V4c0-1.11-.89-2-2-2h-4c-1.11 0-2 .89-2 2v2H4c-1.11 0-1.99.89-1.99 2L2 19c0 1.11.89 2 2 2h16c1.11 0 2-.89 2-2V8c0-1.11-.89-2-2-2zm-6 0h-4V4h4v2z"
                    fill="white"
                  />
                </svg>
              )
            }
            t={t}
            label={key}
          />
        </div>
      </a>
      <a href="/employee/integration/dss/home">
        <div className="actions">
          <ToolTipWrapper
            child={
              <svg xmlns="http://www.w3.org/2000/svg" height="24" viewBox="0 0 24 24" width="24" title="State DSS Home">
                <path d="M24 0H0v24h24z" fill="none" />
                <path
                  d="M17.66 7.93L12 2.27 6.34 7.93c-3.12 3.12-3.12 8.19 0 11.31C7.9 20.8 9.95 21.58 12 21.58c2.05 0 4.1-.78 5.66-2.34 3.12-3.12 3.12-8.19 0-11.31zM12 19.59c-1.6 0-3.11-.62-4.24-1.76C6.62 16.69 6 15.19 6 13.59s.62-3.11 1.76-4.24L12 5.1v14.49z"
                  fill="white"
                />
              </svg>
            }
            t={t}
            label={"ACTION_TEST_HOME"}
          />
        </div>
      </a> 
      */}
    </div>
  );
};

export default EmployeeSideBar;
