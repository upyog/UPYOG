import React, { Fragment } from "react";
import { ArrowRightInbox, MapMarker } from "./svgindex";
import { Link } from "react-router-dom";

const EmployeeModuleCard = ({ Icon, moduleName, kpis = [], links = [], isCitizen = false, className, styles, FsmHideCount }) => {
  return (
    <div className={className ? "employeeCard card-home customEmployeeCard" : "employeeCard card-home customEmployeeCard"} style={className ? {} : styles}>
      <div className="employeeCustomCard" style={{ width: "100%", height: "85%", position: "relative" }}>
        <span className="text-employee-card">{moduleName}</span>
        <span className="logo-removeBorderRadiusLogo" style={{ position: "absolute", right: "10%", top: "10%" }}>{Icon}</span>
        <div className="employee-card-banner">
          <div className="body" style={{ margin: "0px", padding: "0px" }}>
            <div style={{display: "flex",flexDirection: "column"}}>
              <div style={{display:"flex"}}>
            <div style={{ width: "30%", height: "50px" }}><span className="icon-banner-employee" style={{ position: "absolute", left: "10%", top: "10%", borderRadius: "5px", boxShadow: "5px 5px 5px 0px #e3e4e3" }}>{Icon}</span></div>
            
            <div style={{width:"70%"}}>
            {kpis.length !== 0 && (
              <div className="flex-fit" style={isCitizen ? { paddingLeft: "17px" } : {}}>

                {kpis.map(({ count, label, link }, index) => (
                  <div className="card-count" key={index} style={{ display: "flex", width: "100%",flexDirection: "column" }}>
                    {/*  */}
                    <div style={{ marginLeft: "auto", display: "flex", flexDirection: "column-reverse", width: "100%" }}>

                      <div style={{textAlign:"center"}}>
                        {link ? (
                          <Link to={link} className="employeeTotalLink">
                            {label}
                          </Link>
                        ) : null}
                    </div>
                      <div style={{ textAlign:"center"}}>
                        <span style={{ color: "#ae1e28", fontSize: "18px", fontFamily: "sans-serif", fontWeight: "bold" }}>{count || "-"}</span>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            )}
            </div>
            </div>
            <div>
            <div className="links-wrapper" style={{ width: "100%", display: "flex", fontSize: "0.8rem", paddingLeft: "10px", flexWrap:"wrap",flexDirection:"row",paddingTop:"10px"}}>
              {links.map(({ count, label, link }, index) => (
                <div className="link" key={index} style={{ paddingLeft: "5px", color: "#a1a5b7",display:"flex" }}>
                  {link ? <div style={{display:"flex"}}> <Link to={link} style={{color:"black"}}> {label} </Link>  <span style={{margin:"0px 5px"}}>|</span> </div>: null}
                </div>

              ))}
            </div>
          </div>
          </div>
          </div>
        </div>
      </div>
      
      <div>
      </div>
    </div>
  );
};
const ModuleCardFullWidth = ({ moduleName,  links = [], isCitizen = false, className, styles, headerStyle, subHeader, subHeaderLink }) => {
  return (
    <div className={className ? className : "employeeCard card-home customEmployeeCard home-action-cards"} style={styles ? styles : {}}>
      <div className="complaint-links-container" style={{ padding: "10px" }}>
        <div className="header" style={isCitizen ? { padding: "0px" } : headerStyle}>
          <span className="text removeHeight">{moduleName}</span>
          <span className="link">
            <a href={subHeaderLink}>
              <span className={"inbox-total"} style={{ display: "flex", alignItems: "center", color: "#a82227", fontWeight: "bold" }}>
                {subHeader || "-"}
                <span style={{ marginLeft: "10px" }}>
                  {" "}
                  <ArrowRightInbox />
                </span>
              </span>
            </a>
          </span>
        </div>
        <div className="body" style={{ margin: "0px", padding: "0px" }}>
          <div className="links-wrapper" style={{ width: "100%", display: "flex", flexWrap: "wrap" }}>
            {links.map(({ count, label, link }, index) => (
              <span className="link full-employee-card-link" key={index}>
                {link ? (link?.includes('digit-ui/')?<Link to={link}>{label}</Link>:<a href={link}>{label}</a>) : null}
              </span>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
};

export { EmployeeModuleCard, ModuleCardFullWidth };
