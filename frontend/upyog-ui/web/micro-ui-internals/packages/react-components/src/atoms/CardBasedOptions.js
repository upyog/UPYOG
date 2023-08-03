import React from "react";

const Option = ({ name, Icon, onClick, className }) => {
  return (
    <div className={className || `CardBasedOptionsMainChildOption`} onClick={onClick}>
      <div className="ChildOptionImageWrapper">{Icon}</div>
      <p className="ChildOptionName">{name}</p>
    </div>
  );
};

const CardBasedOptions = ({ header, sideOption, options, styles = {}, style={} }) => {
  return (
    <div className="CardBasedOptions" style={style}>
       <div className="employeeCustomCard" style={{ width: "100%", height: "80%", position: "relative",display:"flex",fontSize:"1.2rem",fontWeight:"700" }}>
            <h2 style={{width:"70%",padding:"20px",height:"fit-content",color:"white"}}>{header}</h2>
            {/* <p onClick={sideOption.onClick}></p> */}
            <button type="button" className="inboxButton" onClick={sideOption.onClick}>
            {sideOption.name}
                      </button>
            <div className="employee-card-banner">
          <div className="body" style={{ margin: "0px", padding: "0px",height:"100%" }}>
          <div className="mainContent citizenAllServiceGrid" style={{display:"flex"}}>
            {options.map( (props, index) => 
                <Option key={index} {...props} />
            )}
        </div>
          </div>

        </div>
        </div>
    </div>
  );
};

export default CardBasedOptions;
