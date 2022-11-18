import React from "react";
import { Switch, useLocation, Link } from "react-router-dom";
import { PrivateRoute, BreadCrumb } from "@egovernments/digit-ui-react-components";
import { ReactComponent as BankIcon } from "../Img/BankIcon.svg";
import { ReactComponent as FileProtected } from "../Img/FileProtected.svg";

const FileFlow = ({ path }) => {
  console.log(path);
  const cardMenuData = [
    {
      title: "Finance",
      subTitle: "Inbox",
      img: <BankIcon />,
    },

    {
      title: "Trade License",
      subTitle: "Inbox",
      img: <FileProtected />,
      link: `${path}/trade-lisense`,
    },
    {
      title: "Trade License-1",
      subTitle: "Inbox",
      img: <FileProtected />, 
    },
    {
      title: "Trade License-2",
      subTitle: "Inbox",
      img: <FileProtected />,
    },
    {
      title: "Trade License-3",
      subTitle: "Inbox",
      img: <FileProtected />,
    },
    {
      title: "Trade License-4",
      subTitle: "Inbox",
      img: <FileProtected />,
    },
    {
      title: "Trade License-5",
      subTitle: "Inbox",
      img: <FileProtected />,
    },
  ];
  return (
    <div>
      <div className="fileText">
        {" "}
        <h3>Choose file type</h3>
      </div>
      <div className="FileFlowWrapper">
        <div className="cardWrapper">
          {cardMenuData?.map((item, index) => (
           item.link?(
            <Link to={item.link}>
            {/* <Link to='trade-lisense'> */}
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

export default FileFlow;
