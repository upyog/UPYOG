import React from "react";
import { Switch, useLocation, Link } from "react-router-dom";
// import { PrivateRoute, BreadCrumb } from "@egovernments/digit-ui-react-components";

const CommonDashboard = ({ path,data,title }) => {
  console.log(path);

  return (
    <div>
      <div className="fileText">
        {" "}
        <h3>{title}</h3>
      </div>
      <div className="FileFlowWrapper">
        <div className="cardWrapper">
          {data?.map((item, index) => (
           item.link?(
            <Link to={item.link}>
            <div className="file">
              <div className="contentMenu">
                <div className="contentImg">{item.img}</div>
                <div className="contentText">
                  <h6>{item.title}</h6>
                  <span>{item.subTitle}</span>
                </div>
              </div>
            </div>{" "}
          </Link>
           ):
          ( <div className="file">
             <div className="contentMenu">
               <div className="contentImg">{item.img}</div>
               <div className="contentText">
                 <h6>{item.title}</h6>
                 <span>{item.subTitle}</span>
               </div>
             </div>
           </div>)
          ))}
        </div>
      </div>
    </div>
  );
};

export default CommonDashboard;
