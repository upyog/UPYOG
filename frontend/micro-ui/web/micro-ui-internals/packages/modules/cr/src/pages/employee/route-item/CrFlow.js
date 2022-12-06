import React from "react";
import { Switch, useLocation, Link } from "react-router-dom";
import { PrivateRoute, BreadCrumb } from "@egovernments/digit-ui-react-components";
import { ReactComponent as BankIcon } from "../Img/BankIcon.svg";
import { ReactComponent as FileProtected } from "../Img/FileProtected.svg";

const CrFlow = ({ path }) => {
  console.log(path);
  const cardMenuData = [
    {
      title: "New Registration",
      subTitle: "Inbox",
      img: <BankIcon />,
      link: `${path}/child-details`,
    },

    {
      title: "Name Inclusion",
      subTitle: "Inbox",
      img: <FileProtected />,
      link: `${path}/structure-type`,
    },
    {
      title: "Correction",
      subTitle: "Inbox",
      img: <FileProtected />, 
    },
    {
      title: "Suspension",
      subTitle: "Inbox",
      img: <FileProtected />,
    },
    {
      title: "Cancellation",
      subTitle: "Inbox",
      img: <FileProtected />,
    },
    {
      title: "Revoke",
      subTitle: "Inbox",
      img: <FileProtected />,
    },
    
  ];
  return (
    <div>
      <div className="fileText">
        {" "}
        <h3>Select Functional Modules</h3>
      </div>
      <div className="FileFlowWrapper">
        <div className="cardWrapper">
          {cardMenuData?.map((item, index) => (
           item.link?(
            <Link to={item.link}>
            {/* <Link to='trade-lisense'> */}
            <div className="crfile">
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
          ( <div className="crfile">
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

export default CrFlow;
