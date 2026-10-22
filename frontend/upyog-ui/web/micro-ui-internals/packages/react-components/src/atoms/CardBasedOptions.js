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
       <div className="employeeCustomCard rc-card-based-options-fullwidth">
            <h2 className="rc-card-based-options-header">{header}</h2>
            {/* <p onClick={sideOption.onClick}></p> */}
            <button type="button" className="inboxButton" onClick={sideOption.onClick}>
            {sideOption.name}
                      </button>
            <div className="employee-card-banner">
          <div className="body rc-card-based-options-container-padding">
          <div className="mainContent citizenAllServiceGrid rc-card-based-options-flex-container">
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