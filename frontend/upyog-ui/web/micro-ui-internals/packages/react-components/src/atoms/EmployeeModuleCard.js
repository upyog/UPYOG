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
//         <div className="body" className="custom-style">
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
//           <div className="links-wrapper" className="custom-style">
//             {links.map(({ count, label, link }, index) => (
//               <span className="link" key={index}>
//                 {link ? <Link to={link}>{label}</Link> : null}
//                 {count ? (
//                   <Fragment>
//                     {FsmHideCount ? null : <span className={"inbox-total"}>{count || "-"}</span>}
//                     <Link to={link}>
//                       <ArrowRightInbox />
//                     </Link>
//                   </Fragment>
//                 ) : null}
//               </span>
//             ))}
//           </div>
//         </div>
//       </div>
//     </div>
//   );
// };
const EmployeeModuleCard = ({ Icon, moduleName, kpis = [], links = [], isCitizen = false, className, styles, FsmHideCount }) => {
  return (
    <div className={className ? "employeeCard card-home customEmployeeCard" : "employeeCard card-home customEmployeeCard"} style={className ? {} : styles}>
      <div className="employeeCustomCard rc-employee-module-card-fullwidth">
        <span
          className="text-employee-card rc-employee-module-card-wrapper"
        >
          {moduleName}
        </span>
        <span className="logo-removeBorderRadiusLogo rc-employee-module-card-wrapper-2">{Icon}</span>
        <div className="employee-card-banner">
          <div className="body rc-employee-module-card-container-padding">
            <div className="rc-employee-module-card-flex-container">
              <div className="rc-card-based-options-flex-container">
            <div className="rc-employee-module-card-wrapper-3"><span className="icon-banner-employee rc-employee-module-card-wrapper-4">{Icon}</span></div>
            
            <div className="rc-employee-module-card-wrapper-5">
            {kpis.length !== 0 && (
              <div className="flex-fit" style={isCitizen ? { paddingLeft: "17px" } : {}}>

                {kpis.map(({ count, label, link }, index) => (
                  <div className="card-count rc-employee-module-card-fullwidth-2" key={index}>
                    {/*  */}
                    <div className="rc-employee-module-card-fullwidth-3">

                      <div className="rc-employee-module-card-centered">
                        {link ? (
                          <Link to={link} className="employeeTotalLink">
                            {label}
                          </Link>
                        ) : null}
                    </div>
                      <div className="rc-employee-module-card-centered">
                        <span className="rc-employee-module-card-text-style">{count || "-"}</span>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            )}
            </div>
            </div>
            <div>
            <div className="links-wrapper rc-employee-module-card-fullwidth-4">
              {links.map(({ count, label, link }, index) => (
                <div className="link rc-employee-module-card-flex-container-2" key={index}>
                  {link ? <div className="rc-card-based-options-flex-container"> <Link to={link}> {label} </Link>  <span>|</span> </div>: null}
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
      <div className="complaint-links-container rc-advertisement-module-card-container-padding">
        <div className="header" style={isCitizen ? { padding: "0px" } : headerStyle}>
          <span className="text removeHeight">{moduleName}</span>
          <span className="link">
            <a href={subHeaderLink}>
              <span className={`${"inbox-total"} rc-employee-module-card-flex-row`}>
                {subHeader || "-"}
                <span className="rc-employee-module-card-spacing">
                  {" "}
                  <ArrowRightInbox />
                </span>
              </span>
            </a>
          </span>
        </div>
        <div className="body rc-employee-module-card-container-padding">
          <div className="links-wrapper rc-employee-module-card-fullwidth-5">
            {links.map(({ count, label, link }, index) => (
              <span className="link full-employee-card-link" key={index}>
                {link ? (link?.includes('upyog-ui/')?<Link to={link}>{label}</Link>:<a href={link}>{label}</a>) : null}
              </span>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
};

export { EmployeeModuleCard, ModuleCardFullWidth };
