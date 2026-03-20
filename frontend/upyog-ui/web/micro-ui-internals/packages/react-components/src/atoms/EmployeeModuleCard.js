import React, { Fragment } from "react";
import { ArrowRightInbox } from "./svgindex";
import { Link } from "react-router-dom";

// const EmployeeModuleCard = ({ Icon, moduleName, kpis = [], links = [], isCitizen = false, className, styles, longModuleName=false, FsmHideCount }) => {
//   return (
//     <div className={className ? className : "employeeCard customEmployeeCard card-home home-action-cards"} style={styles ? styles : {}}>
//       <div className="complaint-links-container">
//         <div className="header" style={isCitizen ? { padding: "0px" } : longModuleName ? {alignItems:"flex-start"}:{}}>
//           <span className="text removeHeight">{moduleName}</span>
//           <span className="logo removeBorderRadiusLogo">{Icon}</span>
//         </div>
//         <div className="body" style={{ margin: "0px", padding: "0px" }}>
//           {kpis.length !== 0 && (
//             <div className="flex-fit" style={isCitizen ? { paddingLeft: "17px" } : {}}>
//               {kpis.map(({ count, label, link }, index) => (
//                 <div className="card-count" key={index}>
//                   <div>
//                     <span>{count ? count : count == 0 ? 0 : "-"}</span>
//                   </div>
//                   <div>
//                     {link ? (
//                       <Link to={link} className="employeeTotalLink">
//                         {label}
//                       </Link>
//                     ) : null}
//                   </div>
//                 </div>
//               ))}
//             </div>
//           )}
//           <div className="links-wrapper" style={{ width: "80%" }}>
//             {links.map(({ count, label, link }, index) => (
//               <span className="link" key={index}>
//                 {link ? <Link to={link}>{label}</Link> : null}
//                 {count ? (
//                   <>
//                     {FsmHideCount ? null : <span className={"inbox-total"}>{count || "-"}</span>}
//                     <Link to={link}>
//                       <ArrowRightInbox />
//                     </Link>
//                   </>
//                 ) : null}
//               </span>
//             ))}
//           </div>
//         </div>
//       </div>
//     </div>
//   );
// };
// const EmployeeModuleCard = ({ Icon, moduleName, kpis = [], links = [], isCitizen = false, className, styles, FsmHideCount }) => {
//   return (
//     <div className={className ? "employeeCard card-home customEmployeeCard" : "employeeCard card-home customEmployeeCard"} style={className ? {} : styles}>
//       <div className="employeeCustomCard" style={{ width: "100%", height: "85%", position: "relative" }}>
//         <span className="text-employee-card">{moduleName}</span>
//         <span className="logo-removeBorderRadiusLogo" style={{ position: "absolute", right: "10%", top: "10%" }}>{Icon}</span>
//         <div className="employee-card-banner">
//           <div className="body" style={{ margin: "0px", padding: "0px" }}>
//             <div style={{display: "flex",flexDirection: "column"}}>
//               <div style={{display:"flex"}}>
//             <div style={{ width: "30%", height: "50px" }}><span className="icon-banner-employee" style={{ position: "absolute", left: "10%", top: "10%", borderRadius: "5px", boxShadow: "5px 5px 5px 0px #e3e4e3" }}>{Icon}</span></div>

//             <div style={{width:"70%"}}>
//             {kpis.length !== 0 && (
//               <div className="flex-fit" style={isCitizen ? { paddingLeft: "17px" } : {}}>

//                 {kpis.map(({ count, label, link }, index) => (
//                   <div className="card-count" key={index} style={{ display: "flex", width: "100%",flexDirection: "column" }}>
//                     {/*  */}
//                     <div style={{ marginLeft: "auto", display: "flex", flexDirection: "column-reverse", width: "100%" }}>

//                       <div style={{textAlign:"center"}}>
//                         {link ? (
//                           <Link to={link} className="employeeTotalLink">
//                             {label}
//                           </Link>
//                         ) : null}
//                     </div>
//                       <div style={{ textAlign:"center"}}>
//                         <span style={{ color: "#ae1e28", fontSize: "18px", fontFamily: "sans-serif", fontWeight: "bold" }}>{count || "-"}</span>
//                       </div>
//                     </div>
//                   </div>
//                 ))}
//               </div>
//             )}
//             </div>
//             </div>
//             <div>
//             <div className="links-wrapper" style={{ width: "100%", display: "flex", fontSize: "0.8rem", paddingLeft: "10px", flexWrap:"wrap",flexDirection:"row",paddingTop:"10px"}}>
//               {links.map(({ count, label, link }, index) => (
//                 <div className="link" key={index} style={{ paddingLeft: "5px", color: "#a1a5b7",display:"flex" }}>
//                   {link ? <div style={{display:"flex"}}> <Link to={link}> {label} </Link>  <span>|</span> </div>: null}
//                 </div>

//               ))}
//             </div>
//           </div>
//           </div>
//           </div>
//         </div>
//       </div>

//       <div>
//       </div>
//     </div>
//   );
// };

const EmployeeModuleCard = ({ Icon, moduleName, kpis = [], links = [] }) => {
  return (
    <div
      className="employee-module-card"
      style={{
        background: "#fff",
        borderRadius: "8px",
        boxShadow: "0 2px 6px rgba(0,0,0,0.3)",
        overflow: "hidden",
        width: "280px",
        position: "relative",
        marginLeft: "30px",
        marginBottom: "30px"
      }}
    >
      {/* Header section */}
      <div style={{ padding: "16px", paddingRight: "60px", paddingBottom: "8px" }}>
        <div
          style={{
            fontSize: "1.1rem",
            fontWeight: "600",
            color: "#333",
            height: "44px", // fixed height
            lineHeight: "1.2",
            overflow: "hidden",
            display: "-webkit-box",
            WebkitLineClamp: 2, // max 2 lines
            WebkitBoxOrient: "vertical",
            textOverflow: "ellipsis",
          }}
        >
          {moduleName}
        </div>
      </div>

      {/* Divider */}
      <hr style={{ margin: "0", borderColor: "lightgray" }} />

      {/* Orange icon box (flush top-right corner) */}
      <div
        style={{
          backgroundColor: "#D40000",
          position: "absolute",
          top: 0,
          right: 0,
          width: "68px",
          height: "68px",
          borderBottomLeftRadius: "8px",
          display: "flex",
          alignItems: "center",
          justifyContent: "center",
          color: "#fff",
        }}
      >
        {Icon}
      </div>
      
      {/* KPI Section */}
      <div
        style={{
          display: "flex",
          justifyContent: "space-around",
          padding: "12px 0",
        }}
      >
        {kpis.map(({ count, label }, idx) => (
          <div key={idx} style={{ textAlign: "center" }}>
            <div
              style={{
                fontWeight: "700",
                fontSize: "1.1rem",
                color: "#000",
              }}
            >
              {count || "-"}
            </div>
            <div style={{ fontSize: "0.9rem", color: "#555" }}>{label}</div>
          </div>
        ))}
      </div>

      {/* Divider */}
      <hr style={{ margin: "0", borderColor: "lightgray" }} />

      {/* Links section */}
      <div style={{ padding: "12px 16px" }}>
        {links.map(({ label, link, count }, index) => (
          <div
            key={index}
            style={{
              display: "flex",
              alignItems: "center",
              color: index === 0 ? "#FE7A51" : "#555",
              fontWeight: index === 0 ? "600" : "400",
              marginBottom: "6px",
            }}
          >
            <Link
              to={link}
              style={{
                textDecoration: "none",
                color: index === 0 ? "#D40000" : "#555",
                display: "flex",
                alignItems: "center",
              }}
            >
              {label}
              {count && (
                <span
                  style={{
                    backgroundColor: "#D40000",
                    color: "#fff",
                    fontSize: "0.75rem",
                    borderRadius: "50%",
                    padding: "2px 6px",
                    marginLeft: "6px",
                    marginRight: "4px",
                  }}
                >
                  {count}
                </span>
              )}
              {index === 0 && <span style={{ marginLeft: "4px" }}>→</span>}
            </Link>
          </div>
        ))}
      </div>
    </div>
  );
};
const ModuleCardFullWidth = ({ moduleName, links = [], isCitizen = false, className, styles, headerStyle, subHeader, subHeaderLink }) => {
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
                {link ? (link?.includes('mycity-ui/') ? <Link to={link}>{label}</Link> : <a href={link}>{label}</a>) : null}
              </span>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
};

export { EmployeeModuleCard, ModuleCardFullWidth };